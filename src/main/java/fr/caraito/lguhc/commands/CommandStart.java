package fr.caraito.lguhc.commands;

import fr.caraito.lguhc.Main;
import fr.caraito.lguhc.enums.GState;
import fr.caraito.lguhc.tasks.GameTask;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class CommandStart implements CommandExecutor {

    private final Main main;

    public CommandStart(Main main) {
        this.main = main;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!main.isState(GState.LOBBY)) return true;

        main.setState(GState.GAME);
        main.setGameTask(new GameTask(main));
        main.getGameTask().runTaskTimer(main, 0, 20);

        if (main.getConfig().getBoolean("meetup")) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                giveMeetupStuff(p);
            }
        }

        Bukkit.broadcastMessage("§a§l[LG UHC] §fLa partie commence ! Bonne chance.");
        return true;
    }

    private void giveMeetupStuff(Player p) {
        p.getInventory().clear();

        // Armor
        p.getInventory().setChestplate(createItem(Material.DIAMOND_CHESTPLATE, Enchantment.PROTECTION_ENVIRONMENTAL, 2, Enchantment.DURABILITY, 2));
        p.getInventory().setBoots(createItem(Material.DIAMOND_BOOTS, Enchantment.PROTECTION_ENVIRONMENTAL, 2, Enchantment.DURABILITY, 2));
        p.getInventory().setLeggings(createItem(Material.IRON_LEGGINGS, Enchantment.PROTECTION_ENVIRONMENTAL, 3, Enchantment.DURABILITY, 2));
        p.getInventory().setHelmet(createItem(Material.IRON_HELMET, Enchantment.PROTECTION_ENVIRONMENTAL, 3, Enchantment.DURABILITY, 2));

        // Tools
        p.getInventory().addItem(createItem(Material.IRON_SWORD, Enchantment.DAMAGE_ALL, 3, Enchantment.DURABILITY, 3));
        p.getInventory().addItem(createItem(Material.DIAMOND_PICKAXE, Enchantment.DIG_SPEED, 3, Enchantment.DURABILITY, 3));
        p.getInventory().addItem(createItem(Material.BOW, Enchantment.ARROW_DAMAGE, 3, null, 0));

        // Consumables
        p.getInventory().addItem(new ItemStack(Material.GOLDEN_APPLE, 20));
        for (int i = 0; i < 5; i++) p.getInventory().addItem(new ItemStack(Material.WATER_BUCKET));
        for (int i = 0; i < 6; i++) p.getInventory().addItem(new ItemStack(Material.COBBLESTONE, 64));
        for (int i = 0; i < 5; i++) p.getInventory().addItem(new ItemStack(Material.ARROW, 64));
    }

    private ItemStack createItem(Material mat, Enchantment e1, int l1, Enchantment e2, int l2) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        if (e1 != null) meta.addEnchant(e1, l1, true);
        if (e2 != null) meta.addEnchant(e2, l2, true);
        item.setItemMeta(meta);
        return item;
    }
}