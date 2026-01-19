package fr.caraito.lguhc.commands;

import fr.caraito.lguhc.Main;
import fr.caraito.lguhc.roles.*;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandRevive implements CommandExecutor {
    private final Main main;
    public CommandRevive(Main main) { this.main = main; }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player) || args.length < 2) return true;
        Player p = (Player) sender;
        Player victim = Bukkit.getPlayer(args[0]);
        String type = args[1];

        if (victim == null) return true;
        LGRole role = main.getRoleManager().getRole(p.getUniqueId());

        if (type.equals("sorciere") && role instanceof RoleSorciere) {
            RoleSorciere s = (RoleSorciere) role;
            if (s.hasRevivePotion()) {
                s.setHasRevivePotion(false);
                main.getDeathListener().revivePlayer(victim, "sorciere");
            }
        } else if (type.equals("ipdl") && role instanceof RoleIPDL) {
            RoleIPDL i = (RoleIPDL) role;
            if (i.hasInfection()) {
                i.setHasInfection(false);
                main.getDeathListener().revivePlayer(victim, "ipdl");
            }
        }
        return true;
    }
}