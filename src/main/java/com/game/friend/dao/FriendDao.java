package com.game.friend.dao;

import com.game.friend.domain.BlackData;
import org.apache.ibatis.annotations.Mapper;

import java.sql.Timestamp;
import java.util.List;

@Mapper
public interface FriendDao {

    /** 获取好友列表 **/
    List<Integer> friends(int accountId);

    /** 获取黑名单列表 **/
    List<BlackData> blackList(int accountId);

    /** 添加好友 **/
    int addFriend(int accountId, int friendId, Timestamp time);

    /** 删除关系(好友，黑名单) **/
    int reFriend(int accountId, int friendId);

    /** 拉黑陌生人 **/
    int addBlackList(int accountId, int blackId, Timestamp time);

    /** 拉黑好友 **/
    int friend2Black(int accountId, int friendId, Timestamp time);

    /** 从黑名单移除 **/
    int reBlack(int accountId, int friendId);

    /** 判断是否是好友 **/
    int isFriend(int accountId, int searchId);

    /** 判断是否是黑名单 **/
    int isBlack(int accountId, int searchId);

    /** 移除黑名单判断，如果之前是好友，从黑名单移除的效果就是恢复到好友列表 **/
    int beforeFriend(int accountId, int searchId);

    /** 好友列表移除黑名单 **/
    int black2Friend(int accountId, int friendId, Timestamp time);
}
