package cqxinli;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;

public class AdvancePanel extends JPanel{

	private static final long serialVersionUID = 1L;
	
	public AdvancePanel(){
		initWindow();
	}
	
	private void initWindow(){
		this.setLayout(new GridLayout(11,1));

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
		pCBPanelIsSchool.setSelectedIndex(MainClass.getEncrytedAcc()?1:0);
		this.add(pCBPanelIsSchool);
		
		ComboBoxPanel<String> pCBPanelRouter=new ComboBoxPanel<String>("路由器品牌",MainClass.RouterList,new ItemListener(){
			@Override
			public void itemStateChanged(ItemEvent e) {
				if(e.getStateChange()==ItemEvent.SELECTED){
					String pSelected=e.getItem().toString();
					for(int i=0;i<MainClass.RouterList.length;i++){
						if(pSelected.equals(MainClass.RouterList[i])) {
							MainClass.setRouterManufactor(i+1);
							break;
						}							
					}
				}
			}
		}
		);
		pCBPanelRouter.setSelectedIndex(MainClass.getRouterManufactor()-1);
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
		
		JPanel pPanelRouterWirelessSetButton = new JPanel();
		
		final JCheckBox pHideSSID=new JCheckBox("隐藏SSID");
		
		
		final JButton pButtonSetSSID=new JButton("设置无线(S)");
		pButtonSetSSID.setMnemonic(KeyEvent.VK_S);
		pButtonSetSSID.setEnabled(false);
		pButtonSetSSID.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				Router tRouter=new Router(MainClass.getDataFrame().g_getRouterIP(),MainClass.getDataFrame().g_getRouterAdmin(),MainClass.getDataFrame().g_getRouterPassword(),MainClass.getDataFrame().g_getAccName(),MainClass.getDataFrame().g_getAccPassword());
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
		
		JPanel pPanelApplicationConfig=new JPanel();
		pPanelApplicationConfig.add(new JLabel("应用程序设置"));
		this.add(pPanelApplicationConfig);
		
		JPanel pPanelApplicationConfigButton=new JPanel();
		
		JButton pButInfo=new JButton("路由器当前信息");
		pButInfo.setEnabled(false);
		
		JButton pButInternal = new JButton("内网模式（仅限水星/TP）");
		pButInternal.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				if(JOptionPane.showConfirmDialog(AdvancePanel.this, "请注意：\n这个操作只对TP/水星路由器有效，在执行操作完毕后，路由器的WAN口将断开外网链接同时切换到动态IP模式！\n在继续操作前，你需要在路由器拨号界面填写正确的路由器配置信息！\n确定继续吗？","操作警告",JOptionPane.OK_CANCEL_OPTION)==JOptionPane.OK_OPTION){
					DataFrame tDf=MainClass.getDataFrame();
					Router tRouter=new Router(tDf.g_getRouterIP(),tDf.g_getRouterAdmin(),tDf.g_getRouterPassword(),tDf.g_getAccName(),tDf.g_getAccPassword(),MainClass.getAuthMethod());
					tRouter.setInternalNet();
				}
				
			}
			
		});
		
		pPanelApplicationConfigButton.add(pButInternal);
		pPanelApplicationConfigButton.add(pButInfo);
		this.add(pPanelApplicationConfigButton);
		
	}
	
	
	private void allowWirelessConfig(final JButton d,final JCheckBox p,final FormPanel a,final PasswordPanel b){
		new Thread(new Runnable(){
			@Override
			public void run() {
				Router tRt=new Router(MainClass.getDataFrame().g_getRouterIP(),MainClass.getDataFrame().g_getRouterAdmin(),MainClass.getDataFrame().g_getRouterPassword(),MainClass.getDataFrame().g_getAccName(),MainClass.getDataFrame().g_getAccPassword());
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
