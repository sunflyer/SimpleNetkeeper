package cqxinli;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

public class ClickDial implements ActionListener{

	JButton gBut;
	private int dialRes;
	
	public synchronized void setRes(int R){this.dialRes=R;this.dialRes();}
	public int getRes(){return this.dialRes;}
	
	public ClickDial(JButton p){
		this.gBut=p;
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		this.gBut.setEnabled(false);
		String name=MainClass.getDialFrame().getAccName();
		final String pwd=MainClass.getDialFrame().getAccPassword();
		if(pwd.length()<6 || name.equals("")){
			this.setInfo("用户名或密码不符合要求");
		}
		else{
			
			//真是用户名
			CXKUsername un=new CXKUsername(name);
			final String Realname=un.Realusername();
			//开始拨号
			MainClass.getDialFrame().setConnectionState("开始拨号操作");
			//拨号操作
			new Thread(new Runnable(){

				@Override
				public void run() {
					setRes((int)dialRasWindows(Realname, pwd));	
					gBut.setEnabled(true);
					
				}
				
			}).start();
		}
		
	}
	
	private synchronized void setInfo(CharSequence c){
		MainClass.getDialFrame().setConnectionState(c);
	}
	
	private synchronized void dialRes(){
		switch(this.getRes()){
		case 623:
		case 624:
		case 625:this.setInfo("连接发生内部错误");break;
		case 629:this.setInfo("连接被远程计算机关闭");break;
		case 678:this.setInfo("远程计算机没有响应");break;
		case 691:this.setInfo("用户凭据无法访问网络");break;
		case 720:this.setInfo("不能建立连接。你可能需要更改设置");break;
		default:this.setInfo("不可用的服务状态");
		}
		Log.log(this.getRes()+":"+this.dialRasWindowsErrorStr(getRes()));
	}
	
	private native long dialRasWindows(String username,String password);

	private native String dialRasWindowsErrorStr(long error);
}
