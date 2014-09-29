package cqxinli;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import javax.swing.JOptionPane;

/**
 * @version 1.1
 * @author CrazyChen@CQUT
 * 
 * 这是路由器设置的主要控制类。<br>
 * 控制过程：新建类-》初始化数据（路由器和用户信息）-》测试路由器连接-》设置数据是否已初始化<br>
 * 可完成的功能有：获取当前路由器SSID，获取当前路由器拨号状态，设置路由器拨号，设置路由器无线连接信息<br>
 * 可用构造方法：<br>
 * Router(String IP,String RouterAccName,String RouterAccPassword,String AccName,String AccPassword)<br>
 * 需求参数：IP地址，路由器管理员用户名，密码，宽带帐户名，密码<br>
 * Router()<br>
 * 这个无参构造会设置基本信息为空，并且使得数据状态为未初始化
 */
public class Router {
	private String ip;
	private String username;
	private String password;
	private String dialer;
	private String dialingPWD;
	private CXKUsername un;
	private int mAuthMethod;
	private boolean mIsInit;
	public static final int AUTH_OLD = 401;
	public static final int AUTH_WEB = 402;
	public static final int AUTH_NOT_AVALIABLE = 0;

	public Router(String ip, String username, String pswd, String dialer,
			String dialingPWD) {
		this.ip = ip;
		this.username = username;
		this.password = pswd;
		this.dialer = dialer;
		this.dialingPWD = dialingPWD;
		this.un = new CXKUsername(this.dialer);
		this.mIsInit = true;
		this.mAuthMethod = Router.AUTH_NOT_AVALIABLE;
		runCgi("http://" + this.ip, (this.username + ":" + this.password));
	}

	public Router(String ip, String username, String pswd, String dialer,
			String dialingPWD,int authMethod) {
			this.ip = ip;
			this.username = username;
			this.password = pswd;
			this.dialer = dialer;
			this.dialingPWD = dialingPWD;
			this.un = new CXKUsername(this.dialer);
			this.mIsInit = true;
			this.mAuthMethod = authMethod;
			if(authMethod==Router.AUTH_NOT_AVALIABLE) 
				runCgi("http://" + this.ip, (this.username + ":" + this.password));
	}
	
	public Router() {
		this.mAuthMethod = Router.AUTH_NOT_AVALIABLE;
		this.mIsInit = false;
	}

