package cn.sunflyer.simplenetkeeper;

import cn.sunflyer.simplenetkeeper.ui.SDialog;
import cn.sunflyer.simplenetkeeper.ui.WaitingUi;
import cn.sunflyer.simplenetkeeper.util.AndroidTools;
import cn.sunflyer.simpnk.annotation.Config;
import cn.sunflyer.simpnk.annotation.ConfigLoad;
import cn.sunflyer.simpnk.control.AccountController;
import cn.sunflyer.simpnk.control.DialController;
import cn.sunflyer.simpnk.control.Log;
import cn.sunflyer.simpnk.control.MessageHandler;
import cn.sunflyer.simpnk.control.StatusController;
import cn.sunflyer.simpnk.obj.Router;
import cn.sunflyer.simpnk.obj.RouterMecuryTPF;
import cn.sunflyer.simpnk.obj.RouterMecuryTpNew;
import cn.sunflyer.simpnk.obj.RouterTpLinkNew;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Notification;
import android.content.Context;
import android.graphics.Color;
import android.net.wifi.WifiConfiguration;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;

public class AccountActivity extends Activity {

    /**组件库*/
    private Button mButConnect;
    private Button mButInternal;
    private Button mButState;

    /**输入框*/
    @ConfigLoad(req = "sAccName")
    private EditText mEtNkname; //账号名
    @ConfigLoad(req = "sAccPassword")
    private EditText mEtNkpass; //账号密码
    @ConfigLoad(req = "sRouterAdd")
    private EditText mEtRip; //路由器IP
    @ConfigLoad(req = "sRouterAcc")
    private EditText mEtRname; // 路由器用户名
    @ConfigLoad(req = "sRouterPassword")
    private EditText mEtRpass; // 路由器密码
    @ConfigLoad(req = "mCurrentRadiusStr" , sourceClass = "AccountController")
    private EditText mEtNkradius;
    @ConfigLoad(req = "mCurrentRadiusPrefix" , sourceClass = "AccountController")
    private EditText mEtNkprefix;
    /**CheckBox*/
    private CheckBox mCbSave;

    private CheckBox mCbSelfRadius;

    /**状态提示符*/
    private TextView mTvStatus;
    private TextView mTvTop;
    private TextView mTvInfo;

