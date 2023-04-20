package me.matistan05.minecraftblockshuffle.listeners;

import me.matistan05.minecraftblockshuffle.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import static me.matistan05.minecraftblockshuffle.commands.BlockShuffleCommand.*;

public class MoveListener implements Listener {
    private final Main main;
    public MoveListener(Main main) {
        this.main = main;
        Bukkit.getPluginManager().registerEvents(this, main);
    }
    @EventHandler
    public void moveEvent(PlayerMoveEvent e) {
        if(inGame) {
            for (int i = 0; i < players.size(); i++) {
                if (e.getTo().clone().subtract(0, 1, 0).getBlock().getType().equals(blocks.get(i)) && players.get(i).equals(e.getPlayer().getName()) && !finished.get(i)) {
                    playersMessage(ChatColor.GOLD + e.getPlayer().getName() + " found their block!");
                    finished.set(i, true);
                    if(main.getConfig().getInt("gameMode") == 1) {
                        points.set(i, points.get(i) + 1);
                        if(points.get(i) == requiredPoints) {
                            if(playersWith(requiredPoints - 1) == 0) {
                                playersMessage(ChatColor.DARK_AQUA + "Scoreboard:");
                                for(int j = 0; j < players.size(); j++) {
                                    playersMessage(ChatColor.DARK_AQUA + players.get(j) + " " + points.get(j));
                                }
                                playersMessage(ChatColor.GOLD + "" + ChatColor.MAGIC + "IR" + ChatColor.GOLD + players.get(i) + " won! Their score: " + requiredPoints + " point" +
                                        (requiredPoints == 1 ? "" : "s") + " in " + round + " round" + (round == 1 ? "" : "s") + ChatColor.MAGIC + "IR");
                                reset();
                            }
                        }
                    }
                    break;
                }
            }
        }
    }
}