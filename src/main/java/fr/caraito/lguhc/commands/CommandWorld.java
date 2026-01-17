package fr.caraito.lguhc.commands;

import fr.caraito.lguhc.Main;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CommandWorld implements CommandExecutor {

    private final Main main;

    public CommandWorld(Main main) {
        this.main = main;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!sender.isOp()) return true;

        if (args.length == 2 && args[0].equalsIgnoreCase("create")) {
            int amount = Integer.parseInt(args[1]);
            sender.sendMessage(ChatColor.YELLOW + "Début de la génération de " + amount + " mondes...");
            main.getWorldManager().generateMultipleWorlds(amount);
        }
        else if (args.length == 1 && args[0].equalsIgnoreCase("remove")) {
            main.getWorldManager().deleteAllWorldFolders();
            sender.sendMessage(ChatColor.RED + "Tous les mondes LG ont été déchargés.");
        }

        return true;
    }
}