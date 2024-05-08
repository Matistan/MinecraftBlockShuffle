# Minecraft Block Shuffle

---

View on [Spigot](https://www.spigotmc.org/resources/block-shuffle.109009/) • 
Inspired by [Dream](https://www.youtube.com/@dream) • 
Download [here](https://github.com/Matistan/MinecraftBlockShuffle/releases)

---

> **Having issues?** Feel free to report them on the [Issues tab](https://github.com/Matistan/MinecraftBlockShuffle/issues). I'll be glad to hear your opinion about the plugin as well as extra features you would like me to add!

## Welcome to readme!

Hi!
I just want to thank you for your interest in this plugin.
I put a lot of effort into this project, and I would really love someone to use it!

### Minecraft version

This plugin runs on a Minecraft version 1.16+.

## What is Block Shuffle?

Minecraft Block Shuffle is a very popular game due to a youtuber Dream. Every player has a task to stand on a randomized block on time.
When everyone finds their block, the next round starts. You can play it either solo or with your friends.

## Features

- Ban your own blocks! Go to the `plugins/MinecraftBlockShuffle/disabled.yml` and add blocks that you want to ban (a list of all the blocks is at `plugins/MinecraftBlockShuffle/blocks.yml`)
- Choose between 2 game modes:
- - Play until 1 or 0 players are left in the game
- - Play until a player has a certain number of points

## Important notes

In some Minecraft versions, the plugin can give you a block from a higher version to stand on. For example, if you're playing on 1.20.4,
the plugin can give you blocks from 1.21.

### So what to do in this situation?

The solution to this is play normally, but if the game gives you an impossible block (or just a really hard one),
you can use the `/blockshuffle ban <block_name>` command to ban the block, and then type `/blockshuffle skip` to skip the round (no points will be added).

## How to use it

- drag the .jar file from the [Release tab](https://github.com/Matistan/MinecraftBlockShuffle/releases) to your plugins folder on your server.
- type `/blockshuffle start` to start the match!
- if you don't want to play with every player on the server, change the rule `playWithEveryone` to false, and choose the players using `/blockshuffle add` command

## Commands

- `/blockshuffle add <player> <player> ... ` - adds players
- `/blockshuffle add @a` - adds all players
- `/blockshuffle remove <player> <player> ... ` - removes players
- `/blockshuffle remove @a` - removes all players
- `/blockshuffle start` - starts a game
- `/blockshuffle reset` - resets a game
- `/blockshuffle list` - shows a list of players in a block shuffle game
- `/blockshuffle skip` - skips a round (e.g. when someone got an impossible block)
- `/blockshuffle ban <block>` - bans a block
- `/blockshuffle unban <block>` - unbans a block
- `/blockshuffle rules <rule> value(optional)` - changes some additional rules of the game (in config.yml)
- `/blockshuffle help` - shows a list of block shuffle commands

## Configuration Options

Use the command `/blockshuffle rules` or edit the `plugins/MinecraftBlockShuffle/config.yml` file to change the following options:

### Main Options

| Key                  | Description                                                                                                     | Type    | recommended                                                             |
|----------------------|-----------------------------------------------------------------------------------------------------------------|---------|-------------------------------------------------------------------------|
| timeSetDayOnStart    | Set to true to set the time to day automatically when the game starts.                                          | boolean | true                                                                    |
| weatherClearOnStart  | Set to true to set the weather to clear automatically when the game starts.                                     | boolean | true                                                                    |
| playWithEveryone     | Set to true to not have to use '/blockshuffle add' every time, and instead play with every player on the server | boolean | true                                                                    |
| enableNetherBlocks   | Set to true to enable nether blocks.                                                                            | boolean | false; if you choose true, then I recommend increasing time for a round |
| takeAwayOps          | Set to true to take away OPs for the duration of the game.                                                      | boolean | true                                                                    |
| clearInventories     | Set to true to clear players inventories when the game starts.                                                  | boolean | true                                                                    |
| time                 | Set the time for a round in seconds (60sec - 3600sec).                                                          | int     | 300                                                                     |
| sameBlockForEveryone | Set to true to make it so that every player has the same block to stand on.                                     | boolean | false; true for less RNG                                                |
| pvpEnabled           | Set to true to enable PvP during the match.                                                                     | boolean | false                                                                   |
| scoreboard           | Set to true to show scoreboard with the timer.                                                                  | boolean | true                                                                    |
| usePermissions       | Set to true to require users to have permission to use certain commands.                                        | boolean | false; true if you don't trust the people you're playing with           |

### Game Mode Options

| Key         | Description                                                                                                                    | Type | recommended     |
|-------------|--------------------------------------------------------------------------------------------------------------------------------|------|-----------------|
| gameMode    | Set to 0 to play until there is 1 or 0 players left in a game, set to 1 to play until a player has a certain amount of points. | int  | It's up to you! |
| pointsToWin | Set the number of point required to win (only if you're playing game mode 1).                                                  | int  | 5               |

## Permissions

If `usePermissions` is set to `true` in the `config.yml` file, players without ops will need the following permissions to use the commands:

| Permission                | Description                                                  |
|---------------------------|--------------------------------------------------------------|
| blockshuffle.blockshuffle | Allows the player to use all `/blockshuffle` commands.       |
| blockshuffle.add          | Allows the player to use the `/blockshuffle add` command.    |
| blockshuffle.remove       | Allows the player to use the `/blockshuffle remove` command. |
| blockshuffle.start        | Allows the player to use the `/blockshuffle start` command.  |
| blockshuffle.reset        | Allows the player to use the `/blockshuffle reset` command.  |
| blockshuffle.list         | Allows the player to use the `/blockshuffle list` command.   |
| blockshuffle.skip         | Allows the player to use the `/blockshuffle skip` command.   |
| blockshuffle.ban          | Allows the player to use the `/blockshuffle ban` command.    |
| blockshuffle.unban        | Allows the player to use the `/blockshuffle unban` command.  |
| blockshuffle.rules        | Allows the player to use the `/blockshuffle rules` command.  |
| blockshuffle.help         | Allows the player to use the `/blockshuffle help` command.   |

### Bugs & Issues

> **Having issues?** Feel free to report them on the [Issues tab](https://github.com/Matistan/MinecraftBlockShuffle/issues). I'll be glad to hear your opinion about the plugin as well as extra features you would like me to add!


Made by [Matistan](https://github.com/Matistan)