package fr.caraito.lguhc.roles;

import fr.caraito.lguhc.Main;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class RoleVillageois extends LGRole {

    public RoleVillageois(Main instance) {
        super("Simple Villageois", RoleCamp.VILLAGE, "Votre but est d'éliminer tous les Loups-Garous.");
    }

    @Override
    public void onDistribute(Player player) {
        player.sendMessage(ChatColor.GREEN + "========== RÔLE ==========");
        player.sendMessage(ChatColor.WHITE + "Tu es un : " + ChatColor.GOLD + getName());
        player.sendMessage(ChatColor.GRAY + getDescription());
        player.sendMessage(ChatColor.GREEN + "==========================");
    }
}