package com.game.common.vo;

import com.game.ranking.domain.RankingMe;

import java.util.List;

/**
 * 查询成功后返回的对象
 */
public class RankingVO {
    // 0 表示成功；1表示失败
    private Integer code;
    private String msg;
    private Integer total;
    private int page;
    private int limit;
    private RankingMe me;
    private List<? extends Object> data;


    /**
     * 创建一个表示查询成功的对象
     *
     * @param total 查询总条数
     * @param data  查询数据
     * @return
     */
    public static RankingVO success(Integer total, int page, int limit, RankingMe me, List<? extends Object> data) {
        RankingVO vo = new RankingVO();
        vo.code = 0;
        vo.msg = "success";
        vo.total = total;
        vo.page = page;
        vo.limit = limit;
        vo.me = me;
        vo.data = data;
        return vo;
    }


    /**
     * 创建一个表示查询失败的对象
     *
     * @param msg 失败原因
     * @return
     */
    public static RankingVO error(String msg, Integer code) {
        RankingVO vo = new RankingVO();
        vo.code = code;
        vo.msg = msg;
        vo.total = 0;

        return vo;
    }

    public static RankingVO checkName(String msg) {
        RankingVO vo = new RankingVO();
        vo.code = 4;
        vo.msg = msg;
        return vo;
    }


    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public List<? extends Object> getData() {
        return data;
    }

    public void setData(List<? extends Object> data) {
        this.data = data;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPage() {
        return page;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public RankingMe getMe() {
        return me;
    }

    public void setMe(RankingMe me) {
        this.me = me;
    }
}
