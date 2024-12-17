package baidumapsdk.demo.geometry;

import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.Building;
import com.baidu.mapapi.map.Building3DListener;
import com.baidu.mapapi.map.BuildingOptions;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.Prism;
import com.baidu.mapapi.map.Projection;
import com.baidu.mapapi.map.UiSettings;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.baidu.mapapi.search.building.BuildingResult;
import com.baidu.mapapi.search.building.BuildingSearch;
import com.baidu.mapapi.search.building.BuildingSearchOption;
import com.baidu.mapapi.search.building.OnGetBuildingSearchResultListener;
import com.baidu.mapapi.search.core.BuildingInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.district.DistrictResult;
import com.baidu.mapapi.search.district.OnGetDistricSearchResultListener;

import java.util.List;

import javax.microedition.khronos.opengles.GL10;

import baidumapsdk.demo.R;

public class Building3DDemo extends AppCompatActivity implements
        OnGetBuildingSearchResultListener,
        View.OnClickListener, OnGetDistricSearchResultListener {

    private BuildingSearch mBuildingSearch;
    private MapView mMapView;
    private BaiduMap mBaiduMap;
    private Button mBuildingPrismButton;
    private Button mCleanPrismButton;
    private Button mUpdateFloorButton;
    private Building mBuildingPrism;
    private Prism mCustomPrism;
    private Marker mMarkerA;
    private InfoWindow mInfoWindowA = null;
    private LatLng requestLatlng;
    private float height = 0;
    private float floorheight1 = 40;
    private float floorheight2 = 10;
    // 楼面外接矩形
    private LatLngBounds latLngBounds;

    // BuildingInfo列表
    List<BuildingInfo> buildingList;
    // 建筑物俯视图
    private UiSettings mUiSettings;
    private BitmapDescriptor bitmapA = BitmapDescriptorFactory.fromResource(R.drawable.icon_marka);
    private BitmapDescriptor bitmapE = BitmapDescriptorFactory.fromResource(R.drawable.icon_marke);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_building_3d_overlay_demo);
        requestLatlng = new LatLng(23.008468, 113.72953);
        initView();
//        if (mBaiduMap != null) {
//            mBaiduMap.setOnMapDrawFrameCallback(new BaiduMap.OnMapDrawFrameCallback() {
//                @Override
//                public void onMapDrawFrame(GL10 gl, MapStatus drawingMapStatus) {
//
//                }
//                @Override
//                public void onMapDrawFrame(MapStatus drawingMapStatus) {
//                    if (null != mBaiduMap.getMapStatus() && null != mBuildingPrism) {
//                        Projection projection = mBaiduMap.getProjection();
//                        // 每一帧绘制时重新计算3D marker的外接矩形，根据外接矩形重设infowindow坐标
//                        if (mBuildingPrism != null && !mBuildingPrism.isRemoved() && mBuildingPrism.getBuildingInfo() != null) {
//                            int height = (int) mBuildingPrism.getBuildingInfo().getHeight();
//                            Point srcPoint = projection.geoPoint3toScreenLocation(requestLatlng, height);
//                            LatLng locationLat = projection.fromScreenLocation(srcPoint);
//                            if (mMarkerA != null && !mMarkerA.isRemoved() && locationLat != null) {
//                                mMarkerA.setPosition(locationLat);
//                                if (mInfoWindowA != null && locationLat != null) {
//                                    mInfoWindowA.setPosition(locationLat);
//                                }
//                            }
//                        }
//                    }
//                }
//            });

