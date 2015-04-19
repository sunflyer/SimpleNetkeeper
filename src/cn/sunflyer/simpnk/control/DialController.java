package cn.sunflyer.simpnk.control;

import cn.sunflyer.simpnk.obj.Bundle;
import cn.sunflyer.simpnk.obj.Message;
import cn.sunflyer.simpnk.obj.Router;
import cn.sunflyer.simpnk.obj.RouterMecuryTPF;
import cn.sunflyer.simpnk.obj.RouterMecuryTpNew;

/**
 * 拨号控制器。负责路由器拨号以及本地拨号功能实现
 * 
 * @author CrazyChen
 * @version 1.5.0
 * @since Simple Netkeeper 1.5.0
 * */
public class DialController {

	/**
	 * 路由器拨号实现器
	 * */
	public static void dialRouter(){
		StatusController.saveConfig();
		
		new Thread(new Runnable(){

			@Override
			public void run() {
				
				int pDialMod = StatusController.getStateRouterAuthMethod() != Router.AUTH_NOT_AVALIABLE ? StatusController.getStateRouterAuthMethod() : Router.getRouterAccessMethod(StatusController.sRouterAdd);
				if(pDialMod == Router.AUTH_NOT_AVALIABLE){
					sendMessage("没有可用的拨号连接模式");
					StatusController.setStateRouterDial(false);
				}	
				else{
					StatusController.setStateRouterAuthMethod(pDialMod);
					StatusController.setStateRouterDial(true);
					
					switch(pDialMod){
					case Router.AUTH_OLD:
						
					case Router.AUTH_WEB:{
						final RouterMecuryTPF pRou = new RouterMecuryTPF();
						new Thread(new DialThread(pRou,pDialMod)).start();
					}break;
					case Router.AUTH_DYNAMIC_ID:{
						final RouterMecuryTpNew pRou = new RouterMecuryTpNew();
						
						new Thread(new DialThread(pRou,Router.AUTH_DYNAMIC_ID)).start();
						
					}break;
					default:
						sendMessage("");
						StatusController.setStateRouterDial(false);
					}
										
				}
			}
			
			
		}).start();

	}
	
	public static  void sendMessage(String pMsg){
		MessageController.getInstance().sendMessage(MessageController.MessageCode.LOG_AND_TIPS_ROUTER,new Message(new Bundle("info",pMsg)));
	}
	
	/**
	 * 用于路由器设置的执行类
	 * @param r Router类
	 * @param am 验证方式
	 * */
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
			
			switch(this.am){
			case Router.AUTH_DYNAMIC_ID:
				pDialRes = ((RouterMecuryTpNew)m).connect();
				break;
			case Router.AUTH_OLD:
			case Router.AUTH_WEB:
				pDialRes = ((RouterMecuryTPF)m).connect();
				break;
			default: {
				DialController.sendMessage("没有对应选项可用，无法执行拨号操作");
				return;
			}
			}
			
			switch(pDialRes){
			case Router.RES_SUCCESS:{
				DialController.sendMessage("路由器已经设置成功！现在开始追踪状态");
				if(this.m instanceof RouterMecuryTpNew)
					((RouterMecuryTpNew)m).trackLink();
				else if(this.m instanceof RouterMecuryTPF)
					((RouterMecuryTPF)m).trackLink();
			}break;
			case Router.RES_REQUIRE_LOGIN:
				DialController.sendMessage("路由器身份验证失败！");
				break;
			case Router.RES_META_DATA_NOT_INIT:
				DialController.sendMessage("身份验证显示您提供的信息有错误");
				break;
			case Router.RES_NO_AUTHORITY:
				DialController.sendMessage("无权限操作路由器，或者连接失败");
				break;
			case Router.RES_IO_EXCEPTION:
				DialController.sendMessage("出现了一些异常，请尝试重新设置");
				break;
			case Router.RES_UNABLE_ACCESS:
				DialController.sendMessage("无法访问路由器。（请确认路由器是否已经连接，或者请检查你的路由器连线是否正确）");
				break;
				default:DialController.sendMessage("未知错误，错误代码："+pDialRes);
			}
			
			StatusController.setStateRouterDial(false);
		}
		
		
	}
	
}
