package cn.sunflyer.simpnk.control;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.Properties;

public class Log {
	public static final String gLogFileName="NetkeeperLog.log";
	public static final String gLogFilePathWindows="C:\\";	
	public static final String gLogFilePathLinux="/usr/share/";
	public static final String nLine=System.getProperties().getProperty("line.separator");
	private static File gLogFile=null;
	//初始化事件记录的标记
	private static boolean gIsInit=false;
	
	private static FileWriter mLogger=null;
	private static String gLogPath=null;
	private static int gInitErrorTime=0;
	public static final int gInitErrorTimeLimit=6;
	
	/**
	 * 初始化当前记录器
	 * @param FilePath 日志文件目录
	 * */
	public static void initLogger(String FilePath){
		gLogPath=FilePath;
		gLogFile=new File(gLogPath,Log.gLogFileName);
		try{
			if(!gLogFile.exists()){
				gLogFile.createNewFile();
			}
			mLogger = new FileWriter(Log.gLogFile,true);
			gIsInit=true;
		}
		catch(Exception ex){
			ex.printStackTrace();
			gIsInit=false;
			Log.gInitErrorTime++;
		}
	}
	
	public static boolean isInit(){
		return gIsInit;
	}
	
	/**
	 * 记录当前系统的部分信息，包括操作系统的名称以及JAVA版本
	 * */
	public static void logSysInfo(){
		Properties pPro=System.getProperties();
		String pOsName=pPro.getProperty("os.name");
		String pOsVer=pPro.getProperty("os.version");
		String pJavaVer=pPro.getProperty("java.version");
		String pOsArch=pPro.getProperty("os.arch");
		String pJavaSpecVer=pPro.getProperty("java.specification.version");
		log("=========================System Information=======================");
		log("操作系统："+pOsName+"，版本："+pOsVer+"，架构："+pOsArch);
		log("JAVA版本："+pJavaVer+"(Spec:"+pJavaSpecVer+")");
	}
	
	/**
	 * 记录一段文本。
	 * */
	public static void log(String pCon){
		if(isInit()){
			try {
				mLogger.write(new Date()+"："+pCon+Log.nLine);
				mLogger.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}else{
			if(Log.gInitErrorTime<Log.gInitErrorTimeLimit){
				initLogger(System.getProperty("user.dir"));
				log(pCon);
			}			
		}
	}
	
	/**
	 * 记录一个异常到日志文件，包括错误内容以及堆栈文本
	 * @param e 异常对象
	 * */
	public static void logE(Exception e){
		if(isInit()){
			try {
				mLogger.write((new Date()).toString()+"：################发现异常################"+Log.nLine+e.getMessage()+Log.nLine+Log.getStackTrace(e)+Log.nLine);
				mLogger.flush();
			} catch (IOException ex) {
				e.printStackTrace();
			}
		}else{
			if(Log.gInitErrorTime<Log.gInitErrorTimeLimit){
				initLogger(System.getProperty("user.dir"));
				logE(e);
			}			
		}
	}
	
	/**
	 * 获取异常堆栈的 文本格式
	 * @param e 异常内容
	 * @return 返回字符串形式的堆栈描述
	 * */
	public static String getStackTrace(Exception e){
		StringBuffer pSb=new StringBuffer();
		StackTraceElement[] pSte=e.getStackTrace();
		for(int i=0;i<pSte.length;i++)
			pSb.append(pSte[i].toString()+Log.nLine);
		return pSb.toString();
	}
	
	/**
	 * 显示已记录的日志内容
	 * */
	public static void showLog(){
		Runtime pRt = Runtime.getRuntime();
		String pAppName[] = {null , gLogFileName} ;
		if(System.getProperties().getProperty("os.name").contains("Windows")){
			pAppName[0] = "notepad.exe" ;
		}else{
			pAppName[0] = "nano" ;
		}
		try {
			pRt.exec(pAppName);
		} catch (IOException e) {
			logE(e);
			UIController.showMessage("错误", "打开日志文件出现一些问题："+e.getMessage());
		}
	}
	
	/**
	 * 清除现有的日志文件。这个方法先删除文件，然后创建一个同名文件。如果删除失败或者创建文件发生错误，返回false。
	 * */
	public static boolean clearLog(){
		File pLogFile = new File(gLogFileName);
		if(pLogFile.exists()){
			if(mLogger != null)
				try {
					mLogger.close();
				} catch (IOException e1) {
					
					e1.printStackTrace();
				}
			
			if(!pLogFile.delete()){
				return false;
			}
			initLogger(System.getProperty("user.dir"));
			return pLogFile.exists();
		}
		
		return false;
	}
}
