package fr.caraito.lguhc.managers;

import fr.caraito.lguhc.Main;
import fr.caraito.lguhc.roles.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import java.util.*;

public class RoleManager {
    private final Map<UUID, LGRole> playerRoles = new HashMap<>();

    public void distributeRoles() {
        List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());
        Collections.shuffle(players);

        if (players.size() >= 2) {
            assign(players.remove(0), new RoleSalvateur(Main.getInstance()));
            assign(players.remove(0), new RoleLGPerfide(Main.getInstance()));

            for (Player p : players) {
                assign(p, new Random().nextBoolean() ? new RoleVillageois() : new RoleLG());
            }
        }
        Bukkit.broadcastMessage("§5§l[LG UHC] §fLes rôles ont été distribués !");
    }

    private void assign(Player p, LGRole r) {
        playerRoles.put(p.getUniqueId(), r);
        r.onDistribute(p);
    }

    public LGRole getRole(UUID id) { return playerRoles.get(id); }
    public void clearRoles() { playerRoles.clear(); }
}