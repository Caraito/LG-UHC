package fr.caraito.lguhc.listeners;

import fr.caraito.lguhc.Main;
import org.bukkit.*;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PlayerDeathBeforeRoleListener implements Listener {
    private final Main main;

    public PlayerDeathBeforeRoleListener(Main main) {
        this.main = main;
    }

    @EventHandler
    public void onDeathBeforeRoles(PlayerDeathEvent event) {
        // On vérifie si la distribution n'est PAS encore faite
        if (main.getGameTask() != null && !main.getGameTask().isRoleDistributionDone) {
            Player victim = event.getEntity();

            // On vide les messages et les loots
            event.setDeathMessage(null);
            event.getDrops().clear();

            // Resurrection immediate
            Bukkit.getScheduler().runTaskLater(main, () -> {
                victim.spigot().respawn();

                // Téléportation sécurisée
                teleportToSafeLocation(victim);

                // Reset vie et faim
                victim.setHealth(20.0);
                victim.setFoodLevel(20);
                victim.setGameMode(GameMode.SURVIVAL);

                // Petit message et effets
                victim.sendMessage("§a§l[LG UHC] §7Vous êtes mort avant les rôles. Vous réapparaissez !");
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