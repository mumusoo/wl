package com.lechen.wlanconnector.util;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.lechen.wlanconnector.ConnectActivity;
import com.lechen.wlanconnector.MainActivity;

public class WifiAdmin {
    // 定义WifiManager对象   
    private WifiManager mWifiManager;
    // 定义WifiInfo对象     
    private WifiInfo mWifiInfo;
    // 扫描出的网络连接列表     
    private List<ScanResult> mWifiList;
    // 网络连接列表     
    private List<WifiConfiguration> mWifiConfiguration;

    private String IP = "192.168.55.1";
//    private String IP = "192.168.55.1";
    private int PORT = 8062;
    // 定义一个WifiLock     
    WifiLock mWifiLock;
    //连接用
    public static String ssid;
    public static String password;
    private int type;
    private static Context mcontext;
    private Activity mActivity;

    Handler mHandler;

    // 构造器
    public WifiAdmin(Context context,Activity activity ) {
        mActivity=activity;
        mcontext = context;
        // 取得WifiManager对象     
        mWifiManager = (WifiManager) context
                .getSystemService(Context.WIFI_SERVICE);
        // 取得WifiInfo对象     
        mWifiInfo = mWifiManager.getConnectionInfo();
    }

    public static String getSsid(Context context) {
        if (ssid == null) {
//            Log.i("ssss",mcontext+"");
//            if (mcontext!=null)
            ssid = ((WifiManager) context.getSystemService(Context.WIFI_SERVICE)).getConnectionInfo().getSSID().replaceAll("\"", "");
        }
        return ssid;
    }

    public static String getPassword() {
        return password;
    }

    // 打开WIFI
    public void openWifi(Context context) {
        if (!mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(true);
            myToast("开启Wifi");
        } else if (mWifiManager.getWifiState() == 2) {
            myToast("Wifi正在开启");
        } else {
            myToast("Wifi已经开启");
        }
    }

    // 关闭wifi
    public void closeWifi(Context context) {
        if (mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(false);
        } else if (mWifiManager.getWifiState() == 1) {
            myToast("亲，Wifi已经关闭，不用再关了");
        } else if (mWifiManager.getWifiState() == 0) {
            myToast("亲，Wifi已经关闭，不用再关了");
        } else {
            myToast("请重新关闭");
        }
    }

    // 检查当前WIFI状态     
    public void checkState(Context context) {
        if (mWifiManager.getWifiState() == 0) {
            myToast("Wifi正在关闭");
            openWifi(context);
        } else if (mWifiManager.getWifiState() == 1) {
            myToast("Wifi已经关闭");
            openWifi(context);
        } else if (mWifiManager.getWifiState() == 2) {
            Toast.makeText(context, "Wifi正在开启", Toast.LENGTH_SHORT).show();
        } else if (mWifiManager.getWifiState() == 3) {
            myToast("Wifi已经开启");
            //  Toast.makeText(context, "Wifi已经开启", Toast.LENGTH_SHORT).show();
        } else {
            myToast("没有获取到WiFi状态");
        }
    }

    // 锁定WifiLock     
    public void acquireWifiLock() {
        mWifiLock.acquire();
    }

    // 解锁WifiLock     
    public void releaseWifiLock() {
        // 判断时候锁定     
        if (mWifiLock.isHeld()) {
            mWifiLock.acquire();
        }
    }

    // 创建一个WifiLock     
    public void creatWifiLock() {
        mWifiLock = mWifiManager.createWifiLock("Test");
    }

    // 得到配置好的网络     
    public List<WifiConfiguration> getConfiguration() {
        return mWifiConfiguration;
    }

    // 指定配置好的网络进行连接     
    public void connectConfiguration(int index) {
        // 索引大于配置好的网络索引返回     
        if (index > mWifiConfiguration.size()) {
            return;
        }
        // 连接配置好的指定ID的网络     
        mWifiManager.enableNetwork(mWifiConfiguration.get(index).networkId,
                true);
    }

