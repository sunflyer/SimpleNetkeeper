package cqxinli;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class AdvanceFrame extends JFrame{

	private static final long serialVersionUID = 1L;

	public AdvanceFrame(){
		this.setTitle("高级选项");
		this.setSize(400, 400);
		this.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		Toolkit pTk=Toolkit.getDefaultToolkit();
		Dimension pDemi=pTk.getScreenSize();
		initWindow();
		this.setLocation(pDemi.width/2-200, pDemi.height/2-200);
		this.setVisible(false);
	}
	
	private void initWindow(){
		this.setLayout(new GridLayout(12,2));
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
					String pSelected=e.getItem().toString();
					for(int i=0;i<MainClass.DialList.length;i++){
						if(pSelected.equals(MainClass.DialList[i])) {
							MainClass.setDialType(i+1);
							break;
						}							
					}
				}
			}
			
		});
		pCBPanelDialType.setSelectedItem(MainClass.DialList[MainClass.getDialType()-1]);
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
	}
	
}
