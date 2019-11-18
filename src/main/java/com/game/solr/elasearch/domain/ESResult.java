package com.game.solr.elasearch.domain;

import java.util.List;

/***
 * es的返回结果
 */
public class ESResult {

    private int took;

    private boolean timed_out;

    private Shards _shards;

    private Hits1 hits;

    @Override
    public String toString() {
        return "ESResult{" +
                "took=" + took +
                ", timed_out=" + timed_out +
                ", _shards=" + _shards +
                ", hits=" + hits +
                '}';
    }

    public int getTook() {
        return took;
    }

    public void setTook(int took) {
        this.took = took;
    }

    public boolean isTimed_out() {
        return timed_out;
    }

    public void setTimed_out(boolean timed_out) {
        this.timed_out = timed_out;
    }

    public Shards get_shards() {
        return _shards;
    }

    public void set_shards(Shards _shards) {
        this._shards = _shards;
    }

    public Hits1 getHits() {
        return hits;
    }

    public void setHits(Hits1 hits) {
        this.hits = hits;
    }

    private static class Shards{
        private int total;
        private int successful;
        private int skipped;
        private int failed;

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }

        public int getSuccessful() {
            return successful;
        }

        public void setSuccessful(int successful) {
            this.successful = successful;
        }

        public int getSkipped() {
            return skipped;
        }

        public void setSkipped(int skipped) {
            this.skipped = skipped;
        }

        public int getFailed() {
            return failed;
        }

        public void setFailed(int failed) {
            this.failed = failed;
        }

        @Override
        public String toString() {
            return "Shards{" +
                    "total=" + total +
                    ", successful=" + successful +
                    ", skipped=" + skipped +
                    ", failed=" + failed +
                    '}';
        }
    }

    public static class Hits1{
        private int total;
        private double max_score;
        private List<Hits2> hits;

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }

        public double getMax_score() {
            return max_score;
        }

        public void setMax_score(double max_score) {
            this.max_score = max_score;
        }

        public List<Hits2> getHits() {
            return hits;
        }

        public void setHits(List<Hits2> hits) {
            this.hits = hits;
        }

        @Override
        public String toString() {
            return "Hits1{" +
                    "total=" + total +
                    ", max_score=" + max_score +
                    ", hits=" + hits +
                    '}';
        }
    }

    public static class Hits2{
        private String _index;
        private String _type;
        private String _id;
        private double _score;
        private Source _source;

        public String get_index() {
            return _index;
        }

        public void set_index(String _index) {
            this._index = _index;
        }

        public String get_type() {
            return _type;
        }

        public void set_type(String _type) {
            this._type = _type;
        }

        public String get_id() {
            return _id;
        }

        public void set_id(String _id) {
            this._id = _id;
        }

        public double get_score() {
            return _score;
        }

        public void set_score(double _score) {
            this._score = _score;
        }

        public Source get_source() {
            return _source;
        }

        public void set_source(Source _source) {
            this._source = _source;
        }

        @Override
        public String toString() {
            return "Hits2{" +
                    "_index='" + _index + '\'' +
                    ", _type='" + _type + '\'' +
                    ", _id='" + _id + '\'' +
                    ", _score=" + _score +
                    ", _source=" + _source +
                    '}';
        }
    }

    public static class Source{
        private int accountId;
        private String nickName;

        public int getAccountId() {
            return accountId;
        }

        public void setAccountId(int accountId) {
            this.accountId = accountId;
        }

        public String getNickName() {
            return nickName;
        }

        public void setNickName(String nickName) {
            this.nickName = nickName;
        }
    }
}
