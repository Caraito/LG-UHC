package fr.caraito.lguhc.managers;

import fr.caraito.lguhc.Main;
import fr.caraito.lguhc.roles.LGRole;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
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

        // Calcul des joueurs encore en vie (ceux qui sont en mode Survie)
        long aliveCount = Bukkit.getOnlinePlayers().stream()
                .filter(p -> p.getGameMode() == GameMode.SURVIVAL)
                .count();

        // Récupération de la bordure (rayon de la bordure)
        int border = (int) player.getWorld().getWorldBorder().getSize() / 2;

        obj.getScore("§7----------------").setScore(8);
        obj.getScore("§fTemps: §e" + formatTime(time)).setScore(7);
        obj.getScore("§fJoueurs en vie: §a" + aliveCount).setScore(6);
        obj.getScore("§fBordure: §b±" + border).setScore(5);

        String roleName = "§7Attente...";
        LGRole role = Main.getInstance().getRoleManager().getRole(player.getUniqueId());
        if (role != null) roleName = "§e" + role.getName();
        obj.getScore("§fRôle: " + roleName).setScore(4);

        obj.getScore("§7---------------- ").setScore(3);

        player.setScoreboard(board);
    }

    private String formatTime(int totalSeconds) {
        return String.format("%02d:%02d", totalSeconds / 60, totalSeconds % 60);
    }
}