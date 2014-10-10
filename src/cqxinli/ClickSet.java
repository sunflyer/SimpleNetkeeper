package cqxinli;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;


public class ClickSet implements ActionListener{

	private DataFrame mDF;
	private JButton mButton;
	
	private RouterSet mRouter;
	public ClickSet(JButton but){
		this.mButton=but;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		this.mDF=MainClass.getDataFrame();
		if(this.mDF==null){
			this.setRes("面板操作出现错误");
			return;
		}
		//构造函数：ip,路由器用户名，密码，拨号账户，密码
		if(mDF.g_getAccName().equals("") || mDF.g_getAccPassword().equals("") || mDF.g_getRouterAdmin().equals("")) return;
		else{
			MainClass.saveUserData();
			
			setRes("正在尝试为你设置连接，请稍后。。。");
			new Thread(new Runnable(){
				@Override
				public void run() {
					mButton.setEnabled(false);
					mRouter=new Router(mDF.g_getRouterIP(),mDF.g_getRouterAdmin(),mDF.g_getRouterPassword(),mDF.g_getAccName(),mDF.g_getAccPassword(),MainClass.getAuthMethod());
					Log.log("正在配置用户连接，宽带账号名称"+mDF.g_getAccName()+",路由器账号："+mDF.g_getRouterAdmin());
					
					switch(mRouter.connect()){
					case -2:setRes("尝试连接路由器时发生问题，无法登陆验证");break;
					case -1:setRes("尝试对用户名和密码编码时出现问题");break;
					case 0:setRes("已成功进行连接操作，请在1分钟后检查网络连接");mRouter.trackLink();break;
					case 1:setRes("给定的路由器地址不合法。");break;
					case 2:setRes("没有指定拨号模式，可能是因为与路由器不支持");break;
					case 3:setRes("尝试操作时发生错误，没有足够的权限");break;
					case 4:setRes("IOException Occured");break;
					case 5:setRes("请求操作失败：无法获取链接对象");break;
					case 6:setRes("找不到合适的验证方式，请求操作出现错误。");break;
					case 7:setRes("请求操作失败：提供的数据无法被初始化");break;
					case 8:setRes("尝试加密账号出现错误");break;
					case 9:setRes("检测到额外的不可替代的登陆操作，或者权限不足");break;
					default:setRes("Unknown Error");
					}	
					mButton.setEnabled(true);
				}
				
			}).start();
		}
	}
	
	private void setRes(String r){
		DataFrame.showTips(r);
		Log.log(r);
	}
	
}
