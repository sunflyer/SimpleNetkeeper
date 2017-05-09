package cn.sunflyer.simplenetkeeper;

import cn.sunflyer.simplenetkeeper.ui.SDialog;
import cn.sunflyer.simplenetkeeper.ui.WaitingUi;
import cn.sunflyer.simplenetkeeper.util.AndroidTools;
import cn.sunflyer.simpnk.annotation.Config;
import cn.sunflyer.simpnk.annotation.ConfigLoad;
import cn.sunflyer.simpnk.control.ConfigController;
import cn.sunflyer.simpnk.control.DialController;
import cn.sunflyer.simpnk.control.MessageHandler;
import cn.sunflyer.simpnk.control.StatusController;
import cn.sunflyer.simpnk.obj.Router;
import cn.sunflyer.simpnk.obj.RouterMecuryTPF;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import java.util.Calendar;


public class OntimeDial extends Activity {

    /**
     * UI控件组
     * */
    //保存按钮
    private Button mButSave;
    private Button mButConfig; // 配置按钮

    @ConfigLoad(req = "sStartHour")
    private EditText mEtStartHour;//起始时间-时
    @ConfigLoad(req = "sStartMin")
    private EditText mEtStartMinute;//起始时间-分

    @ConfigLoad(req = "sEndHour")
    private EditText mEtEndHour;//结束时间-时
    @ConfigLoad(req = "sEndMin")
    private EditText mEtEndMinute;//结束时间-分

    @ConfigLoad(req = "sStartDay")
    private EditText mEtStartDay;//起始时间-星期几（从周日开始，0~6）
    @ConfigLoad(req = "sEndDay")
    private EditText mEtEndDay;//结束时间-星期几，不得小于起始时间

    @ConfigLoad(req = "sOntimeWifi")
    private EditText mEtWifiname;
    @ConfigLoad(req = "sOntimeWifiKey")
    private EditText mEtWifikey;

