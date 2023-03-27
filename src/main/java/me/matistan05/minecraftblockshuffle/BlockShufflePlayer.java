package me.matistan05.minecraftblockshuffle;

public class BlockShufflePlayer {
    final private String name;
    private boolean op;
    private boolean stood;
    private int points;

    BlockShufflePlayer(String name, boolean op, boolean stood, int points) {
        this.name = name;
        this.op = op;
        this.stood = stood;
        this.points = points;
    }
    boolean isOp() {
        return op;
    }
    void setOp(boolean op) {
        this.op = op;
    }
    boolean stood() {
        return stood;
    }
    void setStood(boolean stood) {
        this.stood = stood;
    }
    int getPoints() {
        return points;
    }
    void setPoints(int points) {
        this.points = points;
    }
    void addPoint() {
        points += 1;
    }
}
