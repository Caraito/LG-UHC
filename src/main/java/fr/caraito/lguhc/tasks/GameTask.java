package fr.caraito.lguhc.tasks;

import fr.caraito.lguhc.Main;
import fr.caraito.lguhc.enums.GState;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

public class GameTask extends BukkitRunnable {

    private final Main main;
    // On met en static pour que le ScoreboardManager puisse y accéder facilement
    private static int seconds = 0;

    public GameTask(Main main) {
        this.main = main;
        seconds = 0; // On reset au lancement
    }

    @Override
    public void run() {
        if (!main.isState(GState.GAME)) return;

        seconds++;

        // Distribution des rôles à 20 minutes
        if (seconds == 1200) {
            Bukkit.broadcastMessage("§5§lDistribution des rôles...");
            main.getRoleManager().distributeRoles();
        }
    }

    public static int getSeconds() {
        return seconds;
    }
}