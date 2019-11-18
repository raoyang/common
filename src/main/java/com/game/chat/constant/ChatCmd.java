package com.game.chat.constant;

public class ChatCmd {

    /** 客户端发送聊天信息接口 **/
    public static final short ACCOUNT_SENT_CHAT_MSG_C2S = 1;

    /** 服务器广播聊天信息接口 **/
    public static final short BROADCAST_CHAT_MSG_S2C    = 2;

    /** 聊天消息被拒收（被拉黑） **/
    public static final short CHAT_MSG_REFUSE_S2C       = 3;


    public static final short CHAT_MSG_SEND_RESULT      = 4;
}
