package baidumapsdk.demo.layers;

import android.graphics.Color;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.model.LatLng;

import baidumapsdk.demo.R;

public class LayerTrafficAndHeatMapDemo extends AppCompatActivity {

    // MapView 地图主控件
    private MapView mMapView;
    private BaiduMap mBaiduMap;

    private BitmapDescriptor bitmapA = BitmapDescriptorFactory.fromResource(R.drawable.icon_marka);

    private Marker mMarkerA;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_layers);
        mMapView = (MapView) findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();

        //路况事件的点击回调
        mBaiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {


            @Override
            public void onMapClick(LatLng point) {
            }

            @Override
            //注：其他poi也会参与点击，可以通过poi的统计值判断是否为路况事件点
            public void onMapPoiClick(MapPoi poi) {
                if(poi.getPosition() == null){
                    return;
                }

                if(mMarkerA != null){
                    mBaiduMap.clear();
                    mMarkerA = null;
                }
                MarkerOptions markerOptionsE = new MarkerOptions()
                        .position(poi.getPosition())
                        .icon(bitmapA);// 设置 Marker 覆盖物的图标
                mMarkerA = (Marker) (mBaiduMap.addOverlay(markerOptionsE));
            }
        });
    }

    /**
     * 设置是否显示交通图
     */
    public void setTraffic(View view) {
        mBaiduMap.setTrafficEnabled(((CheckBox) view).isChecked());
        mBaiduMap.setCustomTrafficColorEnable(true);
        //自定义路况的颜色
        mBaiduMap.setCustomTrafficColor(0xFF0A17B4,0xFF1021E7,0xFF46D0FF,0xFF7DD250);
    }

    /**
     * 设置是否显示热力图
     */
    public void setHeatMap(View view) {
        mBaiduMap.setBaiduHeatMapEnabled(((CheckBox) view).isChecked());
    }


    /**
     * 设置是否展示路况事件图层
     * @param view
     */
    public void setTrafficUGC(View view){
        mBaiduMap.showTrafficUGCMap(((CheckBox) view).isChecked());
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 在activity执行onResume时必须调用mMapView.onResume ()
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 在activity执行onPause时必须调用mMapView.onPause ()
        mMapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 在activity执行onDestroy时必须调用mMapView.onDestroy()
        mMapView.onDestroy();
    }
}
