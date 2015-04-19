package cn.sunflyer.simpnk.obj;

public class Bundle {

	/**
	 * 组合消息的标识符源
	 * */
	public String bundleSource ; 
	
	/**
	 * 组合消息的内容
	 * */
	public Object bundleData;
	
	public Bundle(String source,Object data){
		this.bundleData = data;
		this.bundleSource = source;
	}
	
}
