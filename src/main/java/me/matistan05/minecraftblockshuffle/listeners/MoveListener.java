package me.matistan05.minecraftblockshuffle.listeners;

import me.matistan05.minecraftblockshuffle.Main;
import me.matistan05.minecraftblockshuffle.classes.BlockShufflePlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
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
        if(!inGame) return;
        Player player = e.getPlayer();
        if (!isPlayer(player.getName())) return;
        BlockShufflePlayer playerObject = getPlayer(player.getName());
        if (e.getTo().clone().subtract(0, 1, 0).getBlock().getType().equals(playerObject.getBlock()) && !playerObject.foundHisBlock()) {
            playersMessage(ChatColor.GOLD + e.getPlayer().getName() + " found their block!");
            playerObject.setFoundHisBlock(true);
            if (main.getConfig().getInt("gameMode") == 1) {
                playerObject.setPoints(playerObject.getPoints() + 1);
            }
        }
    }
}