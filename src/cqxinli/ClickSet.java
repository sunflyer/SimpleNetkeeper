package cqxinli;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class ClickSet implements ActionListener{

	private FormPanel name;
	private PasswordPanel pwd;
	private FormPanel ip;
	private FormPanel adminName;
	private PasswordPanel adminPswd;
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
		Router rt=new Router(this.ip.getValue(),this.adminName.getValue(),this.adminPswd.getPassword(),this.name.getValue(),this.pwd.getPassword());
		switch(rt.connect()){
		case -2:DataFrame.showTips("尝试连接路由器时发生问题，无法登陆验证");break;
		case -1:DataFrame.showTips("尝试对用户名和密码编码时出现问题");break;
		case 0:DataFrame.showTips("已成功进行连接操作，请在30秒后检查网络连接");break;
		case 1:DataFrame.showTips("给定的路由器地址不合法。");break;
		case 3:DataFrame.showTips("尝试操作时发生错误，没有足够的权限");break;
		case 4:DataFrame.showTips("IOException Occured");break;
		case 9:DataFrame.showTips("检测到额外的不可替代的登陆操作，或者权限不足");break;
		default:DataFrame.showTips("Unknown Error");
		}
	}
	
}
