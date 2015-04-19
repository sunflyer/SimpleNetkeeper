package cn.sunflyer.simpnk.model;

import cn.sunflyer.simpnk.control.Log;
import cn.sunflyer.simpnk.control.StatusController;
import cn.sunflyer.simpnk.ui.FrameRouter;

/**
 * 程序主入口
 * */
public class AppEntry {

	/**
	 * 处理命令行参数。如果在命令行参数中检测到非窗口运行，返回false以指示不显示窗口，否则返回true指示窗口显示
	 * */
	public static boolean processArgs(String[] args){
		
		for(String x:args){
			if(x.contains("debug"))
				StatusController.isOnDebug = true;
		}
		
		return true;
	}
	
	/**
	 * 入口方法
	 * */
	public static void main(String[] args){
		
		/**记录开始内容*/
		Log.log("Simple Netkeeper 启动运行，开始载入组件");
		Log.logSysInfo();
		
		//状态控制
		try {
			Class.forName("cn.sunflyer.simpnk.control.StatusController");
		} catch (Exception e) {
			Log.logE(e);
		}

		//处理命令行内容
		if(processArgs(args)){
			new FrameRouter();
		}
		
	}
	
	
}
