package cn.sunflyer.simpnk.control;

import java.util.List;

import cn.sunflyer.simpnk.obj.Bundle;
import cn.sunflyer.simpnk.obj.Message;

@SuppressWarnings("unused")
public class MessageController {

	public static final int MSG_SET_TIPS = 0;
	public static final int MSG_SET_LOG = 1;
	public static final int MSG_SET_LOG_AND_TIPS = 2;
	
	/**消息代码*/
	public static enum MessageCode{
		/**日志和拨号面板更新*/
		LOG_AND_TIPS_ROUTER,
		/**事件记录*/
		LOG_ONLY,
		/**拨号面板提示修改*/
		TIPS_ROUTER_ONLY,
		/**更新状态值*/
		UPDATE_STATUS,
		/**更新拨号状态*/
		UPDATE_STATUS_DIAL,
		/**重置组件*/
		RESET_COMPONENT
	}
	
	//=====================================================================
	
	private static MessageController pMsgCon = new MessageController(){
		@Override
		public synchronized void processMessage(MessageCode code,Message str){
			if(code == null) return;
			switch(code){
			//case MSG_SET_TIPS:StatusController.setStatusRouterConfigStatusBar(str);break;
			//case MSG_SET_LOG:Log.log(str);break;
			//case MSG_SET_LOG_AND_TIPS:
			//	StatusController.setStatusRouterConfigStatusBar(str);
			//	Log.log(str);
			//	break;
			case LOG_AND_TIPS_ROUTER:
				if(str == null) return;
				
				for(Bundle x: str){
					if(x.bundleSource.equals("info") && x.bundleData!=null){
						Log.log(String.valueOf(x.bundleData));
						StatusController.setStatusRouterConfigStatusBar(String.valueOf(x.bundleData));
					}
				}
					
				break;
			case LOG_ONLY:
				
				if(str == null) return;				
				for(Bundle x: str){
					if(x.bundleSource.equals("info") && x.bundleData!=null){
						Log.log(String.valueOf(x.bundleData));
					}
				}
				
				break;
			case TIPS_ROUTER_ONLY:
				
				if(str == null) return;			
				for(Bundle x: str){
					if(x.bundleSource.equals("info") && x.bundleData!=null){
						StatusController.setStatusRouterConfigStatusBar(String.valueOf(x.bundleData));
					}
				}
				
				break;
			case UPDATE_STATUS:
				
				break;
			case UPDATE_STATUS_DIAL:
				
				break;
			case RESET_COMPONENT:
				
				break;
			default:
			}
		}
	};
	
	public static MessageController getInstance(){
		return pMsgCon;
	}
	
	private MessageCode mMessageCode = null;
	private Message mMessageInfo = null;
	private List<Message> mMessageList = null;
		
	public void sendMessage(MessageCode pMsgCode,Message pMsgInfo){
		this.mMessageCode = pMsgCode;
		this.mMessageInfo = pMsgInfo;
		this.processMessage(pMsgCode,pMsgInfo);
	}
	
	public void processMessage(MessageCode pMsgCode,Message pMsgInfo){
		
	}
	
}
