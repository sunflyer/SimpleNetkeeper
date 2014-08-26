package cqxinli;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class DataFrame extends JFrame{
	
	private static JLabel Tips=new JLabel("准备就绪，请输入必要信息后，直接点击“设置”开始设置路由器");
	
	public DataFrame(String name){
		super(name);
		NewFrame();
	}
	
	private void NewFrame(){
		int width=400,height=360;
		this.setSize(width, height);
		Toolkit tk=Toolkit.getDefaultToolkit();
		Dimension ss=tk.getScreenSize();
		this.setLocation(ss.width/2-width/2, ss.height/2-height/2);
		this.setBackground(Color.WHITE);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setResizable(false);
		addPanel();
		this.setVisible(true);
	}
	
	private void addPanel(){
		this.setLayout(new GridLayout(10,1));
		final FormPanel name=new FormPanel("输入您的用户名");
		add(name);
		FormPanel realname=new FormPanel("加密后的用户名","",false);
		add(realname);
		FormPanel encodedRealname=new FormPanel("URL编码后的用户名","",false);
		add(encodedRealname);
		final PasswordPanel password=new PasswordPanel("您的密码");
		add(password);
		final FormPanel ip=new FormPanel("路由器IP地址(默认192.168.1.1)");
		add(ip);
		final FormPanel adminName=new FormPanel("路由器管理员用户名","admin",true);
		add(adminName);
		final PasswordPanel adminPassword=new PasswordPanel("路由器管理员密码");
		add(adminPassword);
		//提示
		JPanel jp2=new JPanel();
		jp2.setBackground(Color.WHITE);
		add(jp2);
		Tips.setForeground(Color.RED);
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
		JButton save = new JButton("保存");
		save.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				MainClass.saveUserData(name, password, ip, adminName, adminPassword);
			}
			
		});
		//jp3.add(dial);
		jp3.add(gen);		
		jp3.add(def);
		jp3.add(set);
		jp3.add(save);
		jp3.add(help);
		this.add(jp3);
		JLabel ver=new JLabel("版本"+MainClass.getVersionNoBuild()+" by CrazyChen@CQUT");
		add(ver);
		MainClass.setUserData(name, password, ip, adminName, adminPassword);
	}
	
	public static void showTips(String info){
		Tips.setText(info);
	}
}
