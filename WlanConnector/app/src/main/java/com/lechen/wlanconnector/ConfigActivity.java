package com.lechen.wlanconnector;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.lechen.wlanconnector.util.NetWorkUtil;
import com.lechen.wlanconnector.util.TelnetUtil;
import com.lechen.wlanconnector.util.WeiboDialogUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class ConfigActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> ,View.OnClickListener{

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final String TAG="ssss";
    private static final int REQUEST_READ_CONTACTS = 0;
    byte[] buff = new byte[2048];
    Context mcontext = ConfigActivity.this;

    private final String START = "START";
    private final String SET_SERVICE_ENABLE = "nvram_set Service1Enable ";

    private final String GET_SERVICE_ENABLE = "nvram_get Service1Enable";
    private final String GET_SERVICE_NAME = "nvram_get Service1name";
    private final String GET_SERVICE_LIST = "nvram_get Service1Servicelist";
    private final String GET_SERVICE_VLANID = "nvram_get Service1VlanId";
    private final String GET_SERVICE_VLANPRI = "nvram_get Service1VlanPri";
    private final String GET_CONNECTIONMODE = "nvram_get wanConnectionMode";
    private final String GET_SERVICE_PORTMAP = "nvram_get Service1Portmap";
    private final String GET_PPPOE_USER = "nvram_get wan_pppoe_user";
    private final String GET_PPPOE_PWD = "nvram_get wan_pppoe_pass";

    private final String GET_DHCP_OPTION = "nvram_get wan_dhcp_option60_enabled";
    private final String GET_WAN_VENDOR = "nvram_get wan_vendor";

    private final String GET_STATIC_IP = "nvram_get wan_ipaddr";
    private final String GET_STATIC_NETMASK = "nvram_get wan_netmas";
    private final String GET_STATIC_GATEWAY = "nvram_get wan_gateway";
    private final String GET_STATIC_DNS = "nvram_get wan_primary_dns";
    private final String GET_STATIC_SDNS = "nvram_get wan_secondary_dns";

    private final String GET_SYS_WANIP = "web 2860 sys wanIpAddr";
    private final String GET_PPPOE_ERRORCODE = "web 2860 sys pppoeerrcode";

    private final String INIT1 = "/sbin/wan.sh stop 1";
    private final String INIT2 = "/sbin/lan.sh stop 1";
    private final String INIT3 = "/sbin/config_mstar_vconfig.sh stop wan 1";

    private final String MAKE_USE1 = "/sbin/wan.sh stop 1";
    private final String MAKE_USE2 = "/sbin/lan.sh stop 1";
    private final String MAKE_USE3 = "/sbin/config_mstar_vconfig.sh stop wan1";

    private final String MAKE_USE4 = "gen_wifi_config";
    private final String MAKE_USE5 = "/sbin/config_mstar_vconfig.sh start wan1";
    private final String MAKE_USE6 = "/sbin/wan.sh start 1";
    private final String MAKE_USE7 = "/sbin/lan.sh start 1";
    private final String MAKE_USE8 = "/sbin/nat.sh 1";

    private final String SET_SERVICE_VLANID = "nvram_set Service1VlanId ";
    private final String SET_SERVICE_VLANPRI = "nvram_set Service1VlanPri ";
    private final String SET_SERVICE_WLANPORT = "nvram_set Service1Portmap ";

    private final String SET_SERVICE_CONNECTION_MODE = "nvram_set wanConnectionMode  ";

    private final String SET_SERVICE_WLAN_USERNAME = "nvram_set wan_pppoe_user  ";
    private final String SET_SERVICE_WLAN_PWD = "nvram_set wan_pppoe_pass  ";

    private final String SET_SERVICE_WLAN_DHCP = "nvram_set wan_dhcp_option60_enabled  ";

    private final String SET_SERVICE_STATIC_IP = "nvram_set wan_ipaddr  ";
    private final String SET_SERVICE_STATIC_NETMASK = "nvram_set wan_netmask  ";
    private final String SET_SERVICE_STATIC_GETWAY = "nvram_set wan_gateway  ";
    private final String SET_SERVICE_STATIC_DNS = "nvram_set wan_primary_dns  ";
    private final String SET_SERVICE_STATIC_SDNS = "nvram_set wan_secondary_dns  ";
    private final String SET_SERVICE_WLAN_OPTION = "nvram_set wan_vendor ";
    private final String SET_SERVICE_MODE = "nvram_set Service1Mode  ";
    private final String SET_SERVICE_LIST = "nvram_set Service1Servicelist  ";
    private final String SET_SERVICE_NAME = "nvram_set Service1name  ";

    private final String SECDNS = "nvram_set dhcpSecDns '10.72.255.131'";
    private final String SECDNS2 = "nvram_set dhcpSecDns2 '10.72.255.131'";
    private final String SECDNS3 = "nvram_set dhcpSecDns3 '10.72.255.131'";
    private final String SECDNS4 = "nvram_set dhcpSecDns4 '10.72.255.131'";
    private final String SECDNS5 = "nvram_set dhcpSecDns5 '10.72.255.131'";
    private final String SECDNS6 = "nvram_set dhcpSecDns6 '10.72.255.131'";
    private final String SECDNS7 = "nvram_set dhcpSecDns7 '10.72.255.131'";
    private final String SECDNS8 = "nvram_set dhcpSecDns8 '10.72.255.131'";
    private final String SECDNS9 = "nvram_set dhcpSecDns9 '10.72.255.131'";

    boolean key1 = false, key3 = true;
    boolean key2 = false;
    String ssid;
    String pwd;
    String mac;
    String port;
    final String ip = "www.baidu.com";// ping 的地址，可以换成任何一种可靠的外网
    Boolean isContinue = false;
    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private ScrollView mconfigScollView;
    private Dialog mWeiboDialog;
    private Timer notifyTimeTimer;
    private boolean getNet;

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_config, menu);
//        return super.onCreateOptionsMenu(menu);
//    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks off the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        switch (id) {
//            case R.id.action_commit:
//
//                if (NetWorkUtil.isWifiConnected(mcontext, ssid)) {
//                    if (key1) {
//                        Log.i("ConfigActivity", "上传配置");
//                        if (checkNumber()) {
//                            isContinue = false;
//                            key1 = false;
//                            key3 = false;
//                            Log.i("ConfigActivity", "上传配置验证通过");
//                            mWeiboDialog = WeiboDialogUtils.createLoadingDialog(mcontext, "上传中...");
//                            exec(SET_SERVICE_VLANID);
//                        }
//                    } else {
//                        myToast("请等待上传完成");
//                    }
//                } else {
//                    myToast("无线连接已断开");
//                }
//                break;
//            case R.id.action_refresh:
//
//                if (NetWorkUtil.isWifiConnected(mcontext, ssid)) {
//                    if (key2) {
//                        isContinue = false;
//                        Log.i("ConfigActivity", "刷新配置");
//                        mWeiboDialog = WeiboDialogUtils.createLoadingDialog(mcontext, "刷新中...");
//                        myToast("正在刷新...");
//                        exec(GET_CONNECTIONMODE);
//                        key2 = false;
//                    } else {
//                        myToast("请等待刷新完成");
//                    }
//                } else {
//                   /* AlertDialog isExit = new AlertDialog.Builder(this).create();
//                    isExit.setTitle("提示");
//                    isExit.setMessage("网络已经断开");
//                    isExit.show();*/
//                    myToast("无线连接已断开");
//                }
//                break;
//            case android.R.id.home:
//                System.out.println("按下了back键   onBackPressed()");
//                AlertDialog isExit = new AlertDialog.Builder(this).create();
//                isExit.setTitle("提示");
//                isExit.setMessage("退出配置页面？");
//                isExit.setButton(-1, "确定", listener);
//                isExit.setButton(-2, "取消", listener);
//                isExit.show();
//                break;
//            default:
//                break;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

    String VLAN_ID, VLAN_PRI;
    String WLAN_PORT = "", WLAN_USERNAME = "", WLAN_PWD = "", SIP = "", GATWAY = "", NETMASK = "", DNS = "", SDNS = "";
    Boolean vlanIdKey = true;
    String ipReg="^(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[1-9])\\."
            +"(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
            +"(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
            +"(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)$";
    String ipReg1="192.168.(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\.(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)$";
    String ipReg2="127.(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\.(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\.(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)$";
    Boolean checkNumber() {
        try {
            String vlanIdStr = vlanId_text.getText().toString().trim();
            String vlanPriStr = text_vlanPri.getText().toString().trim();
            if (vlanIdStr.length() <= 0) {
                myToast("VLAN ID无效");
                return false;
            }
            if (vlanPriStr.length() <= 0) {
                vlanPriStr = "0";
                vlanIdKey = false;
            }
            Integer vlanId = 0, vlanPri = 0;
            try {
                vlanId = Integer.parseInt(vlanIdStr);
            } catch (Exception e) {
                vlanId_text.setText("0");
                e.printStackTrace();
                myToast("VLAN ID无效");
                return false;
            }
            if (vlanIdKey) {
                try {
                    vlanPri = Integer.parseInt(vlanPriStr);
                } catch (Exception e) {
                    text_vlanPri.setText("0");
                    if ("untag".equals(vlanPriStr)) {
                        vlanPri = 0;
                    } else {
                        myToast("优先级无效");
                        e.printStackTrace();
                        return false;
                    }
                }
                if (vlanPri < 0 || vlanPri > 7) {
                    myToast("优先级无效");
                    return false;
                }
                VLAN_PRI = vlanPri + "";
            } else {
                VLAN_PRI = vlanPriStr;
            }

            if (vlanId == 1 || vlanId == 4081 || vlanId > 4095) {
                myToast("VLAN ID无效");
                return false;
            }
            VLAN_ID = vlanId + "";
            if (vlan_switch.isChecked()) {
                serviceName = "1_INTERNET_R_VID_" + VLAN_ID;
            } else {
                serviceName = "1_INTERNET_R_VID_untag";
            }
            String connectionMode = spinner_connectionMode.getSelectedItem().toString().trim();
            if ("PPPOE".equals(connectionMode)) {
                String usernameStr = username.getText().toString().trim();
                String passwordStr = password.getText().toString().trim();
                if (usernameStr.length() <= 0||usernameStr.indexOf(" ")!=-1) {
                    myToast("用户名无效");
                    return false;
                }
                WLAN_USERNAME = usernameStr;
                if (passwordStr.length() <= 0||passwordStr.indexOf(" ")!=-1) {
                    myToast("密码无效");
                    return false;
                }
                WLAN_PWD = passwordStr;
            } else if ("DHCP".equals(connectionMode)) {
                if (switch_option.isChecked()) {
                    String optionValue = et_option.getText().toString().trim();
                    if (optionValue == null || optionValue.length() <= 0 || optionValue.length()>32) {
                        myToast("option60值无效");
                        return false;
                    }
                }

            } else if ("STATIC".equals(connectionMode)) {
                String et_ipStr = et_ip.getText().toString().trim();
                String et_wgStr = et_wg.getText().toString().trim();
                String et_zwyStr = et_zwy.getText().toString().trim();
                String et_dnsStr = et_dns.getText().toString().trim();
                String et_BDNSStr = et_BDNS.getText().toString().trim();
                if (et_ipStr.length() <= 0) {
                    myToast("ip地址无效");
                    return false;
                }else if(et_ipStr.matches(ipReg1)||et_ipStr.matches(ipReg2)){
                    myToast("ip地址无效");
                    return false;
                } else{
                    if(!et_ipStr.matches(ipReg)){
                        myToast("ip地址无效");
                        return false;
                    }
                }
                SIP = et_ipStr;
                if (et_zwyStr.length() <= 0) {
                    myToast("子网掩码无效");
                    return false;
                }else if(et_zwyStr.matches(ipReg1)||et_zwyStr.matches(ipReg2)){
                    myToast("子网掩码无效");
                    return false;
                }else{
                    if(!et_zwyStr.matches(ipReg)){
                        myToast("子网掩码无效");
                        return false;
                    }
                }
                NETMASK = et_zwyStr;
                if (et_wgStr.length() <=0) {
                    myToast("网关无效");
                    return false;
                }else if(et_wgStr.matches(ipReg1)||et_wgStr.matches(ipReg2)){
                    myToast("网关无效");
                    return false;
                }else{
                    if(!et_wgStr.matches(ipReg)){
                        myToast("网关无效");
                        return false;
                    }
                }
                GATWAY = et_wgStr;
                if (et_dnsStr.length() <= 0) {
                    myToast("DNS无效");
                    return false;
                }else if(et_dnsStr.matches(ipReg1)||et_dnsStr.matches(ipReg2)){
                    myToast("DNS无效");
                    return false;
                }else{
                    if(!et_dnsStr.matches(ipReg)){
                        myToast("DNS无效");
                        return false;
                    }
                }
                DNS = et_dnsStr;
                if (et_BDNSStr.length() <= 0) {
                    myToast("备用DNS无效");
                    return false;
                }else if(et_BDNSStr.matches(ipReg1)||et_BDNSStr.matches(ipReg2)){
                    myToast("备用DNS无效");
                    return false;
                }else{
                    if(!et_BDNSStr.matches(ipReg)){
                        myToast("备用DNS无效");
                        return false;
                    }
                }
                SDNS = et_BDNSStr;

            } else {
                myToast("连接模式无效");
                return false;
            }
            if (switch_lan1.isChecked()) {
                WLAN_PORT += "1";
            } else {
                WLAN_PORT += "0";
            }
            if (switch_lan2.isChecked()) {
                WLAN_PORT += "1";
            } else {
                WLAN_PORT += "0";
            }
            if (switch_lan3.isChecked()) {
                WLAN_PORT += "1";
            } else {
                WLAN_PORT += "0";
            }
            if (switch_lan4.isChecked()) {
                WLAN_PORT += "1";
            } else {
                WLAN_PORT += "0";
            }
            if (switch_ssid1.isChecked()) {
                WLAN_PORT += "1";
            } else {
                WLAN_PORT += "0";
            }
            if (switch_ssid2.isChecked()) {
                WLAN_PORT += "1";
            } else {
                WLAN_PORT += "0";
            }
            if (switch_ssid3.isChecked()) {
                WLAN_PORT += "1";
            } else {
                WLAN_PORT += "0";
            }
            if (switch_ssid4.isChecked()) {
                WLAN_PORT += "1";
            } else {
                WLAN_PORT += "0";
            }
            WLAN_PORT += "0000";
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private void commit() {
        Thread t = new Thread() {
            @Override
            public void run() {
                int readLen = 0;
                try {
                    readLen = TelnetUtil.telnetIn.read(buff, 0, buff.length);
                    String text = new String(buff, 0, readLen).trim();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        t.start();
    }

    String cmd;
    Runnable r1, r2, r3, r4, r5, r6, r7, r8, r9, r10, r11, r12, r13, r14, r15, r22, r23, R0;

    String getStrSub(String text, String Type) {
        if (text.length() > 0 && text.indexOf(Type) != -1) {
            return text.substring(text.indexOf(Type), text.length());
        } else {
            return text;
        }
    }

    int count = 0;

    void exec(String cmdStr) {
        cmd = cmdStr;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    switch (cmd) {
                        case GET_CONNECTIONMODE:
                            try {
                                if (!key3) {
                                    int readLen = 0;
                                    readLen = TelnetUtil.telnetIn.read(buff, 0, buff.length);
                                    Log.i("清除IO数据", readLen + "");
                                    buff = new byte[2048 * 4];
                                    if (readLen > 0) {
                                        String text = new String(buff, 0, readLen).trim();
                                        Log.i("清除IO数据", text);
                                    }
                                    key3 = true;
                                }
                                TelnetUtil.writeText(GET_CONNECTIONMODE);
                                Thread.sleep(500);
                                count++;
                                Log.i("count1", count+"");
                            } catch (Exception e) {
                                e.printStackTrace();
                                myToast("CONNECTIONMODE刷新失败");
                                count++;
                                Log.i("count2", count+"");
                                if (count > 6) {
//                                    handler.sendEmptyMessage(1000);
                                    handler.sendEmptyMessage(102);
                                } else {
//                                    //handler.sendEmptyMessage(102);
                                    exec(GET_CONNECTIONMODE);
                                }

                            }
                            r1 = new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        buff = new byte[2048];
                                        TelnetUtil.setIOStream();
                                        int readLen = 0;
                                        readLen = TelnetUtil.telnetIn.read(buff, 0, buff.length);
                                        String text = new String(buff, 0, readLen).trim();
                                        text = getStrSub(text, GET_CONNECTIONMODE);
                                        Log.i("控制台", text);
                                        text = text.replaceAll("#", "").replaceAll("(\r\n|\r|\n|\n\r)", "").replaceAll(":", "").replaceAll(GET_CONNECTIONMODE, "").replaceAll(" ", "");
                                        Log.i("connect mode", text);
                                        if (text.equals("PPPOE") || text.equals("STATIC") || text.equals("DHCP")) {
                                            Message msg = handler.obtainMessage();
                                            msg.what = 0;
                                            msg.obj = text;
                                            handler.sendMessage(msg);
                                        } else {
//                                            //handler.sendEmptyMessage(102);
                                            try {
                                                Thread.sleep(2000);
                                            } catch (InterruptedException e1) {
                                                e1.printStackTrace();
                                            }
                                            exec(GET_CONNECTIONMODE);
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        myToast("CONNECTIONMODE刷新失败");
                                        try {
                                            Thread.sleep(100);
                                        } catch (InterruptedException e1) {
                                            e1.printStackTrace();
                                        }
                                        count++;
                                        if (count >= 6) {
                                         //   handler.sendEmptyMessage(1000);
                                            handler.sendEmptyMessage(102);
                                        } else {
                                           // //handler.sendEmptyMessage(102);
                                            exec(GET_CONNECTIONMODE);
                                        }
                                    } finally {
                                        handler.removeCallbacks(r1);
                                    }
                                }

                            };
                            handler.post(r1);
                            break;
                        case GET_DHCP_OPTION:
                            TelnetUtil.writeText(GET_DHCP_OPTION);
                            Thread.sleep(500);
                            count=0;
                            r2 = new Runnable() {

                                @Override
                                public void run() {
                                    try {
                                        TelnetUtil.setIOStream();
                                        int readLen = 0;
                                        readLen = TelnetUtil.telnetIn.read(buff, 0, buff.length);
                                        String text = new String(buff, 0, readLen).trim();
                                        text = getStrSub(text, GET_DHCP_OPTION);
                                        Log.i("控制台", text);
                                        text = text.replaceAll("#", "").replaceAll("(\r\n|\r|\n|\n\r)", "").replaceAll(":", "").replaceAll(GET_DHCP_OPTION, "").replaceAll(" ", "");
                                        Log.i("connect mode", text);
                                        Message msg = handler.obtainMessage();
                                        msg.what = 120;//wangs
                                        msg.obj = text;
                                        handler.sendMessage(msg);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                        myToast("OPTION刷新失败");
                                        // //handler.sendEmptyMessage(102);
                                        exec(GET_CONNECTIONMODE);
                                    } finally {
                                        handler.removeCallbacks(r2);
                                    }
                                }
                            };
                            handler.post(r2);
                            break;
                        case GET_WAN_VENDOR:
                            TelnetUtil.writeText(GET_WAN_VENDOR);
                            Thread.sleep(500);
                            r15 = new Runnable() {

                                @Override
                                public void run() {
                                    try {
                                        TelnetUtil.setIOStream();
                                        int readLen = 0;
                                        readLen = TelnetUtil.telnetIn.read(buff, 0, buff.length);
                                        String text = new String(buff, 0, readLen).trim();
                                        text = getStrSub(text, GET_WAN_VENDOR);
                                        Log.i("控制台", text);
                                        text = text.replaceAll("#", "").replaceAll("(\r\n|\r|\n|\n\r)", "").replaceAll(":", "").replaceAll(GET_WAN_VENDOR, "").replaceAll(" ", "");
                                        Log.i("connect option value", text);
                                        Message msg = handler.obtainMessage();
                                        msg.what = 5;//wangs
                                        msg.obj = text;
                                        handler.sendMessage(msg);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                        myToast("OPTION刷新失败");
                                        //handler.sendEmptyMessage(102);
                                        exec(GET_CONNECTIONMODE);
                                    } finally {
                                        handler.removeCallbacks(r15);
                                    }
                                }
                            };
                            handler.post(r15);
                            break;
                        case GET_STATIC_IP:
                            TelnetUtil.writeText(GET_STATIC_IP);
                            Thread.sleep(500);
                            r3 = new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        TelnetUtil.setIOStream();
                                        int readLen = 0;
                                        readLen = TelnetUtil.telnetIn.read(buff, 0, buff.length);
                                        String text = new String(buff, 0, readLen).trim();
                                        text = getStrSub(text, GET_STATIC_IP);
                                        Log.i("控制台", text);
                                        text = text.replaceAll("#", "").replaceAll("(\r\n|\r|\n|\n\r)", "").replaceAll(":", "").replaceAll(GET_STATIC_IP, "").replaceAll(" ", "");
                                        Log.i("connect mode", text);
                                        Message msg = handler.obtainMessage();
                                        msg.what = 20;
                                        msg.obj = text;
                                        handler.sendMessage(msg);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                        myToast("STATIC_IP刷新失败");
                                        //handler.sendEmptyMessage(102);
                                        exec(GET_CONNECTIONMODE);
                                    } finally {
                                        handler.removeCallbacks(r3);
                                    }
                                }

                            };
                            handler.post(r3);
                            break;
                        case GET_STATIC_NETMASK:
                            TelnetUtil.writeText(GET_STATIC_NETMASK);
                            Thread.sleep(500);
                            r4 = new Runnable() {

                                @Override
                                public void run() {
                                    try {
                                        TelnetUtil.setIOStream();
                                        int readLen = 0;
                                        readLen = TelnetUtil.telnetIn.read(buff, 0, buff.length);
                                        String text = new String(buff, 0, readLen).trim();
                                        text = getStrSub(text, GET_STATIC_NETMASK);
                                        Log.i("控制台", text);
                                        text = text.replaceAll("#", "").replaceAll("(\r\n|\r|\n|\n\r)", "").replaceAll(":", "").replaceAll(GET_STATIC_NETMASK, "").replaceAll(" ", "");
                                        Log.i("connect mode", text);
                                        Message msg = handler.obtainMessage();
                                        msg.what = 21;
                                        msg.obj = text;
                                        handler.sendMessage(msg);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                        myToast("STATIC_NETMASK刷新失败");
                                        //handler.sendEmptyMessage(102);
                                        exec(GET_CONNECTIONMODE);
                                    } finally {
                                    }
                                }

                            };
                            handler.post(r4);
                            break;
                        case GET_STATIC_GATEWAY:
                            TelnetUtil.writeText(GET_STATIC_GATEWAY);
                            Thread.sleep(500);
                            r5 = new Runnable() {

                                @Override
                                public void run() {
                                    try {
                                        TelnetUtil.setIOStream();
                                        int readLen = 0;
                                        readLen = TelnetUtil.telnetIn.read(buff, 0, buff.length);
                                        String text = new String(buff, 0, readLen).trim();
                                        text = getStrSub(text, GET_STATIC_GATEWAY);
                                        Log.i("控制台", text);
                                        text = text.replaceAll("#", "").replaceAll("(\r\n|\r|\n|\n\r)", "").replaceAll(":", "").replaceAll(GET_STATIC_GATEWAY, "").replaceAll(" ", "");
                                        Log.i("connect mode", text);
                                        Message msg = handler.obtainMessage();
                                        msg.what = 22;
                                        msg.obj = text;
                                        handler.sendMessage(msg);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                        myToast("STATIC_GATEWAY刷新失败");
                                        //handler.sendEmptyMessage(102);
                                        exec(GET_CONNECTIONMODE);
                                    } finally {
                                        handler.removeCallbacks(r5);
                                    }
                                }

                            };
                            handler.post(r5);
                            break;
                        case GET_STATIC_DNS:
                            TelnetUtil.writeText(GET_STATIC_DNS);
                            Thread.sleep(500);
                            r6 = new Runnable() {

                                @Override
                                public void run() {
                                    try {
                                        TelnetUtil.setIOStream();
                                        int readLen = 0;
                                        readLen = TelnetUtil.telnetIn.read(buff, 0, buff.length);
                                        String text = new String(buff, 0, readLen).trim();
                                        text = getStrSub(text, GET_STATIC_DNS);
                                        Log.i("控制台", text);
                                        text = text.replaceAll("#", "").replaceAll("(\r\n|\r|\n|\n\r)", "").replaceAll(":", "").replaceAll(GET_STATIC_DNS, "").replaceAll(" ", "");
                                        Log.i("connect mode", text);
                                        Message msg = handler.obtainMessage();
                                        msg.what = 23;
                                        msg.obj = text;
                                        handler.sendMessage(msg);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                        myToast("STATIC_DNS刷新失败");
                                        //handler.sendEmptyMessage(102);
                                        exec(GET_CONNECTIONMODE);
                                    } finally {
                                        handler.removeCallbacks(r6);
                                    }
                                }

                            };
                            handler.post(r6);
                            break;
                        case GET_STATIC_SDNS:
                            TelnetUtil.writeText(GET_STATIC_SDNS);
                            Thread.sleep(500);
                            r7 = new Runnable() {

                                @Override
                                public void run() {
                                    try {
                                        TelnetUtil.setIOStream();
                                        int readLen = 0;
                                        readLen = TelnetUtil.telnetIn.read(buff, 0, buff.length);
                                        String text = new String(buff, 0, readLen).trim();
                                        text = getStrSub(text, GET_STATIC_SDNS);
                                        Log.i("控制台", text);
                                        text = text.replaceAll("#", "").replaceAll("(\r\n|\r|\n|\n\r)", "").replaceAll(":", "").replaceAll(GET_STATIC_SDNS, "").replaceAll(" ", "");
                                        Log.i("connect mode", text);
                                        Message msg = handler.obtainMessage();
                                        msg.what = 40;
                                        msg.obj = text;
                                        handler.sendMessage(msg);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                        myToast("STATIC_SDNS刷新失败");
                                        //handler.sendEmptyMessage(102);
                                        exec(GET_CONNECTIONMODE);
                                    } finally {
                                        handler.removeCallbacks(r7);
                                    }
                                }

                            };
                            handler.post(r7);
                            break;
                        case GET_SERVICE_VLANID:
                            TelnetUtil.writeText(GET_SERVICE_VLANID);
                            Thread.sleep(500);
                            r8 = new Runnable() {

                                @Override
                                public void run() {
                                    try {
                                        TelnetUtil.setIOStream();
                                        int readLen = 0;
                                        readLen = TelnetUtil.telnetIn.read(buff, 0, buff.length);
                                        String text = new String(buff, 0, readLen).trim();
                                        text = getStrSub(text, GET_SERVICE_VLANID);
                                        Log.i("控制台", text);
                                        text = text.replaceAll("#", "").replaceAll("(\r\n|\r|\n|\n\r)", "").replaceAll(GET_SERVICE_VLANID, "").replaceAll(" ", "");
                                        Log.i("vlanId", text);
                                        Message msg = handler.obtainMessage();
                                        msg.what = 1;
                                        msg.obj = text;
                                        handler.sendMessage(msg);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                        myToast("VLANID刷新失败");
                                        //handler.sendEmptyMessage(102);
                                        exec(GET_CONNECTIONMODE);
                                    } finally {
                                        handler.removeCallbacks(r8);
                                    }
                                }

                            };
                            handler.post(r8);
                            break;
                        case GET_SERVICE_VLANPRI:
                            TelnetUtil.writeText(GET_SERVICE_VLANPRI);
                            Thread.sleep(500);
                            r9 = new Runnable() {

                                @Override
                                public void run() {
                                    try {
                                        TelnetUtil.setIOStream();
                                        int readLen = 0;
                                        readLen = TelnetUtil.telnetIn.read(buff, 0, buff.length);

                                        String text = new String(buff, 0, readLen).trim();
                                        text = getStrSub(text, GET_SERVICE_VLANPRI);
                                        Log.i("控制台", text);
                                        text = text.replaceAll("#", "").replaceAll("\r\n", "").replaceAll(GET_SERVICE_VLANPRI, "").replaceAll(" ", "");
                                        Log.i("vlanPri", text);
                                        Message msg = handler.obtainMessage();
                                        msg.what = 30;
                                        msg.obj = text;
                                        handler.sendMessage(msg);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                        myToast("VLANPRI刷新失败");
                                        //handler.sendEmptyMessage(102);
                                        exec(GET_CONNECTIONMODE);
                                    } finally {
                                        handler.removeCallbacks(r9);
                                    }
                                }

                            };
                            handler.post(r9);
                            break;
                        case GET_SERVICE_LIST:
                            TelnetUtil.writeText(GET_SERVICE_LIST);
                            Thread.sleep(500);
                            r10 = new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        TelnetUtil.setIOStream();
                                        int readLen = 0;
                                        readLen = TelnetUtil.telnetIn.read(buff, 0, buff.length);

                                        String text = new String(buff, 0, readLen).trim();
                                        text = getStrSub(text, GET_SERVICE_LIST);
                                        Log.i("控制台", text);
                                        text = text.replaceAll("#", "").replaceAll("\r\n", "").replaceAll(GET_SERVICE_LIST, "").replaceAll(" ", "");
                                        Log.i("vlanPri", text);
                                        Message msg = handler.obtainMessage();
                                        msg.what = 2;
                                        msg.obj = text;
                                        handler.sendMessage(msg);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                        myToast("SERVICE_LIST刷新失败");
                                        //handler.sendEmptyMessage(102);
                                        exec(GET_CONNECTIONMODE);
                                    } finally {
                                        handler.removeCallbacks(r10);
                                    }
                                }

                            };
                            handler.post(r10);
                            break;

                        case GET_SERVICE_NAME:
                            TelnetUtil.writeText(GET_SERVICE_NAME);
                            Thread.sleep(500);
                            r14 = new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        TelnetUtil.setIOStream();
                                        int readLen = 0;
                                        readLen = TelnetUtil.telnetIn.read(buff, 0, buff.length);
                                        String text = new String(buff, 0, readLen).trim();
                                        text = getStrSub(text, GET_SERVICE_NAME);
                                        Log.i("控制台", text);
                                        text = text.replaceAll("#", "").replaceAll("\r\n", "").replaceAll(GET_SERVICE_NAME, "").replaceAll(" ", "");
                                        Log.i("vlanPri", text);
                                        Message msg = handler.obtainMessage();
                                        msg.what = 60;
                                        msg.obj = text;
                                        handler.sendMessage(msg);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                        myToast("GET_SERVICE_NAME刷新失败");
                                        //handler.sendEmptyMessage(102);
                                        exec(GET_CONNECTIONMODE);
                                    } finally {
                                        handler.removeCallbacks(r14);
                                    }
                                }

                            };
                            handler.post(r14);
                            break;


                        case GET_SERVICE_PORTMAP:
                            TelnetUtil.writeText(GET_SERVICE_PORTMAP);
                            Thread.sleep(500);
                            r11 = new Runnable() {

                                @Override
                                public void run() {
                                    try {
                                        TelnetUtil.setIOStream();
                                        int readLen = 0;
                                        readLen = TelnetUtil.telnetIn.read(buff, 0, buff.length);
                                        String text = new String(buff, 0, readLen).trim();
                                        text = getStrSub(text, GET_SERVICE_PORTMAP);
                                        Log.i("控制台", text);
                                        text = text.replaceAll("#", "").replaceAll("(\r\n|\r|\n|\n\r)", "").replaceAll(GET_SERVICE_PORTMAP, "").replaceAll(" ", "");
                                        Log.i("PORTMAP", text);
                                        Message msg = handler.obtainMessage();
                                        msg.what = 3;
                                        msg.obj = text;
                                        handler.sendMessage(msg);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                        myToast("PORTMAP刷新失败");
                                        //handler.sendEmptyMessage(102);
                                        exec(GET_CONNECTIONMODE);
                                    } finally {
                                        handler.removeCallbacks(r11);
                                    }
                                }

                            };
                            handler.post(r11);
                            break;
                        case GET_PPPOE_USER:
                            TelnetUtil.writeText(GET_PPPOE_USER);
                            Thread.sleep(500);
                            r12 = new Runnable() {

                                @Override
                                public void run() {
                                    try {
                                        TelnetUtil.setIOStream();
                                        int readLen = 0;
                                        String text = "";
                                        readLen = TelnetUtil.telnetIn.read(buff, 0, buff.length);
                                        if (readLen > 0)
                                            text = new String(buff, 0, readLen).trim();
                                        text = getStrSub(text, GET_PPPOE_USER);
                                        Log.i("控制台", text);
                                        text = text.replaceAll("#", "").replaceAll("(\r\n|\r|\n|\n\r)", "").replaceAll(GET_PPPOE_USER, "").replaceAll(" ", "");
                                        Log.i("PORTMAP", text);
                                        Message msg = handler.obtainMessage();
                                        msg.what = 4;
                                        msg.obj = text;
                                        handler.sendMessage(msg);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                        myToast("PPPOE_USERNAME刷新失败");
                                        //handler.sendEmptyMessage(102);
                                        exec(GET_CONNECTIONMODE);
                                    } finally {
                                        handler.removeCallbacks(r12);
                                    }
                                }

                            };
                            handler.post(r12);
                            break;
                        case GET_PPPOE_PWD:
                            TelnetUtil.writeText(GET_PPPOE_PWD);
                            Thread.sleep(500);
                            r13 = new Runnable() {

                                @Override
                                public void run() {
                                    try {
                                        TelnetUtil.setIOStream();
                                        int readLen = 0;
                                        String text = "";
                                        readLen = TelnetUtil.telnetIn.read(buff, 0, buff.length);
                                        if (readLen > 0)
                                            text = new String(buff, 0, readLen).trim();
                                        text = getStrSub(text, GET_PPPOE_PWD);
                                        Log.i("控制台", text);
                                        text = text.replaceAll("#", "").replaceAll("(\r\n|\r|\n|\n\r)", "").replaceAll(GET_PPPOE_PWD, "").replaceAll(" ", "");
                                        Log.i("PORTMAP", text);
                                        Message msg = handler.obtainMessage();
                                        msg.what = 41;
                                        msg.obj = text;
                                        handler.sendMessage(msg);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                        myToast("PPPOE_PWD刷新失败");
                                        //handler.sendEmptyMessage(102);
                                        exec(GET_CONNECTIONMODE);
                                    } finally {
                                        handler.removeCallbacks(r13);
                                    }
                                }

                            };
                            handler.post(r13);
                            break;
                        case GET_PPPOE_ERRORCODE:
                            if (spinner_connectionMode.getSelectedItem().toString().trim().equals("PPPOE")) {
                                TelnetUtil.writeText(GET_PPPOE_ERRORCODE);
                                Thread.sleep(200);
                                r23 = new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            if (key1&&key2){
                                                int readLen = 0;
                                                readLen = TelnetUtil.telnetIn.read(buff, 0, buff.length);
                                                String text = "";
                                                text = new String(buff, 0, readLen).trim();
                                                text = getStrSub(text, GET_PPPOE_ERRORCODE);
                                                Log.i("控制台", text);
                                                text = text.replaceAll("#", "").replaceAll("\r\n", "").replaceAll(GET_PPPOE_ERRORCODE, "").replaceAll("web 2860 sys wanIpAddr", "").replaceAll(" ", "");
                                                Log.i("pppoeerrcode", text);
                                                if (text.equals("")) {
                                                    //wangs
//                                                    if (getNet) {
//                                                        handler.sendEmptyMessage(901);
//                                                    } else {
//                                                        handler.sendEmptyMessage(902);
//                                                    }


                                                } else {
                                                    if (text.contains("678")){
                                                        text="678";
                                                    }
                                                    switch (text) {
                                                        case "678":
                                                            handler.sendEmptyMessage(904);
                                                            break;
                                                        case "691":
                                                            handler.sendEmptyMessage(905);
                                                            break;
                                                        case "769":
                                                            handler.sendEmptyMessage(906);
                                                            break;
                                                        default:
                                                            handler.sendEmptyMessage(907);
                                                            break;
                                                    }
//                                                    handler.sendEmptyMessage(902);
                                                }
                                            }

                                        } catch (Exception e) {
                                            e.printStackTrace();
//                                            handler.sendEmptyMessage(902);
                                        } finally {
                                            handler.removeCallbacks(r23);
                                        }
                                    }
                                };
                                handler.post(r23);
                            }

                            break;
                        case GET_SYS_WANIP:
                            if (isContinue) {
                                Log.i("网络状态", "网络状态确认");
                                if (NetWorkUtil.isWifiConnected(mcontext, ssid)) {
                                    Log.i("网络状态", "wifi连接正常");
                                    //TelnetUtil.writeText(GET_SYS_WANIP);
                                    r22 = new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
//                                                Thread.sleep(1000);
//                                                int readLen = 0;
//                                                readLen = TelnetUtil.telnetIn.read(buff, 0, buff.length);
//                                                String text = "";
//                                                if (readLen != 0)
//                                                    text = new String(buff, 0, readLen).trim();
//                                                Log.i("ping--result", text);
//                                                Thread.sleep(1000);
//                                                if (text.indexOf(".") != -1) {
//                                                    //wangs
//                                                    if (getNet) {
//                                                        handler.sendEmptyMessage(901);
//                                                    } else {
//                                                        handler.sendEmptyMessage(902);
//                                                    }
////                                                    handler.sendEmptyMessage(901);
//                                                } else {
//                                                    handler.sendEmptyMessage(902);
//                                                    exec(GET_PPPOE_ERRORCODE);
//                                                }
                                                if (getNet) {
                                                    handler.sendEmptyMessage(901);
                                                } else {
                                                    handler.sendEmptyMessage(902);
                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                                handler.sendEmptyMessage(902);
                                            } finally {
                                                handler.removeCallbacks(r22);
                                            }
                                        }
                                    };
                                } else {
                                    Log.i("网络状态", "wifi连接异常");
                                    handler.sendEmptyMessage(903);
                                }
                                handler.post(r22);
                                //逻辑处理
                                handler.sendEmptyMessageDelayed(900, 10*1000);//
                            }
                            break;
                        case SET_SERVICE_VLANID:
                            try {
                                Log.i("ConfigActivity", "上传配置中...");
                                myToast("上传配置中...");
                                TelnetUtil.writeText(INIT1);
                                Thread.sleep(500);
                                TelnetUtil.writeText(INIT2);
                                Thread.sleep(500);
                                TelnetUtil.writeText(INIT3);
                                Thread.sleep(500);
                                TelnetUtil.writeText(SET_SERVICE_VLANID + VLAN_ID);
                                Thread.sleep(500);
                                Log.i("ConfigActivity", "SET_SERVICE_VLANID...");
                                handler.sendEmptyMessage(6);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                                myToast("上传失败，请重新上传");
                                key1 = true;
                                key2 = true;
                                WeiboDialogUtils.closeDialog(mWeiboDialog);
                                // exec( GET_CONNECTIONMODE);
                                isContinue = true;
                                handler.sendEmptyMessage(900);
                            }
                            break;
                        case SET_SERVICE_VLANPRI:
                            try {
                                Log.i("ConfigActivity", "SET_SERVICE_VLANPRI...");
                                TelnetUtil.writeText(SET_SERVICE_VLANPRI + VLAN_PRI);
                                Thread.sleep(500);
                                handler.sendEmptyMessage(7);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                                myToast("上传失败，请重新上传");
                                key1 = true;
                                key2 = true;
                                WeiboDialogUtils.closeDialog(mWeiboDialog);
                                // exec( GET_CONNECTIONMODE);
                                isContinue = true;
                                handler.sendEmptyMessage(900);
                            }
                            break;

                        case SET_SERVICE_WLANPORT:
                            try {
                                Log.i("ConfigActivity", "SET_SERVICE_WLANPORT...");
                                if (WLAN_PORT.length() == 12) {
                                    TelnetUtil.writeText(SET_SERVICE_WLANPORT + WLAN_PORT);
                                    Thread.sleep(500);
                                } else {
                                    WLAN_PORT = "";
                                }
                                handler.sendEmptyMessage(8);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                                myToast("上传失败，请重新上传");
                                key1 = true;
                                key2 = true;
                                WeiboDialogUtils.closeDialog(mWeiboDialog);
                                // exec( GET_CONNECTIONMODE);
                                isContinue = true;
                                handler.sendEmptyMessage(900);
                            }
                            break;

                        case SET_SERVICE_LIST:
                            try {
                                Log.i("ConfigActivity", "SET_SERVICE_LIST...");
                                switch (Spinner_fwbd.getSelectedItem().toString().trim()) {
                                    case "INTERNET":
                                        TelnetUtil.writeText(SET_SERVICE_LIST + "INTERNET");
                                        break;
                                    case "TR069":
                                        TelnetUtil.writeText(SET_SERVICE_LIST + "TR069");
                                        break;
                                    case "VOIP":
                                        TelnetUtil.writeText(SET_SERVICE_LIST + "VOIP");
                                        break;
                                    case "OTHER":
                                        TelnetUtil.writeText(SET_SERVICE_LIST + "OTHER");
                                        break;
                                    case "INTERNET_TR069":
                                        TelnetUtil.writeText(SET_SERVICE_LIST + "INTERNET,TR069");
                                        break;
                                    case "TR069_VOIP":
                                        TelnetUtil.writeText(SET_SERVICE_LIST + "TR069,VOIP");
                                        break;
                                }
                                Thread.sleep(500);
                                Thread.sleep(500);
                                handler.sendEmptyMessage(17);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                                myToast("上传失败，请重新上传");
                                key1 = true;
                                key2 = true;
                                WeiboDialogUtils.closeDialog(mWeiboDialog);
                                // exec( GET_CONNECTIONMODE);
                                isContinue = true;
                                handler.sendEmptyMessage(900);
                            }
                            break;

                        case SET_SERVICE_CONNECTION_MODE:
                            try {
                                Log.i("ConfigActivity", "SET_SERVICE_CONNECTION_MODE...");
                                String connectionMode = spinner_connectionMode.getSelectedItem().toString().trim();
                                if ("PPPOE".equals(connectionMode)) {
                                    TelnetUtil.writeText(SET_SERVICE_CONNECTION_MODE + "PPPOE");
                                    Thread.sleep(500);
                                    handler.sendEmptyMessage(9);
                                } else if ("STATIC".equals(connectionMode)) {
                                    TelnetUtil.writeText(SET_SERVICE_CONNECTION_MODE + "STATIC");
                                    Thread.sleep(500);
                                    handler.sendEmptyMessage(12);
                                } else if ("DHCP".equals(connectionMode)) {
                                    TelnetUtil.writeText(SET_SERVICE_CONNECTION_MODE + "DHCP");
                                    Thread.sleep(500);
                                    handler.sendEmptyMessage(11);
                                }
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                                myToast("上传失败，请重新上传");
                                key1 = true;
                                key2 = true;
                                WeiboDialogUtils.closeDialog(mWeiboDialog);
                                // exec( GET_CONNECTIONMODE);
                                isContinue = true;
                                handler.sendEmptyMessage(900);
                            }
                            break;
                        case SET_SERVICE_WLAN_DHCP:
                            try {
                                Log.i("ConfigActivity", "SET_SERVICE_WLAN_DHCP...");
                                if (!switch_option.isChecked()) {
                                    TelnetUtil.writeText(SET_SERVICE_WLAN_DHCP + "0");
                                    Thread.sleep(500);
                                } else {
                                    TelnetUtil.writeText(SET_SERVICE_WLAN_DHCP + "1");
                                    Thread.sleep(500);
                                    TelnetUtil.writeText(SET_SERVICE_WLAN_OPTION + et_option.getText().toString().trim());
                                    Thread.sleep(500);
                                }

                                Thread.sleep(500);
                                handler.sendEmptyMessage(90);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                                myToast("上传失败，请重新上传");
                                key1 = true;
                                key2 = true;
                                WeiboDialogUtils.closeDialog(mWeiboDialog);
                                // exec( GET_CONNECTIONMODE);
                                isContinue = true;
                                handler.sendEmptyMessage(900);
                            }
                            break;
                        case SET_SERVICE_WLAN_USERNAME:
                            try {
                                Log.i("ConfigActivity", "SET_SERVICE_WLAN_USERNAME...");
                                TelnetUtil.writeText(SET_SERVICE_WLAN_USERNAME + WLAN_USERNAME);
                                Thread.sleep(500);
                                handler.sendEmptyMessage(10);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                                myToast("上传失败，请重新上传");
                                key1 = true;
                                key2 = true;
                                WeiboDialogUtils.closeDialog(mWeiboDialog);
                                // exec( GET_CONNECTIONMODE);
                                isContinue = true;
                                handler.sendEmptyMessage(900);
                            }
                            break;
                        case SET_SERVICE_WLAN_PWD:
                            try {
                                Log.i("ConfigActivity", "SET_SERVICE_WLAN_PWD...");
                                TelnetUtil.writeText(SET_SERVICE_WLAN_PWD + WLAN_PWD);
                                Thread.sleep(500);
                                handler.sendEmptyMessage(90);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                                myToast("上传失败，请重新上传");
                                key1 = true;
                                key2 = true;
                                WeiboDialogUtils.closeDialog(mWeiboDialog);
                                // exec( GET_CONNECTIONMODE);
                                isContinue = true;
                                handler.sendEmptyMessage(900);
                            }
                            break;
                        case SET_SERVICE_STATIC_IP:
                            try {
                                Log.i("ConfigActivity", "SET_SERVICE_STATIC_IP...");
                                TelnetUtil.writeText(SET_SERVICE_STATIC_IP + SIP);
                                Thread.sleep(500);
                                handler.sendEmptyMessage(13);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                                myToast("上传失败，请重新上传");
                                key1 = true;
                                key2 = true;
                                WeiboDialogUtils.closeDialog(mWeiboDialog);
                                // exec( GET_CONNECTIONMODE);
                                isContinue = true;
                                handler.sendEmptyMessage(900);
                            }
                            break;
                        case SET_SERVICE_STATIC_GETWAY:
                            try {
                                Log.i("ConfigActivity", "SET_SERVICE_STATIC_GETWAY...");
                                TelnetUtil.writeText(SET_SERVICE_STATIC_GETWAY + GATWAY);
                                Thread.sleep(500);
                                handler.sendEmptyMessage(14);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                                myToast("上传失败，请重新上传");
                                key1 = true;
                                key2 = true;
                                WeiboDialogUtils.closeDialog(mWeiboDialog);
                                // exec( GET_CONNECTIONMODE);
                                isContinue = true;
                                handler.sendEmptyMessage(900);
                            }
                            break;
                        case SET_SERVICE_STATIC_NETMASK:
                            try {
                                Log.i("ConfigActivity", "SET_SERVICE_STATIC_NETMASK...");
                                TelnetUtil.writeText(SET_SERVICE_STATIC_NETMASK + NETMASK);
                                Thread.sleep(500);
                                handler.sendEmptyMessage(15);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                                myToast("上传失败，请重新上传");
                                key1 = true;
                                key2 = true;
                                WeiboDialogUtils.closeDialog(mWeiboDialog);
                                // exec( GET_CONNECTIONMODE);
                                isContinue = true;
                                handler.sendEmptyMessage(900);
                            }
                            break;
                        case SET_SERVICE_STATIC_DNS:
                            try {
                                Log.i("ConfigActivity", "SET_SERVICE_STATIC_DNS...");
                                TelnetUtil.writeText(SET_SERVICE_STATIC_DNS + DNS);
                                Thread.sleep(500);
                                handler.sendEmptyMessage(16);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                                myToast("上传失败，请重新上传");
                                key1 = true;
                                key2 = true;
                                WeiboDialogUtils.closeDialog(mWeiboDialog);
                                // exec( GET_CONNECTIONMODE);
                                isContinue = true;
                                handler.sendEmptyMessage(900);
                            }
                            break;
                        case SET_SERVICE_STATIC_SDNS:
                            try {
                                Log.i("ConfigActivity", "SET_SERVICE_STATIC_SDNS...");
                                TelnetUtil.writeText(SET_SERVICE_STATIC_SDNS + SDNS);
                                Thread.sleep(500);
                                handler.sendEmptyMessage(90);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                                myToast("上传失败，请重新上传");
                                key1 = true;
                                key2 = true;
                                WeiboDialogUtils.closeDialog(mWeiboDialog);
                                // exec( GET_CONNECTIONMODE);
                                isContinue = true;
                                handler.sendEmptyMessage(900);
                            }
                            break;
                        case SET_SERVICE_NAME:
                            try {
                                Log.i("ConfigActivity", "SET_SERVICE_NAME...");
                                TelnetUtil.writeText(SET_SERVICE_NAME + serviceName);
                                Thread.sleep(500);
                                handler.sendEmptyMessage(91);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                                myToast("上传失败，请重新上传");
                                key1 = true;
                                key2 = true;
                                WeiboDialogUtils.closeDialog(mWeiboDialog);
                                // exec( GET_CONNECTIONMODE);
                                isContinue = true;
                                handler.sendEmptyMessage(900);
                            }
                            break;
                        case MAKE_USE1:
                            TelnetUtil.writeText(SET_SERVICE_ENABLE + "1");
                            Thread.sleep(500);
                            TelnetUtil.writeText(SET_SERVICE_MODE + "1");
                            Thread.sleep(500);
                            TelnetUtil.writeText(SECDNS);
                            Thread.sleep(500);
                            TelnetUtil.writeText(SECDNS2);
                            Thread.sleep(500);
                            TelnetUtil.writeText(SECDNS3);
                            Thread.sleep(500);
                            Thread.sleep(500);
                            TelnetUtil.writeText(SECDNS4);
                            Thread.sleep(500);
                            TelnetUtil.writeText(SECDNS5);
                            Thread.sleep(500);
                            TelnetUtil.writeText(SECDNS6);
                            Thread.sleep(500);
                            Thread.sleep(500);
                            TelnetUtil.writeText(SECDNS7);
                            Thread.sleep(500);
                            TelnetUtil.writeText(SECDNS8);
                            Thread.sleep(500);
                            Thread.sleep(500);
                            TelnetUtil.writeText(SECDNS9);
                            Thread.sleep(500);
                            TelnetUtil.writeText(MAKE_USE1);
                            Thread.sleep(500);
                            TelnetUtil.writeText(MAKE_USE2);
                            Thread.sleep(500);
                            TelnetUtil.writeText(MAKE_USE3);
                            Thread.sleep(500);
                            TelnetUtil.writeText(MAKE_USE4);
                            Thread.sleep(500);
                            TelnetUtil.writeText(MAKE_USE5);
                            Thread.sleep(500);
                            TelnetUtil.writeText(MAKE_USE6);
                            Thread.sleep(500);
                            TelnetUtil.writeText(MAKE_USE7);
                            Thread.sleep(500);
                            TelnetUtil.writeText(MAKE_USE8);
                            Log.i("ConfigActivity", "刷新配置...");
                            myToast("上传完成，设备加载...");
                            handler.sendEmptyMessage(101);
                            Thread.sleep(20000);
                            handler.sendEmptyMessage(50);
                            break;
                        default:
                            break;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {

                }
            }
        }).start();
    }

    private String serviceName = "";

    void initUI(String text, String name) {
     /*   if (text.length() > 4) {
            text = text.substring(2, text.length() - 2);
        }*/
        Log.i("ceshi", name);
        switch (name) {
            case GET_CONNECTIONMODE:
                Log.i("ceshi", name);
                SpinnerAdapter apsAdapter = spinner_connectionMode.getAdapter();
                int k = apsAdapter.getCount();
                for (int i = 0; i < k; i++) {
                    if (text.equals(apsAdapter.getItem(i).toString())) {
//                        ll_dhcp.setVisibility(View.VISIBLE);
                        spinner_connectionMode.setSelection(i, true);
                        break;
                    }
                }
                break;
            case GET_SERVICE_VLANID://WAN VLAN  VLAN=0 表示关闭
                if ("0".equals(text.trim())) {
                    vlan_switch.setChecked(false);
                    vlanId_text.setText(text);
                } else {
                    vlan_switch.setChecked(true);
                    vlanId_text.setText(text);
                }
                break;
            case GET_SERVICE_VLANPRI://WAN 优先级
                text_vlanPri.setText(text);
                break;
            case GET_WAN_VENDOR:
                et_option.setText(text);
                break;
            case GET_SERVICE_PORTMAP:  //WAN 端口绑定 12 位依次为:4 位为 LAN1~LAN4 ,4 位为SSID1~SSID4， 4 位预留(默认全 0)
                if (text.length() == 12 || text.length() == 8) {
                    if ("0".equals(text.substring(0, 1))) {
                        switch_lan1.setChecked(false);
                    } else {
                        switch_lan1.setChecked(true);
                    }
                    if ("0".equals(text.substring(1, 2))) {
                        switch_lan2.setChecked(false);
                    } else {
                        switch_lan2.setChecked(true);
                    }
                    if ("0".equals(text.substring(2, 3))) {
                        switch_lan3.setChecked(false);
                    } else {
                        switch_lan3.setChecked(true);
                    }
                    if ("0".equals(text.substring(3, 4))) {
                        switch_lan4.setChecked(false);
                    } else {
                        switch_lan4.setChecked(true);
                    }
                    if ("0".equals(text.substring(4, 5))) {
                        switch_ssid1.setChecked(false);
                    } else {
                        switch_ssid1.setChecked(true);
                    }
                    if ("0".equals(text.substring(5, 6))) {
                        switch_ssid2.setChecked(false);
                    } else {
                        switch_ssid2.setChecked(true);
                    }
                    if ("0".equals(text.substring(6, 7))) {
                        switch_ssid3.setChecked(false);
                    } else {
                        switch_ssid3.setChecked(true);
                    }
                    if ("0".equals(text.substring(7, 8))) {
                        switch_ssid4.setChecked(false);
                    } else {
                        switch_ssid4.setChecked(true);
                    }
                }
                break;
            case GET_PPPOE_USER:
                mEmailView.setText(text);
                break;
            case GET_PPPOE_PWD:
                mPasswordView.setText(text);
                break;
            case GET_DHCP_OPTION:
                if ("0".equals(text)) {
                    switch_option.setChecked(false);
//                    ll_dhcp.setVisibility(View.GONE);
                } else {
                    switch_option.setChecked(true);
                    et_option.setText(text);
                }
                break;
            case GET_STATIC_IP:
                et_ip.setText(text);
                break;
            case GET_STATIC_NETMASK:
                et_zwy.setText(text);
                break;
            case GET_STATIC_GATEWAY:
                et_wg.setText(text);
                break;
            case GET_STATIC_DNS:
                et_dns.setText(text);
                break;
            case GET_STATIC_SDNS:
                et_BDNS.setText(text);
                break;
            case GET_SERVICE_NAME:
                if (text.indexOf("1_INTERNET_R_VID_") == -1) {
                    serviceName = text;
                }
                break;
            case GET_SERVICE_LIST:
                Log.i("GET_SERVICE_LIST", text);
                switch (text) {
                    case "INTERNET":
                        Spinner_fwbd.setSelection(0, true);
                        break;
                    case "TR069":
                        Spinner_fwbd.setSelection(1, true);
                        break;
                    case "VOIP":
                        Spinner_fwbd.setSelection(2, true);
                        break;
                    case "OTHER":
                        Spinner_fwbd.setSelection(3, true);
                        break;
                    case "INTERNET,TR069":
                        Spinner_fwbd.setSelection(4, true);
                        break;
                    case "TR069,VOIP":
                        Spinner_fwbd.setSelection(5, true);
                        break;
                    default:
                        break;
                }
                break;
            default:
                break;
        }

    }

    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);
