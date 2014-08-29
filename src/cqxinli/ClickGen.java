package cqxinli;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JOptionPane;


public class ClickGen implements ActionListener {

	private FormPanel name;
	private RealUserFrame key;
	public ClickGen(FormPanel name,RealUserFrame key){
		this.name=name;
		this.key=key;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(name.getValue().equals("")) {
			JOptionPane.showMessageDialog(null, "您输入的用户名为空");
		}else{
			this.key.setUsername(name.getValue());
			this.key.showFrame();
		}
		
	}
	

}
