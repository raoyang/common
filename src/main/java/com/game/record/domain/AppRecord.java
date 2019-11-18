package com.game.record.domain;

import com.game.home.domain.CommParam;

import java.util.List;

public class AppRecord extends CommParam {
    private List<String> content;

    public List<String> getContent() {
        return content;
    }

    public void setContent(List<String> content) {
        this.content = content;
    }
}
