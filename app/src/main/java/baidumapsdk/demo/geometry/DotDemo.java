package baidumapsdk.demo.geometry;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import baidumapsdk.demo.R;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.Dot;
import com.baidu.mapapi.map.DotOptions;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.model.LatLng;

public class DotDemo extends AppCompatActivity {

    // 地图相关
    MapView mMapView;
    BaiduMap mBaiduMap;
    Dot dot;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dot);
        mMapView = (MapView) findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();
    }


    public void addDot(View v){
        // 添加点
        LatLng llDot = new LatLng(39.98923, 116.397428);
        DotOptions ooDot = new DotOptions().center(llDot).radius(20)
                .color(0xFF0000FF);
        dot = (Dot)mBaiduMap.addOverlay(ooDot);
    }

    public void removedDot(View v) {
        dot.remove();
    }

    @Override
    protected void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        mMapView.onResume();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        mMapView.onDestroy();
        super.onDestroy();
    }
}
