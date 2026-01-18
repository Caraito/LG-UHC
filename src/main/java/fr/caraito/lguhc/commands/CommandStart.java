package fr.caraito.lguhc.commands;

import fr.caraito.lguhc.Main;
import fr.caraito.lguhc.enums.GState;
import fr.caraito.lguhc.tasks.GameTask;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CommandStart implements CommandExecutor {

    private final Main main;

    public CommandStart(Main main) {
        this.main = main;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!sender.isOp()) return true;

        if (main.isState(GState.LOBBY)) {
            boolean success = main.getWorldManager().prepareAndTeleport(1800);

            if (success) {
                main.setState(GState.GAME);

                // Arrêt de l'ancienne tâche si elle existe
                if (main.getGameTask() != null) {
                    main.getGameTask().cancel();
                }

                // Lancement de la nouvelle tâche
                GameTask newTask = new GameTask(main);
                newTask.runTaskTimer(main, 0L, 20L);
                main.setGameTask(newTask);

                sender.sendMessage(ChatColor.GREEN + "Partie lancée !");
            } else {
                sender.sendMessage(ChatColor.RED + "Aucun monde disponible ! /lgworld create");
            }
        }
        return true;
    }
}