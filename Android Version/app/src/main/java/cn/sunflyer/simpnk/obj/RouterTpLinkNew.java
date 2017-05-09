package cn.sunflyer.simpnk.obj;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Iterator;

import cn.sunflyer.simpnk.control.AccountController;
import cn.sunflyer.simpnk.control.Log;
import cn.sunflyer.simpnk.control.StatusController;

/**
 * Created by 陈耀璇 on 2015/9/11.
 * TP-LINK 新版本固件验证类
 * 对比水星新版本，差异在于取消二次加密，采用验证后服务器返回stok作为TOKEN，有效期1分钟
 * 交互采用完全JSON
 */
public class RouterTpLinkNew extends Router {

    public RouterTpLinkNew(){
        super("admin", StatusController.sRouterPassword, StatusController.sRouterAdd, StatusController.sAccName, StatusController.sAccPassword , AUTH_DYNAMIC_ID_TP);
    }

    private String mDynamicId = null;

    public boolean authRouter(){
        if(this.gRouterAccPassword != null){
            String realkey = this.getRealKey();
            if(realkey != null){
                String response = this.requestData(this.gRouterIP , "{\"method\":\"do\" , \"login\":{\"password\":\"" + realkey + "\"}}");
                if(response != null){
                    try {
                        JSONObject jsonData = new JSONObject(response);
                        if("0".equals(jsonData.getString("error_code"))){
                            this.mDynamicId = jsonData.getString("stok");
                            return true;
                        }
                    } catch (JSONException e) {
                        Log.log("JSON解析返回数据出现错误");
                        Log.logE(e);
                    }
                }
            }
        }
        return false;
    }

    public String getRealKey(){
        try{
            return RouterMecuryTpNew.calcDynamicId(this.gRouterAccPassword , RouterMecuryTpNew.AUTH_KEY_SHORT , RouterMecuryTpNew.AUTH_KEY_LONG);
        }catch(Exception e){
            return null;
        }
    }

    @Override
    protected void testLink() {
        Log.log(this.authRouter() ? "路由器身份验证完成。" : "路由器身份验证失败。");
    }

    @Override
    protected void detectOld() throws Exception {
        Log.log("This program is not supported for TP-LINK Old Version");
    }

    /***
     * 执行路由器连接，
     * 若执行成功，返回RES_SUCCESS，若响应错误，返回RES_ROUTER_RESPONSE_ERROR
     */
    @Override
    public int connect() {
        if(this.getConnectionState() == CONNECTION_SUCCESS){
            return RES_SUCCESS;
        }
        if(this.authRouter()){
            try {
                String realName = AccountController.getRealAccount(this.gAccName) ,
                requestAddr = this.gRouterIP  + "/stok=" + this.mDynamicId + "/ds" ,
                requestData = "{\"protocol\":{\"wan\":{\"wan_type\":\"pppoe\"},\"pppoe\":{\"username\":\"" +
                        URLEncoder.encode(realName, "UTF-8").replace("+" , "%20") + "\",\"password\":\"" +
                        URLEncoder.encode(this.gAccPassword , "UTF-8").replace("+" , "%20") + "\"}},\"method\":\"set\"}" ,
                responseData = this.requestData(requestAddr , requestData);

                if(requestData != null){
                    JSONObject jsonData = new JSONObject(responseData);
                    if("0".equals(jsonData.getString("error_code"))){
                        requestData = "{\"network\":{\"change_wan_status\":{\"proto\":\"pppoe\",\"operate\":\"connect\"}},\"method\":\"do\"}" ;
                        responseData = this.requestData(requestAddr , requestData);
                        if(requestData != null){
                            jsonData = new JSONObject(responseData);
                            return "0".equals(jsonData.getString("error_code")) ? RES_SUCCESS : RES_ROUTER_RESPONSE_ERROR;
                        }
                    }
                }
                return RES_ROUTER_RESPONSE_ERROR;
            } catch (Exception e) {
                Log.logE(e);
                return RES_UNABLE_ENCODE;
            }
        }
        return RES_UNABLE_ACCESS;
    }

    @Override
    public int getConnectionState() {
        this.getStatus();
        if(!this.mStatus.isEmpty() && mStatus.containsKey("link_status")){
            return Integer.parseInt(mStatus.get("link_status"));
        }
        return CONNECTION_UNKNOWN;
    }

    private HashMap<String,String> mStatus;

    /**
     * 获取路由器当前状态
     * 如果获取失败，则返回的HashMap为空，而不是null
     * */
    public HashMap<String,String> getStatus(){
        if(this.mStatus == null)
            mStatus = new HashMap<>();
        else
            mStatus.clear();

        if (this.authRouter()) {
            String response = this.requestData(this.gRouterIP + "/stok=" + this.mDynamicId + "/ds", "{\"network\":{\"name\":[\"wan_status\"]},\"method\":\"get\"}");
            if(response != null){
                try {
                    JSONObject jsonData = new JSONObject(response) , wanStatus = "0".equals(jsonData.getString("error_code")) ? jsonData.getJSONObject("network").getJSONObject("wan_status") : null;
                    if(wanStatus != null){
                        Iterator<String> it = wanStatus.keys();
                        String key = null;
                        while(it.hasNext()){
                            key = it.next();
                            mStatus.put( key , wanStatus.getString(key));
                        }
                    }
                } catch (Exception e) {
                    Log.log("JSON化返回结果出现错误");
                    Log.logE(e);
                }
            }else{
                Log.log("Router - TP-LINK新版本：服务器的响应为空。");
            }
        }else{
            Log.log("Router - TP-LINK新版本：身份验证失败，无法获取状态数据");
        }
        return this.mStatus;
    }

