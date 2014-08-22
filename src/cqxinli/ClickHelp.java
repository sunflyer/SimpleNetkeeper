package cqxinli;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JTextArea;

public class ClickHelp implements ActionListener{

	@Override
	public void actionPerformed(ActionEvent e) {
		new HelpFrame();		
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
			this.setVisible(true);
			this.setSize(500, 550);
			Toolkit tk=Toolkit.getDefaultToolkit();
			Dimension dm=tk.getScreenSize();
			this.setResizable(false);
			this.setLocation(dm.width/2-250,dm.height/2-275);
			JTextArea jta=new JTextArea();
			this.setLayout(new BorderLayout());
			add(jta,BorderLayout.NORTH);
			jta.setEditable(false);
			jta.setLineWrap(true);
			String data="欢迎使用Netkeeper Dialer For Router 版本1.0(Build 012.20140820)\n\n"
					+ "这个小JAVA程序可以方便的一键设置路由器拨号。适用于重庆地区校园宽带用户。\n\n"
					+ "已在TP-LINK最新固件，以及水星(Mercury)路由器测试通过，其他路由器请自行测试。理论可用。\n\n"
					+ "使用方法：\n用户名和密码输入你的宽带账号和密码\n"
					+ "路由器IP一行请输入你的路由器的IP地址（默认为192.168.0.1或者192.168.1.1）\n"
					+ "路由器用户名一般默认为admin，密码默认为admin，这两个内容可以在路由器机身下方的标签上找到。\n"
					+ "对于恢复了路由器（恢复出厂设置）的用户，直接点击“默认”，确认信息无误后即可\n"
					+ "点击“设置路由器”，程序会开始尝试为你设置路由器相关内容。如果提示成功操作，请耐心等候1分钟左右，因为路由器需要拨号操作。如果一分钟后网络没有联通，则可能意味着这个程序不适用于该产品。\n\n"
					+ "如果你只需要加密后的用户名，请点击“生成”\n\n"
					+ "鸣谢：重庆邮电大学的学长，是他们的付出逆向得到了Netkeeper的加密算法。\n\n"
					+ "源代码可以在 http://i.itncre.com/redirect?tar=github 找到（链接暂未开通）（当然你也可以反编译啦）\n\n"
					+ "CopyLeft 2014 CrazyChen@重庆理工大学   电子邮件：cx@itncre.com\n\n"
					+ "警告：本软件关于账号加密的全部内容均来自互联网，仅作学习交流之用，本人并不从事任何反向工程。由此软件引发的任何后果由使用者本人承担，制作者概不负责。"
					+ "本软件作为免费软件，不得销售此软件以及此软件的修改或/和衍生版本，否则追究责任。";
			jta.setText(data);
			
		}
	}

}
