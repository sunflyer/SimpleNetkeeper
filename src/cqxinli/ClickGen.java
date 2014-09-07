package cqxinli;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JOptionPane;


public class ClickGen implements ActionListener {

	private FormPanel name;
	private PasswordPanel gAccPwd;
	private FormPanel gRouterIP;
	private RealUserFrame key;
	public ClickGen(FormPanel name,RealUserFrame key,PasswordPanel gAccPwd,FormPanel gRouterIP){
		this.name=name;
		this.key=key;
		this.gAccPwd=gAccPwd;
		this.gRouterIP=gRouterIP;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(name.getValue().equals("")) {
			JOptionPane.showMessageDialog(null, "您输入的用户名为空");
		}else{
			this.key.setUsername(name.getValue());
			this.key.setUrlInfo(this.gRouterIP.getValue(), this.gAccPwd.getPassword());
			this.key.showFrame();
		}
		
	}
	

}
