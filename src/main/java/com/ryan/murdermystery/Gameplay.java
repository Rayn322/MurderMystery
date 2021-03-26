package com.ryan.murdermystery;

import com.ryan.murdermystery.scoreboards.SidebarDisplay;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.util.Ticks;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;

public class Gameplay {
    
    public static ArrayList<Player> alivePlayers = new ArrayList<>();
    
    public static void checkWinConditions() {
        Player murdererPlayer = Bukkit.getPlayer(MurderMystery.murderer);
        Player detectivePlayer = Bukkit.getPlayer(MurderMystery.detective);
        
        if (!alivePlayers.contains(murdererPlayer)) {
            MurderMystery.endGame(true, false);
            
        } else if (alivePlayers.size() == 1) {
            // only murderer is alive
            MurderMystery.endGame(false, false);
        }
    }
    
    public static void kill(Player player) {
        // TODO: don't drop bow if game is now over
        alivePlayers.remove(player);
    
        if (player.getInventory().contains(Material.BOW) && alivePlayers.size() != 1) {
            dropBow(player.getLocation());
        }
        
        player.setGameMode(GameMode.SPECTATOR);
        player.getInventory().clear();
        SidebarDisplay.updateSidebar();
    }
    
    public static void giveArrows(Player player) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!alivePlayers.contains(player) || !MurderMystery.isPlaying) {
                    cancel();
                }
                
                if (player.getInventory().getItem(8) == null) {
                    player.getInventory().setItem(8, new ItemStack(Material.ARROW));
                    
                } else if (player.getInventory().getItem(8).getAmount() < 2) {
                    player.getInventory().addItem(new ItemStack(Material.ARROW));
                }
            }
        }.runTaskTimer(MurderMystery.getPlugin(), 0, 40);
    }
    
    public static void dropBow(Location deathLocation) {
        MurderMystery.world.dropItem(deathLocation, new ItemStack(Material.BOW));
    
        Component titleComponent = Component.text("The bow has been dropped!", TextColor.color(7, 212, 0));
        Component subtitleComponent = Component.text("Go pick it up to become the detective.", TextColor.color(83, 201, 79));
        Title.Times time = Title.Times.of(Ticks.duration(0), Ticks.duration(60), Ticks.duration(0));
        Title title = Title.title(titleComponent, subtitleComponent, time);
        MurderMystery.world.showTitle(title);
    }
}
