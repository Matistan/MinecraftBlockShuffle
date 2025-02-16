package me.matistan05.minecraftblockshuffle.commands;

import me.matistan05.minecraftblockshuffle.Main;
import me.matistan05.minecraftblockshuffle.classes.BlockShufflePlayer;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import static me.matistan05.minecraftblockshuffle.Main.bannedFile;

public class BlockShuffleCommand implements CommandExecutor {
    private static Main main;
    public static List<BlockShufflePlayer> players = new ArrayList<>();
    int startPlayers;
    public static ScoreboardManager scoreboardManager = Bukkit.getScoreboardManager();
    public static boolean inGame = false;
    public static boolean firstGameMode = true;
    public static BukkitTask game;
    public static int roundTime;
    public static int requiredPoints, round;
    public static int seconds;
    public static Material blockForEveryone;

    public BlockShuffleCommand(Main main) {
        BlockShuffleCommand.main = main;
    }

    @Override
    public boolean onCommand(CommandSender p, Command command, String label, String[] args) {
        if (args.length == 0) {
            p.sendMessage(ChatColor.RED + "You must type an argument. For help, type: /blockshuffle help");
        } else if (args[0].equals("help")) {
            if (!p.hasPermission("blockshuffle.help") && main.getConfig().getBoolean("usePermissions")) {
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
            p.sendMessage(ChatColor.YELLOW + "/blockshuffle add @a " + ChatColor.AQUA + "- adds all players");
            p.sendMessage(ChatColor.YELLOW + "/blockshuffle remove <player> <player> ... " + ChatColor.AQUA + "- removes players from a blockshuffle game");
            p.sendMessage(ChatColor.YELLOW + "/blockshuffle remove @a " + ChatColor.AQUA + "- removes all players");
            p.sendMessage(ChatColor.YELLOW + "/blockshuffle start " + ChatColor.AQUA + "- starts a blockshuffle game");
            p.sendMessage(ChatColor.YELLOW + "/blockshuffle reset " + ChatColor.AQUA + "- resets a blockshuffle game");
            p.sendMessage(ChatColor.YELLOW + "/blockshuffle list " + ChatColor.AQUA + "- shows a list of players in a blockshuffle game");
            p.sendMessage(ChatColor.YELLOW + "/blockshuffle skip " + ChatColor.AQUA + "- skips a round (i.e. when someone got an impossible block)");
            p.sendMessage(ChatColor.YELLOW + "/blockshuffle ban " + ChatColor.AQUA + "- bans a block from being chosen next time");
            p.sendMessage(ChatColor.YELLOW + "/blockshuffle unban " + ChatColor.AQUA + "- unbans a block");
            p.sendMessage(ChatColor.YELLOW + "/blockshuffle rules <rule> value(optional) " + ChatColor.AQUA + "- changes some additional rules of the game (in config.yml)");
            p.sendMessage(ChatColor.YELLOW + "/blockshuffle help " + ChatColor.AQUA + "- shows a list of blockshuffle commands");
            p.sendMessage(ChatColor.GREEN + "----------------------------------");
        } else if (args[0].equals("rules")) {
            if (!p.hasPermission("blockshuffle.rules") && main.getConfig().getBoolean("usePermissions")) {
                p.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
                return true;
            }
            if (args.length != 3 && args.length != 2) {
                p.sendMessage(ChatColor.RED + "Wrong usage of this command. For help, type: /blockshuffle help");
                return true;
            }
            if (!main.getConfig().contains(args[1])) {
                p.sendMessage(ChatColor.RED + "There is no such rule. See the config.yml file for more information.");
                return true;
            }
            if (args.length == 2) {
                p.sendMessage(ChatColor.AQUA + "The value of the rule " + args[1] + " is: " + main.getConfig().get(args[1]));
                return true;
            }
            if (args[1].equals("time") || args[1].equals("gameMode") || args[1].equals("pointsToWin")) {
                try {
                    main.getConfig().set(args[1], Integer.parseInt(args[2]));
                } catch (NumberFormatException e) {
                    p.sendMessage(ChatColor.RED + "The value must be a number!");
                    return true;
                }
            } else {
                if (!args[2].equals("true") && !args[2].equals("false")) {
                    p.sendMessage(ChatColor.RED + "The value must be true or false!");
                    return true;
                }
                main.getConfig().set(args[1], Boolean.parseBoolean(args[2]));
            }
            main.saveConfig();
            p.sendMessage(ChatColor.AQUA + "The value of the rule " + args[1] + " has been changed to: " + args[2]);
        } else if (args[0].equals("add")) {
            if (!p.hasPermission("blockshuffle.add") && main.getConfig().getBoolean("usePermissions")) {
                p.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
                return true;
            }
            if (args.length < 2) {
                p.sendMessage(ChatColor.RED + "Wrong usage of this command. For help, type: /blockshuffle help");
                return true;
            }
            List<String> playersToAdd;
            if (args[1].equals("@a")) {
                if (args.length != 2) {
                    p.sendMessage(ChatColor.RED + "Wrong usage of this command. For help, type: /blockshuffle help");
                    return true;
                }
                playersToAdd = Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList());
            } else {
                playersToAdd = Arrays.stream(args).skip(1).collect(Collectors.toList());
                playersToAdd = playersToAdd.stream().filter(name -> Bukkit.getPlayerExact(name) != null).collect(Collectors.toList());
            }
            int count = 0;
            for (String playerName : playersToAdd) {
                if (inGame && startPlayers == 1) {
                    if (!isStillPlaying(playerName)) {
                        p.sendMessage(ChatColor.RED + "You can't add player to the solo game!");
                        return true;
                    }
                }
                boolean newPlayer = false;
                if (!isPlayer(playerName)) {
                    players.add(new BlockShufflePlayer(playerName));
                    if (inGame) {
                        setUpPlayer(playerName);
                    }
                    newPlayer = true;
                } else if (inGame && !isStillPlaying(playerName)) {
                    setUpPlayer(playerName);
                    newPlayer = true;
                }
                if (newPlayer) count++;
            }
            if (count > 0) {
                p.sendMessage(ChatColor.AQUA + "Successfully added " + count + " player" + (count == 1 ? "" : "s") + " to the game!");
            } else {
                p.sendMessage(ChatColor.RED + "No player was added!");
            }
        } else if (args[0].equals("remove")) {
            if (!p.hasPermission("blockshuffle.remove") && main.getConfig().getBoolean("usePermissions")) {
                p.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
                return true;
            }
            if (args.length < 2) {
                p.sendMessage(ChatColor.RED + "Wrong usage of this command. For help, type: /blockshuffle help");
                return true;
            }
            int count = 0;
            List<String> playersToRemove;
            if (args[1].equals("@a")) {
                if (args.length != 2) {
                    p.sendMessage(ChatColor.RED + "Wrong usage of this command. For help, type: /blockshuffle help");
                    return true;
                }
                if (inGame) {
                    p.sendMessage(ChatColor.RED + "You can't remove all players while in game!");
                    return true;
                }
                playersToRemove = players.stream().map(BlockShufflePlayer::getName).collect(Collectors.toList());
            } else {
                playersToRemove = Arrays.stream(args).skip(1).collect(Collectors.toList());
                playersToRemove = playersToRemove.stream().filter(BlockShuffleCommand::isStillPlaying).collect(Collectors.toList());
                if (inGame) {
                    int newSize = amountOfPlayersWhoAreStillPlaying() - playersToRemove.size();
                    if (startPlayers > 1 && newSize < 2) {
                        p.sendMessage(ChatColor.RED + "There must be at least two players in the multiplayer game!");
                        return true;
                    }
                    if (newSize == 0) {
                        p.sendMessage(ChatColor.RED + "There must be at least one player in the game!");
                        return true;
                    }
                }
            }
            for (String playerName : playersToRemove) {
                removePlayer(playerName);
                count++;
            }
            if (count > 0) {
                p.sendMessage(ChatColor.AQUA + "Successfully removed " + count + " player" + (count == 1 ? "" : "s") + " from the game!");
            } else {
                p.sendMessage(ChatColor.RED + "No player was removed!");
            }
        } else if (args[0].equals("start")) {
            if (!p.hasPermission("blockshuffle.start") && main.getConfig().getBoolean("usePermissions")) {
                p.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
                return true;
            }
            if (args.length != 1) {
                p.sendMessage(ChatColor.RED + "Wrong usage of this command. For help, type: /blockshuffle help");
                return true;
            }
            if (inGame) {
                p.sendMessage(ChatColor.YELLOW + "The game has already started!");
                return true;
            }
            if (main.getConfig().getBoolean("playWithEveryone")) {
                players.clear();
                for (Player target : Bukkit.getOnlinePlayers()) {
                    players.add(new BlockShufflePlayer(target.getName()));
                }
            }
            if (players.isEmpty()) {
                p.sendMessage(ChatColor.RED + "There are no players in the game!");
                return true;
            }
            for (BlockShufflePlayer playerObject : players) {
                Player player = Bukkit.getPlayerExact(playerObject.getName());
                if (player == null) {
                    p.sendMessage(ChatColor.RED + "Someone from your game is offline!");
                    return true;
                }
            }
            if (main.getConfig().getBoolean("timeSetDayOnStart")) {
                p.getServer().getWorlds().get(0).setTime(0);
            }
            if (main.getConfig().getBoolean("weatherClearOnStart")) {
                p.getServer().getWorlds().get(0).setStorm(false);
            }
            round = 1;
            startPlayers = players.size();
            firstGameMode = main.getConfig().getInt("gameMode") <= 0;
            playersMessage(ChatColor.AQUA + "START!");
            if (!firstGameMode) {
                requiredPoints = (Math.max(main.getConfig().getInt("pointsToWin"), 1));
                playersMessage(ChatColor.AQUA + "First player to score " + requiredPoints +
                        " point" + (requiredPoints == 1 ? "" : "s") + ", wins!");
            }
            if (main.getConfig().getBoolean("sameBlockForEveryone")) {
                blockForEveryone = randomBlock();
            }
            for (BlockShufflePlayer playerObject : players) {
                setUpPlayer(playerObject.getName());
            }
            inGame = true;
            roundTime = Math.max(Math.min(main.getConfig().getInt("time"), 3600), 1);
            seconds = 0;
            game = new BukkitRunnable() {
                @Override
                public void run() {
                    if (amountOfPlayersWhoFoundTheirBlock() == amountOfPlayersWhoAreStillPlaying() || (!firstGameMode && amountOfPlayersWhoFoundTheirBlock() >= 1 && main.getConfig().getBoolean("onlyFirstPoint"))) {
                        seconds = roundTime;
                    }
                    if (seconds % roundTime == 0 && seconds != 0) {
                        for (BlockShufflePlayer playerObject : players) {
                            if (!playerObject.foundHisBlock()) {
                                playersMessage(ChatColor.DARK_RED + playerObject.getName() + " didn't stand on their block!");
                            }
                        }
                        if (firstGameMode) {
                            if (amountOfPlayersWhoFoundTheirBlock() == 1) {
                                if (startPlayers != 1) {
                                    playersMessage(ChatColor.GOLD + String.valueOf(ChatColor.MAGIC) + "IR" + ChatColor.GOLD + players.stream().filter(BlockShufflePlayer::foundHisBlock).findFirst().get().getName() + " won!" + ChatColor.MAGIC + "IR");
                                    reset();
                                }
                            } else if (amountOfPlayersWhoFoundTheirBlock() == 0) {
                                if (startPlayers == 1) {
                                    playersMessage(ChatColor.DARK_RED + "You lost!");
                                } else {
                                    playersMessage(ChatColor.GOLD + String.valueOf(ChatColor.MAGIC) + "IR" + ChatColor.GOLD + "Draw! Winners:" + ChatColor.MAGIC + "IR");
                                    for (BlockShufflePlayer playerObject : players) {
                                        playersMessage(ChatColor.GOLD + playerObject.getName());
                                    }
                                }
                                reset();
                            }
                            for (BlockShufflePlayer playerObject : players) {
                                if (!playerObject.foundHisBlock()) {
                                    removePlayer(playerObject.getName());
                                }
                            }
                        } else {
                            playersMessage(ChatColor.DARK_AQUA + "Scoreboard:");
                            for (BlockShufflePlayer playerObject : players) {
                                Player player = Bukkit.getPlayerExact(playerObject.getName());
                                if (player == null) continue;
                                sendScoreboard(player);
                            }
                            if (amountOfPlayersWithPoints(requiredPoints) == 1) {
                                playersMessage(ChatColor.GOLD + String.valueOf(ChatColor.MAGIC) + "IR" + ChatColor.GOLD + players.stream().filter(p -> p.getPoints() == requiredPoints).findFirst().get().getName() + " won! Their score: " + requiredPoints
                                        + " point" + (requiredPoints == 1 ? "" : "s") + " in " + round + " round" + (round == 1 ? "" : "s") + ChatColor.MAGIC + "IR");
                                reset();
                            } else if (amountOfPlayersWithPoints(requiredPoints) > 1) {
                                playersMessage(ChatColor.GOLD + String.valueOf(ChatColor.MAGIC) + "IR" + ChatColor.GOLD + "Draw! Winners:" + ChatColor.MAGIC + "IR");
                                for (BlockShufflePlayer playerObject : players) {
                                    if (playerObject.getPoints() == requiredPoints) {
                                        playersMessage(ChatColor.GOLD + playerObject.getName());
                                    }
                                }
                                playersMessage(ChatColor.GOLD + "Their score: " + requiredPoints + " point" +
                                        (requiredPoints == 1 ? "" : "s") + " in " + round + " round" + (round == 1 ? "" : "s"));
                                reset();
                            }
                        }
                        round += 1;
                        if (main.getConfig().getBoolean("sameBlockForEveryone")) {
                            blockForEveryone = randomBlock();
                        }
                        for (BlockShufflePlayer playerObject : players) {
                            setUpBlock(playerObject);
                            Player player = Bukkit.getPlayerExact(playerObject.getName());
                            if (player != null) {
                                player.sendMessage(ChatColor.DARK_GREEN + "Round " + round + ": You must stand on " + blockNameFormatted(playerObject.getBlock().name()));
                            }
                        }
                    }
                    for (int i = 1; i <= 10; i++) {
                        if ((seconds + i) % roundTime == 0) {
                            playersMessage(ChatColor.LIGHT_PURPLE + String.valueOf(i) + " second" + (i == 1 ? "" : "s") + " remaining!");
                            break;
                        }
                    }
                    if (main.getConfig().getBoolean("scoreboard")) {
                        setUpScoreboard();
                    }
                    seconds += 1;
                }
            }.runTaskTimer(main, 0, 20);
        } else if (args[0].equals("reset")) {
            if (!p.hasPermission("blockshuffle.reset") && main.getConfig().getBoolean("usePermissions")) {
                p.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
                return true;
            }
            if (args.length != 1) {
                p.sendMessage(ChatColor.RED + "Wrong usage of this command. For help, type: /blockshuffle help");
                return true;
            }
            p.sendMessage(ChatColor.AQUA + "blockshuffle game has been reseted!");
            reset();
        } else if (args[0].equals("list")) {
            if (!p.hasPermission("blockshuffle.list") && main.getConfig().getBoolean("usePermissions")) {
                p.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
                return true;
            }
            if (args.length != 1) {
                p.sendMessage(ChatColor.RED + "Wrong usage of this command. For help, type: /blockshuffle help");
                return true;
            }
            if (main.getConfig().getBoolean("playWithEveryone")) {
                p.sendMessage(ChatColor.AQUA + "Everyone is in the game!");
                return true;
            }
            if (players.isEmpty()) {
                p.sendMessage(ChatColor.RED + "There is no player in your game!");
                return true;
            }
            sendScoreboard(p);
        } else if (args[0].equals("skip")) {
            if (!p.hasPermission("blockshuffle.skip") && main.getConfig().getBoolean("usePermissions")) {
                p.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
                return true;
            }
            if (args.length != 1) {
                p.sendMessage(ChatColor.RED + "Wrong usage of this command. For help, type: /blockshuffle help");
                return true;
            }
            if (!inGame) {
                p.sendMessage(ChatColor.RED + "The game hasn't started yet!");
                return true;
            }
            if (amountOfPlayersWhoFoundTheirBlock() != amountOfPlayersWhoAreStillPlaying()) {
                for (BlockShufflePlayer playerObject : players) {
                    if (!firstGameMode && playerObject.foundHisBlock()) {
                        playerObject.setPoints(playerObject.getPoints() - 1);
                    }
                    playerObject.setFoundHisBlock(true);
                }
            }
            playersMessage(ChatColor.DARK_GREEN + "Skipped the round!");
        } else if (args[0].equals("ban")) {
            String bannedBlock = args[1].toUpperCase();
            if (!p.hasPermission("blockshuffle.ban") && main.getConfig().getBoolean("usePermissions")) {
                p.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
                return true;
            }
            if (args.length != 2) {
                p.sendMessage(ChatColor.RED + "Wrong usage of this command. For help, type: /blockshuffle help");
                return true;
            }
            if (!Main.blocks.getStringList("blocks").contains(bannedBlock)) {
                p.sendMessage(ChatColor.RED + "This block doesn't exist!");
                return true;
            }
            if (Main.banned.getStringList("bannedBlocks").contains(bannedBlock)) {
                p.sendMessage(ChatColor.RED + "This block is already banned!");
                return true;
            }
            List<String> bannedBlocks = Main.banned.getStringList("bannedBlocks");
            bannedBlocks.add(bannedBlock);
            Main.banned.set("bannedBlocks", bannedBlocks);
            try {
                Main.banned.save(bannedFile);
            } catch (IOException ignored) {}
            p.sendMessage(ChatColor.AQUA + "Successfully banned " + bannedBlock.toLowerCase() + "!");
        } else if (args[0].equals("unban")) {
            String unbannedBlock = args[1].toUpperCase();
            if (!p.hasPermission("blockshuffle.unban") && main.getConfig().getBoolean("usePermissions")) {
                p.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
                return true;
            }
            if (args.length != 2) {
                p.sendMessage(ChatColor.RED + "Wrong usage of this command. For help, type: /blockshuffle help");
                return true;
            }
            if (!Main.blocks.getStringList("blocks").contains(unbannedBlock)) {
                p.sendMessage(ChatColor.RED + "This block doesn't exist!");
                return true;
            }
            if (!Main.banned.getStringList("bannedBlocks").contains(unbannedBlock)) {
                p.sendMessage(ChatColor.RED + "This block isn't banned!");
                return true;
            }
            List<String> bannedBlocks = Main.banned.getStringList("bannedBlocks");
            bannedBlocks.remove(unbannedBlock);
            Main.banned.set("bannedBlocks", bannedBlocks);
            try {
                Main.banned.save(bannedFile);
            } catch (IOException ignored) {}
            p.sendMessage(ChatColor.AQUA + "Successfully unbanned " + unbannedBlock.toLowerCase() + "!");
            return true;
        } else {
            p.sendMessage(ChatColor.RED + "Wrong argument. For help, type: /blockshuffle help");
        }
        return true;
    }

