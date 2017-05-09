package cn.sunflyer.simplenetkeeper;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Arrays;

import cn.sunflyer.simplenetkeeper.ui.SDialog;
import cn.sunflyer.simplenetkeeper.ui.WaitingUi;
import cn.sunflyer.simplenetkeeper.util.AndroidTools;
import cn.sunflyer.simpnk.control.AccountController;
import cn.sunflyer.simpnk.control.Log;
import cn.sunflyer.simpnk.control.MessageHandler;
import cn.sunflyer.simpnk.control.StatusController;
import cn.sunflyer.simpnk.obj.Router;


public class FirstRun extends Activity {

    private TextView mTvGuide;
    private Button mButStart;
    private Spinner mManu;

    private boolean loadCheck = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_run);

        this.mTvGuide = (TextView)findViewById(R.id.guide_tv_text);
        this.mButStart = (Button)findViewById(R.id.guide_but_start);
        this.mManu = (Spinner)findViewById(R.id.guide_spin_manu);
        this.setGuideText(this.mTvGuide);
        this.initComponent();
    }

    /**初始化组件*/
    private void initComponent(){
        this.mButStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final WaitingUi pUi = new WaitingUi(FirstRun.this);
                pUi.show();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Looper.prepare();

                        if ((AndroidTools.isWifiNetwork(FirstRun.this) && AndroidTools.getWifiName(FirstRun.this) != null)) {
                            Log.log("设置向导：已连接到无线网络 - " + AndroidTools.getWifiName(FirstRun.this));

                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    Looper.prepare();
                                    String pServerIp = AndroidTools.getMaskAddress(FirstRun.this);
                                    if (!pServerIp.equals("0.0.0.0")) {
                                        Log.log("设置向导：已发现服务器地址 - " + pServerIp);
                                        StatusController.sRouterAdd = pServerIp;
                                        if(loadCheck){
                                            int pConnectionMethod = Router.getRouterAccessMethod(pServerIp);
                                            if (pConnectionMethod != Router.AUTH_NOT_AVALIABLE) {
                                                StatusController.setStateRouterAuthMethod(pConnectionMethod);

                                                Log.log("设置向导：成功地检查了路由器的可用性 - " + pConnectionMethod);
                                                pUi.cancel();
                                                //启动账号密码
                                                mMsgMakeToast("检查可用性完毕，请按照提示输入必要信息后继续");

                                                mMsgStartConfig(0);
                                            } else {
                                                mMsgMakeToastAndLog("尝试检查出现错误，可能不支持你的路由器，详情请查看NetkeeperLog(检查到的路由器IP为" + pServerIp + ")");
                                                mMsgShowErrorDialog();
                                                pUi.cancel();
                                            }
                                        }else{
                                            mMsgMakeToastAndLog("你已经选择了“" + Router.ROUTER_MANU[mManu.getSelectedItemPosition()] + "”固件模式，请输入账号信息然后继续操作。");
                                            pUi.cancel();
                                            mMsgStartConfig(0);
                                        }
                                    } else {
                                        mMsgMakeToastAndLog("尝试检查出现错误，没有获取到路由器的地址。");
                                        pUi.cancel();
                                    }

                                }
                            }).start();

                        } else {
                            mMsgMakeToast("请连接到无线网络，然后重试");
                            pUi.cancel();
                            Log.log("设置向导：没有检测到已连接的无线网络");
                        }
                    }
                }).start();
            }
        });

        this.setManufactorSelection();
    }



    private void setManufactorSelection(){
        ArrayAdapter<CharSequence> pListData = new ArrayAdapter<CharSequence>(this ,android.R.layout.simple_spinner_item ,
                Arrays.asList(Router.ROUTER_MANU));
        this.mManu.setAdapter(pListData);
        pListData.setDropDownViewResource(R.layout.spinner_list);
        this.mManu.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 1) { //新版本TP
                    loadCheck = false;
                    StatusController.setStateRouterAuthMethod(Router.AUTH_DYNAMIC_ID_TP);
                }else{
                    loadCheck = true;
                    StatusController.setStateRouterAuthMethod(Router.AUTH_NOT_AVALIABLE);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    public static final String MSG_ACTION = "action";
    public static final int MSG_ACTION_TOAST = 1;
    public static final int MSG_ACTION_START_CONFIG = 2;

    public static final String MSG_DATA_STRING = "str";

    private void mMsgStartConfig(int reasonid){
        Message pMsg = new Message();
        Bundle pData = new Bundle();
        pData.putInt(MSG_ACTION,MSG_ACTION_START_CONFIG);
        pData.putInt("Reason",reasonid);
        pMsg.setData(pData);
        mMsgHandler.sendMessage(pMsg);
    }

    private void mMsgMakeToast(CharSequence x){
        Message pMsg = new Message();
        Bundle pData = new Bundle();
        pMsg.setData(pData);
        pData.putInt(MSG_ACTION, MSG_ACTION_TOAST);
        pData.putString(MSG_DATA_STRING, x.toString());
        mMsgHandler.sendMessage(pMsg);
    }

    private void mMsgShowErrorDialog(){
        Message pMsg = new Message();
        Bundle pData = new Bundle();
        pMsg.setData(pData);
        pData.putInt(MSG_ACTION, MessageHandler.MSG_ACTION_SHOW_DIALOG);
        mMsgHandler.sendMessage(pMsg);
    }

    private void mMsgMakeToastAndLog(CharSequence x){
        Message pMsg = new Message();
        Bundle pData = new Bundle();
        pData.putInt(MSG_ACTION, MessageHandler.MSG_ACTION_LOG_AND_REFRESH);
        pData.putString(MSG_DATA_STRING,x.toString());
        pMsg.setData(pData);
        mMsgHandler.sendMessage(pMsg);
    }

    private Handler mMsgHandler = new Handler(){

        @Override
        public void handleMessage(Message msg){
            switch(msg.getData().getInt(MSG_ACTION)){
                case MSG_ACTION_TOAST:
                    showToast(msg.getData().getString(MSG_DATA_STRING));
                    break;
                case MSG_ACTION_START_CONFIG:
                    Intent pActIntent = new Intent();
                    pActIntent.putExtra("from","guide");
                    pActIntent.putExtra("Reason",msg.getData().getInt("Reason"));
                    pActIntent.setClass(FirstRun.this,AccountActivity.class);

                    startActivity(pActIntent);
                    FirstRun.this.finish();
                    break;
                case MessageHandler.MSG_ACTION_LOG_AND_REFRESH:
                    String pData = msg.getData().getString(MSG_DATA_STRING);
                    Log.log(pData);
                    mMsgMakeToast(pData);
                    break;
                case MessageHandler.MSG_ACTION_SHOW_DIALOG:

                    final SDialog pDialog = new SDialog(FirstRun.this);
                    pDialog.show();
                    pDialog.setOnTouchOutsideCloseWindow(false).setWindowTitle("检测操作异常").setWindowContent("我们没有检测到合适于你的路由器的配置方式，可能是由于其中出现了一些兼容性问题（部分旧版本路由器固件可能会触发这个问题，但仍然是可以被设置的）。\n" +
                            "如果需要尝试是否真正可用，请点击“继续尝试”，手动输入相关信息后点击设置看是否可用。\n" +
                            "否则，请点击“取消”。").setPositiveButton("继续尝试", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mMsgStartConfig(1);
                            pDialog.dismiss();
                        }
                    }).setNegativeButton("算了吧", null);
                    break;
            }
        }

    };

    private void showToast(CharSequence x){
        AndroidTools.makeToast(this, x, true);
    }

    /**设置提示文字*/
    private void setGuideText(TextView c){
        if(c!=null){
            String pStr = c.getText().toString();
            String pKeywordRed[] = {"在准备设置向导之前，请您确认","本软件目前适用于","不是WIFI密码，而是你登录路由器管理界面的密码！",
                    "而不是空密码","这台设备已经连接到了你路由器的无线网络","对于使用新版本固件的路由器",
                    "高级设置","网络设置","上网方式","宽带拨号上网","拨号方式","手动拨号"};
            String pKeywordGreen[] = {"欢迎使用Simple Netkeeper","路由器的WAN口与墙上的网线已接好","路由器的管理密码","你已经设置好",
                    "路由器的WIFI功能已经配置完毕","在确认以上操作执行完成后，请在下方选择你的路由器型号，然后点击按钮开始操作",
                    "个人建议关闭路由器的WPS功能，因为WPS功能容易导致蹭网发生"};

            SpannableStringBuilder pSSB = new SpannableStringBuilder(pStr);

            for(String x: pKeywordRed){
                int pPath = pStr.indexOf(x);
                if(pPath == -1) continue;
                int pPathEnd = pPath + x.length() ;
                pSSB.setSpan(new ForegroundColorSpan(Color.RED) , pPath, pPathEnd , Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }

            for(String x: pKeywordGreen){
                int pPath = pStr.indexOf(x);
                if(pPath == -1) continue;
                int pPathEnd = pPath + x.length() ;
                pSSB.setSpan(new ForegroundColorSpan(Color.GREEN) , pPath, pPathEnd , Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }

            c.setText(pSSB);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_first_run, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
