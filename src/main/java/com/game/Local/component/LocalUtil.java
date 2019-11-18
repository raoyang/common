package com.game.Local.component;

import com.game.home.domain.CommParam;
import com.game.util.Constant;
import org.apache.http.util.TextUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

@Component
public class LocalUtil {
    private static final String Lan_Score_Key = "score";
    private static final String Lan_Level_Key = "level";
    public static final String Lan_TogetherGame_Key = "togetherGame";
    public static final String Lan_Challenge_Key = "challenge";
    public static final String Lan_MoreGame_Key = "moreGame";
    public static final String Lan_GameDesc_Key = "gameDesc";
    public static final String Lan_PersonDesc_Key = "personDesc";
    public static final String Lan_Win_Key = "winMore";
    @Autowired
    LocalMessage localMessage;

    public String getScoreUnit(int rankType, CommParam commParam) {
        Locale locale = toLocale(commParam);
        if (rankType == Constant.RANKING_TYPE_SCORE) {
            return localMessage.getMessage(Lan_Score_Key, null, locale);
        }
        if (rankType == Constant.RANKING_TYPE_LEVEL) {
            return localMessage.getMessage(Lan_Level_Key, null, locale);
        }

        return "";
    }

    public String getLocalMessage(String key, CommParam commParam) {
        return localMessage.getMessage(key, null, toLocale(commParam));
    }

    private Locale toLocale(CommParam commParam) {
        String lanCountry = commParam.getLan();
        String lan = "en";
        String country = "";
        if (! TextUtils.isEmpty(lanCountry)) {
            if (lanCountry.contains("-")) {
                List<String> tags = Arrays.asList(lanCountry.split("-"));
                lan = tags.get(0);
                country = tags.get(1);
            } else {
                lan = lanCountry;
            }
        }

       return new Locale(lan, country);
    }
}
