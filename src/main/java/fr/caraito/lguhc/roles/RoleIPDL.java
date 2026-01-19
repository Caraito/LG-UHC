package fr.caraito.lguhc.roles;

import fr.caraito.lguhc.Main;
import org.bukkit.entity.Player;

public class RoleIPDL extends LGRole {

    private boolean hasInfection = true;

    public RoleIPDL(Main main) {
        super(main);
    }

    @Override
    public String getName() { return "Infect Père des Loups"; }

    @Override
    public RoleCamp getCamp() { return RoleCamp.LOUPS; }

    @Override
    public void onDistribute(Player player) {
        player.sendMessage("§c§l[Rôle] §fVous êtes l'§4Infect Père des Loups§f !");
        player.sendMessage("§7Pouvoir : §eVous pouvez infecter un joueur §ctué par un loup§7.");
        player.sendMessage("§7Effet : §fLe joueur réanimé rejoint le camp des §cLoups-Garous§f.");
    }

    public boolean hasInfection() { return hasInfection; }
    public void setHasInfection(boolean hasInfection) { this.hasInfection = hasInfection; }
}