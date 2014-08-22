package cqxinli;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URLEncoder;

import javax.swing.JOptionPane;


public class ClickGen implements ActionListener {

	private FormPanel name;
	private FormPanel realname;
	private FormPanel encodedName;
	
	public ClickGen(FormPanel name,FormPanel realname,FormPanel encodedName){
		this.name=name;
		this.realname=realname;
		this.encodedName=encodedName;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(name.getValue().equals("")) {
			JOptionPane.showMessageDialog(null, "您输入的用户名为空");
		}else{
			CXKUsername un=new CXKUsername(name.getValue());
			realname.setValue(un.Realusername());	
			try{
				String tmp=URLEncoder.encode(un.Realusername(), "UTF-8");
				tmp=tmp.replace("+", "%2D");
				encodedName.setValue(tmp);
			}
			catch(Exception ex){
				DataFrame.showTips("尝试编码时出现问题");
			}
		}
		
	}

}
