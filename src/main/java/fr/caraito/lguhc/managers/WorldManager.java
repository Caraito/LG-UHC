package fr.caraito.lguhc.managers;

import fr.caraito.lguhc.Main;
import org.bukkit.*;
import org.bukkit.entity.Player;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class WorldManager {

    private final List<String> preparedWorlds = new ArrayList<>();
    private World currentGameWorld; // Le monde actuellement utilisé pour la partie

    /**
     * Crée X mondes à l'avance avec une seed aléatoire
     */
    public void generateMultipleWorlds(int amount) {
        // On utilise un BukkitRunnable pour créer une file d'attente
        new org.bukkit.scheduler.BukkitRunnable() {
            int count = 0;

            @Override
            public void run() {
                // Si on a atteint le nombre de mondes demandés, on arrête la tâche
                if (count >= amount) {
                    Bukkit.broadcast("§a[LG UHC] Fin de la génération massive : " + amount + " mondes créés !", "lguhc.admin");
                    Bukkit.broadcast(ChatColor.GREEN + "Mondes créés ! Stock actuel : " + Main.getInstance().getWorldManager().getPreparedWorlds().size(), "lguhc.admin");
                    this.cancel();
                    return;
                }

                // Génération d'un nom unique
                String name = "lg_world_" + System.currentTimeMillis() + "_" + count;

                // Log dans la console pour suivre l'avancée
                Bukkit.getLogger().info("[LGUHC] Génération du monde " + (count + 1) + "/" + amount + "...");

                WorldCreator creator = new WorldCreator(name);
                creator.seed(new java.util.Random().nextLong());
                creator.environment(World.Environment.NORMAL);
                creator.generateStructures(true);

                // On crée le monde (C'est cette ligne qui prend du temps)
                World world = creator.createWorld();
                setupUHCWorld(world);

                preparedWorlds.add(name);

                // Petit message en jeu pour les admins toutes les 5 mondes pour ne pas spammer
                if ((count + 1) % 5 == 0) {
                    Bukkit.broadcast("§e[LG UHC] Progression : " + (count + 1) + "/" + amount, "lguhc.admin");
                }

                count++;
            }
            // 100L = 5 secondes entre CHAQUE monde.
            // Sur un Raspberry Pi 4, c'est le temps idéal pour laisser le CPU souffler.
        }.runTaskTimer(fr.caraito.lguhc.Main.getInstance(), 0L, 100L);
    }

    /**
     * Configure les règles UHC
     */
    public void setupUHCWorld(World world) {
        if (world == null) return;

        world.setGameRuleValue("naturalRegeneration", "false");
        world.setGameRuleValue("doDaylightCycle", "true");
        world.setDifficulty(Difficulty.HARD);
        world.setTime(0);

        WorldBorder border = world.getWorldBorder();
        border.setCenter(0, 0);
        border.setSize(2000);
        border.setDamageAmount(0.2);
        border.setWarningDistance(50);
    }

    /**
     * Sélectionne le prochain monde et téléporte les joueurs
     */
    public boolean prepareAndTeleport(int radius) {
        if (preparedWorlds.isEmpty()) {
            Bukkit.broadcast("§cAucun monde prêt pour la partie !", "lguhc.admin");
            return false;
        }

        String worldName = preparedWorlds.remove(0);
        this.currentGameWorld = Bukkit.getWorld(worldName);

        if (this.currentGameWorld == null) return false;

        for (Player player : Bukkit.getOnlinePlayers()) {
            double x = (Math.random() * radius * 2) - radius;
            double z = (Math.random() * radius * 2) - radius;
            double y = currentGameWorld.getHighestBlockYAt((int)x, (int)z) + 4;

            player.teleport(new Location(currentGameWorld, x, y, z));
            player.setHealth(20.0);
            player.setFoodLevel(20);
            player.getInventory().clear();
            player.getInventory().setArmorContents(null);
            player.setGameMode(GameMode.SURVIVAL);
            Bukkit.broadcastMessage("§a" + player.getName() + " a été téléporté.");
        }
        return true;
    }

    /**
     * Décharge les mondes et supprime les dossiers physiquement (Utile pour hébergeur)
     */
    public void deleteAllWorldFolders() {
        // Récupère le dossier où se trouve le serveur
        File serverFolder = Bukkit.getWorldContainer();
        File[] files = serverFolder.listFiles();

        if (files != null) {
            for (File file : files) {
                // On vérifie si c'est un dossier et s'il commence par notre préfixe
                if (file.isDirectory() && file.getName().startsWith("lg_world_")) {
                    Bukkit.getLogger().info("[LGUHC] Suppression du dossier : " + file.getName());
                    deleteFolderRecursive(file);
                }
            }
        }
    }

    public void deleteCurrentWorld() {
        if (this.currentGameWorld != null) {
            String name = currentGameWorld.getName();

            // On décharge le monde de la mémoire vive
            Bukkit.unloadWorld(this.currentGameWorld, false);

            // Optionnel : Si tu veux le supprimer physiquement du disque immédiatement
            // (attention, sur Raspberry Pi cela peut ralentir un court instant)
            File worldFolder = new File(Bukkit.getWorldContainer(), name);
            deleteFolderRecursive(worldFolder);

            this.currentGameWorld = null;
            Bukkit.getLogger().info("[LGUHC] Le monde de la partie terminée a été supprimé.");
        }
    }

    // Méthode utilitaire pour supprimer un dossier non vide
    private void deleteFolderRecursive(File path) {
        if (path.exists()) {
            File[] files = path.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        deleteFolderRecursive(file);
                    } else {
                        file.delete();
                    }
                }
            }
        }
        path.delete();
    }

    public List<String> getPreparedWorlds() {
        return preparedWorlds;
    }

    public World getCurrentGameWorld() {
        return currentGameWorld;
    }
}