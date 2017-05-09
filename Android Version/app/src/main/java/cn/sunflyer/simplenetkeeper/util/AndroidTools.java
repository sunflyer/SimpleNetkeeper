package cn.sunflyer.simplenetkeeper.util;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.net.URLEncoder;
import java.util.List;


import cn.sunflyer.simpnk.control.Base64;
import cn.sunflyer.simpnk.control.Log;

/**
 * Created by 陈耀璇 on 2015/4/18.
 */
public class AndroidTools {

    /**获取WIFI名称*/
    public static String getWifiName(Context c){
        if(c!=null){
            try{
                String ssid = getWifiInfo(c).getSSID();
                return ssid.substring(1 , ssid.length() - 1);
            }catch(NullPointerException e){
                Log.log("尝试获取WIFI信息时出现空指针错误");
            }catch(Exception e){

            }
        }
        return null;
    }

    /**获取WIFI信息*/
    public static WifiInfo getWifiInfo(Context c){
        if(c!=null){
            WifiManager pWifiMgr = (WifiManager)c.getSystemService(Context.WIFI_SERVICE);
            if(pWifiMgr!=null){
                return pWifiMgr.getConnectionInfo();
            }
        }
        return null;
    }

    /**获取网关地址*/
    public static String getMaskAddress(Context c){
        if(c != null){
            WifiManager pWifiMgr = (WifiManager)c.getSystemService(Context.WIFI_SERVICE);
            DhcpInfo pDhcp = pWifiMgr.getDhcpInfo();
            return long2ip(pDhcp.serverAddress);
        }
        return null;
    }

