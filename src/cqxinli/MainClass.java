package cqxinli;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Properties;

import javax.swing.JOptionPane;

public class MainClass {
	public static final String __g_ver_Build="201409281657";
	public static final int __g_ver_MainVer=1;
	public static final int __g_ver_SubVer=1;
	public static final int __g_ver_FixVer=0;
	public static final String __g_data_file_name="NetkeeperForRouter.ini";
	
	public static final int VER_REL=0;
	public static final int VER_DEBUG=1;
	public static final int VER_BETA=2;
	public static final int VER_SPEC=3;
	//版本标识  0-Release 1-Debug 2-Beta 3-Special
	private static int __g_ver_VerSign=VER_DEBUG;
	
	
	//DEBUG标签
	private static boolean allowDebug=false;
	
	public static boolean isDebugAllow(){
		return MainClass.allowDebug;
	}
	
	//路由器拨号配置数据
	//算法版本
	public static final String []AlgVer={"0055","0087"};
	public static final int ALG_87=87;
	public static final int ALG_55=55;
	
	private static int gAlgVer=ALG_55;
	public static int getAlgVer(){
		return gAlgVer;
	}
	
	//是否需要加密账号，以方便普通家用
	private static boolean __g_isEncrypted=true;
	public static boolean getEncrytedAcc(){
		return MainClass.__g_isEncrypted;
	}
	public static void setEncryptedAcc(boolean i){
		MainClass.__g_isEncrypted=i;
		Log.log("设置是否加密账号："+(i?"是":"否"));
	}
	
	//拨号方式 
	public static final String[] DialList={"自动连接","手动连接"};
	public static final int DIAL_AUTO=0;
	public static final int DIAL_BY_USER=1;
	
	private static int __g_DialType=DIAL_BY_USER;
	public static int getDialType(){
		return MainClass.__g_DialType;
	}
	
	public static void setDialType(String DialType){
		for(int i=0;i<MainClass.DialList.length;i++){
			if(DialType.indexOf(MainClass.DialList[i])>=0)
				MainClass.__g_DialType=i;
		}
		Log.log("已设置用户拨号类型为："+DialList[getDialType()]);
	}
	
	//对于手动连接
	
	//路由器类型
	public static final String[] RouterList={"MECURY/TP-LINK/FAST","DLINK","OPENWRT","TOMATO"};
	
	public static final int ROUTER_MERCURY_TP_FAST=1;
	public static final int ROUTER_DLINK=2;
	public static final int ROUTER_3RD_OPENWRT=3;
	public static final int ROUTER_3RD_TOMATO=4;
	
	public static final String ENCODE_GB2312="GB2312";
	public static final String ENCODE_UTF_8="UTF-8";
	
	private static String __g_Router_EncodePage=MainClass.ENCODE_GB2312;
	private static int __g_Router_Manufactor=MainClass.ROUTER_MERCURY_TP_FAST;
	public static int getRouterManufactor(){
		return MainClass.__g_Router_Manufactor;
	}
	
	public static String getRouterPageEncode(){
		return MainClass.__g_Router_EncodePage;
	}
	
	public static void setRouterPageEncode(String encode){
		if(encode.equals(MainClass.ENCODE_GB2312)){
			MainClass.__g_Router_EncodePage=MainClass.ENCODE_GB2312;
		}else{
			MainClass.__g_Router_EncodePage=MainClass.ENCODE_UTF_8;
		}
		Log.log("已设置页面编码方式为："+MainClass.getRouterPageEncode());
	}
	
	public static void setRouterManufactor(int RM){
		if(RM>=1 && RM<=4){
			MainClass.__g_Router_Manufactor=RM;
			if(RM==MainClass.ROUTER_MERCURY_TP_FAST) 
				MainClass.setRouterPageEncode(ENCODE_GB2312);
			Log.log("已设置路由器为："+MainClass.RouterList[RM-1]);
		}
	}
	
	//版本验证方式
	private static int gAuthMethod=Router.AUTH_NOT_AVALIABLE;
	
	
	//获取APP版本
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
	
	public static final String Config_Acc_Name="AccName";
	public static final String Config_Acc_Password="AccPassword";
	public static final String Config_Router_IP="RouterIP";
	public static final String Config_Router_Name="RouterAdmin";
	public static final String Config_Router_Password="RouterPassword";
	public static final String Config_Router_AuthMethod="AuthMethod";
	public static final String Config_Router_DialMode="DialMode";
	public static final String Config_Router_DialPlace="DialPlace";
	public static final String Config_Router_Encrypt="Encrypt";
	public static final String Config_Router_Manufactor="Manufactor";
	public static final String Config_Application_ConfVer="ConfigVersion";
	public static final String Config_Application_CurrentVersion="3";
	
