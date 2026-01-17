package fr.caraito.lguhc.managers;

import fr.caraito.lguhc.roles.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import java.util.*;

public class RoleManager {

    private final Map<UUID, LGRole> playerRoles = new HashMap<>();

    public void distributeRoles() {
        List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());
        Collections.shuffle(players); // Mélange les joueurs

        // Création de la liste des rôles selon le nombre de joueurs
        List<LGRole> availableRoles = new ArrayList<>();

        // Exemple de répartition : 1/4 de Loups, le reste en Villageois
        int lgCount = players.size() / 4;
        if (lgCount == 0) lgCount = 1;

        for (int i = 0; i < lgCount; i++) availableRoles.add(new RoleLG());
        while (availableRoles.size() < players.size()) {
            availableRoles.add(new RoleVillageois()); // À créer (classe simple)
        }

        Collections.shuffle(availableRoles); // Mélange les rôles

        for (int i = 0; i < players.size(); i++) {
            Player p = players.get(i);
            LGRole role = availableRoles.get(i);

            playerRoles.put(p.getUniqueId(), role);
            role.onDistribute(p);
        }
    }

    public LGRole getRole(UUID uuid) {
        return playerRoles.get(uuid);
    }

    public void clearRoles() {
        playerRoles.clear();
    }
}