package cqxinli;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

public class AdvanceFrame extends JFrame{

	private static final long serialVersionUID = 1L;
	
	private DataFrame mDF;
	
	public AdvanceFrame(DataFrame df){
		this.mDF=df;
		this.setTitle("高级选项");
		this.setSize(500, 400);
		this.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		Toolkit pTk=Toolkit.getDefaultToolkit();
		Dimension pDemi=pTk.getScreenSize();
		initWindow();
		this.setLocation(pDemi.width/2-250, pDemi.height/2-200);
		this.setVisible(false);
	}
	
	private void initWindow(){
		this.setLayout(new GridLayout(10,1));
		this.setResizable(false);
		
		JPanel pPanelDes_Info_1=new JPanel();
		pPanelDes_Info_1.add(new JLabel("你可以在这里配置路由器的高级选项"));
		this.add(pPanelDes_Info_1);
		
		ComboBoxPanel<String> pCBPanelIsSchool=new ComboBoxPanel<String>("使用模式",new String[]{"家用模式（不加密）","学校模式（加密账号）"},new ItemListener(){
			@Override
			public void itemStateChanged(ItemEvent e) {
				// TODO Auto-generated method stub
				if(e.getStateChange()==ItemEvent.SELECTED){
					String pSelected=e.getItem().toString();
					if(pSelected.equals("家用模式（不加密）")){
						MainClass.setEncryptedAcc(false);
					}else if(pSelected.equals("学校模式（加密账号）")){
						MainClass.setEncryptedAcc(true);
					}
				}
			}
			
		});
		pCBPanelIsSchool.setSelectedItem(MainClass.getEncrytedAcc()?"学校模式（加密账号）":"家用模式（不加密）");
		this.add(pCBPanelIsSchool);
		
		ComboBoxPanel<String> pCBPanelRouter=new ComboBoxPanel<String>("路由器品牌",MainClass.RouterList,new ItemListener(){
			@Override
			public void itemStateChanged(ItemEvent e) {
				if(e.getStateChange()==ItemEvent.SELECTED){
					String pSelected=e.getItem().toString();
					for(int i=0;i<MainClass.RouterList.length;i++){
						if(pSelected.equals(MainClass.RouterList[i])) {
							MainClass.setRouterManufactor(i);
							break;
						}							
					}
				}
			}
		}
		);
		pCBPanelRouter.setAllowSelect(false);
		this.add(pCBPanelRouter);
		
		
		ComboBoxPanel<String> pCBPanelDialType=new ComboBoxPanel<String>("拨号类型",MainClass.DialList,new ItemListener(){

			@Override
			public void itemStateChanged(ItemEvent e) {
				if(e.getStateChange()==ItemEvent.SELECTED){
					MainClass.setDialType(e.getItem().toString());
				}
			}
			
		});
		pCBPanelDialType.setSelectedItem(MainClass.DialList[MainClass.getDialType()]);
		this.add(pCBPanelDialType);
			
		ComboBoxPanel<String> pCBPanelAlgVer=new ComboBoxPanel<String>("设置算法版本",MainClass.AlgVer,new ItemListener(){

			@Override
			public void itemStateChanged(ItemEvent e) {
				if(e.getStateChange()==ItemEvent.SELECTED){
					String pSelected=e.getItem().toString();
					for(int i=0;i<MainClass.AlgVer.length;i++){
						if(pSelected.equals(MainClass.AlgVer[i])) {
							//MainClass.setAlgVer(i+1);
							break;
						}							
					}
				}
			}
			
		});
		pCBPanelAlgVer.setAllowSelect(false);
		this.add(pCBPanelAlgVer);
		
		ComboBoxPanel<String> pCBPanelRealm=new ComboBoxPanel<String>("设置所在位置",new String[]{"重庆","浙江"},new ItemListener(){

			@Override
			public void itemStateChanged(ItemEvent e) {
				if(e.getStateChange()==ItemEvent.SELECTED){
					String pSelected=e.getItem().toString();
					if(pSelected.equals("重庆")){
						
					}else if(pSelected.equals("浙江")){
						
					}
				}
			}
			
		});
		pCBPanelRealm.setAllowSelect(false);
		this.add(pCBPanelRealm);
		
		JPanel pPanelRouterWirelessSet = new JPanel();
		pPanelRouterWirelessSet.add(new JLabel("下方是路由器无线设置配置内容"));
		this.add(pPanelRouterWirelessSet);
		
		final FormPanel pFPWirelessSSID=new FormPanel("路由器SSID（即WiFi名称）");
		pFPWirelessSSID.setValue("请先点击“获取信息”以取得当前配置数据");
		this.add(pFPWirelessSSID);
		final PasswordPanel pPPWirelessPwd=new PasswordPanel("设置无线密码");
		this.add(pPPWirelessPwd);
		
		JPanel pPanelRouterWirelessSetButton = new JPanel(new GridLayout(1,4));
		pPanelRouterWirelessSetButton.add(new JLabel("默认AES+WPA2/PSK"));
		
		final JCheckBox pHideSSID=new JCheckBox("隐藏SSID");
		
		
		final JButton pButtonSetSSID=new JButton("设置无线(S)");
		pButtonSetSSID.setMnemonic(KeyEvent.VK_S);
		pButtonSetSSID.setEnabled(false);
		pButtonSetSSID.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				Router tRouter=new Router(mDF.g_getRouterIP(),mDF.g_getRouterAdmin(),mDF.g_getRouterPassword(),mDF.g_getAccName(),mDF.g_getAccPassword());
				tRouter.setWifiState(new WiFiInfo(pFPWirelessSSID.getValue(),pPPWirelessPwd.getPassword(),pHideSSID.isSelected()));
			}
			
		});
				
		JButton pButtonGetData=new JButton("获取信息(G)");
		pButtonGetData.setMnemonic(KeyEvent.VK_G);
		pButtonGetData.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				allowWirelessConfig(pButtonSetSSID,pHideSSID,pFPWirelessSSID,pPPWirelessPwd);
			}			
		});
		pPanelRouterWirelessSetButton.add(pHideSSID);
		pPanelRouterWirelessSetButton.add(pButtonSetSSID);
		pPanelRouterWirelessSetButton.add(pButtonGetData);
		
		this.add(pPanelRouterWirelessSetButton);
	}
	
	
	private void allowWirelessConfig(final JButton d,final JCheckBox p,final FormPanel a,final PasswordPanel b){
		new Thread(new Runnable(){
			@Override
			public void run() {
				Router tRt=new Router(mDF.g_getRouterIP(),mDF.g_getRouterAdmin(),mDF.g_getRouterPassword(),mDF.g_getAccName(),mDF.g_getAccPassword());
				final WiFiInfo pWifi=tRt.getWifiState();
				final Timer tTi=new Timer(2500,null);	
				
				tTi.addActionListener(new ActionListener(){

					@Override
					public void actionPerformed(ActionEvent e) {
						if(pWifi!=null){
							if(pWifi.getWifiName()!=null){
								a.setValue(pWifi.getWifiName());
								d.setEnabled(true);
								tTi.stop();
							}	
						}
					}
					
				});
				tTi.start();
				
			}
			
		}).start();
	}
	
	
}
