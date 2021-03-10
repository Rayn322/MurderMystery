package com.ryan.murdermystery.utils;

import com.ryan.murdermystery.MurderMystery;
import com.ryan.murdermystery.Role;
import org.bukkit.entity.Player;

public final class MMUtils {
    
    public static boolean isInnocent(Player player) {
        return !isDetective(player) && !isMurderer(player);
    }
    
    public static boolean isDetective(Player player) {
        return MurderMystery.detective == player.getUniqueId();
    }
    
    public static boolean isMurderer(Player player) {
        return MurderMystery.murderer == player.getUniqueId();
    }
    
    public static Role getRole(Player player) {
        if (isDetective(player)) {
            return Role.DETECTIVE;
        } else if (isMurderer(player)) {
            return Role.MURDERER;
        } else {
            return Role.INNOCENT;
        }
    }
    
}
