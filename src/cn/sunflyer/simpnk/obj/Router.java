package cn.sunflyer.simpnk.obj;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import cn.sunflyer.simpnk.control.Base64;
import cn.sunflyer.simpnk.control.Log;
import cn.sunflyer.simpnk.control.MessageController;
import cn.sunflyer.simpnk.control.StatusController;


/**
 * @version 1.1.2
 * @author CrazyChen@CQUT
 * 
 * 这是路由器设置的主要控制类。<br>
 * 控制过程：新建类-》初始化数据（路由器和用户信息）-》测试路由器连接-》设置数据是否已初始化<br>
 * 可完成的功能有：获取当前路由器SSID，获取当前路由器拨号状态，设置路由器拨号，设置路由器无线连接信息<br>
 * 跟踪链接状况<br>
 */
public abstract class Router {
	
	protected String gRouterIP;
	protected String gRouterAccName;
	protected String gRouterAccPassword;
	protected String gAccName;
	protected String gAccPassword;
	
	protected int mAuthMethod;
	protected boolean mIsInit;
	
	/**旧版本HTTP 401 验证方式*/
	public static final int AUTH_OLD = 401;
	/**网页COOKIE验证方式*/
	public static final int AUTH_WEB = 402;
	/**网页COOKIE验证方式，但是只BASE64密码*/
	public static final int AUTH_PASSWORD_ONLY = 403;
	/**动态ID模式*/
	public static final int AUTH_DYNAMIC_ID = 404;
	/**不可用的验证*/
	public static final int AUTH_NOT_AVALIABLE = 0;
	
	protected Router(String RouterID,String RouterKey,String IP,String AccName,String AccPassword){
		this(RouterID, RouterKey, IP, AccName, AccPassword, Router.AUTH_NOT_AVALIABLE);
	}
	
	protected Router(String RouterID,String RouterKey,String IP,String AccName,String AccPassword,int AuthMethod){
		this.gRouterAccName=RouterID;
		this.gRouterAccPassword=RouterKey;
		this.gRouterIP=IP;
		this.gAccName=AccName;
		this.gAccPassword=AccPassword;
		this.mIsInit=true;
		if((this.mAuthMethod=AuthMethod)==Router.AUTH_NOT_AVALIABLE){
			this.testLink();
		}	
	}
	
	
	/**
	 * <p>
	 * 初始化路由器可连接情况。
	 * </p>
	 * <p>
	 * Initial whether the router is available to be operated.
	 * </p>
	 * <p>
	 * 路由器的验证方式：输入用户名和密码以后，调用本地JS进行BASE64加密，加密内容为
	 * Base64.Encode(用户名:密码)，然后设置COOKIE，刷新本地页面。<br>
	 * 由于刷新时自动提交COOKIE，因此可以将 验证任务放在客户端处理
	 * </p>
	 * <p>
	 * The authorization method for router:after user input the user name and
	 * password,the login page use local JavaScript method to encrypt these info
	 * with Base64<br>
	 * and set cookie.The Content encrypted is Base64(username:password),and
	 * then refresh local page.<br>
	 * Due to the cookie is uploaded automatically,the authorization can be
	 * simply processed by client
	 * </p>
	 * 
	 */
	protected abstract void testLink();
	
	/**
	 * 检测旧版本的固件可用性<br>
	 * 旧版本使用HTTP 401 Authorization Basic验证方式，也就是见到的弹框输入用户名和密码
	 * @throws Exception 如果该版本固件不支持此操作，将抛出此异常
	 * */
	protected abstract void detectOld() throws Exception;
	
	/**
	 * 修改初始化的状态。
	 * */
	protected void changeInitState(boolean ii) {
		this.mIsInit = ii;
		Log.log("已经更改路由器连接初始化状态为：" + (ii ? "已初始化" : "未初始化"));
		if (!ii)
			changeAuthMethod(Router.AUTH_NOT_AVALIABLE);
	}
	
	/**
	 * 获取用于路由器的验证方式
	 * */
	public static int getRouterAccessMethod(String ip){
		
		try {
			HttpURLConnection pC = (HttpURLConnection)new URL("http://"+ip).openConnection();
			
			pC.connect();
			
			BufferedReader pBr = new BufferedReader(new InputStreamReader(pC.getInputStream()));
			StringBuffer pSb = new StringBuffer();
			
			int pTmp = 0;
			while((pTmp = pBr.read()) != -1){
				pSb.append((char)pTmp);
			}
			
			String pHtml = pSb.toString();
			
			if(StatusController.isOnDebug)
				Log.log(pHtml);
			
			if(pHtml.contains("dynaform/class.js")){
				return AUTH_DYNAMIC_ID;
			}else if(pHtml.contains("cookie")){
				return AUTH_WEB;
			}
			
		}catch (IOException e) {
			Log.logE(e);
			if(e.getMessage().contains("401"))
				return AUTH_OLD;
		}		
		return AUTH_NOT_AVALIABLE;
	}

