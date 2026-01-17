package fr.caraito.lguhc.commands;

import fr.caraito.lguhc.Main;
import fr.caraito.lguhc.enums.GState;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CommandStart implements CommandExecutor {

    private final Main main;

    public CommandStart(Main main) {
        this.main = main;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (!sender.isOp()) {
            sender.sendMessage(ChatColor.RED + "Tu n'as pas la permission !");
            return true;
        }

        if (main.isState(GState.LOBBY)) {
            main.setState(GState.STARTING);

            // Configuration du monde
            main.getWorldManager().setupUHCWorld(Bukkit.getWorlds().get(0));

            // Téléportation
            main.getWorldManager().prepareAndTeleport(800);

            main.setState(GState.GAME);
            new fr.caraito.lguhc.tasks.GameTask(main).runTaskTimer(main, 0L, 20L);
            Bukkit.broadcastMessage(ChatColor.GREEN + "La partie est lancée ! Pas de régénération naturelle !");
        } else {
            sender.sendMessage(ChatColor.RED + "La partie a déjà commencé.");
        }

        return true;
    }
}