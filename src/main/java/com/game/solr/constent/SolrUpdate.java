package com.game.solr.constent;

/***
 * solr原子操作枚举
 * 文档：http://lucene.apache.org/solr/guide/7_7/updating-parts-of-documents.html#field-storage
 */
public enum SolrUpdate {

    /** Set or replace the field value(s) with the specified value(s),
     *  or remove the values if 'null'
     *  or empty list is specified as the new value.
     *  May be specified as a single value, or as a list for multiValued fields. **/
    SET("set"),

    /** Adds the specified values to a multiValued field.
     *  May be specified as a single value, or as a list. **/
    ADD("add"),

    /** Adds the specified values to a multiValued field,
     * only if not already present.
     * May be specified as a single value, or as a list. **/
    ADD_DISTINCT("add-distinct"),

    /** Removes (all occurrences of) the specified values from a multiValued field.
     * May be specified as a single value, or as a list. **/
    REMOVE("remove"),

    /** Removes all occurrences of the specified regex from a multiValued field.
     * May be specified as a single value, or as a list. **/
    REMOVE_REGEX("removeregex"),

    /** Increments a numeric value by a specific amount.
     *  Must be specified as a single numeric value. **/
    INCREMENT("inc");

    private String name;

    SolrUpdate(String name){
        this.name = name;
    }

    public String getName(){
        return name;
    }
}
