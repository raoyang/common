package com.game.gconf.domain;

import com.game.preconf.domain.PreConfVO;

import java.util.List;

public class GConfVO {
    private List<GConf> global;
    private List<PreConfVO> download;

    public List<GConf> getGlobal() {
        return global;
    }

    public List<PreConfVO> getDownload() {
        return download;
    }

    public void setGlobal(List<GConf> global) {
        this.global = global;
    }

    public void setDownload(List<PreConfVO> download) {
        this.download = download;
    }
}
