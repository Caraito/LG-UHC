package fr.caraito.lguhc.listeners;

import fr.caraito.lguhc.Main;
import fr.caraito.lguhc.enums.GState;
import fr.caraito.lguhc.roles.LGRole;
import fr.caraito.lguhc.roles.RoleCamp;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class DeathListener implements Listener {

    private final Main main;

    public DeathListener(Main main) {
        this.main = main;
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player victim = event.getEntity();

        // 1. Annonce du rôle du joueur mort
        LGRole role = main.getRoleManager().getRole(victim.getUniqueId());
        String roleName = (role != null) ? role.getName() : "Aucun rôle";
        event.setDeathMessage(ChatColor.RED + victim.getName() + ChatColor.GRAY + " est mort ! Il était " + ChatColor.GOLD + roleName);

        // 2. Passage en spectateur (délai de 10 ticks pour éviter les bugs de respawn)
        Bukkit.getScheduler().runTaskLater(main, () -> {
            victim.setGameMode(GameMode.SPECTATOR);
        }, 10L);

        // 3. On vérifie si une équipe a gagné
        checkWin();
    }

    private void checkWin() {
        // On ne vérifie la victoire que si la partie est réellement en cours
        if (!main.isState(GState.GAME)) return;

        int villageoisRestants = 0;
        int loupsRestants = 0;

        for (Player p : Bukkit.getOnlinePlayers()) {
            // On ne compte que les survivants
            if (p.getGameMode() == GameMode.SURVIVAL) {
                LGRole role = main.getRoleManager().getRole(p.getUniqueId());
                if (role == null) continue;

                if (role.getCamp() == RoleCamp.LOUPS) {
                    loupsRestants++;
                } else if (role.getCamp() == RoleCamp.VILLAGE) {
                    villageoisRestants++;
                }
            }
        }

        // --- CONDITIONS DE VICTOIRE ---
        if (loupsRestants == 0 && villageoisRestants > 0) {
            finishGame("Le Village");
        } else if (villageoisRestants == 0 && loupsRestants > 0) {
            finishGame("Les Loups-Garous");
        } else if (villageoisRestants == 0 && loupsRestants == 0) {
            finishGame("Personne (Égalité)");
        }
    }

    private void finishGame(String winner) {
        // --- ÉTAPE CRUCIALE POUR TON BUG ---
        // On arrête le chronomètre immédiatement pour éviter qu'il continue de tourner
        if (main.getGameTask() != null) {
            main.getGameTask().cancel();
            main.setGameTask(null);
        }

        // On change l'état pour bloquer les événements de jeu
        main.setState(GState.LOBBY);

        Bukkit.broadcastMessage(ChatColor.DARK_PURPLE + "===============================");
        Bukkit.broadcastMessage(ChatColor.GOLD + "   VICTOIRE : " + ChatColor.YELLOW + winner);
        Bukkit.broadcastMessage(ChatColor.DARK_PURPLE + "===============================");

        // Attente de 10 secondes avant le retour au spawn et le nettoyage
        Bukkit.getScheduler().runTaskLater(main, () -> {
            Bukkit.broadcastMessage(ChatColor.GRAY + "Nettoyage du monde et retour au lobby...");

            for (Player p : Bukkit.getOnlinePlayers()) {
                if (Bukkit.getWorld("world") != null) {
                    p.teleport(Bukkit.getWorld("world").getSpawnLocation());
                }
                p.setGameMode(GameMode.ADVENTURE);
                p.getInventory().clear();
                p.setHealth(20.0);
                p.setFoodLevel(20);
            }

            // Suppression du monde de jeu et reset des rôles
            main.getWorldManager().unloadCurrentWorld();
            main.getRoleManager().clearRoles();

        }, 200L); // 200 ticks = 10 secondes
    }
}