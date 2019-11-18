package com.game.login.service;

import com.game.account.dao.AccountMapper;
import com.game.account.domain.AccountInfo;
import com.game.account.domain.GoogleAuthVO;
import com.game.account.domain.VisitorVO;
import com.game.chat.ChatDispatcher;
import com.game.chat.event.impl.LoginEvent;
import com.game.chat.message.S2CMessage;
import com.game.chat.util.JsonUtils;
import com.game.common.vo.ResultVO;
import com.game.account.domain.FaceBookAuthVO;
import com.game.home.domain.CommParam;
import com.game.firebase.service.FirebaseService;
import com.game.logger.ExceptionLogger;
import com.game.login.dao.LoginMapper;
import com.game.login.domain.*;
import com.game.match.service.MatchService;
import com.game.netty.constant.LoginCmd;
import com.game.netty.constant.Module;
import com.game.netty.manager.ChannelManager;
import com.game.netty.message.BaseMessage;
import com.game.netty.message.LoginMessage;
import com.game.netty.user.MessageWorker;
import com.game.solr.domain.SolrAccount;
import com.game.solr.service.ESService;
import com.game.solr.service.SolrService;
import com.game.tool.dao.AIAccountMapper;
import com.game.util.CommonFunUtil;
import com.game.util.Constant;
import com.game.util.HttpUtil;
import com.game.util.StringUtil;
import com.game.util.account.AccountUtil;
import com.game.util.pic.MD5Util;
import com.google.gson.Gson;
import io.netty.channel.ChannelFuture;
import org.apache.http.util.TextUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class LoginService {
    private static final Logger logger = LoggerFactory.getLogger(LoginService.class);

    private static LoginService instance = null;
    private static Map<String, Object> locks = new ConcurrentHashMap<>();

    @Autowired
    LoginMapper loginMapper;
    @Autowired
    AccountMapper accountMapper;
    @Autowired
    AIAccountMapper aiAccountMapper;
    @Autowired
    ExecutorService threadHttp;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private ESService esService;

    @Autowired
    private FirebaseService firebaseService;

    @Value("${facebook.account.auth.url}")
    private String fbAuthUrl;
    @Value("${google.account.auth.url}")
    private String googleAuthUrl;
    @Value("${account.token.expire.time}")
    private int accountTokenExpireTime;
    @Value("${account.token.expire.test.time}")
    private int accountTokenExpireTestTime;
    @Value("${is.proxy}")
    private int isProxy;
    @Value("${test.token.account}")
    private String testAccount;
    @Value("${avatar.default.female}")
    private String femaleAvatar;
    @Value("${avatar.default.male}")
    private String maleAvatar;

    @PostConstruct
    private void init(){
        instance = this;
    }

    public static LoginService getInstance(){
        return instance;
    }

    //业务系统
    @Transactional(rollbackFor = Exception.class)
    public ResultVO login(Account account, String appId) {
        int accountId = account.getAccountId();
        ValueOperations valueOperations = redisTemplate.opsForValue();

        RegisterVO registerVO = null;
        LoginVO loginVO = new LoginVO();
        AccountInfo accountInfo = null;
        if (account.getgToken().equals("")
                || account.getChangeAccount() == Constant.ACCOUNT_CHANGE
                || account.getChangeAccount() == Constant.ACCOUNT_VISITOR_CHANGE) {
            logger.info("gToken is null, do register");
            if (account.getToken().equals("")
                    || account.getOpenId().equals("")
            ) {
                return ResultVO.error("param error", Constant.RNT_CODE_PARAM_ERROR);
            }

            registerVO = register(account, appId);
            if (registerVO == null) {
                logger.info("check token fail");
                return ResultVO.error("check token fail", Constant.RNT_CODE_TOKEN_CHECK_FAIL);
            }

            BeanUtils.copyProperties(registerVO.getAccountInfo(), loginVO);
            if(registerVO.getAccountInfo().getPhotoWall() != null && !registerVO.getAccountInfo().getPhotoWall().equals("")){
                loginVO.setPhotoWall(JsonUtils.stringToList(registerVO.getAccountInfo().getPhotoWall(), String[].class));
            }
            loginVO.setAccountId(registerVO.getAccountInfo().getId());
            loginVO.setIsNew(registerVO.getIsNew());

            //已注册用户
            if (registerVO.getCode() == Constant.ACCOUNT_SYSTEM_RNT_CODE_REGISTER_NOT_CHECK
                    || registerVO.getCode() == Constant.ACCOUNT_SYSTEM_RNT_CODE_SUCCESS_REGISTER_CHECK) {
                // 查询登录的设备是否一致
                //如果一致，执行原逻辑
                //如果不一致，直接校验三方token。成功则登录成功，并推消息给已登录设备；失败则登录失败
                Object token = valueOperations.get(Constant.REDIS_KEY_PREFIX_ACCOUNT_TOKEN + appId + "_"  + registerVO.getAccountInfo().getId());
                if (token == null) {
                    logger.info("already login user: token expire, account id:" + registerVO.getAccountInfo().getId());
                    //生成平台token
                    String gToken = genToken(account.getOpenId(),account.getPlatform(), appId);
                    loginVO.setgToken(gToken);
                    setRedisToken(appId, registerVO.getAccountInfo().getId(), account.getUuid(), account.getClientId(), gToken);

                    /**
                     *  add by tangmin 2019.07.06
                     *  注册并且登录成功之后，记录该用户的firebase token
                     */
                    firebaseService.insertOrUpdateFirebaseTokenAsync(loginVO.getAccountId(), account.getFirebaseId(), account.getFirebaseToken());
                    //end added

                    return ResultVO.success(loginVO);
                }

                List<String> tokenAndDev = Arrays.asList(((String)token).split("_"));
                if (tokenAndDev.size() == 3) {
                    String devId = tokenAndDev.get(1);
                    if (devId.equals(account.getUuid())) {
                        logger.info("devId same, accountId:" + registerVO.getAccountInfo().getId());
                    } else {
                        if (registerVO.getCode() == Constant.ACCOUNT_SYSTEM_RNT_CODE_REGISTER_NOT_CHECK) {
                            if (! authAccountToken(account.getOpenId(), account.getToken(), account.getPlatform(), appId)) {
                                logger.info("check token fail");

                                /** TODO 是否可以删除？
                                 * add by tangmin 2019.07.06
                                 * 登录失败之后，删除该用户的firebase token
                                 */
                                firebaseService.deleteFirebaseTokenAsync(loginVO.getAccountId());
                                //end added

                                return ResultVO.error("check token fail", Constant.RNT_CODE_TOKEN_CHECK_FAIL);
                            }
                        }
                        //设备不一致，推消息：异步
                        /*String clientId = account.getClientId();
                        executorService.execute(new Runnable() {
                            @Override
                            public void run() {
                                Map<String, String> param = new HashMap<>();
                                param.put("type", "1");

                                Gson gson = new Gson();
                                String msg = gson.toJson(param);
                                PushUtil.sendClientIdMsg(msg, clientId);
                            }
                        });*/
                    }
                }
            }

            //生成平台token
            String gToken = genToken(account.getOpenId(),account.getPlatform(), appId);
            setRedisToken(appId, registerVO.getAccountInfo().getId(), account.getUuid(), account.getClientId(), gToken);
            loginVO.setgToken(gToken);

            /**
             *  add by tangmin 2019.07.06
             *  注册并且登录成功之后，记录该用户的firebase token
             */
            firebaseService.insertOrUpdateFirebaseTokenAsync(registerVO.getAccountInfo().getId(), account.getFirebaseId(), account.getFirebaseToken());
            //end added

            return ResultVO.success(loginVO);
        }

        //查询缓存是否过期
        Object token = valueOperations.get(Constant.REDIS_KEY_PREFIX_ACCOUNT_TOKEN + appId + "_"  + accountId);
        if (token == null) {
            logger.info("token expire, account id:" + accountId);

            //add by tangmin 2019.07.06  登录失败之后，删除该用户的firebase token
            firebaseService.deleteFirebaseTokenAsync(accountId);

            return ResultVO.error("token expire", Constant.RNT_CODE_TOKEN_EXPIRE);
        }

        String tokens = account.getgToken() + "_" + account.getUuid() + "_" + account.getClientId();
        if (! tokens.equals(token)) {
            logger.info("token diff, account id:" + accountId + ", tokens:" + tokens);

            //add by tangmin 2019.07.06  登录失败之后，删除该用户的firebase token
            firebaseService.deleteFirebaseTokenAsync(accountId);

            return ResultVO.error("token diff", Constant.RNT_CODE_TOKEN_EXPIRE);
        }

        accountInfo = getAccountInfo(accountId);
        BeanUtils.copyProperties(accountInfo, loginVO);
        loginVO.setAccountId(accountId);
        loginVO.setgToken(account.getgToken());
        if(accountInfo.getPhotoWall() != null && !accountInfo.getPhotoWall().equals("")){
            loginVO.setPhotoWall(JsonUtils.stringToList(accountInfo.getPhotoWall(), String[].class));
        }
        /**
         *  add by tangmin 2019.07.06
         *  注册并且登录成功之后，记录该用户的firebase token
         */
        firebaseService.insertOrUpdateFirebaseTokenAsync(loginVO.getAccountId(), account.getFirebaseId(), account.getFirebaseToken());
        //end added

        return ResultVO.success(loginVO);
    }

    //账号系统
    @Transactional(rollbackFor = Exception.class)
    public RegisterVO register(Account account, String appId) {
        RegisterVO registerVO = new RegisterVO();

        Object openIdLock = getLock(account.getOpenId());
        synchronized (openIdLock) {
            AppAccountLogin appAccountLogin = new AppAccountLogin();
            AccountInfo accountInfo = accountMapper.queryByOpenId(account.getOpenId(), account.getPlatform());
            if (accountInfo == null) {
                //校验账号信息
                if (! authAccountToken(account.getOpenId(), account.getToken(), account.getPlatform(), appId)) {
                    registerVO.setCode(Constant.ACCOUNT_SYSTEM_RNT_CODE_FAIL);
                    return null;
                }

                //记录新用户
                AccountInfo info = new AccountInfo();
                BeanUtils.copyProperties(account, info);
                info.setType(Constant.ACCOUNT_TYPE_FORMAL);
                //昵称、地址存储字节
                info.setNickNameByte(account.getNickName().getBytes(StandardCharsets.UTF_8));
                info.setAddressByte(account.getAddress().getBytes(StandardCharsets.UTF_8));
                if(account.getSignature() != null){
                    info.setSignatureByte(account.getSignature().getBytes(StandardCharsets.UTF_8));
                }

                if (account.getChangeAccount() == Constant.ACCOUNT_VISITOR_CHANGE) {
                    AccountInfo visitorInfo = accountMapper.queryVisitorAccount(account.getUuid(), Constant.ACCOUNT_TYPE_VISITOR);
                    //不存在访客账号，仍然注册成功，但是没有历史数据
                    if (visitorInfo == null) {
                        logger.warn("no exist visitor account, uuid:" + account.getUuid());
                        accountMapper.saveAccount(info);
                    } else {
                        info.setId(visitorInfo.getId());
                        if(visitorInfo.getPhotoWall() != null && !visitorInfo.getPhotoWall().equals("")){
                            info.setPhotoWall(visitorInfo.getPhotoWall()); //增加照片墙显示
                        }
                        accountMapper.updateAccountInfo(info);
                    }

                } else {
                    accountMapper.saveAccount(info);
                }

                cacheAccount(info);
                //放入solr系统
                esService.saveAccount2Solr(info);
                //记录该应用用户的登录情况
                appAccountLogin.setAppId(Integer.parseInt(appId));
                appAccountLogin.setAccountId(info.getId());
                appAccountLogin.setLoginTime(CommonFunUtil.getNowDay(new Date()));
                appAccountLogin.setExpireTime(CommonFunUtil.getNowDay(CommonFunUtil.getDateAfter(new Date(), accountTokenExpireTime)));
                loginMapper.saveAppAccountLogin(appAccountLogin);

                registerVO.setAccountInfo(info);
                registerVO.setCode(0);
                registerVO.setIsNew(1);


                return registerVO;
            }

            //查询平台账号对应的该应用是否已注册
            AppAccountLogin serAppAccount = loginMapper.queryAppAndAccountId(accountInfo.getId(), Integer.parseInt(appId));
            if (serAppAccount == null) {
                //校验不同应用相同账号的登录
                if (! authAccountToken(account.getOpenId(), account.getToken(), account.getPlatform(),appId)) {
                    registerVO.setCode(Constant.ACCOUNT_SYSTEM_RNT_CODE_FAIL);

                    /**
                     * add by tangmin 2019.07.06
                     * 登录失败之后，删除该用户的firebase token
                     */
                    firebaseService.deleteFirebaseTokenAsync(accountInfo.getId());
                    //end added

                    return null;
                }

                //记录该应用用户的登录情况
                appAccountLogin.setAppId(Integer.parseInt(appId));
                appAccountLogin.setAccountId(accountInfo.getId());
                appAccountLogin.setLoginTime(CommonFunUtil.getNowDay(new Date()));
                appAccountLogin.setExpireTime(CommonFunUtil.getNowDay(CommonFunUtil.getDateAfter(new Date(), accountTokenExpireTime)));
                loginMapper.saveAppAccountLogin(appAccountLogin);

                registerVO.setAccountInfo(accountInfo);
                registerVO.setCode(0);

                return registerVO;
            }

            //已登录，判断是否已过期
            String accountExpireTime = serAppAccount.getExpireTime();
            if (accountExpireTime.compareTo(CommonFunUtil.getNowDay()) <= 0
                    || account.getChangeAccount() == Constant.ACCOUNT_CHANGE) { //过期或者切换账号
                if (! authAccountToken(account.getOpenId(), account.getToken(), account.getPlatform(), appId)) {
                    registerVO.setCode(Constant.ACCOUNT_SYSTEM_RNT_CODE_FAIL);

                    /**
                     * add by tangmin 2019.07.06
                     * 登录失败之后，删除该用户的firebase token
                     */
                    firebaseService.deleteFirebaseTokenAsync(accountInfo.getId());
                    //end added

                    return null;
                }

                //更新登录情况
                serAppAccount.setLoginTime(CommonFunUtil.getNowDay(new Date()));
                serAppAccount.setExpireTime(CommonFunUtil.getNowDay(CommonFunUtil.getDateAfter(new Date(), accountTokenExpireTime)));
                loginMapper.updateAppAccountLogin(serAppAccount);

                registerVO.setCode(Constant.ACCOUNT_SYSTEM_RNT_CODE_SUCCESS_REGISTER_CHECK);
            } else {
                registerVO.setCode(Constant.ACCOUNT_SYSTEM_RNT_CODE_REGISTER_NOT_CHECK);
            }

            registerVO.setAccountInfo(accountInfo);
        }

        return registerVO;
    }

    private Boolean authAccountToken(String openId, String token, int platform, String appId) {
        //获取不同应用的app token
        String accountAppToken = "";
        Gson gson = new Gson();

        //Facebook
        if (platform == Constant.ACCOUNT_PLATFORM_FACEBOOK) {
            if (appId.equals(Constant.APP_TYPE_GAME_PLATFORM)) {
                accountAppToken = Constant.FACEBOOK_APP_TOKEN;
            } else {
                logger.info("facebook auth token: appId no exist");
                return false;
            }

            Map<String, String> params = new HashMap<>();
            params.put("input_token", token);
            params.put("access_token", accountAppToken);
            String result = HttpUtil.sendGet(fbAuthUrl, params, "UTF-8", isProxy);
            logger.info("auth facebook result:" + result);
            if (result.equals("")) {
                return false;
            }

            FaceBookAuthVO authVO = null;
            try {
                authVO = gson.fromJson(result, FaceBookAuthVO.class);
            } catch (Exception e) {
                logger.info("auth facebook exception, msg:" + e.getMessage());
                return false;
            }

            if (authVO == null) {
                logger.info("auth token: authVO is null");
                return false;
            }

            if (authVO.getError() != null) {
                logger.error("auth facebook token err, msg:" + authVO.getError().getMessage());
                return false;
            }

            if (! authVO.getData().getIs_valid()) {
                logger.info("auth token: is_valid is false");
                return false;
            }

            if (! authVO.getData().getUser_id().equals(openId)) {
                logger.info("auth token: open_id diff, user_id:" + authVO.getData().getUser_id());
                return false;
            }

            return true;
        }

        //Google
        if (platform == Constant.ACCOUNT_PLATFORM_GOOGLE) {
            Map<String, String> params = new HashMap<>();
            params.put("id_token", token);
            String result = HttpUtil.sendGet(googleAuthUrl, params, "UTF-8", isProxy);
            logger.info("auth google result:" + result);
            if (result.equals("")) {
                return false;
            }

            GoogleAuthVO googleAuthVO = null;
            try {
                googleAuthVO = gson.fromJson(result, GoogleAuthVO.class);
            } catch (Exception e) {
                logger.info("auth Google exception, msg:" + e.getMessage());
                return false;
            }

            //check error
            if (! TextUtils.isEmpty(googleAuthVO.getError())) {
                logger.error("auth Google token error:" + googleAuthVO.getError());
                return false;
            }

            //check aud
            if (! googleAuthVO.getAud().equals(Constant.GOOGLE_APP_CLIENT_ID)) {
                logger.info("auth google account, aud:" + googleAuthVO.getAud());
                return false;
            }

            //check exp
            String tokenExp = googleAuthVO.getExp() + "000";
            String nowTime = String.valueOf(new Date().getTime());
            if (tokenExp.compareTo(nowTime) < 0) {
                logger.info("auth google account, now exp:" + tokenExp);
                return false;
            }

            if (! openId.equals(googleAuthVO.getSub())) {
                logger.info("auth google account, open id diff:" + googleAuthVO.getSub());
                return false;
            }

            return true;
        }

        logger.info("no support platform:" + platform);

        return false;
    }

    //账号系统
    public AccountInfo queryAccountInfo(int accountId) {
        return accountMapper.queryById(accountId);
    }
    public AccountInfo queryAIAccountInfo(int accountId) {
        return aiAccountMapper.queryById(accountId);
    }

    //业务系统
    private String genToken(String openId, int platform, String appId) {
        String md5 = MD5Util.getMD5String(openId + platform + appId);
        Long time = new Date().getTime();
        return String.valueOf(appId) + md5 + String.valueOf(time);
    }

    public Boolean setRedisToken(String appId, int accountId, String devId, String clientId, String gToken) {
        ValueOperations valueOperations = redisTemplate.opsForValue();

        //测试使用
        if (testAccount.contains(String.valueOf(accountId))) {
            valueOperations.set(Constant.REDIS_KEY_PREFIX_ACCOUNT_TOKEN + appId + "_"  + accountId, gToken + "_" + devId + "_" + clientId, accountTokenExpireTestTime, TimeUnit.MINUTES);
        } else {
            valueOperations.set(Constant.REDIS_KEY_PREFIX_ACCOUNT_TOKEN + appId + "_"  + accountId, gToken + "_" + devId + "_" + clientId, accountTokenExpireTime, TimeUnit.DAYS);
        }

        return true;
    }

    private void cacheAccount(AccountInfo account){
        if(account == null){
            return;
        }
        ValueOperations valueOperations = redisTemplate.opsForValue();
        valueOperations.set(Constant.REDIS_KEY_ACCOUNT + account.getId(), JsonUtils.objectToString(account), Constant.REDIS_KEY_ACCOUNT_EXPIRE, TimeUnit.HOURS);
    }

    /**
     * 查询账号信息（包含机器人）
     * @param accountId
     * @return
     */
    public AccountInfo getAccountInfo(int accountId){
        ZSetOperations zSetOperations = redisTemplate.opsForZSet();
        long likeCount = zSetOperations.size(Constant.REDIS_KEY_PREFIX_LIKE + accountId);

        ValueOperations valueOperations = redisTemplate.opsForValue();
        String key = Constant.REDIS_KEY_ACCOUNT + accountId;
        String value = (String)valueOperations.get(key);

        AccountInfo accountInfo = null;
        if(!StringUtil.isBlank(value)){
            accountInfo = JsonUtils.stringToObject(value, AccountInfo.class);
        } else {
            accountInfo = queryAccountInfo(accountId);
            if (accountInfo == null) {
                accountInfo = queryAIAccountInfo(accountId);
            }

            cacheAccount(accountInfo);
        }
        if (accountInfo != null) {
            accountInfo.setLike(likeCount);
        }

        return accountInfo;
    }

    public void onPlayerLogin(BaseMessage message, MessageWorker worker){
        if(message.getCmd() == LoginCmd.LOGIN_C2S){
            String content = new String(message.getBody(), Charset.forName("utf-8"));
            LoginMessage loginMessage = JsonUtils.stringToObject(content, LoginMessage.class);
            //判断用户是否在线
            MessageWorker mw = ChannelManager.getInstance().getMessageWorker(loginMessage.getAccountId());
            if(mw != null) {
                logger.info("用户:" + loginMessage.getAccountId() + ",在别的设备登录，现在断开其它设备的连接.");
                ChannelManager.getInstance().rmMessageWorker(mw);
                try {
                    mw.getChannel().close().sync().addListener( (future)->{
                       if(future.isSuccess()){
                           logger.info("用户:" + loginMessage.getAccountId() + "连接已经断开.");
                           AccountInfo info = onPlayerLogin(loginMessage, worker);
                           logger.info("AccountInfo is :" + info);
                           if(info != null){
                               sendLoginResult(Constant.RNT_CODE_SUCCESS, worker);
                               worker.setAccount(info);
                               ChannelManager.getInstance().addAccountInfo(worker);
                               LoginEvent event = new LoginEvent();
                               event.setAccountId(info.getId());
                               ChatDispatcher.getInstance().postEvent(event); //分发上线事件
                               MatchService.getInstance().addPreMatchList(loginMessage.getAccountId());
                           }
                       }else{
                           logger.error("用户:" + loginMessage.getAccountId() + "挤账号失败，登录失败.");
                       }
                    });
                }catch (Exception e){
                    e.printStackTrace();
                }
            }else{
                AccountInfo info = onPlayerLogin(loginMessage, worker);
                logger.info("AccountInfo is :" + info);
                if(info != null){
                    sendLoginResult(Constant.RNT_CODE_SUCCESS, worker);
                    worker.setAccount(info);
                    ChannelManager.getInstance().addAccountInfo(worker);
                    LoginEvent event = new LoginEvent();
                    event.setAccountId(info.getId());
                    ChatDispatcher.getInstance().postEvent(event); //分发上线事件
                    MatchService.getInstance().addPreMatchList(loginMessage.getAccountId());
                }
            }
        }
    }

    private AccountInfo onPlayerLogin(LoginMessage message, MessageWorker messageWorker){
        ValueOperations valueOperations = redisTemplate.opsForValue();
        //查询缓存是否过期
        Object token = valueOperations.get(Constant.REDIS_KEY_PREFIX_ACCOUNT_TOKEN + message.getAppId() + "_"  + message.getAccountId());
        if (token == null) {
            logger.info("onPlayerLogin token expire, account id:" + message.getAccountId());
            sendLoginResult(Constant.RNT_CODE_TOKEN_CHECK_FAIL, messageWorker);
            return null;
        }

        String tokens = message.getGtoken() + "_" + message.getUuid() + "_" + message.getClientId();
        if (! tokens.equals(token)) {
            logger.info("token expire, account id:" + message.getAccountId() + ", tokens:" + tokens);
            //发送token过期的消息
            sendLoginResult(Constant.RNT_CODE_TOKEN_EXPIRE, messageWorker);
            return null;
        }
        AccountInfo info = getAccountInfo(message.getAccountId());
        return info;
    }

    private void sendLoginResult(int resultCode, MessageWorker messageWorker){
        S2CMessage resultMessage = S2CMessage.valueOf(Module.LOGIN, LoginCmd.LOGIN_RESULT_S2C);
        if(resultCode == Constant.RNT_CODE_SUCCESS){
            resultMessage.setResult(ResultVO.success());
        }else{
            resultMessage.setResult(ResultVO.error("fail", resultCode));
        }
        messageWorker.sendMessage(resultMessage);
    }

    public ResultVO visitorLogin(Account account) {
        String uuid = account.getUuid();
        if (TextUtils.isEmpty(uuid)) {
            logger.error("uuid empty");
            return ResultVO.error("param error", Constant.RNT_CODE_PARAM_ERROR);
        }

        String firebaseId = account.getFirebaseId();
        String firebaseToken = account.getFirebaseToken();

        //每次登陆都生成新的gToken
        String gToken = genToken(uuid, Constant.ACCOUNT_PLATFORM_SELF, Constant.APP_TYPE_GAME_PLATFORM);
        VisitorVO vo = null;

        Object uuidLock = getLock(uuid);
        synchronized (uuidLock) {
            AccountInfo accountInfo = accountMapper.queryVisitorAccount(uuid, Constant.ACCOUNT_VISITOR_CHANGE);
            if (accountInfo != null) {
                vo = VisitorVO.valueOf(accountInfo);
                ZSetOperations zSetOperations = redisTemplate.opsForZSet();
                long likeCount = zSetOperations.size(Constant.REDIS_KEY_PREFIX_LIKE + vo.getAccountId());
                vo.setLike(likeCount);
            } else {
                AccountInfo visitor = new AccountInfo();
                String nickName = AccountUtil.genNickName();
                visitor.setNickNameByte(nickName.getBytes(StandardCharsets.UTF_8));
                visitor.setAge(AccountUtil.genAge());
                visitor.setPlatform(Constant.ACCOUNT_PLATFORM_VISITOR);

                int sex = AccountUtil.genSex();
                String avatar = "";
                if (sex == Constant.ACCOUNT_SEX_FEMALE) {
                    avatar = femaleAvatar;
                } else if (sex == Constant.ACCOUNT_SEX_MALE) {
                    avatar = maleAvatar;
                }

                visitor.setSex(sex);
                visitor.setHeaderImg(avatar);
                visitor.setConstellation(AccountUtil.genConstellation());
                visitor.setUuid(uuid);
                visitor.setType(Constant.ACCOUNT_TYPE_VISITOR);
                visitor.setOpenId("");
                visitor.setLike(0);

                accountMapper.saveAccount(visitor);

                vo = VisitorVO.valueOf(visitor);
                vo.setIsNew(1);

                //插入ES
                esService.saveAccount2Solr(visitor);
            }
        }

        vo.setgToken(gToken);
        setRedisToken(Constant.APP_TYPE_GAME_PLATFORM, vo.getAccountId(), uuid, account.getClientId(), gToken);

        firebaseService.insertOrUpdateFirebaseTokenAsync(vo.getAccountId(), firebaseId, firebaseToken);

        return ResultVO.success(vo);
    }

    private Object getLock(String key){
        Object object = locks.get(key);
        if(object == null){
            synchronized (this){
                object = locks.get(key);
                if(object == null){
                    object = new Object();
                    locks.put(key, object);
                }
            }
        }

        return object;
    }
}
