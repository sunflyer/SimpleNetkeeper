package cqxinli;

import java.awt.BorderLayout;
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
	private FormPanel name=null;
	private PasswordPanel password=null;
	private FormPanel ip=null;
	private FormPanel adminName=null;
	private PasswordPanel adminPassword=null;
	
	public String g_getAccName(){
		return name.getValue();
	}
	
	public String g_getAccPassword(){
		return this.password.getPassword();
	}
	
	public String g_getRouterIP(){
		return this.ip.getValue();
	}
	
	public String g_getRouterAdmin(){
		return this.adminName.getValue();
	}
	
	public String g_getRouterPassword(){
		return this.adminPassword.getPassword();
	}
	
	public DataFrame(String name){
		super(name);
		NewFrame();
	}
	
	private void NewFrame(){
		int width=400,height=380;
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
		JLabel m_lab_accinfo=new JLabel("校园宽带账号信息");
		add(m_lab_accinfo);
		name=new FormPanel("输入您的用户名");
		add(name);
		password=new PasswordPanel("您的密码");
		add(password);
		JLabel m_lab_routerinfo=new JLabel("路由器管理员信息，可在路由器机身下方标签找到");
		add(m_lab_routerinfo);
		ip=new FormPanel("路由器IP地址(默认192.168.1.1)");
		add(ip);
		adminName=new FormPanel("路由器管理员用户名","admin",true);
		add(adminName);
		adminPassword=new PasswordPanel("路由器管理员密码");
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
		JButton pButGen=new JButton("生成");
		RealUserFrame pRuf=new RealUserFrame();
		pButGen.addActionListener(new ClickGen(name,pRuf,password,ip));
		JButton pButSet=new JButton("设置路由器");
		//用户名，密码，IP,路由器管理员名称，管理员密码
		pButSet.addActionListener(new ClickSet(pButSet,this));
		JButton pButDef=new JButton("默认");
		pButDef.addActionListener(new ClickDefault(ip,adminName,adminPassword));
		JButton pButHelp=new JButton("帮助");
		pButHelp.addActionListener(new ClickHelp(ip,adminName,adminPassword));
		JButton pButSave = new JButton("保存");
		pButSave.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				MainClass.saveUserData(name.getValue(), password.getPassword(), ip.getValue(), adminName.getValue(), adminPassword.getPassword());
			}
			
		});
		
		MainClass.setUserData(name, password, ip, adminName, adminPassword);
		
		JButton pButAdvance=new JButton("高级");
		final AdvanceFrame pAdvFrame=new AdvanceFrame(this);
		pButAdvance.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				pAdvFrame.setVisible(true);
			}
			
		});
		//jp3.add(dial);
		jp3.add(pButGen);		
		jp3.add(pButDef);
		jp3.add(pButSet);
		jp3.add(pButSave);
		jp3.add(pButAdvance);
		this.add(jp3);
		JPanel pVerPanel=new JPanel();
		pVerPanel.setLayout(new BorderLayout());
		JLabel pLabVer=new JLabel("版本"+MainClass.getVersionNoBuild()+" by CrazyChen@CQUT");
		pVerPanel.add(pLabVer,BorderLayout.WEST);
		pVerPanel.add(pButHelp,BorderLayout.EAST);
		add(pVerPanel);		
		Log.log("已经完成界面载入操作");
	}
	
	public static void showTips(String info){
		Tips.setText(info);
	}
}
