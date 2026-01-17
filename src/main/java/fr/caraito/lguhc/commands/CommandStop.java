package fr.caraito.lguhc.commands;

import fr.caraito.lguhc.Main;
import fr.caraito.lguhc.enums.GState;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandStop implements CommandExecutor {

    private final Main main;

    public CommandStop(Main main) {
        this.main = main;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!sender.isOp()) return true;

        if (!main.isState(GState.LOBBY)) {
            // Arrêt du chronomètre
            if (main.getGameTask() != null) {
                main.getGameTask().cancel();
                main.setGameTask(null);
            }

            for (Player player : Bukkit.getOnlinePlayers()) {
                player.teleport(Bukkit.getWorld("world").getSpawnLocation());
                player.setGameMode(GameMode.ADVENTURE);
                player.getInventory().clear();
            }

            main.getRoleManager().clearRoles();
            main.getWorldManager().unloadCurrentWorld();
            main.setState(GState.LOBBY);
            sender.sendMessage(ChatColor.GREEN + "Partie arrêtée et chrono stoppé.");
        }
        return true;
    }
}