package baidumapsdk.demo.geometry;

import java.util.ArrayList;
import java.util.List;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Polyline;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.map.TextPathMarker;
import com.baidu.mapapi.map.TextPathMarkerOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import baidumapsdk.demo.R;

/**
 * 该Demo展示如何给路线进行标注，注：TextPathMarker与普通的Marker无关
 */
public class TextPathMarkerDemo extends AppCompatActivity {
    // 地图相关
    private MapView mMapView;
    private BaiduMap mBaiduMap;
    private Polyline mPolyline;
    private TextPathMarker textPathMarker;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marker2);
        // 初始化地图
        mMapView = (MapView) findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();
        //添加text
        addTextPathMarker();

    }

    public void addTextPathMarker(){
        // 添加文字
        Typeface face = Typeface.DEFAULT;
        LatLng latLngA = new LatLng(39.97923, 116.357428);
        LatLng latLngB = new LatLng(39.94923, 116.397428);
        LatLng latLngC = new LatLng(39.97923, 116.437428);

        List<LatLng> points = new ArrayList<LatLng>();
        points.add(latLngA);
        points.add(latLngB);
        points.add(latLngC);
        // 覆盖物参数配置
        OverlayOptions ooPolyline = new PolylineOptions()
                .width(5)// 设置折线线宽， 默认为 5， 单位：像素
                .color(Color.argb(255, 255, 0, 0))//  折线颜色。注意颜色值得格式为：0xAARRGGBB，透明度值在前
                .points(points).zIndex(0);// 折线坐标点列表 数目[2,10000]，且不能包含 null
        // 添加覆盖物
        mPolyline = (Polyline) mBaiduMap.addOverlay(ooPolyline);

        OverlayOptions ooText =
                new TextPathMarkerOptions().text("百度地图SDK:>").textSize(25).textColor(0xAA0000FF).points(points).textFontOption(Typeface.DEFAULT_BOLD);
        textPathMarker = (TextPathMarker) mBaiduMap.addOverlay(ooText);
        textPathMarker.setZIndex(Short.MAX_VALUE);

        LatLngBounds bounds = new LatLngBounds.Builder().include(new LatLng(39.94923, 116.397428)).build();
        // 设置地图中心点以及缩放级别
        MapStatusUpdate
                mapStatusUpdate = MapStatusUpdateFactory.newLatLngZoom(bounds.getCenter(),15.0f);
        mBaiduMap.setMapStatus(mapStatusUpdate);
    }

    /**
     * 重置
     */
    public void resetOverlay(View view) {
        // remove可以移除某一个overlay
        if(textPathMarker != null) {
            textPathMarker.remove();
        }
        if(mPolyline != null){
            mPolyline.remove();
        }
        // 添加 overlay
        addTextPathMarker();
    }

    /**
     * 清除所有Overlay
     */
    public void clearOverlay(View view) {
        mBaiduMap.clear();
        textPathMarker.remove();
        mPolyline.remove();
        textPathMarker = null;
        mPolyline = null;

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
        // 清除所有图层
        mBaiduMap.clear();
        // 在activity执行onDestroy时必须调用mMapView.onDestroy()
        mMapView.onDestroy();
    }


}
