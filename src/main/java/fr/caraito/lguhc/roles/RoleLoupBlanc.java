package fr.caraito.lguhc.roles;

import fr.caraito.lguhc.Main;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class RoleLoupBlanc extends LGRole {

    private long lastSacrifice = 0;

    public RoleLoupBlanc(Main instance) {
        super("Loup Blanc", RoleCamp.SOLITAIRE, "Vous devez être le dernier survivant. Toutes les 2 nuits, sacrifiez un loup via /lgsacrifice <joueur> pour gagner 1 cœur permanent et Force I pendant 5 min.");
    }

    @Override
    public void onDistribute(Player player) {
        player.sendMessage(ChatColor.DARK_PURPLE + "========== RÔLE ==========");
        player.sendMessage(ChatColor.WHITE + "Tu es le : " + ChatColor.GOLD + getName());
        player.sendMessage(ChatColor.GRAY + getDescription());
        player.sendMessage(ChatColor.DARK_PURPLE + "==========================");
    }

    public long getLastSacrifice() {
        return lastSacrifice;
    }

    public void setLastSacrifice(long lastSacrifice) {
        this.lastSacrifice = lastSacrifice;
    }
}
