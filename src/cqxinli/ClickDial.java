package cqxinli;


/**
 * Windows 拨号，JNI模块类
 * @author CrazyChen
 * @version 1.5.0
 * @since Simple Netkeeper 1.5.0
 * 
 * */
public class ClickDial {
	/**
	 * 本地拨号功能实现，这是一个本地方法，需要库文件实现
	 * @param szUsername 拨号用户名
	 * @param szPassword 拨号密码
	 * @param szDialEntry 拨号连接名称，暂时会被忽略
	 * @return 返回连接错误代码
	 * */
	public static synchronized native int dialRasWindows(String szUsername,String szPassword,String szDialEntry);
	
	/**
	 * 返回拨号功能错误代码对应的提示文本，这是一个本地方法，需要库文件实现
	 * @param intErrorCode 错误代码
	 * @return 返回错误代码代表的提示文本
	 * */
	public static native String dialRasWindowsErrorStr(int intErrorCode);
}
