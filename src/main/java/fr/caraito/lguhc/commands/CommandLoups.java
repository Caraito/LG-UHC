package fr.caraito.lguhc.commands;

import fr.caraito.lguhc.Main;
import fr.caraito.lguhc.roles.*;
import fr.caraito.lguhc.Main;
import fr.caraito.lguhc.roles.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.UUID;

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
            if (args.length >= 1) {
                String roleDisguise = String.join(" ", args);
                ((RoleGrimeur) role).setDisguisedRole(roleDisguise);
                player.sendMessage(ChatColor.RED + "[Grimeur] " + ChatColor.WHITE + "Vous apparaîtrez désormais comme " + ChatColor.GOLD + roleDisguise + ChatColor.WHITE + " à la Voyante.");
            } else {
                openGrimeurGUI(player);
            }

        } else if (label.equalsIgnoreCase("lgsacrifice")) {
            if (!(role instanceof RoleLoupBlanc)) {
                player.sendMessage(ChatColor.RED + "Vous n'êtes pas le Loup Blanc.");
                return true;
            }

            RoleLoupBlanc lb = (RoleLoupBlanc) role;
            // Cooldown de 2 nuits (approx 40 min / 2400 secondes)
            if (System.currentTimeMillis() - lb.getLastSacrifice() < 2400 * 1000L) {
                player.sendMessage(ChatColor.RED + "Vous devez attendre avant de sacrifier un autre loup.");
                return true;
            }

            if (args.length >= 1) {
                Player target = Bukkit.getPlayer(args[0]);
                if (target == null) {
                    player.sendMessage(ChatColor.RED + "Joueur non trouvé.");
                    return true;
                }
                executeSacrifice(player, lb, target);
            } else {
                openSacrificeGUI(player);
            }
        }

        return true;
    }

    public void executeSacrifice(Player player, RoleLoupBlanc lb, Player target) {
        LGRole targetRole = main.getRoleManager().getRole(target.getUniqueId());
        if (targetRole == null || targetRole.getCamp() != RoleCamp.LOUPS) {
            player.sendMessage(ChatColor.RED + "Vous ne pouvez sacrifier que des Loups.");
            return;
        }

        if (target.getMaxHealth() <= 16.0) { // 8 coeurs minimum
            player.sendMessage(ChatColor.RED + "Ce loup est trop faible pour être sacrifié davantage.");
            return;
        }

        if (player.getLocation().distance(target.getLocation()) > 10) {
            player.sendMessage(ChatColor.RED + "Le joueur est trop loin.");
            return;
        }

        // Sacrifice
        target.setMaxHealth(target.getMaxHealth() - 2.0);
        target.sendMessage(ChatColor.RED + "Le Loup Blanc vous a sacrifié ! Vous perdez 1 cœur permanent.");

        player.setMaxHealth(player.getMaxHealth() + 2.0);
        player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 6000, 0)); // 5 min
        player.sendMessage(ChatColor.DARK_PURPLE + "[Loup Blanc] " + ChatColor.WHITE + "Vous avez sacrifié " + ChatColor.RED + target.getName() + ChatColor.WHITE + ". +1 cœur permanent et Force I (5 min).");

        lb.setLastSacrifice(System.currentTimeMillis());
    }

    private void openGrimeurGUI(Player player) {
        Inventory gui = Bukkit.createInventory(null, 27, "§8Choix du Grimage");
        String[] roles = {"Simple Villageois", "Voyante", "Renard", "Petite Fille", "Chasseur", "Salvateur", "Sorcière", "Loup-Garou", "Assassin"};
        for (String rName : roles) {
            ItemStack item = new ItemStack(Material.PAPER);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName("§e" + rName);
            item.setItemMeta(meta);
            gui.addItem(item);
        }
        player.openInventory(gui);
    }

    private void openSacrificeGUI(Player player) {
        Inventory gui = Bukkit.createInventory(null, 27, "§8Sacrifice du Loup Blanc");
        for (UUID uuid : main.getRoleManager().getRoles().keySet()) {
            LGRole r = main.getRoleManager().getRole(uuid);
            if (r != null && r.getCamp() == RoleCamp.LOUPS) {
                Player target = Bukkit.getPlayer(uuid);
                if (target != null && target.isOnline() && !target.getUniqueId().equals(player.getUniqueId())) {
                    ItemStack head = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
                    SkullMeta meta = (SkullMeta) head.getItemMeta();
                    meta.setOwner(target.getName());
                    meta.setDisplayName("§c" + target.getName());
                    head.setItemMeta(meta);
                    gui.addItem(head);
                }
            }
        }
        player.openInventory(gui);
    }
}
