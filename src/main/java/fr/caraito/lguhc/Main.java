package fr.caraito.lguhc;

import fr.caraito.lguhc.commands.*;
import fr.caraito.lguhc.enums.GState;
import fr.caraito.lguhc.listeners.DeathListener;
import fr.caraito.lguhc.listeners.PlayerListener;
import fr.caraito.lguhc.managers.RoleManager;
import fr.caraito.lguhc.managers.ScoreboardManager;
import fr.caraito.lguhc.managers.WorldManager;
import fr.caraito.lguhc.tasks.GameTask;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    private static Main instance;
    private GState state;
    private RoleManager roleManager;
    private WorldManager worldManager;
    private ScoreboardManager scoreboardManager;
    private GameTask gameTask;

    @Override
    public void onEnable() {
        instance = this;
        state = GState.LOBBY;
        roleManager = new RoleManager();
        worldManager = new WorldManager();
        scoreboardManager = new ScoreboardManager();

        // Enregistrement des commandes
        getCommand("start").setExecutor(new CommandStart(this));
        getCommand("stop").setExecutor(new CommandStop(this));
        getCommand("spawn").setExecutor(new CommandSpawn(this));
        getCommand("world").setExecutor(new CommandWorld(this));
        getCommand("lgconfig").setExecutor(new CommandConfig(this));

        // Enregistrement des listeners
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        getServer().getPluginManager().registerEvents(new DeathListener(this), this);

        // Config par défaut
        getConfig().addDefault("meetup", false);
        getConfig().options().copyDefaults(true);
        saveConfig();
    }

    public static Main getInstance() { return instance; }
    public GState getState() { return state; }
    public void setState(GState state) { this.state = state; }
    public RoleManager getRoleManager() { return roleManager; }
    public WorldManager getWorldManager() { return worldManager; }
    public ScoreboardManager getScoreboardManager() { return scoreboardManager; }
    public GameTask getGameTask() { return gameTask; }
    public void setGameTask(GameTask gameTask) { this.gameTask = gameTask; }
    public boolean isState(GState state) { return this.state == state; }
}