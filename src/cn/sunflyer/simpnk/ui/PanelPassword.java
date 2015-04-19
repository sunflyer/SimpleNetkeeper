package cn.sunflyer.simpnk.ui;

import java.awt.Color;
import java.awt.event.FocusListener;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;

public class PanelPassword extends JPanel{

	private static final long serialVersionUID = 1241310366546636676L;
	
	private JLabel gLabel = null;
	private JPasswordField gInput = null ;
	
	/**
	 * 构造一个带标签输入框对象，默认宽500，高45
	 * */
	public PanelPassword(String szDataName,String szInitData,boolean isReadOnly){
		this(szDataName,szInitData,isReadOnly,500,45);
	}
	
	public PanelPassword(String szDataName,String szInitData,boolean isReadOnly,int Width,int Height){
		String pLabelName = ( szDataName == null || szDataName.equals("") )? "Label" : szDataName ;
		String pInputData = ( szInitData == null )? "" : szInitData ;	
		this.setLayout(null);
		this.setSize(Width, Height);
		this.initComponent(pLabelName,pInputData,isReadOnly);
	}

	private void initComponent(String szDataName,String szInitData,boolean isReadOnly){
		this.gInput = new JPasswordField();
		this.gInput.setText(szInitData);
		this.gInput.setEditable(!isReadOnly);
		this.gInput.setBackground(Color.WHITE);
		
		this.gLabel = new JLabel(szDataName);
		this.gLabel.setBackground(Color.WHITE);
		//设置布局
		this.gLabel.setSize((int)(this.getWidth() * 0.4), (int)(this.getHeight() * 0.8));
		this.gInput.setSize((int)(this.getWidth() * 0.55), (int)(this.getHeight() * 0.8));
		
		this.gLabel.setBounds(0 ,  0, this.gLabel.getWidth(), this.gLabel.getHeight());
		this.gInput.setBounds(this.gLabel.getWidth() , 0, this.gInput.getWidth(), this.gInput.getHeight());
		
		this.add(this.gLabel);
		this.add(this.gInput);
		
		this.setBackground(Color.WHITE);
	}
	
	public void setPassword(CharSequence c){
		if(this.gInput == null || c == null) return;
		this.gInput.setText(c.toString());
	}
	
	public String getPassword(){		
		return this.gInput == null ? null : new String(this.gInput.getPassword());
		
	}
	
	public void setLabel(CharSequence c){
		if(this.gLabel == null || c==null) return;
		this.gLabel.setText(c.toString());
	}
	
	public String getLabel(){
		return this.gLabel == null ? null : this.gLabel.getText();
	}
	
	public boolean getReadOnly(){
		return this.gInput == null ? true : !this.gInput.isEditable() ;
	}
	
	public void setReadOnly(boolean a){
		if(this.gInput == null) return ;
		this.gInput.setEditable(!a);
	}
	
	public void setInputHelp(CharSequence c){
		this.gInput.setToolTipText(c.toString());
	}
	
	public void addFocusListenerForBox(FocusListener x){
		this.gInput.addFocusListener(x);
	}
}
