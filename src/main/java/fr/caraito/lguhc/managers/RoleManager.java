package fr.caraito.lguhc.managers;

import fr.caraito.lguhc.Main;
import fr.caraito.lguhc.roles.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

public class RoleManager {

    private Map<UUID, LGRole> playerRoles = new HashMap<>();

    public void distributeRoles() {
        List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());
        Collections.shuffle(players);

        if (players.isEmpty()) return;

        // --- LOGIQUE DE DISTRIBUTION ADAPTATIVE ---
        // Si on est seul : on prend un rôle au hasard
        if (players.size() == 1) {
            assign(players.get(0), new RoleSalvateur(Main.getInstance()));
        }
        // Si on est 2 ou plus
        else {
            assign(players.remove(0), new RoleSalvateur(Main.getInstance()));
            assign(players.remove(0), new RoleLGPerfide(Main.getInstance()));

            // Reste des joueurs
            for (Player player : players) {
                if (new Random().nextBoolean()) {
                    assign(player, new RoleVillageois(Main.getInstance()));
                } else {
                    assign(player, new RoleLG(Main.getInstance()));
                }
            }
        }

        Bukkit.broadcastMessage("§5§l[LG UHC] §fLes rôles ont été distribués !");
    }

    private void assign(Player player, LGRole role) {
        playerRoles.put(player.getUniqueId(), role);
        role.onDistribute(player);
    }

    public LGRole getRole(UUID uuid) {
        return playerRoles.get(uuid);
    }

    public void clearRoles() {
        playerRoles.clear();
    }
}