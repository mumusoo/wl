package com.lechen.wlanconnector;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bstar.WiFi_YOTC;
import com.google.zxing.activity.CaptureActivity;
import com.lechen.wlanconnector.adapter.SwipAdapter;
import com.lechen.wlanconnector.util.WifiAdmin;
import com.lechen.wlanconnector.view.NoScrollListview;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    //    private static final String TAG=MainActivity.class.getSimpleName();
    private static final String TAG = "MainActivity";
    private final static int REQUEST_CODE = 1;
    public final static int REQUEST_CODE_CONNECT=3;
    private NoScrollListview listView = null;
    Context mcontext = this;
    private LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        findViewById(R.id.action_settings).setOnClickListener(this);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
                Log.i("initActionBar", "相机");
                Intent intent = new Intent(MainActivity.this, CaptureActivity.class);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });
        listView = (NoScrollListview) findViewById(R.id.list);
        List<Map<String, Object>> list = getData();
        listView.setAdapter(new SwipAdapter(mcontext, list, listView,this));
        linearLayout = (LinearLayout) findViewById(R.id.linearlayout);
        // linearLayout.getBackground().mutate().setAlpha(170);
/*        listView.setAdapter(new MyAdapter(this, list));listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String sbName=((TextView)view.findViewById(R.id.sbName)).getText().toString().trim();
                String sbPwd=((TextView)view.findViewById(R.id.sbpwd)).getText().toString().trim();
                WifiAdmin wifi = new WifiAdmin(mcontext);
                wifi.connect("", sbName, sbPwd, 3, mcontext);
            }
        });*/

    }

    public List<Map<String, Object>> getData() {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        SharedPreferences read = getSharedPreferences("ip", MODE_PRIVATE);
        String ipStr = read.getString("ip", "");
        String portStr = read.getString("port", "");
        String nameStr = read.getString("name", "");
        String pwdStr = read.getString("pwd", "");

        String[] ipSplit = ipStr.split("#");
        String[] portSplit = portStr.split("#");
        String[] nameSplit = nameStr.split("#");
        String[] pwdSplit = pwdStr.split("#");

        for (int i = 0; i < ipSplit.length; i++) {
            String ip = ipSplit[i];
            String port = portSplit[i];
            String name = "";
            if (nameSplit.length > i)
                name = nameSplit[i];
            String pwd = "";
            if (pwdSplit.length > i)
                pwd = pwdSplit[i];
            if (!"".equals(ip.trim())) {
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("image", R.drawable.wifi);
                map.put("title", ip);
                map.put("sbName", name);
                map.put("sbpwd", pwd);
                list.add(map);
            }
        }
   /*     for (int i = 0; i < 10; i++) {
            Map<String, Object> map=new HashMap<String, Object>();
            map.put("image", R.mipmap.ic_launcher);
            map.put("title", "192.168.1.1");
          //  map.put("info", "这是一个详细信息"+i);
            list.add(map);
        }*/
        return list;
    }


 /*   @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem item = (MenuItem) findViewById(R.id.action_settings);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks off the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent2 = new Intent(MainActivity.this, ConnectActivity.class);
            startActivity(intent2);
        }

        return super.onOptionsItemSelected(item);
    }
*/
 WifiAdmin wifi;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE) {
            if (resultCode == CaptureActivity.RESULT_CODE_QR_SCAN) {
                Bundle bundle = data.getExtras();
                String qrStr = bundle.getString(CaptureActivity.INTENT_EXTRA_KEY_QR_SCAN);
                Log.i(TAG, "" + qrStr);
                Log.i(TAG, "TRUE");


                long mac = 0;
                try {
                    mac = Long.parseLong(qrStr, 16) + 1;
                    String macStr = Long.toHexString(mac);
                    Log.i(TAG, "扫码获取的mac地址：" + qrStr);
                    String ssid = "jxtvnet-" + WiFi_YOTC.createSsid(macStr);
                    String pwd = WiFi_YOTC.createPassword(macStr);
                    Log.i(TAG, "解析后的ssid" + ssid);//wifi名称
                    Log.i(TAG, "解析后的password" + pwd);
                    if(wifi==null)
                    wifi= new WifiAdmin(this,this);
                    wifi.connect("192.168.55.1", ssid, pwd, 3, this, "1");

                } catch (NumberFormatException e) {
                    Toast.makeText(mcontext, "二维码／条形码有误", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }

            }
        }else if(requestCode == REQUEST_CODE_CONNECT){
            Log.i(TAG,"刷新listview");
            listView.setAdapter(new SwipAdapter(mcontext, getData(), listView,this));
        }
    }

    private long exitTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                finish();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.action_settings:
                Intent intent2 = new Intent(MainActivity.this, ConnectActivity.class);
                startActivityForResult(intent2,REQUEST_CODE_CONNECT);
                break;
        }
    }
    
}
