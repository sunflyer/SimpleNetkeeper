package cqxinli;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

/**
 * 
 * @author CrazyChen@CQUT
 * 
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
			case MainClass.DIAL_ON_NEED:
			case MainClass.DIAL_ON_TIME:
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

			switch (this.mAuthMethod) {
			case Router.AUTH_WEB: {
				Log.log("程序正在模拟最新固件操作方式");
				URL tar = null;
				HttpURLConnection Tarhuc = null;
				try {
					tar = new URL(URL);
					if (tar != null) {
						Tarhuc = (HttpURLConnection) tar.openConnection();
						if (Tarhuc != null) {
							// 设置管理员的Cookie
							Tarhuc.setRequestProperty(
									"Cookie",
									"Authorization=Basic "
											+ Base64.encode(this.username + ":"
													+ this.password));
							this.setProperties(Tarhuc);
							Log.log("正在尝试请求数据");
							Tarhuc.connect();
							InputStream is = Tarhuc.getInputStream();
							return this.getResponse(is, URL);
						}
						return Router.RES_NO_CONNECTION_OBJ;
					}
				} catch (MalformedURLException ex) {
					Log.logE(ex);
					return Router.RES_IP_INVALID;
				} catch (IOException e) {
					Log.logE(e);
					return Router.RES_IO_EXCEPTION;
				}
			}
			case Router.AUTH_OLD: {
				Log.log("正在尝试旧版本登陆操作");
				URL mRouterUrl = null;
				HttpURLConnection mRouterUrlCon = null;
				try {
					mRouterUrl = new URL(URL);
					mRouterUrlCon = (HttpURLConnection) mRouterUrl
							.openConnection();
					if (mRouterUrlCon != null) {
						mRouterUrlCon.setRequestProperty(
								"Authorization",
								"Basic "
										+ Base64.encode(this.username + ":"
												+ this.password));
						this.setProperties(mRouterUrlCon);
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
			default:
				return Router.RES_AUTHENTICATION_NO_METHED;
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
		Tarhuc.setConnectTimeout(5000);
		Log.log("相关属性已经设置完毕");
	}

	private int getResponse(InputStream is, String URL) {
		int data = 0;
		StringBuffer sb = new StringBuffer();
		try {
			while ((data = is.read()) != -1) {
				sb.append((char) data);
			}
		} catch (IOException e) {
			Log.logE(e);
		}
		String ResponseHTML = sb.toString();
		Log.log("正在处理操作结果");
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
			URL xUrl = null;
			HttpURLConnection xHuc = null;
			try {
				xUrl = new URL(urlStr);
				if (xUrl != null) {
					xHuc = (HttpURLConnection) xUrl.openConnection();
					if (xHuc != null) {
						if (!"".equals(authorizationStr)) {
							// 设置路由器的COOKIE验证
							xHuc.setRequestProperty(
									"Cookie",
									"Authorization=Basic "
											+ Base64.encode(authorizationStr));
							xHuc.setRequestProperty("Content-Length", "0");
							xHuc.setRequestProperty("Content-Type",
									"application/x-www-form-urlencoded");

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
			URL pUrl = new URL(URL);
			HttpURLConnection pHuc = (HttpURLConnection) pUrl.openConnection();

			pHuc.setRequestProperty("Authorization",
					"Basic " + Base64.encode(auth));
			pHuc.setRequestProperty("Content-Length", "0");
			pHuc.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
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
}
