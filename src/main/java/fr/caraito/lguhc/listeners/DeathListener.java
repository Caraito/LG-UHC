package fr.caraito.lguhc.listeners;

import fr.caraito.lguhc.Main;
import fr.caraito.lguhc.roles.LGRole;
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

        // Récupération du rôle
        LGRole role = main.getRoleManager().getRole(victim.getUniqueId());
        String roleName = (role != null) ? role.getName() : "Aucun rôle";

        // Message de mort public
        event.setDeathMessage(ChatColor.RED + victim.getName() + ChatColor.GRAY + " est mort ! Il était " + ChatColor.GOLD + roleName);

        // Mise en spectateur (un peu après pour éviter les bugs)
        Bukkit.getScheduler().runTaskLater(main, () -> {
            victim.setGameMode(GameMode.SPECTATOR);
            victim.teleport(victim.getLocation().add(0, 5, 0));
        }, 20L);

        // TODO: Ajouter ici la vérification de victoire
    }
}