package com.game.util;

public class Constant {
    // error code
    public static final int RNT_CODE_SUCCESS = 0;
    public static final int RNT_CODE_PARAM_ERROR = 1;
    public static final int RNT_CODE_NO_DATA = 2;
    public static final int RNT_CODE_SIGN_ERROR = 3;
    public static final int RNT_CODE_EXCEPTION= 4;
    public static final int RNT_CODE_LOGIN_FAIL = 5;
    public static final int RNT_CODE_TOKEN_EXPIRE = 6;
    public static final int RNT_CODE_TOKEN_CHECK_FAIL = 7;
    public static final int RNT_CODE_MATCH_FAIL = 8;
    public static final int RNT_CODE_MATCH_READY_TIMEOUT = 9;
    public static final int RNT_CODE_GAME_RANK_TOO_BIG = 10;

    //account platform
    public static final int ACCOUNT_PLATFORM_SELF = 0;
    public static final int ACCOUNT_PLATFORM_FACEBOOK = 1;
    public static final int ACCOUNT_PLATFORM_GOOGLE = 2;
    public static final int ACCOUNT_PLATFORM_WHATSAPP = 3;
    public static final int ACCOUNT_PLATFORM_AI = 4;
    public static final int ACCOUNT_PLATFORM_TEST = 5;
    public static final int ACCOUNT_PLATFORM_VISITOR = 6;

    public static final short ACCOUNT_CHANGE = 1; //切换账号
    public static final short ACCOUNT_VISITOR_CHANGE = 2; //访客切换账号

    //账号系统返回值
    public static final int ACCOUNT_SYSTEM_RNT_CODE_SUCCESS = 0;
    public static final int ACCOUNT_SYSTEM_RNT_CODE_FAIL = 1;
    public static final int ACCOUNT_SYSTEM_RNT_CODE_REGISTER_NOT_CHECK = 2;
    public static final int ACCOUNT_SYSTEM_RNT_CODE_SUCCESS_REGISTER_CHECK = 3;

    //app type
    public static final String APP_TYPE_GAME_PLATFORM = "101";

    //account app key&token
    public static final String FACEBOOK_TEST_APP_TOKEN = "829220277447594|Y5AWbkhyxWM4tH4TRPL5CIS3kuk"; //test
    public static final String FACEBOOK_APP_TOKEN = "312844072944249|SNM13ayhtLO2zl66jq2cbfFIYlk";
    public static final String GOOGLE_TEST_APP_CLIENT_ID = "1026732482120-8kf5kvvc196sgm62q2amtksb0llnsarc.apps.googleusercontent.com";
    public static final String GOOGLE_APP_CLIENT_ID = "647915114848-9u3iejs803i9riu4gcscbogqjaspm6ap.apps.googleusercontent.com";

    //redis key
    public static final String REDIS_KEY_PREFIX_ACCOUNT_TOKEN = "account_";
    public static final String REDIS_KEY_PREFIX_FAVORITE_GAME = "favorite_game_";
    public static final String REDIS_KEY_PREFIX_GAME_ONLINE = "game_online_";
    public static final String REDIS_KEY_ALL_PRECONF = "all_preconf";
    public static final String REDIS_KEY_ACCOUNT = "game_account:";
    public static final String REDIS_KEY_PREFIX_ROOM_INFO = "room_";
    public static final String REDIS_KEY_FRIEND_INVITE_INFO = "friend_invite_";
    public static final String REDIS_KEY_PREFIX_AROUND_PLAYER = "around_";
    public static final String REDIS_KEY_PREFIX_RANKING = "account_ranking_";
    public static final String REDIS_KEY_HOME_ALBUM_CFG = "home_album_cfg";
    public static final String REDIS_KEY_HOME_BANNER = "home_banner";
    public static final String REDIS_KEY_PREFIX_GRAY_NUM = "gray_game_";
    public static final String REDIS_KEY_TOP_GAME = "top_game";
    public static final String REDIS_KEY_PREFIX_GAME_ALL_INFO = "game:";
    public static final String REDIS_KEY_PREFIX_GAME_SECRET = "game_secret_";
    public static final String REDIS_KEY_GLOBAL_CONF = "g_conf";
    public static final String REDIS_KEY_QUEUE_GAME_START = "queue_game_start";
    public static final String REDIS_KEY_QUEUE_GAME_SCORE = "queue_game_score";
    public static final String REDIS_KEY_QUEUE_GAME_RESULT = "queue_game_result";
    public static final String REDIS_KEY_QUEUE_GAME_AD = "queue_game_ad";
    public static final String REDIS_KEY_QUEUE_APP_RECORD = "queue_app_record";
    public static final String REDIS_KEY_PREFIX_GAME_DATA_CONF = "game:data:conf:";
    /** 游戏大神排行榜 **/
    public static final String REDIS_KEY_GAME_GOD_RANK = "game_god_rank";
    public static final String REDIS_KEY_GAME_FACE = "game:face";
    public static final String REDIS_KEY_PHOTO_WALL = "photo_wall:";
    public static final String REDIS_KEY_PREFIX_LIKE = "like:";
    public static final String REDIS_KEY_PREFIX_COMPLAIN = "complain:";
    public static final String REDIS_KEY_PREFIX_BADGE = "badge:";
    public static final String REDIS_KEY_AI_GOD_INFO = "ai_god";
    public static final String REDIS_KEY_PREFIX_ACTIVITY_TIMES = "activity:times:";
    public static final String REDIS_KEY_PREFIX_ACTIVITY_LOGIN_FIRST = "activity:login:";

