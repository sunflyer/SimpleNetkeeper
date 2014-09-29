package cqxinli;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

@SuppressWarnings("serial")
class RealUserFrame extends JFrame{
	private FormPanel URLName=null;
	private FormPanel EncodedName=null;
	private String Username=null;
	private JTextField gUrlArea=null;
	private String gRouterIP=null;
	private String gAccPassword=null;
	private JButton gButCopy=null;
	public RealUserFrame(String username){
		super("编码用户名");
		initFrame();
		if(username!=null)
			setUsername(username);
	}
	
	public RealUserFrame(){
		this(null);
	}
	
	public void setUrlInfo(String ip,String password){
		this.gRouterIP=ip;
		this.gAccPassword=password;
		if(this.NotEmpty(this.gRouterIP) && this.NotEmpty(this.gAccPassword) && this.NotEmpty(this.URLName.getValue())){
			this.gUrlArea.setText("http://"+gRouterIP+"/userRpm/PPPoECfgRpm.htm?wan=0&wantype=2&acc="+URLName.getValue()+"&psw="+gAccPassword+"&confirm="+gAccPassword
	    				+"sta_ip=0.0.0.0&sta_mask=0.0.0.0&linktype=2&Connect=%C1%AC+%BD%D3");
			this.gButCopy.setEnabled(true);
		}		
		else{
			this.gUrlArea.setText("输入路由器IP地址以及宽带账号信息后才可以生成。");
			this.gButCopy.setEnabled(false);
		}
			
	}
	
	private boolean NotEmpty(String x){
		if (x==null) return false;
		return !x.equals("");
	}

	
	private void initFrame(){
		JLabel tips=new JLabel("生成的用户名");
		this.URLName=new FormPanel("URL编码后的用户名","",false);
		this.EncodedName=new FormPanel("加密后的用户名","",false);
		this.add(tips);
		this.add(this.URLName);
		this.add(EncodedName);
		this.setDefaultCloseOperation(HIDE_ON_CLOSE);
		JLabel mTipsURL=new JLabel("生成的设定地址");
		add(mTipsURL);
		gUrlArea=new JTextField();
		gUrlArea.setEditable(false);
		add(gUrlArea);
		gButCopy=new JButton("复制地址到剪切板");
		gButCopy.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
					Toolkit mToolkit=Toolkit.getDefaultToolkit();
					StringSelection mUrlData=new StringSelection(gUrlArea.getText());
					mToolkit.getSystemClipboard().setContents(mUrlData, mUrlData);
					JOptionPane.showMessageDialog(null, "已将地址复制到剪切板");
			}
			
		});
		add(gButCopy);
		this.setSize(320, 220);
		this.setLayout(new GridLayout(6,1));
		Toolkit tk=Toolkit.getDefaultToolkit();
		Dimension di=tk.getScreenSize();
		this.setLocation(200,di.height/2-110);
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