    private static String timeLeftFormatted() {
        return String.format("%02d:%02d", (roundTime - (seconds % roundTime)) / 60, (roundTime - (seconds % roundTime)) % 60);
    }

    private int amountOfPlayersWhoFoundTheirBlock() {
        return (int) players.stream().filter(p -> p.foundHisBlock() && p.isStillPlaying()).count();
    }

    public static int amountOfPlayersWithPoints(int points) {
        return (int) players.stream().filter(p -> p.getPoints() == points && p.isStillPlaying()).count();
    }

    public static int amountOfPlayersWhoAreStillPlaying() {
        return (int) players.stream().filter(BlockShufflePlayer::isStillPlaying).count();
    }

    private static Material randomBlock() {
        Material material;
        List<Material> materials = new ArrayList<>(Arrays.asList(Material.values()));
        Random random = new Random();
        materials.removeIf(obj -> !obj.isBlock());
        for (String g : Main.banned.getStringList("bannedBlocks")) {
            materials.removeIf(obj -> obj.name().equals(g));
        }
        if (!main.getConfig().getBoolean("enableNetherBlocks")) {
            materials.removeIf(obj -> (obj.name().contains("NETHER") || obj.name().contains("CRIMSON") || obj.name().contains("WARPED") || obj.name().contains("TWISTING") ||
                    obj.name().contains("WEEPING") || obj.name().equals("SHROOMLIGHT") || obj.name().contains("BLACKSTONE") || obj.name().contains("QUARTZ") ||
                    obj.name().contains("SOUL") || obj.name().contains("BASALT") || obj.name().equals("GLOWSTONE") || obj.name().equals("REDSTONE_LAMP")));
        }
        material = materials.get(random.nextInt(materials.size()));
        return material;
    }

