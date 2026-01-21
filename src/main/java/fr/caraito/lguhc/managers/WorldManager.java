package fr.caraito.lguhc.managers;

import fr.caraito.lguhc.Main;
import org.bukkit.*;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.*;

public class WorldManager {

    private final List<String> preparedWorlds;
    private World currentGameWorld;

    public WorldManager() {
        // CORRECTION : On utilise la même clé que dans saveToConfig
        this.preparedWorlds = new ArrayList<>(Main.getInstance().getConfig().getStringList("prepared-worlds-list"));
        Bukkit.getLogger().info("[LG UHC] " + preparedWorlds.size() + " mondes chargés depuis la config.");
    }

    private void saveToConfig() {
        // On sauvegarde la liste des noms des mondes
        Main.getInstance().getConfig().set("prepared-worlds-list", preparedWorlds);
        Main.getInstance().saveConfig();
    }

    public void generateMultipleWorlds(int amount, boolean stopAfter) {
        new BukkitRunnable() {
            int count = 0;
            @Override
            public void run() {
                if (count >= amount) {
                    Bukkit.broadcastMessage("§a[LG UHC] Génération terminée ! Mondes dispo : §f" + preparedWorlds.size());

                    if (stopAfter) {
                        Bukkit.getLogger().info("[LG UHC] Fin de generation detectee. Arret du serveur dans 10 secondes...");
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                Bukkit.getLogger().info("[LG UHC] Arret automatique du serveur.");
                                Bukkit.shutdown();
                            }
                        }.runTaskLater(Main.getInstance(), 200L); // 200 ticks = 10 secondes
                    }

                    this.cancel();
                    return;
                }
                String name = "lg_world_" + System.currentTimeMillis() + "_" + count;
                WorldCreator creator = new WorldCreator(name);
                creator.seed(new Random().nextLong());
                World world = creator.createWorld();

                setupUHCWorld(world);
                preparedWorlds.add(name);
                saveToConfig(); // Sauvegarde immédiate après ajout

                count++;
            }
        }.runTaskTimer(Main.getInstance(), 0L, 100L);
    }

    public void setupUHCWorld(World world) {
        if (world == null) return;
        world.setGameRuleValue("naturalRegeneration", "false");
        world.setGameRuleValue("doImmediateRespawn", "true");
        world.setDifficulty(Difficulty.HARD);
        world.getWorldBorder().setCenter(0, 0);
        world.getWorldBorder().setSize(4000);
    }

    public void prepareSafeLocations(int spawnsPerMap, int respawnsPerMap, int mapAmount) {
        if (preparedWorlds.size() < mapAmount) {
            Bukkit.broadcastMessage("§c[Erreur] Pas assez de mondes générés (" + preparedWorlds.size() + "/" + mapAmount + ")");
            return;
        }

        new BukkitRunnable() {
            int currentMapIdx = 0;

            @Override
            public void run() {
                if (currentMapIdx >= mapAmount) {
                    Bukkit.broadcastMessage("§a[LG UHC] Coordonnées préparées pour " + mapAmount + " mondes !");
                    this.cancel();
                    return;
                }

                String worldName = preparedWorlds.get(currentMapIdx);
                World world = Bukkit.getWorld(worldName);
                if (world == null) world = Bukkit.createWorld(new WorldCreator(worldName));

                List<String> spawns = new ArrayList<>();
                for (int i = 0; i < spawnsPerMap; i++) {
                    Location loc = findSingleSafeLoc(world, 1800);
                    spawns.add(loc.getX() + ";" + loc.getY() + ";" + loc.getZ());
                }
                Main.getInstance().getConfig().set("worlds-data." + worldName + ".spawns", spawns);

                List<String> respawns = new ArrayList<>();
                for (int i = 0; i < respawnsPerMap; i++) {
                    Location loc = findSingleSafeLoc(world, 1800);
                    respawns.add(loc.getX() + ";" + loc.getY() + ";" + loc.getZ());
                }
                Main.getInstance().getConfig().set("worlds-data." + worldName + ".respawns", respawns);

                Main.getInstance().saveConfig();
                currentMapIdx++;
            }
        }.runTaskTimer(Main.getInstance(), 0L, 40L);
    }

    public void preLoadChunks(int mapIndex, int radiusBlocks) {
        if (mapIndex >= preparedWorlds.size()) return;

        String worldName = preparedWorlds.get(mapIndex);
        World world = Bukkit.getWorld(worldName);
        if (world == null) world = Bukkit.createWorld(new WorldCreator(worldName));

        List<String> spawns = Main.getInstance().getConfig().getStringList("worlds-data." + worldName + ".spawns");
        List<String> respawns = Main.getInstance().getConfig().getStringList("worlds-data." + worldName + ".respawns");

        List<String> allPositions = new ArrayList<>(spawns);
        allPositions.addAll(respawns);

        if (allPositions.isEmpty()) {
            Bukkit.broadcastMessage("§c[LG UHC] Aucune position n'est enregistrée pour le monde : " + worldName);
            return;
        }

        Set<Long> chunksToLoad = new HashSet<>();
        int radiusChunks = radiusBlocks / 16;

        for (String posStr : allPositions) {
            String[] parts = posStr.split(";");
            int centerX = (int) Double.parseDouble(parts[0]) >> 4;
            int centerZ = (int) Double.parseDouble(parts[2]) >> 4;

            for (int x = centerX - radiusChunks; x <= centerX + radiusChunks; x++) {
                for (int z = centerZ - radiusChunks; z <= centerZ + radiusChunks; z++) {
                    chunksToLoad.add((long) x << 32 | z & 0xFFFFFFFFL);
                }
            }
        }

        List<Long> chunkQueue = new ArrayList<>(chunksToLoad);
        World finalWorld = world;
        int totalChunks = chunkQueue.size();

        new BukkitRunnable() {
            int index = 0;

            @Override
            public void run() {
                for (int i = 0; i < 15; i++) {
                    if (index >= chunkQueue.size()) {
                        Bukkit.broadcastMessage("§a[LG UHC] Pré-chargement de " + totalChunks + " chunks terminé pour " + worldName);
                        this.cancel();
                        return;
                    }

                    long key = chunkQueue.get(index);
                    int x = (int) (key >> 32);
                    int z = (int) key;

                    finalWorld.getChunkAt(x, z).load(true);
                    index++;
                }
            }
        }.runTaskTimer(Main.getInstance(), 0L, 1L);
    }

    public boolean prepareAndTeleport(int radius) {
        if (preparedWorlds.isEmpty()) return false;

        String worldName = preparedWorlds.remove(0);
        saveToConfig();

        this.currentGameWorld = Bukkit.getWorld(worldName);
        if (this.currentGameWorld == null) {
            this.currentGameWorld = Bukkit.createWorld(new WorldCreator(worldName));
        }

        List<String> savedSpawns = Main.getInstance().getConfig().getStringList("worlds-data." + worldName + ".spawns");

        int i = 0;
        for (Player player : Bukkit.getOnlinePlayers()) {
            Location teleLoc;

            if (i < savedSpawns.size()) {
                String[] p = savedSpawns.get(i).split(";");
                teleLoc = new Location(currentGameWorld, Double.parseDouble(p[0]), Double.parseDouble(p[1]), Double.parseDouble(p[2]));
                Bukkit.getLogger().info("[LG UHC] Téléportation de " + player.getName() + " vers un spawn pré-enregistré.");
                savedSpawns.remove(i);
                Main.getInstance().getConfig().set("worlds-data." + worldName + ".spawns", savedSpawns);
                Main.getInstance().saveConfig();
            } else {
                teleLoc = findSingleSafeLoc(currentGameWorld, radius);
                Bukkit.getLogger().info("[LG UHC] Téléportation de " + player.getName() + " vers un spawn aléatoire.");
            }

            teleLoc.getChunk().load();
            player.setFallDistance(0.0f);
            player.teleport(teleLoc);

            player.setHealth(20.0);
            player.setFoodLevel(20);
            player.getInventory().clear();
            player.setGameMode(GameMode.SURVIVAL);

            player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 100, 255));
            player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 60, 1));
            player.getInventory().addItem(new ItemStack(Material.COOKED_BEEF, 64));

            Bukkit.broadcastMessage("§a[LG UHC] " + player.getName() + " est prêt !");
            i++;
        }
        return true;
    }

    private Location findSingleSafeLoc(World world, int radius) {
        Location loc;
        int attempts = 0;
        do {
            attempts++;
            double x = (Math.random() * radius * 2) - radius;
            double z = (Math.random() * radius * 2) - radius;
            world.getChunkAt((int) x >> 4, (int) z >> 4).load();
            double y = world.getHighestBlockYAt((int) x, (int) z);
            loc = new Location(world, x + 0.5, y + 1.2, z + 0.5);
            if (attempts > 500) break;
        } while (!isSafe(loc));
        return loc;
    }

    private boolean isSafe(Location loc) {
        Material feet = loc.getBlock().getType();
        Material head = loc.clone().add(0, 1, 0).getBlock().getType();
        Material floor = loc.clone().add(0, -1, 0).getBlock().getType();
        Biome biome = loc.getBlock().getBiome();

        if (biome.name().contains("OCEAN") || biome.name().contains("RIVER")) return false;
        if (biome == Biome.DESERT || biome == Biome.DESERT_HILLS) return false;
        if (biome.name().contains("MESA") || biome.name().contains("ICE_PLAINS")) return false;

        if (isLiquid(feet) || isLiquid(floor) || isLiquid(head)) return false;
        if (feet != Material.AIR || head != Material.AIR) return false;

        return floor.isSolid() && floor != Material.CACTUS;
    }

    private boolean isLiquid(Material m) {
        return m == Material.WATER || m == Material.STATIONARY_WATER ||
                m == Material.LAVA || m == Material.STATIONARY_LAVA;
    }

    public void unloadCurrentWorld() {
        if (this.currentGameWorld != null) {
            String name = currentGameWorld.getName();

            for (Player p : currentGameWorld.getPlayers()) {
                p.teleport(Bukkit.getWorld("world").getSpawnLocation());
            }

            // On retire le monde de la liste des mondes disponibles
            preparedWorlds.remove(name);
            saveToConfig();

            // On retire ses données de spawn
            Main.getInstance().getConfig().set("worlds-data." + name, null);
            Main.getInstance().saveConfig();

            Bukkit.unloadWorld(this.currentGameWorld, false);

            File worldFolder = new File(Bukkit.getWorldContainer(), name);
            deleteFolderRecursive(worldFolder);

            this.currentGameWorld = null;
            Bukkit.getLogger().info("[LGUHC] Nettoyage complet du monde " + name + " terminé.");
        }
    }

    public void deleteAllWorldFolders() {
        File serverFolder = Bukkit.getWorldContainer();
        File[] files = serverFolder.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory() && file.getName().startsWith("lg_world_")) {
                    deleteFolderRecursive(file);
                }
            }
        }
        preparedWorlds.clear();
        Main.getInstance().getConfig().set("worlds-data", null);
        saveToConfig(); // Sauvegarde la liste vide
    }

    private void deleteFolderRecursive(File path) {
        if (path.exists()) {
            File[] files = path.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) deleteFolderRecursive(file);
                    else file.delete();
                }
            }
        }
        path.delete();
    }

    public List<String> getPreparedWorlds() { return preparedWorlds; }
}