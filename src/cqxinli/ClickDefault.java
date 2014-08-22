package cqxinli;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ClickDefault implements ActionListener{

	private FormPanel ip;
	private FormPanel adminUsername;
	private PasswordPanel adminPassword;
	
	public ClickDefault(FormPanel ip,FormPanel adminUsername,PasswordPanel adminPassword){
		this.ip=ip;
		this.adminUsername=adminUsername;
		this.adminPassword=adminPassword;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		ip.setValue("192.168.1.1");
		adminUsername.setValue("admin");
		adminPassword.setPassword("admin");
	}

}
