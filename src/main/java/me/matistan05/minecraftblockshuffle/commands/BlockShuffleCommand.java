package me.matistan05.minecraftblockshuffle.commands;

import me.matistan05.minecraftblockshuffle.Main;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.*;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import static me.matistan05.minecraftblockshuffle.Main.disabled;

public class BlockShuffleCommand implements CommandExecutor {
    private static Main main;
    public static List<String> players = new LinkedList<>();
    public static List<Boolean> ops = new LinkedList<>();
    public static List<Boolean> finished = new LinkedList<>();
    public static List<Integer> points = new LinkedList<>();
    public static List<Material> blocks = new LinkedList<>();
    int startPlayers;
    public static ScoreboardManager scoreboardManager = Bukkit.getScoreboardManager();
    public static Scoreboard scoreboard;
    static Objective objective;
    public static boolean inGame = false;
    public static BukkitTask game;
    int time;
    public static int requiredPoints, round;
    static int seconds = 0;

    public BlockShuffleCommand(Main main) {
        BlockShuffleCommand.main = main;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }
        Player p = (Player) sender;
        if(args.length == 0) {
            p.sendMessage(ChatColor.RED + "You must type an argument. For help, type: /blockshuffle help");
            return true;
        }
        if (args[0].equals("help")) {
            if(!p.hasPermission("blockshuffle.help") && main.getConfig().getBoolean("usePermissions")) {
                p.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
                return true;
            }
            if (args.length != 1) {
                p.sendMessage(ChatColor.RED + "Wrong usage of this command. For help, type: /blockshuffle help");
                return true;
            }
            p.sendMessage(ChatColor.GREEN + "------- " + ChatColor.WHITE + " Minecraft BlockShuffle " + ChatColor.GREEN + "----------");
            p.sendMessage(ChatColor.BLUE + "Here is a list of blockshuffle commands:");
            p.sendMessage(ChatColor.YELLOW + "/blockshuffle add <player> <player> ... " + ChatColor.AQUA + "- adds players to a blockshuffle game");
            p.sendMessage(ChatColor.YELLOW + "/blockshuffle remove <player> <player> ... " + ChatColor.AQUA + "- removes players from a blockshuffle game");
            p.sendMessage(ChatColor.YELLOW + "/blockshuffle start " + ChatColor.AQUA + "- starts a blockshuffle game");
            p.sendMessage(ChatColor.YELLOW + "/blockshuffle reset " + ChatColor.AQUA + "- resets a blockshuffle game");
            p.sendMessage(ChatColor.YELLOW + "/blockshuffle list " + ChatColor.AQUA + "- shows a list of players in a blockshuffle game");
            p.sendMessage(ChatColor.YELLOW + "/blockshuffle help " + ChatColor.AQUA + "- shows a list of blockshuffle commands");
            p.sendMessage(ChatColor.GREEN + "----------------------------------");
            return true;
        }
        if (args[0].equals("add")) {
            if(!p.hasPermission("blockshuffle.add") && main.getConfig().getBoolean("usePermissions")) {
                p.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
                return true;
            }
            if(args.length < 2) {
                p.sendMessage(ChatColor.RED + "Wrong usage of this command. For help, type: /blockshuffle help");
                return true;
            }
            if(inGame) {
                p.sendMessage(ChatColor.RED + "The game has already started!");
                return true;
            }
            int count = 0;
            for(int i = 1; i < args.length; i++) {
                Player target = Bukkit.getPlayerExact(args[i]);
                if(target == null || players.contains(target.getName())) {continue;}
                players.add(target.getName());
                count++;
            }
            if(count > 0) {
                p.sendMessage(ChatColor.AQUA + "Successfully added " + count + " player" + (count == 1 ? "" : "s") + " to the game!");
            } else {
                p.sendMessage(ChatColor.RED + "Could not add " + (args.length == 2 ? "this player!" : "these players!"));
            }
            return true;
        }
        if (args[0].equals("remove")) {
            if(!p.hasPermission("blockshuffle.remove") && main.getConfig().getBoolean("usePermissions")) {
                p.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
                return true;
            }
            if(args.length < 2) {
                p.sendMessage(ChatColor.RED + "Wrong usage of this command. For help, type: /blockshuffle help");
                return true;
            }
            int count = 0;
            for(int i = 1; i < args.length; i++) {
                Player target = Bukkit.getPlayerExact(args[i]);
                if(target == null || !players.contains(target.getName())) {continue;}
                players.remove(target.getName());
                count++;
            }
            if(count > 0) {
                p.sendMessage(ChatColor.AQUA + "Successfully removed " + count + " player" + (count == 1 ? "" : "s") + " from the game!");
            } else {
                p.sendMessage(ChatColor.RED + "Could not remove " + (args.length == 2 ? "this player!" : "these players!"));
            }
            return true;
        }
        if (args[0].equals("start")) {
            if(!p.hasPermission("blockshuffle.start") && main.getConfig().getBoolean("usePermissions")) {
                p.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
                return true;
            }
            if(args.length != 1) {
                p.sendMessage(ChatColor.RED + "Wrong usage of this command. For help, type: /blockshuffle help");
                return true;
            }
            if(players.isEmpty()) {
                p.sendMessage(ChatColor.RED + "There are no players in the game!");
                return true;
            }
            if(inGame) {
                p.sendMessage(ChatColor.YELLOW + "The game has already started!");
                return true;
            }
            for(String v : players) {
                Player player = Bukkit.getPlayerExact(v);
                if(player == null) {
                    p.sendMessage(ChatColor.RED + "Someone from your game is offline!");
                    return true;
                }
            }
            if(main.getConfig().getBoolean("timeSetDayOnStart")) {
                p.getWorld().setTime(0);
            }
            if(main.getConfig().getBoolean("weatherClearOnStart")) {
                p.getWorld().setStorm(false);
            }
            round = 1;
            startPlayers = players.size();
            playersMessage(ChatColor.AQUA + "START!");
            if(main.getConfig().getInt("gameMode") == 1) {
                requiredPoints = (Math.max(main.getConfig().getInt("pointsToWin"), 1));
                playersMessage(ChatColor.AQUA + "First player to score " + requiredPoints +
                            " point" + (requiredPoints == 1 ? "" : "s") + ", wins!");
            }
            for(int i = 0; i < players.size(); i++) {
                Player player = Bukkit.getPlayerExact(players.get(i));
                if(player == null) {continue;}
                if(main.getConfig().getBoolean("takeAwayOps")) {
                    ops.add(player.isOp());
                    player.setOp(false);
                }
                if(main.getConfig().getBoolean("clearInventories")) {
                    player.getInventory().clear();
                }
                player.setGameMode(GameMode.SURVIVAL);
                player.setHealth(20);
                player.setFoodLevel(20);
                player.setSaturation(20);
                finished.add(false);
                points.add(0);
                if(main.getConfig().getBoolean("sameBlockForEveryone")) {
                    if(i > 0) {
                        blocks.add(blocks.get(0));
                    } else {
                        blocks.add(randomBlock());
                    }
                } else {
                    blocks.add(randomBlock());
                }
                player.sendMessage(ChatColor.DARK_GREEN + "Round "+ round + ": You must stand on " + better(blocks.get(i).name()));
            }
            inGame = true;
            time = Math.max(main.getConfig().getInt("time"), 60);
            time = Math.min(main.getConfig().getInt("time"), 3600);
            game = new BukkitRunnable() {
                @Override
                public void run() {
                    if(goodPlayers() == players.size()) {
                        seconds = time;
                    }
                    for(int i = 1; i <= 10; i++) {
                        if((seconds + i) % time == 0) {
                            playersMessage(ChatColor.LIGHT_PURPLE + String.valueOf(i) + " second" + (i == 1 ? "" : "s") + " remaining!");
                            break;
                        }
                    }
                    if(main.getConfig().getBoolean("scoreboard")) {
                        for(int i = 0; i < players.size(); i++) {
                            Player player = Bukkit.getPlayerExact(players.get(i));
                            if(player != null) {
                                scoreboard = scoreboardManager.getNewScoreboard();
                                objective = scoreboard.registerNewObjective("sb", "dummy", ChatColor.BLUE + String.valueOf(ChatColor.BOLD) + "Block Shuffle");
                                objective.setDisplaySlot(DisplaySlot.SIDEBAR);
                                Score timer = objective.getScore(ChatColor.YELLOW + "Time left: " + (time - (seconds % time)));
                                Score score2;
                                if(main.getConfig().getInt("gameMode") == 0) {
                                    score2 = objective.getScore(ChatColor.BLUE + "Players left: " + players.size());
                                } else {
                                    score2 = objective.getScore(ChatColor.BLUE + "Points: " + points.get(i) + "/" + requiredPoints);
                                }
                                Score score3 = objective.getScore(ChatColor.DARK_GREEN + "Block: " + better(blocks.get(i).name()));
                                Score score = objective.getScore(ChatColor.AQUA + "Round: " + round);
                                timer.setScore(4);
                                score3.setScore(3);
                                score2.setScore(2);
                                score.setScore(1);
                                player.setScoreboard(scoreboard);
                            }
                        }
                    }
                    if(seconds % time == 0 && seconds != 0) {
                        for (int i = 0; i < players.size(); i++) {
                            if (!finished.get(i)) {
                                playersMessage(ChatColor.DARK_RED + players.get(i) + " didn't stand on their block!");
                            }
                        }
                        if(main.getConfig().getInt("gameMode") == 0) {
                            if(goodPlayers() == 1) {
                                if(startPlayers != 1) {
                                    playersMessage(ChatColor.GOLD + String.valueOf(ChatColor.MAGIC) + "IR" + ChatColor.GOLD + players.get(finished.indexOf(true)) + " won!" + ChatColor.MAGIC + "IR");
                                    reset();
                                }
                            } else if(goodPlayers() == 0) {
                                if(startPlayers == 1) {
                                    playersMessage(ChatColor.DARK_RED + "You lost!");
                                } else {
                                    playersMessage(ChatColor.GOLD + String.valueOf(ChatColor.MAGIC) + "IR" + ChatColor.GOLD + "Draw! Winners:" + ChatColor.MAGIC + "IR");
                                    for (String player : players) {
                                        playersMessage(ChatColor.GOLD + player);
                                    }
                                }
                                reset();
                            }
                            for(int i = 0; i < players.size(); i++) {
                                if(!finished.get(i)) {
                                    if(main.getConfig().getBoolean("takeAwayOps")) {
                                        OfflinePlayer target = Bukkit.getOfflinePlayer(players.get(i));
                                        target.setOp(ops.get(i));
                                    }
                                    players.remove(i);
                                    i -= 1;
                                }
                            }
                        } else {
                            playersMessage(ChatColor.DARK_AQUA + "Scoreboard:");
                            for(int i = 0; i < players.size(); i++) {
                                playersMessage(ChatColor.DARK_AQUA + players.get(i) + " " + points.get(i));
                            }
                            if(playersWith(requiredPoints) == 1) {
                                playersMessage(ChatColor.GOLD + String.valueOf(ChatColor.MAGIC) + "IR" + ChatColor.GOLD + players.get(points.indexOf(requiredPoints)) + " won! Their score: " + requiredPoints
                                    + " point" + (requiredPoints == 1 ? "" : "s") + " in " + round + " round" + (round == 1 ? "" : "s") + ChatColor.MAGIC + "IR");
                                reset();
                            } else if(playersWith(requiredPoints) > 1) {
                                playersMessage(ChatColor.GOLD + String.valueOf(ChatColor.MAGIC) + "IR" + ChatColor.GOLD + "Draw! Winners:" + ChatColor.MAGIC + "IR");
                                for(int i = 0; i < players.size(); i++) {
                                    if(points.get(i) == requiredPoints) {
                                        playersMessage(ChatColor.GOLD + players.get(i));
                                    }
                                }
                                playersMessage(ChatColor.GOLD + "Their score: " + requiredPoints + " point" +
                                        (requiredPoints <= 1 ? "" : "s") + " in " + round + " round" + (round == 1 ? "" : "s"));
                                reset();
                            }
                        }
                        round += 1;
                        for(int i = 0; i < players.size(); i++) {
                            finished.set(i, false);
                            if(main.getConfig().getBoolean("sameBlockForEveryone")) {
                                if(i > 0) {
                                    blocks.set(i, blocks.get(0));
                                } else {
                                    blocks.set(0, randomBlock());
                                }
                            } else {
                                blocks.set(i, randomBlock());
                            }
                            Player target = Bukkit.getPlayerExact(players.get(i));
                            if(target != null) {
                                target.sendMessage(ChatColor.DARK_GREEN + "Round " + round + ": You must stand on " + better(blocks.get(i).name()));
                            }
                        }
                    }
                    seconds += 1;
                }
            }.runTaskTimer(main, 0, 20);
            return true;
        }
        if (args[0].equals("reset")) {
            if(!p.hasPermission("blockshuffle.reset") && main.getConfig().getBoolean("usePermissions")) {
                p.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
                return true;
            }
            if(args.length != 1) {
                p.sendMessage(ChatColor.RED + "Wrong usage of this command. For help, type: /blockshuffle help");
                return true;
            }
            p.sendMessage(ChatColor.AQUA + "blockshuffle game has been reseted!");
            reset();
            return true;
        }
        if (args[0].equals("list")) {
            if(!p.hasPermission("blockshuffle.list") && main.getConfig().getBoolean("usePermissions")) {
                p.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
                return true;
            }
            if(args.length != 1) {
                p.sendMessage(ChatColor.RED + "Wrong usage of this command. For help, type: /blockshuffle help");
                return true;
            }
            if(players.isEmpty()) {
                p.sendMessage(ChatColor.RED + "There is no player in your game!");
                return true;
            }
            p.sendMessage(ChatColor.GREEN + "------- " + ChatColor.WHITE + " Minecraft BlockShuffle " + ChatColor.GREEN + "----------");
            for (String player : players) {
                p.sendMessage(ChatColor.AQUA + player);
            }
            p.sendMessage(ChatColor.GREEN + "----------------------------------");
            return true;
        }
        p.sendMessage(ChatColor.RED + "Wrong argument. For help, type: /blockshuffle help");
        return true;
    }

    private int goodPlayers() {
        int a = 0;
        for (boolean f : finished) {
            if (f) {
                a++;
            }
        }
        return a;
    }
    public static int playersWith(int b) {
        int a = 0;
        for (int point : points) {
            if (point == b) {
                a++;
            }
        }
        return a;
    }

    private Material randomBlock() {
        Material material;
        List<Material> materials = new LinkedList<>(Arrays.asList(Material.values()));
        Random random = new Random();
        materials.removeIf(obj -> !obj.isBlock());
        for(String g : disabled.getStringList("disabledBlocks")) {
            materials.removeIf(obj -> obj.name().equals(g));
        }
        if(!main.getConfig().getBoolean("enableNetherBlocks")) {
            materials.removeIf(obj -> (obj.name().contains("NETHER") || obj.name().contains("CRIMSON") || obj.name().contains("WARPED") || obj.name().contains("TWISTING") ||
                    obj.name().contains("WEEPING") || obj.name().equals("SHROOMLIGHT") || obj.name().contains("BLACKSTONE") || obj.name().contains("QUARTZ") ||
                    obj.name().contains("SOUL") || obj.name().contains("BASALT") || obj.name().equals("GLOWSTONE") || obj.name().equals("REDSTONE_LAMP")));
        }
        material = materials.get(random.nextInt(materials.size()));
        return material;
    }

    public static void playersMessage(String s) {
        for (String value : players) {
            Player player = Bukkit.getPlayerExact(value);
            if(player != null) {
                player.sendMessage(s);
            }
        }
    }
    public static void reset() {
        if(inGame) {
            inGame = false;
            game.cancel();
            if(main.getConfig().getBoolean("scoreboard")) {
                objective.setDisplaySlot(null);
                for(String s : players) {
                    Player player = Bukkit.getPlayerExact(s);
                    if(player == null) {continue;}
                    player.getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
                }
            }
            if(main.getConfig().getBoolean("takeAwayOps")) {
                for (int i = 0; i < players.size(); i++) {
                    OfflinePlayer target = Bukkit.getOfflinePlayer(players.get(i));
                    target.setOp(ops.get(i));
                }
            }
        }
        players.clear();
        ops.clear();
        blocks.clear();
        points.clear();
        finished.clear();
        seconds = 0;
    }
    public static String better(String s) {
        StringBuilder finalS = new StringBuilder();
        for(int i = 0; i < s.length(); i++) {
            if(i == 0) {
                finalS.append(s.charAt(i));
            } else if(s.charAt(i) != '_'){
                finalS.append(s.toLowerCase().charAt(i));
            } else {
                finalS.append(' ');
            }
        }
        return finalS.toString();
    }
}