//        addToolBar();
        TelnetUtil.setContext(ConfigActivity.this);
        // Set up the login form.
        findViewById(R.id.back).setOnClickListener(this);
        findViewById(R.id.action_commit).setOnClickListener(this);
        findViewById(R.id.action_refresh).setOnClickListener(this);
        mEmailView = (AutoCompleteTextView) findViewById(R.id.username);
        populateAutoComplete();
        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });
        Intent _intent = getIntent();
        if (_intent != null) {
            this.ssid = _intent.getStringExtra("ssid");
            this.pwd = _intent.getStringExtra("pwd");
            this.mac = _intent.getStringExtra("mac");
            this.port = _intent.getStringExtra("port");
        }
    /*    Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });*/
        initView();
        mWeiboDialog = WeiboDialogUtils.createLoadingDialog(mcontext, "刷新中...");
        exec(GET_CONNECTIONMODE);
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 0:
                        initUI((String) msg.obj, GET_CONNECTIONMODE);
                        exec(GET_SERVICE_VLANID);
                        break;
                    case 1:
                        initUI((String) msg.obj, GET_SERVICE_VLANID);
                        exec(GET_SERVICE_VLANPRI);
                        break;
                    case 30:
                        initUI((String) msg.obj, GET_SERVICE_VLANPRI);
                        exec(GET_SERVICE_LIST);
                        break;
                    case 2:
                        initUI((String) msg.obj, GET_SERVICE_NAME);
                        exec(GET_SERVICE_NAME);
                        break;
                    case 60:
                        initUI((String) msg.obj, GET_SERVICE_LIST);
                        exec(GET_SERVICE_PORTMAP);
                        break;
                    case 3:
                        initUI((String) msg.obj, GET_SERVICE_PORTMAP);
                        if ("PPPOE".equals(spinner_connectionMode.getSelectedItem().toString())) {
                            exec(GET_PPPOE_USER);
                        } else if ("DHCP".equals(spinner_connectionMode.getSelectedItem().toString())) {
                            exec(GET_DHCP_OPTION);
                        } else if ("STATIC".equals(spinner_connectionMode.getSelectedItem().toString())) {
                            exec(GET_STATIC_IP);
                        }
                        break;
                    case 4:
                        initUI((String) msg.obj, GET_PPPOE_USER);
                        exec(GET_PPPOE_PWD);
                        break;
                    //vlan id
                    case 120:
                        initUI((String) msg.obj, GET_DHCP_OPTION);
                        if ("0".equals((String) msg.obj)) {
                            //get结束

                            key1 = true;
                            key2 = true;
                            WeiboDialogUtils.closeDialog(mWeiboDialog);
                            Log.i("---", "初始化结束");
                            startNotifyTime();
                            try {
                                TelnetUtil.setIOStream();
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                            isContinue = true;
                            handler.sendEmptyMessage(900);
                        } else if ("1".equals((String) msg.obj)) {
                            exec(GET_WAN_VENDOR);
                        }
                        break;
                    case 5:
                        //get结束
                        initUI((String) msg.obj, GET_DHCP_OPTION);
                        key1 = true;
                        key2 = true;
                        WeiboDialogUtils.closeDialog(mWeiboDialog);
                        Log.i("---", "初始化结束");
                        startNotifyTime();
                        try {
                            TelnetUtil.setIOStream();
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        isContinue = true;
                        handler.sendEmptyMessage(900);
                        break;
                    case 6:
                        exec(SET_SERVICE_VLANPRI);
                        break;
                    case 7:
                        exec(SET_SERVICE_WLANPORT);
                        break;
                    case 8:
                        exec(SET_SERVICE_LIST);
                        break;
                    case 9:
                        exec(SET_SERVICE_WLAN_USERNAME);
                        break;
                    case 10:
                        exec(SET_SERVICE_WLAN_PWD);
                        break;
                    case 11:
                        exec(SET_SERVICE_WLAN_DHCP);
                        break;
                    case 12:
                        exec(SET_SERVICE_STATIC_IP);
                        break;
                    case 13:
                        exec(SET_SERVICE_STATIC_GETWAY);
                        break;
                    case 14:
                        exec(SET_SERVICE_STATIC_NETMASK);
                        break;
                    case 15:
                        exec(SET_SERVICE_STATIC_DNS);
                        break;
                    case 16:
                        exec(SET_SERVICE_STATIC_SDNS);
                        break;
                    case 17:
                        exec(SET_SERVICE_CONNECTION_MODE);
                        break;
                    case 20:
                        initUI((String) msg.obj, GET_STATIC_IP);
                        exec(GET_STATIC_NETMASK);
                        break;
                    case 21:
                        initUI((String) msg.obj, GET_STATIC_NETMASK);
                        exec(GET_STATIC_GATEWAY);
                        break;
                    case 22:
                        initUI((String) msg.obj, GET_STATIC_GATEWAY);
                        exec(GET_STATIC_DNS);
                        break;
                    case 23:
                        initUI((String) msg.obj, GET_STATIC_DNS);
                        exec(GET_STATIC_SDNS);
                        break;
                    case 40:
                        initUI((String) msg.obj, GET_STATIC_SDNS);
                        key1 = true;
                        key2 = true;
                        WeiboDialogUtils.closeDialog(mWeiboDialog);
                        Log.i("---", "初始化结束");
                        startNotifyTime();
                        isContinue = true;
                        handler.sendEmptyMessage(900);
                        try {
                            TelnetUtil.setIOStream();
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        break;
                    case 41:
                        initUI((String) msg.obj, GET_PPPOE_PWD);
                        key1 = true;
                        key2 = true;
                        WeiboDialogUtils.closeDialog(mWeiboDialog);
                        Log.i("---", "初始化结束");
                        startNotifyTime();
                        try {
                            TelnetUtil.setIOStream();
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        isContinue = true;
                        handler.sendEmptyMessage(900);
                        break;
                    case 91:
                        exec(MAKE_USE1);
                        break;
                    case 90:
                        if (serviceName.equals("")) {
                            exec(MAKE_USE1);
                        } else {
                            exec(SET_SERVICE_NAME);
                        }
                        break;
                    case 50:
                        //  key1 = true;
    /*                    WeiboDialogUtils.closeDialog(mWeiboDialog);
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        mWeiboDialog=WeiboDialogUtils.createLoadingDialog(mcontext,"刷新配置");*/
                        //   exec( GET_CONNECTIONMODE);
                        myToast("上传成功");
                        key1 = true;
                        key2 = true;
                        WeiboDialogUtils.closeDialog(mWeiboDialog);
                        // exec( GET_CONNECTIONMODE);
                        isContinue = true;
                        handler.sendEmptyMessage(900);
                        break;
                    case 101:
                        WeiboDialogUtils.closeDialog(mWeiboDialog);
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        mWeiboDialog = WeiboDialogUtils.createLoadingDialog(mcontext, "设备加载中...");
                        break;
                    case 102:
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        WeiboDialogUtils.closeDialog(mWeiboDialog);
                        key1=true;
                        key2=true;
                        count=0;
                        myToast("刷新失败，请稍后手动刷新");
                    /*    try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        mWeiboDialog = WeiboDialogUtils.createLoadingDialog(mcontext, "重新刷新...");*/
                        break;
                    case 900:
//                        tv_errorcode.setText("");
                        exec(GET_SYS_WANIP);
                        break;
                    case 901:
                        net_iv.setImageResource(R.mipmap.on);
                        tv_errorcode.setText("");
                        break;
                    case 902:
                        net_iv.setImageResource(R.mipmap.off);
                        Log.i(TAG,"902");
                        if ("PPPOE".equals(spinner_connectionMode.getSelectedItem().toString())&&key1&&key2){
                            Log.i(TAG,"ddddd");
                            exec(GET_PPPOE_ERRORCODE);
                        }
                        break;
                    case 903:
                        net_iv.setImageResource(R.mipmap.off);
                        tv_errorcode.setText("无线已断开");

                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                myToast("请重新连接");
                                finish();
                            }
                        },500);
                        break;
                    case 904:
                        tv_errorcode.setText("连接超时");
                        break;
                    case 905:
                        tv_errorcode.setText("认证失败");
                        break;
                    case 906:
                        tv_errorcode.setText("网卡禁用");
                        break;
                    case 907:
                        tv_errorcode.setText("拨号异常");
                        break;
                    case 1000:
//                        mWeiboDialog = WeiboDialogUtils.createLoadingDialog(mcontext, "重新连接...");
                       /* WifiAdmin wifi = new WifiAdmin(mcontext);
                        wifi.connect(mac, ssid, pwd, 3, mcontext);*/
//                        TelnetUtil telnet = new TelnetUtil(mcontext);
//                        telnet.connect(mac, 8062, mWeiboDialog);
                        break;
                    default:
                        break;
                }
            }
        };
    }

    /*private void addToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }*/


    Switch vlan_switch, switch_lan1, switch_lan2, switch_lan3, switch_lan4, switch_ssid1, switch_ssid2, switch_ssid3, switch_ssid4, switch_option;
    EditText vlanId_text, text_vlanPri, username, password, et_ip, et_zwy, et_wg, et_dns, et_BDNS;
    Spinner spinner_connectionMode, Spinner_fwbd;
    View set_vlanId, set_vlanPri, xh_ov1, xh_ov2;
    LinearLayout ll_static, ll_pppoe, ll_dhcp, ll_option;
    EditText et_option;
    ImageView net_iv;
    TextView tv_errorcode;

    void initView() {
        mconfigScollView = (ScrollView) findViewById(R.id.configscrollView);
        mProgressView = findViewById(R.id.config_progress);
        mconfigScollView.smoothScrollTo(0, 0);
        vlan_switch = (Switch) findViewById(R.id.vlan_switch);
        vlanId_text = (EditText) findViewById(R.id.vlanId_text);
        text_vlanPri = (EditText) findViewById(R.id.text_vlanPri);
        spinner_connectionMode = (Spinner) findViewById(R.id.spinner_connectionMode);
        switch_lan1 = (Switch) findViewById(R.id.switch_lan1);
        switch_lan2 = (Switch) findViewById(R.id.switch_lan2);
        switch_lan3 = (Switch) findViewById(R.id.switch_lan3);
        switch_lan4 = (Switch) findViewById(R.id.switch_lan4);
        switch_ssid1 = (Switch) findViewById(R.id.switch_ssid1);
        switch_ssid2 = (Switch) findViewById(R.id.switch_ssid2);
        switch_ssid3 = (Switch) findViewById(R.id.switch_ssid3);
        switch_ssid4 = (Switch) findViewById(R.id.switch_ssid4);
        switch_option = (Switch) findViewById(R.id.switch_option);
        Spinner_fwbd = (Spinner) findViewById(R.id.Spinner_fwbd);
        ll_static = (LinearLayout) findViewById(R.id.ll_static);
        ll_pppoe = (LinearLayout) findViewById(R.id.ll_pppoe);
        ll_dhcp = (LinearLayout) findViewById(R.id.ll_dhcp);
        ll_option = (LinearLayout) findViewById(R.id.ll_option);
        set_vlanId = findViewById(R.id.set_vlanId);
        set_vlanPri = findViewById(R.id.set_vlanPri);
        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        et_ip = (EditText) findViewById(R.id.et_ip);
        et_zwy = (EditText) findViewById(R.id.et_zwy);
        et_wg = (EditText) findViewById(R.id.et_wg);
        et_dns = (EditText) findViewById(R.id.et_dns);
        et_BDNS = (EditText) findViewById(R.id.et_BDNS);
        et_option = (EditText) findViewById(R.id.et_option);
        xh_ov1 = findViewById(R.id.xh_ov1);
        xh_ov2 = findViewById(R.id.xh_ov2);
        net_iv = (ImageView) findViewById(R.id.net_iv);
        tv_errorcode = (TextView) findViewById(R.id.tv_errorcode);
 /*     set_vlanId.setOnClickListener(viewListener);
        set_vlanPri.setOnClickListener(viewListener);
        vlan_switch.setOnClickListener(viewListener);*/
        vlan_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) {
                    vlanId_text.setText("0");
                    text_vlanPri.setText("");
                    vlanId_text.setEnabled(false);
                    text_vlanPri.setEnabled(false);
                } else {
                    vlanId_text.setText("");
                    vlanId_text.setEnabled(true);
                    text_vlanPri.setEnabled(true);
                    vlanId_text.requestFocus();
                }
            }
        });
        vlanId_text.addTextChangedListener(new myWather(vlanId_text));
        spinner_connectionMode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        Log.i("spinner_connectionMode", "DHCP");
                        ll_pppoe.setVisibility(View.GONE);
                        ll_dhcp.setVisibility(View.VISIBLE);
                        xh_ov1.setVisibility(View.VISIBLE);
                        ll_static.setVisibility(View.GONE);
                        if (switch_option.isChecked()) {
                            ll_option.setVisibility(View.VISIBLE);
                            xh_ov2.setVisibility(View.VISIBLE);
                        } else {
                            ll_option.setVisibility(View.GONE);
                            xh_ov2.setVisibility(View.GONE);
                        }

                        break;
                    case 1:
                        Log.i("spinner_connectionMode", "STATIC");
                        ll_static.setVisibility(View.VISIBLE);
                        ll_pppoe.setVisibility(View.GONE);
                        ll_dhcp.setVisibility(View.GONE);
                        ll_option.setVisibility(View.GONE);
                        xh_ov1.setVisibility(View.GONE);
                        xh_ov2.setVisibility(View.GONE);
                        break;
                    case 2:
                        Log.i("spinner_connectionMode", "PPPOE");
                        ll_pppoe.setVisibility(View.VISIBLE);
                        ll_dhcp.setVisibility(View.GONE);
                        ll_static.setVisibility(View.GONE);
                        ll_option.setVisibility(View.GONE);
                        xh_ov1.setVisibility(View.GONE);
                        xh_ov2.setVisibility(View.GONE);
                        break;
                    default:
                        break;

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        switch_option.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    ll_option.setVisibility(View.VISIBLE);
                    xh_ov2.setVisibility(View.VISIBLE);
                    et_option.requestFocus();
                } else {
                    xh_ov2.setVisibility(View.GONE);
                    ll_option.setVisibility(View.GONE);
                }
            }
        });
    }

    private void populateAutoComplete() {
        if (!mayRequestContacts()) {
            return;
        }

        getLoaderManager().initLoader(0, null, this);
    }

    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(mEmailView, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
                        }
                    });
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateAutoComplete();
            }
        }
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new UserLoginTask(email, password);
            mAuthTask.execute((Void) null);
        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mconfigScollView.setVisibility(show ? View.GONE : View.VISIBLE);
            mconfigScollView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mconfigScollView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mconfigScollView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(ConfigActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmailView.setAdapter(adapter);
    }


    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;

        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            try {
                // Simulate network access.
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                return false;
            }

            for (String credential : DUMMY_CREDENTIALS) {
                String[] pieces = credential.split(":");
                if (pieces[0].equals(mEmail)) {
                    // Account exists, return true if the password matches.
                    return pieces[1].equals(mPassword);
                }
            }

            // TODO: register the new account here.
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                finish();
            } else {
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }
        }


        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }


    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        key1 = true;
        key2 = true;
        System.out.println("按下了back键   onBackPressed()");
        AlertDialog isExit = new AlertDialog.Builder(this).create();
        isExit.setTitle("提示");
        isExit.setMessage("退出配置页面？");
        isExit.setButton(-1, "确定", listener);
        isExit.setButton(-2, "取消", listener);
        isExit.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        TelnetUtil.closeConnect();
        stopNotifyTime();

        handler.removeCallbacksAndMessages(null);
    }

    /*
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            System.out.println("按下了back键   onKeyDown()");
            AlertDialog isExit = new AlertDialog.Builder(this).create();
            isExit.setTitle("系统提示");
            isExit.setMessage("确定要退出吗");
            isExit.setButton(1, "确定", listener);
            isExit.setButton(2, "取消", listener);
            isExit.show();
            return false;
        }else{
            return super.onKeyDown(keyCode,event);
        }
    }*/



