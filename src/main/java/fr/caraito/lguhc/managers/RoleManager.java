package fr.caraito.lguhc.managers;

import fr.caraito.lguhc.Main;
import fr.caraito.lguhc.roles.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

public class RoleManager {

    private Map<UUID, LGRole> playerRoles = new HashMap<>();

    // --- AJOUT DE CETTE MÉTHODE POUR FIX L'ERREUR ---
    public Map<UUID, LGRole> getRoles() {
        return playerRoles;
    }

    public void distributeRoles() {
        List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());
        Collections.shuffle(players);

        if (players.isEmpty()) return;

        int total = players.size();
        int wolfLimit = total / 3; // Définit le tiers de loups (arrondi inférieur)
        int wolfCount = 0;
        int villageCount = 0;

        for (Player player : players) {
            if (wolfCount < wolfLimit) {
                // Attribution des Loups (1/3)
                if (wolfCount == 0) {
                    assign(player, new RoleIPDL(Main.getInstance()));
                } else if (wolfCount == 1) {
                    assign(player, new RoleLGPerfide(Main.getInstance()));
                } else {
                    assign(player, new RoleLG(Main.getInstance()));
                }
                wolfCount++;
            } else {
                // Attribution des Villageois (Le reste, soit ~2/3)
                if (villageCount == 0) {
                    assign(player, new RoleSalvateur(Main.getInstance()));
                } else if (villageCount == 1) {
                    assign(player, new RoleSorciere(Main.getInstance()));
                } else {
                    assign(player, new RoleVillageois(Main.getInstance()));
                }
                villageCount++;
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