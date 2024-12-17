package baidumapsdk.demo.geometry;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Prism;
import com.baidu.mapapi.map.PrismOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.baidu.mapapi.search.building.BuildingResult;
import com.baidu.mapapi.search.building.BuildingSearch;
import com.baidu.mapapi.search.building.BuildingSearchOption;
import com.baidu.mapapi.search.building.OnGetBuildingSearchResultListener;
import com.baidu.mapapi.search.core.BuildingInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.district.DistrictResult;
import com.baidu.mapapi.search.district.DistrictSearch;
import com.baidu.mapapi.search.district.DistrictSearchOption;
import com.baidu.mapapi.search.district.OnGetDistricSearchResultListener;

import java.util.List;

import javax.microedition.khronos.opengles.GL10;

import baidumapsdk.demo.R;

public class Building3DPrismDemo extends AppCompatActivity implements
        OnGetBuildingSearchResultListener,
        View.OnClickListener, OnGetDistricSearchResultListener {

    private DistrictSearch mDistrictSearch;
    private MapView mMapView;
    private BaiduMap mBaiduMap;
    private Button mDistrictPrismButton;
    private Button mCleanPrismButton;
    private Prism mCustomPrism;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prism_3d_overlay_demo);
        initView();
    }

    private void initView() {
        mMapView = findViewById(R.id.bmapView);
        mDistrictPrismButton = findViewById(R.id.district_prism);
        mCleanPrismButton = findViewById(R.id.clean_prism);
        mDistrictPrismButton.setOnClickListener(this);
        mCleanPrismButton.setOnClickListener(this);
        mBaiduMap = mMapView.getMap();
        mDistrictSearch = mDistrictSearch.newInstance();
    }

    @Override
    public void onGetBuildingResult(BuildingResult result) {
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
        mMapView.onDestroy();

        if (null != mDistrictSearch) {
            mDistrictSearch.destroy();
        }
    }

    private void searchDistrict() {
        mDistrictSearch.setOnDistrictSearchListener(this);
        mDistrictSearch.searchDistrict(new DistrictSearchOption().cityName("北京市").districtName("海淀区"));
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.district_prism) {
            searchDistrict();
        } else if (v.getId() == R.id.clean_prism) {
            clean3DPrim();
        }
    }

    private void clean3DPrim() {
        if (null != mCustomPrism) {
            // 清除自定义3D棱柱
            mCustomPrism.remove();
        }
    }

    @Override
    public void onGetDistrictResult(DistrictResult result) {
        if (null != result && result.error == SearchResult.ERRORNO.NO_ERROR) {

            List<List<LatLng>> polyLines = result.getPolylines();
            if (polyLines == null) {
                return;
            }
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for (List<LatLng> polyline : polyLines) {
                PrismOptions prismOptions = new PrismOptions();
                prismOptions.setHeight(4800);
                prismOptions.setPoints(polyline);
                prismOptions.setSideFaceColor(0xAAFF0000);
                prismOptions.setTopFaceColor(0xAA00FF00);
                prismOptions.customSideImage(BitmapDescriptorFactory.fromResource(R.drawable.wenli));
                // 添加自定3D棱柱
                mCustomPrism = (Prism) mBaiduMap.addOverlay(prismOptions);
                for (LatLng latLng : polyline) {
                    builder.include(latLng);
                }
            }
            mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLngBounds(builder.build()));
            MapStatus.Builder builder1 = new MapStatus.Builder();
            builder1.overlook(-30.0f);
            mBaiduMap.setMapStatus(MapStatusUpdateFactory.newMapStatus(builder1.build()));
        }
    }
}