package fr.caraito.lguhc.commands;

import fr.caraito.lguhc.Main;
import fr.caraito.lguhc.enums.GState;
import fr.caraito.lguhc.tasks.GameTask;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.Bukkit;

public class CommandStart implements CommandExecutor {

    private final Main main;

    public CommandStart(Main main) {
        this.main = main;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!sender.isOp()) return true;

        if (main.isState(GState.LOBBY)) {
            boolean success = main.getWorldManager().prepareAndTeleport(1800);

            if (success) {
                main.setState(GState.GAME);

                if (main.getConfig().getBoolean("meetup", false)) {
                    for (Player p : Bukkit.getOnlinePlayers()) giveMeetupStuff(p);
                }

                // Arrêt de l'ancienne tâche si elle existe
                if (main.getGameTask() != null) {
                    main.getGameTask().cancel();
                }

                // Lancement de la nouvelle tâche
                GameTask newTask = new GameTask(main);
                newTask.runTaskTimer(main, 0L, 20L);
                main.setGameTask(newTask);

                sender.sendMessage(ChatColor.GREEN + "Partie lancée !");
            } else {
                sender.sendMessage(ChatColor.RED + "Aucun monde disponible ! /lgworld create");
            }
        }
        return true;
    }

    private void giveMeetupStuff(Player p) {
        p.getInventory().clear();
        // Armure
        p.getInventory().setChestplate(createEnchanted(Material.DIAMOND_CHESTPLATE, Enchantment.PROTECTION_ENVIRONMENTAL, 2));
        p.getInventory().setBoots(createEnchanted(Material.DIAMOND_BOOTS, Enchantment.PROTECTION_ENVIRONMENTAL, 2));
        p.getInventory().setLeggings(createEnchanted(Material.IRON_LEGGINGS, Enchantment.PROTECTION_ENVIRONMENTAL, 2));
        p.getInventory().setHelmet(createEnchanted(Material.IRON_HELMET, Enchantment.PROTECTION_ENVIRONMENTAL, 2));
        // Outils
        p.getInventory().addItem(createEnchanted(Material.IRON_SWORD, Enchantment.DAMAGE_ALL, 4));
        p.getInventory().addItem(createEnchanted(Material.DIAMOND_PICKAXE, Enchantment.DIG_SPEED, 3));
        p.getInventory().addItem(createEnchanted(Material.BOW, Enchantment.ARROW_DAMAGE, 3));
        // Consommables
        p.getInventory().addItem(new ItemStack(Material.GOLDEN_APPLE, 20));
        p.getInventory().addItem(new ItemStack(Material.COOKED_BEEF, 64));
        for(int i=0; i<5; i++) p.getInventory().addItem(new ItemStack(Material.WATER_BUCKET));
        for(int i=0; i<6; i++) p.getInventory().addItem(new ItemStack(Material.COBBLESTONE, 64));
        for(int i=0; i<5; i++) p.getInventory().addItem(new ItemStack(Material.ARROW, 64));
    }

    private ItemStack createEnchanted(Material m, Enchantment e1, int l1) {
        ItemStack item = new ItemStack(m);
        ItemMeta meta = item.getItemMeta();
        if(e1 != null) meta.addEnchant(e1, l1, true);
        item.setItemMeta(meta);
        return item;
    }

}