	/**
	 * 修改验证方式
	 * */
	protected void changeAuthMethod(int Au) {
		this.mAuthMethod = Au;
		StatusController.setStateRouterAuthMethod(Au);
		Log.log("已更改路由器验证方式为：" + Au);
		if(Au!=Router.AUTH_NOT_AVALIABLE) 
			this.changeInitState(true);
	}
	
	public static final int RES_UNABLE_ACCESS=-2;
	public static final int RES_UNABLE_ENCODE=-1;
	public static final int RES_SUCCESS=0;
	public static final int RES_IP_INVALID=1;
	public static final int RES_NO_DIAL_MODE=2;
	public static final int RES_NO_AUTHORITY=3;
	public static final int RES_IO_EXCEPTION=4;
	public static final int RES_NO_CONNECTION_OBJ=5;
	public static final int RES_AUTHENTICATION_NO_METHED=6;
	public static final int RES_META_DATA_NOT_INIT=7;
	public static final int RES_ALGRITHOM_NOT_ALLOWED=8;
	public static final int RES_REQUIRE_LOGIN=9;
	public static final int RES_ERROR_UNKNOWN=10;
	
	/**
	 * @return The connection statement of the router you configured.<br>
	 * 		   The beginning of the returns variable is "RES_"	<br>
	 *         - -2 if unable to access the device with the account and password
	 *         user given.<br>
	 *         - -1 if unable to encode the user name and password to URL
	 *         Encoding<br>
	 *         - 0 if configuration success.<br>
	 *         - 2 if dialing mode is not pointed<br>
	 *         - 1 if IP address is not valid<br>
	 *         - 3 if the router returns that no authority to access this
	 *         device.(Always caused by the ROM rejected the access even though
	 *         your name and password is right)<br>
	 *         - 4 if InputStream processing error (IOException Occurred)<br>
	 *         - 5 if application can not get Connection Object<br>
	 *         - 6 if no authentication method available<br>
	 *         - 7 if Meta Data not initialized Correctly;<br>
	 *         - 8 if the Algrithom for calculating truly account is not allowed<br>
	 *         - 9 if the application detected another login request to complete
	 *         this operation, or some routers limited this functions that
	 *         permission denied.<br>
	 *         - 10 if error Unknown<br>
	 */
	public abstract int connect();
	
	public static final int CONNECTION_NOT_CONNECTED=0;
	public static final int CONNECTION_SUCCESS=1;
	public static final int CONNECTION_NO_RESPONSE=4;
	public static final int CONNECTION_AUTHENTICATION_FAILED=3;
	public static final int CONNECTION_UNKNOWN=5;
	public static final int CONNECTION_NOT_CONNECTED_WAN=6;
	public static final int CONNECTION_OPERATION_NO_MODE=10;
	public static final int CONNECTION_OPERATION_EXCEPTION=11;
	public static final int CONNECTION_CONNECTING=2;
	/**
	 * 返回当前网络连接状态,标识符开头：CONNECTION_
	 * */
	public abstract int getConnectionState();
	
	
	public static final String CONNECTION_VAR_LINKSTAT="\"0.0.0.0\",";
	
	public static final String[] PPPoELinkStat={
		"未连接",
		"已连接",
		"正在连接",
		"用户名或密码验证失败",
		"服务器无响应",
		"未知原因失败",
		"WAN口未连接"
	};
	
	
	/** 
	 * 跟踪PPPoE连接状态<br>
	 * 在尝试20次没有获取到连接成功或者失败以后，会提示超时<br>
	 * 由于判断逻辑的问题，这个判断方式可能存在一定的误报率，<br>
	 * */
	public abstract void trackLink();
	
	/** 
	 * 获取从远程计算机下载的HTML文本数据。需要一个输入流
	 * */
	public String getHTMLContent(InputStream is){
		int data = 0;
		String ResponseHTML = null;
		try {
			BufferedReader pBufRd=new BufferedReader(new InputStreamReader(is,"GB2312"));
			StringBuffer sb = new StringBuffer();
			while ((data = pBufRd.read()) != -1) {
				sb.append((char) data);
			}
			ResponseHTML = sb.toString();
		} catch (IOException e) {
			Log.logE(e);
		}
		return ResponseHTML;
	}
	
	
	/**
	 * 设置HttpURLConnection对象的请求属性
	 * 包括引用（Referrer），超时（默认2000）
	 * */
	protected void setProperties(HttpURLConnection Tarhuc) {
		this.setProperties(Tarhuc, 4000);
	}
	
