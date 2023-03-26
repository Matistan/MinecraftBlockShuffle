package me.matistan05.minecraftblockshuffle.listeners;

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
        if(inGame && players.contains(e.getPlayer().getName())) {
            if(e.getTo().clone().subtract(0, 1, 0).getBlock().getType().equals(blocks[players.indexOf(e.getPlayer().getName())]) && !stood.get(players.indexOf(e.getPlayer().getName()))) {
                playersMessage(ChatColor.GOLD + e.getPlayer().getName() + " found their block!");
                stood.set(players.indexOf(e.getPlayer().getName()), true);
            }
        }
    }
}