    @Override
    public void trackLink() {
        int pAccessCount = 0;

        while(pAccessCount ++ < 20){

            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                Log.logE(e);
            }

            this.getStatus();
            if(!this.mStatus.isEmpty()){
                int iStateCode = StatusController.parseInt(this.mStatus.get("link_status"), -1);
                int iErrCode = StatusController.parseInt(this.mStatus.get("error_code"), -1);

                if(iStateCode != -1 && iErrCode != -1){

                    sendMessage(RouterMecuryTpNew.LINK_STATUS[iStateCode]+"("+RouterMecuryTpNew.LINK_CODE[iErrCode]+")(尝试次数："+pAccessCount+"/20)");
                    if(iStateCode == RouterMecuryTpNew.LINK_UP || iStateCode == RouterMecuryTpNew.LINK_DOWN) break;
                    else continue;
                }
            }
            sendMessage("没有获取到路由器状态信息。(尝试次数："+pAccessCount+"/20)");
        }
    }

    @Override
    public void setWifiState(WiFiInfo pW) {

    }

    @Override
    public WiFiInfo getWifiState() {
        return null;
    }

    @Override
    protected int setDialProperty(HttpURLConnection mRouterUrlCon) {
        mRouterUrlCon.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        mRouterUrlCon.setRequestProperty("Charset","UTF-8");
        mRouterUrlCon.setRequestProperty("Referer", this.getSvrURL());
        mRouterUrlCon.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2272.89 Safari/537.36");
        mRouterUrlCon.setRequestProperty("Origin", this.getSvrURL());
        //mRouterUrlCon.setRequestProperty("Host", "tplogin.cn");
        mRouterUrlCon.setRequestProperty("Connection", "keep-alive");
        mRouterUrlCon.setRequestProperty("Accept-Language", "zh-CN,zh;q=0.8");
        return 0;
    }

    private HttpURLConnection getJsonConnection(String addr , String host){
        try {
            HttpURLConnection pHuc = this.getConnection(addr.startsWith("http://") ? addr : "http://" + addr);
            this.setDialProperty(pHuc);
            pHuc.setRequestProperty("Host", host == null ? this.gRouterIP : host);
            return pHuc;
        } catch (IOException e) {
            Log.log("获取链接出现错误：" + addr);
            Log.logE(e);
        }
        return null;
    }

    /**
     * 解析JSON
     * */
    //public static JSONObject parseToJson(String data){
    //    if(data != null && !"".equals(data)){
    //        JsonReader jr = new JsonReader(new StringReader(data));
    //
    //    }
    //    return null;
    //}

    private String mHost = null;

    private String requestData(String addr , String data){
        return this.requestData(addr , data , mHost);
    }

    /**
     * 模拟JSON请求制定目标，并发送制定诗句
     * @param addr 请求对象地址
     * @param data 请求数据，不可为Null
     * @return 返回String，如果出现错误，返回null;
     * */
    private String requestData(String addr , String data , String host){
        if(addr != null && !"".equals(addr)){
            HttpURLConnection pHuc = null;
            try{
                    //if(addr.contains("tplogin.cn"))
                    //    addr = addr.replace("tplogin.cn" , this.gRouterIP);//尝试修正DNS错误
                pHuc = this.getJsonConnection(addr , host);

                pHuc.setDoOutput(true);
                pHuc.setRequestMethod("POST");
                pHuc.setRequestProperty("Content-Length", String.valueOf(data.length()));
                pHuc.setRequestProperty("DNT", String.valueOf(1));

                pHuc.getOutputStream().write(data.getBytes());

                return this.getHTMLContent(pHuc.getInputStream());

            }catch(UnknownHostException e){
                if(host != null){
                    Log.log("新版本拨号：固件强制跳转出现错误。主机地址无法识别。" + e.getMessage());
                    return null;
                }
                String hostAddr = e.getMessage();
                int path = hostAddr.indexOf("\"") + 1 , pathe = hostAddr.indexOf("\"" , path >= 0 ? path + 1 : 0);
                mHost = (path >= 0 && pathe > path ) ? hostAddr.substring(path , pathe) : null;
                Log.log("新版本拨号：尝试重新设置Host为 - " + (mHost == null ? "空值" : mHost));
                if(mHost != null)
                    return this.requestData(addr , data , mHost);
            }catch(Exception e){
                if(e.getMessage() != null && e.getMessage().contains("401")){
                    if(this.authRouter()){
                        this.requestData(addr,data);
                    }
                }else{
                    Log.logE(e);
                    if(pHuc != null){
                        String pData = this.getHTMLContent(pHuc.getErrorStream());
                        if(pData != null) return pData;
                    }
                }
            }
        }
        return null;
    }
}