    @ConfigLoad(req = "mCurrentRadius" ,methodName = "setSelection" ,sourceClass = "AccountController" ,type = Config.CONFIG_NUMBER_INT ,methodType = Config.CONFIG_NUMBER_INT)
    private Spinner mRadiusOpt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_account);

        this.mButConnect = (Button)findViewById(R.id.acc_but_set);
        this.mButInternal = (Button)findViewById(R.id.acc_but_internal);
        this.mButState = (Button)findViewById(R.id.acc_but_info);

        this.mEtNkname = (EditText)findViewById(R.id.acc_et_nkname);
        this.mEtNkpass = (EditText)findViewById(R.id.acc_et_nkpass);
        this.mEtRip = (EditText)findViewById(R.id.acc_et_rip);
        this.mEtRname = (EditText)findViewById(R.id.acc_et_rname);
        this.mEtRpass = (EditText)findViewById(R.id.acc_et_rpass);
        this.mEtNkradius = (EditText)findViewById(R.id.acc_et_nkradius);
        this.mEtNkprefix = (EditText)findViewById(R.id.acc_et_nkprefix);

        this.mCbSave = (CheckBox)findViewById(R.id.acc_cb_save);
        this.mCbSelfRadius = (CheckBox)findViewById(R.id.acc_cb_selfradius);

        this.mTvStatus = (TextView)findViewById(R.id.acc_tv_status);
        this.mTvTop = (TextView)findViewById(R.id.acc_tv_top);
        this.mTvInfo = (TextView)findViewById(R.id.acc_tv_info);

        this.mRadiusOpt = (Spinner)findViewById(R.id.acc_spin_radius);



        this.mBlock = new WaitingUi(this);

        this.initComponentEvent();
        this.initComponentData();
        this.initComponentHandler();
        this.setTextColor();

        this.reflectVar();

        this.mRadiusOpt.setSelection(AccountController.mCurrentRadius);

        if(this.getIntent().getBooleanExtra("autodial", false)){
            if(AndroidTools.isWifiNetwork(this)){
                MessageHandler.sendMessage(MessageHandler.MSG_ACTION_DO_DIAL, "");
            }else{
                MessageHandler.sendMessage(MessageHandler.MSG_ACTION_DO_STATE, "现在无法连接，你没有处于无线网络下");
            }
        }

        if(AccountController.mCurrentRadius == AccountController.RADIUS_SELF){
            this.mCbSelfRadius.setChecked(true);
        }
    }

    private void reflectVar(){

        Class<?> cls = AccountActivity.class;
        Field[] pField = cls.getDeclaredFields();
        for(Field x:pField){
            ConfigLoad ic = x.getAnnotation(ConfigLoad.class);
            if(ic != null){
                //私有域允许访问
                if(Modifier.isPrivate(x.getModifiers())){
                    x.setAccessible(true);
                }
                try{
                    Class<?> sourceClass = Class.forName(ConfigLoad.sourcePrefix + ic.sourceClass());
                    //获取配置的值
                    Object sourceVal = sourceClass.getField(ic.req()).get(null);

                    //如果已请求到的数据类型不同于已有数据，则转换
                    if(!ic.methodType().equals(Config.CONFIG_CHARSEQUENCE) && !ic.type().equals(ic.methodType())){
                        if(ic.methodType().equals(Config.CONFIG_BOOLEAN)){
                            sourceVal = ic.reqVal().equals(String.valueOf(sourceVal));
                        }else{
                            String sourceTmp = String.valueOf(sourceVal);
                            sourceVal = Class.forName("java.lang." + ic.methodType()).getMethod("valueOf",String.class).invoke(null , sourceTmp);
                        }
                    }

                    Class<?> dataClass = x.getType();

                    Method dataMethod =  dataClass.getMethod(ic.methodName(), Class.forName("java.lang." + ic.methodType()));
                    dataMethod.invoke(x.get(AccountActivity.this), sourceVal);
                }catch(Exception e){
                    Log.log("Variable Reflection Error : " + e.toString());
                    e.printStackTrace();
                }
            }
        }
    }

    private Handler mMsgHandler;

    /**Handler*/
    private void initComponentHandler(){
        this.mMsgHandler = new Handler(){

            private String mLastRefreshedMsg = "";

            /**
             * 设置事件处理器
             * */
            @Override
            public void handleMessage(Message msg) {

                Context c = AccountActivity.this;

                if(msg != null && msg.getData() != null && !msg.getData().isEmpty()){
                    switch(msg.getData().getInt(MessageHandler.MSG_DATA_ACTION_CODE)){
                        //执行拨号操作
                        case MessageHandler.MSG_ACTION_DO_DIAL:{
                            if(StatusController.getStateRouterDial())
                                MessageHandler.sendMessage(MessageHandler.MSG_ACTION_LOG_AND_REFRESH, "已在尝试执行拨号中，请不要重复点击");
                            else{
                                Log.log("Prefix : " + (AccountController.getCurrentRadiusPrefix().replace("\r","\\r").replace("\n","\\n")) + " , Radius : " + AccountController.getCurrentRadius());
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
                            break;
                        //执行状态显示，主要是发送Toast
                        case MessageHandler.MSG_ACTION_DO_STATE:{
                            String pszData = msg.getData().getString(MessageHandler.MSG_DATA_INFO);
                            AndroidTools.makeToast(c,pszData == null ? "No data" : pszData,true);
                        }break;
                        //记录并刷新状态
                        case MessageHandler.MSG_ACTION_LOG_AND_REFRESH:{
                            String pszData = msg.getData().getString(MessageHandler.MSG_DATA_INFO);
                            pszData = pszData == null ? "记录数据错误：空值" : pszData;
                            Log.log(pszData);
                            MessageHandler.sendMessage(MessageHandler.MSG_ACTION_REFRESH_INFO , pszData);
                        }break;
                        //刷新状态
                        case MessageHandler.MSG_ACTION_REFRESH_INFO:{
                            String pszData = msg.getData().getString(MessageHandler.MSG_DATA_INFO);
                            pszData = pszData == null ? "未知状态" : pszData;
                            mLastRefreshedMsg = pszData;
                            mBlock.setTips(pszData);
                        }break;
                        case MessageHandler.MSG_ACTION_SHOW_DIALOG:{
                            String pszData = msg.getData().getString(MessageHandler.MSG_DATA_INFO);
                            //new AlertDialog.Builder(AccountActivity.this).setTitle("路由器连接信息").setMessage(pszData).setPositiveButton("好的",null).show();
                            SDialog pvTips = new SDialog(AccountActivity.this);
                            if(pvTips != null){
                                //pvTips.setWindowTitle("路由器连接信息").setWindowContent(pszData).setPositiveButton("好的",null).show();
                                pvTips.show();
                                pvTips.setWindowTitle("路由器连接信息").setWindowContent(pszData).setPositiveButton("好的",null);
                            }
                        }break;
                        case MessageHandler.MSG_ACTION_DIAL_COMPLETE:{
                            Log.log("拨号操作完成。");
                            mTvStatus.setText(mLastRefreshedMsg);
                        }break;
                        case MessageHandler.MSG_ACTION_TRACK_COMPLETE:{
                            Log.log("状态追踪完毕。");
                            mTvStatus.setText(mLastRefreshedMsg);
                        }break;
                        default:
                    }
                }
            }
        };

        MessageHandler.setAndroidHandler(mMsgHandler);
    }

    /**组件文本设置*/
    private void initComponentData(){

        //如果属于网页输入密码的状况，则设置用户名为admin
        if(StatusController.getStateRouterAuthMethod() != Router.AUTH_OLD && StatusController.getStateRouterAuthMethod() != Router.AUTH_NOT_AVALIABLE){
            StatusController.sRouterAcc = "admin";
        }

        //如果来自主界面则表示设置内容应该是可用的
        if(this.getIntent().getStringExtra("from").equals("index")){
            this.mTvTop.setText("检查你输入的信息，然后点击设置即可。");
        }else{
            if(this.getIntent().getIntExtra("Reason",1) == 1){
                this.mTvTop.setText("设置向导检测路由器信息失败，请输入你的上网账号和路由器信息后进行设置。");
            }else{
                this.mTvTop.setText("设置向导已检测到路由器部分信息，请输入你的上网账号和路由器管理密码后进行设置。");
            };
        }


    }

    /**组建消息设置*/
    private void initComponentEvent(){
        /**连接按钮事件*/
        this.mButConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(AndroidTools.isWifiNetwork(AccountActivity.this)){
                    if(StatusController.sAccName.equals("") || StatusController.sAccPassword.equals("") || StatusController.sRouterAdd.equals("") || StatusController.sRouterAcc.equals("") || StatusController.sRouterPassword.equals("")){
                        Log.log("拨号设置：存在空值项，不允许设置");
                        MessageHandler.sendMessage(MessageHandler.MSG_ACTION_DO_STATE,"当前输入项目存在空项，不允许操作");
                    }else{
                        if(mCbSave.isChecked()) StatusController.saveConfig(); //如果勾选则保存配置文件
                        MessageHandler.sendMessage(MessageHandler.MSG_ACTION_DO_DIAL,"");
                    }
                }else{
                    MessageHandler.sendMessage(MessageHandler.MSG_ACTION_DO_STATE,"你还没有连接到无线网络！");
                }
            }
        });

        /**内网切换按钮*/
        this.mButInternal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!AndroidTools.isWifiNetwork(AccountActivity.this)){
                    MessageHandler.sendMessage(MessageHandler.MSG_ACTION_DO_STATE , "你还没有连接到无线网络！");
                    return;
                }

                final SDialog pDialog = new SDialog(AccountActivity.this);
                pDialog.show();
                pDialog.setWindowTitle("内网切换操作").setWindowContent("即将切换路由器连接模式至内网，确认继续操作吗？").setNegativeButton("取消",null)
                        .setPositiveButton("确认", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if(StatusController.getStateRouterAuthMethod() == Router.AUTH_WEB || StatusController.getStateRouterAuthMethod() == Router.AUTH_OLD){
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Looper.prepare();
                                            Log.log("内网切换：旧版本固件 - 尝试开始");
                                            MessageHandler.sendMessage(MessageHandler.MSG_ACTION_SHOW_BLOCK, "");
                                            new RouterMecuryTPF().setInternalNet();
                                            MessageHandler.sendMessage(MessageHandler.MSG_ACTION_HIDE_BLOCK,"");
                                        }
                                    }).start();
                                }else{
                                    MessageHandler.sendMessage(MessageHandler.MSG_ACTION_REFRESH_INFO,"然而内网切换暂时不适用于你的路由器>_<");
                                    /**
                                     new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                    Looper.prepare();
                                    Log.log("内网切换：新版本固件 - 尝试开始");
                                    MessageHandler.sendMessage(MessageHandler.MSG_ACTION_SHOW_BLOCK, "");
                                    MessageHandler.sendMessage(MessageHandler.MSG_ACTION_LOG_AND_REFRESH, new RouterMecuryTpNew().setInternal() ? "内网切换：设置成功" : "内网切换：设置出现错误");
                                    MessageHandler.sendMessage(MessageHandler.MSG_ACTION_HIDE_BLOCK,"");
                                    }
                                    }).start();
                                     */
                                }
                                pDialog.dismiss();
                            }
                        });
            }
        });

        /**状态*/
        this.mButState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!AndroidTools.isWifiNetwork(AccountActivity.this)){
                    MessageHandler.sendMessage(MessageHandler.MSG_ACTION_DO_STATE , "你还没有连接到无线网络！");
                    return;
                }

                if(StatusController.getStateRouterAuthMethod() != Router.AUTH_NOT_AVALIABLE){
                    final StringBuffer pSb = new StringBuffer();

                    new Thread(new Runnable() {
                        @Override
                        public void run() {

                            Looper.prepare();

                            MessageHandler.sendMessage(MessageHandler.MSG_ACTION_SHOW_BLOCK,"");

                            switch(StatusController.getStateRouterAuthMethod()){
                                case Router.AUTH_OLD:
                                case Router.AUTH_WEB:
                                case Router.AUTH_PASSWORD_ONLY:{
                                    RouterMecuryTPF pRouter = new RouterMecuryTPF();
                                    pRouter.LoadLinkInfo();
                                    //pRouter.LoadPPPoEInf();
                                    //首页获取的链路信息
                                    String[] szgLinkInfo = pRouter.getLinkInfoWan();
                                    //拨号页面获取的详细信息
                                    //String[] szgPPPoEInfo = pRouter.getPPPoEInf();
                                    try{
                                        //拼接
                                        //pSb.append("WAN口连接信息\n---------------------\n");
                                        pSb.append("外网连接状态 ： "+ (szgLinkInfo[13].equals("1") ? "已经连接" : "未连接") + "\n");
                                        pSb.append("MAC 地址 : " + szgLinkInfo[1] + "\n");
                                        pSb.append("IP 地址 : " + szgLinkInfo[2] + "\n");
                                        pSb.append("子网掩码 ： " + szgLinkInfo[4] + "\n");
                                        pSb.append("网关地址 ： " + szgLinkInfo[7] + "\n");
                                        pSb.append("DNS服务器 ： "+ szgLinkInfo[11] + "\n");
                                        pSb.append("在线时间 ： " + szgLinkInfo[12] + "\n");
                                        pSb.append("---------------------");

                                    }catch(Exception e){
                                        pSb.append("\n当前信息抓取错误，可能是因为数据有误，或者你输入的路由器信息错误\n");
                                    }

                                    MessageHandler.sendMessage(MessageHandler.MSG_ACTION_SHOW_DIALOG , pSb.toString());
                                }
                                break;
                                case Router.AUTH_DYNAMIC_ID:{
                                    RouterMecuryTpNew pRouter = new RouterMecuryTpNew();
                                    pRouter.loadStatus();
                                    Hashtable<String,String> pStatus = pRouter.getState();
                                    if(pStatus == null){
                                        MessageHandler.sendMessage(MessageHandler.MSG_ACTION_LOG_AND_REFRESH , "查询连接状态出现错误。");
                                        return;
                                    }

                                    StringBuffer pSb = new StringBuffer();
                                    try{
                                        //pSb.append("WAN口连接信息\n---------------------\n");
                                        pSb.append("连接状态："  + (pStatus.get("00000") == null ? "状态错误" : "状态正常") + "\n");
                                        pSb.append("当前状态：" + RouterMecuryTpNew.LINK_STATUS[Integer.parseInt(pStatus.get("status"))] + "\n");
                                        pSb.append("错误状态：" + RouterMecuryTpNew.LINK_CODE[Integer.parseInt(pStatus.get("code"))] + "\n");
                                        pSb.append("IP 地址：" + pStatus.get("ip") + "\n");
                                        pSb.append("网关地址：" + pStatus.get("gateway") + "\n");
                                        pSb.append("子网掩码：" + pStatus.get("mask") + "\n");
                                        pSb.append("主DNS地址：" + pStatus.get("dns0") + "\n");
                                        pSb.append("次DNS地址：" + pStatus.get("dns1") + "\n");

                                        long pOnlineTime = Long.valueOf(pStatus.get("upTime") == null ? "0" : pStatus.get("upTime")) / 100,
                                                pOnlineTimeHour = pOnlineTime / 60 / 60 ,
                                                pOnlineTimeMin = ( pOnlineTime - pOnlineTimeHour * 60 * 60 ) / 60,
                                                pOnlineTimeSec = pOnlineTime - pOnlineTimeHour * 60 * 60 - pOnlineTimeMin * 60;

                                        pSb.append("在线时间：" + pOnlineTimeHour + " 小时 " + pOnlineTimeMin + " 分钟 " + pOnlineTimeSec + "秒\n");
                                        pSb.append("上传/下载数据包：\n" + pStatus.get("outPkts") + "/" + pStatus.get("inPkts") + "\n");

                                        String pOctetsIn = pStatus.get("inOctets") , pOctetsOut = pStatus.get("outOctets");

                                        pSb.append("上传/下载数据量：\n" + Long.valueOf(pOctetsOut == null ? "0" : pOctetsOut) / 1024 / 1024 + "MB /" + Long.valueOf(pOctetsIn == null ? "0" : pOctetsIn) / 1024 / 1024 + "MB\n");
                                        String pRatesIn = pStatus.get("inRates");
                                        String pRatesOut = pStatus.get("outRates");
                                        pSb.append("当前上传速度：" + ( pRatesOut == null ? "没有获取到" : (Integer.parseInt(pRatesOut) / 1024 / 8) + "KB/s")+ "\n");
                                        pSb.append("当前下载速度：" + ( pRatesIn == null ? "没有获取到" : (Integer.parseInt(pRatesIn) / 1024 / 8) + "KB/s") + "\n");
                                        pSb.append("请注意：上述流量数据会因为路由器固件版本原因出现差别，因而有可能会没有数据显示，在此情况下可以通过更新路由器固件后查看\n");
                                    }catch(Exception e){
                                        pSb.append("\n当前信息抓取错误，可能是因为数据有误，或者你输入的路由器信息错误\n");
                                    }

                                    MessageHandler.sendMessage(MessageHandler.MSG_ACTION_SHOW_DIALOG , pSb.toString());

                                }break;
                                case Router.AUTH_DYNAMIC_ID_TP:{
                                    RouterTpLinkNew router = new RouterTpLinkNew();
                                    HashMap<String,String> pStatus = router.getStatus();
                                    if(pStatus == null){
                                        MessageHandler.sendMessage(MessageHandler.MSG_ACTION_LOG_AND_REFRESH , "查询连接状态出现错误。");
                                        return;
                                    }
                                    StringBuffer pSb = new StringBuffer();
                                    try{
                                        pSb.append("连接类型：" + pStatus.get("proto") + "\n");
                                        pSb.append("当前状态：" + RouterMecuryTpNew.LINK_STATUS[Integer.parseInt(pStatus.get("link_status"))] + "\n");
                                        pSb.append("错误状态：" + RouterMecuryTpNew.LINK_CODE[Integer.parseInt(pStatus.get("error_code"))] + "\n");
                                        pSb.append("IP地址 ：" + pStatus.get("ipaddr") + "\n");
                                        pSb.append("网关地址：" + pStatus.get("gateway") + "\n");
                                        pSb.append("子网掩码：" + pStatus.get("netmask") + "\n");
                                        pSb.append("主DNS地址：" + pStatus.get("pri_dns") + "\n");
                                        pSb.append("次DNS地址：" + pStatus.get("snd_dns") + "\n");
                                    }catch(Exception e){
                                        pSb.append("\n当前信息抓取错误，可能是因为数据有误，或者你输入的路由器信息错误\n");
                                    }
                                    MessageHandler.sendMessage(MessageHandler.MSG_ACTION_SHOW_DIALOG , pSb.toString());

                                }break;
                                default:
                            }

                            MessageHandler.sendMessage(MessageHandler.MSG_ACTION_HIDE_BLOCK,"");
                        }
                    }).start();

                }else{
                    MessageHandler.sendMessage(MessageHandler.MSG_ACTION_DO_STATE , "当前状态查询功能目前不适用于当前路由器");
                }
            }
        });

        /**输入框*/
        this.mEtNkname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }
            @Override
            public void afterTextChanged(Editable s) {
                StatusController.sAccName = mEtNkname.getText().toString();
            }
        });

        this.mEtNkpass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }
            @Override
            public void afterTextChanged(Editable s) {
                StatusController.sAccPassword = mEtNkpass.getText().toString();
            }
        });

        this.mEtRip.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                StatusController.sRouterAdd = mEtRip.getText().toString();
            }
        });

        this.mEtRname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                StatusController.sRouterAcc = mEtRname.getText().toString();
            }
        });

        this.mEtRpass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                StatusController.sRouterPassword = mEtRpass.getText().toString();
            }
        });

        //Radius更改
        this.setRadiusSelectionStyle();

        this.mEtNkprefix.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override  public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) {
                AccountController.mCurrentRadiusPrefix = mEtNkprefix.getText().toString();
            }
        });

        this.mEtNkradius.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) {
                AccountController.mCurrentRadiusStr = mEtNkradius.getText().toString();
            }
        });

        this.mCbSelfRadius.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    if(AccountController.mCurrentRadius != AccountController.RADIUS_SELF){
                        final SDialog pDialog = new SDialog(AccountActivity.this);
                        pDialog.show();
                        pDialog.setWindowContent("你选择了使用自定义的Radius来处理账户。\n请注意：在这种情况下，你需要自己提供用于Netkeeper加密的Radius(或称ShareKey)以及前缀(例如\\r\\n)，否则你将由于账号错误无法正常使用设置功能。\n\n请在“设置Radius”填写你所在地区的Radius值\n在“设置Prefix”填写账号的前缀（如果你不知道前缀，请填写\\r\\n，目前只支持对\\r和\\n的自动转义）").setWindowTitle("使用自定义Radius").setPositiveButton("确认", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                pDialog.dismiss();
                            }
                        });
                        AccountController.mCurrentRadius = AccountController.RADIUS_SELF;
                        Log.log("已切换使用地区，并设置为使用自定义Radius和Prefix");
                    }
                    mEtNkradius.setVisibility(View.VISIBLE);
                    mEtNkprefix.setVisibility(View.VISIBLE);
                    mRadiusOpt.setVisibility(View.GONE);
                }else{
                    mEtNkradius.setVisibility(View.GONE);
                    mEtNkprefix.setVisibility(View.GONE);
                    mRadiusOpt.setVisibility(View.VISIBLE);
                    mRadiusOpt.setSelection((AccountController.mCurrentRadius = 0));
                }
            }
        });
    }

    private void setRadiusSelectionStyle(){

        ArrayAdapter<CharSequence> pListData = new ArrayAdapter<CharSequence>(this ,android.R.layout.simple_spinner_item ,
                Arrays.asList(AccountController.RADIUS_NAME));
        this.mRadiusOpt.setAdapter(pListData);
        pListData.setDropDownViewResource(R.layout.spinner_list);
        this.mRadiusOpt.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                AccountController.mCurrentRadius = position;
                Log.log("已修改使用地区：" + AccountController.RADIUS_NAME[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

    }

    /**设置提示文本颜色*/
    private void setTextColor(){
        String pszInfo = this.mTvInfo.getText().toString();

        SpannableStringBuilder pSSB = new SpannableStringBuilder(pszInfo);

        String []pKeywordRed = {"此时拨号连接会被断开",
                "“内网切换”目前仅对2014年上半年及以前的水星/TP/FAST路由器有效",
                "路由器密码是你登录路由器设置界面所需要的密码，而不是你的Wifi密码"};
        String []pKeywordGreen = {"使用说明","如果你使用了设置向导","管理员用户名和路由器地址是不需要","保持默认即可",
                "切换内网","当前状态","可以查询路由器当前连接状态",
                "如果勾选了保存设置","自动载入这些数据","如果在设置路由器过程中出现了问题"};

        for(String x:pKeywordRed){
            int pPath = pszInfo.indexOf(x);
            if(pPath == -1) continue;
            int pPathEnd = pPath + x.length();
            pSSB.setSpan(new ForegroundColorSpan(Color.RED),pPath,pPathEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        for(String x:pKeywordGreen){
            int pPath = pszInfo.indexOf(x);
            if(pPath == -1) continue;
            int pPathEnd = pPath + x.length();
            pSSB.setSpan(new ForegroundColorSpan(Color.GREEN),pPath,pPathEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        this.mTvInfo.setText(pSSB);
    }



    /**UI遮挡显示*/
    private WaitingUi mBlock;

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            this.mBlock.dismiss();
            this.finish();
        }
        return true;
    }

}
