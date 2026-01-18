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
    public boolean isRoleDistributionDone = false;

    public GameTask(Main main) { this.main = main; }

    @Override
    public void run() {
        if (!main.isState(GState.GAME)) return;
        seconds++;

        // --- TIMING DYNAMIQUE ---
        boolean isMeetup = main.getConfig().getBoolean("meetup", false);
        int roleTime = isMeetup ? 300 : 1200; // 5 min ou 20 min

        // 1. Distribution initiale des rôles
        if (seconds == roleTime) {
            main.getRoleManager().distributeRoles();
            String episodeName = isMeetup ? "1" : "2";
            broadcastEpisode(episodeName);
            isRoleDistributionDone = true;
        }

        // 2. Gestion des épisodes suivants (Toutes les 20 minutes : 1200, 2400, 3600...)
        // On vérifie que ce n'est pas le moment où on distribue les rôles pour éviter les doubles messages
        else if (seconds % 1200 == 0) {
            int episode = (seconds / 1200) + 1;
            broadcastEpisode(String.valueOf(episode));

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

            // --- LOGIQUE LOUP-GAROU PERFIDE ---
            if (role instanceof RoleLGPerfide) {
                // Jamais de force
                if (p.hasPotionEffect(PotionEffectType.INCREASE_DAMAGE)) {
                    p.removePotionEffect(PotionEffectType.INCREASE_DAMAGE);
                }

                // Vérification de l'armure
                boolean hasArmor = false;
                for (ItemStack armor : p.getInventory().getArmorContents()) {
                    if (armor != null && armor.getType() != Material.AIR) {
                        hasArmor = true;
                        break;
                    }
                }

                if (isNight) {
                    // Activation de l'invisibilité (une fois par nuit)
                    if (!hasArmor && !hasUsedInvisibilityTonight.getOrDefault(p.getUniqueId(), false)) {
                        if (!p.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
                            p.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 6000, 0, false, false));
                            p.sendMessage("§c§l[Perfide] §fVous avez retiré votre armure, vous êtes §binvisible§f pour 5 min.");
                            hasUsedInvisibilityTonight.put(p.getUniqueId(), true);
                        }
                    }
                } else {
                    // Reset du flag de nuit au lever du jour
                    hasUsedInvisibilityTonight.put(p.getUniqueId(), false);
                }

                // Retrait immédiat si l'armure est remise
                if (hasArmor && p.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
                    p.removePotionEffect(PotionEffectType.INVISIBILITY);
                    p.sendMessage("§c§l[Perfide] §fArmure remise, vous êtes de nouveau §avisible§f.");
                }

                // On utilise continue pour ne pas appliquer la force des LG classiques
                continue;
            }

            // --- LOGIQUE LOUPS CLASSIQUES ---
            if (role.getCamp() == RoleCamp.LOUPS) {
                if (isNight) {
                    if (!p.hasPotionEffect(PotionEffectType.INCREASE_DAMAGE)) {
                        p.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 200, 0, false, false));
                    }
                } else {
                    if (p.hasPotionEffect(PotionEffectType.INCREASE_DAMAGE)) {
                        p.removePotionEffect(PotionEffectType.INCREASE_DAMAGE);
                    }
                }
            }
        }
    }

    private void broadcastEpisode(String name) {
        Bukkit.broadcastMessage("§e§m---------------------------------");
        Bukkit.broadcastMessage("§fDébut de l'§6Épisode " + name + " §f!");
        Bukkit.broadcastMessage("§e§m---------------------------------");
    }

    public int getSeconds() { return seconds; }

}