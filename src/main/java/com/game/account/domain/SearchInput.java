package com.game.account.domain;

import com.game.home.domain.CommParam;

public class SearchInput extends CommParam {

    private String input;

    private int index; //分页下标

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }
}
