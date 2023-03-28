# Minecraft Block Shuffle

---

Made with [Spigot](https://www.spigotmc.org/)/[Bukkit](https://dev.bukkit.org/)<br>
Inspired by [Dream](https://www.youtube.com/@dream)

---

> **Having issues?** Feel free to report them on the [Issues tab](https://github.com/Matistan/MinecraftBlockShuffle/issues). I'll be glad to hear your opinion about the plugin as well as extra features you would like me to add!

## Welcome to readme!

Hi! I just want to thank you for your interest in this plugin. I put a lot of effort into this project and I would really love someone to use it!

### Minecraft version

This plugin runs on a Minecraft version 1.16+.

## What is Block Shuffle?

Minecraft Block Shuffle is a very popular game due to a youtuber Dream. Every player has a task to stand on a randomized block on time.
When everyone finds their block, next round starts. You can play it either solo or with your friends.

## Features

Choose between 2 game modes:
- Play until 1 or 0 players are left in the game
- Play until a player has a certain amount of points

## How to use it

- drag the .jar file from the [Release tab](https://github.com/Matistan/MinecraftBlockShuffle/releases) to your plugins folder on your server.
- select players to your game using `/blockshuffle add` command
- type `/blockshuffle start` to start the match!

## Commands

- `/blockshuffle add <player name> ` - adds a player
- `/blockshuffle remove <player name>` - removes a player
- `/blockshuffle start` - starts a game
- `/blockshuffle reset` - resets a game
- `/blockshuffle list` - shows a list of players in a block shuffle game
- `/blockshuffle help` - shows a list of block shuffle commands

## Configuration Options

Edit the `plugins/MinecraftBlockShuffle/config.yml` file to change the following options:

### Main Options

Key|Description|Type|recommended
--|--|--|--
timeSetDayOnStart | Set to true to set the time to day automatically when the game starts. | boolean | true
weatherClearOnStart | Set to true to set the weather to clear automatically when the game starts. | boolean | true
enableNetherBlocks | Set to true to enable nether blocks. | boolean | false; if you choose true, then I recommend increasing time for a round
takeAwayOps | Set to true to take away OPs for the duration of the game. | boolean | false; true, if you play with friends
clearInventories | Set to true to clear players inventories when the game starts. | boolean | true
time | Set the time for a round in seconds (60sec - 3600sec). | int | 300
sameBlockForEveryone | Set to true to make it so that every player has the same block to stand on. | boolean | false; true for less RNG
pvpEnabled | Set to true to enable PvP during the match. | boolean | false

### Game Mode Options

Key|Description|Type|recommended
--|--|--|--
gameMode | Set to 0 to play until there is 1 or 0 players left in a game, set to 1 to play until a player has a certain amount of points. | int | It's up to you!
pointsToWin | Set the number of point required to win (only if you're playing game mode 1). | int | 5

Made by [Matistan](https://github.com/Matistan)