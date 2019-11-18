package com.game.util;

import com.game.match.domain.PlayerVO;
import com.game.util.pic.MD5Util;
import com.google.gson.Gson;
import com.game.common.vo.ResultVO;
import com.game.home.domain.CommParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class CommonFunUtil {
    private static final Logger logger = LoggerFactory.getLogger(CommonFunUtil.class);

    public static String getNowDay() {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return df.format(new Date());
    }

    public static String timeStamp2Date(Long t) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return df.format(new Date(t));
    }

    public static  <T> T toReq(String p, Long random, Class<T> clz) throws Exception {
        if(StringUtil.isBlank(p)) {
            return null;
        }
        p = URLDecoder.decode(p);
        String json = AESUtil.decrypt(p, random);
        logger.debug("decrypt result:" + json);

        Gson gson = new Gson();
        return gson.fromJson(json, clz);
    }

    public static String toRsp(Object respone, Long random) throws Exception {
        Gson gson = new Gson();
        try {
            String json = gson.toJson(respone);
            String aes = AESUtil.encrypt(json, random);
            return URLEncoder.encode(aes, "UTF-8");
        } catch (Exception e) {
            return AESUtil.encrypt(gson.toJson(ResultVO.error("to response fail")), random);
        }
    }

    public static Boolean checkCommonParams(CommParam commParam) {
        if (commParam == null) {
            return false;
        }

        if (! commParam.getClientId().equals("")) {
            if (! CheckInput.isClientIdStr(commParam.getClientId())) {
                return false;
            }
        }

        if (commParam.getAppVer() == 0) {
            return false;
        }

        if (commParam.getLan().equals("")) {
            return false;
        }

        if (! CheckInput.isNumber(String.valueOf(commParam.getNetType()))) {
            return false;
        }

        return true;
    }

    /**
     * 得到几天前的时间
     * @param d
     * @param day
     * @return
     */
    public static Date getDateBefore(Date d,int day){
        Calendar now =Calendar.getInstance();
        now.setTime(d);
        now.set(Calendar.DATE,now.get(Calendar.DATE)-day);
        return now.getTime();
    }

    /**
     * 得到几天后的时间
     * @param d
     * @param day
     * @return
     */
    public static Date getDateAfter(Date d,int day) {
        Calendar now = Calendar.getInstance();
        now.setTime(d);
        now.set(Calendar.DATE, now.get(Calendar.DATE) + day);
        return now.getTime();
    }

    public static String getNowDay(Date date) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return df.format(date);
    }

    public static long nextWeekStartTimeMilli(long timestamp) {
        ZoneId zoneId = ZoneId.systemDefault();
        return LocalDate.now(Clock.fixed(Instant.ofEpochMilli(timestamp),zoneId)).plusWeeks(1).with(DayOfWeek.MONDAY).atStartOfDay(zoneId).toInstant().toEpochMilli();
    }


    /**
     * 距离下个周一剩余的毫秒数
     * @return
     */
    public static long nextWeekLeftTimeMilli() {
        long now = System.currentTimeMillis();
        return nextWeekStartTimeMilli(now) - now;
    }

    public static long netWeekLeftTimeSecond() {
        long now = System.currentTimeMillis();
        long leftMilli = nextWeekStartTimeMilli(now) - now;
        return leftMilli / 1000;
    }


    public static int getOnlineGamer(int real) {
        Random random = new Random();
        int randNum = random.nextInt(2000) + 8000;

        return randNum + real;
    }

    public static Map<String, String> genUploadFileInfo(MultipartFile file) {
        Map<String, String> info = new HashMap<>();
        String fileName = file.getOriginalFilename();
        String ext = getFileExt(fileName);
        if (ext == null) {
            return null;
        }
        List<String> exts = Arrays.asList("jpg", "jpeg", "png");
        if (! exts.contains(ext)) {
            logger.error("file ext no support, ext:" + ext);
            return null;
        }

        MD5 md5 = new MD5();
        //生成文件名
        String name = md5.toMD5Str(fileName + new Date().getTime()) + "." + ext;
        info.put("name", name);

        //生成文件路径
        DateFormat df = new SimpleDateFormat("yyyyMMdd");
        String nowDay = df.format(new Date());
        Random random = new Random();
        String filePath = nowDay + (random.nextInt(9000) + 1000);
        info.put("dir", filePath);

        return info;
    }

    public static String getFileExt(String fileName) {
        int index = fileName.lastIndexOf(".");

        if (index == -1) {
            return null;
        }

        return fileName.substring(index + 1);
    }

    public static String buildGameRoomId(List<PlayerVO> players, String gameId) {
        String roomId = "";
        StringBuilder sb = new StringBuilder();
        for (PlayerVO account : players) {
            sb.append(account.getAccountId());
        }

        Random random = new Random();
        int randNum = random.nextInt(1000) + 1000;

        roomId = MD5Util.getMD5String(sb.append(gameId).append(randNum).toString());
        Long ts = new Date().getTime();

        return ts + roomId;
    }

    public static int genMatchTimeout(int confNum, int fixNum) {
        int timeout = 10;
        if (confNum == 0) {
            return timeout;
        }

        Random random = new Random();
        int baseNum = confNum - fixNum;
        int randNum = fixNum * 2 + 1;
        timeout = random.nextInt(randNum) + baseNum;
        logger.debug("match timeout rand:" + timeout);

        return timeout;
    }

    public static String getCountryByLan(String lan) {
        String country = "Global";
        if (lan.equals("")) {
            return country;
        }

        String countryTag = lan;
        if (lan.contains("-")) {
            List<String> tags = Arrays.asList(lan.split("-"));
            if (tags.size() > 1) {
                countryTag = tags.get(1);
            }
        }

        switch (countryTag) {
            case "ID":
                country = "Indonesia";
                break;
            case "IN":
                country = "India";
                break;
            case "PH":
                country = "Philippines";
                break;
            default:
                country = "Global";
        }

        return country;
    }

    public static Integer getRemainSecondsOneDay(Date currentDate) {
        LocalDateTime midnight = LocalDateTime.ofInstant(currentDate.toInstant(),
                ZoneId.systemDefault()).plusDays(1).withHour(0).withMinute(0)
                .withSecond(0).withNano(0);
        LocalDateTime currentDateTime = LocalDateTime.ofInstant(currentDate.toInstant(),
                ZoneId.systemDefault());
        long seconds = ChronoUnit.SECONDS.between(currentDateTime, midnight);
        return (int) seconds;
    }

}
