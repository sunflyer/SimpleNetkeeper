package cn.sunflyer.simpnk.obj;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public abstract class PluginConfig {
	
	//配置文件修饰符
	public static final String Config_Prefix_Conf = "conf",
			Config_Prefix_Dial = "dial",
			Config_Prefix_Router = "router";
	
	
	//配置文件项目//
	public static final String Conf_name = "name",
				Conf_author = "author",
				Conf_mail = "mail",
				Conf_version = "version", 
				Conf_date = "date",
				Conf_type= "type";
	
	//拨号项目
	public static final String Dial_Libx86 = "libx86",
			Dial_Libx64 = "libx64",
			Dial_Platform = "platform",
			Dial_Heartbeat = "heartbeat",
			Dial_Entry = "entry",
			Dial_AccProvidor = "providor";
	
	//路由器项目
	public static final String Router_Libx86 = "libx86",
			Router_Libx64="libx64";
	
	//方法集合开始
	
	/**
	 * 用于收集配置文件数据的Properties
	 * */
	protected Properties gConfigData;

	
	public PluginConfig(String szFileName) throws FileNotFoundException, IOException{
		this.gConfigData = new Properties();
		File mConfig = new File(szFileName);
		if(mConfig.exists()){
			gConfigData.load(new FileInputStream(mConfig));
			if(gConfigData.isEmpty()) throw new FileNotFoundException("文件为空，不能创建");
			this.loadConf();
		}
	}
	
	public PluginConfig(Properties prop) throws FileNotFoundException{
		if(prop == null || prop.isEmpty()) throw new FileNotFoundException("文件为空，不能创建");
		this.gConfigData = prop;
	}
	
	/**获取指定项目,不存在则返回null字符串或者默认值*/
	public String get(String szName){
		return gConfigData.isEmpty() ? null : gConfigData.getProperty(szName);
	}
	
	/***/
	public String getDef(String szName,String def){
		String pRes = this.get(szName);
		return pRes == null ? def : pRes;
	}
	
	/**注册到插件控制器*/
	public abstract boolean register();
	
	/**是否合法配置*/
	public boolean isValid = false;
	
	/**配置文件基本属性表*/
	public String configName = "";
	public String configAuthor = "";
	public String configMail = "";
	public String configVersion = "";
	public String configDate = "";
	public String configType = "";
	
	protected void loadConf(){
		if(this.gConfigData!=null && !this.gConfigData.isEmpty()){
			this.configAuthor = this.getDef(Config_Prefix_Conf+"."+Conf_author, "未知作者");
			this.configDate = this.getDef(Config_Prefix_Conf+"."+Conf_date, "未知时间");
			this.configMail = this.getDef(Config_Prefix_Conf+"."+Conf_mail, "未知");
			this.configType = this.getDef(Config_Prefix_Conf+"."+Conf_type, "-1");
			this.configVersion = this.getDef(Config_Prefix_Conf+"."+Conf_version, "未知版本");
			this.configName = this.getDef(Config_Prefix_Conf+"."+Conf_author, "未命名配置文件");
			
			if(this.configType == null || this.configType.equals("-1")) this.isValid = false;
		}
	}

}
