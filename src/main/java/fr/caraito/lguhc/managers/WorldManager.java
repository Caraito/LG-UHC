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
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class WorldManager {

    private final List<String> preparedWorlds;
    private World currentGameWorld;

    public WorldManager() {
        this.preparedWorlds = new ArrayList<>(Main.getInstance().getConfig().getStringList("prepared-worlds"));
    }

    private void saveToConfig() {
        Main.getInstance().getConfig().set("prepared-worlds", preparedWorlds);
        Main.getInstance().saveConfig();
    }

    public void generateMultipleWorlds(int amount) {
        new BukkitRunnable() {
            int count = 0;
            @Override
            public void run() {
                if (count >= amount) {
                    Bukkit.broadcastMessage("§a[LG UHC] Tous les mondes sont prêts ! (Stock total : §f" + preparedWorlds.size() + "§a)");
                    this.cancel();
                    return;
                }
                String name = "lg_world_" + System.currentTimeMillis() + "_" + count;
                WorldCreator creator = new WorldCreator(name);
                creator.seed(new Random().nextLong());
                World world = creator.createWorld();
                setupUHCWorld(world);
                preparedWorlds.add(name);
                saveToConfig();
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

    public boolean prepareAndTeleport(int radius) {
        if (preparedWorlds.isEmpty()) return false;

        String worldName = preparedWorlds.remove(0);
        saveToConfig();

        this.currentGameWorld = Bukkit.getWorld(worldName);
        if (this.currentGameWorld == null) {
            this.currentGameWorld = Bukkit.createWorld(new WorldCreator(worldName));
        }

        Bukkit.getLogger().info("[LGUHC-Debug] Tentative de spawn dans : " + worldName);

        for (Player player : Bukkit.getOnlinePlayers()) {
            Location teleLoc = null;
            int attempts = 0;

            do {
                attempts++;
                double x = (Math.random() * radius * 2) - radius;
                double z = (Math.random() * radius * 2) - radius;

                // Forcer le chargement du chunk
                currentGameWorld.getChunkAt((int)x >> 4, (int)z >> 4).load();

                // getHighestBlockYAt renvoie le Y du premier bloc d'AIR
                double y = currentGameWorld.getHighestBlockYAt((int)x, (int)z);

                // CORRECTION : On se TP exactement à Y.0 pour être au niveau du sol
                teleLoc = new Location(currentGameWorld, x + 0.5, y, z + 0.5);

                if (attempts > 250) {
                    Bukkit.getLogger().severe("[LGUHC-Debug] Spawn forcé pour " + player.getName());
                    break;
                }

            } while (!isSafe(teleLoc, player.getName(), attempts));

            // Anti-glitch : On s'assure que le chunk est bien envoyé au joueur
            teleLoc.getChunk().load();

            player.setFallDistance(0.0f);
            player.teleport(teleLoc);

            player.setHealth(20.0);
            player.setFoodLevel(20);
            player.getInventory().clear();
            player.setGameMode(GameMode.SURVIVAL);

            // Protection 5s pour laisser charger le décor (Raspberry Pi)
            player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 100, 255));
            player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 60, 1));

            player.getInventory().addItem(new ItemStack(Material.COOKED_BEEF, 64));

            Bukkit.broadcastMessage("§a[LG UHC] " + player.getName() + " a été téléporté !");
        }
        return true;
    }

    private boolean isSafe(Location loc, String playerName, int attempt) {
        Material feet = loc.getBlock().getType();
        Material head = loc.clone().add(0, 1, 0).getBlock().getType();
        Material floor = loc.clone().add(0, -1, 0).getBlock().getType();
        Biome biome = loc.getBlock().getBiome();

        // --- FILTRE DES BIOMES "DIFFICILES" ---
        // On refuse l'eau, mais aussi le désert et les biomes sans bois/nourriture
        if (biome.name().contains("OCEAN") || biome.name().contains("RIVER")) return false;

        if (biome == Biome.DESERT || biome == Biome.DESERT_HILLS) {
            // Trop difficile : pas d'arbres, pas de nourriture
            return false;
        }

        if (biome.name().contains("MESA") || biome.name().contains("ICE_PLAINS")) {
            // Mesa = pas de bois facile | Ice = pas de nourriture
            return false;
        }

        // --- VÉRIFICATIONS PHYSIQUES ---
        if (isLiquid(feet) || isLiquid(floor) || isLiquid(head)) return false;
        if (feet != Material.AIR || head != Material.AIR) return false;

        // On accepte si le sol est de l'herbe, de la terre ou de la pierre (Plaines, Forêt, Jungle, Taiga)
        return floor != Material.AIR && floor.isSolid();
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
            Bukkit.unloadWorld(this.currentGameWorld, false);
            File worldFolder = new File(Bukkit.getWorldContainer(), name);
            deleteFolderRecursive(worldFolder);
            this.currentGameWorld = null;
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
        saveToConfig();
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