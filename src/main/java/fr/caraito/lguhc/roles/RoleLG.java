package fr.caraito.lguhc.roles;

import fr.caraito.lguhc.Main;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class RoleLG extends LGRole {

    public RoleLG(Main instance) {
        super("Loup-Garou", RoleCamp.LOUPS, "Votre but est d'éliminer tous les Villageois.");
    }

    @Override
    public void onDistribute(Player player) {
        player.sendMessage(ChatColor.RED + "========== RÔLE ==========");
        player.sendMessage(ChatColor.WHITE + "Tu es un : " + ChatColor.DARK_RED + getName());
        player.sendMessage(ChatColor.GRAY + getDescription());
        player.sendMessage(ChatColor.RED + "==========================");
    }
}