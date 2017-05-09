package cn.sunflyer.simpnk.obj;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.util.Date;

import cn.sunflyer.simpnk.control.AccountController;
import cn.sunflyer.simpnk.control.Base64;
import cn.sunflyer.simpnk.control.Log;
import cn.sunflyer.simpnk.control.StatusController;
import cn.sunflyer.simpnk.netkeeper.CXKUsername;


public class RouterMecuryTPF extends Router{

	public RouterMecuryTPF() {
		super(StatusController.sRouterAcc, StatusController.sRouterPassword, StatusController.sRouterAdd, StatusController.sAccName, StatusController.sAccPassword,StatusController.getStateRouterAuthMethod());
	}
	
	public int connect() {
		if(this.getConnectionState()==CONNECTION_SUCCESS){
			return RES_SUCCESS;
		}
		if (this.mAuthMethod != Router.AUTH_NOT_AVALIABLE && this.mIsInit) {

			String encodeName = null;
			String encodePassword = null;
			if(StatusController.getStateEncrypt()){
				try {
					Log.log("开始计算加密用户名数据");
					// 替换出现的+为空格，否则用户名错误。
					encodeName = URLEncoder.encode(AccountController.getRealAccount(this.gAccName), "UTF-8").replace("+", "%20");
							//.replace("+", "%2D");
					encodePassword = URLEncoder.encode(this.gAccPassword, "UTF-8");
				} catch (Exception ex) {
					Log.logE(ex);
					return Router.RES_UNABLE_ENCODE;
				}
			}else{
				Log.log("检测到连接模式为《家用模式》");
				try {
					encodeName=URLEncoder.encode(this.gAccName,"UTF-8");
					encodePassword=URLEncoder.encode(this.gAccPassword,"UTF-8");
				} catch (UnsupportedEncodingException e) {
					Log.logE(e);
				}
				
			}
			
			// 目标地址，这是设置路由器登陆最必须的条件。
			
			String URL = null;
			/**
			switch(StatusController.getStateDialMode()){
			case StatusController.DIAL_AUTO:
				URL="http://"
						+ this.gRouterIP
						+ "/userRpm/PPPoECfgRpm.htm?wan=0&wantype=2&acc="
						+ encodeName
						+ "&psw="
						+ encodePassword
						+ "&confirm="
						+ encodePassword
						+ "sta_ip=0.0.0.0&sta_mask=0.0.0.0&linktype=2&Connect=%C1%AC+%BD%D3";
				;break;
			case StatusController.DIAL_USER:*/
				URL="http://"
						+ this.gRouterIP
						+ "/userRpm/PPPoECfgRpm.htm?wan=0&wantype=2&acc="
						+ encodeName
						+ "&psw="
						+ encodePassword
						+ "&confirm="
						+ encodePassword
						+ "&sta_ip=0.0.0.0&sta_mask=0.0.0.0&linktype=4&waittime2=0&Connect=%C1%AC+%BD%D3"; //&specialDial=0
				/**;break;
			default:return Router.RES_NO_DIAL_MODE;
			}*/
			//linktype=2 : 自动连接
			
			Log.log("检查到的连接类型为："+StatusController.getStateDialMode());
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
				handleIOException(e);
				return Router.RES_IO_EXCEPTION;
			}
		}
		Log.log(this.mIsInit ? "无法通过现有方式访问路由器，可能是因为账户错误，或者不支持。"
				: "检测到用户数据未被初始化，操作已经停止");
		return this.mIsInit ? Router.RES_UNABLE_ACCESS : Router.RES_META_DATA_NOT_INIT;
	}


	private String[] mPPPoEInf;
	
	public void LoadPPPoEInf(){
		try {
			HttpURLConnection pCon=this.getConnection("http://"+this.gRouterIP+"/userRpm/PPPoECfgRpm.htm");
			if(this.setDialProperty(pCon)!=0) return;
			pCon.connect();
			String HTML=this.getHTMLContent(pCon.getInputStream());
			String KeyWord2="var pppoeInf=new Array(";
			int pIndex=HTML.indexOf(KeyWord2);
			if(pIndex>=0){
				int eIndex=HTML.indexOf(");", pIndex);
				if(eIndex>pIndex){
					this.mPPPoEInf=HTML.substring(pIndex+KeyWord2.length(),eIndex).split(",\n");
					this.TrimPPPoEInf();
					Log.log("已经检测到数组内容。长度为"+this.mPPPoEInf.length);
				}
					
			}else{
				String KeyWord3="var pppoeInf = new Array(";
				pIndex=HTML.indexOf(KeyWord3);
				if(pIndex>=0){
					int eIndex=HTML.indexOf(");", pIndex);
					if(eIndex>pIndex){
						this.mPPPoEInf=HTML.substring(pIndex+KeyWord3.length(),eIndex).split(",\n");
						this.TrimPPPoEInf();
						Log.log("已经检测到数组内容。长度为"+this.mPPPoEInf.length);
					}
				}
			}
		} catch (IOException e) {
			Log.logE(e);
		}
	}
	
	public void TrimPPPoEInf(){
		for(int i=0;i<this.mPPPoEInf.length;i++){
			this.mPPPoEInf[i]=this.mPPPoEInf[i].trim();
		}
	}
	
	public String[] getPPPoEInf(){
		return this.mPPPoEInf;
	}
	
	/**
	 * 返回当前网络连接状态,标识符开头：CONNECTION_
	 * */
	public int getConnectionState(){
		this.LoadPPPoEInf();
		if(this.getPPPoEInf()==null) return CONNECTION_OPERATION_EXCEPTION;
		try{
			return this.mPPPoEInf[26].trim().length() == 1 ? Integer.parseInt(this.mPPPoEInf[26]) : Integer.parseInt(this.mPPPoEInf[27]);
		}catch(Exception e){
			return CONNECTION_UNKNOWN;
		}
	}
	
	
	protected void testLink() {
		if (this.mIsInit) {
			Log.log("正在尝试以新版本固件的方式处理操作数据");
			HttpURLConnection xHuc = null;
			try {
					xHuc = this.getConnection("http://"+this.gRouterIP);
					if (xHuc != null) {
						xHuc.setRequestProperty(
								"Cookie",
								"Authorization="+ this.getBase64Acc());
						Log.log("开始尝试第一次连接");
						xHuc.connect();
						String HTML=this.getHTMLContent(xHuc.getInputStream());
						Log.log("检查是否可用");
						if (HTML.indexOf("noframe") > 0 || HTML.indexOf("utf8_encode") < 0) {
							this.changeAuthMethod(AUTH_WEB);
						} else {
							// 尝试更新版本
							Log.log("已正在尝试最新版本（COOKIE-NOADMIN）");
							this.detectLatest();
						}
					}
			} catch (IOException e) {
				this.changeInitState(false);
				Log.logE(e);
				this.detectOld();
			}
		} else {
			this.changeAuthMethod(Router.AUTH_NOT_AVALIABLE);
		}
	}

	/**
	 * 部分固件更新后只Base64加密密码验证，因此加入此选项并修改了检测规则
	 * */
	private void detectLatest(){
		try{
			HttpURLConnection pHuc = this.getConnection("http://"+this.gRouterIP);
			pHuc.setRequestProperty("Cookie", "Authorization=Basic "+ Base64.encode(this.gRouterAccPassword));
			pHuc.connect();
			String pHtml = this.getHTMLContent(pHuc.getInputStream());
			Log.log("检查状态");
			if(pHtml.indexOf("utf8_encode")<0){
				this.changeAuthMethod(Router.AUTH_PASSWORD_ONLY);
			}else{
				this.changeAuthMethod(Router.AUTH_NOT_AVALIABLE);
			}
		}catch(IOException e){
			Log.logE(e);
			this.changeAuthMethod(Router.AUTH_NOT_AVALIABLE);
		}
	}

	protected void detectOld() {
		try {
			Log.log("尝试以旧版本的方式检测可用性");
			HttpURLConnection pHuc = this.getConnection("http://"+this.gRouterIP);
			pHuc.setRequestProperty(
					"Authorization",
					this.getBase64Acc());		
			pHuc.connect();
			String html=this.getHTMLContent(pHuc.getInputStream());
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
	
	public void trackLink(){
		boolean getData=true;
		int count = 0;
		while(getData){
			if(++count==20) {
				getData=false;
				this.setState("已尝试向路由器发送数据，但在超过时限后没有收到必须的信息，操作已经终止");
				break;
			};
			try {
				Thread.sleep(1500);
			} catch (InterruptedException e) {
				Log.logE(e);
			}
			switch(this.getConnectionState()){
			case CONNECTION_AUTHENTICATION_FAILED:this.setState("验证失败，用户名或密码错误");getData=false;break;
			case CONNECTION_CONNECTING:this.setState("正在连接...(尝试跟踪次数："+count+"/20)");break;
			case CONNECTION_NO_RESPONSE:this.setState("服务器没有响应(请尝试检查你的路由器连线，或重启路由器)");getData=false;break;
			case CONNECTION_NOT_CONNECTED:this.setState("网络目前没有连接");getData=false;break;
			case CONNECTION_NOT_CONNECTED_WAN:this.setState("WAN口未连接");getData=false;break;
			case CONNECTION_OPERATION_EXCEPTION:this.setState("操作出现异常（软件无法判断，请自行查看网络是否连接！）");getData=false;break;
			case CONNECTION_OPERATION_NO_MODE:this.setState("没有可用方式连接到路由器");getData=false;break;
			case CONNECTION_SUCCESS:this.setState("连接成功");getData=false;break;
			case CONNECTION_UNKNOWN:this.setState("发生未知错误");getData=false;break;
			default:break;
			}
		}
	}
	
	
	/**
	 * WLAN基本信息，其中<br>
	 * 0-无线状态（启用与否，1-启用）<br>
	 * 1-WIFI的SSID<br>
	 * 2-无线信道<br>
	 * 3-无线模式<br>
	 * 4-无线的MAC<br>
	 * 5-当前主机IP<br>
	 * 6-无线频段带宽<br>
	 * 7-<br>
	 * 8-<br>
	 * 9-<br>
	 * 10-WDDS状态
	 * */
	private String[] gLinkInfoIndexWlan;
	
	/**
	 * WAN口信息，其中
	 * 1-WAN的MAC地址
	 * 2-WAN口IP地址
	 * 3-
	 * 4-子网掩码
	 * 5-
	 * 6-
	 * 7-网关IP
	 * 11-DNS
	 * 12-已连接时间
	 * 13-连接状态（0-未连接 1-已连接 2-正在连接 3-用户名/密码错误 4-无响应 5-未知原因 6-WAN未连接 ）
	 * */
	private String[] gLinkInfoIndexWan;
	
	/**
	 * 加载当前状态信息，来自路由器状态页面
	 * */
	public void LoadLinkInfo(){
		try{
			HttpURLConnection tHuc=this.getConnection("http://"+this.gRouterIP+"/userRpm/StatusRpm.htm");
			if(this.setDialProperty(tHuc)!=0) Log.log("加载 首页全部信息 出现错误，无法设置属性");
			else{
				tHuc.connect();
				String HTML=this.getHTMLContent(tHuc.getInputStream());
				if(HTML == null)
					return;
				HTML = HTML.replace(" ","");
				String Keyword="varwlanPara=newArray(";
				int tIndex=HTML.indexOf(Keyword);
				if(tIndex>0){
					int tEnd=HTML.indexOf(");", tIndex);
					if(tEnd>tIndex){
						String tArray=HTML.substring(tIndex+Keyword.length(),tEnd);
						this.gLinkInfoIndexWlan=tArray.split(",\n");
						Router.trimString(gLinkInfoIndexWlan);
					}
				}
				String KeywordWAN="varwanPara=newArray(";
				tIndex=HTML.indexOf(KeywordWAN);
				if(tIndex>0){
					int tEnd=HTML.indexOf(");",tIndex);
					if(tEnd>tIndex){
						String tArray=HTML.substring(tIndex, tEnd);
						this.gLinkInfoIndexWan=tArray.split(",\n");
						Router.trimString(gLinkInfoIndexWan);
					}
				}
			}
		}catch(IOException e){
			Log.logE(e);
		}
	}
	
	
	/**
	 * WLAN基本信息，其中<br>
	 * 0-无线状态（启用与否，1-启用）<br>
	 * 1-WIFI的SSID<br>
	 * 2-无线信道<br>
	 * 3-无线模式<br>
	 * 4-无线的MAC<br>
	 * 5-当前主机IP<br>
	 * 6-无线频段带宽<br>
	 * 7-<br>
	 * 8-<br>
	 * 9-<br>
	 * 10-WDDS状态
	 * */
	public String[] getLinkInfoWlan(){
		return this.gLinkInfoIndexWlan;
	}
	
	/**
	 * WAN口信息，其中
	 * 1-WAN的MAC地址
	 * 2-WAN口IP地址
	 * 3-
	 * 4-子网掩码
	 * 5-
	 * 6-
	 * 7-网关IP
	 * 11-DNS
	 * 12-已连接时间
	 * 13-连接状态（0-未连接 1-已连接 2-正在连接 3-用户名/密码错误 4-无响应 5-未知原因 6-WAN未连接 ）
	 * */
	public String[] getLinkInfoWan(){
		return this.gLinkInfoIndexWan;
	}
	
	private String[] gWlanInfoKey;
	
	
	/**
	 * 加载无线路由器信息，来自无线安全页面
	 * */
	public void LoadWlanInfoKey(){
		try {
			HttpURLConnection tHuc=this.getConnection("http://"+this.gRouterIP+"/userRpm/WlanSecurityRpm.htm");
			if(this.setDialProperty(tHuc)!=0) Log.log("加载 WLAN 信息出现错误，无法设置验证属性");
			else{
				tHuc.connect();
				String HTML=this.getHTMLContent(tHuc.getInputStream());
				String Keyword="var wlanPara=new Array(";
				int tIndex=HTML.indexOf(Keyword);
				if(tIndex>0){
					int tEnd=HTML.indexOf(");", tIndex);
					if(tEnd>tIndex){
						String tArray=HTML.substring(tIndex+Keyword.length(),tEnd);
						this.gWlanInfoKey=tArray.split(",\n");
						Router.trimString(gWlanInfoKey);
					}
				}
			}
		} catch (IOException e) {
			Log.logE(e);
		}
	}
	
	public String[] getWlanInfoKey(){
		return this.gWlanInfoKey;
	}
	
	public String getWifiPassword(){
		if(this.gWlanInfoKey!=null && this.gWlanInfoKey.length>12){
			return this.gWlanInfoKey[9];
		}else{
			this.LoadWlanInfoKey();
			return (this.gWlanInfoKey!=null && this.gWlanInfoKey.length>12)?this.gWlanInfoKey[9]:null;
		}
	}
	
	
	/** 
	 * 获取当前WIFI信息，包括WIFI热点名称，热点密码
	 * */
	public WiFiInfo getWifiState(){
		WiFiInfo tWi=null;
		this.LoadLinkInfo();
		this.LoadWlanInfoKey();
		
		String SSID=null;
		String Password=null;
		
		SSID=this.gLinkInfoIndexWlan[1];
		Password=this.getWifiPassword();
		tWi=new WiFiInfo(SSID,Password);
		
		return tWi;
	}
	
	private static final String PAGE_CONFIG_WLAN_SEC="http://%IP%/userRpm/WlanSecurityRpm.htm?secType=3&pskSecOpt=2&pskCipher=3&pskSecret=%KEY%&interval=1800&Save=%B1%A3+%B4%E6";
	private static final String PAGE_CONFIG_WLAN_NETWORK="http://%IP%/userRpm/WlanNetworkRpm.htm?ssid1=%SSID%&wlMode=2&channel=0&mode=5&chanWidth=2&ap=1&brlssid=&brlbssid=&detctwds=1&keytype=1&wepindex=1&keytext=%HIDESSID%&Save=%B1%A3+%B4%E6";
	private static final String PAGE_VAR_IP="%IP%";
	private static final String PAGE_VAR_SSID="%SSID%";
	private static final String PAGE_VAR_KEY="%KEY%";
	private static final String PAGE_VAR_HIDESSID="%HIDESSID%";
	private static final String PAGE_VAR_HIDESSID_DATA="&broadcast=2";
	
	
	public void setWifiState(WiFiInfo pW){
		//密码配置地址：http://172.16.17.1/userRpm/WlanSecurityRpm.htm?
		//secType=3&pskSecOpt=2&pskCipher=3&pskSecret=<password>&interval=1800
		//&Save=%B1%A3+%B4%E6
		String tConfigPassword=RouterMecuryTPF.PAGE_CONFIG_WLAN_SEC.replace(PAGE_VAR_IP, this.gRouterIP).replace(PAGE_VAR_KEY, pW.getWifiPassword());
		try {
			HttpURLConnection tConfigPwdHuc=this.getConnection(tConfigPassword);
			if(this.setDialProperty(tConfigPwdHuc)!=0) return;
			else{
				tConfigPwdHuc.connect();
				boolean tRes=this.getResponseData(tConfigPwdHuc.getInputStream(), pW.getWifiName());
				this.setState(tRes?"修改热点成功":"修改热点失败");
			}
		} catch ( IOException e) {
			Log.logE(e);
		}		
		
		try {
			//JOptionPane.showMessageDialog(null,"已尝试修改你的无线路由器密码，由于安全原因，现在可能会被断开连接\n\n请现在连接你的无线路由器，45秒钟后系统会自动继续修改SSID操作");
			this.wait(45000);
		} catch (InterruptedException e1) {
			Log.logE(e1);
		}
		//SSID：http://172.16.17.1/userRpm/WlanNetworkRpm.htm?
		//ssid1=<ssid>&wlMode=2&channel=0&mode=5&chanWidth=2&ap=1&brlssid=
		//&brlbssid=&detctwds=1&keytype=1&we
		//pindex=1&keytext=&broadcast=2&Save=%B1%A3+%B4%E6
		String tConfigSSID=RouterMecuryTPF.PAGE_CONFIG_WLAN_NETWORK.replace(PAGE_VAR_IP, this.gRouterIP).replace(PAGE_VAR_SSID, pW.getWifiName()).replace(PAGE_VAR_HIDESSID, pW.isWifiBroadCast()?"":PAGE_VAR_HIDESSID_DATA);
		try {
			HttpURLConnection tConfigSSIDHuc=this.getConnection(tConfigSSID);
			if(this.setDialProperty(tConfigSSIDHuc)!=0) return;
			else{
				tConfigSSIDHuc.connect();
				boolean tRes=this.getResponseData(tConfigSSIDHuc.getInputStream(), pW.getWifiName());
				this.setState(tRes?"成功修改热点名":"修改热点名称失败（也有可能是检测错误，请自行检查连接！）");
				Log.log("修改热点名称的最终参考结果为："+tRes);
			}
		} catch (MalformedURLException e) {
			Log.logE(e);
		} catch (IOException e) {
			Log.logE(e);
		}
	}

	@Override
	protected int setDialProperty(HttpURLConnection mRouterUrlCon) {
		switch(this.mAuthMethod){
		case Router.AUTH_OLD:mRouterUrlCon.setRequestProperty(
				"Authorization",this.getBase64Acc());
				Log.log("验证方式：401");
		return 0;
		case Router.AUTH_WEB:mRouterUrlCon.setRequestProperty(
				"Cookie",
				"Authorization="+ this.getBase64Acc());
		Log.log("验证方式：402");
		return 0;
		case Router.AUTH_PASSWORD_ONLY:mRouterUrlCon.setRequestProperty("Cookie", "Authorization=Basic "+Base64.encode(this.gRouterAccPassword));
		Log.log("验证方式：403");
		return 0;
		default:return Router.RES_NO_DIAL_MODE;
		}
		
	}
	
	public void setInternalNet(){
		try {
			HttpURLConnection pCon=this.getConnection("http://"+this.gRouterIP+"/userRpm/WanDynamicIpCfgRpm.htm?wantype=0&mtu=1500&downBandwidth=0&upBandwidth=0&Save=%B1%A3+%B4%E6");
			this.setDialProperty(pCon);
			pCon.connect();
			String tHTML=this.getHTMLContent(pCon.getInputStream());
			Log.log(tHTML);
			this.setState(tHTML.indexOf("dhcp")>=0?"已向路由器发送内网模式操作数据。":"操作失败");
		} catch (IOException e) {
			this.setState("处理操作时出现错误");
			Log.logE(e);
		}
		
	}

	/**定时拨号功能*/
	public void setOntimeDial(int startHour,int startMin,int endHour,int endMin){

		/**
		 * http://172.16.17.1/userRpm/PPPoECfgRpm.htm?wan=0&wantype=2&
		 * acc=%3Cbr%3EEZBAK+&psw=Hello123World&confirm=Hello123World&specialDial=0
		 * &SecType=0&sta_ip=0.0.0.0&sta_mask=0.0.0.0&linktype=3&hour1=06&minute1=30&hour2=23&minute2=30&Save=%B1%A3+%B4%E6
		 * */


/**
 http://172.16.17.1/userRpm/DateTimeCfgRpm.htm?timezone=1200&year=2015&month=4&day=19&hour=12&minute=42&second=55&ntpA=0.0.0.0&ntpB=0.0.0.0&Save=%B1%A3+%B4%E6&isTimeChanged=1
 *
 */
				//时间处
 		Date pDate = new Date();

		if(startHour < 0 || startHour > 23 || endHour < 0 || endHour > 23 || startMin < 0 || startMin > 59 || endMin < 0 || endMin > 59 || (startHour == endHour && startMin == endMin)){
			setState("时间区间错误，请检查你的输入");
			return;
		}

		//一般是凌晨断网
		if(startHour > endHour){
			if((pDate.getHours() > endHour && pDate.getHours() < startHour) || (pDate.getHours() == startHour && pDate.getMinutes() < startMin) || (pDate.getHours() == endHour && pDate.getMinutes() > endMin)){
				pDate.setHours(startHour);
				pDate.setMinutes(startMin);
			}else{
				//凌晨断网不改变日期，应该是当天，因此除开上述情况都应该立即拨号
				//设置立即拨号
				this.setState("当前时间处于设置时间间隔中，将执行立即拨号");
				this.connect();
				return;
			}
		}else{ //否则为12点前断网
			//起始时间前
			if(pDate.getHours() < startHour || (pDate.getHours() == startHour && pDate.getMinutes() < startMin)){
				pDate.setHours(startHour);
				pDate.setMinutes(startMin);
			}else if(pDate.getHours() > endHour || (pDate.getHours() == endHour && pDate.getMinutes() > endMin)){
				//设置第二天
				pDate.setHours(startHour);
				pDate.setMinutes(startMin);
				pDate.setDate(pDate.getDate() + 1);
			}else{
				//设置立即拨号
				this.setState("当前时间处于设置时间间隔中，将执行立即拨号");
				this.connect();
				return;
			}
		}

 		String pszRealname = AccountController.getRealAccount(this.gAccName,pDate.getTime());

		try{
			Date pNow = new Date();
			//先设置路由器时间
			String pszSetTime = this.getSvrURL() + "userRpm/DateTimeCfgRpm.htm?timezone=1200&year=" +
					(pNow.getYear() + 1900) +
					"&month=" + (pNow.getMonth() + 1)
					+"&day="+pNow.getDate() +
					"&hour="+pNow.getHours() +
					"&minute="+pNow.getMinutes() +
					"&second="+pNow.getSeconds() +
					"&ntpA=0.0.0.0&ntpB=0.0.0.0&Save=%B1%A3+%B4%E6&isTimeChanged=1";

			HttpURLConnection pConTime = this.getConnection(pszSetTime);
			this.setDialProperty(pConTime);
			pConTime.getInputStream();

			//开始设置
			String pszUrl = this.getSvrURL() + "userRpm/PPPoECfgRpm.htm?wan=0&wantype=2&acc=" +
					URLEncoder.encode(pszRealname,"UTF-8").replace("+", "%20") +
					"&psw=" +
					URLEncoder.encode(this.gAccPassword,"UTF-8").replace("+","%20") +
					"&confirm=" +
					URLEncoder.encode(this.gAccPassword,"UTF-8").replace("+","%20") +
					"&specialDial=0&SecType=0&sta_ip=0.0.0.0&sta_mask=0.0.0.0&linktype=3&hour1=" +
					startHour +
					"&minute1=" +
					startMin +
					"&hour2=" +
					endHour +
					"&minute2=" +
					endMin +
					"&Save=%B1%A3+%B4%E6";

			HttpURLConnection pCon = this.getConnection(pszUrl);
			if(this.setDialProperty(pCon)!=0) return;
			pCon.connect();
			String pszHtml = this.getHTMLContent(pCon.getInputStream());

			if(pszHtml.indexOf(pszRealname.replace("\r\n","<br>")) >= 0 ){
				this.setState("设置成功。下一次自动拨号时间已被设置");
			}else{
				this.setState("设置失败。");
			}
		}catch(Exception e){
			this.setState("尝试设置定时拨号出现了一些错误");
			Log.logE(e);
		}
	}

}
