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

        // Gestion des épisodes (20 min = 1200s)
        if (seconds == 1200) {
            main.getRoleManager().distributeRoles();
            Bukkit.broadcastMessage("§e§m---------------------------------");
            Bukkit.broadcastMessage("§fDébut de l'§6Épisode 2 §f! Les rôles sont distribués.");
            Bukkit.broadcastMessage("§e§m---------------------------------");
        } else if (seconds > 1200 && seconds % 1200 == 0) {
            int episode = (seconds / 1200) + 1;
            Bukkit.broadcastMessage("§e§m---------------------------------");
            Bukkit.broadcastMessage("§fDébut de l'§6Épisode " + episode);
            Bukkit.broadcastMessage("§e§m---------------------------------");

            // Reset du pouvoir du Salvateur pour le nouvel épisode
            for (LGRole role : main.getRoleManager().getRoles().values()) {
                if (role instanceof RoleSalvateur) {
                    ((RoleSalvateur) role).setUsedThisEpisode(false);
                }
            }
        }

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
                    p.sendMessage("§c§l[Perfide] §fArmure remise, vous êtes §avisible§f.");
                }
            } else if (role.getCamp() == RoleCamp.LOUPS && isNight) {
                if (!p.hasPotionEffect(PotionEffectType.INCREASE_DAMAGE))
                    p.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 200, 0, false, false));
            } else if (role.getCamp() == RoleCamp.LOUPS && !isNight) {
                p.removePotionEffect(PotionEffectType.INCREASE_DAMAGE);
            }
        }
    }

    public int getSeconds() { return seconds; }
}