package cqxinli;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URLEncoder;

public class RouterTpVer4 extends RouterSet {
	private CXKUsername mUn;
	
	public RouterTpVer4(String RouterID, String RouterKey, String IP,
			String AccName, String AccPassword) {
		super(RouterID, RouterKey, IP, AccName, AccPassword);
		
	}

	public RouterTpVer4(String RouterID, String RouterKey, String IP,
			String AccName, String AccPassword, int AuthMethod) {
		super(RouterID, RouterKey, IP, AccName, AccPassword, AuthMethod);
		
	}

	@Override
	protected void testLink() {
		

	}

	@Override
	protected void detectOld() {
		

	}

	@Override
	public int connect() {
		if(this.getConnectionState()==Router.CONNECTION_SUCCESS) return Router.RES_SUCCESS;
		if(this.mIsInit && this.mAuthMethod!=Router.AUTH_NOT_AVALIABLE){
			if(this.mUn==null) this.mUn=new CXKUsername(this.gAccName);
			try {
				String encodeName=null;
				String encodePassword=null;
				if(MainClass.getEncrytedAcc()){
					try {
						Log.log("开始计算加密用户名数据");
						// 替换出现的+为空格，否则用户名错误。
						encodeName = URLEncoder.encode(mUn.Realusername(), "UTF-8")
								.replace("+", "%2D");
						encodePassword = URLEncoder.encode(this.gAccPassword, "UTF-8");
					} catch (Exception ex) {
						Log.log(mUn==null?"true":"false");
						Log.logE(ex);
						return Router.RES_UNABLE_ENCODE;
					}
				}else{
					Log.log("检测到连接模式为《家用模式》");
					try {
						encodeName=URLEncoder.encode(this.gAccName,"UTF-8").replace("+", "%2D");
						encodePassword=URLEncoder.encode(this.gAccPassword,"UTF-8");
					} catch (UnsupportedEncodingException e) {
						Log.logE(e);
						return Router.RES_UNABLE_ENCODE;
					}
					
				}
				String URL=null;
				switch(MainClass.getDialType()){
				case MainClass.DIAL_AUTO:
					//自动拨号模式的访问位置
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
				case MainClass.DIAL_BY_USER:
					URL="http://"
							+ this.gRouterIP
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
				
				HttpURLConnection pCon=this.getConnection(URL);
				if(this.setDialProperty(pCon)!=0) return Router.RES_AUTHENTICATION_NO_METHED;
				Log.log("正在尝试请求数据");
				pCon.connect();
				return this.getResponse(pCon.getInputStream(),URL);
			} catch (IOException e) {
				Log.logE(e);
			}
		}
		
		return this.mIsInit?Router.RES_NO_DIAL_MODE:Router.RES_META_DATA_NOT_INIT;
	}

	@Override
	public int getConnectionState() {
		
		return 0;
	}

	@Override
	public void trackLink() {
		

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
		
		return 0;
	}

}
