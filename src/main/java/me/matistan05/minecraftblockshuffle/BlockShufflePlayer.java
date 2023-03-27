package me.matistan05.minecraftblockshuffle;

import org.bukkit.Material;

public class BlockShufflePlayer {
    final private String name;
    private boolean op;
    private boolean stood;
    private int points;
    private Material block;
    public BlockShufflePlayer(String name) {
        this.name = name;
    }
    public boolean isPlayerOp() {
        return op;
    }
     public void setPlayerOp(boolean op) {
        this.op = op;
    }
    public boolean stood() {
        return stood;
    }
    public void setStood(boolean stood) {
        this.stood = stood;
    }
    public int getPoints() {
        return points;
    }
    public void setPoints(int points) {
        this.points = points;
    }
    public void addPoint() {
        points += 1;
    }
    public String getName() {
        return name;
    }
    public Material getBlock() {
        return block;
    }
    public void setBlock(Material material) {
        block = material;
    }
}
