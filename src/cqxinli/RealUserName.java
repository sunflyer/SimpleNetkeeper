package cqxinli;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.swing.JFrame;
import javax.swing.JLabel;

@SuppressWarnings("serial")
class RealUserFrame extends JFrame{
	private FormPanel URLName=null;
	private FormPanel EncodedName=null;
	private String Username=null;
	public RealUserFrame(String username){
		super("编码用户名");
		initFrame();
		if(username!=null)
			setUsername(username);
	}
	
	public RealUserFrame(){
		this(null);
	}
	
	private void initFrame(){
		JLabel tips=new JLabel("生成的用户名");
		this.URLName=new FormPanel("URL编码后的用户名","",false);
		this.EncodedName=new FormPanel("加密后的用户名","",false);
		this.add(tips);
		this.add(this.URLName);
		this.add(EncodedName);
		this.setDefaultCloseOperation(HIDE_ON_CLOSE);
		this.setSize(320, 120);
		this.setLayout(new GridLayout(3,1));
		Toolkit tk=Toolkit.getDefaultToolkit();
		Dimension di=tk.getScreenSize();
		this.setLocation(di.width/2-160,di.height/2-60);
		this.setResizable(false);
	}
	
	public void showFrame(){
		this.setVisible(true);
	}
	
	public void hideFrame(){
		this.setVisible(false);
	}
	
	public void setUsername(String username){
		this.Username=username;
		if(!this.Username.equals("") && this.Username!=null){
			CXKUsername un=new CXKUsername(this.Username);
			try {
				this.URLName.setValue(URLEncoder.encode(un.Realusername(), "UTF-8").replace("+", "%2D"));
				this.EncodedName.setValue(un.Realusername());
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
	}
	
}