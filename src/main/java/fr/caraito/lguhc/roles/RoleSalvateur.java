package fr.caraito.lguhc.roles;

import fr.caraito.lguhc.Main;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.UUID;

public class RoleSalvateur extends LGRole {

    private UUID lastProtected = null;
    private boolean usedThisEpisode = false;

    public RoleSalvateur(Main main) {
        super(main);
    }

    @Override
    public String getName() {
        return "Salvateur";
    }

    @Override
    public RoleCamp getCamp() {
        return RoleCamp.VILLAGE;
    }

    @Override
    public void onDistribute(Player player) {
        player.sendMessage("§a§l[Rôle] §fVous êtes le §aSalvateur§f !");
        player.sendMessage("§7Pouvoir : §eUtilisez votre §6Étoile §7pour donner §bRésistance I §7pendant 20 minutes.");
        player.sendMessage("§7Contraintes : §fUne fois par épisode, durant les §65 premières minutes§f. Interdiction de protéger la même personne 2x d'affilée.");

        ItemStack shield = new ItemStack(Material.NETHER_STAR);
        ItemMeta meta = shield.getItemMeta();
        meta.setDisplayName("§a§lBouclier de Fortune §7(Usage par épisode)");
        shield.setItemMeta(meta);

        player.getInventory().addItem(shield);
    }

    public UUID getLastProtected() {
        return lastProtected;
    }

    public void setLastProtected(UUID lastProtected) {
        this.lastProtected = lastProtected;
    }

    public boolean isUsedThisEpisode() {
        return usedThisEpisode;
    }

    public void setUsedThisEpisode(boolean usedThisEpisode) {
        this.usedThisEpisode = usedThisEpisode;
    }
}