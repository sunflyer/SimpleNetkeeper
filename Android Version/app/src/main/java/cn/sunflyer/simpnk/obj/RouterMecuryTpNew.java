package cn.sunflyer.simpnk.obj;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.util.Hashtable;

import cn.sunflyer.simpnk.control.AccountController;
import cn.sunflyer.simpnk.control.Log;
import cn.sunflyer.simpnk.control.StatusController;
import cn.sunflyer.simpnk.netkeeper.CXKUsername;

/**
 * 新版水星/TP路由器固件处理类
 * */
public class RouterMecuryTpNew extends Router{

	/**用于验证的动态ID，请使用
	 * generateDynamicId()方法修改此变量。
	 * */
	private String mDynamicId = null;

	/**
	 * 登陆失败次数统计，直到登陆成功后清除
	 * */
	//private int mLoginFailed = 0;

	/**
	 * 重试上限次数
	 * */
	public static int mMaxRetry = 4;

	/**
	 * 路由器的请求代码
	 * */
	public static final int SYN = 0, //同步数据
			ASYN = 1, //异步数据
			TDDP_INSTRUCT = 0,
			TDDP_WRITE = 1, //修改数据
			TDDP_READ = 2, //读取状态数据
			TDDP_UPLOAD = 3, //上传
			TDDP_DOWNLOAD = 4, //下载
			TDDP_RESET = 5, //重置
			TDDP_REBOOT = 6, //重启
			TDDP_AUTH = 7, //验证ID合法性
			TDDP_GETPEERMAC = 8, //获取端点MAC
			TDDP_CONFIG = 9,
			TDDP_CHGPWD = 10,
			TDDP_LOGOUT = 11,
			PARSE_INIT = 0,
			PARSE_NOTE = 1,
			PARSE_CMD = 2,
			PARSE_ID = 3,
			PARSE_INDEX = 4,
			PARSE_VALUE = 5,
			PARSE_ERR = 6;

	private boolean isDebug = true;

	public RouterMecuryTpNew() {
		super("admin", StatusController.sRouterPassword, StatusController.sRouterAdd, StatusController.sAccName, StatusController.sAccPassword , AUTH_DYNAMIC_ID);
		this.isDebug = StatusController.isOnDebug;
		testLink();
	}

	@Override
	protected void testLink() {
		if(this.loadDynamicFile() && this.authRouter()){
			sendMessage("新版本固件方式：登陆成功！");
		}else{
			sendMessage("新版本固件方式：路由器身份验证失败！（检查你输入的路由器管理员密码，此密码为你登陆路由器网页的密码。）");
			this.clearDynamicId();
			this.changeAuthMethod(Router.AUTH_NOT_AVALIABLE);
		}

	}

	public void clearDynamicId(){
		this.mDynamicId = null;
	}

	@Override
	protected void detectOld() throws Exception{
		throw new UnsupportedOperationException("该版本不支持旧版验证方式");
	}

	@Override
	public int connect() {
		if(this.mDynamicId!=null && this.gAccName != null && !this.gAccName.equals("") && this.gAccPassword != null && !this.gAccPassword.equals("")){

			if(this.getConnectionState() == 1) return RES_SUCCESS;

			//if(!this.changeLinkMode(DIAL_MODE_PPPOE)) return CONNECTION_CHANGE_MODE;

			try {

				String pReqAddr = this.getSvrURL() + "?code="+TDDP_WRITE+"&asyn=0&id="+this.mDynamicId;

				String pReqData = "id 26\r\nsvName \r\nacName \r\nname "+URLEncoder.encode(AccountController.getRealAccount(this.gAccName),"UTF-8").replace("+", "%20")+
						"\r\npaswd "+URLEncoder.encode(this.gAccPassword,"UTF-8")+
						"\r\nfixipEnb 0\r\nfixip 0.0.0.0\r\nmanualDns 0\r\ndns 0 0.0.0.0\r\ndns 1 0.0.0.0\r\nlcpMru 1480\r\nlinkType 3\r\nmaxIdleTime 0\r\nid 22\r\nlinkMode 0\r\nlinkType 2\r\n"; //dialMode 0


				if(isDebug){
					Log.log(pReqData);
				}

				String pResData = this.requestData(pReqAddr, pReqData);

				if(pResData!=null && pResData.contains("00000")){

					sendMessage("设置成功！");

					pResData = this.requestData(this.getSvrURL()+"?code=0&asyn=0&id="+this.mDynamicId, "wan -linkUp");

					return pResData.contains("00000") ? RES_SUCCESS : RES_UNABLE_ACCESS;
				}else{
					sendMessage("设置失败！");
					return RES_UNABLE_ACCESS;
				}

			} catch (UnsupportedEncodingException e) {
				Log.logE(e);
			}

		}
		return RES_NO_AUTHORITY;
	}

