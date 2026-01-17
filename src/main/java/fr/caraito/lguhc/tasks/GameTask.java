package fr.caraito.lguhc.tasks;

import fr.caraito.lguhc.Main;
import fr.caraito.lguhc.enums.GState;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

public class GameTask extends BukkitRunnable {

    private final Main main;
    private int seconds = 0; // Plus de static ici

    public GameTask(Main main) {
        this.main = main;
    }

    @Override
    public void run() {
        if (!main.isState(GState.GAME)) return;

        seconds++;

        if (seconds == 1200) {
            Bukkit.broadcastMessage("§5§l[LG UHC] Distribution des rôles !");
            main.getRoleManager().distributeRoles();
        }
    }

    public int getSeconds() {
        return seconds;
    }
}