    private RadioButton mDialByRouter;//路由器定时方式
    private RadioButton mDialByDevice;//安卓设备拨号方式

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_ontime_dial);

        this.initComponentVariable();

        this.initComponentButton(this.getIntent().getBooleanExtra("isFirstRun", true));

        this.initComponentHandler();

        this.initBlockService();
    }

    /**阻止某些未开放功能使用*/
    private void initBlockService(){
        //this.mDialByDevice.setEnabled(false);
        //this.mDialByDevice.setTextColor(Color.GRAY);

        //this.mButSave.setEnabled(false);
        //this.mButSave.setBackgroundColor(Color.GRAY);

        //this.mEtStartDay.setEnabled(false);
        //this.mEtEndDay.setEnabled(false);

    }

    /**初始化组建变量*/
    private void initComponentVariable(){
        this.mButConfig = (Button)findViewById(R.id.otd_button_set);
        this.mButSave = (Button)findViewById(R.id.otd_button_save);

        this.mEtStartHour = (EditText)findViewById(R.id.otd_in_hour_start);
        this.mEtStartMinute = (EditText)findViewById(R.id.otd_in_minute_start);

        this.mEtEndHour = (EditText)findViewById(R.id.otd_in_hour_end);
        this.mEtEndMinute = (EditText)findViewById(R.id.otd_in_minute_end);

        this.mEtStartDay = (EditText)findViewById(R.id.otd_in_ws_start);
        this.mEtEndDay = (EditText)findViewById(R.id.otd_in_ws_end);

        this.mEtWifiname = (EditText)findViewById(R.id.otd_in_wifiname);
        this.mEtWifikey = (EditText)findViewById(R.id.otd_in_wifikey);

        this.mDialByDevice = (RadioButton)findViewById(R.id.otd_opt_device);
        this.mDialByRouter = (RadioButton)findViewById(R.id.otd_opt_router);

        //初始化变量
        this.mEtStartDay.setText(String.valueOf(StatusController.sStartDay));
        this.mEtEndDay.setText(String.valueOf(StatusController.sEndDay));
        this.mEtStartHour.setText(String.valueOf(StatusController.sStartHour));
        this.mEtStartMinute.setText(String.valueOf(StatusController.sStartMin));
        this.mEtEndHour.setText(String.valueOf(StatusController.sEndHour));
        this.mEtEndMinute.setText(String.valueOf(StatusController.sEndMin));

        if(StatusController.sOntimeMode == StatusController.ONTIME_MODE_BACKGROUND){
            this.mDialByDevice.setChecked(true);
        }else{
            this.mDialByRouter.setChecked(true);
        }

        //
        this.mEtWifiname.setText(StatusController.sOntimeWifi);
        this.mEtWifikey.setText(StatusController.sOntimeWifiKey);
    }

    /**初始化按钮监听事件和可用反应*/
    private void initComponentButton(final boolean isFirstRun){

        this.mButSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(isValidatedData()){
                    saveConfig();
                    if(mDialByRouter.isChecked()){
                        BackgroundDialerReceiver.stopBackgroundService(OntimeDial.this);
                        makeToast("定时拨号数据保存完毕,提醒模式已经停止",true);
                    }else{
                        //BackgroundDialerReceiver.stopBackgroundService(OntimeDial.this);//停止一次避免重复设置
                        //BackgroundDialerReceiver.startBackgroundService(OntimeDial.this);
                        makeToast("定时拨号数据保存完毕,请点击现在设置以启动提醒模式功能（选择提醒模式后直接点击现在设置即可，会自动保存）", true);
                    }
                }
                else{
                    makeToast("输入的数据不合法，请检查您的输入!",true);
                }
            }
        });

        if(isFirstRun){
            mButConfig.setBackgroundColor(Color.GRAY);
            mButConfig.setText("不可设置");
        }

        this.mButConfig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isFirstRun){
                    makeToast("你还没有成功地设置过你的路由器，无法使用此功能",true);
                }else{

                   if(isValidatedData()){

                       if(mDialByRouter.isChecked()){

                           if(!AndroidTools.isWifiNetwork(OntimeDial.this)){
                               MessageHandler.sendMessage(MessageHandler.MSG_ACTION_DO_STATE , "你还没有连接到无线网络！");
                               return;
                           }

                           saveConfig();

                           final int pStartHour = Integer.parseInt(mEtStartHour.getText().toString());
                           final int pStartMin = Integer.parseInt(mEtStartMinute.getText().toString());
                           final int pEndHour = Integer.parseInt(mEtEndHour.getText().toString());
                           final int pEndMin = Integer.parseInt(mEtEndMinute.getText().toString());

                           if(StatusController.getStateRouterAuthMethod() == Router.AUTH_WEB || StatusController.getStateRouterAuthMethod() == Router.AUTH_OLD || StatusController.getStateRouterAuthMethod() == Router.AUTH_PASSWORD_ONLY){
                               new Thread(new Runnable() {
                                   @Override
                                   public void run() {
                                       Looper.prepare();
                                       MessageHandler.sendMessage(MessageHandler.MSG_ACTION_SHOW_BLOCK, "");
                                       MessageHandler.sendMessage(MessageHandler.MSG_ACTION_DO_STATE,"开始设置路由器的数据，请稍等");
                                       RouterMecuryTPF pRo = new RouterMecuryTPF();
                                       pRo.setOntimeDial(pStartHour,pStartMin,pEndHour,pEndMin);
                                       MessageHandler.sendMessage(MessageHandler.MSG_ACTION_HIDE_BLOCK,"");
                                   }
                               }).start();
                           }else{
                                makeToast("很抱歉，但是当前路由器不适用此项功能",true);
                           };

                       }else if(mDialByDevice.isChecked()){

                            if(mEtWifikey.getText().toString().equals("") || mEtWifikey.getText().toString().length() < 8){
                                makeToast("使用定时拨号功能前请输入正确合法的WIFI密码" , true);
                            }else{
                                if(mEtWifiname.getText().toString().equals("")){
                                    mEtWifiname.setText(AndroidTools.getWifiName(OntimeDial.this));
                                }
                                saveConfig();

                                //先停止一次
                                BackgroundDialerReceiver.stopBackgroundService(OntimeDial.this);
                                //开始后台服务
                                BackgroundDialerReceiver.startBackgroundService(OntimeDial.this);
                                makeToast("提醒模式已经设置完毕，由于使用闹钟机制，因此在非运行期间不会造成系统资源消耗，关机、重启均会使得设置失效，请留意。",true);
                            }

                       }else{
                            makeToast("似乎出现了一些小问题，因为没有检测到任何设置方式(设备连接 - " + mDialByDevice.isChecked() + " , 手动连接 - " + mDialByRouter.isChecked() + ")" , true);
                       }
                   }else{
                        makeToast("你输入的数据存在错误，请检查输入",true);
                   }

                }
            }
        });

        this.mDialByRouter.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    makeToast("你已经选择了由路由器定时拨号功能来完成连接，请参照下面使用须知第一点的提示内容进行设置", true);
            }
        });

        this.mDialByDevice.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    makeToast("你已经选择了由你的安卓设备执行定时拨号连接，请参照下面使用须知第二点的提示内容进行设置", true);
            }
        });

    }



    /**检查输入合法性*/
    private boolean isValidatedData(){
        try{
            final int pStartHour = Integer.parseInt(mEtStartHour.getText().toString());
            final int pStartMin = Integer.parseInt(mEtStartMinute.getText().toString());
            final int pEndHour = Integer.parseInt(mEtEndHour.getText().toString());
            final int pEndMin = Integer.parseInt(mEtEndMinute.getText().toString());

            final int pStartDay = Integer.parseInt(mEtStartDay.getText().toString());
            final int pEndDay = Integer.parseInt(mEtEndDay.getText().toString());

            //需要修改
            if(mDialByRouter.isChecked()){
                if(pStartHour >= 0 && pStartHour <24 &&  pEndHour >= 0 && pEndHour < 24){
                    return  pStartMin >= 0 && pStartMin <= 59 && pEndMin >= 0 && pEndMin <= 59;
                }
            }else if(mDialByDevice.isChecked()){
                if(pStartHour >= 0 && pStartHour < 24 && pEndHour >= 0 && pEndHour < 24){
                    if(pStartMin >= 0 && pStartMin <= 59 && pEndMin >= 0 && pEndMin <= 59){
                        return pStartDay >= 0 && pStartDay < pEndDay && pEndDay < 7 ;
                    }
                }
            }
        }catch(Exception e) {

        }
        return false;
    }

    private void saveConfig(){
        StatusController.sStartHour = StatusController.parseInt(this.mEtStartHour.getText().toString(),7);
        StatusController.sStartMin = StatusController.parseInt(this.mEtStartMinute.getText().toString(),30);
        StatusController.sEndHour = StatusController.parseInt(this.mEtEndHour.getText().toString(),23);
        StatusController.sEndMin = StatusController.parseInt(this.mEtEndMinute.getText().toString(),30);
        StatusController.sStartDay = StatusController.parseInt(this.mEtStartDay.getText().toString(),1);
        StatusController.sEndDay = StatusController.parseInt(this.mEtEndDay.getText().toString(),5);
        StatusController.sOntimeMode = this.mDialByDevice.isChecked() ? StatusController.ONTIME_MODE_BACKGROUND : StatusController.ONTIME_MODE_MANUALLY;

        StatusController.sOntimeWifi = this.mEtWifiname.getText().toString();
        StatusController.sOntimeWifiKey = this.mEtWifikey.getText().toString();
        ConfigController.saveConfig();
    }

    private WaitingUi mBlock;

    Handler mHideHandler ;

    /**初始化HANDLER和BLOCK*/
    private void initComponentHandler(){
        mBlock = new WaitingUi(this);

        this.mHideHandler = new Handler(){

            private String mLastMessage = "";
            /**
             * 设置事件处理器
             * */
            @Override
            public void handleMessage(Message msg) {

                Context c = OntimeDial.this;

                if(msg != null && msg.getData() != null && !msg.getData().isEmpty()){
                    switch(msg.getData().getInt(MessageHandler.MSG_DATA_ACTION_CODE)){
                        //执行拨号操作
                        case MessageHandler.MSG_ACTION_DO_DIAL:{
                            if(StatusController.getStateRouterDial())
                                MessageHandler.sendMessage(MessageHandler.MSG_ACTION_LOG_AND_REFRESH, "已在尝试执行拨号中，请不要重复点击");
                            else{
                                DialController.dialRouter();
                                mBlock.show();
                                MessageHandler.sendMessage(MessageHandler.MSG_ACTION_DO_STATE, "已开始为你设置路由器，请稍候");
                            }
                        }break;
                        case MessageHandler.MSG_ACTION_SHOW_BLOCK:
                            mBlock.show();
                            break;
                        case MessageHandler.MSG_ACTION_HIDE_BLOCK:
                            mBlock.hide();
                            makeToast(mLastMessage,true);
                            break;
                        //执行状态显示，主要是发送Toast
                        case MessageHandler.MSG_ACTION_DO_STATE:{
                            String pszData = msg.getData().getString(MessageHandler.MSG_DATA_INFO);
                            AndroidTools.makeToast(c, pszData == null ? "No data" : pszData, true);
                        }break;
                        //记录并刷新状态
                        case MessageHandler.MSG_ACTION_LOG_AND_REFRESH:{
                            String pszData = msg.getData().getString(MessageHandler.MSG_DATA_INFO);
                            //mTvStatus.setText(pszData == null ? "未知状态" : pszData);
                            mLastMessage = pszData;
                            mBlock.setTips(pszData);
                            cn.sunflyer.simpnk.control.Log.log(pszData == null ? "记录数据错误：空值" : pszData);
                        }break;
                        //刷新状态
                        case MessageHandler.MSG_ACTION_REFRESH_INFO:{
                            String pszData = msg.getData().getString(MessageHandler.MSG_DATA_INFO);
                            //mTvStatus.setText(pszData == null ? "未知状态" : pszData);
                            mLastMessage = pszData;
                            mBlock.setTips(pszData);
                        }break;
                        case MessageHandler.MSG_ACTION_SHOW_DIALOG:{
                            String pszData = msg.getData().getString(MessageHandler.MSG_DATA_INFO);
                            SDialog pAlert = new SDialog(OntimeDial.this);
                            pAlert.show();
                            pAlert.setWindowContent(pszData).setWindowTitle("定时设置").setPositiveButton("好的",null);
                        }
                        default:
                    }
                }
            }
        };

        MessageHandler.setAndroidHandler(mHideHandler);

    }

    private void makeToast(CharSequence szText,boolean isShort){
        Toast.makeText(this,szText,isShort ? Toast.LENGTH_SHORT : Toast.LENGTH_LONG).show();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            this.mBlock.dismiss();
            this.finish();
        }
        return true;
    }
}
