package fr.caraito.lguhc.commands;

import fr.caraito.lguhc.Main;
import fr.caraito.lguhc.enums.GState;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.World;
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
        if (!sender.isOp()) {
            sender.sendMessage(ChatColor.RED + "Tu n'as pas la permission.");
            return true;
        }

        // On vérifie si on est bien en jeu (pas au lobby)
        if (main.isState(GState.LOBBY)) {
            sender.sendMessage(ChatColor.RED + "Aucune partie n'est lancée.");
            return true;
        }

        World lobby = Bukkit.getWorld("world");

        // 1. Téléportation immédiate de TOUT LE MONDE au spawn (lgspawn all)
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (lobby != null) {
                player.teleport(lobby.getSpawnLocation());
            }
            player.setGameMode(GameMode.ADVENTURE);
            player.getInventory().clear();
            player.setHealth(20.0);
        }

        // 1. Message global
        Bukkit.broadcastMessage(ChatColor.RED + "---------------------------");
        Bukkit.broadcastMessage(ChatColor.RED + "LA PARTIE A ÉTÉ INTERROMPUE");
        Bukkit.broadcastMessage(ChatColor.RED + "---------------------------");

        // 2. Téléporter tout le monde au spawn AVANT de décharger le monde
        if (Bukkit.getWorld("world") != null) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.teleport(Bukkit.getWorld("world").getSpawnLocation());
                player.setGameMode(GameMode.ADVENTURE);
                player.setHealth(20.0);
                player.setFoodLevel(20);
                player.getInventory().clear();
                player.getInventory().setArmorContents(null);
            }
        }

        // 3. Nettoyer les rôles
        main.getRoleManager().clearRoles();

        // 4. Décharger UNIQUEMENT le monde de la partie
        main.getWorldManager().unloadCurrentWorld();

        // 5. Revenir à l'état Lobby
        main.setState(GState.LOBBY);

        sender.sendMessage(ChatColor.GREEN + "Partie stoppée. Le monde utilisé a été nettoyé, mais ton stock est préservé !");
        return true;
    }
}