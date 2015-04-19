package cn.sunflyer.simpnk.control;

import java.util.ArrayList;
import java.util.List;

import cn.sunflyer.simpnk.obj.PluginDial;
import cn.sunflyer.simpnk.obj.PluginRouter;

public class PluginController {

	/**插件总目录*/
	public static final String PLUGIN_PATH = "plugin";
	
	/**拨号插件目录*/
	public static final String PLUGIN_DIAL = "dial";
	
	/**路由器插件总目录*/
	public static final String PLUGIN_ROUTER = "router";
	
	/**
	 * 存放拨号插件列表
	 * */
	private static List<PluginDial> gPluginDial = new ArrayList<PluginDial>();
	
	/**
	 * 存放路由器插件列表
	 * */
	private static List<PluginRouter> gPluginRouter = new ArrayList<PluginRouter>();
	
	/**
	 * 注册一个拨号插件
	 * */
	public static boolean addPluginDial(PluginDial x){
		if(x!=null){
			
		}
		return false;
	}
	
	/**
	 * 注册一个路由器插件
	 * */
	public static boolean addPluginRouter(PluginRouter x){
		if(x!=null){
			
		}
		return false;
	}
	
	/**
	 * 获取可用拨号插件数目
	 * */
	public static int getNumberOfDail(){
		return gPluginDial.size();
	}
	
	/**
	 * 获取可用路由器插件数目
	 * */
	public static int getNumberOfRouter(){
		return gPluginRouter.size();
	}
	
	/**
	 * 获取拨号插件
	 * @param path 下表
	 * @return 如果制定下表可用，返回对象，否则返回null
	 * */
	public static PluginDial getPluginDial(int path){
		return path >= 0 && path < getNumberOfDail() ? gPluginDial.get(path) : null;
	}
	
	/**
	 * 获取路由器插件
	 * @param path 下表
	 * @return 如果制定下表可用，返回对象，否则返回null
	 * */
	public static PluginRouter getPluginRouter(int path){
		return path >= 0 && path < getNumberOfRouter() ? gPluginRouter.get(path) : null;
	}
	
	/**
	 * 删除指定插件
	 * @param path
	 * @return 如果删除成功或者不存在，返回true
	 * */
	public static boolean removePluginDial(int path){
		if(path >= 0  && path < getNumberOfDail()){
			return gPluginDial.remove(path) == null;
		}
		return true;
	}
	
	/**
	 * 删除指定插件
	 * @param path
	 * @return 如果删除成功或者不存在，返回true
	 * */
	public static boolean removePluginRouter(int path){
		if(path >= 0  && path < getNumberOfRouter()){
			return gPluginRouter.remove(path) == null;
		}
		return true;
	}
	
	/**从文件夹读取并自动载入插件信息*/
	public static void loadPlugin(){
		
	}
	
	
}
