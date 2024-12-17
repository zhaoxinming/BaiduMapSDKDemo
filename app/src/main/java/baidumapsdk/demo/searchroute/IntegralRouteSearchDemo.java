package baidumapsdk.demo.searchroute;

import android.content.Context;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapBaseIndoorMapInfo;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.overlayutil.IntegralRouteOverlay;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.route.BikingRouteResult;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.IndoorRouteResult;
import com.baidu.mapapi.search.route.IntegralRouteResult;
import com.baidu.mapapi.search.route.MassTransitRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteLine;
import com.baidu.mapapi.search.route.WalkingRoutePlanOption;
import com.baidu.mapapi.search.route.WalkingRouteResult;

import java.util.ArrayList;

import baidumapsdk.demo.R;
import baidumapsdk.demo.indoorview.BaseStripAdapter;
import baidumapsdk.demo.indoorview.StripListView;

/**
 * 介绍室内路线规划
 */

public class IntegralRouteSearchDemo extends AppCompatActivity implements OnGetRoutePlanResultListener {

    private MapView mMapView;
    private BaiduMap mBaiduMap;
    private StripListView mStripListView;
    private BaseStripAdapter mFloorListAdapter;
    private MapBaseIndoorMapInfo mMapBaseIndoorMapInfo = null;
    private RoutePlanSearch mSearch;
    private EditText mStartLatitudeET;
    private EditText mStartLongitudeET;
    private EditText mStartfloorET;
    private EditText mEndLatitudeET;
    private EditText mEndLongitudeET;
    private EditText mEndfloorET;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        RelativeLayout layout = new RelativeLayout(this);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View mainview = inflater.inflate(R.layout.activity_integralroute, null);
        layout.addView(mainview);

        mStartLatitudeET = (EditText) mainview.findViewById(R.id.start_lat);
        mStartLongitudeET = (EditText)  mainview.findViewById(R.id.start_lon);
        mStartfloorET = (EditText)  mainview.findViewById(R.id.start_floor);
        mEndLatitudeET = (EditText)  mainview.findViewById(R.id.end_lat);
        mEndLongitudeET = (EditText)  mainview.findViewById(R.id.end_lon);
        mEndfloorET =  mainview.findViewById(R.id.end_floor);
        mMapView = (MapView) mainview.findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();

