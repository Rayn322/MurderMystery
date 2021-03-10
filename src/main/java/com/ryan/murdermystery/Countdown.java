package com.ryan.murdermystery;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;

public class Countdown {
    
    public static void startCountdown() {
        Component fiveSeconds = Component.text("Starting in 5 seconds!", TextColor.color(61, 175, 224));
        Component fourSeconds = Component.text("Starting in 4 seconds!", TextColor.color(61, 175, 224));
        Component threeSeconds = Component.text("Starting in 3 seconds!", TextColor.color(61, 175, 224));
        Component twoSeconds = Component.text("Starting in 2 seconds!", TextColor.color(61, 175, 224));
        Component oneSecond = Component.text("Starting in 1 second!", TextColor.color(61, 175, 224));
    
        MurderMystery.isPlaying = true;
        MurderMystery.world.sendMessage(fiveSeconds);
        Bukkit.getScheduler().runTaskLater(MurderMystery.getPlugin(), () -> MurderMystery.world.sendMessage(fourSeconds), 20);
        Bukkit.getScheduler().runTaskLater(MurderMystery.getPlugin(), () -> MurderMystery.world.sendMessage(threeSeconds), 40);
        Bukkit.getScheduler().runTaskLater(MurderMystery.getPlugin(), () -> MurderMystery.world.sendMessage(twoSeconds), 60);
        Bukkit.getScheduler().runTaskLater(MurderMystery.getPlugin(), () -> MurderMystery.world.sendMessage(oneSecond), 80);
        Bukkit.getScheduler().runTaskLater(MurderMystery.getPlugin(), MurderMystery::startGame, 100);
    
    }
    
}
