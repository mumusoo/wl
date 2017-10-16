package com.lechen.wlanconnector.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lechen.wlanconnector.MainActivity;
import com.lechen.wlanconnector.R;
import com.lechen.wlanconnector.util.WifiAdmin;
import com.lechen.wlanconnector.view.NoScrollListview;
import com.lechen.wlanconnector.view.SwipeItemView;
import com.lechen.wlanconnector.view.SwipeLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;


public class SwipAdapter extends BaseAdapter {
    public static final String TAG = SwipAdapter.class.getSimpleName();
    private List<Map<String, Object>> data;
    private LayoutInflater layoutInflater;
    private Context mcontext;
    private SwipeItemView mLastSlideViewWithStatusOn;
    private NoScrollListview mview;
    private List<SwipeLayout> openList = new ArrayList<SwipeLayout>();
    private Activity mActivity;

    public SwipAdapter(Context context, List<Map<String, Object>> data, NoScrollListview view,Activity activity) {
        this.mview = view;
        this.mcontext = context;
        this.mActivity=activity;
        this.data = data;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        data = getData();
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = layoutInflater.inflate(R.layout.switch_item, null);
            holder.swipeLayout = (SwipeLayout) convertView.findViewById(R.id.swipeLayout);
            holder.icon = (ImageView) convertView.findViewById(R.id.image);
            holder.title = (TextView) convertView.findViewById(R.id.title);
            holder.sbName = (TextView) convertView.findViewById(R.id.sbName);
            holder.sbpwd = (TextView) convertView.findViewById(R.id.sbpwd);
            holder.deleteHolder = (TextView) convertView.findViewById(R.id.deleteHolder);
            holder.llCustomerInfo = (LinearLayout) convertView.findViewById(R.id.ll_customer_info);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.icon.setBackgroundResource((Integer) data.get(position).get("image"));
        holder.title.setText((String) data.get(position).get("title"));
        holder.sbName.setText((String) data.get(position).get("sbName"));
        holder.sbpwd.setText((String) data.get(position).get("sbpwd"));

//        holder.swipeLayout.setTag(R.id.tag_first,position);
        holder.llCustomerInfo.setTag(R.id.tag_first, holder.swipeLayout);//绑定
        //侧滑
        holder.swipeLayout.setSwipeChangeListener(new SwipeLayout.OnSwipeChangeListener() {
            @Override
            public void onStartOpen(SwipeLayout mSwipeLayout) {

                Log.i(TAG, "onStartOpen");
                for (SwipeLayout layout : openList) {
                    layout.close();
                }
                openList.clear();
            }

            @Override
            public void onStartClose(SwipeLayout mSwipeLayout) {
                Log.i(TAG, "onStartClose");
            }

            @Override
            public void onOpen(SwipeLayout mSwipeLayout) {
                Log.i(TAG, "onOpen");
                mview.setNoScroll(false);
                openList.add(mSwipeLayout);
            }

            @Override
            public void onDraging(SwipeLayout mSwipeLayout) {
                mview.setNoScroll(true);
                Log.i(TAG, "onDraging");
            }

            @Override
            public void onClose(SwipeLayout mSwipeLayout) {
                Log.i(TAG, "onClose");
                mview.setNoScroll(false);
                openList.remove(mSwipeLayout);
            }
        });

        //条目点击
        final View finalConvertView = convertView;
        holder.llCustomerInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SwipeLayout layout = (SwipeLayout) v.getTag(R.id.tag_first);
                if (layout.getStatus() == SwipeLayout.Status.CLOSE) {
//                    //在此进行条目点击后的操作
                    String mac = ((TextView) finalConvertView.findViewById(R.id.title)).getText().toString().trim();
                    String sbName = ((TextView) finalConvertView.findViewById(R.id.sbName)).getText().toString().trim();
                    String sbPwd = ((TextView) finalConvertView.findViewById(R.id.sbpwd)).getText().toString().trim();
                    WifiAdmin wifi = new WifiAdmin(mcontext,mActivity);
                    wifi.connect(mac, sbName, sbPwd, 3, mcontext,"1");
                } else {
                    layout.close();
                }
            }
        });
        //删除
        holder.deleteHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO Auto-generated method stub
                data.remove(position);
                Log.i(TAG, "删除了条目" + position);
                notifyDataSetChanged();
                SharedPreferences read = mcontext.getSharedPreferences("ip", MODE_PRIVATE);
                SharedPreferences.Editor editor = mcontext.getSharedPreferences("ip", MODE_PRIVATE).edit();
                String ipStr = "";
                String portStr = "";
                String nameStr = "";
                String pwdStr = "";
                for (int i = 0; i < data.size(); i++) {
                    ipStr = ipStr + "#" + data.get(i).get("title");
                    portStr = portStr + "#" +8062;
                    nameStr = nameStr + "#" + data.get(i).get("sbName");
                    pwdStr = pwdStr + "#" + data.get(i).get("sbpwd");
                }
                editor.putString("ip", ipStr);
                editor.putString("port", portStr);
                editor.putString("name", nameStr);
                editor.putString("pwd", pwdStr);
                editor.commit();

//                int vposition = mview.getSelectedItemPosition();
                mview.setAdapter(new SwipAdapter(mcontext, data, mview, mActivity));
//                mview.setSelection(vposition);

            }
        });
        return convertView;
    }

    public List<Map<String, Object>> getData() {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        SharedPreferences read = mcontext.getSharedPreferences("ip", MODE_PRIVATE);
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
                map.put("title", ip );
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
//    private void ItemOnLongClick() {
//        mview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//
//            @Override
//            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
//                                           final int arg2, long arg3) {
//                data.remove(arg2);
//                new AlertDialog.Builder(mcontext)
//                        .setTitle("对Item进行操作")
//                        .setItems(R.array.arrcontent,
//                                new DialogInterface.OnClickListener() {
//                                    public void onClick(DialogInterface dialog,
//                                                        int which) {
//                                        String[] PK = mcontext.getResources()
//                                                .getStringArray(
//                                                        R.array.arrcontent);
//                                        Toast.makeText(
//                                                mcontext,
//                                                PK[which], Toast.LENGTH_LONG)
//                                                .show();
//                                        if (PK[which].equals("删除")) {
//                                            // 按照这种方式做删除操作，这个if内的代码有bug，实际代码中按需操作
//                                            data.remove(arg2);
//                                            SimpleAdapter adapter = (SimpleAdapter) mview
//                                                    .getAdapter();
//                                            if (!adapter.isEmpty()) {
//                                                adapter.notifyDataSetChanged();// 实现数据的实时刷新
//                                            }
//                                        }
//                                    }
//                                })
//                        .setNegativeButton("取消",
//                                new DialogInterface.OnClickListener() {
//                                    public void onClick(DialogInterface dialog,
//                                                        int which) {
//                                        // TODO Auto-generated method stub
//                                    }
//                                }).show();
//                return true;
//            }
//        });
//
//    }

    public final class ViewHolder {
        public ImageView icon;
        public TextView title;
        public TextView msg;
        public TextView sbName;
        public TextView sbpwd;
        public TextView deleteHolder;
        public SwipeLayout swipeLayout;
        public LinearLayout llCustomerInfo;
    }

}

