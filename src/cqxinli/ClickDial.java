package cqxinli;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


public class ClickDial implements ActionListener{

	public ClickDial(FormPanel username,PasswordPanel pswd){
		this.username=username;
		this.password=pswd;
	}
	
	private FormPanel username;
	private PasswordPanel password;
	@Override
	public void actionPerformed(ActionEvent e) {
		String name=username.getValue();
		String pwd=password.getPassword();
		if(pwd.length()<6 || name.equals("")){
			DataFrame.showTips("错误：用户名或者密码不合法");
		}
		else{
			DataFrame.showTips("进度：计算加密后的用户名，");
			//真是用户名
			CXKUsername un=new CXKUsername(name);
			name=un.Realusername();
			//开始拨号
			DataFrame.showTips("进度：开始拨号");
			//拨号操作
			try {
				//有问题
				Process p=Runtime.getRuntime().exec("cmd /c rasdial NetKeeper "+name+" "+pwd);
				System.out.println("cmd /c rasdial NetKeeper "+name+" "+pwd);
				StringBuilder sb=new StringBuilder();
				BufferedReader be=new BufferedReader(new InputStreamReader(p.getInputStream()));
				String line;
				while((line=be.readLine())!=null){
					sb.append(line);
				}
				String processData=sb.toString();
				if(processData.indexOf("已连接")>=0 || processData.indexOf("Connected")>=0){
					DataFrame.showTips("已成功建立连接");
				}
				else{
					DataFrame.showTips("建立连接失败");
					System.out.println(processData);
				}
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}

}
