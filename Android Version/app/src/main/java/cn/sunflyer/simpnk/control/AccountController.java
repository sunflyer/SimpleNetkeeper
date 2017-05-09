package cn.sunflyer.simpnk.control;

import java.util.Date;

import cn.sunflyer.simpnk.annotation.Config;
import cn.sunflyer.simpnk.netkeeper.CXKUsername;

/**
 * Created by 陈耀璇 on 2015/5/6.
 * 真实帐号控制类。
 */
public class AccountController {

    public static final CharSequence[] RADIUS_NAME = {
            "家用模式","重庆 Netkeeper 2.5.0055 - 0092","湖北 E信","浙江 闪讯","重庆 Netkeeper 2.5.0094","江西 星空极速 v32","山东移动 Netkeeper 0094","河北联通 Netkeeper",
            "陕西翼讯 3.7.3" , "山东电信 3.7.3 8月17日版本" , "山东电信 3.7.3 10月27日版本" , "甘肃电信 3.7.1"
    };

    public static final String[] RADIUS = {
            "",
            "cqxinliradius002", //重庆地区Netkeeper2.5
            "hubtxinli01", //湖北E信
            "singlenet01", //杭州地区
            "xianxinli1radius",
            "jiangxi4.0",
            "shandongmobile13",
            "hebeicncxinli002",
            "sh_xi@xiaoyuan01",
            "560Ox!a0yuanOlIz",
            "shdOx!a0yuan01lz",
            "xiaoyuanyixun001"
    };

    public static final String[] RADIUS_PREFIX = {
            "",
            "\r\n",
            "\r\n",
            "\r\n",
            "\r\n",
            "\r1",
            "\r\n",
            "\r\n",
            "\r\n",
            "\r\n",
            "\r\n",
            "\r\n"
    };

    public static final int RADIUS_HOME = 0,
            RADIUS_CHONGQING = 1,
            RADIUS_HUBEI = 2,
            RADIUS_HANGZHOU = 3,
            RADIUS_CHONGQING_NEW = 4 ,
            RADIUS_JIANGXI = 5,
            RADIUS_SHANDONG = 6,
            RADIUS_HEBEI = 7,
            RADIUS_SHANXI = 8,
            RADIUS_SHANDONG_AUG = 9,
            RADIUS_SHANDONG_OCT = 10 ,
    RADIUS_GANSU = 11,
            RADIUS_SELF = 1000;

    @Config(configName = ConfigController.Config_Router_DialRadiusPrefix)
    public static String mCurrentRadiusPrefix = null;

    @Config(configName = ConfigController.Config_Router_DialRadiusDefined )
    public static String mCurrentRadiusStr = null;

    /**RADIUS标识*/
    @Config(configName = ConfigController.Config_Router_DialPlace , configType = Config.CONFIG_NUMBER_INT)
    public static int mCurrentRadius = RADIUS_CHONGQING_NEW;

    public static String getRealAccount(String username){
        return getRealAccount(username , (new Date()).getTime());
    }

    public static String getRealAccount(String username,long time){
        if(username == null) return null;
        if(time > 0 && mCurrentRadius != RADIUS_HOME && (mCurrentRadius < RADIUS.length || mCurrentRadius == RADIUS_SELF) && mCurrentRadius >= 0){
            Log.log("Real Acc : using " + getCurrentRadius());
            return new CXKUsername(getCurrentRadiusPrefix() , username,getCurrentRadius()).Realusername(time);
        }
        return username;
    }

    public static String getCurrentRadius(){
        return mCurrentRadius == RADIUS_SELF ? mCurrentRadiusStr : RADIUS[mCurrentRadius];
    }

    public static String getCurrentRadiusPrefix(){
        return mCurrentRadius == RADIUS_SELF ? mCurrentRadiusPrefix.replace("\\r", "\r").replace("\\n", "\n") : RADIUS_PREFIX[mCurrentRadius];
    }

}
