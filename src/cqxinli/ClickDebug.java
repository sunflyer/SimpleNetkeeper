package cqxinli;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.JOptionPane;

public class ClickDebug implements ActionListener{

	protected FormPanel ip;
	protected FormPanel admin;
	protected PasswordPanel pwd;
	
	public ClickDebug(FormPanel ip,FormPanel admin,PasswordPanel pwd){
		this.ip=ip;
		this.admin=admin;
		this.pwd=pwd;
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if(!ip.getValue().equals("") && !admin.getValue().equals("")){
			URL xUrl = null;    
	        HttpURLConnection xHuc = null;    
	        try {    
	            xUrl = new URL("http://"+ip.getValue());  
	            if (xUrl != null) {    
	                xHuc = (HttpURLConnection) xUrl.openConnection();    
	                if (xHuc != null) {    
	                    if (!"".equals(admin.getValue()+":"+pwd.getPassword())) {    
	                    	//设置路由器的COOKIE验证
	                        xHuc.setRequestProperty("Cookie", "Authorization=Basic "+Base64.encode(admin.getValue()+":"+pwd.getPassword()));
	                    }                         
	                    xHuc.setRequestProperty("Content-Length", "0");    
	                    xHuc.setRequestProperty("Content-Type",    
	                            "application/x-www-form-urlencoded");    
	                    xHuc.connect();     
	                    BufferedReader pBufRdr=new BufferedReader(new InputStreamReader(xHuc.getInputStream(),MainClass.getRouterPageEncode()));
	                    int chint=0;  
	                    StringBuffer sb=new StringBuffer();  
	                    while((chint=pBufRdr.read())!=-1){  
	                        sb.append((char)chint);  
	                    }  
	                    String html=sb.toString(); 
	                    System.out.print(html);
	                    Log.log("在《新版本固件处理方式》取得的HTML内容如下"+Log.nLine+html);
	                    Boolean isAuthed=false;
	                    //设置可用  如果检测到登陆成功后的框架代码
	                    //set it available if detected keyword that appear in the page which means login success.
	                    if(html.indexOf("noframe")>0 || html.indexOf("frame")>=0){
	                    	isAuthed=true;                   	
	                    }
	                    Log.log("DEBUG:以最新固件方式的最终处理结果为："+(isAuthed?"可用":"不可用"));
	                }    
	            }    
	        } catch (MalformedURLException e1) {    
	            Log.logE(e1); 
	        } catch (IOException e1) {    
	            Log.logE(e1);  
	            this.detectOld(ip.getValue(), admin.getValue()+":"+pwd.getPassword());
	            
	        }  
		}else{
			JOptionPane.showMessageDialog(null,"请确定你输入的IP和管理员账户无误！");
		}
	}
	
	private void detectOld(String URL, String auth) {
		try {
			Log.log("尝试以旧版本的方式检测可用性");
			URL pUrl = new URL("http://"+URL);
			HttpURLConnection pHuc = (HttpURLConnection) pUrl.openConnection();

			pHuc.setRequestProperty("Authorization",
					"Basic " + Base64.encode(auth));
			pHuc.setRequestProperty("Content-Length", "0");
			pHuc.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
			pHuc.connect();
			InputStream in = pHuc.getInputStream();
			StringBuffer sb = new StringBuffer();
			int chint;
			while ((chint = in.read()) != -1) {
				sb.append((char) chint);
			}
			String html = sb.toString();
			if (html.indexOf("noframe") > 0 || html.indexOf("frame") >= 0){
				Log.log("DEBUG:以《旧版本固件处理方式》的结果为可用");
			}
			else {
				Log.log("DEBUG:以《旧版本固件处理方式》的结果为不可用"+Log.nLine+html);
			}
		} catch (IOException e) {
			Log.logE(e);
		}

	}
	
}
