package cn.sunflyer.simpnk.control;

import android.os.Bundle;
import android.os.Message;

import java.util.HashMap;

import cn.sunflyer.simpnk.obj.Router;
import cn.sunflyer.simpnk.obj.RouterMecuryTPF;
import cn.sunflyer.simpnk.obj.RouterMecuryTpNew;
import cn.sunflyer.simpnk.obj.RouterTpLinkNew;

/**
 * @author CrazyChen
 * @version 1.5.0
 * @since Simple Netkeeper 1.5.0
 * */
public class DialController {


	public static void dialRouter(){
		new Thread(new Runnable(){

			@Override
			public void run() {
				
				int pDialMod = StatusController.getStateRouterAuthMethod() != Router.AUTH_NOT_AVALIABLE ? StatusController.getStateRouterAuthMethod() : Router.getRouterAccessMethod(StatusController.sRouterAdd);
				if(pDialMod == Router.AUTH_NOT_AVALIABLE){

					HashMap<Integer , Router> routerList = new HashMap<>();
                    //401 First
                    StatusController.setStateRouterAuthMethod(Router.AUTH_OLD);
					Router r = new RouterMecuryTPF();
                    if(StatusController.getStateRouterAuthMethod() != Router.AUTH_NOT_AVALIABLE){
                        DialController.dialRouter();
                    }else{
                        StatusController.setStateRouterAuthMethod(Router.AUTH_DYNAMIC_ID);
                        r = new RouterMecuryTpNew();
                        if(StatusController.getStateRouterAuthMethod() != Router.AUTH_NOT_AVALIABLE){
                            DialController.dialRouter();
                        }else{
                            sendMessage("没有访问路由器的方式。请检查路由器是否被支持。此外，如果你使用TP-LINK系列新版路由器（TP-LINK 886N等），请在设置向导修改路由器品牌为“TP-LINK”新版本后重试。");
                            StatusController.setStateRouterDial(false);
                        }
                    }
				}	
				else{
					//设置验证方式
					StatusController.setStateRouterAuthMethod(pDialMod);
					StatusController.setStateRouterDial(true);
					//保存配置文件
					ConfigController.saveConfig();

					switch(pDialMod){
					case Router.AUTH_OLD:
					case Router.AUTH_PASSWORD_ONLY:
					case Router.AUTH_WEB:{
						final RouterMecuryTPF pRou = new RouterMecuryTPF();
						new Thread(new DialThread(pRou,pDialMod)).start();
					}break;
					case Router.AUTH_DYNAMIC_ID:{
						final RouterMecuryTpNew pRou = new RouterMecuryTpNew();
						new Thread(new DialThread(pRou,Router.AUTH_DYNAMIC_ID)).start();
					}break;
					case Router.AUTH_DYNAMIC_ID_TP:{
						new Thread(new DialThread(new RouterTpLinkNew() , Router.AUTH_DYNAMIC_ID_TP)).start();
					}break;
					default:
						sendMessage("验证方式存在错误，无法执行操作");
						StatusController.setStateRouterDial(false);
					}
										
				}
			}
			
			
		}).start();

	}



	public static  void sendMessage(String pMsg){
		MessageHandler.sendMessage(MessageHandler.MSG_ACTION_LOG_AND_REFRESH , pMsg);
	}

    public static void sendMessageCompleteDial(){
        MessageHandler.sendMessage(MessageHandler.MSG_ACTION_DIAL_COMPLETE , "");
    }

    public static void sendMessageCompleteTrack(){
        MessageHandler.sendMessage(MessageHandler.MSG_ACTION_TRACK_COMPLETE , "");
    }

	static class DialThread implements Runnable{

		private Router m = null;
		private int am = Router.AUTH_NOT_AVALIABLE;
		
		public DialThread(Router r,int am){
			this.m = r;
			this.am = am;
		}
		@Override
		public void run() {
			
			int pDialRes = Router.RES_NO_DIAL_MODE;
			
			pDialRes = m.connect();

			DialController.sendMessage(pDialRes == Router.RES_IO_EXCEPTION ? Router.RES_TIPS[pDialRes] + m.LAST_EXCEPTION_STRING : Router.RES_TIPS[pDialRes]);
            //发送拨号完毕的消息
            DialController.sendMessageCompleteDial();
            //如果拨号成功，启动追踪
            if(pDialRes == Router.RES_SUCCESS){
                m.trackLink();
                //发送追踪完毕
                DialController.sendMessageCompleteTrack();
            }
            //隐藏BLOCK
			MessageHandler.sendMessage(MessageHandler.MSG_ACTION_HIDE_BLOCK,"");
            //取消状态
			StatusController.setStateRouterDial(false);
		}
		
		
	}
	
}
