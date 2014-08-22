package cqxinli;

import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;

@SuppressWarnings("serial")
public class PasswordPanel extends JPanel{
	private String LabelName;
	private boolean isEditable;
	
	private JLabel jl;
	private JPasswordField jpf;
	public PasswordPanel(String LabelName,boolean isEditable){
		this.LabelName=LabelName;
		this.isEditable=isEditable;
		PanelPre();
	}
	
	public PasswordPanel(String LabelName){
		this(LabelName,true);
	}
	
	private void PanelPre(){
		this.setLayout(new GridLayout(1,2));
		this.jl=new JLabel(this.LabelName);
		this.jpf=new JPasswordField();
		this.jpf.setEditable(this.isEditable);
		add(jl);
		add(jpf);
	}
	
	public String getPassword(){
		return new String(jpf.getPassword());
	}
	
	public void setPassword(String ps){
		this.jpf.setText(ps);
	}
	
	public void setColumn(int co){
		this.jpf.setColumns(co);
	}
}