    public static final String REDIS_KEY_PREFIX_FIREBASE_TOKEN = "FBT_";
    public static final String REDIS_KEY_PREFIX_FIREBASE_ID = "FBI_";
    public static final String REDIS_KEY_PREFIX_ACCOUNT_GAME_RESULT = "game::";
    public static final String REDIS_KEY_PREFIX_GAME_AD_CONFIG = "game:ad:";

    //net type
    public static final int NET_TYPE_ALL = 0; //全网
    public static final int NET_TYPE_WIFI = 1; //wifi
    public static final int NET_TYPE_DATA = 2; //data

    //banner type
    public static final int BANNER_TYPE_GAME_RECOMMEND = 1; //游戏推荐
    public static final int BANNER_TYPE_H5 = 2; //H5


    //proxy conf
    public static final String PROXY_HOST = "10.16.13.18";
    public static final int PROXY_PORT = 8080;

    //record event type
    public static final int RECORD_EVENT_GAME_START = 1;
    public static final int RECORD_EVENT_GAME_FINISH = 2;
    public static final int RECORD_EVENT_RANKING_REPORT = 3;
    public static final int RECORD_EVENT_GAME_RESULT = 4;
    public static final int RECORD_EVENT_GAME_ACTIVITY = 5;
    //record share type
    public static final int RECORD_EVENT_ACTIVITY_SHARE = 2;

    //record game result
    public static final int RECORD_EVENT_GAME_RESULT_WIN = 2;
    public static final int RECORD_EVENT_GAME_RESULT_DRAW = 3;
    public static final int RECORD_EVENT_GAME_RESULT_LOSS = 4;

    //game result hash key
    public static final String TOTAL_COUNT_KEY = "total";
    public static final String TOTAL_WIN_COUNT_KEY = "win";
    public static final String IS_P_WIN_KEY = "isPWin";
    public static final String IS_P_LOSS_KEY = "isPLoss";

    //ranking type
    public static final int RANKING_TYPE_SCORE = 2;
    public static final int RANKING_TYPE_LEVEL = 1;
    public static final int RANKING_CATEGORY_GLOBAL = 1;
    public static final int RANKING_CATEGORY_FRIEND = 2;
    public static final int RANKING_MAX = 100;
    public static final int RANKING_GAME_MAX = 9999;

    //redis key expire
    public static final int REDIS_KEY_ACCOUNT_EXPIRE = 24;

    //分享
    public static final String SHARE_APP_NAME = "";
    public static final String SHARE_APP_ICON = "";
    public static final String SHARE_APP_URL = "";

    //游戏资源状态
    public static final short GAME_STATUS_DEFAULT = 0; //默认下线状态
    public static final short GAME_STATUS_TEST = 1;
    public static final short GAME_STATUS_ONLINE = 2;

    //多语言：默认
    public static final String LANGUAGE_DEFAULT = "en";

    //游戏统一接口类型
    public static final short GAME_DATA_TYPE_FACE = 1;
    public static final short GAME_DATA_TYPE_CONFIG = 2;
    public static final short GAME_DATA_TYPE_RANKING = 3;

    //sex
    public static final int ACCOUNT_SEX_FEMALE = 1;
    public static final int ACCOUNT_SEX_MALE = 2;

    //account type
    public static final short ACCOUNT_TYPE_FORMAL = 1;
    public static final short ACCOUNT_TYPE_VISITOR = 2;

}
