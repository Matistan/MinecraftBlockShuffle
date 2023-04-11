package me.matistan05.minecraftblockshuffle.commands;

import me.matistan05.minecraftblockshuffle.BlockShufflePlayer;
import me.matistan05.minecraftblockshuffle.Main;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.*;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class BlockShuffleCommand implements CommandExecutor {
    private static Main main;
    public static List<BlockShufflePlayer> players = new LinkedList<>();
    int startPlayers;
    ScoreboardManager scoreboardManager = Bukkit.getScoreboardManager();
    Scoreboard scoreboard;
    static Objective objective;
    Material[] materials = Material.values();
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
            if (args.length != 1) {
                p.sendMessage(ChatColor.RED + "Wrong usage of this command. For help, type: /blockshuffle help");
                return true;
            }
            p.sendMessage(ChatColor.GREEN + "------- " + ChatColor.WHITE + " Minecraft BlockShuffle " + ChatColor.GREEN + "----------");
            p.sendMessage(ChatColor.BLUE + "Here is a list of blockshuffle commands:");
            p.sendMessage(ChatColor.YELLOW + "/blockshuffle add <player name> " + ChatColor.AQUA + "- adds a player to a blockshuffle game");
            p.sendMessage(ChatColor.YELLOW + "/blockshuffle remove <player name> " + ChatColor.AQUA + "- removes a player from a blockshuffle game");
            p.sendMessage(ChatColor.YELLOW + "/blockshuffle start " + ChatColor.AQUA + "- starts a blockshuffle game");
            p.sendMessage(ChatColor.YELLOW + "/blockshuffle reset " + ChatColor.AQUA + "- resets a blockshuffle game");
            p.sendMessage(ChatColor.YELLOW + "/blockshuffle list " + ChatColor.AQUA + "- shows a list of players in a blockshuffle game");
            p.sendMessage(ChatColor.YELLOW + "/blockshuffle help " + ChatColor.AQUA + "- shows a list of blockshuffle commands");
            p.sendMessage(ChatColor.GREEN + "----------------------------------");
            return true;
        }
        if (args[0].equals("add")) {
            if(args.length != 2) {
                p.sendMessage(ChatColor.RED + "Wrong usage of this command. For help, type: /blockshuffle help");
                return true;
            }
            Player target = Bukkit.getPlayerExact(args[1]);
            if(target == null) {
                p.sendMessage(ChatColor.RED + "This player does not exist or is offline");
                return true;
            }
            if(inGame) {
                p.sendMessage(ChatColor.RED + "The game has already started!");
                return true;
            }
            if(players.stream().anyMatch(obj -> obj.getName().equals(target.getName()))) {
                p.sendMessage(ChatColor.RED + "This player is already in a game!");
                return true;
            }
            players.add(new BlockShufflePlayer(target.getName()));
            p.sendMessage(ChatColor.AQUA + "Successfully added " + target.getName() + " to the game!");
            return true;
        }
        if (args[0].equals("remove")) {
            if(args.length != 2) {
                p.sendMessage(ChatColor.RED + "Wrong usage of this command. For help, type: /blockshuffle help");
                return true;
            }
            Player target = Bukkit.getPlayerExact(args[1]);
            if(target == null) {
                p.sendMessage(ChatColor.RED + "This player does not exist or is offline");
                return true;
            }
            if(inGame) {
                p.sendMessage(ChatColor.RED + "The game has already started!");
                return true;
            }
            if (players.stream().anyMatch(obj -> obj.getName().equals(target.getName()))) {
                players.removeIf(obj -> obj.getName().equals(target.getName()));
                p.sendMessage(ChatColor.AQUA + "Successfully removed " + target.getName() + " from the game");
                return true;
            }
            p.sendMessage(ChatColor.RED + "This player is not in your blockshuffle game");
            return true;
        }
        if (args[0].equals("start")) {
            if(args.length != 1) {
                p.sendMessage(ChatColor.RED + "Wrong usage of this command. For help, type: /blockshuffle help");
                return true;
            }if(players.size() == 0) {
                p.sendMessage(ChatColor.RED + "There are no players in the game!");
                return true;
            }
            if(inGame) {
                p.sendMessage(ChatColor.YELLOW + "The game has already started!");
                return true;
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
                Player player = Bukkit.getPlayerExact(players.get(i).getName());
                if(player == null) {
                    continue;
                }
                if(main.getConfig().getBoolean("takeAwayOps")) {
                    players.get(i).setPlayerOp(player.isOp());
                    player.setOp(false);
                }
                if(main.getConfig().getBoolean("clearInventories")) {
                    player.getInventory().clear();
                }
                player.setGameMode(GameMode.SURVIVAL);
                player.setHealth(20);
                player.setFoodLevel(20);
                player.setSaturation(20);
                players.get(i).setStood(false);
                players.get(i).setPoints(0);
                if(main.getConfig().getBoolean("sameBlockForEveryone")) {
                    if(i > 0) {
                        players.get(i).setBlock(players.get(i - 1).getBlock());
                    } else {
                        players.get(i).setBlock(randomBlock());
                    }
                } else {
                    players.get(i).setBlock(randomBlock());
                }
                player.sendMessage(ChatColor.DARK_GREEN + "Round "+ round + ": You must stand on " + better(players.get(i).getBlock().name()));
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
                            playersMessage(ChatColor.LIGHT_PURPLE + "" + i + " second" + (i == 1 ? "" : "s") + " remaining!");
                            break;
                        }
                    }
                    if(main.getConfig().getBoolean("scoreboard")) {
                        for(BlockShufflePlayer v : players) {
                            Player player = Bukkit.getPlayerExact(v.getName());
                            if(player != null) {
                                scoreboard = scoreboardManager.getNewScoreboard();
                                objective = scoreboard.registerNewObjective("sb", "dummy", ChatColor.BLUE + "Block Shuffle");
                                objective.setDisplaySlot(DisplaySlot.SIDEBAR);
                                Score timer = objective.getScore(ChatColor.YELLOW + "Time left: " + (time - (seconds % time)));
                                Score score2;
                                if(main.getConfig().getInt("gameMode") == 0) {
                                    score2 = objective.getScore(ChatColor.BLUE + "Players left: " + players.size());
                                } else {
                                    score2 = objective.getScore(ChatColor.BLUE + "Points: " + v.getPoints() + "/" + requiredPoints);
                                }
                                Score score3 = objective.getScore(ChatColor.DARK_GREEN + "Block: " + better(v.getBlock().name()));
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
                        for (BlockShufflePlayer player : players) {
                            if (!player.stood()) {
                                playersMessage(ChatColor.DARK_RED + player.getName() + " didn't stand on their block!");
                            }
                        }
                        if(main.getConfig().getInt("gameMode") == 0) {
                            if(goodPlayers() == 1) {
                                if(startPlayers != 1) {
                                    for (BlockShufflePlayer player : players) {
                                        if (player.stood()) {
                                            playersMessage(ChatColor.GOLD + "" + ChatColor.MAGIC + "IR" + ChatColor.GOLD + player.getName() + " won!" + ChatColor.MAGIC + "IR");
                                            break;
                                        }
                                    }
                                    reset();
                                }
                            } else if(goodPlayers() == 0) {
                                if(startPlayers == 1) {
                                    playersMessage(ChatColor.DARK_RED + "You lost!");
                                } else {
                                    playersMessage(ChatColor.GOLD + "" + ChatColor.MAGIC + "IR" + ChatColor.GOLD + "Draw! Winners:" + ChatColor.MAGIC + "IR");
                                    for (BlockShufflePlayer player : players) {
                                        playersMessage(ChatColor.GOLD + player.getName());
                                    }
                                }
                                reset();
                            }
                            for(int i = 0; i < players.size(); i++) {
                                if(!players.get(i).stood()) {
                                    if(main.getConfig().getBoolean("takeAwayOps")) {
                                        Player target = Bukkit.getPlayerExact(players.get(i).getName());
                                        if(target != null) {
                                            target.setOp(players.get(i).isPlayerOp());
                                        }
                                    }
                                    players.remove(i);
                                    i -= 1;
                                }
                            }
                        } else {
                            playersMessage(ChatColor.DARK_AQUA + "Scoreboard:");
                            for(BlockShufflePlayer player : players) {
                                playersMessage(ChatColor.DARK_AQUA + player.getName() + " " + player.getPoints());
                            }
                            if(playersWith(requiredPoints) == 1) {
                                for(BlockShufflePlayer player : players) {
                                    if(player.getPoints() == requiredPoints) {
                                        playersMessage(ChatColor.GOLD + "" + ChatColor.MAGIC + "IR" + ChatColor.GOLD + player.getName() + " won! Their score: " + requiredPoints + " point" +
                                                (requiredPoints == 1 ? "" : "s") + " in " + round + " round" + (round == 1 ? "" : "s") + ChatColor.MAGIC + "IR");
                                        break;
                                    }
                                }
                                reset();
                            } else if(playersWith(requiredPoints) > 1) {
                                playersMessage(ChatColor.GOLD + "" + ChatColor.MAGIC + "IR" + ChatColor.GOLD + "Draw! Winners:" + ChatColor.MAGIC + "IR");
                                for(BlockShufflePlayer player : players) {
                                    if(player.getPoints() == main.getConfig().getInt("pointsToWin")) {
                                        playersMessage(ChatColor.GOLD + player.getName());
                                    }
                                }
                                playersMessage(ChatColor.GOLD + "Their score: " + requiredPoints + " point" +
                                        (requiredPoints <= 1 ? "" : "s") + " in " + round + " round" + (round == 1 ? "" : "s"));
                                reset();
                            }
                        }
                        round += 1;
                        for(int i = 0; i < players.size(); i++) {
                            players.get(i).setStood(false);
                            if(main.getConfig().getBoolean("sameBlockForEveryone")) {
                                if(i > 0) {
                                    players.get(i).setBlock(players.get(i - 1).getBlock());
                                } else {
                                    players.get(i).setBlock(randomBlock());
                                }
                            } else {
                                players.get(i).setBlock(randomBlock());
                            }
                            Player target = Bukkit.getPlayerExact(players.get(i).getName());
                            if(target != null) {
                                target.sendMessage(ChatColor.DARK_GREEN + "Round " + round + ": You must stand on " + better(players.get(i).getBlock().name()));
                            }
                        }
                    }
                    seconds += 1;
                }
            }.runTaskTimer(main, 0, 20);
            return true;
        }
        if (args[0].equals("reset")) {
            if(args.length != 1) {
                p.sendMessage(ChatColor.RED + "Wrong usage of this command. For help, type: /blockshuffle help");
                return true;
            }
            p.sendMessage(ChatColor.AQUA + "blockshuffle game has been reseted!");
            reset();
            return true;
        }
        if (args[0].equals("list")) {
            if(args.length != 1) {
                p.sendMessage(ChatColor.RED + "Wrong usage of this command. For help, type: /blockshuffle help");
                return true;
            }
            if(players.size() == 0) {
                p.sendMessage(ChatColor.RED + "There is no player in your game!");
                return true;
            }
            p.sendMessage(ChatColor.GREEN + "------- " + ChatColor.WHITE + " Minecraft BlockShuffle " + ChatColor.GREEN + "----------");
            for (BlockShufflePlayer player : players) {
                p.sendMessage(ChatColor.AQUA + player.getName());
            }
            p.sendMessage(ChatColor.GREEN + "----------------------------------");
            return true;
        }
        p.sendMessage(ChatColor.RED + "Wrong argument. For help, type: /blockshuffle help");
        return true;
    }

    private int goodPlayers() {
        int a = 0;
        for (BlockShufflePlayer player : players) {
            if (player.stood()) {
                a++;
            }
        }
        return a;
    }
    public static int playersWith(int b) {
        int a = 0;
        for (BlockShufflePlayer player : players) {
            if (player.getPoints() == b) {
                a++;
            }
        }
        return a;
    }

    private Material randomBlock() {
        Material material;
        do {
            Random random = new Random();
            material = materials[random.nextInt(materials.length)];
        } while(!material.isBlock() || material.name().contains("SHULKER") || material.name().contains("COMMAND") || material.name().equals("DIAMOND_BLOCK") ||
                material.name().equals("EMERALD_BLOCK") || material.name().contains("END") || material.name().contains("GOLD_BLOCK") ||
                material.name().equals("LAPIS_BLOCK") || material.name().equals("NETHERITE_BLOCK") || material.name().equals("BARRIER") ||
                material.name().contains("PURPUR") || material.name().equals("RESPAWN_ANCHOR") || material.name().equals("LODESTONE") ||
                material.name().equals("JIGSAW") || material.name().contains("STRUCTURE") || material.name().contains("CHORUS") ||
                material.name().equals("SPAWNER") || material.name().equals("LIGHT") || material.name().contains("INFESTED") ||
                material.name().equals("ANCIENT_DEBRIS") || material.name().equals("CRYING_OBSIDIAN") || material.name().equals("FROSTED_ICE") ||
                material.name().equals("BEACON") || material.name().equals("CONDUIT") || material.name().contains("FROG") || material.name().contains("EXPOSED") ||
                material.name().contains("WEATHERED") || material.name().contains("OXIDIZED") || material.name().equals("HANGING_ROOTS") ||
                material.name().contains("SKULL") || material.name().contains("SCULK") || material.name().contains("CORAL") || material.name().contains("CAKE") ||
                (material.name().contains("PLAYER") && material.name().contains("HEAD")) ||
                (material.name().contains("ZOMBIE") && material.name().contains("HEAD")) ||
                (material.name().contains("CREEPER") && material.name().contains("HEAD")) ||
                (material.name().contains("DRAGON") && material.name().contains("HEAD")) ||
                (!main.getConfig().getBoolean("enableNetherBlocks") && (material.name().contains("NETHER") || material.name().contains("CRIMSON") ||
                        material.name().contains("WARPED") || material.name().contains("TWISTING") ||
                        material.name().contains("WEEPING") || material.name().equals("SHROOMLIGHT") || material.name().contains("BLACKSTONE") ||
                        material.name().contains("QUARTZ") || material.name().contains("SOUL") || material.name().contains("BASALT") ||
                        material.name().equals("GLOWSTONE") || material.name().equals("REDSTONE_LAMP"))) ||
                (main.getServer().getClass().getPackage().getName().endsWith("1_17_R1") && material.name().equals("CAVE_VINES")));
        return material;
    }

    public static void playersMessage(String s) {
        for (BlockShufflePlayer value : players) {
            Player player = Bukkit.getPlayerExact(value.getName());
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
            }
            if(main.getConfig().getBoolean("takeAwayOps")) {
                for (BlockShufflePlayer player : players) {
                    Player target = Bukkit.getPlayerExact(player.getName());
                    if (target != null) {
                        target.setOp(player.isPlayerOp());
                    }
                }
            }
        }
        players.clear();
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