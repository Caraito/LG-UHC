package fr.caraito.lguhc.tasks;

import fr.caraito.lguhc.Main;
import fr.caraito.lguhc.enums.GState;
import fr.caraito.lguhc.roles.*;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GameTask extends BukkitRunnable {
    private final Main main;
    private int seconds = 0;
    private final Map<UUID, Boolean> hasUsedInvisibilityTonight = new HashMap<>();

    public GameTask(Main main) { this.main = main; }

    @Override
    public void run() {
        if (!main.isState(GState.GAME)) return;
        seconds++;

        boolean isMeetup = main.getConfig().getBoolean("meetup");
        int roleTime = isMeetup ? 300 : 1200; // 5 min vs 20 min

        // Distribution des rôles
        if (seconds == roleTime) {
            main.getRoleManager().distributeRoles();
            String epName = isMeetup ? "1" : "2";
            broadcastEpisodeHeader(epName);
        }
        // Gestion des épisodes suivants
        else if (seconds > roleTime && (seconds - roleTime) % 1200 == 0) {
            int epNumber = isMeetup ? ((seconds - roleTime) / 1200) + 1 : (seconds / 1200) + 1;
            broadcastEpisodeHeader(String.valueOf(epNumber));

            for (LGRole role : main.getRoleManager().getRoles().values()) {
                if (role instanceof RoleSalvateur) ((RoleSalvateur) role).setUsedThisEpisode(false);
            }
        }

        // --- Logique Cycles / Pouvoirs ---
        World world = Bukkit.getWorlds().get(0);
        boolean isNight = (world.getTime() >= 13000 && world.getTime() <= 23000);

        for (Player p : Bukkit.getOnlinePlayers()) {
            LGRole role = main.getRoleManager().getRole(p.getUniqueId());
            if (role == null) continue;

            if (role instanceof RoleLGPerfide) {
                if (p.hasPotionEffect(PotionEffectType.INCREASE_DAMAGE)) p.removePotionEffect(PotionEffectType.INCREASE_DAMAGE);

                boolean hasArmor = false;
                for (ItemStack armor : p.getInventory().getArmorContents()) {
                    if (armor != null && armor.getType() != Material.AIR) { hasArmor = true; break; }
                }

                if (isNight) {
                    if (!hasArmor && !hasUsedInvisibilityTonight.getOrDefault(p.getUniqueId(), false)) {
                        if (!p.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
                            p.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 6000, 0, false, false));
                            p.sendMessage("§c§l[Perfide] §fVous êtes §binvisible§f pour 5 min.");
                            hasUsedInvisibilityTonight.put(p.getUniqueId(), true);
                        }
                    }
                } else {
                    hasUsedInvisibilityTonight.put(p.getUniqueId(), false);
                }

                if (hasArmor && p.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
                    p.removePotionEffect(PotionEffectType.INVISIBILITY);
                    p.sendMessage("§c§l[Perfide] §fArmure remise, vous êtes de nouveau §avisible§f.");
                }
            } else if (role.getCamp() == RoleCamp.LOUPS) {
                if (isNight) {
                    if (!p.hasPotionEffect(PotionEffectType.INCREASE_DAMAGE))
                        p.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 200, 0, false, false));
                } else {
                    p.removePotionEffect(PotionEffectType.INCREASE_DAMAGE);
                }
            }
        }
    }

    private void broadcastEpisodeHeader(String ep) {
        Bukkit.broadcastMessage("§e§m---------------------------------");
        Bukkit.broadcastMessage("§fDébut de l'§6Épisode " + ep + " §f! Les rôles sont distribués.");
        Bukkit.broadcastMessage("§e§m---------------------------------");
    }

    public int getSeconds() { return seconds; }
}