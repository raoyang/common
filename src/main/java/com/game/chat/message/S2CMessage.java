package com.game.chat.message;

import com.game.chat.util.JsonUtils;
import com.game.common.vo.ResultVO;
import com.game.netty.message.BaseMessage;
import java.nio.charset.Charset;

public class S2CMessage {

    private short module;

    private short cmd;

    private ResultVO result;

    public static S2CMessage valueOf(short module, short cmd){
        S2CMessage msg = new S2CMessage();
        msg.module = module;
        msg.cmd = cmd;
        return msg;
    }

    public ResultVO getResult() {
        return result;
    }

    public void setResult(ResultVO result) {
        if(result.getData() != null && !(result.getData() instanceof S2CContent)){
            throw new IllegalArgumentException("非法的resultVO，必须遵循data类型为S2CContent");
        }
        this.result = result;
    }

    public BaseMessage serialize(){
        if(result == null){
            return null;
        }
        BaseMessage message = BaseMessage.valueOf(module, cmd);
        message.setBody(JsonUtils.objectToString(result).getBytes(Charset.forName("utf-8")));
        return message;
    }

    public short getModule() {
        return module;
    }

    public short getCmd() {
        return cmd;
    }

}
