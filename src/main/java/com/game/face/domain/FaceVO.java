package com.game.face.domain;

public class FaceVO {
    private int id;
    private String url;
    private int property;

    public static FaceVO valueOf(Face face) {
        FaceVO faceVO = new FaceVO();
        faceVO.setId(face.getId());
        faceVO.setUrl(face.getUrl());
        faceVO.setProperty(face.getProperty());

        return faceVO;
    }

    public int getId() {
        return id;
    }

    public String getUrl() {
        return url;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getProperty() {
        return property;
    }

    public void setProperty(int property) {
        this.property = property;
    }
}
