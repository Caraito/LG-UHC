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
    // Utiliser l'UUID est plus sûr pour éviter les fuites de mémoire
    private final Map<UUID, Boolean> hasUsedInvisibilityTonight = new HashMap<>();

    public GameTask(Main main) { this.main = main; }

    @Override
    public void run() {
        if (!main.isState(GState.GAME)) return;
        seconds++;

        if (seconds == 1200) main.getRoleManager().distributeRoles();

        World world = Bukkit.getWorlds().get(0);
        boolean isNight = (world.getTime() >= 13000 && world.getTime() <= 23000);

        for (Player p : Bukkit.getOnlinePlayers()) {
            LGRole role = main.getRoleManager().getRole(p.getUniqueId());
            if (role == null || role.getCamp() != RoleCamp.LOUPS) continue;

            if (role instanceof RoleLGPerfide) {
                // 1. Toujours retirer la force
                if (p.hasPotionEffect(PotionEffectType.INCREASE_DAMAGE)) {
                    p.removePotionEffect(PotionEffectType.INCREASE_DAMAGE);
                }

                // Vérification de l'armure (nécessaire jour et nuit)
                boolean hasArmor = false;
                for (ItemStack armor : p.getInventory().getArmorContents()) {
                    if (armor != null && armor.getType() != Material.AIR) {
                        hasArmor = true;
                        break;
                    }
                }

                if (isNight) {
                    // Logique d'activation la nuit
                    if (!hasArmor && !hasUsedInvisibilityTonight.getOrDefault(p.getUniqueId(), false)) {
                        if (!p.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
                            // 6000 ticks = 5 minutes | false, false = pas de particules
                            p.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 6000, 0, false, false));
                            p.sendMessage("§c§l[Perfide] §fVous êtes maintenant §binvisible§f pour 5 minutes.");
                            hasUsedInvisibilityTonight.put(p.getUniqueId(), true);
                        }
                    }
                } else {
                    // Reset du flag au lever du jour pour la nuit suivante
                    hasUsedInvisibilityTonight.put(p.getUniqueId(), false);
                }

                // Sécurité ARMURE : Si il remet son armure (Jour OU Nuit), on retire l'invisibilité
                if (hasArmor && p.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
                    p.removePotionEffect(PotionEffectType.INVISIBILITY);
                    p.sendMessage("§c§l[Perfide] §fVous avez remis votre armure, vous êtes de nouveau §avisible§f.");
                }

                continue; // Fin du traitement pour le Perfide

            } else if (isNight) {
                // Autres Loups-Garous
                if (!p.hasPotionEffect(PotionEffectType.INCREASE_DAMAGE))
                    p.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 200, 0, false, false));
            } else {
                p.removePotionEffect(PotionEffectType.INCREASE_DAMAGE);
            }
        }
    }

    public int getSeconds() { return seconds; }
}