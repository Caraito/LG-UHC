package fr.caraito.lguhc.commands;

import fr.caraito.lguhc.Main;
import fr.caraito.lguhc.roles.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class CommandLoups implements CommandExecutor {

    private final Main main;

    public CommandLoups(Main main) {
        this.main = main;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) return true;
        Player player = (Player) sender;
        LGRole role = main.getRoleManager().getRole(player.getUniqueId());

        if (label.equalsIgnoreCase("lggrimer")) {
            if (!(role instanceof RoleGrimeur)) {
                player.sendMessage(ChatColor.RED + "Vous n'êtes pas le Loup Grimeur.");
                return true;
            }
            if (args.length < 1) {
                player.sendMessage(ChatColor.RED + "Usage: /lggrimer <nom_du_rôle>");
                return true;
            }
            String roleDisguise = String.join(" ", args);
            ((RoleGrimeur) role).setDisguisedRole(roleDisguise);
            player.sendMessage(ChatColor.RED + "[Grimeur] " + ChatColor.WHITE + "Vous apparaîtrez désormais comme " + ChatColor.GOLD + roleDisguise + ChatColor.WHITE + " à la Voyante.");

        } else if (label.equalsIgnoreCase("lgsacrifice")) {
            if (!(role instanceof RoleLoupBlanc)) {
                player.sendMessage(ChatColor.RED + "Vous n'êtes pas le Loup Blanc.");
                return true;
            }
            if (args.length < 1) {
                player.sendMessage(ChatColor.RED + "Usage: /lgsacrifice <joueur>");
                return true;
            }

            RoleLoupBlanc lb = (RoleLoupBlanc) role;
            // Cooldown de 2 nuits (approx 40 min / 2400 secondes)
            if (System.currentTimeMillis() - lb.getLastSacrifice() < 2400 * 1000L) {
                player.sendMessage(ChatColor.RED + "Vous devez attendre avant de sacrifier un autre loup.");
                return true;
            }

            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                player.sendMessage(ChatColor.RED + "Joueur non trouvé.");
                return true;
            }

            LGRole targetRole = main.getRoleManager().getRole(target.getUniqueId());
            if (targetRole == null || targetRole.getCamp() != RoleCamp.LOUPS) {
                player.sendMessage(ChatColor.RED + "Vous ne pouvez sacrifier que des Loups.");
                return true;
            }

            if (player.getLocation().distance(target.getLocation()) > 10) {
                player.sendMessage(ChatColor.RED + "Le joueur est trop loin.");
                return true;
            }

            // Sacrifice
            target.setMaxHealth(target.getMaxHealth() - 2.0);
            target.sendMessage(ChatColor.RED + "Le Loup Blanc vous a sacrifié ! Vous perdez 1 cœur permanent.");

            player.setMaxHealth(player.getMaxHealth() + 2.0);
            player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 6000, 0)); // 5 min
            player.sendMessage(ChatColor.DARK_PURPLE + "[Loup Blanc] " + ChatColor.WHITE + "Vous avez sacrifié " + ChatColor.RED + target.getName() + ChatColor.WHITE + ". +1 cœur permanent et Force I (5 min).");

            lb.setLastSacrifice(System.currentTimeMillis());
        }

        return true;
    }
}
