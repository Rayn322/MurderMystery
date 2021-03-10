package com.ryan.murdermystery.scoreboards;

import com.ryan.murdermystery.Gameplay;
import com.ryan.murdermystery.utils.MMUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

public class SidebarDisplay {
    
    private static final ScoreboardManager manager = Bukkit.getScoreboardManager();
    private static final Scoreboard board = manager.getMainScoreboard();
    private static Team innocentCounter;
    
    public static void createSidebar() {
        Component displayName = Component.text("MURDER MYSTERY", TextColor.color(255, 245, 48));
        Objective sidebar = board.registerNewObjective("Sidebar", "dummy", displayName);
        sidebar.setDisplaySlot(DisplaySlot.SIDEBAR);
        System.out.println("created sidebar");
        
        innocentCounter = board.registerNewTeam("InnocentCounter");
        innocentCounter.addEntry("Innocents Alive: ");
        sidebar.getScore("").setScore(1);
        sidebar.getScore("Innocents Alive: ").setScore(0);
        
    }
    
    public static void updateSidebar() {
        
        // innocents alive is everyone minus the murderer.
        int innocentsAlive = 0;
        
        for (Player player : Gameplay.alivePlayers) {
            if (!MMUtils.isMurderer(player)) {
                innocentsAlive++;
            }
        }
        
        Component innocentsText = Component.text(Integer.toString(innocentsAlive), TextColor.color(66, 182, 245));
        innocentCounter.suffix(innocentsText);
        System.out.println("Alive players");
    }
    
    
    // can be removed
    public static Scoreboard getSidebar() {
        return board;
    }
}
