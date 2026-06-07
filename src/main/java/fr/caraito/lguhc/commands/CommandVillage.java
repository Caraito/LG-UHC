package fr.caraito.lguhc.commands;

import fr.caraito.lguhc.Main;
import fr.caraito.lguhc.roles.LGRole;
import fr.caraito.lguhc.roles.RoleRenard;
import fr.caraito.lguhc.roles.RoleVoyante;
import fr.caraito.lguhc.roles.RoleGrimeur;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class CommandVillage implements CommandExecutor {

    private final Main main;

    public CommandVillage(Main main) {
        this.main = main;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) return true;
        Player player = (Player) sender;
        LGRole role = main.getRoleManager().getRole(player.getUniqueId());

        if (label.equalsIgnoreCase("lgvoyante")) {
            if (!(role instanceof RoleVoyante)) {
                player.sendMessage(ChatColor.RED + "Vous n'êtes pas la Voyante.");
                return true;
            }
            if (args.length < 1) {
                player.sendMessage(ChatColor.RED + "Usage: /lgvoyante <joueur>");
                return true;
            }

            RoleVoyante voyante = (RoleVoyante) role;
            boolean isMeetup = main.getConfig().getBoolean("meetup", false);
            long cooldown = (isMeetup ? 10 : 20) * 60 * 1000L;
            if (System.currentTimeMillis() - voyante.getLastUse() < cooldown) {
                player.sendMessage(ChatColor.RED + "Votre pouvoir est en recharge.");
                return true;
            }

            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                player.sendMessage(ChatColor.RED + "Joueur non trouvé.");
                return true;
            }

            LGRole targetRole = main.getRoleManager().getRole(target.getUniqueId());
            String roleName = "Inconnu";
            if (targetRole != null) {
                if (targetRole instanceof RoleGrimeur && ((RoleGrimeur) targetRole).getDisguisedRole() != null) {
                    roleName = ((RoleGrimeur) targetRole).getDisguisedRole();
                } else {
                    roleName = targetRole.getName();
                }
            }

            player.sendMessage(ChatColor.GOLD + "[Voyante] " + ChatColor.WHITE + "Le rôle de " + ChatColor.YELLOW + target.getName() + ChatColor.WHITE + " est : " + ChatColor.GOLD + roleName);
            voyante.setLastUse(System.currentTimeMillis());

        } else if (label.equalsIgnoreCase("lgrenard")) {
            if (!(role instanceof RoleRenard)) {
                player.sendMessage(ChatColor.RED + "Vous n'êtes pas le Renard.");
                return true;
            }
            RoleRenard renard = (RoleRenard) role;
            if (renard.getUses() <= 0) {
                player.sendMessage(ChatColor.RED + "Vous n'avez plus d'utilisations.");
                return true;
            }

            boolean found = false;
            for (Entity entity : player.getNearbyEntities(10, 10, 10)) {
                if (entity instanceof Player) {
                    LGRole r = main.getRoleManager().getRole(entity.getUniqueId());
                    if (r != null && r.getCamp() == fr.caraito.lguhc.roles.RoleCamp.LOUPS) {
                        found = true;
                        break;
                    }
                }
            }

            if (found) {
                player.sendMessage(ChatColor.GOLD + "[Renard] " + ChatColor.WHITE + "Il y a un " + ChatColor.RED + "Loup-Garou" + ChatColor.WHITE + " à proximité !");
            } else {
                player.sendMessage(ChatColor.GOLD + "[Renard] " + ChatColor.WHITE + "Il n'y a aucun Loup-Garou à proximité.");
            }
            renard.setUses(renard.getUses() - 1);
        }

        return true;
    }
}
