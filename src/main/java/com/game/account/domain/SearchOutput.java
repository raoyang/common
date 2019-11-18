package com.game.account.domain;

import java.util.List;

public class SearchOutput {

    private int index;

    private int length;

    private List<AccountPanelInfo> infos;

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public List<AccountPanelInfo> getInfos() {
        return infos;
    }

    public void setInfos(List<AccountPanelInfo> infos) {
        this.infos = infos;
    }

    @Override
    public String toString() {
        return "SearchOutput{" +
                "index=" + index +
                ", length=" + length +
                ", infos=" + infos +
                '}';
    }
}
