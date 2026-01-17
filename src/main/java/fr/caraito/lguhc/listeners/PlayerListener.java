package fr.caraito.lguhc.listeners;

import fr.caraito.lguhc.Main;
import fr.caraito.lguhc.enums.GState;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.ChatColor;

public class PlayerListener implements Listener {

    private final Main main;

    public PlayerListener(Main main) {
        this.main = main;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        event.setJoinMessage(ChatColor.GRAY + "[" + ChatColor.GREEN + "+" + ChatColor.GRAY + "] " + ChatColor.YELLOW + event.getPlayer().getName());

        // Téléportation au spawn du lobby si besoin
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
    public void onDamage(EntityDamageEvent event) {
        if (main.isState(GState.LOBBY)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onOreBreak(org.bukkit.event.block.BlockBreakEvent event) {
        if (!main.isState(GState.GAME)) return;

        org.bukkit.block.Block block = event.getBlock();
        org.bukkit.inventory.ItemStack drop = null;

        // Système de CutClean (Cuit direct + bonus)
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
            // On peut laisser le drop naturel ou le booster
        }

        if (drop != null) {
            block.getWorld().dropItemNaturally(block.getLocation(), drop);
        }
    }

}