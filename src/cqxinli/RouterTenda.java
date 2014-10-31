package cqxinli;

import java.io.IOException;
import java.net.HttpURLConnection;

public class RouterTenda extends RouterSet {

	private CXKUsername gUn;
	
	protected RouterTenda(String RouterID, String RouterKey, String IP,
			String AccName, String AccPassword) {
		this(RouterID, RouterKey, IP, AccName, AccPassword,AUTH_NOT_AVALIABLE);
		
	}
	
	protected RouterTenda(String RouterID, String RouterKey, String IP,
			String AccName, String AccPassword, int AuthMethod) {
		super(RouterID, RouterKey, IP, AccName, AccPassword, AuthMethod);
		this.gUn=new CXKUsername(AccName);
		testLink();
	}

	@Override
	protected void testLink() {
		try {
			HttpURLConnection pCon=this.getConnection("http://"+this.gRouterIP+"/LoginCheck");
			if(this.setDialProperty(pCon)!=0){
				this.changeAuthMethod(AUTH_NOT_AVALIABLE);
			}else{
				pCon.setRequestProperty("Referer", "http://"+this.gRouterIP+"/login.asp");
				this.writePostParam(pCon, "Username="+this.gRouterAccName+"&checkEn=0&Password="+this.gRouterAccPassword);
				String pHtml=this.getHTMLContent(pCon.getInputStream());
				if(pHtml.indexOf("frame")>=0){
					this.changeAuthMethod(Router.AUTH_WEB);
				}
				
			}
		}  catch (IOException e) {
			Log.logE(e);
			this.changeAuthMethod(Router.AUTH_NOT_AVALIABLE);
		}

	}

	@Override
	protected void detectOld() {
		

	}

	@Override
	public int connect() {
		if(this.gUn==null) this.gUn=new CXKUsername(this.gAccName);
		if(this.mIsInit && this.mAuthMethod!=Router.AUTH_NOT_AVALIABLE){
			try {
				HttpURLConnection pCon=this.getConnection("http://"+this.gRouterIP+"/goform/AdvSetWan");	
				if(this.setDialProperty(pCon)!=0) return RES_AUTHENTICATION_NO_METHED;
				pCon.setRequestProperty("Referer", "http://"+this.gRouterIP+"/system_status.asp");
				StringBuilder pSb=new StringBuilder();
				pSb.append("GO=wan_connectd.asp&rebootTag=&v12_time="+(System.currentTimeMillis()/1000));
				pSb.append("&WANT2=3&WANT1=2&dynamicMTU=1480&WANIP=0.0.0.0&WANMSK=0.0.0.0&WANGW=0.0.0.0&DS1=0.0.0.0&DS2=0.0.0.0&staticMTU=1480&PUN=");
				pSb.append(this.gUn.Realusername());
				pSb.append("&PPW="+this.gRouterAccPassword);
				pSb.append("&MTU=1492&SVC=&AC=&PCM=0&PIDL=60&hour1=0&minute1=0&hour2=0&minute2=0&l2tpIP=&l2tpPUN=&l2tpPPW=&l2tpMTU=1452&l2tpAdrMode=1&l2tpWANIP=0.0.0.0&l2tpWANMSK=0.0.0.0&l2tpWANGW=0.0.0.0");				
				this.writePostParam(pCon, pSb.toString());
				pCon.connect();
				
				//启动链接
				HttpURLConnection pConNow=this.getConnection("http://"+this.gRouterIP+"/goform/SysStatusHandle");
				this.setDialProperty(pConNow);
				pConNow.setRequestProperty("Referer", "http://"+this.gRouterIP+"/system_status.asp");
				this.writePostParam(pConNow, "CMD=WAN_CON&GO=system_status.asp&action=3");
				pConNow.connect();
				String pHtml=this.getHTMLContent(pConNow.getInputStream());
				return (pHtml.indexOf("frame")>0) ? RES_SUCCESS:RES_UNABLE_ACCESS;
			} catch (IOException e) {
				Log.logE(e);
				return RES_IO_EXCEPTION;
			}
			
		}
		return RES_NO_DIAL_MODE;
	}

	private String[] gPPPoEInf;
	
	private void LoadPPPoEInf(){
		
		trimString(gPPPoEInf);
	}
	
	private String[] getPPPoEInf(){
		return this.gPPPoEInf;
	}
	
	@Override
	public int getConnectionState() {
		this.LoadPPPoEInf();
		if(this.getPPPoEInf()!=null){
			
		}
		return CONNECTION_OPERATION_NO_MODE;
	}

	@Override
	public void trackLink() {
		this.setState("已经处理拨号操作，Tenda路由器请自行检查连接状态。");

	}
	
	private String[] gWifiData;
	
	private void LoadWifiData(){
		
		trimString(gWifiData);
	}
	
	private String[] getWifiData(){
		return this.gWifiData;
	}

	@Override
	public void setWifiState(WiFiInfo pW) {
		

	}

	@Override
	public WiFiInfo getWifiState() {
		this.LoadWifiData();
		if(this.getWifiData()!=null){
			//
		}
		return null;
	}

	@Override
	protected int setDialProperty(HttpURLConnection mRouterUrlCon) {
		switch(this.mAuthMethod){
		case AUTH_WEB:{
			mRouterUrlCon.setRequestProperty("Host", this.gRouterIP);
			mRouterUrlCon.setRequestProperty("Connection", "Keep-alive");
			mRouterUrlCon.setRequestProperty("Cache-Control", "max-age=0");
			mRouterUrlCon.setRequestProperty("Accept", " text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
			mRouterUrlCon.setRequestProperty("Origin", "http://"+this.gRouterIP);
			mRouterUrlCon.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			mRouterUrlCon.setRequestProperty("Cookie", "admin:language=cn");
			return 0;
		}
		case AUTH_OLD:{
			
			return 0;
		}
		}
		return -1;
	}

}
