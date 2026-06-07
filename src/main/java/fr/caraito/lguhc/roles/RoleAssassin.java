package fr.caraito.lguhc.roles;

import fr.caraito.lguhc.Main;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class RoleAssassin extends LGRole {

    public RoleAssassin(Main instance) {
        super("Assassin", RoleCamp.SOLITAIRE, "Vous devez tuer tout le monde. Vous avez Vitesse I permanente et Force I durant le jour.");
    }

    @Override
    public void onDistribute(Player player) {
        player.sendMessage(ChatColor.DARK_PURPLE + "========== RÔLE ==========");
        player.sendMessage(ChatColor.WHITE + "Tu es l' : " + ChatColor.GOLD + getName());
        player.sendMessage(ChatColor.GRAY + getDescription());
        player.sendMessage(ChatColor.DARK_PURPLE + "==========================");
    }
}
