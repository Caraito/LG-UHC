package fr.caraito.lguhc.listeners;

import fr.caraito.lguhc.Main;
import org.bukkit.*;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerDeathBeforeRoleListener implements Listener {
    private final Main main;

    // Maps pour stocker l'inventaire et l'armure
    private final Map<UUID, ItemStack[]> savedItems = new HashMap<>();
    private final Map<UUID, ItemStack[]> savedArmor = new HashMap<>();

    public PlayerDeathBeforeRoleListener(Main main) {
        this.main = main;
    }

    @EventHandler
    public void onDeathBeforeRoles(PlayerDeathEvent event) {
        // On vérifie si la distribution n'est PAS encore faite
        if (main.getGameTask() != null && !main.getGameTask().isRoleDistributionDone) {
            Player victim = event.getEntity();
            UUID uuid = victim.getUniqueId();

            // 1. SAUVEGARDE DU STUFF
            savedItems.put(uuid, victim.getInventory().getContents());
            savedArmor.put(uuid, victim.getInventory().getArmorContents());

            // On vide les messages et les loots au sol
            event.setDeathMessage(null);
            event.getDrops().clear();

            // Resurrection immediate (1 tick de délai pour laisser Bukkit traiter la mort)
            Bukkit.getScheduler().runTaskLater(main, () -> {
                victim.spigot().respawn();

                // 2. RESET TOTAL POUR STOPPER LES DÉGATS (Feu, Chute, Potions)
                victim.setFireTicks(0);
                victim.setFallDistance(0.0f);
                for (PotionEffect effect : victim.getActivePotionEffects()) {
                    victim.removePotionEffect(effect.getType());
                }

                // 3. TÉLÉPORTATION SÉCURISÉE
                teleportToSafeLocation(victim);

                // 4. RESTITUTION DU STUFF
                if (savedItems.containsKey(uuid)) {
                    victim.getInventory().setContents(savedItems.get(uuid));
                    victim.getInventory().setArmorContents(savedArmor.get(uuid));
                    savedItems.remove(uuid);
                    savedArmor.remove(uuid);
                }

                // Reset vie et faim
                victim.setHealth(20.0);
                victim.setFoodLevel(20);
                victim.setGameMode(GameMode.SURVIVAL);

                // Petit message et effets
                victim.sendMessage("§a§l[LG UHC] §7Vous êtes mort avant les rôles. Vous réapparaissez avec votre stuff !");
                Bukkit.broadcastMessage("§e§l[LG UHC] §7" + victim.getName() + " §aest ressuscité(e) après une mort prématurée.");

                // Super résistance pour annuler tout dégât résiduel de la mort précédente
                victim.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 100, 255));
                victim.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 60, 1));
            }, 1L);
        }
    }

    private void teleportToSafeLocation(Player player) {
        World world = player.getWorld();
        int radius = 1000;
        Location teleLoc = null;
        int attempts = 0;

        do {
            attempts++;
            double x = (Math.random() * radius * 2) - radius;
            double z = (Math.random() * radius * 2) - radius;

            // On s'assure que le chunk est chargé pour éviter getHighestBlockYAt à 0
            world.getChunkAt((int)x >> 4, (int)z >> 4).load();

            double y = world.getHighestBlockYAt((int) x, (int) z);
            teleLoc = new Location(world, x + 0.5, y, z + 0.5);

            if (attempts > 100) break;
        } while (!isSafe(teleLoc));

        player.teleport(teleLoc);
    }

    private boolean isSafe(Location loc) {
        Material floor = loc.clone().add(0, -1, 0).getBlock().getType();
        Biome biome = loc.getBlock().getBiome();

        if (biome.name().contains("OCEAN") || biome.name().contains("RIVER")) return false;
        if (isLiquid(floor) || floor == Material.AIR) return false;

        return floor.isSolid();
    }

    private boolean isLiquid(Material m) {
        return m == Material.WATER || m == Material.STATIONARY_WATER ||
                m == Material.LAVA || m == Material.STATIONARY_LAVA;
    }
}