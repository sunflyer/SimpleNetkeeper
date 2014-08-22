package cqxinli;

import java.io.IOException;    
import java.io.InputStream;  
import java.net.HttpURLConnection;    
import java.net.MalformedURLException;    
import java.net.URL;   
import java.net.URLEncoder;


/** 
 *  
 * @author CrazyChen@CQUT
 * 
 */    
public class Router {     
    private String ip;
    private String username;
    private String password;
    private boolean isAuthed;
    private String dialer;
    private String dialingPWD;
    private CXKUsername un;
    public Router(String ip,String username,String pswd,String dialer,String dialingPWD) {    
        this.ip=ip;
        this.username=username;
        this.password=pswd;
        this.dialer=dialer;
        this.dialingPWD=dialingPWD;
        this.un=new CXKUsername(this.dialer);
        this.isAuthed=false;
        runCgi("http://"+this.ip,this.username+":"+this.password);
        
    }    
      
    /**
     * @return The connection statement of the router you configured.<br>
     * - -2 if unable to access the device with the account and password user given.<br>
     * - -1 if unable to encode the username and password to URL Encoding<br>
     * - 0 if configuration success.<br>
     * - 1 if IP address is not valid<br>
     * - 3 if the router returns that no authority to access this device.(Always caused by the ROM rejected the access even though your name and password is right)<br>
     * - 4 if InputStream processing error (IOException Occured)<br>
     * - 9 if the application detected another login request to complete this operation, or some routers limited this functions that permision denied.<br>
     * - 10 if error Unknown
     */
    public int connect(){
    	if(isAuthed){
    		String encodeName=null;
    		String encodePassword=null;
    		try{
    			//替换出现的+为空格，否则用户名错误。
    			encodeName=URLEncoder.encode(un.Realusername(),"UTF-8").replace("+", "%2D");
    			encodePassword=URLEncoder.encode(this.dialingPWD, "UTF-8");
    		}catch(Exception ex){
    			ex.printStackTrace();
    			return -1;
    		}
    		//目标地址，这是设置路由器登陆最必须的条件。
    		String URL="http://"+this.ip+"/userRpm/PPPoECfgRpm.htm?wan=0&wantype=2&acc="+encodeName+"&psw="+encodePassword+"&confirm="+encodePassword
    				+"sta_ip=0.0.0.0&sta_mask=0.0.0.0&linktype=2&Connect=%C1%AC+%BD%D3";
    		
    		URL tar=null;
    		HttpURLConnection Tarhuc=null;
    		try{
    			tar=new URL(URL);
    			if(tar!=null){
    				Tarhuc=(HttpURLConnection) tar.openConnection();
    				if(Tarhuc!=null){
    					//设置管理员的Cookie
    					Tarhuc.setRequestProperty("Cookie", "Authorization=Basic "+Base64.encode(this.username+":"+this.password));   					
    					//设置引用页避免权限错误
    					Tarhuc.setRequestProperty("Referer", "http://"+this.ip+"/userRpm/PPPoECfgRpm.htm");
    					Tarhuc.setRequestProperty("Host", this.ip);
    					Tarhuc.setRequestProperty("Connection", "Keep-alive");
    					Tarhuc.setRequestProperty("Content-Length", "0");
    					Tarhuc.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
    				}   				
    				Tarhuc.setConnectTimeout(5000);
    				Tarhuc.connect();
    				
    				InputStream is=Tarhuc.getInputStream();
    				int data=0;
    				StringBuffer sb=new StringBuffer();
    				while((data=is.read())!=-1){
    					sb.append((char)data);
    				}
    				String ResponseHTML=sb.toString();
    				System.out.println(ResponseHTML);
    				//如果回复无权限
    				if(ResponseHTML.indexOf("You have no authority to access this device!")>=0){    					
    					return 3;
    				}else if(ResponseHTML.indexOf("noframe")>=0 || ResponseHTML.indexOf("已连接")>=0 || ResponseHTML.indexOf("PPPoECfgRpm.htm")>=0){    						
    					return 0;
    				}else if(ResponseHTML.indexOf("loginBox")>=0){
    					new FormTips(URL);    					
    				}
    				
    			}
    		}catch(MalformedURLException ex){
    			ex.printStackTrace();
    			return 1;
    		} catch (IOException e) {				
				e.printStackTrace();
				return 4;
			}
    		return 9;
    	}
    	else
    		return -2;
    } 
      
	/**
	 * @param urlStr: The remote address to configure.
	 * @param authorizationStr: Username and password (for access network)
	 */
	private void runCgi(String urlStr, String authorizationStr) {    
        URL xUrl = null;    
        HttpURLConnection xHuc = null;    
        try {    
            xUrl = new URL(urlStr);  
            if (xUrl != null) {    
                xHuc = (HttpURLConnection) xUrl.openConnection();    
                if (xHuc != null) {    
                    if (!"".equals(authorizationStr)) {    
                        //xHuc.setRequestProperty("Authorization", "Basic " + Base64.encode(authorizationStr));   
                        xHuc.setRequestProperty("Cookie", "Authorization=Basic "+Base64.encode(authorizationStr));
                    }                         
                    xHuc.setRequestProperty("Content-Length", "0");    
                    xHuc.setRequestProperty("Content-Type",    
                            "application/x-www-form-urlencoded");    
                    xHuc.connect();     
                    InputStream in=xHuc.getInputStream();  
                    int chint=0;  
                    StringBuffer sb=new StringBuffer();  
                    while((chint=in.read())!=-1){  
                        sb.append((char)chint);  
                    }  
                    String html=sb.toString();  
                    if(html.indexOf("noframe")>0){
                    	this.isAuthed=true;                   	
                    }
                    //DEBUG用，输出调试数据
                    System.out.print(this.isAuthed+"\nBasic "    
                            + Base64.encode(authorizationStr)+"\n"+html);
                }    
            }    
        } catch (MalformedURLException e) {    
            e.printStackTrace();    
        } catch (IOException e) {    
            e.printStackTrace();    
        }  
    }    
}    