	//读取并设置用户数据
	public static void setUserData(FormPanel name,PasswordPanel pwd,FormPanel ip,FormPanel adminName,PasswordPanel adminPswd){
		File f=new File(System.getProperty("user.dir")+File.separator+__g_data_file_name);
		if(f.exists()){
			Properties pro=new Properties();
			try{
				pro.load(new FileInputStream(System.getProperty("user.dir")+File.separator+__g_data_file_name));
				
				name.setValue(pro.getProperty(MainClass.Config_Acc_Name));
				pwd.setPassword(Base64.decode(pro.getProperty(MainClass.Config_Acc_Password)));
				ip.setValue(pro.getProperty(MainClass.Config_Router_IP));
				adminName.setValue(Base64.decode(pro.getProperty(MainClass.Config_Router_Name)));
				adminPswd.setPassword(Base64.decode(pro.getProperty(MainClass.Config_Router_Password)));
				MainClass.setAuthMethod(Integer.parseInt(pro.getProperty(MainClass.Config_Router_AuthMethod)));
				Log.log("通过读取配置文件取得的基本拨号方式为："+pro.getProperty(MainClass.Config_Router_AuthMethod));
				
				if(pro.getProperty(MainClass.Config_Application_ConfVer)!=null){
					//配置信息			
					switch(pro.getProperty(MainClass.Config_Application_ConfVer)){
					case MainClass.Config_Application_CurrentVersion:{
						MainClass.setDialType(MainClass.DialList[Integer.parseInt(pro.getProperty(MainClass.Config_Router_DialMode))]);
						MainClass.setEncryptedAcc(pro.getProperty(MainClass.Config_Router_Encrypt).equals("true"));
						MainClass.setRouterManufactor(Integer.parseInt(pro.getProperty(MainClass.Config_Router_Manufactor)));
					}break;
					case "2":{
						MainClass.setDialType(MainClass.DialList[Integer.parseInt(pro.getProperty(MainClass.Config_Router_DialMode))>=1?1:0]);
						MainClass.setEncryptedAcc(pro.getProperty(MainClass.Config_Router_Encrypt).equals("true"));
						MainClass.setRouterManufactor(Integer.parseInt(pro.getProperty(MainClass.Config_Router_Manufactor)));
					}break;
					default:{
						Log.log("其他配置读取出现错误，无法操作，保持默认");
					}
					}
				}else{
					Log.log("配置文件版本属性不存在，无法继续读取操作。");
				}
				
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
			pro.setProperty(MainClass.Config_Acc_Name, name);
			pro.setProperty(MainClass.Config_Acc_Password, Base64.encode(pwd));
			pro.setProperty(MainClass.Config_Router_IP, ip);
			pro.setProperty(MainClass.Config_Router_Name,Base64.encode(adminName));
			pro.setProperty(MainClass.Config_Router_Password, Base64.encode(adminPswd));
			pro.setProperty(MainClass.Config_Router_AuthMethod, ""+MainClass.getAuthMethod());
			pro.setProperty(MainClass.Config_Application_ConfVer, MainClass.Config_Application_CurrentVersion);
			pro.setProperty(MainClass.Config_Router_DialMode, MainClass.getDialType()+"");
			pro.setProperty(MainClass.Config_Router_Encrypt, MainClass.getEncrytedAcc()?"true":"false");
			pro.setProperty(MainClass.Config_Router_Manufactor, MainClass.getRouterManufactor()+"");
			pro.store(new FileOutputStream(f), "Netkeeper For Router Configuration File");
			Log.log("已成功保存配置文件");
		}catch(FileNotFoundException e){
			Log.log(e.getMessage());
		}catch(Exception e){
			Log.log(e.getMessage());
		}
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Log.log(Log.nLine+"==========================已经启动===============================");
		for(int i=0;i<args.length;i++){
			if(args[i].equals("debug") || args[i].equals("/debug")){
				MainClass.allowDebug=true;
				Log.log("检测到命令行指令参数："+args[i]+"，允许调试模式启动");
			}
		}		
		Log.log("应用程序版本为"+MainClass.getVersion());
		DataFrame pDF=new DataFrame("Netkeeper For Router");
		String tConfirmData="您应当为本软件的使用以及行为受到约束，在同意以下条件的情况下，您可以免费使用、"
				+ "\n分发、修改本软件或基于本软件源代码创建新的程序：\n\n"
				+ "1.你不能将本软件或/和本软件的源代码用于商业用途，包括但不限于出售本软件（以任何形式）。\n\n"
				+ "2.你不能将本软件的任何修改/衍生版本用于商业用途，限制条件等同本软件的原始副本\n\n"
				+ "3.你可以将本软件共享给你的朋友，但是你的朋友使用本软件依然需要遵循以上要求。\n\n"
				+ "4.你在此确认并同意，如果你违反了以上规则，软件的原作者有权利要求你停止违规行为。\n\n"
				+ "如果你同意以上约束，请点击“是”继续操作，否则请退出。";
		if(!new File(System.getProperty("user.dir")+File.separator+__g_data_file_name).exists()){
			if(JOptionPane.showConfirmDialog(pDF, tConfirmData)==JOptionPane.YES_OPTION){
				JOptionPane.showMessageDialog(pDF, "欢迎使用Netkeeper For Router（重庆高校版本）\n使用方式请参见ReadMe文档\n如果您在使用过程中遇到任何问题，请将文件夹内的NetkeeperLog.Log发送到 cx@itncre.com\n非常感谢！\n\n隐私声明：\n本软件的源代码已公布在GitHub,使用过程中不会发送任何隐私信息给任何人，敬请留意！");
			}else{
				System.exit(0);
			}
		}
		
	}

	public static int getAuthMethod(){
		return MainClass.gAuthMethod;
	}
	
	public static void setAuthMethod(int aM){
		MainClass.gAuthMethod=aM;
	}
}
