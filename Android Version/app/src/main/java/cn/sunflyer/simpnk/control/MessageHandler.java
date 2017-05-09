package cn.sunflyer.simpnk.control;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

/**
 * Created by 陈耀璇 on 2015/4/9.
 */
public class MessageHandler {

    /**定义事件行为*/
    public static final String MSG_DATA_ACTION_CODE = "Action",
                            MSG_DATA_INFO = "Info",
                            MSG_DATA_STATUS_CODE = "StatusCode",
                            MSG_DATA_TITLE = "Title";

    /**定义事件代码*/
    public static final int MSG_ACTION_REFRESH_INFO = 1, //刷新状态信息
                MSG_ACTION_SHOW_BLOCK = 2,
                MSG_ACTION_HIDE_BLOCK = 3,
                MSG_ACTION_LOG_AND_REFRESH = 4, //记录日志并刷新状态
                MSG_ACTION_DO_DIAL = 5, // 执行路由器拨号
                MSG_ACTION_DO_STATE = 6, // 执行状态追踪
                MSG_ACTION_SHOW_DIALOG = 7, //显示消息
                MSG_ACTION_UPDATE = 8,
                MSG_ACTION_UPLOAD_LOG = 9,
                MSG_ACTION_DIAL_COMPLETE = 10 ,//拨号完毕
                MSG_ACTION_TRACK_COMPLETE = 11  ,// 追踪连接完毕
                MSG_ACTION_SHOW_NOTIFICATION = 12 ;

    /**用于安卓系统的事件处理器*/
    private static Handler mAndroidHandler = null;

    /**注册安卓客户端事件处理*/
    public static void setAndroidHandler(Handler h){
        mAndroidHandler = h;
    }

    /**获取安卓客户端事件处理器*/
    public static Handler getAndroidHandler(){
        return mAndroidHandler;
    }


    /**向安卓客户端事件处理器发送消息*/
    public static void sendMessageAndroidHandler(Message msg){
        if(msg != null && getAndroidHandler() != null) getAndroidHandler().sendMessage(msg);
    }

    /**以指定行为代码发送字符串消息，包含于INFO字段*/
    public static  void sendMessage(int pActCode,String pMsg){
        Message pMsgToSend = new Message();
        Bundle pMsgBundle = new Bundle();

        //添加数据
        pMsgBundle.putInt(MessageHandler.MSG_DATA_ACTION_CODE , pActCode);
        pMsgBundle.putString(MessageHandler.MSG_DATA_INFO , pMsg);
        pMsgToSend.setData(pMsgBundle);
        MessageHandler.sendMessageAndroidHandler(pMsgToSend);
    }

}