	public static final int DIAL_MODE_AUTO_IP = 1;
	public static final int DIAL_MODE_PPPOE = 2;

	/**修改连接模式*/
	public boolean changeLinkMode(int mode){
		String pReqChange = this.getSvrURL();
		switch (mode){
			case DIAL_MODE_AUTO_IP:
				pReqChange +="BasicDynamicIp.htm?id=" + this.mDynamicId;
				break;
			case DIAL_MODE_PPPOE:
				pReqChange += "PPPoE.htm?id=" + this.mDynamicId ;
				break;
			default:return false;
		}
		String pRes = this.requestData(pReqChange , "");
		return pRes != null;
	}

	/**切换到自动获取IP模式*/
	public boolean setInternal(){
		//if(changeLinkMode(DIAL_MODE_AUTO_IP)){
		String pRes = this.requestData(this.getSvrURL()+"?code=1&asyn=0&id=" + this.mDynamicId,"id 22\r\nlinkMode 0\r\nlinkType 0\r\n");
		Log.log("内网切换：已发送数据");
		return pRes == null ? false : pRes.contains("00000");
		//}
		//Log.log("内网切换：更改模式失败");
		//return false;
	}

	@Override
	public int getConnectionState() {
		if(this.isAuthed()){

			this.loadStatus();
			return this.mStatus.get("status") == null ? CONNECTION_UNKNOWN : Integer.parseInt(this.mStatus.get("status"));

		}
		return CONNECTION_UNKNOWN;
	}


	/**链路状态，对应status*/
	public static final int LINK_DOWN = 0,
			LINK_UP = 1,
			LINKING_UP = 2,
			LINKING_DOWN = 3;

	/**链路状态字符串*/
	public static final String[] LINK_STATUS = {"已断开连接","连接成功","正在连接","正在断开连接"};

	/**错误代码，对应CODE*/
	public static final int LINK_CODE_NORMAL = 0,
			LINK_CODE_MANUAL = 1,
			LINK_CODE_UNKNOWN = 2,
			LINK_CODE_DENY = 3,
			LINK_CODE_PHYDOWN = 4,
			LINK_CODE_NOECHO = 5,
			LINK_CODE_SRVDOWN = 6,
			LINK_CODE_OPT_UNSUPPORT = 7,
			LINK_CODE_AUTH_ERR = 8,
			LINK_CODE_AUTH_UNSUPPORT = 9,
			LINK_CODE_IP_CONFLICT = 10;

	public static final String[] LINK_CODE = {"状态正常","手动断开连接","未知错误","服务器拒绝了连接","WAN口没有插网线","服务器无响应",
			"服务器断开连接","选项不支持","认证失败（账号或密码错误）","认证方式不被支持","WAN口IP地址与获取到的IP地址冲突"};

	/**
	 *
	 * status 0 code 8 表示认证失败
	 * status 1 code 0 认真成功
	 * */
	@Override
	public void trackLink() {

		int pAccessCount = 0;

		while(pAccessCount ++ < 20){

			try {
				Thread.sleep(1500);
			} catch (InterruptedException e) {
				Log.logE(e);
			}

			this.loadStatus();
			if(!this.mStatus.isEmpty()){
				int iStateCode = StatusController.parseInt(this.mStatus.get("status"), -1);
				int iErrCode = StatusController.parseInt(this.mStatus.get("code"), -1);

				if(iStateCode != -1 && iErrCode != -1){

					sendMessage(LINK_STATUS[iStateCode]+"("+LINK_CODE[iErrCode]+")(尝试次数："+pAccessCount+"/20)");
					if(iStateCode == LINK_UP || iStateCode == LINK_DOWN) break;
					else continue;
				}
			}
			sendMessage("没有获取到路由器状态信息。");
		}

	}


	/**
	 * 路由器状态信息存放表
	 * */
	private Hashtable<String,String> mStatus = new Hashtable<String,String>();

	/**
	 * 即时查询路由器状态信息，并存入HashTable
	 * */
	public void loadStatus(){

		if(!this.isAuthed()){
			this.loadDynamicFile();
			this.authRouter();
		}

		String pReqAddr = this.getSvrURL() + "?code=2&asyn=1&id="+this.mDynamicId;

		String pResData = this.requestData(pReqAddr, "23");

		if(pResData!=null){
			/**
			 * 00000
			 id 23
			 ip 0.0.0.0
			 mask 0.0.0.0
			 gateway 0.0.0.0
			 dns 0 0.0.0.0
			 dns 1 0.0.0.0
			 status 0
			 code 8
			 upTime 0
			 inPkts 0
			 inOctets 0
			 outPkts 0
			 outOctets 0
			 * */
			String[] pRes = parseResult(pResData);

			this.mStatus.clear();

			for(String x: pRes){
				String[] pContent = x.split(" ");
				if(pContent.length == 3){
					mStatus.put(pContent[0] + pContent[1],pContent[2]);
				}else{
					mStatus.put(pContent[0], pContent.length >= 2 ? pContent[1] : "");
				}
			}

		}

	}

