package cn.sunflyer.simpnk.obj;

public class WiFiInfo {
	private String gWifiName;
	private String gWifiPassword;
	private boolean gWifiBroadCast;
	
	public WiFiInfo(String name,String key,boolean allowBroadCast){
		this.setWifiName(name);
		this.setWifiBroadCast(allowBroadCast);
		this.setWifiPassword(key);
	}
	
	public WiFiInfo(String name,String key){
		this(name,key,true);
	}

	public String getWifiName() {
		return gWifiName;
	}

	public void setWifiName(String gWifiName) {
		this.gWifiName = gWifiName;
	}

	public String getWifiPassword() {
		return gWifiPassword;
	}

	public void setWifiPassword(String gWifiPassword) {
		this.gWifiPassword = gWifiPassword;
	}

	public boolean isWifiBroadCast() {
		return gWifiBroadCast;
	}

	public void setWifiBroadCast(boolean gWifiBroadCast) {
		this.gWifiBroadCast = gWifiBroadCast;
	}
}
