package cn.sunflyer.simplenetkeeper;

import android.content.Intent;
import android.graphics.Color;
import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import cn.sunflyer.simplenetkeeper.util.AndroidTools;
import cn.sunflyer.simpnk.annotation.Config;
import cn.sunflyer.simpnk.control.ConfigController;
import cn.sunflyer.simpnk.control.DialController;
import cn.sunflyer.simpnk.control.Log;
import cn.sunflyer.simpnk.control.MessageHandler;
import cn.sunflyer.simpnk.control.StatusController;


public class ConfigRouter extends Activity {

    //控件组
    private Button mButConnect ;
    private Button mButGuide;
    private TextView mTvConnect;
    private TextView mTvGuide;
    private Button mButHelp;
    private Button mButOnTimeDial;
    private Button mButChangeInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_config_router);

        ConfigController.setConfigPath(this.getFilesDir().toString());
        //加载配置文件状态
        StatusController.initStatus(this.getFilesDir().toString());

        this.initComponent();
        this.initComponentEvent();
        this.initStateHandler();
    }


    /**
     * 初始化组件连接
     * */
    private void initComponent(){
        this.mButConnect = (Button)findViewById(R.id.but_connect);
        this.mButGuide = (Button)findViewById(R.id.but_guide);
        this.mTvConnect = (TextView)findViewById(R.id.str_main_tip_connect);
        this.mTvGuide = (TextView)findViewById(R.id.main_view_tip_guide);
        this.mButHelp = (Button)findViewById(R.id.but_about);
        this.mButOnTimeDial=(Button)findViewById(R.id.but_ontime);
        this.mButChangeInfo = (Button)findViewById(R.id.but_changeinfo);

        if(!StatusController.isConfigExists){
            this.mTvConnect.setVisibility(View.INVISIBLE);
            this.mButConnect.setEnabled(false);
            this.mButConnect.setBackgroundColor(Color.GRAY);
            this.mButChangeInfo.setVisibility(View.INVISIBLE);
            this.mTvGuide.setText("这是你首次使用，请从设置向导开始");
        }else{
            this.mTvGuide.setText("Wifi状态：" + (!AndroidTools.isWifiNetwork(this) ? "未连接到无线网络" : "已连接到无线网络 " + AndroidTools.getWifiName(this)));
        }
    }

    /**
     * 初始化组件的事件
     * */
    private void initComponentEvent(){

        //一键连接的操作
        this.mButConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pActIntent = new Intent();
                pActIntent.setClass(ConfigRouter.this,AccountActivity.class);
                pActIntent.putExtra("from", "index");
                pActIntent.putExtra("autodial",true);
                startActivity(pActIntent);
            }
        });

        this.mButGuide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pActIntent = new Intent();
                pActIntent.setClass(ConfigRouter.this, FirstRun.class);
                pActIntent.putExtra("isFirstRun", !StatusController.isConfigExists);
                startActivity(pActIntent);
            }
        });

        this.mButOnTimeDial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pActIntent = new Intent();
                pActIntent.setClass(ConfigRouter.this,OntimeDial.class);
                pActIntent.putExtra("isFirstRun",!StatusController.isConfigExists);
                startActivity(pActIntent);
            }
        });

        this.mButChangeInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pActIntent = new Intent();
                pActIntent.setClass(ConfigRouter.this, AccountActivity.class);
                pActIntent.putExtra("from", "index");
                startActivity(pActIntent);
            }
        });

        this.mButHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pActIntent = new Intent();
                pActIntent.setClass(ConfigRouter.this, Options.class);
                pActIntent.putExtra("from", "index");
                startActivity(pActIntent);
            }
        });
    }

    /**
     * 初始化处理程序
     * */
    private void initStateHandler(){

    }


}