	public void setRouterData(String ip, String username, String pswd,
			String dialer, String dialingPWD) {
		this.ip = ip;
		this.username = username;
		this.password = pswd;
		this.dialer = dialer;
		this.dialingPWD = dialingPWD;
		this.mIsInit = true;
		runCgi("http://" + this.ip, this.username + ":" + this.password);
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
	 *         - -2 if unable to access the device with the account and password
	 *         user given.<br>
	 *         - -1 if unable to encode the username and password to URL
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
	 *         - 10 if error Unknown
	 */
	public int connect() {
		if (this.mAuthMethod != Router.AUTH_NOT_AVALIABLE && this.mIsInit) {
			String encodeName = null;
			String encodePassword = null;
			if(MainClass.getEncrytedAcc()){
				try {
					Log.log("开始计算加密用户名数据");
					// 替换出现的+为空格，否则用户名错误。
					encodeName = URLEncoder.encode(un.Realusername(), "UTF-8")
							.replace("+", "%2D");
					encodePassword = URLEncoder.encode(this.dialingPWD, "UTF-8");
				} catch (Exception ex) {
					Log.logE(ex);
					return Router.RES_UNABLE_ENCODE;
				}
			}else{
				Log.log("检测到连接模式为《家用模式》");
				try {
					encodeName=URLEncoder.encode(username,"UTF-8").replace("+", "%2D");
					encodePassword=URLEncoder.encode(this.dialingPWD,"UTF-8");
				} catch (UnsupportedEncodingException e) {
					Log.logE(e);
				}
				
			}
			
			// 目标地址，这是设置路由器登陆最必须的条件。
			
			String URL = null;
			switch(MainClass.getDialType()){
			case MainClass.DIAL_AUTO:
				URL="http://"
						+ this.ip
						+ "/userRpm/PPPoECfgRpm.htm?wan=0&wantype=2&acc="
						+ encodeName
						+ "&psw="
						+ encodePassword
						+ "&confirm="
						+ encodePassword
						+ "sta_ip=0.0.0.0&sta_mask=0.0.0.0&linktype=2&Connect=%C1%AC+%BD%D3";
				;break;
			case MainClass.DIAL_BY_USER:
				URL="http://"
						+ this.ip
						+ "/userRpm/PPPoECfgRpm.htm?wan=0&wantype=2&acc="
						+ encodeName
						+ "&psw="
						+ encodePassword
						+ "&confirm="
						+ encodePassword
						+ "sta_ip=0.0.0.0&sta_mask=0.0.0.0&linktype=4&waittime2=0&Connect=%C1%AC+%BD%D3";
				;break;
			default:return Router.RES_NO_DIAL_MODE;
			}
			//linktype=2 : 自动连接
			
			Log.log("检查到的连接类型为："+MainClass.getDialType());
			try {
				HttpURLConnection mRouterUrlCon =this.getConnection(URL);
				if (mRouterUrlCon != null) {
					if(this.setDialProperty(mRouterUrlCon)!=0) return Router.RES_NO_DIAL_MODE;
					Log.log("正在尝试请求数据");
					mRouterUrlCon.connect();
					return this.getResponse(mRouterUrlCon.getInputStream(),
							URL);
				}
				return Router.RES_NO_CONNECTION_OBJ;
			} catch (MalformedURLException e) {
				Log.logE(e);
				return Router.RES_IP_INVALID;
			} catch (IOException e) {
				Log.logE(e);
				return Router.RES_IO_EXCEPTION;
			}
		}
		Log.log(this.mIsInit ? "无法通过现有方式访问路由器，可能是因为账户错误，或者不支持。"
				: "检测到用户数据未被初始化，操作已经停止");
		return this.mIsInit ? Router.RES_UNABLE_ACCESS : Router.RES_META_DATA_NOT_INIT;
	}

	private void setProperties(HttpURLConnection Tarhuc) {
		// 设置引用页避免权限错误
		Tarhuc.setRequestProperty("Referer", "http://" + this.ip + "/");
		Tarhuc.setRequestProperty("Host", this.ip);
		Tarhuc.setRequestProperty("Connection", "Keep-alive");
		Tarhuc.setRequestProperty("Content-Length", "0");
		Tarhuc.setRequestProperty("Content-Type",
				"application/x-www-form-urlencoded");
		Tarhuc.setConnectTimeout(2000);
		Log.log("相关属性已经设置完毕");
	}

	
	/** 
	 * 返回一个连接状态，在Connect方法之后使用
	 * */
	private int getResponse(InputStream is, String URL) {
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
			new FormTips(URL);
		}
		return Router.RES_REQUIRE_LOGIN;
	}
	
	
	/** 
	 * 获取从远程计算机下载的HTML文本数据。需要一个输入流
	 * */
	private String getHTMLContent(InputStream is){
		int data = 0;
		String ResponseHTML = null;
		try {
			BufferedReader pBufRd=new BufferedReader(new InputStreamReader(is,MainClass.getRouterPageEncode()));
			StringBuffer sb = new StringBuffer();
			while ((data = pBufRd.read()) != -1) {
				sb.append((char) data);
			}
			ResponseHTML = sb.toString();
			Log.log(ResponseHTML);
		} catch (IOException e) {
			Log.logE(e);
		}
		return ResponseHTML;
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
	 * @param urlStr
	 *            : The remote address to configure.
	 * @param authorizationStr
	 *            : Username and password (for access network)
	 */
	private void runCgi(String urlStr, String authorizationStr) {
		if (this.mIsInit) {
			Log.log("正在尝试以新版本固件的方式处理操作数据");
			HttpURLConnection xHuc = null;
			try {
					xHuc = this.getConnection(urlStr);
					if (xHuc != null) {
						if (!"".equals(authorizationStr)) {
							// 设置路由器的COOKIE验证
							xHuc.setRequestProperty(
									"Cookie",
									"Authorization=Basic "
											+ Base64.encode(authorizationStr));
						}
						Log.log("开始尝试第一次连接");
						xHuc.connect();
						InputStream in = xHuc.getInputStream();
						int chint = 0;
						StringBuffer sb = new StringBuffer();
						while ((chint = in.read()) != -1) {
							sb.append((char) chint);
						}
						String html = sb.toString();
						Log.log("检查是否可用");
						// 设置可用 如果检测到登陆成功后的框架代码
						// set it available if detected keyword that appear in
						// the
						// page which means login success.
						if (html.indexOf("noframe") > 0
								|| html.indexOf("frame") >= 0) {
							this.changeAuthMethod(AUTH_WEB);
						} else {
							// 尝试旧版本
							this.changeInitState(false);
						}
						// DEBUG用，输出调试数据
						Log.log(this.mAuthMethod + Log.nLine + "Basic "
								+ Base64.encode(authorizationStr) + Log.nLine
								+ html);
					}
			} catch (MalformedURLException e) {
				this.changeInitState(false);
				Log.logE(e);
			} catch (IOException e) {
				this.changeInitState(false);
				Log.logE(e);
				this.detectOld(urlStr, authorizationStr);
			}
		} else {
			this.changeAuthMethod(Router.AUTH_NOT_AVALIABLE);
		}
	}

	private void changeInitState(boolean ii) {
		this.mIsInit = ii;
		Log.log("已经更改路由器连接初始化状态为：" + (ii ? "已初始化" : "未初始化"));
		if (!ii)
			changeAuthMethod(Router.AUTH_NOT_AVALIABLE);
	}

	private void changeAuthMethod(int Au) {
		this.mAuthMethod = Au;
		MainClass.setAuthMethod(Au);
		Log.log("已更改路由器验证方式为：" + Au);
		if(Au!=Router.AUTH_NOT_AVALIABLE) 
			this.changeInitState(true);
	}

	private void detectOld(String URL, String auth) {
		try {
			Log.log("尝试以旧版本的方式检测可用性");
			HttpURLConnection pHuc = this.getConnection(URL);

			pHuc.setRequestProperty("Authorization",
					"Basic " + Base64.encode(auth));
			pHuc.connect();
			InputStream in = pHuc.getInputStream();
			StringBuffer sb = new StringBuffer();
			int chint;
			while ((chint = in.read()) != -1) {
				sb.append((char) chint);
			}
			String html = sb.toString();
			if (html.indexOf("noframe") > 0 || html.indexOf("frame") >= 0){
				this.changeAuthMethod(Router.AUTH_OLD);
			}
			else {
				this.changeInitState(false);
				Log.log(html);
			}
		} catch (IOException e) {
			Log.logE(e);
		}

	}
	
	/** 
	 * 跟踪PPPoE连接状态
	 * */
	public void trackLink(){
		boolean getData=true;
		while(getData){
			try {
				HttpURLConnection mHuc=getConnection("http://"+ip+"/userRpm/PPPoECfgRpm.htm");
				if(setDialProperty(mHuc)!=0){
					DataFrame.showTips("没有合适的连接方式，启动跟踪失败");
				}else{
					mHuc.connect();
					String mContent=getHTMLContent(mHuc.getInputStream());
					if(getResponseData(mContent, "正在连接")){
						DataFrame.showTips("正在建立连接中");
					}else if(getResponseData(mContent,"已连接")){
						DataFrame.showTips("已经连接");
						getData=false;
					}else if(getResponseData(mContent,"服务器") || getResponseData(mContent,"响应")){
						DataFrame.showTips("检测到服务器没有响应，你需要重新设置连接。");
						getData=false;
					}else if(getResponseData(mContent,"用户名") || getResponseData(mContent,"密码") || getResponseData(mContent,"错误")){
						DataFrame.showTips("用户名或者密码错误");
						getData=false;
					}
					/*
					 * else if(getResponseData(mContent,"")){
						DataFrame.showTips("");
						getData=false;
					}
					 * */
				}
			} catch (MalformedURLException ex) {
				Log.logE(ex);
				getData=false;
			} catch (IOException ex) {
				Log.logE(ex);
				getData=false;
			}
		}
	}
	
	/** 
	 * 获取当前WIFI信息，包括WIFI热点名称，热点密码
	 * */
	public WiFiInfo getWifiState(){
		WiFiInfo tWi=null;
		String SSID=null;
		String HTML=null;
		try {
			//先获取WIFI名称
			HttpURLConnection tHuc=this.getConnection("http://"+this.ip+"/userRpm/WlanNetworkRpm.htm");
			if(this.setDialProperty(tHuc)!=0) return null;
			tHuc.connect();
			HTML=this.getHTMLContent(tHuc.getInputStream());
			String keyWord="var wlanPara=new Array(";
			int tIndex=HTML.indexOf(keyWord);
			if(tIndex>0){
				//获取第三个“，”的位置
				tIndex+=keyWord.length();
				for(int i=0;i<3;i++){
					tIndex=HTML.indexOf(",",tIndex);
				}
				//第四个
				int fourthIndex=tIndex+HTML.indexOf(",",tIndex);
				SSID=HTML.substring(tIndex, fourthIndex).replace("\"", "");
				String[] tmp=SSID.split(",");
				SSID=tmp[3].trim();
				
				tWi=new WiFiInfo(SSID,"",false);
			}
		} catch (IOException e) {
			Log.logE(e);
			Log.log(HTML);
		}
		return tWi;
	}
	
	private static final String PAGE_CONFIG_WLAN_SEC="http://%IP%/userRpm/WlanSecurityRpm.htm?secType=3&pskSecOpt=2&pskCipher=3&pskSecret=%KEY%&interval=1800&Save=%B1%A3+%B4%E6";
	private static final String PAGE_CONFIG_WLAN_NETWORK="http://%IP%/userRpm/WlanNetworkRpm.htm?ssid1=%SSID%&wlMode=2&channel=0&mode=5&chanWidth=2&ap=1&brlssid=&brlbssid=&detctwds=1&keytype=1&wepindex=1&keytext=%HIDESSID%&Save=%B1%A3+%B4%E6";
	private static final String PAGE_VAR_IP="%IP%";
	private static final String PAGE_VAR_SSID="%SSID%";
	private static final String PAGE_VAR_KEY="%KEY%";
	private static final String PAGE_VAR_HIDESSID="%HIDESSID";
	private static final String PAGE_VAR_HIDESSID_DATA="&broadcast=2";
	
	public static final int SET_SUCCESS=0;
	public static final int SET_DATA_ERROR=1;
	public static final int SET_NO_DIAL_MODE=2;
	/** 
	 * 设置无线网络连接信息，SSID，密码以及是否隐藏你的无线网络<br>
	 * 参数 WiFiInfo
	 * */
	public void setWifiState(WiFiInfo pW){
		//密码配置地址：http://172.16.17.1/userRpm/WlanSecurityRpm.htm?
		//secType=3&pskSecOpt=2&pskCipher=3&pskSecret=CYXnetkeeperA617&interval=1800
		//&Save=%B1%A3+%B4%E6
		String tConfigPassword=Router.PAGE_CONFIG_WLAN_SEC.replace(PAGE_VAR_IP, this.ip).replace(PAGE_VAR_KEY, pW.getWifiPassword());
		try {
			HttpURLConnection tConfigPwdHuc=this.getConnection(tConfigPassword);
			if(this.setDialProperty(tConfigPwdHuc)!=0) return;
			else{
				tConfigPwdHuc.connect();
				boolean tRes=this.getResponseData(tConfigPwdHuc.getInputStream(), pW.getWifiName());
				DataFrame.showTips(tRes?"修改热点成功":"修改热点失败");
			}
		} catch ( IOException e) {
			Log.logE(e);
		}		
		
		try {
			JOptionPane.showMessageDialog(null,"已尝试修改你的无线路由器密码，由于安全原因，现在可能会被断开连接\n\n请现在连接你的无线路由器，45秒钟后系统会自动继续修改SSID操作");
			this.wait(45000);
		} catch (InterruptedException e1) {
			Log.logE(e1);
		}
		//SSID：http://172.16.17.1/userRpm/WlanNetworkRpm.htm?
		//ssid1=ChenYX&wlMode=2&channel=0&mode=5&chanWidth=2&ap=1&brlssid=
		//&brlbssid=&detctwds=1&keytype=1&we
		//pindex=1&keytext=&broadcast=2&Save=%B1%A3+%B4%E6
		String tConfigSSID=Router.PAGE_CONFIG_WLAN_NETWORK.replace(PAGE_VAR_IP, this.ip).replace(PAGE_VAR_SSID, pW.getWifiName()).replace(PAGE_VAR_HIDESSID, pW.isWifiBroadCast()?"":PAGE_VAR_HIDESSID_DATA);
		try {
			HttpURLConnection tConfigSSIDHuc=this.getConnection(tConfigSSID);
			if(this.setDialProperty(tConfigSSIDHuc)!=0) return;
			else{
				tConfigSSIDHuc.connect();
				boolean tRes=this.getResponseData(tConfigSSIDHuc.getInputStream(), pW.getWifiName());
				DataFrame.showTips(tRes?"成功修改热点名":"修改热点名称失败（也有可能是检测错误，请自行检查连接！）");
				Log.log("修改热点名称的最终参考结果为："+tRes);
			}
		} catch (MalformedURLException e) {
			Log.logE(e);
		} catch (IOException e) {
			Log.logE(e);
		}
	}
	
	private boolean  getResponseData(InputStream is,String Data){
		return this.getResponseData(this.getHTMLContent(is), Data);
	}
	
	private boolean getResponseData(String meta,String data){
		return meta.indexOf(data)>=0;
	}
	
	/** 
	 * Returns a HttpURLConnection Object,Requires a valid URL for generating.<br/>
	 * This operation will set properties at the same time<br/>
	 * 返回一个HttpURLConnection对象，需要一个合法的URL。同时会设置一些属性，但不包括验证字符串
	 * */
	private HttpURLConnection getConnection(String URL) throws MalformedURLException, IOException{
		HttpURLConnection tHuc=(HttpURLConnection)(new URL(URL).openConnection());
		if(tHuc!=null){
			this.setProperties(tHuc);
		}
		return tHuc;
	}
	
	
	/** 
	 * 设置验证属性，如果设置失败，会返回非0值。
	 * */
	private int setDialProperty(HttpURLConnection mRouterUrlCon){
		switch(this.mAuthMethod){
		case Router.AUTH_OLD:mRouterUrlCon.setRequestProperty(
				"Authorization",this.getBase64Acc());return 0;
		case Router.AUTH_WEB:mRouterUrlCon.setRequestProperty(
				"Cookie",
				"Authorization="+ this.getBase64Acc());return 0;
		default:return Router.RES_NO_DIAL_MODE;
		}
		
	}
	
	/** 
	 * 获取路由器验证字符串
	 * */
	private String getBase64Acc(){
		return "Basic "+Base64.encode(this.username + ":"
											+ this.password);
	}
}
