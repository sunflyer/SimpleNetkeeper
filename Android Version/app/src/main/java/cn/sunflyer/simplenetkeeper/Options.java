package cn.sunflyer.simplenetkeeper;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.File;
import java.util.Collections;

import cn.sunflyer.simplenetkeeper.ui.SDialog;
import cn.sunflyer.simplenetkeeper.ui.WaitingUi;
import cn.sunflyer.simplenetkeeper.util.AndroidTools;
import cn.sunflyer.simpnk.control.Log;
import cn.sunflyer.simpnk.control.MessageHandler;


public class Options extends Activity {

    private TextView mTvVersion;
    private Button mButUpdate;
    private Button mButLog;
    private Button mButBlog;
    private Button mButGroup;

    private Handler mMsgHandler;
    private WaitingUi mBlock;

    private String mLatestAdd = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);

        this.initComponent();
        this.initComponentEvent();
    }

    /**初始化组件*/
    private void initComponent(){
        this.mTvVersion = (TextView)findViewById(R.id.opt_about_ver);
        this.mButUpdate = (Button)findViewById(R.id.opt_but_update);
        this.mButLog = (Button)findViewById(R.id.opt_but_report);
        this.mButBlog = (Button)findViewById(R.id.opt_but_blog);
        this.mButGroup = (Button)findViewById(R.id.opt_but_group);

        mMsgHandler = new Handler(){
            @Override
            public void handleMessage(Message msg){
                switch(msg.getData().getInt(MessageHandler.MSG_DATA_ACTION_CODE)){

                    case MessageHandler.MSG_ACTION_SHOW_BLOCK:
                        mBlock.show();
                        break;
                    case MessageHandler.MSG_ACTION_HIDE_BLOCK:
                        mBlock.hide();
                        break;
                    case MessageHandler.MSG_ACTION_DO_STATE:{
                        String pszData = msg.getData().getString(MessageHandler.MSG_DATA_INFO);
                        mTvVersion.setText(pszData);
                    }break;
                    //记录并刷新状态
                    case MessageHandler.MSG_ACTION_LOG_AND_REFRESH:{
                        String pszData = msg.getData().getString(MessageHandler.MSG_DATA_INFO);
                        makeToast(pszData,true);
                        cn.sunflyer.simpnk.control.Log.log(pszData);
                    }break;
                    //刷新状态
                    case MessageHandler.MSG_ACTION_REFRESH_INFO:{
                        String pszData = msg.getData().getString(MessageHandler.MSG_DATA_INFO);
                        makeToast(pszData,true);
                    }break;
                    case MessageHandler.MSG_ACTION_SHOW_DIALOG:{
                        String pszData = msg.getData().getString(MessageHandler.MSG_DATA_INFO);
                        SDialog pDialog = new SDialog(Options.this);
                        pDialog.show();
                        pDialog.setWindowTitle("发现有新的版本").setWindowContent("检测到应用程序的更新版本，你要现在下载它吗？").setPositiveButton("立即下载", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                AndroidTools.openBrowser(Options.this , mLatestAdd);
                            }
                        }).setNegativeButton("不用了，谢谢",null).setOnTouchOutsideCloseWindow(false);
                    }break;
                    case MessageHandler.MSG_ACTION_UPDATE:{

                    }break;
                    case MessageHandler.MSG_ACTION_UPLOAD_LOG:

                        break;
                    default:
                }

            }
        };

        mBlock = new WaitingUi(this);
    }

    /**初始化组件事件*/
    private void initComponentEvent(){
        this.mTvVersion.setText("当前版本：" + AndroidTools.getApplicationVersionName(this));

        this.mButUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int pVerBuild = AndroidTools.getApplicationVersionBuild(Options.this);
                if (pVerBuild == -1) {
                    sendMessage(MessageHandler.MSG_ACTION_LOG_AND_REFRESH, "获取当前程序版本号出现错误。检查更新失败。");
                } else {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Looper.prepare();
                            sendMessage(MessageHandler.MSG_ACTION_SHOW_BLOCK, "");
                            String pRes = null;

                            NameValuePair pDataPair = new BasicNameValuePair("data", "{\"act\":1,\"data\":{\"name\":\"simpnk-android\",\"version\":" + pVerBuild + "}}");
                            pRes = AndroidTools.postToUrl("http://sunflyer.cn/shares/simpnk.php?act=1&name=simpnk-android&version=" + pVerBuild, Collections.singletonList(pDataPair));

                            if (pRes != null && !pRes.trim().equals("")) {
                                Log.log("程序更新 - 从服务器返回的数据为 ： " + pRes);
                                if (pRes.equals("noupdate") || pRes.indexOf("http") != 0) {
                                    sendMessage(MessageHandler.MSG_ACTION_LOG_AND_REFRESH, "应用程序当前版本已是最新！");
                                } else {
                                    mLatestAdd = pRes;
                                    sendMessage(MessageHandler.MSG_ACTION_DO_STATE, "当前版本：" + AndroidTools.getApplicationVersion(Options.this) + " 更新版本可用！");
                                    sendMessage(MessageHandler.MSG_ACTION_SHOW_DIALOG, "更新版本可用，是否立即下载更新？");
                                }
                            } else {
                                sendMessage(MessageHandler.MSG_ACTION_LOG_AND_REFRESH, "请求网络数据出现错误。");
                            }
                            sendMessage(MessageHandler.MSG_ACTION_HIDE_BLOCK, "");
                        }
                    }).start();
                }
            }
        });

        this.mButLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(new File(Log.gLogFilePathAndroid + "/" + Log.gLogFileName).exists()){

                    final EditText pMail = new EditText(Options.this);
                    AlertDialog.Builder pAlert = new AlertDialog.Builder(Options.this);
                    pAlert.setView(pMail).setPositiveButton("确认", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            final String pMailAdd = pMail.getText().toString();
                            if(pMailAdd.matches("\\w+@(\\w+.)+[a-z]{2,3}")){
                                sendMessage(MessageHandler.MSG_ACTION_SHOW_BLOCK , "");
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Looper.prepare();
                                        String pData = AndroidTools.urlencodeFile(Log.gLogFilePathAndroid + "/" + Log.gLogFileName);
                                        if(pData != null){
                                            String pRes = null;
                                            NameValuePair pDataPair = new BasicNameValuePair("data","{\"act\":2,\"data\":{\"name\":\"simpnk-android\",\"version\":\"\"" + AndroidTools.getApplicationVersionBuild(Options.this) + "\",\"mail\":\"" + pMailAdd + "\",\"info\":\"" + pData + "\"}}");
                                            pRes = AndroidTools.postToUrl("http://nk.sunflyer.cn/shares/simpnk.php", Collections.singletonList(pDataPair));

                                            sendMessage(MessageHandler.MSG_ACTION_LOG_AND_REFRESH , (pRes == null ? "请求发送出现了一些错误。" : (pRes.equals("true")) ? "文件上传成功。感谢你的帮助。" : "文件上传失败"));
                                        }else{
                                            sendMessage(MessageHandler.MSG_ACTION_LOG_AND_REFRESH , "编码文件出现异常。上传已被停止");
                                        }
                                        sendMessage(MessageHandler.MSG_ACTION_HIDE_BLOCK , "");
                                    }
                                }).start();
                            }else{
                                makeToast("输入的电子邮件地址不合法",true);
                            }
                        }
                    }).setNegativeButton("取消",null).setTitle("发送错误记录").setMessage("这个操作将发送你的操作错误记录文件，但是我在此承诺错误记录文件中不包含任何隐私数据。（有可能会记录操作的宽带账号名称，但也仅仅是账号名称，且我不会透露给任何人。）\n文件上传需要一些时间，请耐心等待。谢谢。\n请输入你的电子邮件地址，以便回复：").show();

                }else{
                    makeToast("无法发送错误日志，文件不存在", true);
                }
            }
        });

        this.mButBlog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final SDialog pDialog = new SDialog(Options.this);
                pDialog.show();
                pDialog.setWindowTitle("打开浏览器").setWindowContent("即将跳转到我的个人博客，这将会打开你的浏览器\n建议在WiFi网络下访问").setPositiveButton("我去看看", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AndroidTools.openBrowser(Options.this, "https://sunflyer.cn");
                        pDialog.dismiss();
                    }
                }).setNegativeButton("不用了，谢谢", null);
            }
        });

        this.mButGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final SDialog pDialog = new SDialog(Options.this);
                pDialog.show();
                pDialog.setWindowTitle("打开浏览器").setWindowContent("即将跳转到Crazy For Code Studio官网，这将会打开你的浏览器\n建议在WiFi网络下访问").setPositiveButton("我去看看", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AndroidTools.openBrowser(Options.this , "http://www.crazyforcode.org");
                        pDialog.dismiss();
                    }
                }).setNegativeButton("不用了，谢谢",null);
            }
        });
    }

    private void makeToast(CharSequence szText,boolean isShort){
        Toast.makeText(this, szText, isShort ? Toast.LENGTH_SHORT : Toast.LENGTH_LONG).show();
    }

    private  void sendMessage(int pActCode,String pMsg){
        Message pMsgToSend = new Message();
        Bundle pMsgBundle = new Bundle();

        //添加数据
        pMsgBundle.putInt(MessageHandler.MSG_DATA_ACTION_CODE , pActCode);
        pMsgBundle.putString(MessageHandler.MSG_DATA_INFO , pMsg);
        pMsgToSend.setData(pMsgBundle);
        mMsgHandler.sendMessage(pMsgToSend);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_options, menu);
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

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            this.mBlock.dismiss();
            this.finish();
        }
        return true;
    }
}
