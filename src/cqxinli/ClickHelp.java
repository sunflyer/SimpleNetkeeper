package cqxinli;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextArea;

public class ClickHelp implements ActionListener{

	protected FormPanel ip;
	protected FormPanel admin;
	protected PasswordPanel pwd;
	
	public ClickHelp(FormPanel ip,FormPanel admin,PasswordPanel pwd){
		this.ip=ip;
		this.admin=admin;
		this.pwd=pwd;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		new HelpFrame("帮助");		
	}
	
	@SuppressWarnings("serial")
	class HelpFrame extends JFrame{
		public HelpFrame(String name){
			super(name);
			HelpPre();
		}
		
		public HelpFrame(){
			super();
			HelpPre();
		}
		
		private void HelpPre(){
			Toolkit tk=Toolkit.getDefaultToolkit();
			Dimension dm=tk.getScreenSize();
			this.setResizable(false);
			this.setLocation(dm.width/2-250,dm.height/2-275);
			JTextArea jta=new JTextArea();
			this.setLayout(new BorderLayout());		
			jta.setEditable(false);
			jta.setLineWrap(true);
			//由于String 的拼接操作非常缓慢，因此更换为StringBuilder处理信息
			StringBuilder sb=new StringBuilder();
			sb.append("欢迎使用Netkeeper Dialer For Router 版本"+MainClass.getVersion()+"\n");
			sb.append("这个小JAVA程序可以方便的一键设置路由器拨号。适用于重庆地区校园宽带用户。\n\n");
			sb.append("已在TP-LINK最新固件，以及水星(Mercury)路由器测试通过，其他路由器请自行测试。理论可用。(D-LINK貌似有一点区别，请自行测试，不可用请联系我方便做出修正)\n\n");
			sb.append("重要提示：如果路由器断电，请尝试重新启动路由器，然后路由器通电3分钟后再进行连接。由于断电重连的原因路由器通电后的首次拨号时间会比较长，请耐心等待\n\n");
			sb.append("使用方法：\n用户名和密码输入你的宽带账号和密码\n");
			sb.append("路由器IP一行请输入你的路由器的IP地址（默认为192.168.0.1或者192.168.1.1）\n");
			sb.append("路由器用户名一般默认为admin，密码默认为admin，这两个内容可以在路由器机身下方的标签上找到。\n");
			sb.append("对于恢复了路由器（恢复出厂设置）的用户，直接点击“默认”，确认信息无误后即可\n");
			sb.append("点击“设置路由器”，程序会开始尝试为你设置路由器相关内容。如果提示成功操作，请耐心等候1分钟左右，因为路由器需要拨号操作。如果一分钟后网络没有联通，则可能意味着这个程序不适用于该产品。\n\n");
			sb.append("如果你只需要加密后的用户名，请点击“生成”\n\n");
			sb.append("鸣谢：重庆邮电大学的学长，是他们的付出逆向得到了Netkeeper的加密算法。\n");
			sb.append("源代码可以在 https://github.com/sunflyer/NetkeeperForRouter 找到\n\n");
			sb.append("CopyLeft 2014 CrazyChen@CQUT   电子邮件：cx@itncre.com\n\n");
			sb.append("警告：本软件关于账号加密的全部内容均来自互联网，仅作学习交流之用，本人并不从事任何反向工程。由此软件引发的任何后果由使用者本人承担，制作者概不负责。");
			sb.append("本软件作为免费软件，不得销售此软件以及此软件的修改或/和衍生版本，否则追究责任。");
			jta.setText(sb.toString());
			add(jta,BorderLayout.NORTH);
			JButton debug=new JButton("Debug");
			debug.addActionListener(new ClickDebug(ip,admin,pwd));
			if(MainClass.getVersionSig()==MainClass.VER_DEBUG || MainClass.getVersionSig()==MainClass.VER_BETA || MainClass.isDebugAllow()) {
				debug.setEnabled(true);
				debug.setText("Debug(点击后将尝试获取连接方式并输出信息到LOG)");
			}else{
				debug.setEnabled(false);
				debug.setText("Debug(仅在调试/测试版本或高级模式下开放)");
			}				
			add(debug,BorderLayout.SOUTH);
			this.setSize(650, 545);
			this.setVisible(true);
		}
	}

}
