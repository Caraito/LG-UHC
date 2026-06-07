package fr.caraito.lguhc.roles;

import fr.caraito.lguhc.Main;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class RoleVoyante extends LGRole {

    private long lastUse = 0;

    public RoleVoyante(Main instance) {
        super("Voyante", RoleCamp.VILLAGE, "Une fois toutes les 10 min (Meetup) ou 20 min (Normal), vous pouvez voir le rôle d'un joueur.");
    }

    @Override
    public void onDistribute(Player player) {
        player.sendMessage(ChatColor.GREEN + "========== RÔLE ==========");
        player.sendMessage(ChatColor.WHITE + "Tu es une : " + ChatColor.GOLD + getName());
        player.sendMessage(ChatColor.GRAY + getDescription());
        player.sendMessage(ChatColor.GREEN + "==========================");
    }

    public long getLastUse() {
        return lastUse;
    }

    public void setLastUse(long lastUse) {
        this.lastUse = lastUse;
    }
}