/*    View.OnClickListener viewListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.set_vlanId:
                    showVlanDailog();
                    break;
                case R.id.vlan_switch:
                    if (vlan_switch.isChecked()) {
                        showVlanDailog();
                    } else {
                        vlanId_text.setText("0");
                    }
                    break;
                case R.id.set_vlanPri:
                    showVlanPriDailog();
                    break;
                default:
                    break;
            }
        }
    };*/

    private void myToast(String text) {
        final String tempText = text;
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

    //正则验证式
    String reg = "";

    void setReg(String mreg) {
        this.reg = mreg;
    }

    class myWather implements TextWatcher {
        //监听改变的文本框
        private EditText editText;

        public myWather(EditText editText) {
            this.editText = editText;
        }

        @Override
        public void onTextChanged(CharSequence ss, int start, int before, int count) {
            String editable = editText.getText().toString();

        }

        @Override
        public void afterTextChanged(Editable s) {

        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

    }


    public static String stringFilter(String str, String reg) throws PatternSyntaxException {
        if ("0".equals(reg)) {
            if (str == null || "".equals(str)) {
                return "";
            }
            if ("1".equals(str)) {
                return str;
            } else if ("4081".equals(str)) {
                return "408";
            } else if (Integer.parseInt(str) > 4095) {
                return str.substring(0, str.length() - 1);
            } else {
                return str;
            }
        } else if ("1".equals(reg)) {
            try {
                int text = Integer.parseInt(str);
                if (text > 7 || text < 0) {
                    return "";
                } else {
                    return str;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return "";
            }
        } else {
            String regEx = reg;
            Pattern p = Pattern.compile(regEx);
            Matcher m = p.matcher(str);
            return m.replaceAll("").trim();
        }
    }


/*    //设置vlanID数值
    void showVlanDailog() {
        Log.i("viewListener", "设置vlanid");
        AlertDialog.Builder builder = new AlertDialog.Builder(mcontext);
        View view = View
                .inflate(mcontext, R.layout.input_dialog, null);
        builder.setView(view);
        builder.setCancelable(true);
        TextView title = (TextView) view
                .findViewById(R.id.title);//设置标题
        title.setText("VLAN ID [0,2-4080,4082-4095]");
        final EditText input_edt = (EditText) view
                .findViewById(R.id.dialog_edit);//输入内容
        Button btn_cancel = (Button) view
                .findViewById(R.id.dialog_btn_cancel);//取消按钮
        Button btn_comfirm = (Button) view
                .findViewById(R.id.dialog_btn_comfirm);//确定按钮
        final AlertDialog dialog = builder.create();
        setReg("0");
        input_edt.addTextChangedListener(new myWather(input_edt));

        btn_cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        btn_comfirm.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!input_edt.getText().toString().trim().equals("")) {
                    try {
                        String text = Integer.parseInt(input_edt.getText().toString()) + "";
                        if ("1".equals(text)) {
                            myToast("值无效");

                        } else {
                            vlanId_text.setText(text);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        myToast("值无效");
                    }
                    if (input_edt.getText().toString().trim().equals("0")) {
                        vlan_switch.setChecked(false);
                    } else {
                        vlan_switch.setChecked(true);
                    }
                }
                dialog.dismiss();
            }
        });

        dialog.show();
    }*/

/*    //设置优先级
    void showVlanPriDailog() {
        Log.i("viewListener", "设置vlanid");
        AlertDialog.Builder builder = new AlertDialog.Builder(mcontext);
        View view = View
                .inflate(mcontext, R.layout.input_dialog, null);
        builder.setView(view);
        builder.setCancelable(true);
        TextView title = (TextView) view
                .findViewById(R.id.title);//设置标题
        title.setText("优先级 [0-7]");
        final EditText input_edt = (EditText) view
                .findViewById(R.id.dialog_edit);//输入内容
        Button btn_cancel = (Button) view
                .findViewById(R.id.dialog_btn_cancel);//取消按钮
        Button btn_comfirm = (Button) view
                .findViewById(R.id.dialog_btn_comfirm);//确定按钮
        final AlertDialog dialog = builder.create();
        setReg("1");
        input_edt.addTextChangedListener(new myWather(input_edt));

        btn_cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        btn_comfirm.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!"".equals(input_edt.getText().toString().trim()))
                    try {
                        int text = Integer.parseInt(input_edt.getText().toString());
                        if (text > 7 || text < 0) {
                            myToast("值无效");

                        } else {
                            text_vlanPri.setText(text + "");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        myToast("值无效");
                    }
                dialog.dismiss();
            }
        });

        dialog.show();
    }*/


    /*
          判断是否有外网连接（普通方法不能判断外网的网络是否连接，比如连接上局域网）
      */
//    public static final boolean ping() {
    public boolean ping() {
        String result = null;
        try {
            String ip = "202.108.22.5";// ping 的地址，可以换成任何一种可靠的外网
            Process p = Runtime.getRuntime().exec("ping -c 1 -w 100 " + ip);// ping网址3次 "ping -c 3 -w 100 "
            // 读取ping的内容，可以不加
            /*InputStream input = p.getInputStream();
            BufferedReader in = new BufferedReader(new InputStreamReader(input));
            StringBuffer stringBuffer = new StringBuffer();
            String content = "";
            while ((content = in.readLine()) != null) {
                stringBuffer.append(content);
            }
            Log.d("------ping-----", "result content : " + stringBuffer.toString());
            Log.i("ssss","result content :"+stringBuffer.toString());
            // ping的状态*/
            int status = p.waitFor();
//            Log.i("ssss", "yy==" + status);
            if (status == 0) {
                result = "success========================";
                Log.i("ssss", "yy==" + result);
                getNet = true;
                return true;
            } else {
                result = "failed=========================";
                getNet = false;
                Log.i("ssss", "yy==" + result);
            }

        } catch (IOException e) {
            result = "IOException";
        } catch (InterruptedException e) {
            result = "InterruptedException";
        } finally {
            Log.d("----result---", "result = " + result);
        }
        return false;
    }

    /* private String getWifeName(){
         WifiManager wifiManager = (WifiManager) mcontext.getSystemService(WIFI_SERVICE);
         WifiInfo wifiInfo = wifiManager.getConnectionInfo();
         Log.i("ssss", wifiInfo.toString());
         Log.i("ssss",wifiInfo.getSSID());
 //        wifiManager.getConfiguredNetworks();
         return null;
     }*/
    public void startNotifyTime() {
        if (notifyTimeTimer == null) {
            notifyTimeTimer = new Timer();
            notifyTimeTimer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
//                    Log.i("ssss", "ping");
//                    if (key1 && key2)
//                        ping();
                }
            }, 100, 5 * 1000);//5s ping 一次
        }
    }

    public void stopNotifyTime() {
        if (notifyTimeTimer != null) {
            notifyTimeTimer.cancel();
        }
        notifyTimeTimer = null;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.back:
                AlertDialog isExit = new AlertDialog.Builder(this).create();
                isExit.setTitle("提示");
                isExit.setMessage("退出配置页面？");
                isExit.setButton(-1, "确定", listener);
                isExit.setButton(-2, "取消", listener);
                isExit.show();
                break;
            case R.id.action_refresh:
                if (NetWorkUtil.isWifiConnected(mcontext, ssid)) {
                    if (key2) {
                        isContinue = false;
                        Log.i("ConfigActivity", "刷新配置");
                        mWeiboDialog = WeiboDialogUtils.createLoadingDialog(mcontext, "刷新中...");
                        myToast("正在刷新...");
                        exec(GET_CONNECTIONMODE);
                        key2 = false;
                    } else {
                        myToast("请等待刷新完成");
                    }
                } else {
                   /* AlertDialog isExit = new AlertDialog.Builder(this).create();
                    isExit.setTitle("提示");
                    isExit.setMessage("网络已经断开");
                    isExit.show();*/
                    myToast("无线连接已断开");
                }
                break;
            case R.id.action_commit:
                if (NetWorkUtil.isWifiConnected(mcontext, ssid)) {
                    if (key1) {
                        Log.i("ConfigActivity", "上传配置");
                        if (checkNumber()) {
                            isContinue = false;
                            key1 = false;
                            key3 = false;
                            Log.i("ConfigActivity", "上传配置验证通过");
                            mWeiboDialog = WeiboDialogUtils.createLoadingDialog(mcontext, "上传中...");
                            exec(SET_SERVICE_VLANID);
                        }
                    } else {
                        myToast("请等待上传完成");
                    }
                } else {
                    myToast("无线连接已断开");
                }
                break;
        }
    }

    DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {

        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case AlertDialog.BUTTON_POSITIVE:
                    TelnetUtil.closeConnect();
                    finish();
                    break;
                case AlertDialog.BUTTON_NEGATIVE:
                    break;
                default:
                    break;
            }
        }
    };
}

