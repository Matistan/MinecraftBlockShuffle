package me.matistan05.minecraftblockshuffle;

import me.matistan05.minecraftblockshuffle.commands.BlockShuffleCommand;
import me.matistan05.minecraftblockshuffle.commands.BlockShuffleCompleter;
import me.matistan05.minecraftblockshuffle.listeners.MoveListener;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {

    @Override
    public void onEnable() {
        saveDefaultConfig();
        getServer().getPluginCommand("blockshuffle").setExecutor(new BlockShuffleCommand(this));
        getCommand("blockshuffle").setTabCompleter(new BlockShuffleCompleter());
        new MoveListener(this);
    }
}