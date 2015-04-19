package cn.sunflyer.simpnk.control;


/**
 * Windows 拨号，JNI模块类
 * @author CrazyChen
 * @version 1.5.0
 * @since Simple Netkeeper 1.5.0
 * 
 * */
public class DialWindows {

	public static final int HEARTBEAT_NOT_SUPPORT = 123456;
	/**
	 * 本地拨号功能实现，这是一个本地方法，需要库文件实现
	 * @param szUsername 拨号用户名
	 * @param szPassword 拨号密码
	 * @param szDialEntry 拨号连接名称
	 * @return 返回连接错误代码
	 * */
	public static synchronized native int Dial(String szUsername,String szPassword,String szDialEntry);
	
	/**
	 * 心跳包发送功能实现，这是一个本地方法，需要库文件实现
	 * @param szTargetIpAdd 服务器IP地址
	 * @param szHeartBeatContent 心跳包内容
	 * @param intTargetPort 服务器心跳包端口
	 * @return 返回心跳包错误代码，如果不支持心跳操作，返回123456
	 * */
	public static synchronized native int HeartBeat(String szTargetIpAdd,String szHeartBeatContent,int intTargetPort);
	
	/**
	 * 返回拨号功能错误代码对应的提示文本，这是一个本地方法，需要库文件实现
	 * @param intErrorCode 错误代码
	 * @return 返回错误代码代表的提示文本
	 * */
	public static native String getDialResult(int intErrorCode);
	
	public static String getDialRes(int ErrorCode){
		
		return null;
	}
}
