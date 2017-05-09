package cn.sunflyer.simplenetkeeper;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;

import java.util.Calendar;
import java.util.Hashtable;
import java.util.logging.Logger;

import cn.sunflyer.simplenetkeeper.util.AndroidTools;
import cn.sunflyer.simplenetkeeper.util.WifiAdmin;
import cn.sunflyer.simpnk.control.ConfigController;
import cn.sunflyer.simpnk.control.DialController;
import cn.sunflyer.simpnk.control.Log;
import cn.sunflyer.simpnk.control.MessageHandler;
import cn.sunflyer.simpnk.control.StatusController;
import cn.sunflyer.simpnk.obj.RouterMecuryTpNew;

/**
 * Created by 陈耀璇 on 2015/5/25.
 */
public class BackgroundDialerReceiver extends BroadcastReceiver {

    /**Action:启动后台拨号服务*/
    public static final String BG_DIAL_START = "cn.sunflyer.simplenetkeeper.svc.startService";

    /**Action :启动后台心跳服务*/
    public static final String BG_HEART_START = "cn.sunflyer.simplenetkeeper.svc.startServiceHeartBeat";

    private Context mContext;

    private WifiAdmin mWifiAdmin ;

    private Handler mMsgHandler = new Handler(){
        private String mLastMessage = "";

        @Override
        public void handleMessage(Message msg){
            android.util.Log.d("消息处理","收到消息提醒");
            if(msg != null && msg.getData() != null && !msg.getData().isEmpty()){
                switch(msg.getData().getInt(MessageHandler.MSG_DATA_ACTION_CODE)){
                    case MessageHandler.MSG_ACTION_REFRESH_INFO:{
                        String pszData = msg.getData().getString(MessageHandler.MSG_DATA_INFO);
                        mLastMessage = ( pszData == null ? "状态数据错误" : pszData );
                    }break;
                    case MessageHandler.MSG_ACTION_DIAL_COMPLETE:{
                        Log.log("BGB : 后台拨号处理完毕。上一条结果为 - " + mLastMessage);
                    }break;
                    case MessageHandler.MSG_ACTION_TRACK_COMPLETE:{
                        Log.log("BGB : 后台状态追踪完毕。上一条结果为 - " + mLastMessage);

                        /***FOR DEBUG TEST ONLY
                        Message pMsg = new Message();
                        Bundle pData = new Bundle();
                        pData.putInt(MessageHandler.MSG_DATA_ACTION_CODE, MessageHandler.MSG_ACTION_SHOW_NOTIFICATION);
                        pData.putString(MessageHandler.MSG_DATA_INFO, mLastMessage);
                        pData.putString(MessageHandler.MSG_DATA_TITLE, "Simple Netkeeper 后台任务");
                        pData.putString("icon", (mLastMessage != null && mLastMessage.contains("连接成功") ? "ok" : "error"));
                        pMsg.setData(pData);

                        this.sendMessage(pMsg);**/
                    }break;
                    case MessageHandler.MSG_ACTION_HIDE_BLOCK:{
                        Log.log("BGB : 拨号处理进程操作完毕，开始启动通知");
                        Message pMsg = new Message();
                        Bundle pData = new Bundle();
                        pData.putInt(MessageHandler.MSG_DATA_ACTION_CODE, MessageHandler.MSG_ACTION_SHOW_NOTIFICATION);
                        pData.putString(MessageHandler.MSG_DATA_INFO, mLastMessage);
                        pData.putString(MessageHandler.MSG_DATA_TITLE, "Simple Netkeeper 后台任务");
                        pData.putString("icon", (mLastMessage != null && mLastMessage.contains("连接成功") ? "ok" : "error"));
                        pMsg.setData(pData);

                        this.sendMessage(pMsg);
                    }break;
                    case MessageHandler.MSG_ACTION_SHOW_NOTIFICATION:{

                        Bundle pData = msg.getData();
                        if(pData != null && mContext != null){
                            String pTitle = pData.getString(MessageHandler.MSG_DATA_TITLE),
                                    pContent = pData.getString(MessageHandler.MSG_DATA_INFO),
                                    pIcon = pData.getString("icon");
                            int pIconVal =  (pIcon != null && pIcon.equals("ok")) ? R.drawable.ok : R.drawable.error;
                            Log.log("BGB : 通知栏提示 ： " + pContent);
                            AndroidTools.postNotification(mContext,pTitle,pContent,pIconVal);
                        }else
                            Log.log("BGB : 通知栏提示 ： 操作取消（数据不存在）");
                    }break;
                    default:
                }
            }

        }
    };

