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
        int wolfLimit = total / 3;
        if (total == 2) wolfLimit = 1; // Fix pour 2 joueurs : 1 loup, 1 villageois

        List<LGRole> wolves = new ArrayList<>();
        wolves.add(new RoleIPDL(Main.getInstance()));
        wolves.add(new RoleLGPerfide(Main.getInstance()));
        wolves.add(new RoleAlpha(Main.getInstance()));
        wolves.add(new RoleGrimeur(Main.getInstance()));
        Collections.shuffle(wolves);

        List<LGRole> villagers = new ArrayList<>();
        villagers.add(new RoleSalvateur(Main.getInstance()));
        villagers.add(new RoleSorciere(Main.getInstance()));
        villagers.add(new RoleVoyante(Main.getInstance()));
        villagers.add(new RoleRenard(Main.getInstance()));
        villagers.add(new RolePetiteFille(Main.getInstance()));
        villagers.add(new RoleChasseur(Main.getInstance()));
        villagers.add(new RoleVillageois(Main.getInstance()));
        Collections.shuffle(villagers);

        List<LGRole> solitaires = new ArrayList<>();
        solitaires.add(new RoleLoupBlanc(Main.getInstance()));
        solitaires.add(new RoleAssassin(Main.getInstance()));
        Collections.shuffle(solitaires);

        int wolfCount = 0;
        int villageCount = 0;

        // Création d'une liste de tous les rôles à distribuer
        List<LGRole> rolesToDistribute = new ArrayList<>();

        // Distribution équilibrée (1/3 loups, 2/3 villageois)
        for (int i = 0; i < total; i++) {
            if (i < wolfLimit) {
                if (i < wolves.size()) rolesToDistribute.add(wolves.get(i));
                else rolesToDistribute.add(new RoleLG(Main.getInstance()));
            } else {
                // On peut potentiellement ajouter un solitaire si on a assez de joueurs (> 6 par exemple)
                if (i == total - 1 && total > 6) {
                    rolesToDistribute.add(solitaires.get(new Random().nextInt(solitaires.size())));
                } else {
                    if (villageCount < villagers.size()) rolesToDistribute.add(villagers.get(villageCount));
                    else rolesToDistribute.add(new RoleVillageois(Main.getInstance()));
                    villageCount++;
                }
            }
        }

        Collections.shuffle(rolesToDistribute);

        for (int i = 0; i < players.size(); i++) {
            assign(players.get(i), rolesToDistribute.get(i));
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