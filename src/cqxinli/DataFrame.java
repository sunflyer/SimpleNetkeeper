package cqxinli;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class DataFrame extends JFrame{
	
	private static JLabel Tips=new JLabel("准备就绪");
	
	public DataFrame(String name){
		super(name);
		NewFrame();
	}
	
	private void NewFrame(){
		int width=400,height=330;
		this.setSize(width, height);
		Toolkit tk=Toolkit.getDefaultToolkit();
		Dimension ss=tk.getScreenSize();
		this.setLocation(ss.width/2-width/2, ss.height/2-height/2);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setResizable(false);
		addPanel();
		this.setVisible(true);
	}
	
	private void addPanel(){
		this.setLayout(new GridLayout(9,1));
		FormPanel name=new FormPanel("输入您的用户名");
		add(name);
		FormPanel realname=new FormPanel("加密后的用户名","",false);
		add(realname);
		FormPanel encodedRealname=new FormPanel("URL编码后的用户名","",false);
		add(encodedRealname);
		PasswordPanel password=new PasswordPanel("您的密码");
		add(password);
		FormPanel ip=new FormPanel("路由器IP地址(默认192.168.1.1)");
		add(ip);
		FormPanel adminName=new FormPanel("路由器管理员用户名","admin",true);
		add(adminName);
		PasswordPanel adminPassword=new PasswordPanel("路由器管理员密码");
		add(adminPassword);
		//提示
		JPanel jp2=new JPanel();
		add(jp2);
		jp2.add(Tips);
		//按钮
		JPanel jp3=new JPanel();
		//JButton dial=new JButton("本机连接");
		//dial.addActionListener(new ClickDial(name,password));
		JButton gen=new JButton("生成");
		gen.addActionListener(new ClickGen(name,realname,encodedRealname));
		JButton set=new JButton("设置路由器");
		//用户名，密码，IP,路由器管理员名称，管理员密码
		set.addActionListener(new ClickSet(name,password,ip,adminName,adminPassword));
		JButton def=new JButton("默认");
		def.addActionListener(new ClickDefault(ip,adminName,adminPassword));
		JButton help=new JButton("帮助");
		help.addActionListener(new ClickHelp());
		//jp3.add(dial);
		jp3.add(gen);
		jp3.add(set);
		jp3.add(def);
		jp3.add(help);
		this.add(jp3);
	}
	
	public static void showTips(String info){
		Tips.setText(info);
	}
}
