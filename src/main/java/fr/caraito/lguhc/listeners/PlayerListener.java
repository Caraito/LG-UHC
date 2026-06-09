package fr.caraito.lguhc.listeners;

import fr.caraito.lguhc.Main;
import fr.caraito.lguhc.commands.CommandConfig;
import fr.caraito.lguhc.commands.CommandLoups;
import fr.caraito.lguhc.enums.GState;
import fr.caraito.lguhc.roles.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class PlayerListener implements Listener {

    private final Main main;
    private final Map<UUID, Integer> diamondsMined = new HashMap<>();

    public PlayerListener(Main main) {
        this.main = main;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        event.setJoinMessage(ChatColor.GRAY + "[" + ChatColor.GREEN + "+" + ChatColor.GRAY + "] " + ChatColor.YELLOW + event.getPlayer().getName());

        if (main.isState(GState.GAME)) {
            Player player = event.getPlayer();
            player.setGameMode(org.bukkit.GameMode.SPECTATOR);
            player.sendMessage("§cLa partie est déjà en cours. Vous êtes en mode spectateur.");
        }
    }

    @EventHandler
    public void onCraft(CraftItemEvent event) {
        ItemStack item = event.getCurrentItem();
        if (item == null || item.getType() == Material.AIR) return;

        String name = item.getType().name();
        if (name.contains("PICKAXE") || name.contains("AXE") || name.contains("SPADE") || name.contains("SWORD")) {
            item.addUnsafeEnchantment(Enchantment.DIG_SPEED, 3);
            item.addUnsafeEnchantment(Enchantment.DURABILITY, 3);
            if (name.contains("SWORD")) item.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 3);
        }
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        if (main.isState(GState.LOBBY)) {
            event.setCancelled(true);
            return;
        }
        if (!main.isState(GState.GAME)) return;

        Player player = event.getPlayer();
        org.bukkit.block.Block block = event.getBlock();

        if (block.getType() == Material.DIAMOND_ORE) {
            int count = diamondsMined.getOrDefault(player.getUniqueId(), 0);
            int limit = main.getConfig().getInt("diamond_limit", 15);
            if (count >= limit) {
                event.setCancelled(true);
                player.sendMessage("§c§l[Limite] §fVous avez atteint la limite de §b" + limit + " diamants §f!");
                return;
            }
            diamondsMined.put(player.getUniqueId(), count + 1);
        }

        if (block.getType() == Material.LOG || block.getType() == Material.LOG_2) {
            int appleRate = main.getConfig().getInt("apple_rate", 5);
            for (int i = 0; i <= 20; i++) {
                org.bukkit.block.Block b = block.getRelative(0, i, 0);
                if (b.getType() == Material.LOG || b.getType() == Material.LOG_2) {
                    b.breakNaturally();
                    if (new Random().nextInt(100) < appleRate)
                        b.getWorld().dropItemNaturally(b.getLocation(), new ItemStack(Material.APPLE));
                } else break;
            }
        }

        int multiplier = main.getConfig().getInt("ore_multiplier", 2);
        if (block.getType() == Material.IRON_ORE) {
            block.setType(Material.AIR);
            block.getWorld().dropItemNaturally(block.getLocation(), new ItemStack(Material.IRON_INGOT, multiplier));
        } else if (block.getType() == Material.GOLD_ORE) {
            block.setType(Material.AIR);
            block.getWorld().dropItemNaturally(block.getLocation(), new ItemStack(Material.GOLD_INGOT, multiplier));
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (main.isState(GState.LOBBY)) {
            event.setCancelled(true);
            return;
        }

        if (event instanceof org.bukkit.event.entity.EntityDamageByEntityEvent) {
            org.bukkit.event.entity.EntityDamageByEntityEvent e = (org.bukkit.event.entity.EntityDamageByEntityEvent) event;
            if (e.getDamager() instanceof Player) {
                Player damager = (Player) e.getDamager();
                LGRole role = main.getRoleManager().getRole(damager.getUniqueId());
                if (role instanceof RoleAlpha) {
                    World world = damager.getWorld();
                    if (world.getTime() >= 13000 && world.getTime() <= 23000) {
                        e.setDamage(e.getDamage() + 2.0); // +1 coeur de dégâts brut
                    }
                }
            }
        }
    }

    @EventHandler
    public void onFood(FoodLevelChangeEvent event) {
        if (main.isState(GState.LOBBY)) event.setCancelled(true);
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        if (main.isState(GState.GAME)) event.setCancelled(true);
    }

    @EventHandler
    public void onSalvateurInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        // Sécurité : Item nul ou pas de meta
        if (item == null || item.getType() == Material.AIR || !item.hasItemMeta()) return;

        // Sécurité : L'item doit avoir un nom pour être comparé
        if (!item.getItemMeta().hasDisplayName() || !item.getItemMeta().getDisplayName().contains("Bouclier de Fortune")) return;

        event.setCancelled(true);

        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            // Sécurité : Vérifier si la partie a bien commencé (Task non nulle)
            if (main.getGameTask() == null) return;

            LGRole role = main.getRoleManager().getRole(player.getUniqueId());
            if (!(role instanceof RoleSalvateur)) return;

            RoleSalvateur salvateur = (RoleSalvateur) role;

            boolean isMeetup = main.getConfig().getBoolean("meetup", false);
            int roleTime = isMeetup ? 300 : 1200;
            int totalSeconds = main.getGameTask().getSeconds();

            if (totalSeconds < roleTime) return;

            int secondsInEpisode = (totalSeconds - roleTime) % 1200;

            if (secondsInEpisode > 300) {
                player.sendMessage("§cErreur : Vous ne pouvez utiliser votre pouvoir que durant les 5 premières minutes de l'épisode.");
                return;
            }

            if (salvateur.isUsedThisEpisode()) {
                player.sendMessage("§cErreur : Vous avez déjà utilisé votre pouvoir pour cet épisode.");
                return;
            }

            openSalvateurGUI(player);
        }
    }

    private void openSalvateurGUI(Player player) {
        Inventory gui = Bukkit.createInventory(null, 27, "§8Protection du Salvateur");
        for (Player target : Bukkit.getOnlinePlayers()) {
            ItemStack head = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
            SkullMeta meta = (SkullMeta) head.getItemMeta();
            meta.setOwner(target.getName());
            meta.setDisplayName("§e" + target.getName());
            head.setItemMeta(meta);
            gui.addItem(head);
        }
        player.openInventory(gui);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        String title = event.getView().getTitle();

        if (title.equals("§8Configuration LG UHC")) {
            event.setCancelled(true);
            int slot = event.getRawSlot();
            boolean isLeft = event.isLeftClick();
            boolean isShift = event.isShiftClick();

            int delta = isShift ? 5 : 1;
            if (!isLeft) delta = -delta;

            switch (slot) {
                case 0:
                    main.getConfig().set("meetup", !main.getConfig().getBoolean("meetup", false));
                    break;
                case 1:
                    main.getConfig().set("reveal_roles", !main.getConfig().getBoolean("reveal_roles", false));
                    break;
                case 3:
                    int rTimeN = main.getConfig().getInt("role_time_normal", 1200);
                    main.getConfig().set("role_time_normal", Math.max(60, rTimeN + (delta * 60)));
                    break;
                case 4:
                    int rTimeM = main.getConfig().getInt("role_time_meetup", 300);
                    main.getConfig().set("role_time_meetup", Math.max(60, rTimeM + (delta * 60)));
                    break;
                case 5:
                    int epDur = main.getConfig().getInt("episode_duration", 1200);
                    main.getConfig().set("episode_duration", Math.max(60, epDur + (delta * 60)));
                    break;
                case 10:
                    int dLim = main.getConfig().getInt("diamond_limit", 15);
                    main.getConfig().set("diamond_limit", Math.max(0, dLim + delta));
                    break;
                case 11:
                    int aRate = main.getConfig().getInt("apple_rate", 5);
                    main.getConfig().set("apple_rate", Math.max(0, Math.min(100, aRate + delta)));
                    break;
                case 12:
                    int oMult = main.getConfig().getInt("ore_multiplier", 2);
                    main.getConfig().set("ore_multiplier", Math.max(1, oMult + (isLeft ? 1 : -1)));
                    break;
                case 13:
                    int bShrink = main.getConfig().getInt("border_shrink_per_minute", 0);
                    int bDelta = isShift ? 10 : 1;
                    main.getConfig().set("border_shrink_per_minute", Math.max(0, bShrink + (isLeft ? bDelta : -bDelta)));
                    break;
                default:
                    return;
            }
            main.saveConfig();
            new CommandConfig(main).openConfigGUI((Player) event.getWhoClicked());
            return;
        }

        if (title.equals("§8Vengeance du Chasseur")) {
            event.setCancelled(true);
            ItemStack clicked = event.getCurrentItem();
            if (clicked == null || clicked.getType() != Material.SKULL_ITEM || !clicked.hasItemMeta() || !clicked.getItemMeta().hasDisplayName()) return;

            String targetName = ChatColor.stripColor(clicked.getItemMeta().getDisplayName());
            Player target = Bukkit.getPlayer(targetName);
            if (target != null) {
                target.setMaxHealth(8.0); // 4 coeurs
                target.sendMessage("§c§l[Chasseur] §fLe Chasseur vous a maudit ! Votre vie maximale est réduite à 4 cœurs pendant 10 minutes.");
                Bukkit.getScheduler().runTaskLater(main, () -> {
                    if (target.isOnline()) {
                        target.setMaxHealth(20.0);
                        target.sendMessage("§a§l[Chasseur] §fLa malédiction du Chasseur est levée.");
                    }
                }, 12000L);
                event.getWhoClicked().closeInventory();
            }
            return;
        }

        if (title.equals("§8Choix du Grimage")) {
            event.setCancelled(true);
            ItemStack clicked = event.getCurrentItem();
            if (clicked == null || clicked.getType() != Material.PAPER || !clicked.hasItemMeta()) return;

            String roleDisguise = ChatColor.stripColor(clicked.getItemMeta().getDisplayName());
            LGRole role = main.getRoleManager().getRole(event.getWhoClicked().getUniqueId());
            if (role instanceof RoleGrimeur) {
                ((RoleGrimeur) role).setDisguisedRole(roleDisguise);
                event.getWhoClicked().sendMessage(ChatColor.RED + "[Grimeur] " + ChatColor.WHITE + "Vous apparaîtrez désormais comme " + ChatColor.GOLD + roleDisguise + ChatColor.WHITE + " à la Voyante.");
            }
            event.getWhoClicked().closeInventory();
            return;
        }

        if (title.equals("§8Sacrifice du Loup Blanc")) {
            event.setCancelled(true);
            ItemStack clicked = event.getCurrentItem();
            if (clicked == null || clicked.getType() != Material.SKULL_ITEM || !clicked.hasItemMeta()) return;

            String targetName = ChatColor.stripColor(clicked.getItemMeta().getDisplayName());
            Player target = Bukkit.getPlayer(targetName);
            if (target != null) {
                LGRole role = main.getRoleManager().getRole(event.getWhoClicked().getUniqueId());
                if (role instanceof RoleLoupBlanc) {
                    new CommandLoups(main).executeSacrifice((Player) event.getWhoClicked(), (RoleLoupBlanc) role, target);
                }
                event.getWhoClicked().closeInventory();
            }
            return;
        }

        if (event.getInventory() == null || !title.equals("§8Protection du Salvateur")) return;
        event.setCancelled(true);

        if (!(event.getWhoClicked() instanceof Player)) return;
        Player player = (Player) event.getWhoClicked();
        ItemStack clicked = event.getCurrentItem();

        // Sécurité : On vérifie si l'item cliqué a bien un nom
        if (clicked == null || clicked.getType() != Material.SKULL_ITEM || !clicked.hasItemMeta() || !clicked.getItemMeta().hasDisplayName()) return;

        String targetName = ChatColor.stripColor(clicked.getItemMeta().getDisplayName());
        Player target = Bukkit.getPlayer(targetName);

        if (target == null) return;

        LGRole role = main.getRoleManager().getRole(player.getUniqueId());
        if (!(role instanceof RoleSalvateur)) return;
        RoleSalvateur salvateur = (RoleSalvateur) role;

        if (target.getUniqueId().equals(salvateur.getLastProtected())) {
            player.sendMessage("§cErreur : Vous ne pouvez pas protéger la même personne deux fois d'affilée.");
            return;
        }

        target.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20*20*60, 0));
        target.sendMessage("§a§l[Salvateur] §fLe Salvateur vous a protégé ! §bRésistance I §fpendant 20 minutes.");
        player.sendMessage("§a§l[Salvateur] §fVous avez protégé §e" + target.getName() + "§f.");

        salvateur.setLastProtected(target.getUniqueId());
        salvateur.setUsedThisEpisode(true);
        player.closeInventory();
    }
}