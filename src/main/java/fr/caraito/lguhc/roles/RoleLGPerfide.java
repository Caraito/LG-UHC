package fr.caraito.lguhc.roles;

import fr.caraito.lguhc.Main;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class RoleLGPerfide extends LGRole {

    public RoleLGPerfide(Main main) {
        super(main);
    }

    @Override
    public String getName() {
        return "Loup-Garou Perfide";
    }

    @Override
    public RoleCamp getCamp() {
        return RoleCamp.LOUPS;
    }

    @Override
    public void onDistribute(Player player) {
        player.sendMessage("§c§l[Rôle] §fVous êtes le §cLoup-Garou Perfide§f !");
        player.sendMessage("§7Particularité : §eVous possédez la §cForce I §een permanence.");

        // On donne la force immédiatement
        player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 0, false, false));
    }
}