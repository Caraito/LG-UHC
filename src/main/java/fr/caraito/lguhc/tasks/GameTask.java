package fr.caraito.lguhc.tasks;

import fr.caraito.lguhc.Main;
import fr.caraito.lguhc.enums.GState;
import fr.caraito.lguhc.roles.*;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
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

        // --- CONFIGURATION DYNAMIQUE ---
        boolean isMeetup = main.getConfig().getBoolean("meetup", false);
        int roleTime = isMeetup ? main.getConfig().getInt("role_time_meetup", 300) : main.getConfig().getInt("role_time_normal", 1200);
        int episodeDuration = main.getConfig().getInt("episode_duration", 1200);
        int borderShrink = main.getConfig().getInt("border_shrink_per_minute", 0);

        // 1. Distribution initiale des rôles
        if (seconds == roleTime) {
            main.getRoleManager().distributeRoles();
            broadcastEpisode("1");
            isRoleDistributionDone = true;
        }

        // Révélation des rôles à 20 min (si option activée)
        if (seconds == 1200 && main.getConfig().getBoolean("reveal_roles", false)) {
            List<String> roleNames = new ArrayList<>();
            for (LGRole r : main.getRoleManager().getRoles().values()) {
                roleNames.add(r.getName());
            }
            Bukkit.broadcastMessage("§6§l[Révélation] §fLes rôles présents dans cette partie sont :");
            Bukkit.broadcastMessage("§e" + String.join(", ", roleNames));
        }

        // 2. Gestion des épisodes suivants
        if (seconds > roleTime && (seconds - roleTime) % episodeDuration == 0) {
            int episode = ((seconds - roleTime) / episodeDuration) + 1;
            broadcastEpisode(String.valueOf(episode));

            // Reset du pouvoir du Salvateur pour le nouvel épisode
            for (LGRole role : main.getRoleManager().getRoles().values()) {
                if (role instanceof RoleSalvateur) {
                    ((RoleSalvateur) role).setUsedThisEpisode(false);
                }
            }
        }

        // 3. Réduction de la bordure
        if (borderShrink > 0 && seconds % 60 == 0) {
            for (World world : Bukkit.getWorlds()) {
                double currentSize = world.getWorldBorder().getSize();
                double newSize = Math.max(10, currentSize - (borderShrink * 2)); // *2 car on réduit le diamètre
                if (newSize < currentSize) {
                    world.getWorldBorder().setSize(newSize, 60); // Animation sur 60 secondes
                }
            }
        }

        World world = Bukkit.getWorlds().get(0);
        boolean isNight = (world.getTime() >= 13000 && world.getTime() <= 23000);

        for (Player p : Bukkit.getOnlinePlayers()) {
            LGRole role = main.getRoleManager().getRole(p.getUniqueId());
            if (role == null) continue;

            // --- LOGIQUE PETITE FILLE / PERFIDE ---
            if (role instanceof RolePetiteFille || role instanceof RoleLGPerfide) {
                if (p.hasPotionEffect(PotionEffectType.INCREASE_DAMAGE)) {
                    p.removePotionEffect(PotionEffectType.INCREASE_DAMAGE);
                }

                boolean hasArmor = false;
                for (ItemStack armor : p.getInventory().getArmorContents()) {
                    if (armor != null && armor.getType() != Material.AIR) {
                        hasArmor = true;
                        break;
                    }
                }

                if (isNight) {
                    if (!hasArmor && !hasUsedInvisibilityTonight.getOrDefault(p.getUniqueId(), false)) {
                        if (!p.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
                            p.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 12000, 0, false, false));
                            p.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 12000, 0, false, false));
                            String roleNameMsg = (role instanceof RolePetiteFille) ? "Petite Fille" : "Perfide";
                            p.sendMessage("§c§l[" + roleNameMsg + "] §fVous avez retiré votre armure, vous êtes §binvisible§f pour 10 min.");
                            hasUsedInvisibilityTonight.put(p.getUniqueId(), true);
                        }
                    }
                } else {
                    hasUsedInvisibilityTonight.put(p.getUniqueId(), false);
                }

                if (hasArmor && p.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
                    p.removePotionEffect(PotionEffectType.INVISIBILITY);
                    p.removePotionEffect(PotionEffectType.WEAKNESS);
                    String roleNameMsg = (role instanceof RolePetiteFille) ? "Petite Fille" : "Perfide";
                    p.sendMessage("§c§l[" + roleNameMsg + "] §fArmure remise, vous êtes de nouveau §avisible§f.");
                }
                continue;
            }

            // --- LOGIQUE ASSASSIN ---
            if (role instanceof RoleAssassin) {
                if (!p.hasPotionEffect(PotionEffectType.SPEED)) {
                    p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 200, 0, false, false));
                }
                if (!isNight) {
                    if (!p.hasPotionEffect(PotionEffectType.INCREASE_DAMAGE)) {
                        p.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 200, 0, false, false));
                    }
                } else if (p.hasPotionEffect(PotionEffectType.INCREASE_DAMAGE)) {
                    p.removePotionEffect(PotionEffectType.INCREASE_DAMAGE);
                }
                continue;
            }

            // --- LOGIQUE LOUPS CLASSIQUES ---
            if (role.getCamp() == RoleCamp.LOUPS || role instanceof RoleLoupBlanc) {
                if (isNight) {
                    if (!p.hasPotionEffect(PotionEffectType.INCREASE_DAMAGE)) {
                        p.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 200, 0, false, false));
                    }
                } else {
                    if (p.hasPotionEffect(PotionEffectType.INCREASE_DAMAGE)) {
                        for (PotionEffect effect : p.getActivePotionEffects()) {
                            if (effect.getType().equals(PotionEffectType.INCREASE_DAMAGE) && effect.getDuration() <= 200) {
                                p.removePotionEffect(PotionEffectType.INCREASE_DAMAGE);
                            }
                        }
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