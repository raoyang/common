package com.game.friend.constant;

public class FriendRedisKey {

    /** 申请列表 **/
    public static final String FRIEND_APPLY = "friend_apply:";
    public static final int FRIEND_APPLY_EXPIRE = 7; //过期时间

    public static final String FRIEND_LIST = "friend_list:";
    public static final int FRIEND_LIST_EXPIRE = 1; //过期时间

    public static final String BLACK_LIST = "black_list:";
    public static final int BLACK_LIST_EXPIRE = 1; //过期时间
}
