package cn.sunflyer.simpnk.control;

import android.os.Environment;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.Properties;

public class Log {
	public static final String gLogFileName="NetkeeperLog.log";
	public static final String gLogFilePathWindows="C:\\";	
	public static final String gLogFilePathLinux="/usr/share/";
	public static final String gLogFilePathAndroid = Environment.getExternalStorageDirectory().toString();
	public static final String nLine=System.getProperties().getProperty("line.separator");
	private static File gLogFile=null;

	private static boolean gIsInit=false;
	
	private static FileWriter mLogger=null;
	private static String gLogPath=null;
	private static int gInitErrorTime=0;
	public static final int gInitErrorTimeLimit=6;

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

	public static void log(String pCon){
		if(isInit()){
			try {
				mLogger.write(new Date()+" - "+pCon+Log.nLine);
				mLogger.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}else{
			if(Log.gInitErrorTime<Log.gInitErrorTimeLimit){
				initLogger(gLogFilePathAndroid);
				log(pCon);
			}			
		}
	}

	public static void logE(Exception e){
		if(isInit()){
			try {

				mLogger.write((new Date()).toString()+"################出现异常信息################"
						+ Log.nLine + e.toString()
						+ Log.nLine + e.getMessage() + Log.nLine + Log.getStackTrace(e) +Log.nLine);
				mLogger.flush();
			} catch (IOException ex) {
				e.printStackTrace();
			}
		}else{
			if(Log.gInitErrorTime<Log.gInitErrorTimeLimit){
				initLogger(gLogFilePathAndroid);
				logE(e);
			}			
		}
	}

	public static String getStackTrace(Exception e){
		StringBuffer pSb=new StringBuffer();
		StackTraceElement[] pSte=e.getStackTrace();
		for(int i=0;i<pSte.length;i++)
			pSb.append(pSte[i].toString()+Log.nLine);
		return pSb.toString();
	}

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

		}
	}

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
			initLogger(gLogFilePathAndroid);
			return pLogFile.exists();
		}
		
		return false;
	}
}
