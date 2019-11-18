package com.game.gconf.domain;

import java.util.List;

public class Comment {
    private short enable;
    private int times;
    private List<Integer> strategy;
    private S1 s1;
    private S2 s2;
    private S3 s3;

    public short getEnable() {
        return enable;
    }

    public int getTimes() {
        return times;
    }

    public List<Integer> getStrategy() {
        return strategy;
    }

    public S1 getS1() {
        return s1;
    }

    public S2 getS2() {
        return s2;
    }

    public S3 getS3() {
        return s3;
    }

    public void setEnable(short enable) {
        this.enable = enable;
    }

    public void setTimes(int times) {
        this.times = times;
    }

    public void setStrategy(List<Integer> strategy) {
        this.strategy = strategy;
    }

    public void setS1(S1 s1) {
        this.s1 = s1;
    }

    public void setS2(S2 s2) {
        this.s2 = s2;
    }

    public void setS3(S3 s3) {
        this.s3 = s3;
    }

    public class S1 {
        private int times;

        public int getTimes() {
            return times;
        }

        public void setTimes(int times) {
            this.times = times;
        }
    }

    public class S2 {
        private int duration;

        public int getDuration() {
            return duration;
        }

        public void setDuration(int duration) {
            this.duration = duration;
        }
    }

    public class S3 {
        private String gameId;
        private int times;

        public String getGameId() {
            return gameId;
        }

        public int getTimes() {
            return times;
        }

        public void setGameId(String gameId) {
            this.gameId = gameId;
        }

        public void setTimes(int times) {
            this.times = times;
        }
    }
}
