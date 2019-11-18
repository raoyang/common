package com.game.solr.elasearch.domain;

public class Search {

    private Query query;
    private int from;
    private int size;

    public Search(){
        query = new Query();
    }

    public void setInput(String input){
        query.getMatch().getNickName().setQuery(input);
    }

    public Query getQuery() {
        return query;
    }

    public void setQuery(Query query) {
        this.query = query;
    }

    public int getFrom() {
        return from;
    }

    public int getSize() {
        return size;
    }

    public void setFrom(int from) {
        this.from = from;
    }

    public void setSize(int size) {
        this.size = size;
    }

    private static class Query{
        private Match match;
        public Query(){
            match = new Match();
        }
        public Match getMatch() {
            return match;
        }
        public void setMatch(Match match) {
            this.match = match;
        }
    }

    private static class Match{
        private NickName nickName;
        public Match(){
            nickName = new NickName();
        }
        public NickName getNickName() {
            return nickName;
        }
        public void setNickName(NickName nickName) {
            this.nickName = nickName;
        }
    }

    private static class NickName{
        private String query;
        private int fuzziness = 1; //编辑距离，默认设置为2，该参数值只能设置为0，1，2
        public String getQuery() {
            return query;
        }
        public void setQuery(String query) {
            this.query = query;
        }
        public int getFuzziness() {
            return fuzziness;
        }
        public void setFuzziness(int fuzziness) {
            this.fuzziness = fuzziness;
        }
    }
}
