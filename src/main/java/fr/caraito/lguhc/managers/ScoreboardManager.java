package fr.caraito.lguhc.managers;

import fr.caraito.lguhc.Main;
import fr.caraito.lguhc.roles.LGRole;
import fr.caraito.lguhc.tasks.GameTask;
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

        obj.getScore("§7----------------").setScore(7);

        // --- AFFICHAGE DU TEMPS ---
        obj.getScore("§fTemps: §e" + formatTime(GameTask.getSeconds())).setScore(6);

        obj.getScore("§fJoueurs: §a" + Bukkit.getOnlinePlayers().size()).setScore(5);

        String roleName = "§7Attente...";
        if (Main.getInstance().getRoleManager() != null) {
            LGRole role = Main.getInstance().getRoleManager().getRole(player.getUniqueId());
            if (role != null) roleName = "§e" + role.getName();
        }

        obj.getScore("§fRôle: " + roleName).setScore(4);

        int borderSize = (int) player.getWorld().getWorldBorder().getSize() / 2;
        obj.getScore("§fBordure: §c" + borderSize).setScore(3);

        obj.getScore("§7---------------- ").setScore(2);
        obj.getScore("§eLG UHC").setScore(1);

        player.setScoreboard(board);
    }

    // Méthode pour transformer les secondes en 00:00
    private String formatTime(int totalSeconds) {
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }
}