    /**版本名称*/
    public static int getApplicationVersion(Context c){
        PackageManager pPm = c.getPackageManager();
        try {
            PackageInfo pPi = pPm.getPackageInfo(c.getPackageName() , 0);
            return pPi.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**版本Build*/
    public static int getApplicationVersionBuild(Context c){
        return getApplicationVersion(c);
        /*
            try{
                String pVerName = c.getPackageManager().getPackageInfo(c.getPackageName() , 0).versionName;
                return Integer.parseInt(pVerName.substring(pVerName.lastIndexOf('.')).replace(".",""));
            }catch(Exception e){
                Log.log("版本号获取错误："+e.getMessage());
            }

        return -1;
        */
    }

    public static String getApplicationVersionName(Context c){
        try{
            return c.getPackageManager().getPackageInfo(c.getPackageName() , 0).versionName;
        }catch(Exception e){

        }
        return null;
    }

    /**POST数据到指定URL*/
    public static String postToUrl(String url,String data){
        try{

            HttpClient pHc = new DefaultHttpClient();
            HttpPost pHPost = new HttpPost(url);
            StringEntity pStrEntity = new StringEntity(data,"UTF-8");
            pStrEntity.setContentType("text/plain");
            pHPost.setEntity(pStrEntity);
            pHPost.setHeader("Content-Type", "text/plain;charset=UTF-8");
            pHPost.setHeader("User-Agent","Sunflyer Application - Simple Netkeeper Android");

            HttpResponse pHres = pHc.execute(pHPost);

            return getContent(pHres.getEntity().getContent());

        }catch(Exception e){
            Log.log("网络请求 - POST ： 发送到 " + url + " 的请求数据出现异常\n");
            Log.logE(e);
        }
        return null;
    }

    /**POST指定数据*/
    public static String postToUrl(String url,List<NameValuePair> list){
        try{

            HttpClient pHc = new DefaultHttpClient();
            HttpPost pHPost = new HttpPost(url);

            UrlEncodedFormEntity pUEFE = new UrlEncodedFormEntity(list);
            pUEFE.setContentType("text/plain");
            pHPost.setEntity(pUEFE);
            pHPost.setHeader("Content-Type", "text/plain;charset=UTF-8");
            pHPost.setHeader("User-Agent","Sunflyer Application - Simple Netkeeper Android");

            HttpResponse pHres = pHc.execute(pHPost);
            return getContent(pHres.getEntity().getContent());

        }catch(Exception e){
            Log.log("网络请求 - POST ： 发送到 " + url + " 的请求数据出现异常\n");
            Log.logE(e);
        }
        return null;
    }

    public static String getContent(InputStream in){
        BufferedReader pBr = new BufferedReader(new InputStreamReader(in));
        StringBuffer pSb = new StringBuffer();
        int i = -1;
        try {
            while((i = pBr.read()) != -1){
                pSb.append((char)i);
            }
            pBr.close();
        } catch (IOException e) {
            Log.log("获取输入流数据出现错误。");
            Log.logE(e);
        }
        return pSb.toString();
    }

    /**打开浏览器*/
    public static void openBrowser(Context c,String url){
        Uri uri = Uri.parse(url);
        Intent intent = new  Intent(Intent.ACTION_VIEW, uri);
        c.startActivity(intent);
    }

    /**URL编码文本文件*/
    public static String urlencodeFile(String f){
        if(f!=null && !f.equals("")){
            File pFile = new File(f);
            try {
                FileReader pFr = new FileReader(pFile);
                StringBuffer pSb = new StringBuffer();

                int i = -1;
                while((i = pFr.read()) != -1){
                    pSb.append((char)i);
                }

                return URLEncoder.encode(pSb.toString(),"UTF-8");

            } catch (FileNotFoundException e) {
                Log.log("文件编码 ： 文件不存在。请求的文件为：" + f);
            }  catch (Exception e){
                Log.log("文件编码 ： 出现了一些错误。请求的文件为：" + f);
                Log.logE(e);
            }
        }
        return null;
    }

    /**Base64文本文件*/
    public static String base64File(String f){
        String res = urlencodeFile(f);
        return res == null ? null : Base64.encode(res);
    }

    public static String long2ip(long ip){
        StringBuffer sb=new StringBuffer();
        sb.append(String.valueOf((int)(ip&0xff)));
        sb.append('.');
        sb.append(String.valueOf((int)((ip>>8)&0xff)));
        sb.append('.');
        sb.append(String.valueOf((int)((ip>>16)&0xff)));
        sb.append('.');
        sb.append(String.valueOf((int) ((ip >> 24) & 0xff)));
        return sb.toString();
    }

    /**是否在WIFI网络*/
    public static boolean isWifiNetwork(Context c){
        NetworkInfo[] pNetInfo = getConnectivity(c).getAllNetworkInfo();
        for(NetworkInfo x: pNetInfo){
            if(x.getType() == ConnectivityManager.TYPE_WIFI && x.isConnected())
                return true;
        }
        return false;
    }

    /**Wifi网络是否已经连接*/
    public static boolean isWifiConnected(Context c){
        if(c != null){
            ConnectivityManager pCon = (ConnectivityManager)c.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo.State pNetState = pCon.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
            if(pNetState != null){
                return pNetState == NetworkInfo.State.CONNECTED;
            }
        }
        return  false;
    }

    /**获取当前网络信息*/
    public static ConnectivityManager getConnectivity(Context c){
        return c!=null ? (ConnectivityManager)c.getSystemService(Context.CONNECTIVITY_SERVICE) : null;
    }

    /**发送Toast信息*/
    public static void makeToast(Context c,CharSequence szText,boolean isShort){
        Toast.makeText(c, szText, isShort ? Toast.LENGTH_SHORT : Toast.LENGTH_LONG).show();
    }

    /**发送一个简易的Notification*/
    public static void postNotification(Context c,CharSequence title,CharSequence info,int icon){
        if( c != null && title != null && info != null){
            NotificationManager pNotiMgr = (NotificationManager)c.getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationCompat.Builder pNoti = new NotificationCompat.Builder(c);
            pNoti.setContentTitle(title).setContentText(info).setTicker("Simple Netkeeper").setWhen(System.currentTimeMillis()).setPriority(Notification.PRIORITY_DEFAULT)
                    .setSmallIcon(icon).setDefaults(Notification.DEFAULT_ALL).setOngoing(false);
            pNotiMgr.notify(1038403669,pNoti.build());
        }
    }
}
