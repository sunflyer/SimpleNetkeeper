package cn.sunflyer.simpnk.control;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 * 用户界面控制类
 * @author CrazyChen
 * @version 1.5.0
 * @since Simple Netkeeper 1.5.0
 * */
public class UIController {
	
	private static boolean gIsToolkitLoaded = false ;

	private static Toolkit gTool = null;
	
	/**
	 * 获取系统Toolkit
	 * */
	public static Toolkit getSystemToolkit(){
		return gTool;
	}
	
	/**
	 * 加载Toolkit
	 * */
	private static boolean loadToolkit(){
		Toolkit pTk = Toolkit.getDefaultToolkit();
		if(pTk!=null){
			gTool = pTk;
		}
		if(gTool != null){
			gIsToolkitLoaded = true ;
			return true;
		}
		else{
			gIsToolkitLoaded = false ;
			return false;
		}
	}
	
	/**
	 * 是否已经加载正确的Toolkit
	 * */
	private static boolean isToolkitLoaded(){
		return gIsToolkitLoaded ? ( gTool != null ) : false ;
	}	
	
	/**
	 * 设置指定的窗体居中
	 * @param frame 窗体变量
	 * */
	public static boolean centerScreen(JFrame frame){
		if(frame==null) return false;
		if(!isToolkitLoaded()){
			if(!loadToolkit()) return false;
		}
		Dimension pDem = gTool.getScreenSize();
		frame.setLocation((int)(pDem.getWidth() - frame.getWidth()) / 2, (int)(pDem.getHeight() - frame.getHeight()) / 2);
		return true;
	}
	
	/**
	 * 显示向导界面
	 * @param parent 父窗体界面
	 * */
	public static void showGuide(JFrame parent){
		
	}
	
	/**
	 * 显示一个消息框，当用户点击“是”按钮时返回true，否则返回false
	 * @param szMsg 消息内容
	 * @param szTitle 标题内容
	 * @return true or false
	 * */
	public static boolean showConfirm(String szTitle,String szMsg){
		return showConfirm(null,szTitle,szMsg);
	}
	
	/**
	 * 显示一个消息框，当用户点击“是”按钮时返回true，否则返回false
	 * @param p 父窗体组件
	 * @param szMsg 消息内容
	 * @param szTitle 标题内容
	 * @return true or false
	 * */
	public static boolean showConfirm(Component p,String szTitle,String szMsg){
		return JOptionPane.showConfirmDialog(p, szMsg, szTitle , JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
	}
	
	/**
	 * 显示一个消息框
	 * @param p 父窗体
	 * @param szMsg 消息内容
	 * @param szTitle 窗口标题
	 * */
	public static void showMessage(Component p,String szTitle,String szMsg){
		JOptionPane.showMessageDialog(p, szMsg , szTitle , JOptionPane.INFORMATION_MESSAGE);
	}
	
	/**
	 * 显示一个消息框
	 * @param szMsg 消息内容
	 * @param szTitle 窗口标题
	 * */
	public static void showMessage(String szTitle,String szMsg){
		showMessage(null,szTitle,szMsg);
	}
	
	//=====================================================================
	
	
	

}
