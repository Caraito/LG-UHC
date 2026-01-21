package fr.caraito.lguhc.listeners;

import fr.caraito.lguhc.Main;
import fr.caraito.lguhc.enums.GState;
import fr.caraito.lguhc.roles.*;
import org.bukkit.*;
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

        // Sécurité : pas de mort gérée avant la distribution des rôles
        if (main.getGameTask() == null || !main.getGameTask().isRoleDistributionDone) return;

        Player victim = event.getEntity();
        UUID uuid = victim.getUniqueId();
        Location deathLoc = victim.getLocation();
        Player killer = victim.getKiller();

        event.setDeathMessage("");
        event.getDrops().clear();

        // Sauvegarde
        savedInventories.put(uuid, victim.getInventory().getContents());
        savedArmor.put(uuid, victim.getInventory().getArmorContents());
        deathLocations.put(uuid, deathLoc);
        waitingForRespawn.add(uuid);

        // Détection tueur loup
        boolean killedByWolf = false;
        if (killer != null) {
            LGRole killerRole = main.getRoleManager().getRole(killer.getUniqueId());
            if (killerRole != null && killerRole.getCamp() == RoleCamp.LOUPS) {
                killedByWolf = true;
            }
        }

        // Notification Sorcière / IPDL via tellraw
        for (Player p : Bukkit.getOnlinePlayers()) {
            LGRole role = main.getRoleManager().getRole(p.getUniqueId());
            if (role == null) continue;

            if (role instanceof RoleSorciere && ((RoleSorciere) role).hasRevivePotion()) {
                sendTellraw(p, victim.getName(), "sorciere");
            }
            else if (role instanceof RoleIPDL && ((RoleIPDL) role).hasInfection() && killedByWolf) {
                sendTellraw(p, victim.getName(), "ipdl");
            }
        }

        // Passage en limbo (spectateur temporaire)
        Bukkit.getScheduler().runTaskLater(main, () -> {
            victim.spigot().respawn();
            victim.setGameMode(GameMode.SPECTATOR);
            victim.teleport(deathLoc);
            victim.sendMessage("§c§l[Mort] §7Vous êtes entre la vie et la mort pendant §e15 secondes§7...");
        }, 1L);

        // Mort définitive après 15 secondes
        Bukkit.getScheduler().runTaskLater(main, () -> {
            if (waitingForRespawn.contains(uuid)) {
                finalizeDeath(victim);
            }
        }, 300L);
    }

    private void sendTellraw(Player receiver, String victimName, String type) {

        String roleName = type.equals("sorciere") ? "§5§lSorcière" : "§c§lInfect";

        String json =
                "[\"\","
                        + "{\"text\":\"§8[§6LG UHC§8] " + roleName + " §7: §e" + victimName + " §fest mort. \"},"
                        + "{\"text\":\"§a§l[RÉANIMER]\","
                        + "\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/lgrevive " + victimName + " " + type + "\"},"
                        + "\"hoverEvent\":{\"action\":\"show_text\",\"value\":\"§7Cliquez pour utiliser votre pouvoir !\"}"
                        + "}"
                        + "]";

        Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                "tellraw " + receiver.getName() + " " + json);
    }

    public void revivePlayer(Player victim, String type) {

        UUID uuid = victim.getUniqueId();
        if (!waitingForRespawn.contains(uuid)) return;
        waitingForRespawn.remove(uuid);

        // Infection IPDL
        if (type.equalsIgnoreCase("ipdl")) {
            LGRole role = main.getRoleManager().getRole(uuid);
            if (role != null) {
                role.setCamp(RoleCamp.LOUPS);
                victim.sendMessage("§c§l[Infection] §fVous avez été infecté ! Vous rejoignez les §cLoups§f.");
            }
        }

        // Restitution inventaire
        victim.getInventory().setContents(savedInventories.remove(uuid));
        victim.getInventory().setArmorContents(savedArmor.remove(uuid));
        deathLocations.remove(uuid);

        // Choix du respawn
        World world = victim.getWorld();
        List<String> respawns = main.getConfig().getStringList("worlds-data." + world.getName() + ".respawns");
        Location finalLoc;

        if (respawns != null && !respawns.isEmpty()) {
            String pos = respawns.remove(0);
            String[] p = pos.split(";");
            finalLoc = new Location(world,
                    Double.parseDouble(p[0]),
                    Double.parseDouble(p[1]),
                    Double.parseDouble(p[2]));
            main.getConfig().set("worlds-data." + world.getName() + ".respawns", respawns);
            main.saveConfig();
        } else {
            finalLoc = findSafeLocationFallback(world);
        }

        finalLoc.getChunk().load();
        victim.teleport(finalLoc);

        victim.setGameMode(GameMode.SURVIVAL);
        victim.setHealth(20.0);
        victim.setFoodLevel(20);

        // Protection post-revive
        victim.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 100, 255));
        victim.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 60, 1));

        victim.sendMessage("§a§l[Vie] §7Vous avez été réanimé !");
    }

    private void finalizeDeath(Player victim) {

        UUID uuid = victim.getUniqueId();
        Location loc = deathLocations.get(uuid);

        if (loc != null && savedInventories.containsKey(uuid)) {
            for (ItemStack is : savedInventories.get(uuid)) {
                if (is != null && is.getType() != Material.AIR)
                    loc.getWorld().dropItemNaturally(loc, is);
            }
            for (ItemStack is : savedArmor.get(uuid)) {
                if (is != null && is.getType() != Material.AIR)
                    loc.getWorld().dropItemNaturally(loc, is);
            }
        }

        waitingForRespawn.remove(uuid);
        savedInventories.remove(uuid);
        savedArmor.remove(uuid);
        deathLocations.remove(uuid);

        victim.sendMessage("§c§l[Mort] §7Personne ne vous a sauvé. Vous êtes définitivement spectateur.");

        LGRole role = main.getRoleManager().getRole(uuid);
        String roleName = (role != null) ? role.getName() : "Aucun rôle";

        Bukkit.broadcastMessage(ChatColor.RED + victim.getName()
                + ChatColor.GRAY + " est mort ! Il était "
                + ChatColor.GOLD + roleName);

        checkWin();
    }

    private void checkWin() {

        if (!main.isState(GState.GAME)) return;

        int village = 0;
        int loups = 0;

        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.getGameMode() == GameMode.SURVIVAL && !waitingForRespawn.contains(p.getUniqueId())) {
                LGRole role = main.getRoleManager().getRole(p.getUniqueId());
                if (role == null) continue;

                if (role.getCamp() == RoleCamp.LOUPS) loups++;
                else if (role.getCamp() == RoleCamp.VILLAGE) village++;
            }
        }

        if (loups == 0 && village > 0) finishGame("Le Village");
        else if (village == 0 && loups > 0) finishGame("Les Loups-Garous");
        else finishGame("Egalité");
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
                p.setGameMode(GameMode.ADVENTURE);
                p.getInventory().clear();
                p.getInventory().setArmorContents(null);
            }

            main.getRoleManager().clearRoles();
            main.getWorldManager().unloadCurrentWorld();

            Bukkit.broadcast("§8[§6LG UHC§8] §7Retour au lobby.", "lguhc.state");

        }, 200L);
    }

    private Location findSafeLocationFallback(World world) {

        Random r = new Random();
        int x = r.nextInt(3600) - 1800;
        int z = r.nextInt(3600) - 1800;

        world.getChunkAt(x >> 4, z >> 4).load();
        int y = world.getHighestBlockYAt(x, z);

        return new Location(world, x + 0.5, y + 1.2, z + 0.5);
    }

    public Set<UUID> getWaitingForRespawn() {
        return waitingForRespawn;
    }
}
