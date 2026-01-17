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

        // 1. Récupération et annonce du rôle du mort
        LGRole role = main.getRoleManager().getRole(victim.getUniqueId());
        String roleName = (role != null) ? role.getName() : "Aucun rôle";
        event.setDeathMessage(ChatColor.RED + victim.getName() + ChatColor.GRAY + " est mort ! Il était " + ChatColor.GOLD + roleName);

        // 2. Passage en spectateur
        Bukkit.getScheduler().runTaskLater(main, () -> {
            victim.setGameMode(GameMode.SPECTATOR);
        }, 10L);

        // 3. Vérification de la victoire
        checkWin();
    }

    private void checkWin() {
        // On ne vérifie la victoire que si la partie est lancée
        if (!main.isState(GState.GAME)) return;

        int villageoisRestants = 0;
        int loupsRestants = 0;

        for (Player p : Bukkit.getOnlinePlayers()) {
            // On ne compte que les joueurs en vie (Survie)
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

        // Conditions de victoire
        if (loupsRestants == 0 && villageoisRestants > 0) {
            finishGame("Le Village");
        } else if (villageoisRestants == 0 && loupsRestants > 0) {
            finishGame("Les Loups-Garous");
        } else if (villageoisRestants == 0 && loupsRestants == 0) {
            finishGame("Personne (Égalité)");
        }
    }

    private void finishGame(String winner) {
        main.setState(GState.LOBBY); // On repasse en Lobby pour bloquer les events de jeu

        Bukkit.broadcastMessage(ChatColor.DARK_PURPLE + "===============================");
        Bukkit.broadcastMessage(ChatColor.GOLD + "   VICTOIRE : " + ChatColor.YELLOW + winner);
        Bukkit.broadcastMessage(ChatColor.DARK_PURPLE + "===============================");

        // 4. Nettoyage automatique après 10 secondes
        Bukkit.getScheduler().runTaskLater(main, () -> {
            Bukkit.broadcastMessage(ChatColor.GRAY + "Fin de partie, retour au lobby et nettoyage du monde...");

            // On TP tout le monde sur le monde "world"
            for (Player p : Bukkit.getOnlinePlayers()) {
                p.teleport(Bukkit.getWorld("world").getSpawnLocation());
                p.setGameMode(GameMode.ADVENTURE);
                p.getInventory().clear();
            }

            // On supprime le monde de la partie
            main.getWorldManager().unloadCurrentWorld();

            // On vide les rôles
            main.getRoleManager().clearRoles();

        }, 200L); // 200 ticks = 10 secondes
    }
}