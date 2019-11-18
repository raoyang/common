package com.game.solr.dao;

import com.game.solr.elasearch.domain.ESAccount;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ESMapper {

    List<ESAccount> getSolrAccounts(@Param("start") int start, @Param("limit") int limit);
}
