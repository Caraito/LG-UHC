package fr.caraito.lguhc.roles;

import fr.caraito.lguhc.Main;
import org.bukkit.entity.Player;

public class RoleSorciere extends LGRole {

    private boolean hasRevivePotion = true;

    public RoleSorciere(Main main) {
        super(main);
    }

    @Override
    public String getName() { return "Sorcière"; }

    @Override
    public RoleCamp getCamp() { return RoleCamp.VILLAGE; }

    @Override
    public void onDistribute(Player player) {
        player.sendMessage("§a§l[Rôle] §fVous êtes la §5Sorcière§f !");
        player.sendMessage("§7Pouvoir : §eVous possédez une §dunique potion de résurrection§7.");
        player.sendMessage("§7Usage : §fLorsqu'un joueur meurt, vous avez 15s pour cliquer dans le chat.");
    }

    public boolean hasRevivePotion() { return hasRevivePotion; }
    public void setHasRevivePotion(boolean hasRevivePotion) { this.hasRevivePotion = hasRevivePotion; }
}