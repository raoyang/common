package com.game.common.vo;

/**
 * 新增成功后返回的对象
 */
public class ResultVO {
    private Integer code;
    private String msg;
    private Object data;

    public static ResultVO success(Object obj) {
        ResultVO vo = new ResultVO();
        vo.setCode(0);
        vo.setMsg("success");
        vo.setData(obj);

        return vo;
    }

    public static ResultVO success() {
        return success(null);
    }

    public static ResultVO error(String msg) {
        ResultVO vo = new ResultVO();
        vo.setCode(1);
        vo.setMsg(msg);
        return vo;
    }

    public static ResultVO error(String msg, Integer code) {
        ResultVO vo = new ResultVO();
        vo.setCode(code);
        vo.setMsg(msg);
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

    public void setData(Object data) {
        this.data = data;
    }

    public Object getData() {
        return data;
    }
}
