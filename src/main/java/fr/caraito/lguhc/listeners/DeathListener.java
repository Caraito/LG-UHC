package fr.caraito.lguhc.listeners;

import fr.caraito.lguhc.Main;
import fr.caraito.lguhc.enums.GState;
import fr.caraito.lguhc.roles.LGRole;
import fr.caraito.lguhc.roles.RoleCamp;
import org.bukkit.*;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class DeathListener implements Listener {

    private final Main main;
    private final Map<UUID, ItemStack[]> savedInventories = new HashMap<>();
    private final Map<UUID, ItemStack[]> savedArmor = new HashMap<>();
    private final Map<UUID, Location> deathLocations = new HashMap<>();
    private final Set<UUID> waitingForRespawn = new HashSet<>();

    public DeathListener(Main main) {
        this.main = main;
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        // --- AJOUT : Verification de la distribution ---
        if (main.getGameTask() == null || !main.getGameTask().isRoleDistributionDone) {
            return;
        }

        Player victim = event.getEntity();
        UUID uuid = victim.getUniqueId();
        Location deathLoc = victim.getLocation();

        event.setDeathMessage("");

        savedInventories.put(uuid, victim.getInventory().getContents());
        savedArmor.put(uuid, victim.getInventory().getArmorContents());
        deathLocations.put(uuid, deathLoc);
        waitingForRespawn.add(uuid);

        event.getDrops().clear();

        Bukkit.getScheduler().runTaskLater(main, () -> {
            victim.spigot().respawn();
            victim.setGameMode(GameMode.SPECTATOR);
            victim.teleport(deathLoc);
            victim.sendMessage("§c§l[Mort] §7Vous êtes entre la vie et la mort pendant §e15 secondes§7...");
        }, 1L);

        Bukkit.getScheduler().runTaskLater(main, () -> {
            if (waitingForRespawn.contains(uuid)) {
                finalizeDeath(victim);
            }
        }, 300L);
    }

    private void finalizeDeath(Player victim) {
        UUID uuid = victim.getUniqueId();
        Location loc = deathLocations.get(uuid);

        if (loc != null && savedInventories.containsKey(uuid)) {
            for (ItemStack is : savedInventories.get(uuid)) {
                if (is != null && is.getType() != Material.AIR) loc.getWorld().dropItemNaturally(loc, is);
            }
            for (ItemStack is : savedArmor.get(uuid)) {
                if (is != null && is.getType() != Material.AIR) loc.getWorld().dropItemNaturally(loc, is);
            }
        }

        waitingForRespawn.remove(uuid);
        savedInventories.remove(uuid);
        savedArmor.remove(uuid);
        deathLocations.remove(uuid);

        victim.sendMessage("§c§l[Mort] §7Personne ne vous a sauvé. Vous êtes définitivement spectateur.");

        LGRole role = main.getRoleManager().getRole(uuid);
        String roleName = (role != null) ? role.getName() : "Aucun rôle";
        Bukkit.broadcastMessage(ChatColor.RED + victim.getName() + ChatColor.GRAY + " est mort ! Il était " + ChatColor.GOLD + roleName);

        checkWin();
    }

    public void revivePlayer(Player victim) {
        UUID uuid = victim.getUniqueId();
        if (!waitingForRespawn.contains(uuid)) return;

        waitingForRespawn.remove(uuid);

        victim.getInventory().setContents(savedInventories.get(uuid));
        victim.getInventory().setArmorContents(savedArmor.get(uuid));

        savedInventories.remove(uuid);
        savedArmor.remove(uuid);
        deathLocations.remove(uuid);

        teleportToSafeLocation(victim);

        victim.setGameMode(GameMode.SURVIVAL);
        victim.setHealth(20.0);
        victim.setFoodLevel(20);

        victim.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 100, 255));
        victim.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 60, 1));

        victim.sendMessage("§a§l[Vie] §7Vous avez été réanimé !");
        Bukkit.broadcastMessage("§a[LG UHC] " + victim.getName() + " a été réanimé et téléporté !");
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

    private void checkWin() {
        if (!main.isState(GState.GAME)) return;

        int villageoisRestants = 0;
        int loupsRestants = 0;

        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.getGameMode() == GameMode.SURVIVAL && !waitingForRespawn.contains(p.getUniqueId())) {
                LGRole role = main.getRoleManager().getRole(p.getUniqueId());
                if (role == null) continue;

                if (role.getCamp() == RoleCamp.LOUPS) loupsRestants++;
                else if (role.getCamp() == RoleCamp.VILLAGE) villageoisRestants++;
            }
        }

        if (loupsRestants == 0 && villageoisRestants > 0) finishGame("Le Village");
        else if (villageoisRestants == 0 && loupsRestants > 0) finishGame("Les Loups-Garous");
        else finishGame("Égalité");
    }

    private void finishGame(String winner) {
        if (main.getGameTask() != null) {
            main.getGameTask().cancel();
            main.setGameTask(null);
        }
        main.setState(GState.LOBBY);

        Bukkit.broadcastMessage("§d===============================");
        Bukkit.broadcastMessage("§6   VICTOIRE : §e" + winner);
        Bukkit.broadcastMessage("§d===============================");

        Bukkit.getScheduler().runTaskLater(main, () -> {
            for (Player p : Bukkit.getOnlinePlayers()) {
                p.teleport(Bukkit.getWorld("world").getSpawnLocation());
                p.setGameMode(GameMode.ADVENTURE);
                p.getInventory().clear();
            }
            main.getRoleManager().clearRoles();
            Bukkit.broadcast("§8[§6LG UHC§8] §7Le jeu est terminé. Retour au lobby.", "lguhc.state");
        }, 200L);
    }
}