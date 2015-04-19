package cqxinli;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;

public class RouterFast extends RouterSet {

	public RouterFast(String RouterID, String RouterKey, String IP,
			String AccName, String AccPassword) {
		super(RouterID, RouterKey, IP, AccName, AccPassword);
		// TODO Auto-generated constructor stub
	}

	public RouterFast(String RouterID, String RouterKey, String IP,
			String AccName, String AccPassword, int AuthMethod) {
		super(RouterID, RouterKey, IP, AccName, AccPassword, AuthMethod);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void testLink() {
		// TODO Auto-generated method stub
		try {
			HttpURLConnection pHuc=this.getConnection(getSvrURL());
			pHuc.setRequestProperty("Authorization", this.getBase64Acc());
			pHuc.connect();
			String HTML=this.getHTMLContent(pHuc.getInputStream());
			if(HTML.indexOf("iframe")>=0){
				this.changeAuthMethod(AUTH_WEB);
				this.changeInitState(true);
			}
		} catch (MalformedURLException e) {
			Log.logE(e);
		} catch (IOException e) {
			Log.logE(e);
		}
	}

	@Override
	protected void detectOld() {
		

	}

	@Override
	public int connect() {
		if(this.mIsInit){
			try {
				
				//String pUsername=MainClass.getEncrytedAcc()?(new CXKUsername(this.gAccName).Realusername()):this.gAccName;
				
				HttpURLConnection pHuc=this.getConnection(this.getSvrURL()+"cgi?2&2");
				pHuc.setRequestMethod("POST");
				if(this.setDialProperty(pHuc)!=0) return RES_AUTHENTICATION_NO_METHED;
				PrintWriter pPw=new PrintWriter(pHuc.getOutputStream());
				pPw.print("");
				pPw.flush();
				pPw.close();
				pHuc.connect();
				String HTML=this.getHTMLContent(pHuc.getInputStream());
				Log.log(HTML);
				
				return HTML.indexOf("iframe")>=0?RES_SUCCESS:RES_UNABLE_ACCESS;
			} catch (IOException e) {
				Log.logE(e);
				return RES_IO_EXCEPTION;
			}
		}
		return RES_META_DATA_NOT_INIT;
	}

	@Override
	public int getConnectionState() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void trackLink() {
		// TODO Auto-generated method stub

	}

	@Override
	public void setWifiState(WiFiInfo pW) {
		// TODO Auto-generated method stub

	}

	@Override
	public WiFiInfo getWifiState() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	protected void setProperties(HttpURLConnection mR){
		mR.setRequestProperty("Host", this.gRouterIP);
		mR.setRequestProperty("Connection", "Keep-alive");
		mR.setRequestProperty("Content-Type", "text-plain");
		mR.setRequestProperty("Referer", this.getSvrURL());
	}
	

	@Override
	protected int setDialProperty(HttpURLConnection mRouterUrlCon) {
		// TODO Auto-generated method stub
		switch(this.mAuthMethod){
		case AUTH_WEB:mRouterUrlCon.setRequestProperty("Cookie", "Authorization="+this.getBase64Acc());return 0;
		case AUTH_OLD:break;
		}
		return -1;
	}
	
	@Override 
	protected String getBase64Acc(){
		return "Basic "+Base64.encode(this.gRouterAccPassword);
	}

}
