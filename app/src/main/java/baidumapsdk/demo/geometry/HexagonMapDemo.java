package baidumapsdk.demo.geometry;

import android.graphics.Color;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.Gradient;
import com.baidu.mapapi.map.HexagonMap;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.model.LatLng;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import baidumapsdk.demo.R;

public class HexagonMapDemo extends AppCompatActivity {
    // 地图相关
    private MapView mMapView;
    private BaiduMap mBaiduMap;
    private HexagonMap mHexagonMap;
    private CheckBox mClick;
    private static final int[] DEFAULT_GRADIENT_COLORS = {
            Color.rgb(0, 0, 200), Color.rgb(0, 225, 0), Color.rgb(255, 0, 0) };
    private static final float[] DEFAULT_GRADIENT_START_POINTS = { 0.01f, 0.6f, 0.8f };
    /**
     * 热力图默认渐变
     */
    public static final Gradient DEFAULT_GRADIENT = new Gradient(
            DEFAULT_GRADIENT_COLORS, DEFAULT_GRADIENT_START_POINTS);//将gradient转成int型


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hexagon);

        // 初始化地图
        mMapView = (MapView) findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();

        // 构建地图状态
        MapStatus.Builder builder = new MapStatus.Builder();
        // 默认 天安门
        LatLng center = new LatLng(39.915071, 116.403907);
        // 默认 11级
        float zoom = 11.0f;
        builder.target(center).zoom(zoom);
        MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.newMapStatus(builder.build());

        // 设置地图状态
        mBaiduMap.setMapStatus(mapStatusUpdate);
    }

    /**
     *清除所有图层
     */
    public void clearOverlay(View view) {
        // 清除所有图层
        if (mHexagonMap != null) {
            mHexagonMap.remove();
        }
        mBaiduMap.clear();
    }

    public void addHexagon(View view){
        List<LatLng> datas = getLocations();
        mHexagonMap = new HexagonMap.Builder()
                .data(datas)
                .opacity(1.0f)
                .radius(2000)
                .minShowLevel(10)
                .maxShowLevel(14)
                .hexagonType(HexagonMap.HexagonType.EDGE_UP)
                .gradient(DEFAULT_GRADIENT)
                .maxIntensity(10)
                .gap(100)
                .build();
        mBaiduMap.addHexagonMap(mHexagonMap);
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

    private List<LatLng> getLocations() {
        List<LatLng> list = new ArrayList<LatLng>();
        InputStream inputStream = getResources().openRawResource(R.raw.locations);
        String json = new Scanner(inputStream).useDelimiter("\\A").next();
        JSONArray array;
        try {
            array = new JSONArray(json);
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                double lat = object.getDouble("lat");
                double lng = object.getDouble("lng");
                list.add(new LatLng(lat, lng));
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return list;
    }
}
