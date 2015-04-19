package cn.sunflyer.simpnk.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.border.Border;

import cn.sunflyer.simpnk.control.*;

public class FrameRouter extends JFrame{

	private static final long serialVersionUID = 1038403669 ;
	
	private JLabel gInfo = null;
	
	public FrameRouter(){
		this(AppInfo.APP_NAME);
	}
	
	public FrameRouter(String title){
		super(title);
		this.initFrame();
		this.initComponent();
		this.setVisible(true);
	}
	
	/**
	 * 初始化窗体基本设置
	 * */
	private void initFrame(){
		this.setSize(400, 400);
		this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		UIController.centerScreen(this);
		this.setResizable(false);
		this.setBackground(Color.WHITE);
		this.addWindowListener(new WindowListener(){
			@Override
			public void windowOpened(WindowEvent e) {}
			@Override
			public void windowClosing(WindowEvent e) {
				closeWindow();
			}
			@Override
			public void windowClosed(WindowEvent e) {}
			@Override
			public void windowIconified(WindowEvent e) {}
			@Override
			public void windowDeiconified(WindowEvent e) {}
			@Override
			public void windowActivated(WindowEvent e) {}
			@Override
			public void windowDeactivated(WindowEvent e) {}
			
		});
		Log.log("----设置路由器窗体基本变量完毕");
	}
	
	/**
	 * 初始化窗体组件库
	 * */
	private void initComponent(){
		//水平菜单
		this.setLayout(new BorderLayout());
		JMenuBar pMenu = this.initComponentMenu();
		this.add(pMenu,BorderLayout.NORTH);
		//切换面板
		JTabbedPane pTab = new JTabbedPane();
		this.add(pTab);
		
		pTab.add(this.initComponentRouter(),"路由器");
		pTab.add(new PanelDialLocal(this.getWidth(),this.getHeight()),"拨号/算号");
		pTab.add(new PanelOperation(),"设置/工具");
		
		Log.log("----设置路由器窗体基本组件完毕");
	}
	
