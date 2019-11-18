package com.game.account.service;

import com.game.account.dao.AccountMapper;
import com.game.account.dao.GodMapper;
import com.game.account.dao.PhotoWallMapper;
import com.game.account.domain.*;
import com.game.chat.util.JsonUtils;
import com.game.common.vo.ResultVO;
import com.game.device.dao.DeviceMapper;
import com.game.login.domain.Account;
import com.game.login.service.LoginService;
import com.game.solr.service.ESService;
import com.game.solr.service.SolrService;
import com.game.util.CommonFunUtil;
import com.game.util.Constant;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class AccountService {
    private static final Logger logger = LoggerFactory.getLogger(AccountService.class);
    @Autowired
    AccountMapper accountMapper;
    @Autowired
    RedisTemplate redisTemplate;
    @Autowired
    private ESService esService;
    @Autowired
    private LoginService loginService;
    @Autowired
    private PhotoWallMapper photoWallMapper;
    @Autowired
    GodMapper godMapper;
    @Autowired
    private DeviceMapper deviceMapper;
    @Value("${account.avatar.save.path}")
    private String savePath;
    @Value("${cdn.domain}")
    private String cdnHost;


    public ResultVO avatar(MultipartFile file, int accountId) {
        //校验文件格式
        List<String> imgSuffix = Arrays.asList("image/jpeg", "application/x-jpg", "application/x-jpe");
        String fileSuffix = file.getContentType();
        logger.info("file type:" + fileSuffix);
        if (! imgSuffix.contains(fileSuffix)) {
            return ResultVO.error("file suffix error", Constant.RNT_CODE_EXCEPTION);
        }

        Map<String, String> fileInfo = CommonFunUtil.genUploadFileInfo(file);
        if (fileInfo == null) {
            return ResultVO.error("file parse error", Constant.RNT_CODE_EXCEPTION);
        }

        String name = fileInfo.get("name");
        String dir = fileInfo.get("dir");
        String fileDir = savePath + dir;
        String filePath = fileDir + "/" + name;
        logger.info("save path:" + filePath);
        File destDir = new File(fileDir);
        if (!destDir.exists()) {
            destDir.mkdirs();
        }
        try {
            File dest = new File(filePath);
            file.transferTo(dest);
            logger.info("upload avatar success");
        } catch (Exception e) {
            logger.info("upload exception, msg:" + e.getMessage());
            return ResultVO.error("update exception", Constant.RNT_CODE_EXCEPTION);
        }

        String url = cdnHost + dir + "/" + name;
        if (url.equals("")) {
            return ResultVO.error("update url empty", Constant.RNT_CODE_EXCEPTION);
        }

        AccountInfo accountInfo = new AccountInfo();
        accountInfo.setId(accountId);
        accountInfo.setHeaderImg(url);
        accountMapper.updateAccountInfo(accountInfo);
        esService.updateAccount2Es(accountInfo);
        clearAccountInfoCache(accountId);

        AvatarVO avatarVO = new AvatarVO();
        avatarVO.setUrl(url);

        return ResultVO.success(avatarVO);
    }

    @Transactional(rollbackFor = Exception.class)
    public ResultVO update(Account account) {
        AccountInfo accountInfo = new AccountInfo();
        BeanUtils.copyProperties(account, accountInfo);
        accountInfo.setId(account.getAccountId());
        //昵称、地址存储字节
        accountInfo.setNickNameByte(account.getNickName().getBytes(StandardCharsets.UTF_8));
        accountInfo.setAddressByte(account.getAddress().getBytes(StandardCharsets.UTF_8));
        if(account.getSignature() != null){
            accountInfo.setSignatureByte(account.getSignature().getBytes(StandardCharsets.UTF_8));
        }
        if(account.getPhotoWall() != null && account.getPhotoWall().size() > 0){
            accountInfo.setHeaderImg(account.getPhotoWall().get(0));
            accountInfo.setPhotoWall(JsonUtils.objectToString(account.getPhotoWall()));
        }
        accountMapper.updateAccountInfo(accountInfo);
        //清缓存
        clearAccountInfoCache(account.getAccountId());
        esService.updateAccount2Es(accountInfo);

        return ResultVO.success();
    }

    public void updateDeviceInfo(Account account){
        deviceMapper.updateDiviceInfo(account.getAccountId(), account.getUuid());
    }

    public ResultVO updateLatAndLon(Account account) {
        AccountInfo accountInfo = new AccountInfo();
        BeanUtils.copyProperties(account, accountInfo);
        accountInfo.setId(account.getAccountId());

        accountMapper.updateAccountInfo(accountInfo);

        return ResultVO.success();
    }

    private void clearAccountInfoCache(int accountId) {
        redisTemplate.delete(Constant.REDIS_KEY_ACCOUNT + accountId);
    }

    public List<AccountInfo> batchQueryAccount(List<String> ids) {
        return accountMapper.queryByFBOpenId(ids);
    }

    public List<AccountInfo> queryAccountAtPage(int accountId, int offset, int limit) {
        return accountMapper.queryByPage(accountId, offset, limit);
    }

    public AccountInfo queryById(int accountId) {
        return accountMapper.queryById(accountId);
    }

    public AccountInfo queryByOpenId(String openId, int platform) {
        return accountMapper.queryByOpenId(openId, platform);
    }

    public void addAccount(AccountInfo accountInfo) {
        accountMapper.saveAccount(accountInfo);
    }

    @Transactional(rollbackFor = Exception.class)
    public ResultVO uploadPhotoWallUrl(PhotoWallVO vo){
        if(photoWallMapper.getPhotoWalls(vo.getAccountId()) != null){
            return ResultVO.error("photo wall exist.");
        }
        logger.info("用户:" + vo.getAccountId() + "插入照片墙数据：" + vo);
        //更新数据库，更新缓存
        PhotoWallInfo info = PhotoWallInfo.valueOf(vo);
        photoWallMapper.insertPhotoUrl(info);
        addPhotoWall2Cache(info);
        return ResultVO.success();
    }

    @Transactional(rollbackFor = Exception.class)
    public ResultVO getPhotoWallInfos(int accountId){
        PhotoWallVO result = loadPhotoWallInfos(accountId);
        return ResultVO.success(result);
    }

    @Transactional(rollbackFor = Exception.class)
    public ResultVO updatePhotoWallInfo(PhotoWallVO vo){
        logger.info("用户:" + vo.getAccountId() + "更新照片墙数据：" + vo);
        PhotoWallInfo info = PhotoWallInfo.valueOf(vo);
        photoWallMapper.updatePhotoUrl(info);
        addPhotoWall2Cache(info);
        return ResultVO.success();
    }

    private PhotoWallVO loadPhotoWallInfos(int accountId){
        ValueOperations operations = redisTemplate.opsForValue();
        String key = Constant.REDIS_KEY_PHOTO_WALL + accountId;
        String value = (String)operations.get(key);
        if(value == null){
           return updatePhotoWallCache(accountId);
        }else{
            List<String> list = JsonUtils.stringToList(value, String[].class);
            PhotoWallVO vo = new PhotoWallVO();
            vo.setUrl(list);
            vo.setAccountId(accountId);
            return vo;
        }
    }

    private void addPhotoWall2Cache(PhotoWallInfo info){
        ValueOperations operations = redisTemplate.opsForValue();
        String key = Constant.REDIS_KEY_PHOTO_WALL + info.getAccountId();
        operations.set(key, info.getPhoto(), 1,  TimeUnit.DAYS);
    }

    private PhotoWallVO updatePhotoWallCache(int accountId){
        ValueOperations operations = redisTemplate.opsForValue();
        String key = Constant.REDIS_KEY_PHOTO_WALL + accountId;
        //从数据库查询，同步数据到缓存
        String photo = photoWallMapper.getPhotoWalls(accountId);
        List<String> list = JsonUtils.stringToList(photo, String[].class);
        operations.set(key, photo, 1, TimeUnit.DAYS);
        PhotoWallVO vo = new PhotoWallVO();
        vo.setAccountId(accountId);
        vo.setUrl(list);
        return vo;
    }
    /**
     * 点赞
     * @param accountRVO
     * @return
     */
    public ResultVO likeAccount(AccountRVO accountRVO) {
        int tAId = accountRVO.getTaId();
        AccountInfo accountInfo = loginService.getAccountInfo(tAId);
        if (accountInfo == null) {
            logger.debug("like account error, tAId:" + tAId);
            return ResultVO.error("accountId exception", Constant.RNT_CODE_EXCEPTION);
        }

        String redisKey = Constant.REDIS_KEY_PREFIX_LIKE + tAId;
        ZSetOperations zSetOperations = redisTemplate.opsForZSet();
        zSetOperations.add(redisKey, String.valueOf(accountRVO.getAccountId()), 1);

        return ResultVO.success();
    }

    /**
     * 投诉
     * @param accountRVO
     * @return
     */
    public ResultVO complainAccount(AccountRVO accountRVO) {
        int tAId = accountRVO.getTaId();
        AccountInfo accountInfo = loginService.getAccountInfo(tAId);
        if (accountInfo == null) {
            logger.debug("complain account error, tAId:" + tAId);
            return ResultVO.error("accountId exception", Constant.RNT_CODE_EXCEPTION);
        }

        String redisKey = Constant.REDIS_KEY_PREFIX_COMPLAIN + tAId;
        ZSetOperations zSetOperations = redisTemplate.opsForZSet();
        zSetOperations.add(redisKey, String.valueOf(tAId), 1);

        return ResultVO.success();
    }

    public ResultVO detailAccount(AccountsInfo accountsInfo) {
        List<AccountPanelInfo> data = new ArrayList<>();

        ZSetOperations zSetOperations = redisTemplate.opsForZSet();
        for(Integer id : accountsInfo.getAccounts()){
            if (id == 0) {
                continue;
            }

            AccountInfo accountInfo = LoginService.getInstance().getAccountInfo(id);
            if (accountInfo != null) {
                AccountPanelInfo panelInfo = AccountPanelInfo.valueOf(accountInfo);
                String redisKey = Constant.REDIS_KEY_PREFIX_LIKE + id;
                Long rankIndex = zSetOperations.rank(redisKey, String.valueOf(accountsInfo.getAccountId()));
                if (rankIndex != null) {
                    panelInfo.setIsLike(1);
                }
                data.add(panelInfo);
            }
        }

        return ResultVO.success(data);
    }

    public List<GodInfo> queryAIGodList() {
        Gson gson = new Gson();
        ValueOperations operations = redisTemplate.opsForValue();
        Object banners = operations.get(Constant.REDIS_KEY_AI_GOD_INFO);
        List<GodInfo> lists = new ArrayList<>();
        if (banners == null) {
            lists = godMapper.queryAll();
            operations.set(Constant.REDIS_KEY_AI_GOD_INFO, gson.toJson(lists), 7, TimeUnit.DAYS);
        } else {
            lists = gson.fromJson((String)banners, new TypeToken<List<GodInfo>>() {
            }.getType());
        }

        return lists;
    }

    public ResultVO listAvatar(AccountRVO accountRVO) {
        int limit = accountRVO.getLimit();
        if (limit == 0) {
            limit = 20;
        }

        List<String> avatars = accountMapper.queryAvatarRand(limit);
        avatars.removeIf(url -> url.contains("porfile_default"));
        if (avatars.isEmpty()) {
            return ResultVO.error("no more data", Constant.RNT_CODE_NO_DATA);
        }

        AvatarListVO listVO = new AvatarListVO();
        listVO.setAvatar(avatars);

        return ResultVO.success(listVO);
    }
}
