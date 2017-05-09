package cn.sunflyer.simpnk.control;

public class AppInfo {

	public static final int APP_VERSION_MAIN = 1 ; //主版本号
	public static final int APP_VERSION_SUB = 2 ; //子版本号
	public static final int APP_VERSION_FIX = 0 ; //修正版本
	public static final String APP_VERSION_BUILD = "56" ; //build
	
	public static String getVersionString(){
		return APP_VERSION_MAIN+"."+APP_VERSION_SUB+"."+APP_VERSION_FIX+"."+APP_VERSION_BUILD;
	}
	
	public static final String APP_VERSION_ST[] = {"正式版本","测试版本"};
	
	public static final int APP_VERSION_ST_CODE = 1 ;
	
	public static String getVersionStringWithRel(){
		return getVersionString() + (APP_VERSION_ST_CODE >= APP_VERSION_ST.length ? "Unknown" : APP_VERSION_ST[APP_VERSION_ST_CODE]);
	}
	
	
	public static final String APP_VERSION_DATE = "2015-03-20 13:00"; //版本日期
	
	/**
	 * 程序集信息
	 * */
	public static final String APP_NAME = "Simple Netkeeper";
	public static final String APP_DES = "";
	
	public static final String APP_WEB_UPDATE = "http://www.sunflyer.cn/archives/8";
	public static final String APP_WEB_HELP = "http://www.sunflyer.cn/";
	public static final String APP_WEB_DEV = "http://www.sunflyer.cn/";
	
	
	/**
	 * 作者信息
	 * */
	public static final String APP_AUTHOR_NAME = "CrazyChen @ CQUT";
	public static final String APP_AUTHOR_MAIL = "cx@itncre.com";
	public static final String APP_AUTHOR_WEB = "http://www.sunflyer.cn";
	
	public static final String APP_AUTHOR_DES = "程序源代码已经公开在  https://github.com/sunflyer/simplenetkeeper ";
	
	
}
