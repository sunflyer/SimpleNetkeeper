package cn.sunflyer.simpnk.control;

import javax.swing.JLabel;

/**
 * 程序状态控制器
 * @author CrazyChen
 * @version 1.5.0
 * @since Simple Netkeeper 1.5.0
 * */
public class StatusController {
	
	//拨号状态
	private static boolean isOnDialing = false ;
	
	/**获取当前正在拨号状态，如果正在拨号，返回true*/
	public static boolean getStateDial(){return isOnDialing;}
	public static synchronized void setStateDial(boolean x){isOnDialing = x;};
	//========================================
	//路由器状态
	private static boolean isRouterDialed = false ;
	
	/**获取当前路由器是否正在拨号，如果是，返回true*/
	public static boolean getStateRouterDial(){return isRouterDialed ;}
	public static synchronized void setStateRouterDial(boolean x){isRouterDialed = x;}
	//========================================
	//是否启用账号加密
	private static boolean isEncrypt = true ;
	
	/**账号是否需要加密*/
	public static boolean getStateEncrypt(){return isEncrypt;}
	public static synchronized void setStateEncrypt(boolean x){isEncrypt = x ;}
	//========================================
	//验证方式
	private static int routerAuthMethod = 0;
	
	/**获取路由器的登录方式*/
	public static int getStateRouterAuthMethod(){return routerAuthMethod;}
	public static synchronized void setStateRouterAuthMethod(int a){routerAuthMethod = a;}
	//========================================
	//拨号模式
	private static int dialMode = 2 ; 
	
	/**获取路由器拨号模式*/
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
	//状态信息显示器
	private static JLabel routerConfigStatusBar = null ;
	
	public static void setComponentRouterConfigStatusBar(JLabel x){routerConfigStatusBar = x;}
	public static void setStatusRouterConfigStatusBar(CharSequence x){
		if(routerConfigStatusBar!=null) routerConfigStatusBar.setText(x.toString());
	}
	
	//========================================
	//本地拨号状态显示器
	
	private static JLabel dialStatusBar = null;
	
	public static void setComponentDialStatusBar(JLabel x){dialStatusBar = x;}
	public static void setStatusDial(CharSequence x){
		if(x!=null && dialStatusBar!=null) dialStatusBar.setText(x.toString()); 
	}
	
	//========================================
	//心跳状态
	private static boolean isHeartBeat = false ;
	
	/**返回是否正在心跳*/
	public static boolean getStateHeartBeat(){return isHeartBeat;}
	public static synchronized void setStateHeartBeat(boolean x){isHeartBeat = x;}
	
	//========================================
	//debug状态
	public static boolean isOnDebug = false;
	
	
	//========================================
	
	//账户信息
	
	public static String sAccName = "";
	public static String sAccPassword = "";
	public static String sRouterAdd = "192.168.1.1";
	public static String sRouterAcc = "admin";
	public static String sRouterPassword = "";
	
	public static String sLocalAccName = "";
	public static String sLocalAccPassword = "";
	/**
	 * 将这里保存的状态变量转移到Properties
	 * */
	public static void saveConfig(){
		
		//帐户名，密码，路由器IP/账号/密码，路由器设备名称/验证方式，本地拨号用户名/密码，使用模式
		String[] pSet = {ConfigController.Config_Dial_Acc_Name,ConfigController.Config_Dial_Acc_Password,
				ConfigController.Config_Router_IP,ConfigController.Config_Router_Name,ConfigController.Config_Router_Password,
				ConfigController.Config_Router_Manufactor,ConfigController.Config_Router_AuthMethod,
				ConfigController.Config_Acc_Name,ConfigController.Config_Acc_Password,
				ConfigController.Config_Router_Encrypt,ConfigController.Config_Router_DialMode};
		
		String[] pVal = {sAccName,Base64.encode(sAccPassword),
				sRouterAdd,sRouterAcc,Base64.encode(sRouterPassword),
				"TP-LINK/水星/FAST",String.valueOf(routerAuthMethod),
				sLocalAccName,Base64.encode(sLocalAccPassword),
				String.valueOf(isEncrypt),String.valueOf(dialMode)};
		
		for(int i=0;i<pSet.length;i++){
			ConfigController.updateConfig(pSet[i], pVal[i]);
		}
	}
	
	//========================================
		//
	public static boolean loadStatus = false;
	
	static{
		loadStatus = ConfigController.loadConfig();
		//加载拨号模式
		String pDialMode = ConfigController.getConfig(ConfigController.Config_Router_DialMode);
		String pAuthMedhod = ConfigController.getConfig(ConfigController.Config_Router_AuthMethod);
		String pRequireEncrypt = ConfigController.getConfig(ConfigController.Config_Router_Encrypt);
		
		setStateDialMode(parseInt(pDialMode,2));
		setStateRouterAuthMethod(parseInt(pAuthMedhod,0));
		setStateEncrypt(!"false".equals(pRequireEncrypt));
		
		if(loadStatus){
			sAccName = ConfigController.getConfig(ConfigController.Config_Dial_Acc_Name);
			sAccPassword = Base64.decode(ConfigController.getConfig(ConfigController.Config_Dial_Acc_Password));
			sRouterAcc = ConfigController.getConfig(ConfigController.Config_Router_Name);
			sRouterPassword = Base64.decode(ConfigController.getConfig(ConfigController.Config_Router_Password));
			sRouterAdd  = ConfigController.getConfig(ConfigController.Config_Router_IP);
			
			sLocalAccName = ConfigController.getConfig(ConfigController.Config_Acc_Name);
			sLocalAccPassword = Base64.decode(ConfigController.getConfig(ConfigController.Config_Acc_Password));
		}	
	}
	
	//
	//========================================
	
	
	/**
	 * 尝试将一个对象转换为整数，如果转换失败则返回给定的默认值。
	 * @param x 要转换的对象
	 * @param defaultVal 转换失败时返回的默认值
	 * @return 返回转换后的整形值，或者指定的默认值
	 * */
	public static int parseInt(String x,int defaultVal){
		if(x!=null && !x.equals("")){
			try{
				return Integer.parseInt(x);
			}catch(Exception e){
				Log.log("尝试转换为数字时出现错误：");
				Log.logE(e);
			}
		}		
		return defaultVal;
	}
}
