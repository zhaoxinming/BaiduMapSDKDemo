package baidumapsdk.demo.layers;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.CoordUtil;
import com.baidu.mapapi.model.LatLng;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import androidx.core.content.ContextCompat;
import baidumapsdk.demo.R;

/**
 * 展示定位图层3种定位模式
 */

public class LocationTypeDemo extends AppCompatActivity implements SensorEventListener {

    // 定位相关
    private LocationClient mLocClient;
    private MyLocationListener myListener = new MyLocationListener();
    // 定位图层显示方式
    private MyLocationConfiguration.LocationMode mCurrentMode;
    private SensorManager mSensorManager;
    private Double lastX = 0.0;
    private int mCurrentDirection = 0;
    private double mCurrentLat = 0.0;
    private double mCurrentLon = 0.0;
    private float mCurrentAccracy;
    // 初始化地图
    private MapView mMapView;
    private BaiduMap mBaiduMap;
    // 是否首次定位
    private boolean isFirstLoc = true;
    // 是否开启定位图层
    private boolean isLocationLayerEnable = true;
    private MyLocationData myLocationData;
    //精度圈信息
    public int accuracyCircleFillColor = 0xAAFFFF88;
    public int accuracyCircleStrokeColor = 0xAA00FF00;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_type);
        // 地图初始化
        mMapView = (MapView) findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();
        // 获取传感器管理服务
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mCurrentMode = MyLocationConfiguration.LocationMode.NORMAL;
        // 为系统的方向传感器注册监听器
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION), SensorManager.SENSOR_DELAY_UI);
        // 定位初始化
        if ((ContextCompat.checkSelfPermission(LocationTypeDemo.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                && (ContextCompat.checkSelfPermission(LocationTypeDemo.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)) {
            initLocation();
        }
        BitmapDescriptor customMarker = BitmapDescriptorFactory.fromResource(R.drawable.icon_blue);
        //导航箭头
        BitmapDescriptor arrow = BitmapDescriptorFactory.fromResource(R.drawable.icon_arrow);

        final MyLocationConfiguration myLocationConfiguration = new MyLocationConfiguration.Builder(mCurrentMode, true)
                .setArrow(arrow).setArrowSize(0.5f)
                .setCustomMarker(customMarker).setMarkerSize(0.2f)
                .setAnimation(true).setMarkerRotation(false)
                .build();

        mBaiduMap.setOnMapLoadedCallback(new BaiduMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                mBaiduMap.setMyLocationEnabled(true);
                mBaiduMap.setMyLocationConfiguration(myLocationConfiguration);
            }
        });
        getPermissions();
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        double x = sensorEvent.values[SensorManager.DATA_X];
        if (Math.abs(x - lastX) > 1.0) {
            mCurrentDirection = (int) x;
            myLocationData = new MyLocationData.Builder()
                    .accuracy(mCurrentAccracy)// 设置定位数据的精度信息，单位：米
                    .direction(mCurrentDirection)// 此处设置开发者获取到的方向信息，顺时针0-360
                    .latitude(mCurrentLat)
                    .longitude(mCurrentLon)
                    .build();
            mBaiduMap.setMyLocationData(myLocationData);
        }
        lastX = x;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    /**
     * 定位初始化
     */
    public void initLocation() {
        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);
        // 定位初始化
        try {
            LocationClient.setAgreePrivacy(true);
            mLocClient = new LocationClient(this);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        mLocClient.registerLocationListener(myListener);
        LocationClientOption option = new LocationClientOption();
        // 打开gps
        option.setOpenGps(true);
        // 设置坐标类型
        option.setCoorType("bd09ll");
        option.setScanSpan(1000);
        mLocClient.setLocOption(option);
        mLocClient.start();
    }

    /**
     * 设置定位图层的开启和关闭
     */
    public void setLocEnable(View v) {
        if (isLocationLayerEnable) {
            mBaiduMap.setMyLocationEnabled(false);
            ((Button) v).setText("开启定位图层");
            isLocationLayerEnable = !isLocationLayerEnable;
        } else {
            mBaiduMap.setMyLocationEnabled(true);
            mBaiduMap.setMyLocationData(myLocationData);
            ((Button) v).setText("关闭定位图层");
            isLocationLayerEnable = !isLocationLayerEnable;
        }
    }


    /**
     * 设置普通模式
     */
    public void setNormalType(View v) {
        mCurrentMode = MyLocationConfiguration.LocationMode.NORMAL;
        //定位图标
        BitmapDescriptor customMarker = BitmapDescriptorFactory.fromResource(R.drawable.icon_blue);
        //导航箭头
        BitmapDescriptor arrow = BitmapDescriptorFactory.fromResource(R.drawable.icon_arrow);

        MyLocationConfiguration myLocationConfiguration = new MyLocationConfiguration.Builder(mCurrentMode, true)
                .setArrow(arrow).setArrowSize(0.5f)
                .setCustomMarker(customMarker).setMarkerSize(0.2f)
                .setAnimation(true).setMarkerRotation(false)
                .build();

        mBaiduMap.setMyLocationConfiguration(myLocationConfiguration);
        customMarker.recycle();
        MapStatus.Builder builder1 = new MapStatus.Builder();
        builder1.overlook(0);
        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder1.build()));
    }


    /**
     * 设置跟随模式
     */
    public void setFollowType(View v) {
        mCurrentMode = MyLocationConfiguration.LocationMode.FOLLOWING;
        //导航箭头
        BitmapDescriptor arrow = BitmapDescriptorFactory.fromResource(R.drawable.icon_arrow);
        //在assets目录下
        String gifMarker = getCustomGIFFilePath(LocationTypeDemo.this, "icon_cat.gif");

        MyLocationConfiguration myLocationConfiguration = new MyLocationConfiguration.Builder(mCurrentMode, true)
                .setArrow(arrow).setArrowSize(0.5f)
                .setGifMarker(gifMarker).setMarkerSize(0.2f)
                .setAnimation(true).setMarkerRotation(false)
                .build();
        mBaiduMap.setMyLocationConfiguration(myLocationConfiguration);
        MapStatus.Builder builder = new MapStatus.Builder();
        builder.overlook(0);
        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
    }

    /**
     * 设置罗盘模式
     */
    public void setCompassType(View v) {
        mCurrentMode = MyLocationConfiguration.LocationMode.COMPASS;
        mBaiduMap.setMyLocationConfiguration(new MyLocationConfiguration(mCurrentMode, true, null));
    }

    /**
     * 定位SDK监听函数
     */
    public class MyLocationListener extends BDAbstractLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            // MapView 销毁后不在处理新接收的位置
            if (location == null || mMapView == null) {
                return;
            }
            mCurrentLat = location.getLatitude();
            mCurrentLon = location.getLongitude();
            mCurrentAccracy = location.getRadius();
            myLocationData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())// 设置定位数据的精度信息，单位：米
                    .direction(mCurrentDirection)// 此处设置开发者获取到的方向信息，顺时针0-360
                    .latitude(location.getLatitude())
                    .longitude(location.getLongitude())
                    .build();
            mBaiduMap.setMyLocationData(myLocationData);
            if (isFirstLoc) {
                isFirstLoc = false;
                LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
                MapStatus.Builder builder = new MapStatus.Builder();
                builder.target(ll).zoom(18.0f);
                mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 在activity执行onResume时必须调用mMapView. onResume ()
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 在activity执行onPause时必须调用mMapView. onPause ()
        mMapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 取消注册传感器监听
        mSensorManager.unregisterListener(this);
        // 退出时销毁定位
        if (mLocClient != null) {
            mLocClient.stop();
        }
        // 关闭定位图层
        mBaiduMap.setMyLocationEnabled(false);
        // 在activity执行onDestroy时必须调用mMapView.onDestroy()
        mMapView.onDestroy();
    }

    /**
     * 将gif文件内容写入本地存储路径
     */
    private String getCustomGIFFilePath(Context context, String GIFFileName) {
        FileOutputStream outputStream = null;
        InputStream inputStream = null;
        String parentPath = null;
        try {
            inputStream = context.getAssets().open(GIFFileName);
            byte[] buffer = new byte[inputStream.available()];
            inputStream.read(buffer);

            parentPath = context.getFilesDir().getAbsolutePath();
            File customStyleFile = new File(parentPath + "/" + GIFFileName);
            if (customStyleFile.exists()) {
                customStyleFile.delete();
            }
            customStyleFile.createNewFile();
            outputStream = new FileOutputStream(customStyleFile);
            outputStream.write(buffer);
        } catch (IOException e) {
            Log.e("LocationTypeDemo", "Copy custom style file failed", e);
            return null;
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException e) {
                Log.e("LocationTypeDemo", "Close stream failed", e);
            }
        }
        return parentPath + "/" + GIFFileName;
    }

    @TargetApi(23)
    private void getPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ArrayList<String> permissions = new ArrayList<String>();
            // 定位精确位置
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
            }
            if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
            }

            if (permissions.size() > 0) {
                requestPermissions(permissions.toArray(new String[0]), 100);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100 && grantResults.length > 0) {
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(LocationTypeDemo.this, "请授予app定位权限", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            initLocation();
        }
    }
}