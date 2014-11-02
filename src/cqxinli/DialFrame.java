package cqxinli;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

public class DialFrame extends JFrame{
	
	private FormPanel gFPUser;
	private PasswordPanel gPPPassword;
	
	private JCheckBox pChkRem;
	private JCheckBox pChkAutoDial;
	private JCheckBox pChkHeartBeat;
	//获取账号和密码
	public String getAccName(){
		return this.gFPUser.getValue();
	}
	
	public String getAccPassword(){
		return this.gPPPassword.getPassword();
	}
	
	public boolean isRememberAcc(){
		return pChkRem.isSelected();
	}
	
	public boolean isAutoDial(){
		return this.pChkAutoDial.isSelected();
	}
	
	public boolean isHeartBeat(){
		return this.pChkHeartBeat.isSelected();
	}
	
	//无限面板项目
	
	private FormPanel gSSID;
	private PasswordPanel gPPWifiPassword;
	//获取WIFI信息
	public String getSSID(){
		return this.gSSID.getValue();
	}
	
	public String getWifiKey(){
		return this.gPPWifiPassword.getPassword();
	}
		
	private JLabel gConState;
	private JLabel gWifiState;
		
	public void setConnectionState(CharSequence c){
		Log.log("拨号状态："+c);
		this.gConState.setText(c.toString());
	}
	
	//设置项,仅初始化
	public void setConfigDataDial(String name,String password,boolean isHeartBeat,boolean isRem,boolean isAutoDial){
		this.gFPUser.setValue(name);
		this.gPPPassword.setPassword(password);
		this.pChkAutoDial.setSelected(isAutoDial);
		this.pChkHeartBeat.setSelected(isHeartBeat);
		this.pChkRem.setSelected(isRem);
	}
	
	public void setConfigDataWifi(String SSID,String password){
		this.gSSID.setValue(SSID);
		this.gPPWifiPassword.setPassword(password);
	}
	
	
	
	
	private boolean isDialed;
	/**
	 * 
	 */
	private static final long serialVersionUID = 2828127360103736288L;

	public DialFrame(String m) {
		super(m);
		int width=400,height=200;
		this.setSize(width, height);
		Toolkit tk=Toolkit.getDefaultToolkit();
		Dimension ss=tk.getScreenSize();
		this.setLocation(ss.width/2-width/2, ss.height/2-height/2);
		this.setBackground(Color.WHITE);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setResizable(false);
		add();
	}
	
	private void add(){
		this.setLayout(new BorderLayout());
		
		//选项卡页面
		JTabbedPane pTPDialPanel=new JTabbedPane();
		
		JPanel pTPDial=new JPanel();
		pTPDial.setLayout(new GridLayout(5,1));
		
		this.gFPUser=new FormPanel("校园宽带账号","请输入你的校园宽带账号",true);
		this.gPPPassword=new PasswordPanel("校园宽带密码");
		this.gSSID=new FormPanel("无线热点名称","设置你的无线热点名称",false);
		this.gPPWifiPassword=new PasswordPanel("设置你的无线热点的密码",false);
		pTPDial.add(this.gFPUser);
		pTPDial.add(this.gPPPassword);
		JPanel pPanCheck1=new JPanel();
		pChkRem=new JCheckBox("记住我的账户");
		
		pChkAutoDial=new JCheckBox("开机时自动登录");
		pChkAutoDial.setEnabled(false);
		pPanCheck1.add(pChkRem);
		pPanCheck1.add(pChkAutoDial);
		
		pChkHeartBeat=new JCheckBox("心跳包模拟");
		pChkHeartBeat.setEnabled(false);
		pPanCheck1.add(pChkHeartBeat);
		
		pTPDial.add(pPanCheck1);
		
		this.gConState=new JLabel("准备就绪");
		this.gConState.setForeground(Color.RED);
		pTPDial.add(this.gConState);
		
		final JButton pButTestDial=new JButton("测试账号");
		pButTestDial.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				new ClickDial(pButTestDial).Dial("chongzhi@cqdx", "111111", false);			
			}
		});
				
		JPanel pPanButton = new JPanel();
		JButton pButDial=new JButton("立即拨号");
		pButDial.setForeground(Color.MAGENTA);
		pButDial.addActionListener(new ClickDial(pButDial));	
		
		JButton pButBackMenu=new JButton("返回菜单");
		pButBackMenu.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				MainClass.getMenuFrame().setVisible(true);
				MainClass.getDialFrame().setVisible(false);
			}
			
		});
		
		pPanButton.add(pButTestDial);
		pPanButton.add(pButDial);				
		pPanButton.add(pButBackMenu);
		
		pTPDial.add(pPanButton);
		pTPDialPanel.add(pTPDial,"拨号操作");
		
		
		JPanel pTPWifi=new JPanel(new GridLayout(4,1));
		pTPWifi.add(this.gSSID);
		pTPWifi.add(this.gPPWifiPassword);
		
		JPanel pPanConfig=new JPanel();		
		JButton pButWifi=new JButton("启动无线热点");
		pButWifi.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				if(isDialed){
					
				}
				
			}
			
		});
		
		pPanConfig.add(pButWifi);
		this.gWifiState=new JLabel("无线状态：Unknown");		
		pTPWifi.add(this.gWifiState);		
		pTPWifi.add(pPanConfig);
		
		pTPDialPanel.add(pTPWifi,"无线共享");
		
		add(pTPDialPanel);
	}

}
