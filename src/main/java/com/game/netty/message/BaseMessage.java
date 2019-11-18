package com.game.netty.message;

public class BaseMessage {

    private short module;

    private short cmd;

    /** 包体 **/
    private byte[] body;

    public static BaseMessage valueOf(short module, short cmd){
        BaseMessage msg = new BaseMessage();
        msg.module = module;
        msg.cmd = cmd;
        return msg;
    }

    public byte[] getBody() {
        return body;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }

    public short getModule(){
        return module;
    }

    public short getCmd(){
        return cmd;
    }

    public boolean isValid(){
        if(module > 0 && cmd > 0){
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return "BaseMessage{" +
                "module=" + module +
                ", cmd=" + cmd +
                '}';
    }
}