	/**
	 * 初始化水平菜单栏
	 * */
	private JMenuBar initComponentMenu(){
		JMenuBar pMenu = new JMenuBar();
		
		JMenu[] pMenuList = new JMenu[3];
		String[] pMenuListLabel = new String[]{"程序","配置","帮助"};
		for(int i=0;i<pMenuList.length;i++){
			pMenuList[i] = new JMenu(pMenuListLabel[i]);
			pMenu.add(pMenuList[i]);
		}
		
		JMenuItem[] pApplication = new JMenuItem[1];
		String[] pApplicationLabel = new String[]{"退出(&X)"};
		ActionListener[] pApplicationEvent = new ActionListener[]{
			new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e) {
					closeWindow();					
				}
				
			}	
		};
		
		addToMenu(pApplication,pApplicationLabel,pApplicationEvent,pMenuList[0]);
		
		JMenuItem[] pConfig = new JMenuItem[6];
		String[] pConfigLabel = new String[]{"路由器设置向导(&R)","-","清除配置文件(&C)","保存配置文件(&S)","设置路由器配置至默认(&D)","-","查看日志记录(&V)","清除日志记录(&L)"};
		ActionListener[] pConfigEvent = new ActionListener[]{
			new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e) {
					//显示设置向导
					UIController.showGuide(FrameRouter.this);
				}				
			},
			new ActionListener(){
				//清除配置文件
				@Override
				public void actionPerformed(ActionEvent e) {
					if(JOptionPane.showConfirmDialog(FrameRouter.this, "你确定要清空你的配置文件？\n这个操作将清除所有已保存的配置内容，包括路由器连接信息以及你的账号信息！","警告",JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION){
						JOptionPane.showMessageDialog(FrameRouter.this, (ConfigController.clearConfig()?"已清除你的配置文件，请重启程序运行。":"清除配置文件时出现错误。"));
					}
				}
				
			},
			new ActionListener(){
				//保存配置文件
				@Override
				public void actionPerformed(ActionEvent e) {
					JOptionPane.showMessageDialog(FrameRouter.this, (ConfigController.saveConfig()?"已保存你的配置文件。":"保存配置文件时出现错误。"));					
				}
				
			},
			new ActionListener(){
				//回复配置文件到默认
				@Override
				public void actionPerformed(ActionEvent e) {
					
				}
				
			},
			new ActionListener(){
				//查看日志
				@Override
				public void actionPerformed(ActionEvent e) {
					Log.showLog();
				}
				
			},
			new ActionListener(){
				//清除日志
				@Override
				public void actionPerformed(ActionEvent e) {
					if(UIController.showConfirm(FrameRouter.this, "日志清除", "你确定要清除程序日志吗？")){
						UIController.showMessage("日志操作", (Log.clearLog() ? "清除日志完毕。" : "清除日志发生错误"));
					}
				}
				
			}
		};
		
		addToMenu(pConfig, pConfigLabel, pConfigEvent, pMenuList[1]);
		
		JMenuItem [] pHelp = new JMenuItem[5];
		String[] pHelpLabel = new String[]{"帮助主题(&H)","开发人员文档(&E)","-","转到我的主页(&M)","检查更新(&U)","关于软件(&A)"};
		ActionListener [] pHelpEvent = new ActionListener[]{
				new WebSiteOpen(AppInfo.APP_WEB_HELP),
				new WebSiteOpen(AppInfo.APP_WEB_DEV),
				new WebSiteOpen(AppInfo.APP_AUTHOR_WEB),
				new WebSiteOpen(AppInfo.APP_WEB_UPDATE),
				new ActionListener(){
					@Override
					public void actionPerformed(ActionEvent e) {
						UIController.showMessage("关于"+AppInfo.APP_NAME, "版权所有，2014-2015 Authored By "+AppInfo.APP_AUTHOR_NAME+"\n保留所有权利\n"+AppInfo.APP_AUTHOR_DES+"\n\n请注意：\n使用本程序或者使用本程序源代码创建任何衍生程序均需要在遵守非商业用途的条件，\n即同意如果在非商业用途下使用软件/软件源代码/衍生软件，原作者\n"+AppInfo.APP_AUTHOR_NAME+"有权利要求停止这一行为。否则无权使用。\n\n当前版本："+AppInfo.getVersionStringWithRel());
					}					
				},
		};
		
		addToMenu(pHelp,pHelpLabel,pHelpEvent,pMenuList[2]);
		
		return pMenu;
	}
	
	/**
	 * 将菜单项添加到选单<br>
	 * 请保证数组长度一致，否则无法继续操作
	 * @param list 菜单列表项数组
	 * @param label 菜单列表标识符
	 * @param event 为每个对象创建的事件
	 * @param parent 父菜单
	 * */
	private void addToMenu(JMenuItem[] list,String[] label,ActionListener[] event,JMenu parent){
		if(list == null || label == null || list.length == 0 || label.length == 0 || parent == null) return;
		int j = 0 , i = 0;
		while(i<list.length){
			if(label[j].equals("-")){
				parent.addSeparator();
			}else{
				list[i] = new JMenuItem(label[j]);
				list[i].addActionListener(event[i]);				
				parent.add(list[i]);
				i++;
			}
			j++;
		}
	}
	
	/**
	 * 初始化路由器拨号面板
	 * */
	private JPanel initComponentRouter(){
		JPanel pRouter = new JPanel();
		pRouter.setLayout(null);
		//宽带账号部分
		Border pTitle = BorderFactory.createTitledBorder("宽带信息");
		JPanel pRouterAcc = new JPanel();
		pRouterAcc.setLayout(new GridLayout(2,1));
		pRouterAcc.setBorder(pTitle);
		pRouterAcc.setSize(this.getWidth(), 100);
		pRouterAcc.setBounds(0, 0, pRouterAcc.getWidth(), pRouterAcc.getHeight());
		//================================
		final PanelInput pValAcc = new PanelInput("宽带账号","",false,this.getWidth(),45);
		final PanelPassword pValPass = new PanelPassword("宽带密码","",false,this.getWidth(),45);
		
		pValAcc.addFocusListenerForBox(new FocusListener(){
			public void focusGained(FocusEvent arg0) {}

			@Override
			public void focusLost(FocusEvent arg0) {
				StatusController.sAccName = pValAcc.getText();
			}
		});
		
		pValPass.addFocusListenerForBox(new FocusListener(){
			public void focusGained(FocusEvent arg0) {}

			@Override
			public void focusLost(FocusEvent arg0) {
				StatusController.sAccPassword = pValPass.getPassword();
			}
		});
		
			//设置帮助
			pValAcc.setInputHelp("请在这里输入你的宽带账号");
			pValPass.setInputHelp("请在这里输入你的宽带密码");
		
			//设置初始值
			pValAcc.setText(StatusController.sAccName);
			pValPass.setPassword(StatusController.sAccPassword);
			
			//将组件添加
			pRouterAcc.add(pValAcc);
			pRouterAcc.add(pValPass);
			
		//路由器部分
		Border pTitleRouter = BorderFactory.createTitledBorder("路由器信息");
		JPanel pRouterAdmin = new JPanel();
		pRouterAdmin.setBorder(pTitleRouter);
		pRouterAdmin.setLayout(new GridLayout(3,1));
		pRouterAdmin.setSize(this.getWidth(), 45 * 3);
		pRouterAdmin.setBounds(0 , 105 , pRouterAdmin.getWidth(),pRouterAdmin.getHeight());
		//================================
		final PanelInput pValRouterIP = new PanelInput("IP地址","192.168.1.1",false,this.getWidth(),45);
		final PanelInput pValRouterAdmin = new PanelInput("管理员名称","admin",false,this.getWidth(),45);
		final PanelPassword pValRouterPass = new PanelPassword("管理员密码","",false,this.getWidth(),45);
			
			//设置监听器
			pValRouterIP.addFocusListenerForBox(new FocusListener(){
				public void focusGained(FocusEvent arg0) {}

				@Override
				public void focusLost(FocusEvent arg0) {
					StatusController.sRouterAdd = pValRouterIP.getText();
				}
				
			});
			
			pValRouterAdmin.addFocusListenerForBox(new FocusListener(){
				public void focusGained(FocusEvent arg0) {}

				@Override
				public void focusLost(FocusEvent arg0) {
					StatusController.sRouterAcc = pValRouterAdmin.getText();
				}
			});
		
			pValRouterPass.addFocusListenerForBox(new FocusListener(){
				public void focusGained(FocusEvent arg0) {}

				@Override
				public void focusLost(FocusEvent arg0) {
					StatusController.sRouterPassword = pValRouterPass.getPassword();
				}
			});
		
			//设置帮助
			pValRouterIP.setInputHelp("请在这里输入你的路由器配置地址，默认为192.168.1.1");
			pValRouterAdmin.setInputHelp("请输入管理员账户名称，如果不知道请保持默认 admin");
			pValRouterPass.setInputHelp("请输入管理员密码，如果不知道请输入 admin");
			
			//设置默认值
			pValRouterIP.setText(StatusController.sRouterAdd);
			pValRouterAdmin.setText(StatusController.sRouterAcc);
			pValRouterPass.setPassword(StatusController.sRouterPassword);
			
			//添加组件
			pRouterAdmin.add(pValRouterIP);
			pRouterAdmin.add(pValRouterAdmin);
			pRouterAdmin.add(pValRouterPass);
			
		//按钮部分
		JPanel pRouterOpr = new JPanel();
		pRouterOpr.setLayout(null);
		pRouterOpr.setSize(this.getWidth(), 90);
		pRouterOpr.setBounds(50 , 45 * 5 + 20 , pRouterOpr.getWidth() - 50, pRouterOpr.getHeight());
		//===============================
		gInfo = new JLabel("Ready.");
		gInfo.setForeground(Color.RED);
			
			//添加到面板
			gInfo.setBounds(0 , 0 , pRouterOpr.getWidth(), 35);
			StatusController.setComponentRouterConfigStatusBar(gInfo);
		
		JButton pButSet = new JButton("立即设置！");
		pButSet.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {					
				if(StatusController.getStateRouterDial()){
					JOptionPane.showMessageDialog(FrameRouter.this, "正在设置路由器中，请不要重复点击！");
				}else{
					Log.log("开始尝试设置路由器。。。");
					StatusController.setStateRouterDial(true);
					DialController.dialRouter();
				}
			}
		});
		
			//帮助信息
				pButSet.setToolTipText("点击这里，在信息无误的情况下程序会帮你设置路由器拨号");
				pButSet.setBounds(0 , 35, 120, 30);
			//添加组件
				pRouterOpr.add(gInfo);
				pRouterOpr.add(pButSet);
		
		//最后添加
		pRouter.add(pRouterAcc);
		pRouter.add(pRouterAdmin);
		pRouter.add(pRouterOpr);
		return pRouter;
	}
	
	private void closeWindow(){
		if(checkCloseWindow()){
			JOptionPane.showMessageDialog(this, "现在暂时不能关闭程序，目前程序仍然还在处理拨号任务，或者心跳任务。" , "关闭提示" ,JOptionPane.YES_NO_OPTION);
		}else{
			if(JOptionPane.showConfirmDialog(this, "确认要退出吗？","关闭提示",JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION){
				ConfigController.saveConfig();
				System.exit(0);
			}
				
		}
	}
	
	private static boolean checkCloseWindow(){
		return StatusController.getStateRouterDial() || StatusController.getStateDial() || StatusController.getStateHeartBeat();
	}
	
	/**
	 * 用于响应点击事件以后打开网站
	 * */
	class WebSiteOpen implements ActionListener{

		private String uri ;
		
		public WebSiteOpen(String uri){
			this.uri = uri;
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				Desktop.getDesktop().browse(new URI(uri));
			} catch (IOException e1) {
				Log.logE(e1);
			} catch (URISyntaxException e1) {
				Log.log("网站语法错误：不是一个正确的网址");
			}
		}
		
	}
}
