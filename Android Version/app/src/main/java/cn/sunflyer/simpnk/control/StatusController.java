package cn.sunflyer.simpnk.control;

import android.os.Handler;
import android.os.Message;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import cn.sunflyer.simpnk.annotation.Config;

/**
 * 状态控制器
 * @author CrazyChen
 * @version 1.5.0
 * @since Simple Netkeeper 1.5.0
 * */
public class StatusController {

	private static boolean isOnDialing = false ;

	public static boolean getStateDial(){return isOnDialing;}
	public static synchronized void setStateDial(boolean x){isOnDialing = x;};
	//========================================
	private static boolean isRouterDialed = false ;

	public static boolean getStateRouterDial(){return isRouterDialed ;}
	public static synchronized void setStateRouterDial(boolean x){isRouterDialed = x;}
	//========================================

	@Config(configName = ConfigController.Config_Router_Encrypt , configType = Config.CONFIG_BOOLEAN)
	private static boolean isEncrypt = true ;

	public static boolean getStateEncrypt(){return isEncrypt;}
	public static synchronized void setStateEncrypt(boolean x){isEncrypt = x ;}
	//========================================
	//路由器验证方式
	@Config(configName = ConfigController.Config_Router_AuthMethod , configType = Config.CONFIG_NUMBER_INT)
	private static int routerAuthMethod = 0;

	public static int getStateRouterAuthMethod(){return routerAuthMethod;}
	public static synchronized void setStateRouterAuthMethod(int a){routerAuthMethod = a;}
	//========================================
	//路由器拨号模式，手动或者自动
	@Config(configName = ConfigController.Config_Router_DialMode , configType = Config.CONFIG_NUMBER_INT)
	private static int dialMode = 2 ; 

	public static int getStateDialMode(){return dialMode ;}
	public static synchronized void setStateDialMode(int d){dialMode = d;}
	
	public static final int DIAL_AUTO = 1 ;
	public static final int DIAL_USER = 2 ;
	
	/*
	enum DialMode{
		DIAL_AUTO,
		DIAL_USER
	}*/

	
	//========================================
	@Config(configName = ConfigController.Config_Dial_is_HeartBeat , configType = Config.CONFIG_BOOLEAN)
	private static boolean isHeartBeat = false ;

	public static boolean getStateHeartBeat(){return isHeartBeat;}
	public static synchronized void setStateHeartBeat(boolean x){isHeartBeat = x;}
	
	//========================================

	public static boolean isOnDebug = false;
	
	public static boolean isConfigExists = false;
	//========================================

	//========================================
	@Config(configName = ConfigController.Config_Dial_Acc_Name)
	public static String sAccName = "";
	@Config(configName = ConfigController.Config_Dial_Acc_Password  ,base64 = true)
	public static String sAccPassword = "";
	@Config(configName = ConfigController.Config_Router_IP)
	public static String sRouterAdd = "192.168.1.1";
	@Config(configName = ConfigController.Config_Router_Name)
	public static String sRouterAcc = "admin";
	@Config(configName = ConfigController.Config_Router_Password,base64 = true)
	public static String sRouterPassword = "";

	//用于本地拨号
	@Config(configName = ConfigController.Config_Acc_Name)
	public static String sLocalAccName = "";
	@Config(configName = ConfigController.Config_Acc_Password)
	public static String sLocalAccPassword = "";

	//========================================
	//=======定时拨号数据

	public static final int ONTIME_MODE_MANUALLY = 0;
	public static final int ONTIME_MODE_BACKGROUND = 1;

