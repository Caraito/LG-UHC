package fr.caraito.lguhc.roles;

import fr.caraito.lguhc.Main;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class RoleChasseur extends LGRole {

    public RoleChasseur(Main instance) {
        super("Chasseur", RoleCamp.VILLAGE, "À votre mort définitive, vous pouvez choisir un de vos attaquants pour réduire sa vie maximale à 4 cœurs pendant 10 minutes.");
    }

    @Override
    public void onDistribute(Player player) {
        player.sendMessage(ChatColor.GREEN + "========== RÔLE ==========");
        player.sendMessage(ChatColor.WHITE + "Tu es le : " + ChatColor.GOLD + getName());
        player.sendMessage(ChatColor.GRAY + getDescription());
        player.sendMessage(ChatColor.GREEN + "==========================");
    }
}
