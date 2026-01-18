package fr.caraito.lguhc.listeners;

import fr.caraito.lguhc.Main;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class PlayerDeathBeforeRoleListener implements Listener {

    private final Main main;

    // Sauvegarde inventaire
    private final Map<UUID, ItemStack[]> savedItems = new HashMap<>();
    private final Map<UUID, ItemStack[]> savedArmor = new HashMap<>();

    // Sauvegarde monde avant la mort
    private final Map<UUID, World> savedWorld = new HashMap<>();

    // Joueurs à ressusciter
    private final Set<UUID> reviveQueue = new HashSet<>();

    public PlayerDeathBeforeRoleListener(Main main) {
        this.main = main;
    }

    /* ===================== DEATH ===================== */

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        if (main.getGameTask() == null) return;
        if (main.getGameTask().isRoleDistributionDone) return;

        Player player = event.getEntity();
        UUID uuid = player.getUniqueId();

        savedItems.put(uuid, player.getInventory().getContents());
        savedArmor.put(uuid, player.getInventory().getArmorContents());
        savedWorld.put(uuid, player.getWorld());

        reviveQueue.add(uuid);

        event.getDrops().clear();
        event.setDeathMessage(null);
    }

    /* ===================== RESPAWN ===================== */

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        if (!reviveQueue.contains(uuid)) return;

        reviveQueue.remove(uuid);

        Bukkit.getScheduler().runTask(main, () -> {

            // Reset joueur
            player.setFireTicks(0);
            player.setFallDistance(0);
            player.setHealth(20.0);
            player.setFoodLevel(20);
            player.setGameMode(GameMode.SURVIVAL);

            for (PotionEffect effect : player.getActivePotionEffects()) {
                player.removePotionEffect(effect.getType());
            }

            // Monde UHC
            World world = savedWorld.remove(uuid);

            // Position aléatoire
            int radius = 1800;
            Random random = new Random();

            int x = random.nextInt(radius * 2) - radius;
            int z = random.nextInt(radius * 2) - radius;

            // Charger le chunk
            world.getChunkAt(x >> 4, z >> 4).load();

            int y = world.getHighestBlockYAt(x, z);

            // Toujours y + 1
            Location baseLoc = new Location(world, x + 0.5, y + 1, z + 0.5);

            Location safeLoc = findSafeLocationAround(baseLoc);
            player.teleport(safeLoc);

            // Restauration inventaire
            if (savedItems.containsKey(uuid)) {
                player.getInventory().setContents(savedItems.remove(uuid));
                player.getInventory().setArmorContents(savedArmor.remove(uuid));
            }

            // Protection courte
            player.addPotionEffect(
                    new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 60, 5)
            );

            player.sendMessage("§a§l[LG UHC] §7Mort avant les rôles — vous avez été ressuscité avec votre stuff.");
        });
    }

    /* ===================== SAFE LOCATION ===================== */

    private Location findSafeLocationAround(Location base) {
        World world = base.getWorld();
        Random random = new Random();
        int radius = 30;

        for (int i = 0; i < 200; i++) {
            int x = base.getBlockX() + random.nextInt(radius * 2) - radius;
            int z = base.getBlockZ() + random.nextInt(radius * 2) - radius;

            world.getChunkAt(x >> 4, z >> 4).load();

            int y = world.getHighestBlockYAt(x, z);
            Location loc = new Location(world, x + 0.5, y + 1, z + 0.5);

            if (isSafe(loc)) {
                return loc;
            }
        }

        // Fallback
        return base.clone().add(0, 1, 0);
    }

    private boolean isSafe(Location loc) {
        Block ground = loc.clone().add(0, -1, 0).getBlock();
        Block feet = loc.getBlock();
        Block head = loc.clone().add(0, 1, 0).getBlock();

        Material groundType = ground.getType();

        if (groundType == Material.AIR) return false;
        if (groundType == Material.LAVA || groundType == Material.STATIONARY_LAVA) return false;
        if (groundType == Material.FIRE) return false;
        if (groundType == Material.CACTUS) return false;

        if (feet.getType() != Material.AIR) return false;
        if (head.getType() != Material.AIR) return false;

        return groundType.isSolid();
    }
}