    public void startScan(Context context) {
        mWifiManager.startScan();
        //得到扫描结果   
        List<ScanResult> results = mWifiManager.getScanResults();
        // 得到配置好的网络连接
        mWifiConfiguration = mWifiManager.getConfiguredNetworks();
        if (results == null) {
            if (mWifiManager.getWifiState() == 3) {
                myToast("当前区域没有无线网络");
            } else if (mWifiManager.getWifiState() == 2) {
                myToast("wifi正在开启，请稍后扫描");
            } else {
                myToast("WiFi没有开启");
            }
        } else {
            mWifiList = new ArrayList();
            for (ScanResult result : results) {
                if (result.SSID == null || result.SSID.length() == 0
                        || result.capabilities.contains("[IBSS]")) {
                    continue;
                }
                boolean found = false;
                Log.i("MainActivity", "result= " + result.SSID + " capabilities= " + result.capabilities);
                for (ScanResult item : mWifiList) {
                    Log.i("MainActivity", "item= " + item.SSID + " capabilities=" + item.capabilities);
                    if (item.SSID.equals(result.SSID) && item.capabilities.equals(result.capabilities)) {
                        Log.i("MainActivity", "found true");
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    mWifiList.add(result);
                }
            }
        }
    }

    // 得到网络列表   
    public List<ScanResult> getWifiList() {
        return mWifiList;
    }

    // 查看扫描结果   
    public StringBuilder lookUpScan() {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < mWifiList.size(); i++) {
            stringBuilder
                    .append("Index_" + new Integer(i + 1).toString() + ":");
            // 将ScanResult信息转换成一个字符串包     
            // 其中把包括：BSSID、SSID、capabilities、frequency、level    
            stringBuilder.append((mWifiList.get(i)).toString());
            stringBuilder.append("/n");
        }
        return stringBuilder;
    }

    // 得到MAC地址  
    public String getMacAddress() {
        return (mWifiInfo == null) ? "NULL" : mWifiInfo.getMacAddress();
    }

    // 得到接入点的BSSID   
    public String getBSSID() {
        return (mWifiInfo == null) ? "NULL" : mWifiInfo.getBSSID();
    }

    // 得到连接的IP  
    public int getIPAddress() {
        return (mWifiInfo == null) ? 0 : mWifiInfo.getIpAddress();
    }

    // 得到连接的ID   
    public int getNetworkId() {
        return (mWifiInfo == null) ? 0 : mWifiInfo.getNetworkId();
    }

    // 得到WifiInfo的所有信息包   
    public String getWifiInfo() {
        return (mWifiInfo == null) ? "NULL" : mWifiInfo.toString();
    }

    // 添加一个网络并连接   
    public void addNetwork(WifiConfiguration wcg) {
        myToast("连接中...");
        int wcgID = mWifiManager.addNetwork(wcg);
        boolean b = mWifiManager.enableNetwork(wcgID, true);
        Log.i("wifi", "wifi连接成功");
    }

    // 断开指定ID的网络   
    public void disconnectWifi(int netId) {
        mWifiManager.disableNetwork(netId);
        mWifiManager.disconnect();
    }

    public void removeWifi(int netId) {
        disconnectWifi(netId);
        mWifiManager.removeNetwork(netId);
    }

//创建wifi热点的。

    public WifiConfiguration CreateWifiInfo(String SSID, String Password, int Type) {
        WifiConfiguration config = new WifiConfiguration();
        config.allowedAuthAlgorithms.clear();
        config.allowedGroupCiphers.clear();
        config.allowedKeyManagement.clear();
        config.allowedPairwiseCiphers.clear();
        config.allowedProtocols.clear();
        config.SSID = "\"" + SSID + "\"";

        WifiConfiguration tempConfig = this.IsExsits(SSID);
        if (tempConfig != null) {
            mWifiManager.removeNetwork(tempConfig.networkId);
        }

        if (Type == 1) //WIFICIPHER_NOPASS
        {
            config.wepKeys[0] = "";
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
        }
        if (Type == 2) //WIFICIPHER_WEP
        {
            config.hiddenSSID = true;
            config.wepKeys[0] = "\"" + Password + "\"";
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
        }
        if (Type == 3) //WIFICIPHER_WPA
        {
            config.preSharedKey = "\"" + Password + "\"";
            config.hiddenSSID = true;
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            //config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            config.status = WifiConfiguration.Status.ENABLED;
        }
        return config;
    }

    private WifiConfiguration IsExsits(String SSID) {
        List<WifiConfiguration> existingConfigs = mWifiManager.getConfiguredNetworks();
        for (WifiConfiguration existingConfig : existingConfigs) {
            if (existingConfig.SSID.equals("\"" + SSID + "\"")) {
                return existingConfig;
            }
        }
        return null;
    }

