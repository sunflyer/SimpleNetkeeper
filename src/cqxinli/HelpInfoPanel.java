package cqxinli;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class HelpInfoPanel extends JPanel{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public HelpInfoPanel(){
		this.setSize(400,320);
		
		this.setLayout(new BorderLayout());
		
		JTextArea jta=new JTextArea();
		jta.setEditable(false);
		jta.setLineWrap(true);
		//由于String 的拼接操作非常缓慢，因此更换为StringBuilder处理信息
		StringBuilder sb=new StringBuilder();
		sb.append("欢迎使用Netkeeper Dialer For Router 版本"+MainClass.getVersion()+MainClass.BUILD_DATE+"\n");
		sb.append("这个小JAVA程序可以方便的一键设置路由器拨号。适用于重庆地区校园宽带用户。\n\n");
		sb.append("你可以在我的个人博客  http://www.sunflyer.cn/?p=8 找到软件的更新版本\n\n");
		sb.append("鸣谢：重庆邮电大学的学长的Netkeeper的加密算法。(虽然后来自己逆向也成功了。)\n");
		sb.append("源代码可以在 https://github.com/sunflyer/NetkeeperForRouter 找到\n\n");
		sb.append("CopyLeft 2014 CrazyChen@CQUT   电子邮件：cx@itncre.com\n\n");
		sb.append("警告：本软件关于账号加密的全部内容均来自互联网，仅作学习交流之用，本人并不从事任何反向工程。由此软件引发的任何后果由使用者本人承担，制作者概不负责。");
		sb.append("本软件作为免费软件，不得销售此软件以及此软件的修改或/和衍生版本，否则追究责任。");
		jta.setText(sb.toString());
		
		jta.setSize(400, 300);
		
		JScrollPane scroll = new JScrollPane(jta); 
				scroll.setVerticalScrollBarPolicy( 
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS); 

		scroll.setSize(400, 300);
		this.add(scroll,BorderLayout.NORTH);
	}	
}
