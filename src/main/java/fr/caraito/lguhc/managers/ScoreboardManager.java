package fr.caraito.lguhc.managers;

import fr.caraito.lguhc.Main;
import fr.caraito.lguhc.roles.LGRole;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.World;
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

        // Récupération des infos du jeu
        int seconds = 0;
        if (Main.getInstance().getGameTask() != null) {
            seconds = Main.getInstance().getGameTask().getSeconds();
        }

        int roleTime = Main.getInstance().getConfig().getBoolean("meetup", false) ?
                Main.getInstance().getConfig().getInt("role_time_meetup", 300) :
                Main.getInstance().getConfig().getInt("role_time_normal", 1200);
        int epDuration = Main.getInstance().getConfig().getInt("episode_duration", 1200);

        int episode = 1;
        if (seconds > roleTime) {
            episode = ((seconds - roleTime) / epDuration) + 1;
        }

        World world = player.getWorld();
        boolean isNight = world.getTime() >= 13000 && world.getTime() <= 23000;
        String timeIcon = isNight ? "§9☽" : "§e☀";

        // Calcul des joueurs encore en vie
        long aliveCount = Bukkit.getOnlinePlayers().stream()
                .filter(p -> p.getGameMode() == GameMode.SURVIVAL || Main.getInstance().getDeathListener().getWaitingForRespawn().contains(p.getUniqueId()))
                .count();

        // Récupération de la bordure
        int border = (int) world.getWorldBorder().getSize() / 2;

        obj.getScore("§7----------------").setScore(10);
        obj.getScore("§fTemps: §e" + formatTime(seconds) + " " + timeIcon).setScore(9);
        obj.getScore("§fÉpisode: §6" + episode).setScore(8);
        obj.getScore("§7 ").setScore(7);
        obj.getScore("§fJoueurs: §a" + aliveCount).setScore(6);
        obj.getScore("§fBordure: §b±" + border).setScore(5);
        obj.getScore("§8 ").setScore(4);

        String roleName = "§7Attente...";
        LGRole role = Main.getInstance().getRoleManager().getRole(player.getUniqueId());
        if (role != null) roleName = "§e" + role.getName();
        obj.getScore("§fRôle: " + roleName).setScore(3);

        obj.getScore("§7---------------- ").setScore(2);
        obj.getScore("§cplay.monserveur.fr").setScore(1);

        player.setScoreboard(board);
    }

    private String formatTime(int totalSeconds) {
        return String.format("%02d:%02d", totalSeconds / 60, totalSeconds % 60);
    }
}