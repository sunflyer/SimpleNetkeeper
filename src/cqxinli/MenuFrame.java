package cqxinli;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class MenuFrame extends javax.swing.JFrame {

	private static final long serialVersionUID = -4433192896260661893L;
	
	private MenuFrame pMf;
	
	public MenuFrame(String t){
		super(t);
		int width=400,height=180;
		this.setSize(width, height);
		Toolkit tk=Toolkit.getDefaultToolkit();
		Dimension ss=tk.getScreenSize();
		this.setLocation(ss.width/2-width/2, ss.height/2-height/2);
		this.setBackground(Color.WHITE);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setResizable(false);
		pMf=this;
		this.addPanel();
	}
	
	private void addPanel(){
		this.setLayout(new GridLayout(4,1));
		add(new JLabel("选择你要进行的操作"));
		JPanel pPanRo=new JPanel();
		JButton pButRouter=new JButton("连接路由器设置（包括真实账号计算功能）");
		pButRouter.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				if(MainClass.getRemWindowState()){
					MainClass.setDefaultWindow(MainClass.WINDOW_ROUTER);
				}
				MainClass.getDataFrame().setVisible(true);
				pMf.setVisible(false);
			}
			
		});
		pPanRo.add(pButRouter);
		JPanel pPanDial=new JPanel();
		JButton pButDial=new JButton("本机连接+Wifi共享（仅限Windows）");
		pButDial.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				if(MainClass.getRemWindowState()){
					MainClass.setDefaultWindow(MainClass.WINDOW_DIAL);
				}
				if(MainClass.getLibLoaded()){
					MainClass.getDialFrame().setVisible(true);
					pMf.setVisible(false);
				}
			}
			
		});
		
		pPanDial.add(pButDial);
		
		JPanel pPanSelect=new JPanel();
		final JCheckBox pChkDef=new JCheckBox("记住我的选择,下次自动进入");
		pChkDef.addItemListener(new ItemListener(){

			@Override
			public void itemStateChanged(ItemEvent e) {
				MainClass.setRemWindowState(pChkDef.isSelected());			
			}
			
		});
		
		JButton pButWeb=new JButton("软件更新");
		pButWeb.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					Desktop.getDesktop().browse(new URI("http://www.sunflyer.cn/?p=8"));
				} catch (IOException e1) {
					Log.logE(e1);
				} catch (URISyntaxException e1) {
					Log.logE(e1);
				}
				
			}
			
		});
		
		pPanSelect.add(pChkDef);
		pPanSelect.add(pButWeb);
		add(pPanRo);
		add(pPanDial);
		add(pPanSelect);
	}

}
