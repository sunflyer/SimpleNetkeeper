package cqxinli;

import java.net.HttpURLConnection;

public class RouterTenda extends RouterSet {

	protected RouterTenda(String RouterID, String RouterKey, String IP,
			String AccName, String AccPassword) {
		super(RouterID, RouterKey, IP, AccName, AccPassword);
	}
	
	protected RouterTenda(String RouterID, String RouterKey, String IP,
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
		
		return 0;
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
			
			return 0;
		}
		case AUTH_OLD:{
			
			return 0;
		}
		}
		return -1;
	}

}
