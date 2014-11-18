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
import javax.swing.JTabbedPane;

@SuppressWarnings("serial")
public class DataFrame extends JFrame{
	
	private static JLabel Tips=new JLabel("准备就绪，请输入必要信息后，直接点击“设置”开始设置路由器");
	private FormPanel name=null;
	private PasswordPanel password=null;
	private FormPanel ip=null;
	private FormPanel adminName=null;
	private PasswordPanel adminPassword=null;
	
	public void setAccName(String accName){
		this.name.setValue(accName);
	}
	
	public void setAccPassword(String password){
		this.password.setPassword(password);
	}
	
	public void setRouterIpAddress(String ip){
		this.ip.setValue((ip==null || ip.trim().equals(""))?"192.168.1.1":ip);
	}
	
	public void setRouterAccName(String name){
		this.adminName.setValue((name==null || name.trim().equals(""))?"admin":name);
	}
	
	public void setRouterAccPassword(String password){
		this.adminPassword.setPassword(password);
	}
	
	public String g_getAccName(){
		return name.getValue();
	}
	
	public String g_getAccPassword(){
		return this.password.getPassword();
	}
	
	public String g_getRouterIP(){
		return this.ip.getValue().trim().equals("")?"192.168.1.1":this.ip.getValue();
	}
	
	public String g_getRouterAdmin(){
		return this.adminName.getValue().trim().equals("")?"admin":this.adminName.getValue();
	}
	
	public String g_getRouterPassword(){
		return this.adminPassword.getPassword();
	}
	
	public DataFrame(String name){
		super(name);
		NewFrame();
	}
	
	private void NewFrame(){
		int width=400,height=400;
		this.setSize(width, height);
		Toolkit tk=Toolkit.getDefaultToolkit();
		Dimension ss=tk.getScreenSize();
		this.setLocation(ss.width/2-width/2, ss.height/2-height/2);
		this.setBackground(Color.WHITE);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setResizable(false);
		addPanel();	
	}
	
	private void addPanel(){
		JTabbedPane pTPSwitchPanel = new JTabbedPane();
		
		
		JPanel pPanelRouter = new JPanel();
		pPanelRouter.setLayout(new GridLayout(10,1));
		JLabel m_lab_accinfo=new JLabel("校园宽带账号信息");
		pPanelRouter.add(m_lab_accinfo);
		name=new FormPanel("输入您的用户名");
		name.setTooltipData("请在这里输入你的校园/家庭宽带账号名称\n例如 111111111@cqxxx");
		pPanelRouter.add(name);
		password=new PasswordPanel("您的密码");
		password.setTooltipData("请在这里输入你的校园/家庭宽带账号密码");
		pPanelRouter.add(password);
		JLabel m_lab_routerinfo=new JLabel("路由器管理员信息，可在路由器机身下方标签找到");
		pPanelRouter.add(m_lab_routerinfo);
		ip=new FormPanel("路由器IP地址(默认192.168.1.1)");
		ip.setValue("192.168.1.1");
		ip.setTooltipData("请输入你的路由器的IP地址！");
		pPanelRouter.add(ip);
		adminName=new FormPanel("路由器管理员用户名","admin",true);
		adminName.setTooltipData("输入你的路由器的管理员名称，如果你的路由器登陆只要求密码，请输入“admin”");
		pPanelRouter.add(adminName);
		adminPassword=new PasswordPanel("路由器管理员密码");
		adminPassword.setTooltipData("请在这里输入你的路由器管理员的密码。");
		pPanelRouter.add(adminPassword);
		//提示
		JPanel jp2=new JPanel();
		jp2.setBackground(Color.WHITE);
		jp2.setLayout(new GridLayout(1,1));
		pPanelRouter.add(jp2);
		Tips.setForeground(Color.RED);
		jp2.add(Tips);
		//按钮
		JPanel jp3=new JPanel();		
		//JButton dial=new JButton("本机连接");
		//dial.addActionListener(new ClickDial(name,password));
		
		JButton pButSet=new JButton("设置路由器");
		pButSet.setToolTipText("点击以后，程序将开始尝试使用你提供的数据执行链接操作。");
		//用户名，密码，IP,路由器管理员名称，管理员密码
		pButSet.addActionListener(new ClickSet(pButSet));
		JButton pButDef=new JButton("默认");
		pButDef.setToolTipText("这个操作会将路由器信息全部重置。IP重置为192.168.1.1，管理员名称admin，管理员密码admin。适用于重置路由器后使用。");
		pButDef.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				MainClass.getDataFrame().setRouterAccName("admin");
				MainClass.getDataFrame().setRouterIpAddress("192.168.1.1");
				MainClass.getDataFrame().setRouterAccPassword("admin");
			}
			
		});
		
		JButton pButSave = new JButton("保存");
		pButSave.setToolTipText("保存你已经输入的信息");
		pButSave.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				MainClass.saveUserData();
			}
			
		});

		//jp3.add(dial);		
		jp3.add(pButDef);
		jp3.add(pButSet);
		jp3.add(pButSave);
		pPanelRouter.add(jp3);
		JPanel pVerPanel=new JPanel();
		pVerPanel.setLayout(new BorderLayout());
		JLabel pLabVer=new JLabel("版本"+MainClass.getVersionNoBuild()+" by CrazyChen@CQUT");
		pVerPanel.add(pLabVer,BorderLayout.WEST);
		
		JButton pButBackMenu=new JButton("返回菜单");
		pButBackMenu.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				MainClass.getMenuFrame().setVisible(true);
				MainClass.getDataFrame().setVisible(false);
			}
			
		});
		pVerPanel.add(pButBackMenu,BorderLayout.EAST);
		
		pPanelRouter.add(pVerPanel);		
				
		pTPSwitchPanel.add(pPanelRouter,"路由器设置");
		pTPSwitchPanel.add(new RealUserFrame(),"账号计算");
		pTPSwitchPanel.add(new AdvancePanel(),"高级选项");
		pTPSwitchPanel.add(new HelpInfoPanel(),"关于软件");
		add(pTPSwitchPanel);
		
		Log.log("已经完成界面载入操作");
	}
	
	public static void showTips(String info){
		Tips.setText(info);
	}
}
