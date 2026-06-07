package fr.caraito.lguhc.roles;

import fr.caraito.lguhc.Main;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class RoleAlpha extends LGRole {

    public RoleAlpha(Main instance) {
        super("Loup Alpha", RoleCamp.LOUPS, "Le chef de meute. Vous avez Force I la nuit et infligez des dégâts supplémentaires.");
    }

    @Override
    public void onDistribute(Player player) {
        player.sendMessage(ChatColor.RED + "========== RÔLE ==========");
        player.sendMessage(ChatColor.WHITE + "Tu es le : " + ChatColor.GOLD + getName());
        player.sendMessage(ChatColor.GRAY + getDescription());
        player.sendMessage(ChatColor.RED + "==========================");
    }
}