    public static void playersMessage(String s) {
        for (BlockShufflePlayer playerObject : players) {
            Player player = Bukkit.getPlayerExact(playerObject.getName());
            if (player != null) player.sendMessage(s);
        }
    }

    public static void reset() {
        if (inGame) {
            game.cancel();
            for (BlockShufflePlayer playerObject : players) {
                if (main.getConfig().getBoolean("takeAwayOps")) {
                    OfflinePlayer player = Bukkit.getOfflinePlayer(playerObject.getName());
                    player.setOp(playerObject.isOp());
                }
                Player player = Bukkit.getPlayerExact(playerObject.getName());
                if (player == null) continue;
                player.setGameMode(playerObject.getOldGameMode());
                if (main.getConfig().getBoolean("scoreboard")) {
//                    playerObject.getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
                    player.setScoreboard(scoreboardManager.getMainScoreboard());
                }
            }
            inGame = false;
        }
        players.clear();
    }

    private void removePlayer(String name) {
        BlockShufflePlayer playerObject = players.stream().filter(p -> p.getName().equals(name)).findFirst().get();
        if (inGame) {
            playerObject.setStillPlaying(false);
            if (main.getConfig().getBoolean("giveSpectators")) {
                Player player = Bukkit.getPlayerExact(name);
                if (player != null) player.setGameMode(GameMode.SPECTATOR);
            }
        } else {
            players.removeIf(p -> p.getName().equals(name));
        }
    }

