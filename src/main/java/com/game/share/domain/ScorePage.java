package com.game.share.domain;

public class ScorePage {
    private String desc;
    private String score;
    private String buttonText;
    private String extDesc;
    private Object games;

    public String getDesc() {
        return desc;
    }

    public String getScore() {
        return score;
    }

    public String getButtonText() {
        return buttonText;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public void setButtonText(String buttonText) {
        this.buttonText = buttonText;
    }

    public Object getGames() {
        return games;
    }

    public void setGames(Object games) {
        this.games = games;
    }

    public void setExtDesc(String extDesc) {
        this.extDesc = extDesc;
    }

    public String getExtDesc() {
        return extDesc;
    }
}
