package me.matistan05.minecraftblockshuffle.listeners;

import me.matistan05.minecraftblockshuffle.Main;
import me.matistan05.minecraftblockshuffle.classes.BlockShufflePlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import static me.matistan05.minecraftblockshuffle.commands.BlockShuffleCommand.*;

public class JoinListener implements Listener {
    private final Main main;
    public JoinListener(Main main) {
        this.main = main;
        Bukkit.getPluginManager().registerEvents(this, main);
    }
    @EventHandler
    public void onJoinEvent(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        if (inGame && isPlayer(player.getName()) && main.getConfig().getBoolean("scoreboard")) {
            BlockShufflePlayer playerObject = getPlayer(player.getName());
            player.setScoreboard(playerObject.getScoreboard());
        }
    }
}