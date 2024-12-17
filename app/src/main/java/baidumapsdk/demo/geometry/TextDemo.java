package baidumapsdk.demo.geometry;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.GroundOverlay;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.SeekBar;
import baidumapsdk.demo.R;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.Text;
import com.baidu.mapapi.map.TextOptions;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.TextStyle;
import com.baidu.mapapi.map.bmsdk.ui.HorizontalLayout;
import com.baidu.mapapi.map.bmsdk.ui.ImageUI;
import com.baidu.mapapi.map.bmsdk.ui.LabelUI;
import com.baidu.mapapi.map.bmsdk.ui.Located;
import com.baidu.mapapi.map.bmsdk.ui.RichView;
import com.baidu.mapapi.map.bmsdk.ui.UIGravity;
import com.baidu.mapapi.map.bmsdk.ui.VerticalLayout;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.baidu.platform.comapi.bmsdk.ui.BmGravity;

public class TextDemo extends AppCompatActivity {
    // 地图相关
    private MapView mMapView;
    private BaiduMap mBaiduMap;

    private Text mText;
    //UI相关
    private CheckBox mClickText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text);
        // 初始化地图
        mMapView = (MapView) findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();
        // 初始化UI
        mClickText=(CheckBox) findViewById(R.id.clicktext);
        //添加text
        addText();

        // 点击text的事件响应
        mBaiduMap.setOnTextClickListener(new BaiduMap.OnTextClickListener() {
            @Override
            public boolean onTextClick(Text text) {
                if (text == mText) {
                    text.remove();
                }
                return false;
            }
        });
    }

    public void addText(){
        // 添加文字
        Typeface face = Typeface.DEFAULT;
        LatLng llText = new LatLng(39.86923, 116.397428);
        OverlayOptions ooText = new TextOptions().bgColor(0xAAFFFF00)
                .fontSize(24).fontColor(0xFFFF00FF).text("百度地图SDK").typeface(face)
                .position(llText);
        mText = (Text) mBaiduMap.addOverlay(ooText);
        mText.setBorderWidth(5);
        mText.setBgColor(0xAAFFFF00);

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

        RichView richView1 = new RichView();
        richView1.setView(hhLayout);

        mText.addRichView(richView1);
        LatLngBounds bounds = new LatLngBounds.Builder().include(new LatLng(39.86923, 116.397428)).build();
        // 设置地图中心点以及缩放级别
        MapStatusUpdate
                mapStatusUpdate = MapStatusUpdateFactory.newLatLngZoom(bounds.getCenter(),15.0f);
        mBaiduMap.setMapStatus(mapStatusUpdate);

    }

    /**
     * 重置 Text
     */
    public void resetOverlay(View view) {
        // remove可以移除某一个overlay
        mText.remove();
        mClickText.setChecked(true);
        // 添加 overlay
        addText();
    }

    /**
     * 设置Text是否可点击
     */
    public void setTextClick(View view) {
        if (mText == null) {
            return;
        }
        CheckBox checkBox = (CheckBox) view;
        if (checkBox.isChecked()){
            mText.setClickable(true);
        } else {
            mText.setClickable(false);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 在activity执行onResume时必须调用mMapView. onResume ()
        mMapView.onResume();
    }

//    0000000000326e24

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
