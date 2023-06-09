package me.matistan05.minecraftblockshuffle.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static me.matistan05.minecraftblockshuffle.commands.BlockShuffleCommand.players;

public class BlockShuffleCompleter implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> list = new ArrayList<>();
        if(args.length == 1) {
            if(startsWith("add", args[0])) {
                list.add("add");
            }
            if(startsWith("remove", args[0])) {
                list.add("remove");
            }
            if(startsWith("start", args[0])) {
                list.add("start");
            }
            if(startsWith("reset", args[0])) {
                list.add("reset");
            }
            if(startsWith("list", args[0])) {
                list.add("list");
            }
            if(startsWith("help", args[0])) {
                list.add("help");
            }
        } else if(args.length > 1 && (args[0].equals("add") || args[0].equals("remove"))) {
            List<String> notForTab = new LinkedList<>();
            for(int i = 1; i < args.length - 1; i++) {
                Player player = Bukkit.getPlayerExact(args[i]);
                if(player == null) {continue;}
                notForTab.add(player.getName());
            }
            List<Player> tabPlayers = new LinkedList<>();
            if(args[0].equals("add")) {
                tabPlayers = new LinkedList<>(Bukkit.getOnlinePlayers());
                for(String s : players) {
                    Player player = Bukkit.getPlayerExact(s);
                    if(player == null) {continue;}
                    notForTab.add(player.getName());
                }
            } else {
                for(String s : players) {
                    Player player = Bukkit.getPlayerExact(s);
                    if(player == null) {continue;}
                    tabPlayers.add(player);
                }
            }
            for (String argument : notForTab) {
                tabPlayers.remove(Bukkit.getPlayerExact(argument));
            }
            for (Player player : tabPlayers) {
                if (startsWith(player.getName(), args[args.length - 1])) {
                    list.add(player.getName());
                }
            }
        }
        return list;
    }
    private boolean startsWith(String a, String b) {
        if(b.length() <= a.length()) {
            for(int i = 0; i < b.length(); i++) {
                if(b.toLowerCase().charAt(i) != a.toLowerCase().charAt(i)) {
                    return false;
                }
            }
        } else {
            return false;
        }
        return true;
    }
}