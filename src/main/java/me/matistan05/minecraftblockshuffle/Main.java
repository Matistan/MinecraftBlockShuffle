package me.matistan05.minecraftblockshuffle;

import me.matistan05.minecraftblockshuffle.commands.BlockShuffleCommand;
import me.matistan05.minecraftblockshuffle.commands.BlockShuffleCompleter;
import me.matistan05.minecraftblockshuffle.listeners.DamageListener;
import me.matistan05.minecraftblockshuffle.listeners.MoveListener;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileWriter;

import static me.matistan05.minecraftblockshuffle.commands.BlockShuffleCommand.inGame;

public final class Main extends JavaPlugin {
    public static FileConfiguration blocks, banned;
    public static File bannedFile;
    @Override
    public void onEnable() {
        saveDefaultConfig();
        getServer().getPluginCommand("blockshuffle").setExecutor(new BlockShuffleCommand(this));
        getCommand("blockshuffle").setTabCompleter(new BlockShuffleCompleter(this));
        new MoveListener(this);
        new DamageListener(this);
        saveResource("banned.yml", false);
        bannedFile = new File(getDataFolder(), "banned.yml");
        banned = YamlConfiguration.loadConfiguration(bannedFile);
        banned.options().copyDefaults(true);
        saveResource("blocks.yml", false);
        File blocksFile = new File(getDataFolder(), "blocks.yml");
        FileWriter writer;
        try {
            String version = Bukkit.getBukkitVersion();
            writer = new FileWriter(blocksFile);
            writer.write("# Below is the list of blocks that can be selected in a blockshuffle game\n" +
                    "# You are playing on a " + version + " server, and these are the blocks that can be selected\n" +
                    "# Don't modify this file, this is only for you to know how blocks are named\n" +
                    "# If you want to ban certain blocks, go to banned.yml and add them exactly as they are written here\n" +
                    "# If you changed this file accidentally, and want a default one again, just reload the server\n" +
                    "\n" +
                    "blocks:\n");
            for(Material material : Material.values()) {
                if (material.isBlock()) writer.write("  - " + material.name() + "\n");
            }
            writer.close();
        } catch (Exception e) {
            System.out.println("An error occurred while creating the blocks.yml file.");
        }
        blocks = YamlConfiguration.loadConfiguration(blocksFile);
        blocks.options().copyDefaults(true);
        System.out.println("*********************************************************\n" +
                "Thank you for using this plugin! <3\n" +
                "Author: Matistan\n" +
                "If you enjoy this plugin, please rate it on spigotmc.org:\n" +
                "https://www.spigotmc.org/resources/block-shuffle.109009/\n" +
                "*********************************************************");
    }

    @Override
    public void onDisable() {
        if(inGame) {
            BlockShuffleCommand.reset();
        }
    }
}