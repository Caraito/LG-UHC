package fr.caraito.lguhc.commands;

import fr.caraito.lguhc.Main;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandConfig implements CommandExecutor {
    private final Main main;
    public CommandConfig(Main main) { this.main = main; }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player) || !sender.isOp()) return true;
        openConfigGUI((Player) sender);
        return true;
    }

    public void openConfigGUI(Player player) {
        Inventory gui = Bukkit.createInventory(null, 27, "§8Configuration LG UHC");
        FileConfiguration config = main.getConfig();

        // 1. Mode Meetup
        boolean meetup = config.getBoolean("meetup", false);
        gui.setItem(0, createItem(meetup ? Material.EMERALD_BLOCK : Material.REDSTONE_BLOCK,
                "§eMode Meetup : " + (meetup ? "§aActivé" : "§cDésactivé"),
                "§7Cliquez pour changer", "", "§fStuff auto & Rôles à 5min"));

        // 2. Révélation des rôles
        boolean revealRoles = config.getBoolean("reveal_roles", false);
        gui.setItem(1, createItem(revealRoles ? Material.EYE_OF_ENDER : Material.ENDER_PEARL,
                "§eRévélation des rôles : " + (revealRoles ? "§aActivé" : "§cDésactivé"),
                "§7Cliquez pour changer", "", "§fAffiche les rôles présents à 20min"));

        // 3. Temps d'attribution des rôles (Normal)
        int roleTimeNormal = config.getInt("role_time_normal", 1200);
        gui.setItem(3, createItem(Material.WATCH, "§eTemps Rôles (Normal) : §b" + (roleTimeNormal / 60) + " min",
                "§7G: +1m | D: -1m | S: +5m", "§fTemps avant l'attribution des rôles", "§fquand le mode Meetup est désactivé."));

        // 4. Temps d'attribution des rôles (Meetup)
        int roleTimeMeetup = config.getInt("role_time_meetup", 300);
        gui.setItem(4, createItem(Material.WATCH, "§eTemps Rôles (Meetup) : §b" + (roleTimeMeetup / 60) + " min",
                "§7G: +1m | D: -1m | S: +5m", "§fTemps avant l'attribution des rôles", "§fquand le mode Meetup est activé."));

        // 5. Durée d'un épisode
        int episodeDuration = config.getInt("episode_duration", 1200);
        gui.setItem(5, createItem(Material.PAPER, "§eDurée Épisode : §b" + (episodeDuration / 60) + " min",
                "§7G: +1m | D: -1m | S: +5m", "§fFréquence des annonces d'épisodes", "§fet du reset des pouvoirs (Salvateur)."));

        // 6. Limite de diamants
        int diamondLimit = config.getInt("diamond_limit", 15);
        gui.setItem(10, createItem(Material.DIAMOND, "§eLimite Diamants : §b" + diamondLimit,
                "§7G: +1 | D: -1 | S: +5", "§fNombre max de diamants minables", "§fpar joueur."));

        // 7. Taux de pommes
        int appleRate = config.getInt("apple_rate", 5);
        gui.setItem(11, createItem(Material.APPLE, "§eTaux de Pommes : §b" + appleRate + "%",
                "§7G: +1% | D: -1% | S: +5%", "§fChance d'obtenir une pomme", "§fen cassant des feuilles."));

        // 8. Multiplicateur de minerais
        int oreMultiplier = config.getInt("ore_multiplier", 2);
        gui.setItem(12, createItem(Material.IRON_INGOT, "§eMultiplicateur Minerais : §b" + oreMultiplier,
                "§7G: +1 | D: -1", "§fNombre de lingots obtenus", "§fpar minerai cassé."));

        // 9. Réduction Bordure
        int borderShrink = config.getInt("border_shrink_per_minute", 0);
        gui.setItem(13, createItem(Material.BARRIER, "§eBordure : §b-" + borderShrink + " m/min",
                "§7G: +1 | D: -1 | S: +10", "§fRéduction de la taille de la bordure", "§fchaque minute."));

        player.openInventory(gui);
    }

    private ItemStack createItem(Material material, String name, String... lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(Arrays.asList(lore));
        item.setItemMeta(meta);
        return item;
    }
}