package cqxinli;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Properties;

public class MainClass {
	public static final String __g_ver_Build="201409121946";
	public static final int __g_ver_MainVer=1;
	public static final int __g_ver_SubVer=0;
	public static final int __g_ver_FixVer=21;
	public static final String __g_data_file_name="NetkeeperForRouter.ini";
	
	public static final int VER_REL=0;
	public static final int VER_DEBUG=1;
	public static final int VER_BETA=2;
	public static final int VER_SPEC=3;
	//版本标识  0-Release 1-Debug 2-Beta 3-Special
	private static int __g_ver_VerSign=VER_REL;
	
	private static int gAuthMethod=Router.AUTH_NOT_AVALIABLE;
	
	public static String getVersion(){
		return __g_ver_MainVer+"."+__g_ver_SubVer+"."+__g_ver_FixVer+"(Build"+__g_ver_Build+")"+getVersionRelOrDebug();
	}
	
	public static String getBuild(){
		return __g_ver_Build;
	}
	
	public static String getVersionNoBuild(){
		return __g_ver_MainVer+"."+__g_ver_SubVer+"."+__g_ver_FixVer+getVersionRelOrDebug();
	}
	
	public static int getVersionSig(){
		return MainClass.__g_ver_VerSign;
	}
	
	public static String getVersionRelOrDebug(){
		String sig="";
		switch(MainClass.__g_ver_VerSign){
		case VER_REL:sig="正式版本";break;
		case VER_DEBUG:sig="调试版本";break;
		case VER_BETA:sig="测试版本";break;
		case VER_SPEC:sig="特殊版本";break;
		}
		return sig;
	}
	
	//读取并设置用户数据
	public static void setUserData(FormPanel name,PasswordPanel pwd,FormPanel ip,FormPanel adminName,PasswordPanel adminPswd){
		File f=new File(System.getProperty("user.dir")+File.separator+__g_data_file_name);
		if(f.exists()){
			Properties pro=new Properties();
			try{
				pro.load(new FileInputStream(System.getProperty("user.dir")+File.separator+__g_data_file_name));
				name.setValue(pro.getProperty("AccName"));
				pwd.setPassword(Base64.decode(pro.getProperty("AccPassword")));
				ip.setValue(pro.getProperty("RouterIP"));
				adminName.setValue(Base64.decode(pro.getProperty("RouterAdmin")));
				adminPswd.setPassword(Base64.decode(pro.getProperty("RouterPassword")));
				MainClass.setAuthMethod(Integer.parseInt(pro.getProperty("AuthMethod")));
				Log.log("通过读取配置文件取得的基本拨号方式为："+pro.getProperty("AuthMethod"));
			}catch(FileNotFoundException e){
				Log.log(e.getMessage());
			}catch(Exception e){
				Log.log(e.getMessage());
			}
		}
		
	}
	
	//保存用户数据
	public static void saveUserData(String name,String pwd,String ip,String adminName,String adminPswd){
		Properties pro=new Properties();
		try{
			File f=new File(System.getProperty("user.dir")+File.separator+__g_data_file_name);
			if(!f.exists()) f.createNewFile();
			pro.load(new FileInputStream(f));
			pro.setProperty("AccName", name);
			pro.setProperty("AccPassword", Base64.encode(pwd));
			pro.setProperty("RouterIP", ip);
			pro.setProperty("RouterAdmin",Base64.encode(adminName));
			pro.setProperty("RouterPassword", Base64.encode(adminPswd));
			pro.setProperty("AuthMethod", ""+MainClass.getAuthMethod());
			pro.store(new FileOutputStream(f), "Saved Data For Netkeeper Dialer For Router");
		}catch(FileNotFoundException e){
			e.printStackTrace();
			Log.log(e.getMessage());
		}catch(Exception e){
			e.printStackTrace();
			Log.log(e.getMessage());
		}
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Log.log(Log.nLine+"==========================已经启动===============================");
		Log.log("应用程序版本为"+MainClass.getVersion());
		new DataFrame("Netkeeper For Router");		
	}

	public static int getAuthMethod(){
		return MainClass.gAuthMethod;
	}
	
	public static void setAuthMethod(int aM){
		MainClass.gAuthMethod=aM;
	}
}
