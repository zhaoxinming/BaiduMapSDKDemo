package baidumapsdk.demo.geometry;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.CollisionBehavior;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.bmsdk.ui.BaseUI;
import com.baidu.mapapi.map.bmsdk.ui.UIGravity;
import com.baidu.mapapi.map.bmsdk.ui.HorizontalLayout;
import com.baidu.mapapi.map.bmsdk.ui.ImageUI;
import com.baidu.mapapi.map.bmsdk.ui.LabelUI;
import com.baidu.mapapi.map.bmsdk.ui.Located;
import com.baidu.mapapi.map.bmsdk.ui.RichView;
import com.baidu.mapapi.map.TextStyle;
import com.baidu.mapapi.map.bmsdk.ui.VerticalLayout;
import com.baidu.mapapi.model.LatLng;

import baidumapsdk.demo.R;

/**
 * 介绍升级绘制方法后在地图上为Marker添加RichView
 */
public class RichViewDemo extends AppCompatActivity {
    // MapView 是地图主控件
    private MapView mMapView;
    private BaiduMap mBaiduMap;
    private Marker mMarkerA;
    private Marker mMarkerB;
    private RichView mRichViewA;
    private RichView mRichViewB;
    private final String TAG = RichViewDemo.class.getName();

    // 初始化全局 bitmap 信息，不用时及时 recycle
    private BitmapDescriptor bitmapA = BitmapDescriptorFactory.fromResource(R.drawable.icon_marka);
    private BitmapDescriptor bitmapB = BitmapDescriptorFactory.fromResource(R.drawable.icon_markb);


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_richview);
        mMapView = (MapView) findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();
        MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.zoomTo(13.0f);
        mBaiduMap.setMapStatus(mapStatusUpdate);

        mBaiduMap.setOnMapLoadedCallback(new BaiduMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                initMarker();
            }
        });

        initListener();
    }

    private void initMarker() {
        LatLng latLngA = new LatLng(39.963175, 116.400244);
        LatLng latLngB = new LatLng(39.942821, 116.369199);

        MarkerOptions markerOptionsA = new MarkerOptions()
                .position(latLngA)
                .perspective(false)
                .icon(bitmapA)// 设置 Marker 覆盖物的图标
                .zIndex(9)// 设置 marker 覆盖物的 zIndex
                .clickable(true) // 设置Marker是否可点击
                .draggable(true); // 设置 marker 是否允许拖拽，默认不可拖拽

        mMarkerA = (Marker) (mBaiduMap.addOverlay(markerOptionsA));

        MarkerOptions markerOptionsB = new MarkerOptions()
                .position(latLngB)
                .flat(true)
                .icon(bitmapB)
                .draggable(true)
                .zIndex(5)
                .clickable(true);

        mMarkerB = (Marker) (mBaiduMap.addOverlay(markerOptionsB));
    }

    private void initListener() {

        mBaiduMap.setOnMarkerWithBaseUIClickListener(new BaiduMap.OnMarkerWithBaseUIClickListener() {
            @Override
            public void onMarkerClick(Marker marker) {
                if (marker == mMarkerA || marker == mMarkerB) {

                    HorizontalLayout hhLayout = new HorizontalLayout();
                    hhLayout.setBackgroundColor(Color.RED);
                    hhLayout.setGravity(UIGravity.CENTER_VERTICAL);
                    hhLayout.setClickable(true);
                    hhLayout.setClickAction("hhLayout");

                    LabelUI labelUI1 = new LabelUI();
                    labelUI1.setGravity(UIGravity.TOP);
                    labelUI1.setText("#BmMarker# 百度地图");
                    labelUI1.setClickable(true);
                    labelUI1.setClickAction("labelUI1");

                    TextStyle textStyle2 = new TextStyle();
                    textStyle2.setTextColor(0xFF2211EE);
                    textStyle2.setTextSize(28);
                    labelUI1.setTextStyle(textStyle2);

                    hhLayout.addView(labelUI1);

                    if(mRichViewA != null){
                        marker.removeRichView(mRichViewA);
                        mRichViewA = null;
                    }
                    mRichViewA = new RichView();
                    mRichViewA.setView(hhLayout);
                    mRichViewA.setLocated(Located.TOP);
                    marker.addRichView(mRichViewA);

                }
            }

            @Override
            public void onMarkerClick(Marker marker, BaseUI baseUI) {
                if (baseUI instanceof LabelUI) {
                    Log.d(TAG, "onMarkerClick(LabelUI) " + baseUI.getClickAction());
                    LabelUI labelUI = (LabelUI) baseUI;
                    labelUI.setText(baseUI.getClickAction() + "被点击");
                    labelUI.setPadding(0, 0, 0, 0);
                    labelUI.setGravity(UIGravity.TOP);
                    //必须更新richview 使改动生效
                    marker.updateRichView();
                } else if (baseUI instanceof ImageUI) {
                    Log.d(TAG, "onMarkerClick(ImageUI) " + baseUI.getClickAction());
                    ImageUI imageUI = (ImageUI) baseUI;
                    imageUI.setColor(Color.RED);
                    //必须更新richview 使改动生效
                    marker.updateRichView();
                } else if (baseUI instanceof VerticalLayout) {
                    Log.d(TAG, "onMarkerClick(VerticalLayout) " + baseUI.getClickAction());
                    VerticalLayout verticalLayout = (VerticalLayout) baseUI;
                    verticalLayout.setBackgroundColor(Color.BLUE);
                    verticalLayout.setGravity(UIGravity.LEFT);
                    //必须更新richview 才能使改动生效
                    marker.updateRichView();
                } else if (baseUI instanceof  HorizontalLayout){
                    HorizontalLayout horizontalLayout = (HorizontalLayout) baseUI;
                    horizontalLayout.setBackgroundColor(Color.GREEN);
                    horizontalLayout.setGravity(UIGravity.BOTTOM);
                }
            }
        });
    }


    public void addLabelUI(View view){
        mMarkerB.clearRichViews(); //先清空marker上所有的richview；
        mRichViewB = null;
        LabelUI label = new LabelUI();
        label.setText("我是markerB");
        label.setMaxLines(2);

        TextStyle txtStyle = new TextStyle();
        txtStyle.setTextColor(Color.RED);
        txtStyle.setTextSize(28);
        label.setTextStyle(txtStyle);

        mRichViewB = new RichView();
        mRichViewB.setView(label);
        mRichViewB.setLocated(Located.BOTTOM);
        mRichViewB.setCollisionBehavior(CollisionBehavior.ALWAYS_SHOW); //碰撞方法建议与Marker同步
        mMarkerB.addRichView(mRichViewB);
    }

    public void addImageUI(View view){
        mMarkerB.clearRichViews(); //先清空marker上所有的richview；
        mRichViewB = null;
        ImageUI imageUI = new ImageUI();
        BitmapDescriptor bitmapDescriptor1 = BitmapDescriptorFactory.fromResource(R.drawable.xhdpi_uber);
        imageUI.setDrawableResource(bitmapDescriptor1);
        imageUI.setClickable(true);
        imageUI.setClickAction("imageUI");

        mRichViewB = new RichView();
        mRichViewB.setView(imageUI);
        mRichViewB.setLocated(Located.TOP);
        mRichViewB.setScale(0.7f); //调整大小
        mRichViewB.setCollisionBehavior(CollisionBehavior.ALWAYS_SHOW); //碰撞方法建议与Marker同步
        mMarkerB.addRichView(mRichViewB);

    }

    public void addLayout(View view){
        mMarkerA.clearRichViews();
        HorizontalLayout hhLayout = new HorizontalLayout();
        hhLayout.setBackgroundColor(Color.RED);
        hhLayout.setGravity(UIGravity.CENTER_VERTICAL);
        hhLayout.setClickable(true);

        ImageUI imageUI = new ImageUI();
        BitmapDescriptor bitmapDescriptor1 = BitmapDescriptorFactory.fromResource(R.drawable.xhdpi_uber);
        BitmapDescriptor bitmapDescriptor2 = BitmapDescriptorFactory.fromResource(R.drawable.nodpi_titlebg);
        imageUI.setDrawableResource(bitmapDescriptor1);
        imageUI.setClickable(true);
        imageUI.setClickAction("imageUI");
        hhLayout.addView(imageUI);
        hhLayout.setClickAction("hhLayout");

        VerticalLayout vvlayout = new VerticalLayout();
        vvlayout.setClickable(true);
        vvlayout.setBackgroundColor(Color.GREEN);
        vvlayout.setClickAction("vvlayout");

        LabelUI labelUI1 = new LabelUI();
        labelUI1.setGravity(UIGravity.TOP);
        labelUI1.setText("#BmMarker# 百度地图");
        labelUI1.setClickable(true);
        labelUI1.setClickAction("labelUI1");

        TextStyle textStyle2 = new TextStyle();
        textStyle2.setTextColor(0xFF2211EE);
        textStyle2.setTextSize(28);
        labelUI1.setTextStyle(textStyle2);

        // Label-B
        LabelUI labelUI2 = new LabelUI();
        labelUI2.setGravity(UIGravity.BOTTOM);
        labelUI2.setText("TAG 百度地图");
        labelUI2.setClickable(true);
        labelUI2.setClickAction("labelUI2");
        TextStyle textStyle22 = new TextStyle();
        textStyle22.setTextColor(0xFFAA2211);
        textStyle22.setTextSize(22);

        labelUI2.setTextStyle(textStyle22);
        vvlayout.addView(labelUI2);
        vvlayout.addView(labelUI1);
        hhLayout.addView(vvlayout);

        if(mRichViewA != null){
            mMarkerA.removeRichView(mRichViewA);
            mRichViewA = null;
        }
        mRichViewA = new RichView();
        mRichViewA.setView(hhLayout);
        mRichViewA.setLocated(Located.TOP);
        mMarkerA.addRichView(mRichViewA);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // MapView的生命周期与Activity同步，当activity挂起时需调用MapView.onPause()
        mMapView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // MapView的生命周期与Activity同步，当activity恢复时必须调用MapView.onResume()
        mMapView.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 回收bitmap资源，防止内存泄露
        bitmapA.recycle();
        bitmapB.recycle();
        // 清除所有图层
        mBaiduMap.clear();
        // MapView的生命周期与Activity同步，当activity销毁时必须调用MapView.destroy()
        mMapView.onDestroy();
    }

}
