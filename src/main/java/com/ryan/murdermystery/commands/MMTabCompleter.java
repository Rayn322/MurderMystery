package com.ryan.murdermystery.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class MMTabCompleter implements TabCompleter {
    
    
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        
        if (args.length == 1) {
            
            ArrayList<String> parameters = new ArrayList<>();
            parameters.add("start");
            parameters.add("stop");
            
            return parameters;
        }
        
        return null;
    }
}
