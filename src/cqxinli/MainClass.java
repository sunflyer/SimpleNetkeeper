package cqxinli;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Properties;

public class MainClass {
	private static String __g_ver_Build="201408262302";
	private static int __g_ver_MainVer=1;
	private static int __g_ver_SubVer=0;
	private static int __g_ver_FixVer=15;
	private static String __g_data_file_name="NetkeeperForRouter.ini";
	
	public static String getVersion(){
		return __g_ver_MainVer+"."+__g_ver_SubVer+"."+__g_ver_FixVer+"(Build"+__g_ver_Build+")";
	}
	
	public static String getBuild(){
		return __g_ver_Build;
	}
	
	public static String getVersionNoBuild(){
		return __g_ver_MainVer+"."+__g_ver_SubVer+"."+__g_ver_FixVer;
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
			}catch(FileNotFoundException e){
				e.printStackTrace();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		
	}
	
	//保存用户数据
	public static void saveUserData(FormPanel name,PasswordPanel pwd,FormPanel ip,FormPanel adminName,PasswordPanel adminPswd){
		Properties pro=new Properties();
		try{
			File f=new File(System.getProperty("user.dir")+File.separator+__g_data_file_name);
			if(!f.exists()) f.createNewFile();
			pro.load(new FileInputStream(f));
			pro.setProperty("AccName", name.getValue());
			pro.setProperty("AccPassword", Base64.encode(pwd.getPassword()));
			pro.setProperty("RouterIP", ip.getValue());
			pro.setProperty("RouterAdmin",Base64.encode(adminName.getValue()));
			pro.setProperty("RouterPassword", Base64.encode(adminPswd.getPassword()));
			pro.store(new FileOutputStream(f), "Saved Data For Netkeeper Dialer For Router");
		}catch(FileNotFoundException e){
			e.printStackTrace();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new DataFrame("Netkeeper For Router");
	}

}
