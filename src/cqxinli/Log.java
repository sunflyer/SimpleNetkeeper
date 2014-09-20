package cqxinli;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

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
	
	public static void initLogger(String FilePath){
		gLogPath=FilePath;
		gLogFile=new File(gLogPath,Log.gLogFileName);
		try{
			if(!gLogFile.exists()){
				gLogFile.createNewFile();
			}
			gIsInit=true;
		}
		catch(IOException ex){
			ex.printStackTrace();
			gIsInit=false;
			Log.gInitErrorTime++;
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
				mLogger=new FileWriter(Log.gLogFile,true);
				mLogger.write((new Date()).toString()+"："+pCon+Log.nLine);
				mLogger.close();
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
	
	public static void logE(Exception e){
		if(isInit()){
			try {
				mLogger=new FileWriter(Log.gLogFile,true);
				mLogger.write((new Date()).toString()+"：################发现异常################"+Log.nLine+e.getMessage()+Log.nLine+Log.getStackTrace(e)+Log.nLine);
				mLogger.close();
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
	
	public static String getStackTrace(Exception e){
		StringBuffer pSb=new StringBuffer();
		StackTraceElement[] pSte=e.getStackTrace();
		for(int i=0;i<pSte.length;i++)
			pSb.append(pSte[i].toString()+Log.nLine);
		return pSb.toString();
	}
}
