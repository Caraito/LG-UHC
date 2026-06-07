package fr.caraito.lguhc.roles;

import fr.caraito.lguhc.Main;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class RoleRenard extends LGRole {

    private int uses = 3;

    public RoleRenard(Main instance) {
        super("Renard", RoleCamp.VILLAGE, "Vous pouvez flairer un joueur. S'il y a un loup dans un rayon de 10 blocs, vous recevez une confirmation. (3 utilisations)");
    }

    @Override
    public void onDistribute(Player player) {
        player.sendMessage(ChatColor.GREEN + "========== RÔLE ==========");
        player.sendMessage(ChatColor.WHITE + "Tu es un : " + ChatColor.GOLD + getName());
        player.sendMessage(ChatColor.GRAY + getDescription());
        player.sendMessage(ChatColor.GREEN + "==========================");
    }

    public int getUses() {
        return uses;
    }

    public void setUses(int uses) {
        this.uses = uses;
    }
}