	/**获取连接数据
	 * 00000
	 id 23
	 ip 0.0.0.0
	 mask 0.0.0.0
	 gateway 0.0.0.0
	 dns 0 0.0.0.0
	 dns 1 0.0.0.0
	 status 0
	 code 8
	 upTime 0
	 inPkts 0
	 inOctets 0
	 outPkts 0
	 outOctets 0
	 * */
	public Hashtable<String,String> getState(){
		this.loadStatus();
		if(this.mStatus != null && !this.mStatus.isEmpty())
			return (Hashtable<String,String>)mStatus.clone();
		return null;
	}

	@Override
	public void setWifiState(WiFiInfo pW) {


	}

	@Override
	public WiFiInfo getWifiState() {

		return null;
	}

	@Override
	protected int setDialProperty(HttpURLConnection mRouterUrlCon) {
		if(mRouterUrlCon != null){
			mRouterUrlCon.setRequestProperty("Content-Type", "text/plain;charset=UTF-8");
			mRouterUrlCon.setRequestProperty("Charset","UTF-8");
			mRouterUrlCon.setRequestProperty("Referer", this.getSvrURL());
			mRouterUrlCon.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2272.89 Safari/537.36");
			mRouterUrlCon.setRequestProperty("Origin", this.getSvrURL());
			mRouterUrlCon.setRequestProperty("Host", this.gRouterIP);
			mRouterUrlCon.setRequestProperty("Connection", "keep-alive");
			mRouterUrlCon.setRequestProperty("Accept-Language", "zh-CN,zh;q=0.8");
			return 0;
		}
		return -1;
	}


	/**
	 * 计算加密ID<br>
	 * 原始JS：<br>
	 * var d = "", e, f, h, m, k = 187, l = 187;<br>
	 f = a.length;<br>
	 h = b.length;<br>
	 m = c.length;<br>
	 e = f > h ? f : h;<br>
	 for (var g = 0; g < e; g++)<br>
	 l = k = 187, g >= f ? l = b.charCodeAt(g) : <br>
	 g >= h ? k = a.charCodeAt(g) : (k = a.charCodeAt(g), l = b.charCodeAt(g)), d += c.charAt((k ^ l) % m);<br>
	 return d<br>
	 * <br>
	 *  安全编码（a,b,c）

	 a为原始密码
	 b为短验证码
	 c为长验证码

	 d初始化为空字符串，K和L默认187
	 f为原始密码长度
	 h为短验证码长度
	 m为长验证码长度
	 e为 f 和 h 中最大值
	 循环 e 的次数，临时变量g
	 l = k = 187，
	 如果 g 大等于 原始密码长度 l =
	 短验证码的第g位的字符
	 否则 如果 g > h
	 k = 原始密码第g位字符
	 否则 k = 段密码g位，l = 短密文的g位
	 d += 长验证码的第 k ^ l % m 字符
	 返回密码
	 * @param pOrigin 原始密码
	 * @param pShort 短验证码
	 * @param pLong 长验证码
	 * */
	public static String calcDynamicId(String pOrigin,String pShort,String pLong) throws Exception{
		if(pOrigin!=null && pShort!=null && pLong!=null){
			StringBuffer pId = new StringBuffer();

			char k = 187 , l = 187;

			// f 短验证码长度    h  原密码长度   m  长验证码长度   e 为 h 和 f 的最大值
			int f = pShort.length(), h =  pOrigin.length() , m = pLong.length() , e = f > h ? f : h;
			for(int g = 0 ; g < e ; g++){
				l = k = 187;
				if(g >= f){
					l = pOrigin.charAt(g);
				}else if(g >= h){
					k = pShort.charAt(g);
				}else{
					k = pShort.charAt(g);
					l = pOrigin.charAt(g);
				}
				pId.append(pLong.charAt((k ^ l) % m));
			}

			return URLEncoder.encode(pId.toString(),"UTF-8");
		}
		return null;
	}

	/**用于第一次设置路由器时修改默认密码*/
	public static String AUTH_KEY_CHANGE_DEFAULT_PASSWORD = "WaQ7xbhc9TefbwK"; // admin

