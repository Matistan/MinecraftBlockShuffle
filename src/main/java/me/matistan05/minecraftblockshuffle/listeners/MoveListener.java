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
                    }
                    break;
                }
            }
        }
    }
}