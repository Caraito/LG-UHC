package fr.caraito.lguhc.commands;

import fr.caraito.lguhc.Main;
import fr.caraito.lguhc.managers.RoleManager;
import fr.caraito.lguhc.roles.*;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class CommandLGInfo implements CommandExecutor {

    private final Main main;

    public CommandLGInfo(Main main) {
        this.main = main;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return true;
        Player player = (Player) sender;

        RoleManager roleManager = main.getRoleManager();
        LGRole role = roleManager.getRole(player.getUniqueId());

        // Vérification : Si le joueur n'a pas de rôle
        if (role == null) {
            player.sendMessage("§c§l[Erreur] §fVous n'avez pas de rôle assigné.");
            return true;
        }

        // 1. Affichage des infos selon le type de rôle (reprend le style de onDistribute)
        displayRoleInfo(player, role);

        // 2. Si c'est un loup, on ajoute la liste des compères
        if (role.getCamp() == RoleCamp.LOUPS) {
            List<String> wolfNames = new ArrayList<>();
            Map<UUID, LGRole> allRoles = roleManager.getRoles();

            for (Map.Entry<UUID, LGRole> entry : allRoles.entrySet()) {
                if (entry.getValue().getCamp() == RoleCamp.LOUPS) {
                    String name = Bukkit.getOfflinePlayer(entry.getKey()).getName();
                    if (name != null) {
                        if (entry.getKey().equals(player.getUniqueId())) {
                            wolfNames.add("§c" + name + " §7(Vous)");
                        } else {
                            wolfNames.add("§f" + name);
                        }
                    }
                }
            }

            player.sendMessage("");
            player.sendMessage("§c§lMembres de la meute :");
            player.sendMessage("§7- " + String.join("\n§7- ", wolfNames));
            player.sendMessage("§c§l===============================");
        }

        return true;
    }

    private void displayRoleInfo(Player player, LGRole role) {
        if (role instanceof RoleIPDL) {
            player.sendMessage("§c§l[Rôle] §fVous êtes l'§4Infect Père des Loups§f !");
            player.sendMessage("§7Pouvoir : §eVous pouvez infecter un joueur §ctué par un loup§7.");
            player.sendMessage("§7Effet : §fLe joueur réanimé rejoint le camp des §cLoups-Garous§f.");
        } else if (role instanceof RoleLGPerfide) {
            player.sendMessage("§c§l[Rôle] §fVous êtes le §cLoup-Garou Perfide§f !");
            player.sendMessage("§7Particularité : §eVous pouvez devenir §binvisible §ela nuit en retirant votre armure.");
            player.sendMessage("§7Note : §cVous ne possédez pas de bonus de force, même la nuit.");
        } else if (role instanceof RoleSalvateur) {
            player.sendMessage("§a§l[Rôle] §fVous êtes le §aSalvateur§f !");
            player.sendMessage("§7Pouvoir : §eUtilisez votre §6Étoile §7pour donner §bRésistance I §7pendant 20 minutes.");
            player.sendMessage("§7Contraintes : §fUne fois par épisode, durant les §65 premières minutes§f. Interdiction de protéger la même personne 2x d'affilée.");
        } else if (role instanceof RoleSorciere) {
            player.sendMessage("§a§l[Rôle] §fVous êtes la §5Sorcière§f !");
            player.sendMessage("§7Pouvoir : §eVous possédez une §dunique potion de résurrection§7.");
            player.sendMessage("§7Usage : §fLorsqu'un joueur meurt, vous avez 15s pour cliquer dans le chat.");
        } else if (role instanceof RoleLG) {
            player.sendMessage("§c========== RÔLE ==========");
            player.sendMessage("§fTu es un : §4" + role.getName());
            player.sendMessage("§7" + role.getDescription());
        } else if (role instanceof RoleVillageois) {
            player.sendMessage("§a========== RÔLE ==========");
            player.sendMessage("§fTu es un : §6" + role.getName());
            player.sendMessage("§7" + role.getDescription());
        } else {
            // Cas par défaut si un nouveau rôle est ajouté
            player.sendMessage("§6§l[Rôle] §fVous êtes §e" + role.getName());
            player.sendMessage("§7" + role.getDescription());
        }
    }
}