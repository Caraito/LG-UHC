package fr.caraito.lguhc.tasks;

import fr.caraito.lguhc.Main;
import fr.caraito.lguhc.enums.GState;
import fr.caraito.lguhc.roles.*;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class GameTask extends BukkitRunnable {
    private final Main main;
    private int seconds = 0;

    public GameTask(Main main) { this.main = main; }

    @Override
    public void run() {
        if (!main.isState(GState.GAME)) return;
        seconds++;

        if (seconds == 1200) main.getRoleManager().distributeRoles();

        World world = Bukkit.getWorlds().get(0); // On récupère le monde de jeu
        boolean isNight = (world.getTime() >= 13000 && world.getTime() <= 23000);

        for (Player p : Bukkit.getOnlinePlayers()) {
            LGRole role = main.getRoleManager().getRole(p.getUniqueId());
            if (role == null || role.getCamp() != RoleCamp.LOUPS) continue;

            if (role instanceof RoleLGPerfide) {
                // Le Loup-Garou Perfide ne doit jamais avoir force

                continue;

            } else if (isNight) {
                if (!p.hasPotionEffect(PotionEffectType.INCREASE_DAMAGE))
                    p.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 200, 0, false, false));
            } else {
                p.removePotionEffect(PotionEffectType.INCREASE_DAMAGE);
            }
        }
    }

    public int getSeconds() { return seconds; }
}