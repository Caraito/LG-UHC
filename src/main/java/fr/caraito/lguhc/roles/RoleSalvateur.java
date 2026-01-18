package fr.caraito.lguhc.roles;

import fr.caraito.lguhc.Main;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class RoleSalvateur extends LGRole {

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
        player.sendMessage("§7Pouvoir : §eUtilisez votre §6Bouclier §7(clic droit) pour obtenir §bRésistance II §7pendant 30 secondes (Usage unique).");

        ItemStack shield = new ItemStack(Material.IRON_DOOR);
        ItemMeta meta = shield.getItemMeta();
        meta.setDisplayName("§a§lBouclier de Fortune §7(Usage unique)");
        shield.setItemMeta(meta);

        player.getInventory().addItem(shield);
    }
}