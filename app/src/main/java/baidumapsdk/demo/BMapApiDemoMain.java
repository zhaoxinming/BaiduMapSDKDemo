/*
 * Copyright (C) 2015 Baidu, Inc. All Rights Reserved.
 */
package baidumapsdk.demo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.VersionInfo;
import com.baidu.mapapi.map.OverlayUtil;

import androidx.appcompat.app.AppCompatActivity;
import baidumapsdk.demo.createmap.CreateMapList;
import baidumapsdk.demo.geometry.GeometryList;
import baidumapsdk.demo.layers.LayersList;
import baidumapsdk.demo.mapcontrol.MapControlList;
import baidumapsdk.demo.search.SearchList;
import baidumapsdk.demo.searchroute.SearchRouteList;
import baidumapsdk.demo.util.UtilsList;


public class BMapApiDemoMain extends AppCompatActivity {
    private static final String LTAG = BMapApiDemoMain.class.getSimpleName();
    private boolean isPermissionRequested;
    private CheckBox checkBox;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        TextView text = (TextView) findViewById(R.id.text_Info);
        text.setTextColor(Color.GREEN);
        checkBox = (CheckBox)findViewById(R.id.overlay2) ;
        text.setText("欢迎使用百度地图Android SDK v" + VersionInfo.getApiVersion());
        setTitle(getTitle() + " v" + VersionInfo.getApiVersion());
        ListView mListView = (ListView) findViewById(R.id.listView);
        // 添加ListItem，设置事件响应
        mListView.setAdapter(new DemoListAdapter());
        mListView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> arg0, View v, int index, long arg3) {
                onListItemClick(index);
            }
        });

        privacyDialog();
    }

    public void isUpdateOverlay(View view) {
        CheckBox checkBox = (CheckBox) view;
        Log.d("cxy", "isUpdateOverlay is " + checkBox.isChecked());

        OverlayUtil.setOverlayUpgrade(checkBox.isChecked());

    }

    private void privacyDialog() {
        SharedPreferences sp = getSharedPreferences(DemoApplication.SP_NAME, Context.MODE_PRIVATE);
        boolean ifAgree = sp.getBoolean(DemoApplication.SP_KEY, false);
        if (ifAgree) {
            return;
        }
        // ‼️重要：设置用户是否同意SDK隐私协议，必须SDK初始化前按用户意愿设置
        // 隐私政策官网链接：https://lbsyun.baidu.com/index.php?title=openprivacy
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("温馨提示");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            View view = getLayoutInflater().inflate(R.layout.layout_privacy_dialog, null);
            TextView privacyTv = view.findViewById(R.id.privacy_tv);
            privacyTv.setMovementMethod(LinkMovementMethod.getInstance());
            dialog.setView(view);
        } else {
            dialog.setMessage(R.string.privacy_content);
        }
        dialog.setPositiveButton("同意", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SharedPreferences.Editor editor = getSharedPreferences(DemoApplication.SP_NAME, Context.MODE_PRIVATE).edit();
                editor.putBoolean(DemoApplication.SP_KEY, true);
                editor.apply();
                SDKInitializer.setAgreePrivacy(BMapApiDemoMain.this.getApplicationContext(), true);
                dialog.dismiss();
            }
        });
        dialog.setNegativeButton("不同意", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SDKInitializer.setAgreePrivacy(BMapApiDemoMain.this.getApplicationContext(), false);
                dialog.dismiss();
            }
        });
        dialog.setCancelable(false);
        dialog.show();

    }

    void onListItemClick(int index) {
        Intent intent;
        intent = new Intent(BMapApiDemoMain.this, DEMOS[index].demoClass);
        this.startActivity(intent);
    }

    private static final DemoInfo[] DEMOS = {
            new DemoInfo(R.drawable.map, R.string.demo_title_createmaplist, R.string.demo_desc_createmaplist, CreateMapList.class),
            new DemoInfo(R.drawable.layers, R.string.demo_title_layerlist, R.string.demo_desc_layerlist, LayersList.class),
            new DemoInfo(R.drawable.control, R.string.demo_title_mapcontrollist, R.string.demo_desc_mapcontrollist, MapControlList.class),
            new DemoInfo(R.drawable.draw, R.string.demo_title_drawlist, R.string.demo_desc_drawlist, GeometryList.class),
            new DemoInfo(R.drawable.search, R.string.demo_title_searchlist, R.string.demo_desc_searchlist, SearchList.class),
            new DemoInfo(R.drawable.route, R.string.demo_title_routeplan, R.string.demo_desc_routeplan, SearchRouteList.class),
            new DemoInfo(R.drawable.util, R.string.demo_title_util, R.string.demo_desc_util, UtilsList.class)
    };

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private class DemoListAdapter extends BaseAdapter {
        private DemoListAdapter() {
            super();
        }

        @Override
        public View getView(int index, View convertView, ViewGroup parent) {
            if (null == convertView) {
                convertView = View.inflate(BMapApiDemoMain.this, R.layout.demo_item, null);
            }
            ImageView imageView =(ImageView)convertView.findViewById(R.id.image);
            TextView title = (TextView) convertView.findViewById(R.id.title);
            TextView desc = (TextView) convertView.findViewById(R.id.desc);
            imageView.setBackgroundResource(DEMOS[index].image);
            title.setText(DEMOS[index].title);
            desc.setText(DEMOS[index].desc);
            return convertView;
        }

        @Override
        public int getCount() {
            return DEMOS.length;
        }

        @Override
        public Object getItem(int index) {
            return DEMOS[index];
        }

        @Override
        public long getItemId(int id) {
            return id;
        }
    }

    private static class DemoInfo {
        private final int image;
        private final int title;
        private final int desc;
        private final Class<? extends Activity> demoClass;

        private DemoInfo(int image,int title, int desc, Class<? extends Activity> demoClass) {
            this.image = image;
            this.title = title;
            this.desc = desc;
            this.demoClass = demoClass;
        }
    }
}