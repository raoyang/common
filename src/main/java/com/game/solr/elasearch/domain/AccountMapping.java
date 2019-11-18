package com.game.solr.elasearch.domain;

public class AccountMapping {
    private Proper properties;

    public AccountMapping build() {
        AccountMapping mapping = new AccountMapping();
        Proper proper = new Proper();
        proper.setAccountId(new CommField("long"));
        proper.setCreateTime(new CommField("long"));
        proper.setNickName(new CommField("text"));
        proper.setTime(new CommField("text"));
        mapping.setProperties(proper);

        return mapping;
    }

    public Proper getProperties() {
        return properties;
    }

    public void setProperties(Proper properties) {
        this.properties = properties;
    }

    public class Proper {
        private CommField accountId;
        private CommField createTime;
        private CommField nickName;
        private CommField time;

        public CommField getAccountId() {
            return accountId;
        }

        public CommField getCreateTime() {
            return createTime;
        }

        public CommField getNickName() {
            return nickName;
        }

        public CommField getTime() {
            return time;
        }

        public void setAccountId(CommField accountId) {
            this.accountId = accountId;
        }

        public void setCreateTime(CommField createTime) {
            this.createTime = createTime;
        }

        public void setNickName(CommField nickName) {
            this.nickName = nickName;
        }

        public void setTime(CommField time) {
            this.time = time;
        }
    }

    public class CommField {
        private String type;

        public void setType(String type) {
            this.type = type;
        }

        public String getType() {
            return type;
        }

        CommField (String type) {
            this.type = type;
        }
    }

    public class TextIKField extends CommField {
        private String analyzer;
        private String search_analyzer;

        TextIKField (String type, String analyzer, String search_analyzer) {
            super(type);
            this.analyzer = analyzer;
            this.search_analyzer = search_analyzer;
        }

        public String getAnalyzer() {
            return analyzer;
        }

        public String getSearch_analyzer() {
            return search_analyzer;
        }

        public void setAnalyzer(String analyzer) {
            this.analyzer = analyzer;
        }

        public void setSearch_analyzer(String search_analyzer) {
            this.search_analyzer = search_analyzer;
        }
    }
}
