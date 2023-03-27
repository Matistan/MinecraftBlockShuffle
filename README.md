# Minecraft Block Shuffle

---

Made with [Spigot](https://www.spigotmc.org/)/[Bukkit](https://dev.bukkit.org/)<br>
Inspired by [Dream](https://www.youtube.com/@dream)

---

## Welcome to readme!

Hi! I just want to thank you for your interest in this plugin. I put a lot of effort into this project and I would really love someone to use it!

### Minecraft version

This plugin runs on a Minecraft version 1.16+.

## What is Block Shuffle?

Minecraft Block Shuffle is a very popular game due to a youtuber Dream. Every player has a task to stand on a randomized block on time.
When everyone finds their block, next round starts. This goes on until 1 or 0 players are left.
You can play it either solo or with your friends.

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

## Configuration options
Edit the `plugins/MinecraftBlockShuffle/config.yml` file to change the following options:
Key|Description|Type|recommended
--|--|--|--
timeSetDayOnStart | Set to true to set the time to day automatically when the game starts. | boolean | true
weatherClearOnStart | Set to true to set the weather to clear automatically when the game starts. | boolean | true
enableNetherBlocks | Set to true to enable nether blocks. | boolean | false; if you choose true, then I recommend increasing time for a round
takeAwayOps | Set to true to take away OPs for the duration of the game. | boolean | false; true, if you play with friends
clearInventories | Set to true to clear players inventories when the game starts. | boolean | true
time | Set the time for a round in seconds (60sec - 3600sec) | int | 300

> **Having issues?** Feel free to report them on the [Issues tab](https://github.com/Matistan/MinecraftBlockShuffle/issues). I'll be glad to hear your opinion about the plugin!

Made by [Matistan](https://github.com/Matistan)