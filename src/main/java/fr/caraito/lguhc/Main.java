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
    private GameTask gameTask; // Ajout pour stocker la tâche

    private static Main instance;

    @Override
    public void onEnable() {
        instance = this;

        setState(GState.LOBBY);
        this.worldManager = new WorldManager();
        this.roleManager = new RoleManager();
        this.sbManager = new ScoreboardManager();

        worldManager.deleteAllWorldFolders();

        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        getServer().getPluginManager().registerEvents(new DeathListener(this), this);

        getCommand("lgstart").setExecutor(new CommandStart(this));
        getCommand("lgworld").setExecutor(new CommandWorld(this));
        getCommand("lgspawn").setExecutor(new CommandSpawn());
        getCommand("lgstop").setExecutor(new CommandStop(this));

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
        if (worldManager != null) worldManager.deleteAllWorldFolders();
    }

    public void setState(GState state) {
        this.gameState = state;
        Bukkit.broadcastMessage("§8[§6LG UHC§8] §7État : §e" + state.name());
    }

    public boolean isState(GState state) { return this.gameState == state; }
    public WorldManager getWorldManager() { return worldManager; }
    public RoleManager getRoleManager() { return roleManager; }
    public static Main getInstance() { return instance; }

    // Getters et Setters pour la Task
    public GameTask getGameTask() { return gameTask; }
    public void setGameTask(GameTask gameTask) { this.gameTask = gameTask; }
}