	/**用于身份验证的短验证码，固定字符串*/
	public static String AUTH_KEY_SHORT = "RDpbLfCPsJZ7fiv";
	/**用于身份验证的长验证码，固定字符串*/
	public static String AUTH_KEY_LONG = "yLwVl0zKqws7LgKPRQ84Mdt708T1qQ3Ha7xv3H7NyU84p21BriUWBU43odz3iP4rBL3cD02KZciXTysVXiV8ngg6vL48rPJyAUw0HurW20xqxv9aYb4M9wK1Ae0wlro510qXeU07kV57fQMc8L6aLgMLwygtc0F10a0Dg70TOoouyFhdysuRMO51yY5ZlOZZLEal1h0t9YQW0Ko7oBwmCAHoic4HYbUyVeU3sfQ1xtXcPcf1aT303wAQhv66qzW";

	/**
	 * 加载身份验证的固定字符串
	 * */
	private void loadAuthKey(){

	}

	/**更改默认管理员密码*/
	public void changeDefaultPassword(String newpass){

	}

	/**
	 * 加载动态密码文件并计算动态密码，如果成功返回true
	 * */
	public boolean loadDynamicFile(){
		//使用固定密钥加密一次

		try {
			String mFirst = this.calcDynamicId(this.gRouterAccPassword, AUTH_KEY_SHORT, AUTH_KEY_LONG);

			HttpURLConnection pHuc = this.getConnection(this.getSvrURL()+"common/Content.htm");
			if(this.setDialProperty(pHuc) == 0){

				String pData = this.getHTMLContent(pHuc.getInputStream());
				String []pKey = pData.split(pData.contains("\r\n") ? "\r\n" : "\r");

				if(pKey.length < 5){
                    Log.log("处理数据 ： 从Content.htm抓取到的数据结果为 ： " + pData.replace((pData.contains("\r\n")? "\r\n" : "\r"),"<br/>"));
                    return false;//不符合返回要求
                }

				//debug
				Log.log("短密钥："+pKey[3]);
				Log.log("长密钥："+pKey[4]);

				//动态ID
				this.mDynamicId = this.calcDynamicId(mFirst , pKey[3], pKey[4]);

				//debug
				System.out.println("动态ID："+this.mDynamicId);

				return this.mDynamicId != null;
			}

		}catch (Exception e) {
			Log.logE(e);
		}
		return false;
	}

	/**
	 * 返回验证状态
	 * */
	public boolean isAuthed(){
		return (this.mDynamicId != null || (this.loadDynamicFile() && this.authRouter() && this.mDynamicId!=null));
	}

	/**
	 * 登录路由器验证
	 * */
	public boolean authRouter(){
		if(this.mDynamicId != null){

			String pResponse = this.requestData(this.getSvrURL()+"?code="+TDDP_AUTH+"&asyn=0&id="+this.mDynamicId, "");

			//如果不出现401问题此处应该已经验证成功
			if(pResponse != null && pResponse.indexOf("00000") == 0){
				//this.mLoginFailed = 0;
				return true;
			}

		}
		return false;
	}

	/**
	 * 执行AJAX模拟请求数据
	 * @param pAdd 请求完整地址
	 * @param pParam 参数内容
	 * @return String
	 * */
	public String requestData(String pAdd,String pParam){
		if(pAdd != null && pParam!=null && !pAdd.equals("")){
			if(this.mDynamicId != null || (this.mDynamicId == null && this.loadDynamicFile() && this.authRouter())){

				HttpURLConnection pHuc = null;

				try{

					pHuc = this.getConnection(pAdd);

					if(this.setDialProperty(pHuc) == 0){

						pHuc.setDoOutput(true);
						pHuc.setRequestMethod("POST");
						pHuc.setRequestProperty("Content-Length", String.valueOf(pParam.length()));
						pHuc.setRequestProperty("DNT", String.valueOf(1));

						pHuc.getOutputStream().write(pParam.getBytes());

						return this.getHTMLContent(pHuc.getInputStream());
					}

				}catch(Exception e){
					if(e.getMessage() != null && e.getMessage().contains("401")){
						if(this.loadDynamicFile() && this.authRouter()){
							this.requestData(pAdd,pParam);
						}
					}else{
						if(e instanceof IOException){
							handleIOException((IOException)e);
						}
						if(pHuc != null){
							String pData = this.getHTMLContent(pHuc.getErrorStream());
							if(pData != null) return pData;
						}
					}

				}

			}
		}
		return null;
	}

	/**
	 * parse data
	 * */
	public static String[] parseResult(String pCon){
		if(pCon!=null && !pCon.equals("")){
			return pCon.split("\r\n");
		}
		return null;
	}
}
