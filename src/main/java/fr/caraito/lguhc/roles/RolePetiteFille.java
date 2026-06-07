package fr.caraito.lguhc.roles;

import fr.caraito.lguhc.Main;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class RolePetiteFille extends LGRole {

    public RolePetiteFille(Main instance) {
        super("Petite Fille", RoleCamp.VILLAGE, "La nuit, retirez votre armure pour devenir invisible (1x/nuit, 10 min). Attention à l'effet de faiblesse !");
    }

    @Override
    public void onDistribute(Player player) {
        player.sendMessage(ChatColor.GREEN + "========== RÔLE ==========");
        player.sendMessage(ChatColor.WHITE + "Tu es la : " + ChatColor.GOLD + getName());
        player.sendMessage(ChatColor.GRAY + getDescription());
        player.sendMessage(ChatColor.GREEN + "==========================");
    }
}
