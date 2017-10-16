package com.lechen.wlanconnector.util;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.lechen.wlanconnector.ConfigActivity;

import org.apache.commons.net.telnet.TelnetClient;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by guoy off 2017/8/11.
 */

public class TelnetUtil {
    private static final String TELNET_TYPE = "vt200";//指明Telnet终端类型，否则会返回来的数据中文会乱码
    static String charset = null;
    public static byte[] buff = new byte[2048];
    public static TelnetClient telnetClient;
    public static BufferedReader telnetReader = null;
    public static BufferedWriter telnetWirter = null;
    public static InputStream telnetIn = null;
    public static OutputStream telnetOut = null;
    private String ipAddressd;
    private int portd;
    private static Context mcontext;
    private static Dialog mdialog;
    private final String USER_NAME = "admin";
    private final String PWD = "1qaz2wsx";

    public static void setContext(Context context) {
        mcontext = context;
    }

    public TelnetUtil(Context context) {
        telnetClient = new TelnetClient(TELNET_TYPE);
        telnetClient.setDefaultTimeout(10000); //socket延迟时间：5000ms
        this.mcontext = context;
    }

    public void connect(String ipAddress, Integer port,Dialog dialog) {
        this.ipAddressd = ipAddress;
        this.portd = port;
        this.mdialog=dialog;
        if(dialog==null){

        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    telnetClient.connect(ipAddressd, portd);
                    myToast("设备连接成功");
                    try {
                        setIOStream();
                        login("", "");
                    } catch (IOException e) {
                        e.printStackTrace();
                        myToast("IO异常");
                    } finally {
                    }
                    //  exec("test");
                } catch (Exception e) {
                    e.printStackTrace();
                    myToast("设备连接失败");
                    WeiboDialogUtils.closeDialog(mdialog);
                } finally {
                }
            }
        }).start();
    }


    public static void setIOStream() throws UnsupportedEncodingException {
        if (telnetIn == null)
            telnetIn = telnetClient.getInputStream();
        if (telnetOut == null)
            telnetOut = telnetClient.getOutputStream();
        if (null == charset) {
            telnetReader = new BufferedReader(new InputStreamReader(telnetIn));
            telnetWirter = new BufferedWriter(new OutputStreamWriter(telnetOut));
        } else {
            telnetReader = new BufferedReader(new InputStreamReader(telnetIn, charset));
            telnetWirter = new BufferedWriter(new OutputStreamWriter(telnetOut, charset));
        }
    }

    /**
     * 读取字符串<br/>
     * 相当于readByte()转为字符串
     *
     * @return
     * @throws IOException
     */
    private int readLen;
    public static void readString() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    int readLen = telnetIn.read(buff, 0, buff.length);
                    String text = new String(buff, 0, readLen).trim();
                    Log.i("TelnetUtil", text);
                    myToast(text);
                } catch (IOException e) {
                    e.printStackTrace();
                }finally{

                }

            }
        }).start();
    }


    /**
     * 向服务器写字符串
     *
     * @param text
     * @throws IOException
     */
    private static String textStr;

    public static void writeText(String text) {
        Log.i("writeText","---------------");
        Log.i("写入",text);
        textStr = text;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    telnetWirter.write(textStr);
                    telnetWirter.write('\r');
                    telnetWirter.write('\n');
                    telnetWirter.flush();
                } catch (Exception ex) {
                }
            }
        }).start();
    }

    public void writeBytes(byte[] buff, int offset, int len) throws IOException {
        telnetOut.write(buff, offset, len);
    }

    /**
     * 执行命令，并返回结果<br/>
     * 相当于: <br>
     * writeText();  <br/>
     * return readString();
     *
     * @param cmd
     * @return
     * @throws IOException
     */
    public static void exec(String cmd) throws IOException {
        writeText(cmd);
       // readString();
    }

    /**
     * 关闭
     */
    public static void close() {
        try {
        /*    writeText("exit");
            writeText("exit");
            writeText("exit");*/
        } catch (Exception ex) {
        }
        try {
            if (null != telnetIn) {
                telnetIn.close();
                telnetIn = null;
            }
        } catch (Exception e) {
        }
        try {
            if (null != telnetOut) {
                telnetOut.close();
                telnetOut = null;
            }
        } catch (Exception e) {
        }
   /*     try {
            if (null != telnetClient) telnetClient.disconnect();
        } catch (Exception e) {
        }*/
    }

    /**
     * 关闭
     */
    public static void closeConnect() {
        try {
            writeText("exit");
            writeText("exit");
            writeText("exit");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        try {
            if (null != telnetIn) {
                telnetIn.close();
                telnetIn = null;
            }
        } catch (Exception e) {
        }
        try {
            if (null != telnetOut) {
                telnetOut.close();
                telnetOut = null;
            }
        } catch (Exception e) {
        }
        try {
            if (null != telnetClient) {
                telnetClient.disconnect();
                telnetClient = null;
            }
        } catch (Exception e) {
        }
    }

    /**
     * 设置telnet通信时的字符集<br/>
     * 注:此字符集与服务器端字符集没有必然关系<br/>
     * 此方法需在connect()前调用
     *
     * @param charset
     */
    public void setCharset(String charset) {
        this.charset = charset;
    }

    /**
     * 重新设置buff大小,默认为2048字节.
     *
     * @param size
     */
    public void setBufferSize(int size) {
        this.buff = new byte[size];
    }

    /**
     * 登录
     *
     * @param user
     * @param passwd
     * @return 是否登录成功.
     * @throws IOException
     */
    String data;
    public void login(String user, final String passwd) throws IOException {
       // mdialog = WeiboDialogUtils.createLoadingDialog(mcontext, "连接中...");
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Boolean key = true;
                    int readLen = telnetIn.read(buff, 0, buff.length);
                    String text = new String(buff, 0, readLen);
                    if (text.length() > 0) text = text.trim();
                    Log.i("控制台：", text);
                    int count = 0;
                    while (true) {
                        count++;
                        if (text.length() <= 0 || -1 == text.indexOf("login")) {
                            readLen = telnetIn.read(buff, 0, buff.length);
                            text = new String(buff, 0, readLen).trim();
                            Log.i("控制台：", text);
                            Thread.sleep(500);
                            if (count > 10) {
                                myToast("登陆失败");
                                WeiboDialogUtils.closeDialog(mdialog);
                                key = false;
                                closeConnect();
                                break;
                            }
                        } else {
                            key = true;
                            break;
                        }
                    }
                    if (key) {
                        Thread.sleep(500);
                        writeText(USER_NAME);
                    }
                    count = 0;
                    key = true;
                    while (true) {
                        count++;
                        if (-1 == text.indexOf("Password")) {
                            readLen = telnetIn.read(buff, 0, buff.length);
                            text = new String(buff, 0, readLen).trim();
                            Log.i("控制台：", text);
                            Thread.sleep(500);
                            if (count > 10) {
                                myToast("登陆失败");
                                WeiboDialogUtils.closeDialog(mdialog);
                                key = false;
                                closeConnect();
                                break;
                            }
                        } else {
                            key = true;
                            break;
                        }
                    }
                    if (key) {
                        Thread.sleep(500);
                        writeText(PWD);
                    }
                    Thread.sleep(500);
                    count = 0;
                    key = true;
                    while (true) {
                        count++;
                        if (-1 == text.indexOf("BusyBox")) {
                            readLen = telnetIn.read(buff, 0, buff.length);
                            text = new String(buff, 0, readLen).trim();
                            Log.i("控制台：", text);
                            Thread.sleep(500);
                            if (count > 10) {
                                myToast("登陆失败");
                                WeiboDialogUtils.closeDialog(mdialog);
                                key = false;
                                closeConnect();
                                break;
                            }
                        } else {
                            myToast(text);
                            key = true;
                            break;
                        }
                    }
                    if (key) {
                        myToast("设备登陆成功");
                        WeiboDialogUtils.closeDialog(mdialog);
                        add();
                        Intent intent = new Intent(mcontext, ConfigActivity.class);
                        intent.putExtra("ssid",WifiAdmin.getSsid(mcontext));
                        intent.putExtra("pwd",passwd);
                        intent.putExtra("mac",ipAddressd);
                        intent.putExtra("port",portd);
                        mcontext.startActivity(intent);
                    }
                } catch (IOException|InterruptedException e) {
                    e.printStackTrace();
                    myToast("连接异常，请重新连接");
                    WeiboDialogUtils.closeDialog(mdialog);
                    closeConnect();
                } finally {
                    //close();
                }
            }
        }).start();
    }

    static String tempText;

    private static void myToast(String text) {
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

    void add() {
        SharedPreferences read = mcontext.getSharedPreferences("ip", MODE_PRIVATE);
        SharedPreferences.Editor editor = mcontext.getSharedPreferences("ip", MODE_PRIVATE).edit();
        String nameStr = read.getString("name", "").trim();
        String[] nameSplit = nameStr.split("#");
        Boolean key = true;
        if (nameSplit!=null&&nameSplit.length > 0) {
            for (int i = 0; i < nameSplit.length; i++) {
                if (WifiAdmin.getSsid(mcontext)!=null&&WifiAdmin.getSsid(mcontext).equals(nameSplit[i])) {
                    key = false;
                }
                if(WifiAdmin.getPassword()==null){
                    key = false;
                }
            }
        } else {
            key = false;
        }
        if (key) {
            editor.putString("ip", read.getString("ip", "") + "#" + ipAddressd);
            editor.putString("port", read.getString("port", "") + "#" + portd);
            String ssid=WifiAdmin.getSsid(mcontext);
            String pwd=WifiAdmin.getPassword();
            editor.putString("name", read.getString("name", "") + "#" + ssid);
            editor.putString("pwd", read.getString("pwd", "") + "#" + pwd);
            editor.commit();
        }
    }
}
