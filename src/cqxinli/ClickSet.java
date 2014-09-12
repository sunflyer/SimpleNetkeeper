package cqxinli;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class ClickSet implements ActionListener{

	private FormPanel name;
	private PasswordPanel pwd;
	private FormPanel ip;
	private FormPanel adminName;
	private PasswordPanel adminPswd;
	
	private Router mRouter;
	public ClickSet(FormPanel name,PasswordPanel pwd,FormPanel ip,FormPanel adminName,PasswordPanel adminPswd){
		this.name=name;
		this.pwd=pwd;
		this.ip=ip;
		this.adminName=adminName;
		this.adminPswd=adminPswd;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		//构造函数：ip,路由器用户名，密码，拨号账户，密码
		if(name.getValue().equals("") || pwd.getPassword().equals("") || adminName.getValue().equals("")) return;
		else{
			mRouter=new Router(this.ip.getValue(),this.adminName.getValue(),this.adminPswd.getPassword(),this.name.getValue(),this.pwd.getPassword(),MainClass.getAuthMethod());
			Log.log("正在配置用户连接，宽带账号名称"+this.name.getValue()+",路由器账号："+this.adminName.getValue());
			setRes("正在尝试为你设置连接，请稍后。。。");
			switch(mRouter.connect()){
			case -2:setRes("尝试连接路由器时发生问题，无法登陆验证");break;
			case -1:setRes("尝试对用户名和密码编码时出现问题");break;
			case 0:setRes("已成功进行连接操作，请在1分钟后检查网络连接");break;
			case 1:setRes("给定的路由器地址不合法。");break;
			case 3:setRes("尝试操作时发生错误，没有足够的权限");break;
			case 4:setRes("IOException Occured");break;
			case 5:setRes("请求操作失败：无法获取链接对象");break;
			case 6:setRes("找不到合适的验证方式，请求操作出现错误。");break;
			case 7:setRes("请求操作失败：提供的数据无法被初始化");break;
			case 9:setRes("检测到额外的不可替代的登陆操作，或者权限不足");break;
			default:setRes("Unknown Error");
			}	
			MainClass.saveUserData(this.name.getValue(), this.pwd.getPassword(), this.ip.getValue(), adminName.getValue(), adminPswd.getPassword());
		}
	}
	
	private void setRes(String r){
		DataFrame.showTips(r);
		Log.log(r);
	}
	
}
