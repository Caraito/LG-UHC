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
        // Condition critique : on vérifie si un monde est prêt
        if (preparedWorlds.isEmpty()) {
            return false;
        }

        String worldName = preparedWorlds.remove(0);
        this.currentGameWorld = Bukkit.getWorld(worldName);

        if (this.currentGameWorld == null) return false;

        for (Player player : Bukkit.getOnlinePlayers()) {
            // 1. On génère les coordonnées
            double x = (Math.random() * radius * 2) - radius;
            double z = (Math.random() * radius * 2) - radius;

            // 2. IMPORTANT : On force le chargement du chunk avant de calculer le Y
            currentGameWorld.getChunkAt((int)x >> 4, (int)z >> 4).load();

            // 3. On récupère le bloc le plus haut et on ajoute une marge de sécurité (+2)
            double y = currentGameWorld.getHighestBlockYAt((int)x, (int)z) + 1.5;

            Location teleLoc = new Location(currentGameWorld, x, y, z);

            // 4. Vérification anti-étouffement : si le bloc est solide (feuilles, etc), on monte
            while (teleLoc.getBlock().getType().isSolid() || teleLoc.clone().add(0, 1, 0).getBlock().getType().isSolid()) {
                teleLoc.add(0, 1, 0);
            }

            player.teleport(teleLoc);
            player.setHealth(20.0);
            player.setFoodLevel(20);
            player.getInventory().clear();
            player.setGameMode(GameMode.SURVIVAL);

            Bukkit.broadcastMessage("§a[LG UHC] " + player.getName() + " a été téléporté dans le monde de jeu.");
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

    public List<String> getPreparedWorlds() { return preparedWorlds; }
}