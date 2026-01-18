package fr.caraito.lguhc.listeners;

import fr.caraito.lguhc.Main;
import fr.caraito.lguhc.enums.GState;
import fr.caraito.lguhc.roles.LGRole;
import fr.caraito.lguhc.roles.RoleSalvateur;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class PlayerListener implements Listener {

    private final Main main;
    private final Map<UUID, Integer> diamondsMined = new HashMap<>();

    public PlayerListener(Main main) {
        this.main = main;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        event.setJoinMessage(ChatColor.GRAY + "[" + ChatColor.GREEN + "+" + ChatColor.GRAY + "] " + ChatColor.YELLOW + event.getPlayer().getName());
    }

    @EventHandler
    public void onCraft(CraftItemEvent event) {
        ItemStack item = event.getCurrentItem();
        if (item == null || item.getType() == Material.AIR) return;

        String name = item.getType().name();
        if (name.contains("PICKAXE") || name.contains("AXE") || name.contains("SPADE") || name.contains("SWORD")) {
            item.addUnsafeEnchantment(Enchantment.DIG_SPEED, 3);
            item.addUnsafeEnchantment(Enchantment.DURABILITY, 3);
            if (name.contains("SWORD")) item.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 3);
        }
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        if (main.isState(GState.LOBBY)) {
            event.setCancelled(true);
            return;
        }

        if (!main.isState(GState.GAME)) return;

        Player player = event.getPlayer();
        org.bukkit.block.Block block = event.getBlock();

        // 1. LIMITE DIAMANTS (15)
        if (block.getType() == Material.DIAMOND_ORE) {
            int count = diamondsMined.getOrDefault(player.getUniqueId(), 0);
            if (count >= 15) {
                event.setCancelled(true);
                player.sendMessage("§c§l[Limite] §fVous avez atteint la limite de §b15 diamants §f!");
                return;
            }
            diamondsMined.put(player.getUniqueId(), count + 1);
        }

        // 2. TIMBER (Arbres)
        if (block.getType() == Material.LOG || block.getType() == Material.LOG_2) {
            for (int i = 0; i <= 20; i++) {
                org.bukkit.block.Block b = block.getRelative(0, i, 0);
                if (b.getType() == Material.LOG || b.getType() == Material.LOG_2) {
                    b.breakNaturally();
                    if (new Random().nextInt(100) < 5)
                        b.getWorld().dropItemNaturally(b.getLocation(), new ItemStack(Material.APPLE));
                } else break;
            }
        }

        // 3. CUTCLEAN (Fer / Or)
        if (block.getType() == Material.IRON_ORE) {
            block.setType(Material.AIR);
            block.getWorld().dropItemNaturally(block.getLocation(), new ItemStack(Material.IRON_INGOT, 2));
        } else if (block.getType() == Material.GOLD_ORE) {
            block.setType(Material.AIR);
            block.getWorld().dropItemNaturally(block.getLocation(), new ItemStack(Material.GOLD_INGOT, 2));
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (main.isState(GState.LOBBY)) event.setCancelled(true);
    }

    @EventHandler
    public void onFood(FoodLevelChangeEvent event) {
        if (main.isState(GState.LOBBY)) event.setCancelled(true);
    }

    // 4. POUVOIR DU SALVATEUR
    @EventHandler
    public void onSalvateurInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getItemInHand();

        if ((event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) &&
                item != null && item.getType() == Material.IRON_DOOR && item.hasItemMeta()) {

            if (item.getItemMeta().getDisplayName().equals("§a§lBouclier de Fortune §7(Usage unique)")) {
                LGRole role = main.getRoleManager().getRole(player.getUniqueId());
                if (role instanceof RoleSalvateur) {
                    event.setCancelled(true);
                    player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 600, 1));
                    player.sendMessage("§a§l[Salvateur] §fBouclier activé ! §bRésistance II §f(30s).");
                    player.setItemInHand(null);
                }
            }
        }
    }
}