    public static String blockNameFormatted(String s) {
        StringBuilder finalS = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            if (i == 0) {
                finalS.append(s.charAt(i));
            } else if (s.charAt(i) != '_') {
                finalS.append(s.toLowerCase().charAt(i));
            } else {
                finalS.append(' ');
            }
        }
        return finalS.toString();
    }

    public static void setUpScoreboard() {
        for (BlockShufflePlayer playerObject : players) {
            Player player = Bukkit.getPlayerExact(playerObject.getName());
            if (player == null) continue;
            Scoreboard scoreboard = playerObject.getScoreboard();
            scoreboard.getEntries().forEach(scoreboard::resetScores);
            Objective objective = scoreboard.getObjective("sb");
            if (objective == null) continue;
            objective.getScore(ChatColor.YELLOW + "Time left: " + timeLeftFormatted()).setScore(4);
            if (playerObject.isStillPlaying()) {
                objective.getScore(ChatColor.DARK_GREEN + "Block: " + blockNameFormatted(playerObject.getBlock().name())).setScore(3);
            } else {
                objective.getScore(ChatColor.RED + "You lost!").setScore(3);
            }
            if (firstGameMode) {
                objective.getScore(ChatColor.BLUE + "Players left: " + playersLeft()).setScore(2);
            } else {
                objective.getScore(ChatColor.BLUE + "Points: " + playerObject.getPoints() + "/" + requiredPoints).setScore(2);
            }
            objective.getScore(ChatColor.AQUA + "Round: " + round).setScore(1);
        }
    }

    public static boolean isPlayer(String name) {
        return players.stream().anyMatch(p -> p.getName().equals(name));
    }

    public static BlockShufflePlayer getPlayer(String name) {
        return players.stream().filter(p -> p.getName().equals(name)).findFirst().orElse(null);
    }

    public static boolean isStillPlaying(String name) {
        return players.stream().anyMatch(p -> p.getName().equals(name) && p.isStillPlaying());
    }

    public static int playersLeft() {
        return (int) players.stream().filter(BlockShufflePlayer::isStillPlaying).count();
    }

    public void setUpPlayer(String name) {
        Player player = Bukkit.getPlayerExact(name);
        if (player == null) return;
        BlockShufflePlayer playerObject = getPlayer(name);
        if (playerObject == null) return;
        if (playerObject.isStillPlaying()) {
            playerObject.setOldGameMode(player.getGameMode());
            if (main.getConfig().getBoolean("takeAwayOps")) {
                playerObject.setOp(player.isOp());
                player.setOp(false);
            }
            if (main.getConfig().getBoolean("scoreboard")) {
                Scoreboard scoreboard = scoreboardManager.getNewScoreboard();
                Objective objective = scoreboard.registerNewObjective("sb", "dummy", ChatColor.BLUE + String.valueOf(ChatColor.BOLD) + "Block Shuffle");
                objective.setDisplaySlot(DisplaySlot.SIDEBAR);
                playerObject.setScoreboard(scoreboard);
                player.setScoreboard(scoreboard);
            }
        } else {
            playerObject.resetStats();
        }
        setUpBlock(playerObject);
        player.setGameMode(GameMode.SURVIVAL);
        player.setHealth(20);
        player.setFoodLevel(20);
        player.setSaturation(5);
        if (main.getConfig().getBoolean("clearInventories")) {
            player.getInventory().clear();
        }
        player.sendMessage(ChatColor.DARK_GREEN + "Round " + round + ": You must stand on " + blockNameFormatted(playerObject.getBlock().name()));
    }

    public static void setUpBlock(BlockShufflePlayer playerObject) {
        playerObject.setFoundHisBlock(false);
        if (main.getConfig().getBoolean("sameBlockForEveryone")) {
            playerObject.setBlock(blockForEveryone);
        } else {
            playerObject.setBlock(randomBlock());
        }
    }

    public static void sendScoreboard(CommandSender p) {
        p.sendMessage(ChatColor.YELLOW + "-------" + ChatColor.WHITE + " Minecraft BlockShuffle " + ChatColor.YELLOW + "-------");
        for (BlockShufflePlayer playerObject : players) {
            StringBuilder status = new StringBuilder();
            status.append(ChatColor.AQUA).append(playerObject.getName());
            if (inGame && !firstGameMode && playerObject.isStillPlaying()) {
                status.append(ChatColor.BLUE).append(" - ").append(playerObject.getPoints()).append(" points");
            }
            if (!playerObject.isStillPlaying()) {
                status.append(ChatColor.RED).append(" (lost)");
            }
            p.sendMessage(status.toString());
        }
        p.sendMessage(ChatColor.YELLOW + "----------------------------------");
    }
}
