package fr.caraito.lguhc.tasks;

import fr.caraito.lguhc.Main;
import fr.caraito.lguhc.enums.GState;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

public class GameTask extends BukkitRunnable {

    private final Main main;
    private int seconds = 0;

    public GameTask(Main main) {
        this.main = main;
    }

    @Override
    public void run() {
        if (!main.isState(GState.GAME)) return;

        seconds++;

        // Annonce toutes les 5 minutes
        if (seconds % 300 == 0) {
            Bukkit.broadcastMessage(ChatColor.YELLOW + "Temps écoulé : " + (seconds / 60) + " minutes.");
        }

        // Distribution des rôles à 20 minutes (1200 secondes)
        if (seconds == 1200) {
            Bukkit.broadcastMessage(ChatColor.DARK_PURPLE + "---------------------------");
            Bukkit.broadcastMessage(ChatColor.LIGHT_PURPLE + "Distribution des rôles...");
            Bukkit.broadcastMessage(ChatColor.DARK_PURPLE + "---------------------------");
            main.getRoleManager().distributeRoles();
        }
    }
}