	@Config(configName = ConfigController.Config_Ontime_Mode , configType = Config.CONFIG_NUMBER_INT)
	public static int sOntimeMode = ONTIME_MODE_MANUALLY; // 0 for manualy and 1 for background
	@Config(configName = ConfigController.Config_Ontime_SH , configType = Config.CONFIG_NUMBER_INT)
	public static int sStartHour = 7;
	@Config(configName = ConfigController.Config_Ontime_SM , configType = Config.CONFIG_NUMBER_INT)
	public static int sStartMin = 0;
	@Config(configName = ConfigController.Config_Ontime_SD , configType = Config.CONFIG_NUMBER_INT)
	public static int sStartDay = 1 ;
	@Config(configName = ConfigController.Config_Ontime_EH , configType = Config.CONFIG_NUMBER_INT)
	public static int sEndHour = 23;
	@Config(configName = ConfigController.Config_Ontime_EM , configType = Config.CONFIG_NUMBER_INT)
	public static int sEndMin = 30;
	@Config(configName = ConfigController.Config_Ontime_ED , configType = Config.CONFIG_NUMBER_INT)
	public static int sEndDay = 5;
	@Config(configName = ConfigController.Config_Ontime_WifiName)
	public static String sOntimeWifi = "" ;
	@Config(configName = ConfigController.Config_Ontime_WifiKey , base64 = true)
	public static String sOntimeWifiKey = "";

	//=========================================
	/**
	 * */
	public static boolean saveConfig(){

		try{
			Class<?> pClassList[] = {StatusController.class ,AccountController.class};
			for(Class<?> pClass : pClassList){
				Field[] pConfigField = pClass.getDeclaredFields();
				for(Field f : pConfigField){
					Config ic = f.getAnnotation(Config.class);
					if(ic != null){
						if(Modifier.isPrivate(f.getModifiers())){
							f.setAccessible(true);
						}
						String configval = String.valueOf(f.get(null));

						if(ic.base64()){
							configval = Base64.encode(configval);
						}
						ConfigController.updateConfig(ic.configName(),configval);
					}
				}
			}
			Log.log("状态控制器：刷新配置文件完毕");
			return true;
		}catch(Exception e){
			Log.log("状态控制器：刷新配置文件出现错误");
			Log.logE(e);
		}
		return false;
	}
	
	//========================================
		//
	
	public static void initStatus(String szContentPath){
        ConfigController.setConfigPath(szContentPath);
		isConfigExists = ConfigController.loadConfig();
		if(!isConfigExists){
			Log.log("初始化：配置文件不存在");
			return;
		}

		try{
			Class<?> pClassList[] = { StatusController.class ,AccountController.class };
			for(Class<?> pClass : pClassList){
				Field []pFieldList = pClass.getDeclaredFields();
				for(Field f : pFieldList){
					Config ic = f.getAnnotation(Config.class);
					if(ic != null){
						String confval = ConfigController.getConfig(ic.configName());
						if(!confval.equals("")){
							if(ic.base64()){
								confval = Base64.decode(confval);
							}
							if(Modifier.isPrivate(f.getModifiers())){
								f.setAccessible(true);
							}
							Object confvalobj = null;
							String realType = "java.lang." + ic.configType();
							try{
								if(!ic.configType().equals(Config.CONFIG_STRING)){
									Class<?> objDataType = Class.forName(realType);
									Method transVal = objDataType.getMethod("valueOf",String.class);
									confvalobj = transVal.invoke(null , confval);
								}else{
									confvalobj = confval;
								}
								f.set(null, confvalobj);
								//Log.log("状态控制：读取配置 - " + ic.configName() + " 为 " + confval);
							}catch(ClassNotFoundException e){
								Log.log("状态控制：读取配置 - 类型不存在 ： " + realType);
							} catch(NoSuchMethodException e){
								Log.log("状态控制：读取配置 - 方法不存在 ： valueOf 在 类型 " + realType);
							}
						}else{
							Log.log("状态控制：读取配置 - 配置不存在或者为空 ： " + ic.configName());
						}
					}
				}
			}
		}catch(Exception e){
			Log.log("状态控制：读取配置出现异常");
			Log.logE(e);
		}

	}
	
	//
	//========================================
	
	

	public static int parseInt(String x,int defaultVal){
		if(x!=null && !x.equals("")){
			try{
				return Integer.parseInt(x);
			}catch(Exception e){
				Log.logE(e);
			}
		}		
		return defaultVal;
	}
}
