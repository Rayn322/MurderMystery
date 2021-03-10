package com.ryan.murdermystery;

import com.ryan.murdermystery.commands.Commands;
import com.ryan.murdermystery.commands.MMTabCompleter;
import com.ryan.murdermystery.listeners.Listeners;
import com.ryan.murdermystery.scoreboards.HideNametags;
import com.ryan.murdermystery.scoreboards.SidebarDisplay;
import com.ryan.murdermystery.utils.MMUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.util.Ticks;
import org.apache.commons.lang.StringUtils;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public final class MurderMystery extends JavaPlugin {
    
    public static UUID detective;
    public static UUID murderer;
    public static World world;
    public static boolean isPlaying;
    private static MurderMystery plugin;
    private static final ArrayList<Location> spawnpoints = new ArrayList<>();
    private static final Scoreboard emptyScoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
    
    public static MurderMystery getPlugin() {
        return plugin;
    }
    
    @Override
    public void onEnable() {
        
        getServer().getPluginManager().registerEvents(new Listeners(), this);
        getCommand("mm").setExecutor(new Commands());
        getCommand("mm").setTabCompleter(new MMTabCompleter());
        
        SidebarDisplay.createSidebar();
        HideNametags.createHideNametagsTeam();
    
        plugin = this;
    }
    
    @Override
    public void onDisable() {
        Team team = Bukkit.getScoreboardManager().getMainScoreboard().getTeam("HiddenNametag");
        
        if (team != null) team.unregister();
    }
    
    public static void startGame() {
        List<Player> players = world.getPlayers();
        Random random = new Random();
        int randomNumber;
    
        Gameplay.alivePlayers.addAll(players);
        
        // random detective
        randomNumber = random.nextInt(players.size());
        detective = players.get(randomNumber).getUniqueId();
        
        // random murderer that isn't also detective
        while (true) {
            randomNumber = random.nextInt(players.size());
            UUID murdererPlayer = players.get(randomNumber).getUniqueId();
            
            if (murdererPlayer != detective) {
                murderer = murdererPlayer;
                break;
            }
        }
    
        for (Player player : players) {
            SidebarDisplay.updateSidebar();
            player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
            player.setGameMode(GameMode.ADVENTURE);
            System.out.println("displaying sidebar");
        }
        
        teleportPlayers();
        showRoles(players);
        giveItems();
//        HideNametags.hideNametags();
    }
    
    public static void endGame(boolean innocentsWon, boolean force) {
        isPlaying = false;
        Gameplay.alivePlayers.clear();
//        HideNametags.showNametags();
    
        if (innocentsWon && !force) {
            ArrayList<Component> winMessage = new ArrayList<>();
            winMessage.add(Component.text("--------------------------------------------------", TextColor.color(230, 211, 41)));
            winMessage.add(Component.text(""));
            winMessage.add(Component.text(StringUtils.center("INNOCENTS WIN!", 50), TextColor.color(4, 219, 0)));
            winMessage.add(Component.text(""));
            winMessage.add(Component.text(StringUtils.center("Detective: " + Bukkit.getPlayer(detective).getName(), 50), TextColor.color(94, 235, 255)));
            winMessage.add(Component.text(StringUtils.center("Murderer: " + Bukkit.getPlayer(murderer).getName(), 50), TextColor.color(184, 42, 42)));
            winMessage.add(Component.text(""));
            winMessage.add(Component.text("--------------------------------------------------", TextColor.color(230, 211, 41)));
            
            for (Component message : winMessage) {
                world.sendMessage(message);
            }
            
            Component titleComponent = Component.text("INNOCENTS WIN!", TextColor.color(4, 219, 0));
            Component subtitleComponent = Component.text("");
            Title.Times time = Title.Times.of(Ticks.duration(0), Ticks.duration(100), Ticks.duration(0));
            Title title = Title.title(titleComponent, subtitleComponent, time);
            world.showTitle(title);
            
        } else if (!force) {
            ArrayList<Component> winMessage = new ArrayList<>();
            winMessage.add(Component.text("--------------------------------------------------", TextColor.color(230, 211, 41)));
            winMessage.add(Component.text(""));
            winMessage.add(Component.text(StringUtils.center("MURDERER WINS!", 50), TextColor.color(184, 42, 42)));
            winMessage.add(Component.text(""));
            winMessage.add(Component.text(StringUtils.center("Detective: " + Bukkit.getPlayer(detective).getName(), 50), TextColor.color(94, 235, 255)));
            winMessage.add(Component.text(StringUtils.center("Murderer: " + Bukkit.getPlayer(murderer).getName(), 50), TextColor.color(184, 42, 42)));
            winMessage.add(Component.text(""));
            winMessage.add(Component.text("--------------------------------------------------", TextColor.color(230, 211, 41)));
            
            for (Component message : winMessage) {
                world.sendMessage(message);
            }
            
            Component titleComponent = Component.text("MURDERER WINS!", TextColor.color(184, 42, 42));
            Component subtitleComponent = Component.text("");
            Title.Times time = Title.Times.of(Ticks.duration(0), Ticks.duration(100), Ticks.duration(0));
            Title title = Title.title(titleComponent, subtitleComponent, time);
            world.showTitle(title);
        }
        
        detective = null;
        murderer = null;
        if (force) {
            Bukkit.getScheduler().runTaskLater(getPlugin(), MurderMystery::returnToLobby, 0);
        } else {
            Bukkit.getScheduler().runTaskLater(getPlugin(), MurderMystery::returnToLobby, 100);
        }
        
        for (Entity entity : world.getEntities()) {
            if (entity.getType() == EntityType.DROPPED_ITEM) {
                entity.remove();
            }
        }
        
    }
    
    public static void showRoles(List<Player> players) {
        
        for (Player player : players) {
            switch (MMUtils.getRole(player)) {
                case INNOCENT:
                    player.sendTitle(ChatColor.GREEN + "INNOCENT",
                            ChatColor.WHITE + "Stay alive for as long as possible.",
                            0,
                            60,
                            10);
                    break;
                
                case DETECTIVE:
                    player.sendTitle(ChatColor.AQUA + "DETECTIVE",
                            ChatColor.WHITE + "Find and kill the murderer.",
                            0,
                            60,
                            10);
                    break;
                
                case MURDERER:
                    player.sendTitle(ChatColor.RED + "MURDERER",
                            ChatColor.WHITE + "Kill everyone but don't get caught by the detective.",
                            0,
                            60,
                            10);
                    break;
            }
        }
    }
    
    public static void giveItems() {
        Player murdererPlayer = Bukkit.getPlayer(murderer);
        Player detectivePlayer = Bukkit.getPlayer(detective);
        
        murdererPlayer.getInventory().setItem(1, new ItemStack(Material.IRON_SWORD));
        detectivePlayer.getInventory().setItem(1, new ItemStack(Material.BOW));
        murdererPlayer.getInventory().setHeldItemSlot(0);
        detectivePlayer.getInventory().setHeldItemSlot(0);
        
        Gameplay.giveArrows(detectivePlayer);
    }
    
    public static void returnToLobby() {
        for (Player player : world.getPlayers()) {
            player.teleport(new Location(world, -116, 4, 282));
            player.setGameMode(GameMode.ADVENTURE);
            player.getInventory().clear();
            player.setScoreboard(emptyScoreboard);
        }
    }
    
    public static void teleportPlayers() {
        Random random = new Random();
        
        // reset the list
        spawnpoints.clear();
        spawnpoints.add(new Location(world, -82, 11, 279));
        spawnpoints.add(new Location(world, -77, 10, 293));
        spawnpoints.add(new Location(world, -90, 13, 302));
        spawnpoints.add(new Location(world, -83, 12, 320));
        spawnpoints.add(new Location(world, -82, 5, 297));
        
        for (Player player : world.getPlayers()) {
            
            // if theres more players than spawns, don't try to keep one player per spawn
            if (world.getPlayers().size() > spawnpoints.size()) {
                player.teleport(spawnpoints.get(random.nextInt(spawnpoints.size())));
            } else {
                // remove spawnpoints from the list after being used
                int i = random.nextInt(spawnpoints.size());
                player.teleport(spawnpoints.get(i));
                spawnpoints.remove(i);
            }
        }
    }
}
