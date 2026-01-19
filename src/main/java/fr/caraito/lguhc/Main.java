package fr.caraito.lguhc;

import fr.caraito.lguhc.commands.*;
import fr.caraito.lguhc.enums.GState;
import fr.caraito.lguhc.listeners.DeathListener;
import fr.caraito.lguhc.listeners.PlayerDeathBeforeRoleListener;
import fr.caraito.lguhc.listeners.PlayerListener;
import fr.caraito.lguhc.managers.RoleManager;
import fr.caraito.lguhc.managers.ScoreboardManager;
import fr.caraito.lguhc.managers.WorldManager;
import fr.caraito.lguhc.tasks.GameTask;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    private GState gameState;
    private WorldManager worldManager;
    private RoleManager roleManager;
    private ScoreboardManager sbManager;
    private GameTask gameTask;
    private DeathListener deathListener;

    private static Main instance;

    @Override
    public void onEnable() {
        instance = this;

        saveDefaultConfig();

        setState(GState.LOBBY);

        // Initialisation des managers
        this.worldManager = new WorldManager();
        this.roleManager = new RoleManager();
        this.sbManager = new ScoreboardManager();

        // CORRECTION 1 & 2 : On passe 'this' et on stocke l'instance
        this.deathListener = new DeathListener(this);

        // Enregistrement des listeners
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        // CORRECTION 3 : On utilise l'instance déjà créée au dessus
        getServer().getPluginManager().registerEvents(this.deathListener, this);
        getServer().getPluginManager().registerEvents(new PlayerDeathBeforeRoleListener(this), this);

        // Commandes
        getCommand("lgstart").setExecutor(new CommandStart(this));
        getCommand("lgworld").setExecutor(new CommandWorld(this));
        getCommand("lgspawn").setExecutor(new CommandSpawn());
        getCommand("lgstop").setExecutor(new CommandStop(this));
        getCommand("lgconfig").setExecutor(new CommandConfig(this));
        getCommand("lgrevive").setExecutor(new CommandRevive(this));

        Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD + "Plugin LG UHC par Caraito - Operationnel");

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
        Bukkit.getLogger().info("LG UHC désactivé.");
    }

    public void setState(GState state) {
        this.gameState = state;
        Bukkit.broadcast("§8[§6LG UHC§8] §7État : §e" + state.name(), "lguhc.state");
    }

    // Getters
    public boolean isState(GState state) { return this.gameState == state; }
    public WorldManager getWorldManager() { return worldManager; }
    public RoleManager getRoleManager() { return roleManager; }

    // CORRECTION : Le nom de la méthode doit être getDeathListener
    public DeathListener getDeathListener() { return deathListener; }

    public GameTask getGameTask() { return gameTask; }
    public void setGameTask(GameTask gameTask) { this.gameTask = gameTask; }

    public static Main getInstance() { return instance; }
}