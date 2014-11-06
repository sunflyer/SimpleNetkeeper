package cqxinli;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.UnsupportedEncodingException;

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
		String name=MainClass.getDialFrame().getAccName();
		final String pwd=MainClass.getDialFrame().getAccPassword();
		Dial(name,pwd,true);
	}
	
	public synchronized void Dial(String name,final String pwd,boolean enc){
		if(pwd.length()<6 || name.equals("")){
			this.setInfo("用户名或密码不符合要求");
			if(this.gBut!=null)
				this.gBut.setEnabled(true);
		}
		else{
			if(this.gBut!=null) this.gBut.setEnabled(false);
			if(MainClass.getDialFrame().isRememberAcc()){
				MainClass.saveUserData();
			}
			//真是用户
			final String Realname=enc?new CXKUsername(name).Realusername():name;
			//开始拨号
			MainClass.getDialFrame().setConnectionState("开始拨号操作，请稍后");
			//拨号操作
			new Thread(new Runnable(){

				@Override
				public void run() {
					setRes((int)dialRasWindows(Realname, pwd));	
					if(gBut!=null)
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
		case 0:this.setInfo("连接成功！");break;
		case 625:this.setInfo("连接发生内部错误（625）");break;
		case 629:this.setInfo("连接被远程计算机关闭（629）");break;
		case 651:this.setInfo("调制解调器或其他设备无法连接（651）");break;
		case 678:this.setInfo("远程计算机没有响应（678）");break;
		case 691:this.setInfo("用户凭据无法访问网络（691）");break;
		case 711:this.setInfo("请检查拨号相关服务是否已经启动！（711）");break;
		case 720:this.setInfo("不能建立连接。你可能需要更改设置（720）");break;
		case 813:this.setInfo("你已经有一个活动的连接了！(813)");break;
		case 815:this.setInfo("远程计算机没有响应(815)");
		default:this.setInfo("不可用的服务状态，代码："+this.getRes());
		}
		System.out.println(this.getRes());
		MainClass.getDialFrame().allowWifi(this.getRes()==0);
		try {
			Log.log(this.getRes()+":"+new String(this.dialRasWindowsErrorStr(getRes()).getBytes(),"GB2312"));
		} catch (UnsupportedEncodingException e) {
			Log.logE(e);
		}
	}
	
	private native long dialRasWindows(String username,String password);

	private native String dialRasWindowsErrorStr(long error);
}