	protected void setProperties(HttpURLConnection Tarhuc,int timeOut){
		// 设置引用页避免权限错误
				Tarhuc.setRequestProperty("Referer", "http://" + this.gRouterIP + "/");
				Tarhuc.setRequestProperty("Host", this.gRouterIP);
				Tarhuc.setRequestProperty("Connection", "Keep-alive");
				Tarhuc.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
				Tarhuc.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.3; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2251.0 Safari/537.36");
				Tarhuc.setConnectTimeout(timeOut);
	}
	
	/** 
	 * 返回一个连接状态，在Connect方法之内使用
	 * */
	protected int getResponse(InputStream is, String URL) {
		String ResponseHTML=this.getHTMLContent(is);
		if (ResponseHTML
				.indexOf("You have no authority to access this device!") >= 0) {
			Log.log("检测到关键字：无授权访问");
			return Router.RES_NO_AUTHORITY;
		} else if (ResponseHTML.indexOf("noframe") >= 0
				|| ResponseHTML.indexOf("已连接") >= 0
				|| ResponseHTML.indexOf("PPPoECfgRpm.htm") >= 0) {
			return Router.RES_SUCCESS;
		} else if (ResponseHTML.indexOf("loginBox") >= 0) {
			Log.log("检测到额外登陆操作");
//			new FormTips(URL);
		}
		return Router.RES_REQUIRE_LOGIN;
	}
	
	
	public static final int SET_SUCCESS=0;
	public static final int SET_DATA_ERROR=1;
	public static final int SET_NO_DIAL_MODE=2;
	/** 
	 * 设置无线网络连接信息，SSID，密码以及是否隐藏你的无线网络<br>
	 * 参数 WiFiInfo.
	 * */
	public abstract void setWifiState(WiFiInfo pW);
	
	public abstract WiFiInfo getWifiState();
	
	
	/**
	 * 设置显示的提示内容
	 * */
	protected void setState(String r){
		sendMessage(r);
	}
	
	/**
	 * 获取响应数据
	 * */
	protected boolean  getResponseData(InputStream is,String Data){
		return this.getResponseData(this.getHTMLContent(is), Data);
	}
	
	protected boolean getResponseData(String meta,String data){
		return meta.indexOf(data)>=0;
	}
	
	/** 
	 * Returns a HttpURLConnection Object,Requires a valid URL for generating.<br/>
	 * This operation will set properties at the same time<br/>
	 * 返回一个HttpURLConnection对象，需要一个合法的URL。同时会设置一些属性，但不包括验证字符串
	 * */
	protected HttpURLConnection getConnection(String URL) throws MalformedURLException, IOException{
		HttpURLConnection tHuc=(HttpURLConnection)(new URL(URL).openConnection());
		if(tHuc!=null){
			this.setProperties(tHuc);
		}
		return tHuc;
	}
	

	/** 
	 * 设置验证属性，如果设置失败，会返回非0值。
	 * */
	protected abstract int setDialProperty(HttpURLConnection mRouterUrlCon);
	
	/** 
	 * 获取路由器验证字符串，根据需要重载此方法。
	 * */
	protected String getBase64Acc(){
		return "Basic "+Base64.encode(this.gRouterAccName + ":"
											+ this.gRouterAccPassword);
	}
	
	/**
	 * 返回服务器URL,即 http://ip/
	 * */
	protected String getSvrURL(){
		return "http://"+this.gRouterIP+"/";
	}
	
	/**
	 * 清除字符串数组中的多余空格 trim
	 * */
	public static void trimString(String[] pStr){
		for(int i=0;i<pStr.length;i++){
			pStr[i]=pStr[i].trim();
		}
	}
	
	protected boolean writePostParam(HttpURLConnection thuc,String param){
		try {
			thuc.setRequestMethod("POST");
			thuc.setDoOutput(true);// 是否输入参数
			thuc.getOutputStream().write(param.getBytes());
		} catch (IOException e) {
			Log.logE(e);
			return false;
		}		
		return true;
	}
	
	protected static void sendMessage(String pMsg){
		MessageController.getInstance().sendMessage(MessageController.MessageCode.LOG_AND_TIPS_ROUTER,new Message(new Bundle("info",pMsg)));
	}
}
