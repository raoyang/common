package com.game.solr.domain;

import org.apache.solr.client.solrj.beans.Field;

/***
 * solr存储基类
 * solr中，除了id和_version_，其它的全是List
 */
public abstract class SolrBaseDomain {

    @Field("id")
    public long id; //solr主键

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    /** 查询的关键词 **/
    public abstract String getKeywords();
}