    // 提供一个外部接口，传入要连接的无线网
    String mac;
    String mkey;
    Dialog mdialog;

    public void connect(final String mac, final String ssid, final String password, final int type, final Context context, String key) {

        this.ssid = ssid;
        this.password = password;
        this.type = type;
        this.mac = mac;
        this.mkey = key;
        if (mkey.equals("1")) {
            mdialog = WeiboDialogUtils.createLoadingDialog(mcontext, "连接中...");
        }
        /*this.mcontext = context;*/

        Thread thread = new Thread() {
            public void run() {
                try {
                    checkState(context);
                    while (mWifiManager.getWifiState() != WifiManager.WIFI_STATE_ENABLED) {
                        try {
                            // 为了避免程序一直while循环，让它睡个100毫秒检测……
                            Thread.sleep(300);
                            Log.i("wifi", "正在打开wifi");
                        } catch (InterruptedException ie) {
                        }
                    }
                    mWifiManager = (WifiManager) context
                            .getSystemService(Context.WIFI_SERVICE);
                    mWifiInfo = mWifiManager.getConnectionInfo();
                    String tempSSID = mWifiInfo.getSSID().replaceAll("\"", "").trim();
                    if (ssid.equals(tempSSID)) {
                        myToast("连接成功");
                        WeiboDialogUtils.closeDialog(mdialog);
                        TelnetUtil telnet = new TelnetUtil(mcontext);
                        try {
                            telnet.connect(mac, 8062,null);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        Intent intent = new Intent(mcontext, ConnectActivity.class);
//                        intent.putExtra("key", mkey);
                        intent.putExtra("admin",true);
                        mActivity.startActivityForResult(intent,MainActivity.REQUEST_CODE_CONNECT);
                    } else {
                        Log.i("", ssid + " " + password);
                        WifiConfiguration wifiConfig = CreateWifiInfo(ssid, password, type);
                        if (password == null) {
                            myToast("密码未保存");
                            WeiboDialogUtils.closeDialog(mdialog);
                            return;
                        }
                        if (wifiConfig == null) {
                            myToast("未找到该SSID");
                            WeiboDialogUtils.closeDialog(mdialog);
                            return;
                        }
                        WifiConfiguration tempConfig = IsExsits(ssid);
                        if (tempConfig != null) {
                            removeWifi(tempConfig.networkId);
                        }
                        addNetwork(wifiConfig);
                        Thread.sleep(3000);

                        // 取得WifiInfo对象
                        mWifiManager = (WifiManager) context
                                .getSystemService(Context.WIFI_SERVICE);
                        mWifiInfo = mWifiManager.getConnectionInfo();
                        boolean key = true;
                        int count = 0;
                        while (key) {
                            mWifiManager = (WifiManager) context
                                    .getSystemService(Context.WIFI_SERVICE);
                            mWifiInfo = mWifiManager.getConnectionInfo();
                            tempSSID = mWifiInfo.getSSID().replaceAll("\"", "").trim();
                            Log.i("获取连接状态:", tempSSID);
                            count++;
                            System.out.println(ssid);
                            if (ssid.equals(tempSSID)) {
                                key = false;
                                Thread.sleep(500);
                                TelnetUtil telnet = new TelnetUtil(mcontext);
                                telnet.connect(IP, PORT, null);
                                WeiboDialogUtils.closeDialog(mdialog);
                                Intent intent = new Intent(mcontext, ConnectActivity.class);
                                intent.putExtra("key", mkey);
                                mActivity.startActivityForResult(intent,MainActivity.REQUEST_CODE_CONNECT);
                            } else {
                                disconnectWifi(mWifiInfo.getNetworkId());
                                Thread.sleep(2000);
                                addNetwork(wifiConfig);
                                Thread.sleep(2000);
                            }
                            if (count > 4) {
                                myToast("获取IP地址失败");
                                WeiboDialogUtils.closeDialog(mdialog);
                                key = false;
                            }
                            Thread.sleep(500);
                        }
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();
    }

    private String tempText = "";

    private void myToast(String text) {
        tempText = text;
        Thread t = new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                Toast.makeText(mcontext, tempText, Toast.LENGTH_SHORT).show();
                Looper.loop();
            }
        };
        t.start();

    }
}