//        }
    }

    private void initView() {
        mMapView = findViewById(R.id.bmapView);
        mBuildingPrismButton = findViewById(R.id.building_prism);
        mCleanPrismButton = findViewById(R.id.clean_prism);
        mUpdateFloorButton = findViewById(R.id.update_floor);
        mBuildingPrismButton.setOnClickListener(this);
        mCleanPrismButton.setOnClickListener(this);
        mUpdateFloorButton.setOnClickListener(this);
        mBaiduMap = mMapView.getMap();
        mUiSettings = mBaiduMap.getUiSettings();
        mUiSettings.setEnlargeCenterWithDoubleClickEnable(true);
        mUiSettings.setFlingEnable(false);

        MapStatus mapStatus = new MapStatus.Builder().target(requestLatlng).overlook(-30f).build();
        MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mapStatus);
        mBaiduMap.setMapStatus(mapStatusUpdate);
        mBuildingSearch = BuildingSearch.newInstance();
        mBuildingSearch.setOnGetBuildingSearchResultListener(this);

    }

    private void searchBuilding() {
        BuildingSearchOption buildingSearchOption = new BuildingSearchOption();
        buildingSearchOption.setLatLng(requestLatlng);
        mBuildingSearch.requestBuilding(buildingSearchOption);
    }

    @Override
    public void onGetBuildingResult(BuildingResult result) {
        if (null == result || result.error != SearchResult.ERRORNO.NO_ERROR) {
            return;
        }

        buildingList = result.getBuildingList();
        // 楼面外接矩形建造器
        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
        for (int i = 0; i < buildingList.size(); i++) {
            BuildingInfo buildingInfo = buildingList.get(i);
            // 创建3D棱柱覆盖物选类配置参数
            BuildingOptions buildingOptions = new BuildingOptions();
            buildingOptions.setBuildingInfo(buildingInfo);
            buildingOptions.setSideFaceColor(0xAAFF0000);
            buildingOptions.setRoundedCornerEnable(true);
            buildingOptions.setRoundedCornerRadius(2.0f);
//            buildingOptions.customSideImage(BitmapDescriptorFactory.fromResource(R.drawable.wenli));
            buildingOptions.setTopFaceColor(0xAA00FF00);
            buildingOptions.setBuildingFloorAnimateType(Prism.AnimateType.AnimateSlow);
            buildingOptions.setFloorHeight(buildingInfo.getHeight() - 10);
            // 控制3D建筑物单体动画
            buildingOptions.setAnimation(true);

            // 设置3D建筑物开始显示层级
            buildingOptions.setShowLevel(17);
            LatLngBounds latLngBounds = mBaiduMap.getOverlayLatLngBounds(buildingOptions);
            if (latLngBounds != null) {
                boundsBuilder.include(latLngBounds.northeast).include(latLngBounds.southwest);
            }
            // 添加3D棱柱
            mBuildingPrism = (Building) mBaiduMap.addOverlay(buildingOptions);
            mBaiduMap.setOn3DBuildingListener(new Building3DListener() {
                @Override
                public void onBuildingFloorAnimationStop(Building buildings) {
                    Log.d("Building3DPrismDemo ", "setPrismCallBack() ::onBuildingFloorAnimationStop");

                }
            });
        }

        if (mMarkerA != null) {
            mMarkerA.remove();
        }

        if (mBaiduMap.getMapStatus() != null && mBuildingPrism != null) {
            Projection projection = mBaiduMap.getProjection();
            Point srcPoint = projection.geoPoint3toScreenLocation(requestLatlng, (int) mBuildingPrism.getBuildingInfo().getHeight());
            MarkerOptions markerOptionsA = new MarkerOptions().position(projection.fromScreenLocation(srcPoint))
                    .icon(bitmapA)// 设置 Marker 覆盖物的图标
                    .perspective(false) // 关闭近大远小效果
                    .zIndex(10)
                    .clickable(true); // 设置Marker是否可点击
            mMarkerA = (Marker) mBaiduMap.addOverlay(markerOptionsA);
        }


        // 获取3D建筑物外接矩形
        latLngBounds = boundsBuilder.build();
        // 令3D建筑物适应地图展示
        if (latLngBounds != null) {
            mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLngBounds(latLngBounds));
            MapStatus.Builder builder = new MapStatus.Builder();
            builder.zoom(21).overlook(-40.0f);
            mBaiduMap.setMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
        }
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
        if (null != mBuildingSearch) {
            mBuildingSearch.destroy();
        }

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.building_prism) {
            clean3DPrim();
            searchBuilding();

            mBaiduMap.setBuildingsEnabled(false);
            // 设置Marker 点击事件监听
            mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
                public boolean onMarkerClick(final Marker marker) {
                    Button button = new Button(getApplicationContext());
                    button.setBackgroundResource(R.drawable.popup);

                    InfoWindow.OnInfoWindowClickListener listener = null;
                    if (marker == mMarkerA) {
                        if (mInfoWindowA != null) {
                            mInfoWindowA = null;
                        }
                        button.setText("更改图标");
                        button.setTextColor(Color.BLACK);
                        listener = new InfoWindow.OnInfoWindowClickListener() {
                            public void onInfoWindowClick() {
                                marker.setIcon(bitmapE);
                                mBaiduMap.hideInfoWindow();
                                mInfoWindowA = null;
                            }
                        };
                        LatLng latLng = marker.getPosition();
                        // 创建InfoWindow
                        mInfoWindowA = new InfoWindow(BitmapDescriptorFactory.fromView(button), latLng, -95, listener);
                        // 显示 InfoWindow, 该接口会先隐藏其他已添加的InfoWindow, 再添加新的InfoWindow
                        mBaiduMap.showInfoWindow(mInfoWindowA);
                    }
                    return true;
                }
            });
        } else if (v.getId() == R.id.clean_prism) {
            clean3DPrim();
        } else if (v.getId() == R.id.update_floor) {
            float tmp = floorheight1;
            floorheight1 = floorheight2;
            floorheight2 = tmp;

            if (mBuildingPrism != null) {
                mBuildingPrism.setFloorHeight(floorheight1);
                mBuildingPrism.setFloorColor(0xFF0000AA);
                mBuildingPrism.setFloorSideTextureImage(null);
                mBuildingPrism.setBuildingFloorAnimateType(Prism.AnimateType.AnimateFast);
            }
        }
    }

    private void clean3DPrim() {
        if (null != mBuildingPrism) {
            // 清除建筑物3D棱柱
            mBuildingPrism.remove();
        }

        if (null != mBaiduMap) {
            mBaiduMap.clear();
            mInfoWindowA = null;
        }

    }

    @Override
    public void onGetDistrictResult(DistrictResult result) {
    }
}