package com.game.netty.constant;

public class MatchCmd {
    //匹配请求
    public static final short MATCH_C2S = 1;
    //匹配请求结果
    public static final short MATCH_RESULT_S2C = 2;
    //自动准备
    public static final short MATCH_READY_C2S = 3;
    //自动准备结果
    public static final short MATCH_READY_RESULT_S2C = 4;
    //匹配请求取消
    public static final short MATCH_CANCEL_C2S = 5;
    //群游戏邀请
    public static final short BROADCAST_GAME_INVITE_S2C = 6;
    //群游戏加入邀请
    public static final short BROADCAST_ACCEPT_GAME_INVITE_C2S = 7;

    //换个对手
    public static final short MATCH_CHANGE_OPPONENT_C2S = 20;
    //不服再来(再来一局)
    public static final short MATCH_ONCE_AGAIN_C2S = 21;
    //广播再来一局
    public static final short MATCH_ONCE_AGAIN_S2C = 22;
    //换个游戏
    public static final short MATCH_CHANGE_ANOTHER_GAME_C2S = 23;
    //广播换个游戏
    public static final short MATCH_CHANGE_ANOTHER_GAME_S2C = 24;
    //退出房间
    public static final short MATCH_EXIT_ROOM_C2S = 25;
    //加入邀请
    public static final short MATCH_ENTER_INVITE_C2S = 26;
    //用户离开房间广播
    public static final short MATCH_EXIT_ROOM_S2C = 27;
    //拒绝邀请
    public static final short MATCH_REFUSE_INVITE_C2S = 28;
    //拒绝邀请下发
    public static final short MATCH_REFUSE_INVITE_S2C = 29;
    //发出再来一局的取消
    public static final short MATCH_AGAIN_CANCEL_C2S = 37;
    //再来一局取消下发
    public static final short MATCH_AGAIN_CANCEL_S2C = 38;

    //约战好友
    public static final short INVITE_FRIEND_MATCH_C2S = 30;
    //约战下发
    public static final short INVITE_FRIEND_MATCH_S2C = 31;
    //取消约战
    public static final short INVITE_FRIEND_MATCH_CANCEL_C2S = 32;
    //约战取消下发
    public static final short INVITE_FRIEND_MATCH_CANCEL_S2C = 33;
    //好友加入约战
    public static final short FRIEND_ENTER_MATCH_C2S=34;
    //好友拒绝约战
    public static final short FRIEND_REFUSE_MATCH_C2S = 35;
    //拒绝约战转发
    public static final short FRIEND_REFUSE_MATCH_S2C = 36;

    //主动约战被拒绝因为黑名单
    public static final short INVITE_FREND_REFUSE_BECAUSE_BLACK = 39;

    //通用转发通道
    public static final short COMMON_TRANSFER_PIPLINE_C2S = 40;
    //通用转发通道
    public static final short COMMON_TRANSFER_PIPLINE_S2C = 41;

}
