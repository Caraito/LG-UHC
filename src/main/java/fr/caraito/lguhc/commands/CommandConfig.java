package fr.caraito.lguhc.commands;

import fr.caraito.lguhc.Main;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class CommandConfig implements CommandExecutor {
    private final Main main;
    public CommandConfig(Main main) { this.main = main; }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player) || !sender.isOp()) return true;
        openConfigGUI((Player) sender);
        return true;
    }

    public void openConfigGUI(Player player) {
        Inventory gui = Bukkit.createInventory(null, 9, "§8Configuration LG UHC");
        boolean meetup = main.getConfig().getBoolean("meetup", false);

        ItemStack item = new ItemStack(meetup ? Material.EMERALD_BLOCK : Material.REDSTONE_BLOCK);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§eMode Meetup : " + (meetup ? "§aActivé" : "§cDésactivé"));
        meta.setLore(Arrays.asList("§7Cliquez pour changer", "", "§fStuff auto & Rôles à 5min"));
        item.setItemMeta(meta);

        gui.setItem(4, item);
        player.openInventory(gui);
    }
}