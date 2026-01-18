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
    // Stockage des joueurs en attente de réanimation
    private final Map<UUID, ItemStack[]> savedInventories = new HashMap<>();
    private final Map<UUID, ItemStack[]> savedArmor = new HashMap<>();
    private final Map<UUID, Location> deathLocations = new HashMap<>();
    private final Set<UUID> waitingForRespawn = new HashSet<>();

    public DeathListener(Main main) {
        this.main = main;
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        UUID uuid = victim.getUniqueId();
        Location deathLoc = victim.getLocation();

        event.setDeathMessage(""); // On gère le message plus tard

        // 2. Sauvegarde de l'inventaire et annulation des drops immédiats
        savedInventories.put(uuid, victim.getInventory().getContents());
        savedArmor.put(uuid, victim.getInventory().getArmorContents());
        deathLocations.put(uuid, deathLoc);
        waitingForRespawn.add(uuid);

        event.getDrops().clear(); // On ne drop rien pour l'instant

        // 3. Forcer le respawn et mettre en "Limbo" (Spectateur temporaire)
        Bukkit.getScheduler().runTaskLater(main, () -> {
            victim.spigot().respawn();
            victim.setGameMode(GameMode.SPECTATOR);
            victim.teleport(deathLoc);
            victim.sendMessage("§c§l[Mort] §7Vous êtes entre la vie et la mort pendant §e15 secondes§7...");
        }, 1L);

        // 4. Task de 15 secondes
        Bukkit.getScheduler().runTaskLater(main, () -> {
            if (waitingForRespawn.contains(uuid)) {
                // Le joueur n'a pas été réanimé par la Sorcière ou l'Infect
                finalizeDeath(victim);
            }
        }, 300L); // 15 secondes = 300 ticks
    }

    /**
     * Méthode appelée si le joueur n'est pas sauvé après 15s.
     */
    private void finalizeDeath(Player victim) {
        UUID uuid = victim.getUniqueId();
        Location loc = deathLocations.get(uuid);

        // Drop du stuff au sol
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

        // 1. Annonce du rôle
        LGRole role = main.getRoleManager().getRole(uuid);
        String roleName = (role != null) ? role.getName() : "Aucun rôle";
        Bukkit.broadcastMessage(ChatColor.RED + victim.getName() + ChatColor.GRAY + " est mort ! Il était " + ChatColor.GOLD + roleName);

        checkWin();
    }

    /**
     * Méthode à appeler par la Sorcière ou l'Infect pour réanimer un joueur.
     */
    public void revivePlayer(Player victim) {
        UUID uuid = victim.getUniqueId();
        if (!waitingForRespawn.contains(uuid)) return;

        waitingForRespawn.remove(uuid);

        // On lui rend son stuff
        victim.getInventory().setContents(savedInventories.get(uuid));
        victim.getInventory().setArmorContents(savedArmor.get(uuid));

        savedInventories.remove(uuid);
        savedArmor.remove(uuid);
        deathLocations.remove(uuid);

        // Téléportation aléatoire sécurisée
        teleportToSafeLocation(victim);

        victim.setGameMode(GameMode.SURVIVAL);
        victim.setHealth(20.0);
        victim.setFoodLevel(20);

        // Effets de protection après réanimation
        victim.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 100, 255));
        victim.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 60, 1));

        victim.sendMessage("§a§l[Vie] §7Vous avez été réanimé !");
        Bukkit.broadcastMessage("§a[LG UHC] " + victim.getName() + " a été réanimé et téléporté !");
    }

    // --- LOGIQUE DE TÉLÉPORTATION ALÉATOIRE SÉCURISÉE ---

    private void teleportToSafeLocation(Player player) {
        World world = player.getWorld();
        int radius = 1000; // Rayon de TP (à ajuster selon ta config)
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

    // --- LOGIQUE DE FIN DE PARTIE ---

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
        }, 200L);
    }
}