package com.game.schedule.service;

import com.game.account.domain.AccountInfo;
import com.game.account.service.AccountService;
import com.game.chat.util.JsonUtils;
import com.game.home.domain.CommParam;
import com.game.util.Constant;
import org.apache.http.util.TextUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AroundPlayerService {
    private static final Logger logger = LoggerFactory.getLogger(AroundPlayerService.class);
    private static double EARTH_RADIUS = 6378.137;
    private static int SCAN_PAGE_LIMIT = 500;
    private static int SCAN_PAGE_MAX = 20;

    @Autowired
    AccountService accountService;
    @Autowired
    RedisTemplate redisTemplate;
    @Value("${around.player.distance}")
    private String aroundDistance;

    /**
     * 附近的人
     * @param accountId
     */
    public void dealAroundPlayer(int accountId, CommParam commParam) {
        //重新计算该用户
        if (accountId != 0) {
            AccountInfo account = accountService.queryById(accountId);
            if (account == null) {
                logger.debug("account not exist");
                return;
            }

            doOnePlayer(account, commParam);
        } else {
            //扫描全部用户
            logger.debug("no deal all player");
        }
    }

    private void doOnePlayer(AccountInfo account, CommParam commParam) {
        logger.info("start deal account:" + account.getId());
        int accountId = account.getId();
        String lon;
        String lat;
        if (commParam != null) {
            lon = commParam.getLongitude();
            lat = commParam.getLatitude();
        } else {
            lon = account.getLongitude();
            lat = account.getLatitude();
        }

        if ((TextUtils.isEmpty(lon) && TextUtils.isEmpty(lat)) || (lon.equals("0") && lat.equals("0"))) {
            logger.warn("deal around players, account info not perfect, accountId:" + accountId);
            return;
        }

        Map<String, Double> ret = getAround(lat, lon);
        logger.debug("account distance info :" + ret.toString());
        Map<Integer, Long> ids = new HashMap<>();

        //扫描其他用户，预top10000
        for (int page = 0; page < SCAN_PAGE_MAX; page ++) {
            int offset = page * SCAN_PAGE_LIMIT;
            List<AccountInfo> accountInfos = accountService.queryAccountAtPage(accountId, offset, SCAN_PAGE_LIMIT);
            if (accountInfos.isEmpty()) {
                logger.info("done");
                break;
            }

            for (AccountInfo info : accountInfos) {
                if (info.getId() == accountId) {
                    continue;
                }

                if (TextUtils.isEmpty(info.getLatitude()) && TextUtils.isEmpty(info.getLongitude())) {
                    logger.debug("no upload lat, lon info");
                    continue;
                }

                Double longitude = Double.parseDouble(info.getLongitude());
                Double latitude = Double.parseDouble(info.getLatitude());
                if (longitude >= ret.get("minLng") && longitude <= ret.get("maxLng")
                        && latitude >= ret.get("minLat") && latitude <= ret.get("maxLat")
                        ) {
                    //计算距离
                    Double distance = LantitudeLongitudeDist(Double.parseDouble(lon), Double.parseDouble(lat), longitude, latitude);
                    Long distanceM = Math.round(distance * 1000);
                    logger.debug("accountId: " + info.getId() + ", distance:" + distance);
                    ids.put(info.getId(), distanceM);
                }
            }

        }

        logger.debug("ids list:" + ids.toString());

        if (! ids.isEmpty()) {
            ValueOperations operations = redisTemplate.opsForValue();
            String key = Constant.REDIS_KEY_PREFIX_AROUND_PLAYER + accountId;
            operations.set(key, JsonUtils.objectToString(ids));
        }
    }

    /**
     * 获取当前用户一定距离以内的经纬度值
     * 单位米 return
     * 最小经度 minLng
     * 最小纬度 maxLat
     * 最大经度 maxLng
     * 最大纬度 minLat
     */
    private Map<String, Double> getAround(String latStr, String lngStr) {
        Map<String, Double> map = new HashMap<>();

        Double latitude = Double.parseDouble(latStr);// 传值给纬度
        Double longitude = Double.parseDouble(lngStr);// 传值给经度

        Double degree = (24901 * 1609) / 360.0; // 获取每度
        double raidusMile = Double.parseDouble(aroundDistance);

        Double mpdLng = Double.parseDouble((degree * Math.cos(latitude * (Math.PI / 180))+"").replace("-", ""));
        Double dpmLng = 1 / mpdLng;
        Double radiusLng = dpmLng * raidusMile;
        //获取最小经度
        Double minLng = longitude - radiusLng;
        // 获取最大经度
        Double maxLng = longitude + radiusLng;

        Double dpmLat = 1 / degree;
        Double radiusLat = dpmLat * raidusMile;
        // 获取最小纬度
        Double minLat = latitude - radiusLat;
        // 获取最大纬度
        Double maxLat = latitude + radiusLat;

        map.put("minLat", minLat);
        map.put("maxLat", maxLat);
        map.put("minLng", minLng);
        map.put("maxLng", maxLng);

        return map;
    }


    private static double rad(double d) {
        return d * Math.PI / 180.0;
    }

    /**
     * 基于余弦定理求两经纬度距离
     * @param lon1 第一点的精度
     * @param lat1 第一点的纬度
     * @param lon2 第二点的精度
     * @param lat2 第二点的纬度
     * @return 返回的距离，单位km
     * */
    public static double LantitudeLongitudeDist(double lon1, double lat1,double lon2, double lat2) {
        double radLat1 = rad(lat1);
        double radLat2 = rad(lat2);

        double radLon1 = rad(lon1);
        double radLon2 = rad(lon2);

        if (radLat1 < 0)
            radLat1 = Math.PI / 2 + Math.abs(radLat1);// south
        if (radLat1 > 0)
            radLat1 = Math.PI / 2 - Math.abs(radLat1);// north
        if (radLon1 < 0)
            radLon1 = Math.PI * 2 - Math.abs(radLon1);// west
        if (radLat2 < 0)
            radLat2 = Math.PI / 2 + Math.abs(radLat2);// south
        if (radLat2 > 0)
            radLat2 = Math.PI / 2 - Math.abs(radLat2);// north
        if (radLon2 < 0)
            radLon2 = Math.PI * 2 - Math.abs(radLon2);// west
        double x1 = EARTH_RADIUS * Math.cos(radLon1) * Math.sin(radLat1);
        double y1 = EARTH_RADIUS * Math.sin(radLon1) * Math.sin(radLat1);
        double z1 = EARTH_RADIUS * Math.cos(radLat1);

        double x2 = EARTH_RADIUS * Math.cos(radLon2) * Math.sin(radLat2);
        double y2 = EARTH_RADIUS * Math.sin(radLon2) * Math.sin(radLat2);
        double z2 = EARTH_RADIUS * Math.cos(radLat2);

        double d = Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2)+ (z1 - z2) * (z1 - z2));
        //余弦定理求夹角
        double theta = Math.acos((EARTH_RADIUS * EARTH_RADIUS + EARTH_RADIUS * EARTH_RADIUS - d * d) / (2 * EARTH_RADIUS * EARTH_RADIUS));
        double dist = theta * EARTH_RADIUS;
        return dist;
    }

}
