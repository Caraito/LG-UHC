package fr.caraito.lguhc.roles;

import fr.caraito.lguhc.Main;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class RoleGrimeur extends LGRole {

    private String disguisedRole = null;

    public RoleGrimeur(Main instance) {
        super("Loup Grimeur", RoleCamp.LOUPS, "Vous pouvez choisir un rôle du village pour tromper la Voyante via /lggrimer <rôle>.");
    }

    @Override
    public void onDistribute(Player player) {
        player.sendMessage(ChatColor.RED + "========== RÔLE ==========");
        player.sendMessage(ChatColor.WHITE + "Tu es le : " + ChatColor.GOLD + getName());
        player.sendMessage(ChatColor.GRAY + getDescription());
        player.sendMessage(ChatColor.RED + "==========================");
    }

    public String getDisguisedRole() {
        return disguisedRole;
    }

    public void setDisguisedRole(String disguisedRole) {
        this.disguisedRole = disguisedRole;
    }
}
