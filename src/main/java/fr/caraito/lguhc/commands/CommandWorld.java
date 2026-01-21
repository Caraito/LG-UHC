package fr.caraito.lguhc.commands;

import fr.caraito.lguhc.Main;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CommandWorld implements CommandExecutor {

    private final Main main;

    public CommandWorld(Main main) {
        this.main = main;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!sender.isOp()) return true;

        // Si aucun argument ou argument mal tapé, on affiche l'aide
        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }

        try {
            switch (args[0].toLowerCase()) {
                case "create":
                    if (args.length < 2) {
                        sender.sendMessage("§cUsage: /lgworld create <nb_mondes> [--stop]");
                        return true;
                    }
                    int amount = Integer.parseInt(args[1]);
                    boolean shouldStop = args.length == 3 && args[2].equalsIgnoreCase("--stop");

                    main.getWorldManager().generateMultipleWorlds(amount, shouldStop);
                    break;

                case "prepare":
                    if (args.length != 4) {
                        sender.sendMessage("§cUsage: /lgworld prepare <spawns_par_map> <respawns_par_map> <nb_maps>");
                        return true;
                    }
                    // args[1]: spawns, args[2]: respawns, args[3]: maps
                    main.getWorldManager().prepareSafeLocations(
                            Integer.parseInt(args[1]),
                            Integer.parseInt(args[2]),
                            Integer.parseInt(args[3])
                    );
                    break;

                case "load":
                    if (args.length != 3) {
                        sender.sendMessage("§cUsage: /lgworld load <le_nombre_de_map> <radius_blocks>");
                        return true;
                    }
                    main.getWorldManager().preLoadChunks(
                            Integer.parseInt(args[1]),
                            Integer.parseInt(args[2])
                    );
                    break;

                case "remove":
                    main.getWorldManager().deleteAllWorldFolders();
                    sender.sendMessage("§a[LG UHC] Nettoyage des dossiers mondes effectué.");
                    break;

                default:
                    sendHelp(sender);
                    break;
            }
        } catch (NumberFormatException e) {
            sender.sendMessage("§cErreur: Les paramètres doivent être des nombres entiers.");
        }

        return true;
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage("§6§m---------§r §6Aide /lgworld §6§m---------");
        sender.sendMessage("§e/lgworld create <nb> [--stop] §7: Génère X mondes. --stop éteint le serveur après.");
        sender.sendMessage("§e/lgworld remove §7: Supprime tous les mondes LG du dossier serveur.");
        sender.sendMessage("§e/lgworld prepare <spawns_par_map> <respawns_par_map> <nb_maps> : Prépare les emplacements sûrs.");
        sender.sendMessage("§e/lgworld load <nombre_de_maps> <radius> §7: Pré-charge (génère) les chunks.");
        sender.sendMessage("§6§m------------------------------");
    }
}