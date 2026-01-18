package fr.caraito.lguhc.listeners;

import fr.caraito.lguhc.Main;
import fr.caraito.lguhc.enums.GState;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.ChatColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import java.util.Random;

public class PlayerListener implements Listener {

    private final Main main;

    public PlayerListener(Main main) {
        this.main = main;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        event.setJoinMessage(ChatColor.GRAY + "[" + ChatColor.GREEN + "+" + ChatColor.GRAY + "] " + ChatColor.YELLOW + event.getPlayer().getName());

        if (main.isState(GState.LOBBY)) {
            event.getPlayer().sendMessage(ChatColor.GOLD + "Bienvenue en LG UHC ! En attente du lancement...");
        }
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        if (main.isState(GState.LOBBY)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEat(FoodLevelChangeEvent event) {
        if (main.isState(GState.LOBBY)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onSpawn(EntitySpawnEvent event) {
        if (main.isState(GState.LOBBY)) {
            event.setCancelled(true);
        }
    }


    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (main.isState(GState.LOBBY)) {
            event.setCancelled(true);
        }
    }

    // --- AJOUT : AUTO-ENCHANT DES OUTILS ---
    @EventHandler
    public void onCraft(org.bukkit.event.inventory.CraftItemEvent event) {
        ItemStack item = event.getCurrentItem();
        if (item == null || item.getType() == Material.AIR) return;

        Material type = item.getType();
        String name = type.name();

        // Si c'est une pioche, hache, pelle ou épée
        if (name.contains("PICKAXE") || name.contains("AXE") || name.contains("SPADE")) {
            item.addUnsafeEnchantment(Enchantment.DIG_SPEED, 3);
            item.addUnsafeEnchantment(Enchantment.DURABILITY, 3);

        }
    }

    @EventHandler
    public void onOreBreak(org.bukkit.event.block.BlockBreakEvent event) {
        if (!main.isState(GState.GAME)) return;

        org.bukkit.block.Block block = event.getBlock();
        org.bukkit.inventory.ItemStack drop = null;

        // --- AJOUT : TIMBER (CASSAGE D'ARBRE AUTO) ---
        if (block.getType() == Material.LOG || block.getType() == Material.LOG_2) {
            // On casse jusqu'à 20 blocs vers le haut
            for (int i = 0; i <= 20; i++) {
                org.bukkit.block.Block b = block.getRelative(0, i, 0);
                if (b.getType() == Material.LOG || b.getType() == Material.LOG_2) {
                    b.breakNaturally();
                    // 5% de chance de faire tomber une pomme par bûche cassée
                    if (new Random().nextInt(100) < 20) {
                        b.getWorld().dropItemNaturally(b.getLocation(), new ItemStack(Material.APPLE));
                    }
                } else {
                    break;
                }
            }
        }

        // Système de CutClean
        if (block.getType() == Material.IRON_ORE) {
            event.setExpToDrop(3);
            block.setType(Material.AIR);
            drop = new org.bukkit.inventory.ItemStack(Material.IRON_INGOT, 2);
        } else if (block.getType() == Material.GOLD_ORE) {
            event.setExpToDrop(5);
            block.setType(Material.AIR);
            drop = new org.bukkit.inventory.ItemStack(Material.GOLD_INGOT, 2);
        } else if (block.getType() == Material.DIAMOND_ORE) {
            event.setExpToDrop(10);
        }

        if (drop != null) {
            block.getWorld().dropItemNaturally(block.getLocation(), drop);
        }
    }
}