        LatLng centerpos = new LatLng(31.804305, 117.296975); // 合肥南站
        MapStatus.Builder builder = new MapStatus.Builder();
        builder.target(centerpos).zoom(19.0f);
        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
        // 设置是否显示室内图, 默认室内图不显示
        mBaiduMap.setIndoorEnable(true);
        // 获取RoutePlan检索实例
        mSearch = RoutePlanSearch.newInstance();
        // 设置路线检索监听者
        mSearch.setOnGetRoutePlanResultListener(this);
        Button integralRoutePlaneBtn = (Button) mainview.findViewById(R.id.indoorRoutePlane);
        integralRoutePlaneBtn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 清除之前的覆盖物
                mBaiduMap.clear();
                String startfloor = mStartfloorET.getText().toString().trim();
                String endfloor = mEndfloorET.getText().toString().trim();
                double startLatitude;
                double startLongitude;
                double endLatitude;
                double endLongitude;
                try {
                    startLatitude = Double.valueOf(mStartLatitudeET.getText().toString().trim());
                    startLongitude = Double.valueOf(mStartLongitudeET.getText().toString().trim());
                    endLatitude = Double.valueOf(mEndLatitudeET.getText().toString().trim());
                    endLongitude = Double.valueOf(mEndLongitudeET.getText().toString().trim());
                } catch (NumberFormatException e){
                    e.printStackTrace();
                    Toast.makeText(IntegralRouteSearchDemo.this,"请输入正确经纬度",Toast.LENGTH_LONG).show();
                    return;
                }
                // 设置起终点
                PlanNode startNode = PlanNode.withLocationAndFloorAndBid(new LatLng(startLatitude, startLongitude), startfloor, mMapBaseIndoorMapInfo.getID());
                if (TextUtils.isEmpty(startfloor)) {
                    // 室外->室内->室外, 需要cityCode
                    startNode = PlanNode.withCityCodeAndLocation("127", new LatLng(startLatitude, startLongitude));
                }
                PlanNode endNode = PlanNode.withLocationAndFloorAndBid(new LatLng(endLatitude, endLongitude), endfloor, mMapBaseIndoorMapInfo.getID());
                if (TextUtils.isEmpty(endfloor)) {
                    // 室外->室内->室外, 需要cityCode
                    endNode = PlanNode.withCityCodeAndLocation("127", new LatLng(endLatitude, endLongitude));
                }
                mSearch.walkingIntegralSearch((new WalkingRoutePlanOption()).from(startNode).to(endNode));
            }
        });

        mStripListView = new StripListView(this);
        layout.addView(mStripListView);
        setContentView(layout);
        mFloorListAdapter = new BaseStripAdapter(IntegralRouteSearchDemo.this);
        mBaiduMap.setOnBaseIndoorMapListener(new BaiduMap.OnBaseIndoorMapListener() {
            @Override
            public void onBaseIndoorMapMode(boolean isIndoorMap, MapBaseIndoorMapInfo mapBaseIndoorMapInfo) {
                if (!isIndoorMap || mapBaseIndoorMapInfo == null) {
                    mStripListView.setVisibility(View.INVISIBLE);
                    return;
                }
                mFloorListAdapter.setFloorList( mapBaseIndoorMapInfo.getFloors());
                mStripListView.setVisibility(View.VISIBLE);
                mStripListView.setStripAdapter(mFloorListAdapter);
                mMapBaseIndoorMapInfo = mapBaseIndoorMapInfo;
            }
        });
        mStripListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mMapBaseIndoorMapInfo == null) {
                    return;
                }
                String floor = (String) mFloorListAdapter.getItem(position);
                mBaiduMap.switchBaseIndoorMapFloor(floor, mMapBaseIndoorMapInfo.getID());
                mFloorListAdapter.setSelectedPostion(position);
                mFloorListAdapter.notifyDataSetInvalidated();
            }
        });
    }

    @Override
    public void onGetWalkingRouteResult(WalkingRouteResult result) {

    }

    @Override
    public void onGetTransitRouteResult(TransitRouteResult transitRouteResult) {

    }

    @Override
    public void onGetMassTransitRouteResult(MassTransitRouteResult massTransitRouteResult) {

    }

    @Override
    public void onGetDrivingRouteResult(DrivingRouteResult drivingRouteResult) {

    }

    @Override
    public void onGetIndoorRouteResult(IndoorRouteResult indoorRouteResult) {

    }

    @Override
    public void onGetBikingRouteResult(BikingRouteResult bikingRouteResult) {

    }

    @Override
    public void onGetIntegralRouteResult(IntegralRouteResult result) {
        if (null == result) {
            return;
        }
        if (result.error == SearchResult.ERRORNO.INTEGRAL_ROUTE_NOT_SUPPORT_MULTIPLE_INDOOR) {
            Toast.makeText(IntegralRouteSearchDemo.this, "室内外一体化路线不支持起终点在不同的室内", Toast.LENGTH_SHORT).show();
            return;
        }
        if (result.error == SearchResult.ERRORNO.NO_ADVANCED_PERMISSION) {
            Toast.makeText(IntegralRouteSearchDemo.this, "没有室内外一体化路线规划高级权限", Toast.LENGTH_SHORT).show();
            return;
        }
        if (result.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(IntegralRouteSearchDemo.this, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
            return;
        }
        IntegralRouteOverlay overlay = new IntegralRouteOverlay(mBaiduMap);
        mBaiduMap.setOnMarkerClickListener(overlay);
        if (result.getRouteLines() != null && result.getRouteLines().size() > 0) {
            overlay.setData(new ArrayList<WalkingRouteLine>(result.getRouteLines()));
        }
        if (result.getIndoorRouteLines() != null && result.getIndoorRouteLines().size() > 0) {
            overlay.setData(result.getIndoorRouteLines().get(0));
        }
        overlay.addToMap();
        overlay.zoomToSpan();
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
        mBaiduMap.clear();
        mMapView.onDestroy();
        // 释放检索对象
        mSearch.destroy();
    }
}
