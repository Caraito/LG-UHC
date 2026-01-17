package fr.caraito.lguhc;

import fr.caraito.lguhc.commands.CommandSpawn;
import fr.caraito.lguhc.commands.CommandStart;
import fr.caraito.lguhc.commands.CommandStop;
import fr.caraito.lguhc.commands.CommandWorld;
import fr.caraito.lguhc.enums.GState;
import fr.caraito.lguhc.listeners.DeathListener;
import fr.caraito.lguhc.listeners.PlayerListener;
import fr.caraito.lguhc.managers.RoleManager;
import fr.caraito.lguhc.managers.ScoreboardManager;
import fr.caraito.lguhc.managers.WorldManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    private GState gameState;
    private WorldManager worldManager;
    private RoleManager roleManager;
    private ScoreboardManager sbManager;

    private static Main instance;

    @Override
    public void onEnable() {
        instance = this;

        // Initialisation de l'état au démarrage
        setState(GState.LOBBY);
        this.worldManager = new WorldManager();
        this.roleManager = new RoleManager();
        this.sbManager = new ScoreboardManager();

        // Enregistrement des événements (Listeners)
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        getServer().getPluginManager().registerEvents(new DeathListener(this), this);

        // Enregistrement des commandes
        getCommand("lgstart").setExecutor(new CommandStart(this));
        getCommand("lgworld").setExecutor(new CommandWorld(this));
        getCommand("lgspawn").setExecutor(new CommandSpawn());
        getCommand("lgstop").setExecutor(new CommandStop(this));

        // Message de démarrage stylisé dans la console
        Bukkit.getConsoleSender().sendMessage(ChatColor.GRAY + "---------------------------");
        Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD + "Plugin LG UHC par Caraito");
        Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "Statut: " + ChatColor.GREEN + "Operationnel (1.8.8)");
        Bukkit.getConsoleSender().sendMessage(ChatColor.GRAY + "---------------------------");

        // Runnable de mise à jour des scoreboards
        Bukkit.getScheduler().runTaskTimer(this, () -> {
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (sbManager != null) {
                    sbManager.updateScoreboard(p);
                }
            }
        }, 0L, 20L);
    }

    @Override
    public void onDisable() {
        getLogger().info("Arret du plugin LG UHC...");
    }

    /**
     * Change l'état de la partie et en informe tout le serveur
     */
    public void setState(GState state) {
        this.gameState = state;
        Bukkit.broadcastMessage(ChatColor.DARK_GRAY + "[" + ChatColor.GOLD + "LG UHC" + ChatColor.DARK_GRAY + "] "
                + ChatColor.GRAY + "L'etat du jeu est maintenant : " + ChatColor.YELLOW + state.name());
    }

    public boolean isState(GState state) {
        return this.gameState == state;
    }

    public GState getGameState() {
        return gameState;
    }

    public WorldManager getWorldManager() {
        return worldManager;
    }

    public RoleManager getRoleManager() {
        return roleManager;
    }

    public ScoreboardManager getSbManager() {
        return sbManager;
    }

    public static Main getInstance() {
        return instance;
    }
}