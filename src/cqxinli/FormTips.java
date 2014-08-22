package cqxinli;

import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;

@SuppressWarnings("serial")
public class FormTips extends JFrame{
	private String url;
	public FormTips(String URL){
		super();
		this.url=URL;
		this.setVisible(true);
		this.setLocation(200, 200);
		this.setSize(600,400);
		this.setLayout(new BorderLayout());
		this.setResizable(false);
		JTextArea jtf=new JTextArea();
		jtf.setEditable(false);
		jtf.setText("我们在尝试设置你的账号时检测到了一个登陆操作\n这意味着：\n有可能你提供的路由器管理员账号错误，或者\n路由器可能允许你尝试以下方法设置路由器拨号\n\n对于第二种情况，请打开浏览器，在地址栏中输入下列内容\n\n"+this.url+"\n\n接下来路由器会要求输入密码，请输入您的管理员账号和密码继续操作");
		jtf.setLineWrap(true);
		add(jtf,BorderLayout.NORTH);
		
		JButton jbt=new JButton("复制到剪切板");
		jbt.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				StringSelection stsel = new StringSelection(url);
		        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stsel, stsel);
		        JOptionPane.showMessageDialog(null, "地址已复制到剪切板");
			}
			
		});
		add(jbt,BorderLayout.SOUTH);
	}
	
	
}
