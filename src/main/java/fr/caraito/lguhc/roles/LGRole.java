package fr.caraito.lguhc.roles;

import fr.caraito.lguhc.Main;
import org.bukkit.entity.Player;

public abstract class LGRole {

    private String name;
    private RoleCamp camp;
    private String description;

    public LGRole(String name, RoleCamp camp, String description) {
        this.name = name;
        this.camp = camp;
        this.description = description;
    }

    public LGRole(Main main) {
    }

    // Cette méthode sera exécutée quand le joueur reçoit son rôle
    public abstract void onDistribute(Player player);

    // Getters
    public String getName() {
        return name;
    }

    public RoleCamp getCamp() {
        return camp;
    }

    public String getDescription() {
        return description;
    }
}