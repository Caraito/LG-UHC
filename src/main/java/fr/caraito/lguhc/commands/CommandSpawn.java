package fr.caraito.lguhc.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandSpawn implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Seul un joueur peut utiliser cette commande.");
            return true;
        }

        Player player = (Player) sender;

        // On récupère le monde principal nommé "world"
        World spawnWorld = Bukkit.getWorld("world");

        if (spawnWorld == null) {
            player.sendMessage(ChatColor.RED + "Erreur : Le monde 'world' est introuvable.");
            return true;
        }

        if (args.length == 1 && args[0].equalsIgnoreCase("all")) {
            if (!player.isOp()) return true;

            for (Player all : Bukkit.getOnlinePlayers()) {
                all.teleport(spawnWorld.getSpawnLocation());
                all.sendMessage(ChatColor.YELLOW + "Tout le monde a été ramené au spawn !");
            }
            return true;
        }

        // Téléportation au spawn du monde principal
        Location spawnLocation = spawnWorld.getSpawnLocation();
        player.teleport(spawnLocation);
        player.sendMessage(ChatColor.GREEN + "Retour au spawn principal !");


        return true;
    }
}