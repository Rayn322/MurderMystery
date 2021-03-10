package com.ryan.murdermystery.commands;

import com.ryan.murdermystery.Countdown;
import com.ryan.murdermystery.MurderMystery;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class Commands implements CommandExecutor {
    
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        
        if (label.equalsIgnoreCase("mm")) {
            if (args[0].equalsIgnoreCase("start") && sender instanceof Player) {
                Player player = (Player) sender;
                
                if (player.getWorld().getPlayers().size() < 2) {
                    player.sendMessage("Not enough players to start!");
                } else if (MurderMystery.isPlaying) {
                    player.sendMessage(ChatColor.RED + "Cannot start a game during a game!");
                } else {
                    Countdown.startCountdown();
                }
                
            } else if (args[0].equalsIgnoreCase("stop")) {
                Component component = Component.text("The game was force stopped. You have been returned to the lobby.", TextColor.color(189, 45, 45));
                MurderMystery.world.sendMessage(component);
                MurderMystery.endGame(false, true);
            }
        }
        
        return true;
    }
}
