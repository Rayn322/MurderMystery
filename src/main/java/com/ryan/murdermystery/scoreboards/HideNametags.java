package com.ryan.murdermystery.scoreboards;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

public class HideNametags {
    
    private static final ScoreboardManager manager = Bukkit.getScoreboardManager();
    private static final Scoreboard board = manager.getMainScoreboard();
    private static final Team team = board.registerNewTeam("HiddenNametag");
    
    public static void joinTeam(Player player) {
        team.addEntry(player.getName());
    }
    
    public static void leaveTeam(Player player) {
        team.removeEntry(player.getName());
    }
    
    public static void createHideNametagsTeam() {
        team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);
    }
}
