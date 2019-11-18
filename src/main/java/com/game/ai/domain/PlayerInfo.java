package com.game.ai.domain;

public class PlayerInfo {
    private int total;
    private int win;
    private int rate;
    private int level;
    private int weight;
    private int pWin;
    private int pLoss;

    public int getTotal() {
        return total;
    }

    public int getWin() {
        return win;
    }

    public int getRate() {
        return rate;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public void setWin(int win) {
        this.win = win;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public int getpWin() {
        return pWin;
    }

    public int getpLoss() {
        return pLoss;
    }

    public void setpWin(int pWin) {
        this.pWin = pWin;
    }

    public void setpLoss(int pLoss) {
        this.pLoss = pLoss;
    }

    @Override
    public String toString() {
        return "total:" + total +
                ", win:" + win +
                ", rate:" + rate +
                ", level:" + level +
                ", weight:" + weight +
                ", pWin:" + pWin +
                ", pLoss:" + pLoss;
    }
}
