package fr.caraito.lguhc.managers;

import fr.caraito.lguhc.Main;
import fr.caraito.lguhc.roles.LGRole;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

public class ScoreboardManager {

    public void updateScoreboard(Player player) {
        org.bukkit.scoreboard.ScoreboardManager manager = Bukkit.getScoreboardManager();
        Scoreboard board = manager.getNewScoreboard();
        Objective obj = board.registerNewObjective("lguhc", "dummy");

        obj.setDisplaySlot(DisplaySlot.SIDEBAR);
        obj.setDisplayName("§6§lLG UHC");

        // Récupération du temps depuis la tâche active
        int time = 0;
        if (Main.getInstance().getGameTask() != null) {
            time = Main.getInstance().getGameTask().getSeconds();
        }

        obj.getScore("§7----------------").setScore(7);
        obj.getScore("§fTemps: §e" + formatTime(time)).setScore(6);
        obj.getScore("§fJoueurs: §a" + Bukkit.getOnlinePlayers().size()).setScore(5);

        String roleName = "§7Attente...";
        LGRole role = Main.getInstance().getRoleManager().getRole(player.getUniqueId());
        if (role != null) roleName = "§e" + role.getName();
        obj.getScore("§fRôle: " + roleName).setScore(4);

        player.setScoreboard(board);
    }

    private String formatTime(int totalSeconds) {
        return String.format("%02d:%02d", totalSeconds / 60, totalSeconds % 60);
    }
}