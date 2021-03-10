package com.ryan.murdermystery;

import com.ryan.murdermystery.scoreboards.SidebarDisplay;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
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
        player.setGameMode(GameMode.SPECTATOR);
        alivePlayers.remove(player);
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
}
