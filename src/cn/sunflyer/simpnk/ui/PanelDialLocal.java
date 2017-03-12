package cn.sunflyer.simpnk.ui;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import cqxinli.ClickDial;
import cn.sunflyer.simpnk.control.StatusController;
import cn.sunflyer.simpnk.netkeeper.CXKUsername;

/**
 * 路由器本地拨号界面
 * @author CrazyChen
 * */
public class PanelDialLocal extends JPanel{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1089909794048458208L;

	public PanelDialLocal(int width,int height){
		this.setLayout(null);
		this.setSize(width, height);
		this.setBounds(0, 0, width, height);
		JPanel pDial = this.initComponentDial();
		
		this.add(pDial);
		pDial.setBounds(0, 0, width, 180);
	}
	
	private JPanel initComponentDial(){
		JPanel pDial = new JPanel();
		pDial.setBorder(BorderFactory.createTitledBorder("本地拨号功能"));
		
		pDial.setLayout(new GridLayout(4,1));
		
		final PanelInput pLocalAcc = new PanelInput("宽带账号","",false,this.getWidth(),45);
		pLocalAcc.addFocusListenerForBox(new FocusListener(){
			@Override
			public void focusGained(FocusEvent arg0) {}
			@Override
			public void focusLost(FocusEvent arg0) {
				StatusController.sLocalAccName = pLocalAcc.getText();
			}
			
		});
		final PanelPassword pLocalPass = new PanelPassword("宽带密码","",false,this.getWidth(),45);
		pLocalPass.addFocusListenerForBox(new FocusListener(){
			@Override
			public void focusGained(FocusEvent arg0) {}
			@Override
			public void focusLost(FocusEvent arg0) {
				StatusController.sLocalAccPassword = pLocalPass.getPassword();
			}
			
		});

		//设置默认值
		pLocalAcc.setText(StatusController.sLocalAccName);
		pLocalPass.setPassword(StatusController.sLocalAccPassword);

		final JLabel pDialState = new JLabel("Ready");
		pDialState.setForeground(Color.RED);
		StatusController.setComponentDialStatusBar(pDialState);

		if (System.getProperty("os.name").startsWith("Windows")) {
			String libPath = System.getProperty("user.dir") + File.separator + "DialLib" + File.separator;
			try {
				System.load(libPath + "SimpNKRasDialLibx64.dll");
			} catch (UnsatisfiedLinkError e1) {
				try {
					System.load(libPath + "SimpNKRasDialLibx86.dll");
				} catch (UnsatisfiedLinkError e2) {
					pDialState.setText("DLL加载失败");
				}
			}
		} else {
			pDialState.setText("仅Windows可用");
		}
		
		JPanel pDialOpr = new JPanel();
		
		JButton pConnect = new JButton("现在连接");
		pConnect.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (pLocalAcc.getText().isEmpty())
					return;

				String realUsername = (new CXKUsername(pLocalAcc.getText())).Realusername();
				String password = pLocalPass.getPassword();

				int err = ClickDial.dialRasWindows(realUsername, password, "ignored");
				if (err == 0) {
					pDialState.setText("拨号成功");
				} else {
					pDialState.setText(ClickDial.dialRasWindowsErrorStr(err) + "(Code: " + err + ")");
				}
			}
		});
		
		final JCheckBox pHeart = new JCheckBox("开启心跳模拟");
		pHeart.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
			    pHeart.setSelected(false);  // not implemented
			}
		});
		
		pDialOpr.add(pConnect);
		pDialOpr.add(pHeart);
		
		pDial.add(pLocalAcc);
		pDial.add(pLocalPass);
		pDial.add(pDialState);
		pDial.add(pDialOpr);
		
		return pDial;
	}
	
}
