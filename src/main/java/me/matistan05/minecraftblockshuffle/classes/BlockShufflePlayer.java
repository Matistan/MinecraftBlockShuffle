package me.matistan05.minecraftblockshuffle.classes;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.scoreboard.Scoreboard;

import static me.matistan05.minecraftblockshuffle.commands.BlockShuffleCommand.scoreboardManager;

public class BlockShufflePlayer {
    private final String name;
    private int points;
    private boolean op;
    private boolean foundHisBlock;
    private boolean isStillPlaying;
    private Material block;
    private GameMode oldGameMode;
    private Scoreboard scoreboard;

    public BlockShufflePlayer(String name) {
        this.name = name;
        this.points = 0;
        this.op = false;
        this.foundHisBlock = false;
        this.isStillPlaying = true;
        this.block = null;
        this.oldGameMode = GameMode.SURVIVAL;
        this.scoreboard = scoreboardManager.getNewScoreboard();
    }

    public void resetStats() {
        this.points = 0;
        this.foundHisBlock = false;
        this.isStillPlaying = true;
        this.block = null;
    }

    public String getName() {
        return name;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public boolean isOp() {
        return op;
    }

    public void setOp(boolean op) {
        this.op = op;
    }

    public boolean foundHisBlock() {
        return foundHisBlock;
    }

    public void setFoundHisBlock(boolean foundHisBlock) {
        this.foundHisBlock = foundHisBlock;
    }

    public boolean isStillPlaying() {
        return isStillPlaying;
    }

    public void setStillPlaying(boolean isStillPlaying) {
        this.isStillPlaying = isStillPlaying;
    }

    public Material getBlock() {
        return block;
    }

    public void setBlock(Material block) {
        this.block = block;
    }

    public GameMode getOldGameMode() {
        return oldGameMode;
    }

    public void setOldGameMode(GameMode oldGameMode) {
        this.oldGameMode = oldGameMode;
    }

    public Scoreboard getScoreboard() {
        return scoreboard;
    }

    public void setScoreboard(Scoreboard scoreboard) {
        this.scoreboard = scoreboard;
    }
}