    private Handler mOriginalHandler = null;

    /**修改处理器为当前状态的处理器*/
    private void setMessageHandler(){
        mOriginalHandler = MessageHandler.getAndroidHandler();
        MessageHandler.setAndroidHandler(mMsgHandler);
    }

    /**恢复原始处理器*/
    private void recoverMessageHandler(){
        MessageHandler.setAndroidHandler(mOriginalHandler);
    }

    @Override
    public void onReceive(Context context, final Intent intent) {

        this.mContext = context;
        //修改配置文件位置
        this.setConfigPath(context);

        android.util.Log.d("后台拨号服务程序", "接收到广播请求,action:" + intent.getAction() + ", debug:" + intent.getBooleanExtra(SIGNAL_DEBUG, false));
        Log.log("BGB : 后台请求已经发现," + intent.getBooleanExtra(SIGNAL_DEBUG, false));

        setMessageHandler();

        if(BG_DIAL_START.equals(intent.getAction())){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    if(!checkWifiState(mContext)){
                        Log.log("BGB : 没有连接到无线网络，或者尝试连接到无线网络出现错误，因此处理出现错误。有关本次错误报告，请参见日志记录。");
                        AndroidTools.postNotification(mContext, "操作失败", "连接无线网络出现错误，因此设置失败。", R.drawable.error);
                        return;
                    }

                    if(!checkTimeSet(mContext)){
                        Log.log("BGB : 时间不在设置范围之内，取消操作，进行下一次设置");
                        startBackgroundService(mContext,true);
                        return;
                    }

                    if(StatusController.isConfigExists && !"".equals(StatusController.sAccName) && !"".equals(StatusController.sAccPassword)){
                        Log.log("BGB : 请求后台拨号服务");
                        DialController.dialRouter();
                    /**
                        RouterMecuryTpNew pRouter = new RouterMecuryTpNew();
                        pRouter.trackLink();
                        Hashtable<String,String> pState = pRouter.getState();
                        MessageHandler.sendMessage(MessageHandler.MSG_ACTION_REFRESH_INFO, "连接成功,IP:" + pState.get("ip"));
                        MessageHandler.sendMessage(MessageHandler.MSG_ACTION_TRACK_COMPLETE ,"");
**/
                    }else{
                        //否则配置不存在取消服务
                        Log.log("BGB : 后台拨号服务请求失败，配置文件并不存在");
                    }

                    startBackgroundService(mContext, true);

                    recoverMessageHandler();
                }
            }).start();
        }else if(BG_HEART_START.equals(intent.getAction())){

            Log.log("BGB : 请求心跳代理服务");

        }else{
            Log.log("BGB : Required Service Not Found , Current Action is : " + intent.getAction());
        }


    }

    private boolean checkTimeSet(Context c){
        Calendar pCal = Calendar.getInstance();
        if(pCal != null){
            int pDay = pCal.get(Calendar.DAY_OF_WEEK) - 1 ;
            return pDay >= StatusController.sStartDay && pDay <= StatusController.sEndDay;
        }
        return false;
    }

    private boolean checkWifiState(Context c){
        if(AndroidTools.isWifiNetwork(c)){
            String pWifiName = AndroidTools.getWifiName(c);
            Log.log("BGB : 无线网络状态检查 - 已连接到 " + pWifiName + " , 原始设置要求的无线网络为 " + StatusController.sOntimeWifi);
            return StatusController.sOntimeWifi != null && !StatusController.sOntimeWifi.equals("") && StatusController.sOntimeWifi.equals(pWifiName);
        }else{
            //连接到指定WIFI
            if(this.mWifiAdmin == null)
                this.mWifiAdmin = new WifiAdmin(c);

            this.mWifiAdmin.openWifi();
            //创建WIFI配置文件，强制WPA/WPA2
            WifiConfiguration pTargetWifiConf = this.mWifiAdmin.createWifiInfo(StatusController.sOntimeWifi , StatusController.sOntimeWifiKey , WifiAdmin.AUTH_WPA);
            if(pTargetWifiConf != null){
                Log.log("BGB : 连接到无线网络 - 配置生成完毕。");
                //等待5秒返回，避免WIFI没有连接上出现问题
                boolean pRes = this.mWifiAdmin.addNetwork(pTargetWifiConf);
                try {
                    Log.log("BGB : 等待最多10秒连接到无线网络");
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    Log.log("BGB : 连接到无线网络 - 等待出现错误");
                }

                return pRes && mWifiAdmin.checkState() == WifiManager.WIFI_STATE_ENABLED  && AndroidTools.isWifiConnected(c) && StatusController.sOntimeWifi.equals(AndroidTools.getWifiName(c));
            }
            Log.log("BGB : 连接到无线网络 - 配置生成出现异常，请检查输入内容。(SSID:" + StatusController.sOntimeWifi + ",密码长度：" + StatusController.sOntimeWifiKey.length() + "/需求最低长度为8");
            return false;
        }
    }

    private void setConfigPath(Context c){
        ConfigController.setConfigPath(c.getFilesDir().toString());
        //加载配置文件状态
        StatusController.initStatus(c.getFilesDir().toString());
    }

    public static void startBackgroundService(Context c){
        startBackgroundService(c,false);
    }

    /**启动后台服务*/
    public static void startBackgroundService(Context c,boolean secday){
        AlarmManager pAlarmMgr = (AlarmManager)c.getSystemService(c.ALARM_SERVICE);
        Intent pTargetIntent = new Intent(c,BackgroundDialerReceiver.class);
        //TODO:for debug , DO NOT MODIFY THE FOLLOWING CODE
        pTargetIntent.putExtra(SIGNAL_DEBUG, true);
        pTargetIntent.setAction(BackgroundDialerReceiver.BG_DIAL_START);

        PendingIntent pAlarmInt = PendingIntent.getBroadcast(c , 0 , pTargetIntent , 0);
        //检查时间选项
        Calendar pCal = Calendar.getInstance();
        //周日为1，周六为7
        int pCurDay = pCal.get(Calendar.DAY_OF_WEEK);

        //TODO : GO EDIT TIME LOGIC
        if(pCurDay >= StatusController.sStartDay + 1  && pCurDay <= StatusController.sEndDay  + 1 ){
            if(pCal.get(Calendar.HOUR_OF_DAY) < StatusController.sStartHour || (pCal.get(Calendar.HOUR_OF_DAY) == StatusController.sStartHour && pCal.get(Calendar.MINUTE) <= StatusController.sStartMin && !secday)){
                //默认当天
                cn.sunflyer.simpnk.control.Log.log("定时设置 ： 设置为当天的 " + StatusController.sStartHour + ":" + StatusController.sStartMin);
            }else{
                //推迟至第二天
                pCal.add(Calendar.DAY_OF_MONTH , 1);
                cn.sunflyer.simpnk.control.Log.log("定时设置 ： 设置为次日的 " + StatusController.sStartHour + ":" + StatusController.sStartMin);
            }
        }else{
            //推迟天数
            pCurDay = 7 + StatusController.sStartDay + 1 - pCurDay;
            pCal.add(Calendar.DAY_OF_MONTH, pCurDay);
        }
        pCal.set(pCal.get(Calendar.YEAR), pCal.get(Calendar.MONTH), pCal.get(Calendar.DAY_OF_MONTH), StatusController.sStartHour, StatusController.sStartMin, 0);

        Log.log("定时设置 ： 取得的最终时间为 " + pCal.getTime().toString());

        pAlarmMgr.set(AlarmManager.RTC_WAKEUP, pCal.getTimeInMillis(), pAlarmInt);

    }

    private static final String SIGNAL_DEBUG = "sunflyer.debugconfigured";

    /**关闭后台服务*/
    public static void stopBackgroundService(Context c){
        AlarmManager pAlarmMgr = (AlarmManager)c.getSystemService(c.ALARM_SERVICE);
        Intent pTargetIntent = new Intent(c,BackgroundDialerReceiver.class);
        PendingIntent pAlarmInt = PendingIntent.getBroadcast(c , 0 , pTargetIntent , 0);
        pAlarmMgr.cancel(pAlarmInt);
    }
}
