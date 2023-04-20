package me.matistan05.minecraftblockshuffle;

import me.matistan05.minecraftblockshuffle.commands.BlockShuffleCommand;
import me.matistan05.minecraftblockshuffle.commands.BlockShuffleCompleter;
import me.matistan05.minecraftblockshuffle.listeners.DamageListener;
import me.matistan05.minecraftblockshuffle.listeners.MoveListener;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class Main extends JavaPlugin {
    public static FileConfiguration blocks, disabled;
    @Override
    public void onEnable() {
        saveDefaultConfig();
        getServer().getPluginCommand("blockshuffle").setExecutor(new BlockShuffleCommand(this));
        getCommand("blockshuffle").setTabCompleter(new BlockShuffleCompleter());
        new MoveListener(this);
        new DamageListener(this);
        blocks = YamlConfiguration.loadConfiguration(new File(getDataFolder(), "blocks.yml"));
        blocks.options().copyDefaults(true);
        saveResource("blocks.yml", false);
        disabled = YamlConfiguration.loadConfiguration(new File(getDataFolder(), "disabled.yml"));
        disabled.options().copyDefaults(true);
        saveResource("disabled.yml", false);
        System.out.println("*********************************************************\n" +
                "Thank you for using this plugin! <3\n" +
                "Author: Matistan\n" +
                "If you enjoy this plugin, please rate it on spigotmc.org:\n" +
                "https://www.spigotmc.org/resources/block-shuffle.109009/\n" +
                "*********************************************************");
    }
}