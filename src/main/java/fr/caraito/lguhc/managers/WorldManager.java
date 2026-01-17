package fr.caraito.lguhc.managers;

import fr.caraito.lguhc.Main;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class WorldManager {

    private final List<String> preparedWorlds = new ArrayList<>();
    private World currentGameWorld;

    public void generateMultipleWorlds(int amount) {
        new BukkitRunnable() {
            int count = 0;
            @Override
            public void run() {
                if (count >= amount) {
                    Bukkit.broadcastMessage("§a[LG UHC] Tous les mondes sont prêts !");
                    this.cancel();
                    return;
                }
                String name = "lg_world_" + System.currentTimeMillis() + "_" + count;
                WorldCreator creator = new WorldCreator(name);
                creator.seed(new Random().nextLong());
                World world = creator.createWorld();
                setupUHCWorld(world);
                preparedWorlds.add(name);
                count++;
            }
        }.runTaskTimer(Main.getInstance(), 0L, 100L);
    }

    public void setupUHCWorld(World world) {
        if (world == null) return;
        world.setGameRuleValue("naturalRegeneration", "false");
        world.setDifficulty(Difficulty.HARD);
        world.getWorldBorder().setCenter(0, 0);
        world.getWorldBorder().setSize(2000);
    }

    public boolean prepareAndTeleport(int radius) {
        if (preparedWorlds.isEmpty()) return false;

        String worldName = preparedWorlds.remove(0);
        this.currentGameWorld = Bukkit.getWorld(worldName);
        if (this.currentGameWorld == null) return false;

        for (Player player : Bukkit.getOnlinePlayers()) {
            Location teleLoc;
            int attempts = 0;

            // Boucle de recherche de position sûre
            do {
                double x = (Math.random() * radius * 2) - radius;
                double z = (Math.random() * radius * 2) - radius;

                // On charge le chunk pour être sûr que getHighestBlockYAt fonctionne
                currentGameWorld.getChunkAt((int)x >> 4, (int)z >> 4).load();

                double y = currentGameWorld.getHighestBlockYAt((int)x, (int)z);
                teleLoc = new Location(currentGameWorld, x + 0.5, y + 1, z + 0.5);

                attempts++;
                // Sécurité pour éviter une boucle infinie si la map est 100% océan
                if (attempts > 50) break;

            } while (!isSafe(teleLoc));

            player.teleport(teleLoc);
            player.setHealth(20.0);
            player.getInventory().clear();
            player.setGameMode(GameMode.SURVIVAL);
            Bukkit.broadcastMessage("§a[LG UHC] " + player.getName() + " a été téléporté dans le monde de jeu !");
        }
        return true;
    }

    /**
     * Décharge et supprime UNIQUEMENT le monde actuel
     */
    public void unloadCurrentWorld() {
        if (this.currentGameWorld != null) {
            String name = currentGameWorld.getName();

            // On s'assure que personne n'est dedans (sécurité supplémentaire)
            for (Player p : currentGameWorld.getPlayers()) {
                p.teleport(Bukkit.getWorld("world").getSpawnLocation());
            }

            Bukkit.unloadWorld(this.currentGameWorld, false);
            File worldFolder = new File(Bukkit.getWorldContainer(), name);
            deleteFolderRecursive(worldFolder);

            this.currentGameWorld = null;
            Bukkit.getLogger().info("[LGUHC] Monde de jeu supprimé.");
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

    private boolean isSafe(Location location) {
        // On vérifie le bloc aux pieds du joueur
        Material blockType = location.getBlock().getType();
        // On vérifie aussi le bloc juste en dessous
        Material underType = location.clone().add(0, -1, 0).getBlock().getType();

        // Si c'est de l'eau ou de la lave (statique ou coulante), ce n'est pas sûr
        if (blockType == Material.WATER || blockType == Material.STATIONARY_WATER ||
                blockType == Material.LAVA || blockType == Material.STATIONARY_LAVA) {
            return false;
        }

        // Pareil pour le bloc du dessous (on ne veut pas spawn sur un bloc de lave)
        if (underType == Material.WATER || underType == Material.STATIONARY_WATER ||
                underType == Material.LAVA || underType == Material.STATIONARY_LAVA) {
            return false;
        }

        // Le bloc doit être de l'air pour que le joueur ne soit pas étouffé
        return blockType == Material.AIR;
    }

    public List<String> getPreparedWorlds() { return preparedWorlds; }
}