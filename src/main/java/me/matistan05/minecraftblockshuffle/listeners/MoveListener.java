package me.matistan05.minecraftblockshuffle.listeners;

import me.matistan05.minecraftblockshuffle.BlockShufflePlayer;
import me.matistan05.minecraftblockshuffle.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import static me.matistan05.minecraftblockshuffle.commands.BlockShuffleCommand.*;

public class MoveListener implements Listener {
    public MoveListener(Main main) {
        Bukkit.getPluginManager().registerEvents(this, main);
    }
    @EventHandler
    public void moveEvent(PlayerMoveEvent e) {
        if(inGame) {
            for (BlockShufflePlayer player : players) {
                if (e.getTo().clone().subtract(0, 1, 0).getBlock().getType().equals(player.getBlock()) && player.getName().equals(e.getPlayer().getName())) {
                    playersMessage(ChatColor.GOLD + e.getPlayer().getName() + " found their block!");
                    player.setStood(true);
                    break;
                }
            }
        }
    }
}