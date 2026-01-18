package fr.caraito.lguhc.roles;

import fr.caraito.lguhc.Main;
import org.bukkit.entity.Player;

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
        player.sendMessage("§7Particularité : §eVous pouvez devenir §binvisible §ela nuit en retirant votre armure.");
        player.sendMessage("§7Note : §cVous ne possédez pas de bonus de force, même la nuit.");

        // On retire l'ajout de PotionEffect ici car il ne doit jamais avoir force
    }
}