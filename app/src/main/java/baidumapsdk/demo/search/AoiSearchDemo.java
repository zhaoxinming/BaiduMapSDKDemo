package baidumapsdk.demo.search;

import android.graphics.Color;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.EncodePointType;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.PolygonOptions;
import com.baidu.mapapi.map.Stroke;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.baidu.mapapi.search.aoi.AoiResult;
import com.baidu.mapapi.search.aoi.AoiSearch;
import com.baidu.mapapi.search.aoi.AoiSearchOption;
import com.baidu.mapapi.search.aoi.OnGetAoiSearchResultListener;
import com.baidu.mapapi.search.core.AoiInfo;
import com.baidu.mapapi.search.core.SearchResult;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import baidumapsdk.demo.R;

/**
 * 介绍AOI搜索面搜索功能
 */
public class AoiSearchDemo extends AppCompatActivity implements
        OnGetAoiSearchResultListener {

    private AoiSearch mSearch = null;
    private BaiduMap mBaiduMap = null;
    private MapView mMapView = null;

    private EditText mEditLatitude;
    private EditText mEditLongitude;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aoi_search);

        // 初始化ui
        mEditLatitude = (EditText) findViewById(R.id.lat);
        mEditLongitude = (EditText) findViewById(R.id.lon);
        // 地图初始化
        mMapView = (MapView) findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();

        // 初始化搜索模块，注册事件监听
        mSearch = AoiSearch.newInstance();
        mSearch.setOnGetAoiSearchResultListener(this);
    }

    /**
     * 发起搜索
     */
    public void searchButtonProcess(View v) {
        if (!isFloat(mEditLatitude.getText().toString()) ||
                !isFloat(mEditLongitude.getText().toString())) {
            Toast.makeText(AoiSearchDemo.this, "请输入正确的数据", Toast.LENGTH_SHORT).show();
            return;
        }
        LatLng ptCenter = new LatLng((Float.valueOf(mEditLatitude.getText().toString())), (Float.valueOf(mEditLongitude.getText().toString())));
        ArrayList<LatLng> latLngs = new ArrayList<>();
        latLngs.add(ptCenter);
        AoiSearchOption aoiSearchOption = new AoiSearchOption();
        aoiSearchOption.setLatLngList(latLngs);
        mSearch.requestAoi(aoiSearchOption);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 释放检索对象
        mSearch.destroy();
        // 清空地图所有的覆盖物
        mBaiduMap.clear();
        // 释放地图
        mMapView.onDestroy();
    }

    public static boolean isFloat(String str) {
        Pattern pattern = Pattern.compile("^[-\\+]?[.\\d]*$");
        return pattern.matcher(str).matches();
    }

    @Override
    public void onGetAoiResult(AoiResult aoiResult) {
        mBaiduMap.clear();
        if (aoiResult == null) {
            return;
        }
        if (aoiResult.error != SearchResult.ERRORNO.NO_ERROR) {
            return;
        }
        List<AoiInfo> aoiList = aoiResult.getAoiList();
        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
        if (null == aoiList) {
            return;
        }
        for (int i = 0; i < aoiList.size(); i++) {
            AoiInfo aoiInfo = aoiList.get(i);
            String polygon = aoiInfo.getPolygon();
            PolygonOptions polygonOptions = new PolygonOptions();
            polygonOptions.points(polygon, EncodePointType.AOI);
            polygonOptions.stroke(new Stroke(5,  Color.argb(255, 0, 150, 150)));// 设置多边形边框信息
            polygonOptions.fillColor(Color.argb(100, 110, 160, 0));// 设置多边形填充颜色
            mBaiduMap.addOverlay(polygonOptions);

            LatLngBounds latLngBounds = mBaiduMap.getOverlayLatLngBounds(polygonOptions);
            if (latLngBounds != null) {
                boundsBuilder.include(latLngBounds.northeast).include(latLngBounds.southwest);
            }

            Log.e("TAG ", "onGetAoiResult: " + aoiInfo.getNearestDistance() );
            Log.e("TAG ", "onGetAoiResult: " + polygon );
        }
        mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLngBounds(boundsBuilder.build(),100,100,100,100));
    }
}
