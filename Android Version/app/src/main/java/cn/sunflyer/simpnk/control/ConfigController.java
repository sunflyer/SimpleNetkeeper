package cn.sunflyer.simpnk.control;

import android.app.Application;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * 配置文件控制类
 * @version 1.5.0
 * @author CrazyChen
 * @since Simple Netkeeper 1.5.0
 * */
public class ConfigController {
	
	/**
	 * properties
	 * */
	private static Properties gPro = null ;
	
	/**配置文件版本*/
	private static int gProVersion = 6;
	
	/**
	 * 返回配置文件版本号
	 * */
	public static int getConfigVersion(){
		return gProVersion;
	}

    /**
     * 配置文件位置
     * */
    private static String gConfigPath = "";

    public static void setConfigPath(String szPath){
        gConfigPath = szPath;
    }

	/**
	 * 获取制定的配置数据。如果不存在会返回空字符串
	 * */
	public static String getConfig(String szConfigName){
		return (gPro!=null && !gPro.isEmpty()) ? (gPro.getProperty(szConfigName) == null ? "" : gPro.getProperty(szConfigName)) : "";
	}
	
	/**
	 * 获取制定配置数值
	 * @param szConfigName 配置项目名称
	 * @param defVal 默认值，如果没有找到该项，则返回这个默认值
	 * */
	public static int getConfig(String szConfigName,int defVal){
		if(szConfigName != null && !szConfigName.equals("")){
			try{
				return Integer.parseInt(ConfigController.getConfig(szConfigName));
			}catch(Exception e){
				Log.log("尝试转换配置文件数值出现错误");
			}
		}
		return defVal;
	}
	
	/**
	 * 制定配置文件的值是否与给定的值相等。如果配置不存在，返回给定的默认值
	 * @param szConfigName 配置项目名称
	 * @param szCompareData 比较数据
	 * @param defVal 默认值
	 * */
	public static boolean getConfig(String szConfigName, String szCompareData,boolean defVal ){
		if(szConfigName != null && !szConfigName.equals("")){
			String pConfig  =  getConfig(szConfigName);
			return pConfig == null ? szCompareData == null : pConfig.equals(szCompareData);
		}
		return defVal;
	}
	
	/**
	 * 更新配置项目
	 * @param szConfigName
	 * @param szConfigVal
	 * @return
	 * */
	public static boolean updateConfig(String szConfigName,String szConfigVal){
		if(gPro!=null && szConfigName!=null && szConfigVal!=null && !szConfigName.equals("")){
			gPro.setProperty(szConfigName, szConfigVal);
			
			return true;
		}
		return false;
	}

	public static boolean saveConfig(String szFileName){
		StatusController.saveConfig();
		
		if(gPro!=null && !gPro.isEmpty()){
			try {
				updateConfig(Config_Application_ConfVer, Config_Application_CurrentVersion);
				gPro.store(new FileOutputStream(new File(szFileName)), Config_Comment);
				return true;
			}	catch (IOException e) {
				Log.logE(e);
			}
		}
		return false;
	}

	public static boolean loadConfig(String szFileName){
		if(szFileName!=null && !szFileName.equals("")){
			File pConfigFile = new File(szFileName);
			if(pConfigFile.exists()){
				gPro = new Properties();
				try {
					gPro.load(new FileInputStream(pConfigFile));
					if(!gPro.isEmpty()){
						String pConVer = gPro.getProperty(Config_Application_ConfVer);
						
						if(pConVer != null){
							gProVersion = Integer.parseInt(pConVer);
							return true;
						}
					}				
				} catch (Exception e) {
					Log.logE(e);
				}
			}				
		}
		gPro = new Properties();
		
		return false;
	}

	public static boolean saveConfig(){
		return saveConfig(gConfigPath+"/"+CONFIG_FILE);
	}

	public static boolean loadConfig(){
		return loadConfig(gConfigPath+"/"+CONFIG_FILE);
	}

	private static boolean removeConfigFile(String szFileName){
		if(szFileName!=null && !szFileName.equals("")){
			File pFile = new File(szFileName);
			if(pFile.exists()){
				return pFile.delete();
			}
			return true;
		}
		return false;
	}

	public static boolean clearConfig(){
		if(gPro!=null){
			gPro.clear();
			removeConfigFile(gConfigPath+"/"+CONFIG_FILE);
			return gPro.isEmpty();
		}
		return true;
	}
	
	//========================================================================

			private static final String Config_Comment = "Simple Netkeeper User Configuaration Files";

			public static final String Config_Acc_Name="AccName";
			public static final String Config_Acc_Password="AccPassword";
			public static final String Config_Router_IP="RouterIP";
			public static final String Config_Router_Name="RouterAdmin";
			public static final String Config_Router_Password="RouterPassword";
			public static final String Config_Router_AuthMethod="AuthMethod";
			public static final String Config_Router_DialMode="DialMode";
			public static final String Config_Router_DialPlace="DialPlace";
			public static final String Config_Router_DialRadiusDefined = "enc.radius";
			public static final String Config_Router_DialRadiusPrefix = "enc.prefix";
			public static final String Config_Router_Encrypt="Encrypt";
			public static final String Config_Router_Manufactor="Manufactor";
			
			private static final String Config_Application_ConfVer="ConfigVersion";

			public static final String Config_Dial_Acc_Name="DialAccName";
			public static final String Config_Dial_Acc_Password="DialAccPassword";
			public static final String Config_Dial_is_Rem="DialAccRem";
			public static final String Config_Dial_is_HeartBeat="DialHeartBeat";
			public static final String Config_Dial_is_AutoDial="DialAuto";

			public static final String Config_Wifi_SSID="WifiSSID";
			public static final String Config_Wifi_Password="WifiPassword";

			public static final String Config_Ontime_Mode = "ontime.mode",
					Config_Ontime_SH = "ontime.starthour",
				Config_Ontime_SM = "ontime.startmin",
				Config_Ontime_EH = "ontime.endhour",
				Config_Ontime_EM = "ontime.endmin",
				Config_Ontime_SD = "ontime.startday",
				Config_Ontime_ED = "ontime.endday",
				Config_Ontime_WifiName = "ontime.wifiname",
				Config_Ontime_WifiKey = "ontime.wifikey";


			public static final String Config_Application_CurrentVersion="6";
			
			//========================================================================	
			public static final String CONFIG_FILE = "SimpleNetkeeper.conf";
}
