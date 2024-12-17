package baidumapsdk.demo.geometry;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.SeekBar;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.TitleOptions;
import com.baidu.mapapi.model.LatLng;

import java.util.ArrayList;

import baidumapsdk.demo.R;

public class MarkerPoiCollidedDemo extends AppCompatActivity {
    // MapView 是地图主控件
    private MapView mMapView;
    private BaiduMap mBaiduMap;
    private CheckBox mPoiCollided = null;
    private CheckBox mDragMarker = null;

    // 初始化全局 bitmap 信息，不用时及时 recycle
    private BitmapDescriptor bitmapA = BitmapDescriptorFactory.fromResource(R.drawable.icon_marka);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marker_poi);
        mMapView = (MapView) findViewById(R.id.bmapView);
        // 设置marker及其title是否碰撞底图poi
        // 注：只有15层级以上才会生效，天安门poi不可被碰掉
        mPoiCollided = (CheckBox)findViewById(R.id.poi_collision);
        mDragMarker = (CheckBox)findViewById(R.id.drag_marker);
        mBaiduMap = mMapView.getMap();
        MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.zoomTo(15.0f);
        mBaiduMap.setMapStatus(mapStatusUpdate);

        mBaiduMap.setOnMapLoadedCallback(new BaiduMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                initMarker();
            }
        });
    }

    public void initMarker() {
        LatLng latLngA = new LatLng(39.914114, 116.400244);

        // 设置title
        TitleOptions titleOptions = new TitleOptions()
                .text("我是A")
                .titleFontSize(48)
                .titleFontColor(Color.BLUE)
                .titleBgColor(Color.RED);
        MarkerOptions markerOptionsA = new MarkerOptions()
                .position(latLngA)
                .titleOptions(titleOptions)
                .icon(bitmapA)// 设置 Marker 覆盖物的图标
                .zIndex(9)// 设置 marker 覆盖物的 zIndex
                .poiCollided(mPoiCollided.isChecked()) // 设置marker是否能碰撞poi
                .draggable(mDragMarker.isChecked()); // 设置 marker 是否允许拖拽，默认不可拖拽

        mBaiduMap.addOverlay(markerOptionsA);


    }

    /**
     * 清除所有Overlay
     */
    public void clearOverlay(View view) {
        mBaiduMap.clear();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBaiduMap.clear();
        mMapView.onDestroy();
    }
}
