package cqxinli;

import java.awt.Color;
import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

@SuppressWarnings("serial")
public class FormPanel extends JPanel{
	private String labelname;
	private String TFName;
	private boolean isEditable;
	private JLabel jl;
	private JTextField jtf;
	public FormPanel(String labelName,String TextFieldName,boolean isEditable){
		this.labelname=labelName;
		this.TFName=TextFieldName;
		this.isEditable=isEditable;
		this.setBackground(Color.WHITE);
		this.setLayout(new GridLayout(1,2));
		add();
	}
	
	public FormPanel(String labelName){
		this(labelName,"",true);
	}
	
	private void add(){
		this.jl=new JLabel(labelname);
		add(jl);
		this.jtf=new JTextField(TFName);
		jtf.setEditable(isEditable);
		add(jtf);
	}
	
	public void setTooltipData(String tooltip){
		this.jtf.setToolTipText(tooltip);
	}
	
	public String getValue(){
		return this.jtf.getText();
	}
	
	public void setValue(String value){
		this.jtf.setText(value);
	}
	
	public void setColumn(int co){
		this.jtf.setColumns(co>=0?co:10);
	}
}
