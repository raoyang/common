package com.game.solr.elasearch.domain;

public class ESUpdateVO {
    private DocVO doc;

    public void setNickName(String nickName) {
        doc.setNickName(nickName);
    }

    public DocVO getDoc() {
        return doc;
    }

    public void setDoc(DocVO doc) {
        this.doc = doc;
    }

    public class DocVO {
        private String nickName;

        public void setNickName(String nickName) {
            this.nickName = nickName;
        }

        public String getNickName() {
            return nickName;
        }
    }
}
