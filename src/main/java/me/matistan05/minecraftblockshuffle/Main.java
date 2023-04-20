package me.matistan05.minecraftblockshuffle;

import me.matistan05.minecraftblockshuffle.commands.BlockShuffleCommand;
import me.matistan05.minecraftblockshuffle.commands.BlockShuffleCompleter;
import me.matistan05.minecraftblockshuffle.listeners.DamageListener;
import me.matistan05.minecraftblockshuffle.listeners.MoveListener;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public final class Main extends JavaPlugin {
    public FileConfiguration customConfig;
    @Override
    public void onEnable() {
        saveDefaultConfig();
        getServer().getPluginCommand("blockshuffle").setExecutor(new BlockShuffleCommand(this));
        getCommand("blockshuffle").setTabCompleter(new BlockShuffleCompleter());
        new MoveListener(this);
        new DamageListener(this);
        File file = new File(getDataFolder(), "customConfig.yml");
        customConfig = YamlConfiguration.loadConfiguration(file);
        customConfig.options().copyDefaults(true);
        try {
            customConfig.save(file);
        } catch (IOException ignored) {

        }
    }
}