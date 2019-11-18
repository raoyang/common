package com.game.util;

/**
 * 输入参数检查
 */
public class CheckInput {

    /**
     * 判断参数是否合法
     */
    public static boolean isParamAble(String param) {
        return (!param.toLowerCase().contains("exec ")) &&
                (!param.toLowerCase().contains("execute")) &&
                (!param.toLowerCase().contains("insert ")) &&
                (!param.toLowerCase().contains("update ")) &&
                (!param.toLowerCase().contains("select ")) &&
                (!param.toLowerCase().contains("delete ")) &&
                (!param.toLowerCase().contains("drop ")) &&
                (!param.toLowerCase().contains("from ")) &&
                (!param.toLowerCase().contains("truncate ")) &&
                (!param.toLowerCase().contains("\\ ")) &&
                (!param.toLowerCase().contains("sleep ")) &&
                (!param.toLowerCase().contains("ping ")) &&
                (!param.toLowerCase().contains("print ")) &&
                (!param.toLowerCase().contains("response ")) &&
                (!param.toLowerCase().contains("script ")) &&
                (!param.toLowerCase().contains("convert(")) &&
                (!param.toLowerCase().contains("waitfor(")) &&
                (!param.toLowerCase().contains("concat(")) &&
                (!param.toLowerCase().contains("alert(")) &&
                (!param.toLowerCase().contains("database")) &&
                (!param.toLowerCase().contains("iframe")) &&
                (!param.toLowerCase().contains("frame")) &&
                (!param.toLowerCase().contains(".ini ")) &&
                (!param.toLowerCase().contains("phpinfo")) &&
                (!param.toLowerCase().contains("dir ")) &&
                (!param.toLowerCase().contains("/etc/"));
    }

    /**
     * 判断参数是否为数字
     */
    public static boolean isNumber(String param) {
        return param.matches("[0-9]+");
    }

    /**
     * 判断参数是否为md5串
     */
    public static boolean isMd5Str(String param) {
        return param.matches("[0-9a-z]{32}");
    }
    /**
     * 判断参数是否为clientId串
     */
    public static boolean isClientIdStr(String param) {
        if (param.equals("")) {
            return false;
        }
        if (param.length() != 35) {
            return false;
        }
        if (! param.contains("-")) {
            return false;
        }
        String[] ids = param.split("-");

        return ids[1].matches("[0-9a-z]{32}");
    }

    /**
     * 判断参数是否为合法的电话号码
     */
    public static boolean isPhone(String param) {
        return param.matches("1[0-9]{10}");
    }

    /**
     * 判断参数是否为合法的ICCID
     */
    public static boolean isICCID(String param) {
        return param.matches("[0-9A-Z]{20}");
    }

    /**
     * 判断参数是否为合法的IMSI
     */
    public static boolean isIMSI(String param) {
        return (param.matches("[0-9]+") && param.length() <= 15);
    }

    /**
     * 判断参数是否为合法的IMEI或MEID
     */
    public static boolean isIMEI(String param) {
        return param.length() >= 14 && param.length() <= 15 && !param.matches("0+") && param.matches("[0-9a-zA-Z]+");
    }

    /**
     * 验证网址Url
     *
     */
    public static boolean isUrl(String str) {
        String regex = "http(s)?://([\\w-]+\\.)+[\\w-]+(/[\\w- ./?%&=]*)?";
        return  str.matches(regex);
    }

    /**
     * 验证包名
     * @param str
     * @return
     */
    public static boolean isPackageName(String str) {
        String regex = "([a-zA-Z_][a-zA-Z0-9_]*[.])*([a-zA-Z_][a-zA-Z0-9_]*)$";
        return  str.matches(regex);
    }

}
