package fr.caraito.lguhc.listeners;

import fr.caraito.lguhc.Main;
import fr.caraito.lguhc.enums.GState;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public class PlayerListener implements Listener {

    private final Main main;

    public PlayerListener(Main main) {
        this.main = main;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        event.setJoinMessage("§8[§a+§8] §7" + player.getName());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        event.setQuitMessage("§8[§c-§8] §7" + player.getName());
    }

    // --- AUTO-ENCHANT DES OUTILS ---
    @EventHandler
    public void onCraft(CraftItemEvent event) {
        ItemStack item = event.getCurrentItem();
        if (item == null || item.getType() == Material.AIR) return;

        Material type = item.getType();
        String name = type.name();

        // On enchante pioches, haches, pelles, épées
        if (name.contains("PICKAXE") || name.contains("AXE") || name.contains("SPADE")) {
            item.addUnsafeEnchantment(Enchantment.DIG_SPEED, 3);
            item.addUnsafeEnchantment(Enchantment.DURABILITY, 3);

        }
    }

    // --- TIMBER (CASSAGE D'ARBRE) ---
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (!main.isState(GState.GAME)) return;

        Block block = event.getBlock();
        // LOG = Chêne, Sapin, Bouleau, Jungle | LOG_2 = Acacia, Chêne noir
        if (block.getType() == Material.LOG || block.getType() == Material.LOG_2) {
            breakTree(block);
        }
    }

    private void breakTree(Block block) {
        // On casse les blocs vers le haut pour simuler la chute de l'arbre
        for (int y = 0; y <= 30; y++) {
            Block b = block.getRelative(0, y, 0);
            if (b.getType() == Material.LOG || b.getType() == Material.LOG_2) {
                b.breakNaturally();

                // Chance de faire tomber une pomme à chaque bûche cassée (plus simple pour le serveur)
                if (new Random().nextInt(100) < 5) { // 5% de chance par bûche
                    b.getWorld().dropItemNaturally(b.getLocation(), new ItemStack(Material.APPLE));
                }
            } else {
                break; // Plus de bois au dessus, on arrête
            }
        }
    }
}