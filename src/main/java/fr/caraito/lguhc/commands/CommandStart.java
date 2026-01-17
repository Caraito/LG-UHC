package fr.caraito.lguhc.commands;

import fr.caraito.lguhc.Main;
import fr.caraito.lguhc.enums.GState;
import fr.caraito.lguhc.tasks.GameTask;
import org.bukkit.Bukkit;
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

        if (!sender.isOp()) {
            sender.sendMessage(ChatColor.RED + "Tu n'as pas la permission !");
            return true;
        }

        // On ne peut lancer que si on est au Lobby
        if (main.isState(GState.LOBBY)) {

            // 1. On tente de préparer le monde et de TP
            // On utilise un rayon de 800 blocs
            boolean success = main.getWorldManager().prepareAndTeleport(800);

            if (success) {
                // 2. Si le TP a réussi, on change l'état
                main.setState(GState.STARTING);

                // 3. On passe en jeu
                main.setState(GState.GAME);

                // 4. On lance le chrono de la partie (pour les rôles à 20 min)
                new GameTask(main).runTaskTimer(main, 0L, 20L);

                Bukkit.broadcastMessage(ChatColor.GREEN + "La partie est lancée !");
            } else {
                // 5. Si aucun monde n'était prêt dans la liste
                sender.sendMessage(ChatColor.RED + "Erreur : Aucun monde de disponible !");
                sender.sendMessage(ChatColor.GRAY + "Utilise /lgworld create <nombre> pour en préparer un.");
            }

        } else {
            sender.sendMessage(ChatColor.RED + "La partie a déjà commencé.");
        }

        return true;
    }
}