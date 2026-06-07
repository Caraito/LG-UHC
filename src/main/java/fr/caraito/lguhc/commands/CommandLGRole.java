package fr.caraito.lguhc.commands;

import fr.caraito.lguhc.Main;
import fr.caraito.lguhc.roles.LGRole;
import fr.caraito.lguhc.roles.RoleCamp;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CommandLGRole implements CommandExecutor {

    private final Main main;

    public CommandLGRole(Main main) {
        this.main = main;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) return true;
        Player player = (Player) sender;

        LGRole role = main.getRoleManager().getRole(player.getUniqueId());
        if (role == null) {
            player.sendMessage(ChatColor.RED + "Les rôles n'ont pas encore été distribués.");
            return true;
        }

        player.sendMessage(ChatColor.GREEN + "========== VOTRE RÔLE ==========");
        player.sendMessage(ChatColor.WHITE + "Rôle : " + ChatColor.GOLD + role.getName());
        player.sendMessage(ChatColor.WHITE + "Camp : " + (role.getCamp() == RoleCamp.LOUPS ? ChatColor.RED : ChatColor.GREEN) + role.getCamp().name());
        player.sendMessage(ChatColor.GRAY + role.getDescription());

        if (role.getCamp() == RoleCamp.LOUPS || role.getCamp() == RoleCamp.SOLITAIRE) {
            List<String> wolfNames = new ArrayList<>();
            for (UUID uuid : main.getRoleManager().getRoles().keySet()) {
                LGRole r = main.getRoleManager().getRole(uuid);
                if (r != null && r.getCamp() == RoleCamp.LOUPS) {
                    Player p = Bukkit.getPlayer(uuid);
                    if (p != null) {
                        wolfNames.add(ChatColor.RED + p.getName() + ChatColor.GRAY + " (" + r.getName() + ")");
                    } else {
                        wolfNames.add(ChatColor.RED + Bukkit.getOfflinePlayer(uuid).getName() + ChatColor.GRAY + " (" + r.getName() + ")");
                    }
                }
            }
            player.sendMessage("");
            player.sendMessage(ChatColor.RED + "Liste des Loups :");
            for (String name : wolfNames) {
                player.sendMessage("- " + name);
            }
        }
        player.sendMessage(ChatColor.GREEN + "================================");

        return true;
    }
}
