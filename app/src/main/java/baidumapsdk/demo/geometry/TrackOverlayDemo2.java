package baidumapsdk.demo.geometry;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.baidu.mapapi.map.BMTrackOptions;
import com.baidu.mapapi.map.BMTrackType;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Track;
import com.baidu.mapapi.map.track.TraceAnimationListener;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;

import java.util.ArrayList;
import java.util.List;

import baidumapsdk.demo.R;

/**
 * 动态轨迹示例
 */
public class TrackOverlayDemo2 extends AppCompatActivity implements View.OnClickListener, TraceAnimationListener {

    private MapView mMapView;
    private BaiduMap mBaiDuMap;
    private Track track;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_overlay_demo);
        mMapView = findViewById(R.id.bmapView);
        mBaiDuMap = mMapView.getMap();
        Button addTrace = findViewById(R.id.add_trace);
        Button addTrace2 = findViewById(R.id.add_trace_overlay);
        Button addTrace3 = findViewById(R.id.add_trace_gradient);
        Button traceClean = findViewById(R.id.trace_clean);
        Button tracePause = findViewById(R.id.trace_pause);
        Button traceResume = findViewById(R.id.trace_resume);
        addTrace.setOnClickListener(this);
        addTrace2.setOnClickListener(this);
        addTrace3.setOnClickListener(this);
        traceClean.setOnClickListener(this);
        tracePause.setOnClickListener(this);
        traceResume.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(track != null){
            track.resume();
        }
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(track != null){
            track.pause();
        }
        mMapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        removeTrace();
        mMapView.onDestroy();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.add_trace) {
            if (track != null) {
                track.remove();
            }
            BMTrackOptions trackOptions = initTrackOption();
            track = (Track) mBaiDuMap.addOverlay(trackOptions);
            upDataMapStatus();
        }
        else if (v.getId() == R.id.add_trace_gradient) {
            if (null != track) {
                track.remove();
            }
            BMTrackOptions trackOptions = initTrackOption();
            trackOptions.setTrackType(BMTrackType.TrackGradient);
            track = (Track) mBaiDuMap.addOverlay(trackOptions);
            upDataMapStatus();
        }
        else if (v.getId() == R.id.add_trace_overlay) {
            if (null != track) {
                track.remove();
            }
            BMTrackOptions trackOptions = initTrackOption();
            trackOptions.setTrackType(BMTrackType.TrackGradient);
            trackOptions.setTrackMove(true);
            track = (Track) mBaiDuMap.addOverlay(trackOptions);
            upDataMapStatus();
        } else if(v.getId() == R.id.trace_pause){
            if (null != track) {
                track.pause();
            }
        } else if(v.getId() == R.id.trace_resume){
            if (null != track) {
                track.resume();
            }
        } else if (v.getId() == R.id.trace_clean) {
            removeTrace();
        }
    }

    /**
     * 配置轨迹参数
     */
    private BMTrackOptions initTrackOption() {
        BMTrackOptions trackOptions = new BMTrackOptions();
        trackOptions.setColor(0xAAFFFF00);
        trackOptions.setTrackType(BMTrackType.Track);
        trackOptions.setPoints(getTraceLocation());
        trackOptions.setColorsArray(getColorsArr());
        trackOptions.setPalette(BitmapDescriptorFactory.fromResource(R.drawable.track_palette));
        trackOptions.setPaletteOpacity(1.0f);
        trackOptions.setProjectionPalette(BitmapDescriptorFactory.fromResource(R.drawable.track_projection_palette));
        trackOptions.setWidth(5);
        trackOptions.setAnimationTime(5000);
        trackOptions.setTraceAnimationListener(this);
        trackOptions.setAnimateType(BMTrackOptions.BMTrackAnimateType.TraceOverlayAnimationEasingCurveEaseIn);
        return trackOptions;
    }

    /**
     * 移除图层
     */
    private void removeTrace() {
        if (null != track) {
            track.remove();
        }
        mBaiDuMap.clear();
    }

    /**
     * 更新地图范围
     */
    private void upDataMapStatus() {
        if (null == track || null == mBaiDuMap) {
            return;
        }
        LatLngBounds latLngBounds = getLatLngBounds(getTraceLocation());
        mBaiDuMap.setMapStatus(MapStatusUpdateFactory.newLatLngBounds(latLngBounds));
    }

    public LatLngBounds getLatLngBounds(List<LatLng> points) {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(points);
        return builder.build();
    }

    /**
     * 获取轨迹点
     *
     * @return 经纬度list
     */
    private List<LatLng> getTraceLocation() {
        ArrayList<LatLng> latLngList = new ArrayList<>();
        for (int i = 0; i < latlngs.length; i++) {
            latLngList.add(latlngs[i]);
        }
        return latLngList;
    }

    /**
     * 获取渐变轨迹颜色list
     *
     * @return 颜色list
     */
    private List<Integer> getColorsArr() {
        ArrayList<Integer> colorsList = new ArrayList<>();
        for (int i = 0; i < arrColor.length; i++) {
            colorsList.add(arrColor[i]);
        }
        return colorsList;
    }

    /**
     * 模拟轨迹点
     */
    private static final LatLng[] latlngs = new LatLng[]{
            new LatLng(40.055826, 116.307917), new LatLng(40.055916, 116.308455), new LatLng(40.055967, 116.308549),
            new LatLng(40.056014, 116.308574), new LatLng(40.056440, 116.308485), new LatLng(40.056816, 116.308352),
            new LatLng(40.057997, 116.307725), new LatLng(40.058022, 116.307693), new LatLng(40.058029, 116.307590),
            new LatLng(40.057913, 116.307119), new LatLng(40.057850, 116.306945), new LatLng(40.057756, 116.306915),
            new LatLng(40.057225, 116.307164), new LatLng(40.056134, 116.307546), new LatLng(40.055879, 116.307636),
            new LatLng(40.055826, 116.307697),
    };

    /**
     * 每个元素对应一个轨迹点的颜色
     * 注：轨迹点和颜色的数量必须保持一致
     */
    private static final  int[] arrColor = new int[]{
            0xAA0000FF,0xAA00FF00,0xAAFF0000,
            0xAA0000FF,0xAA00FF00,0xAAFF0000,
            0xAA0000FF,0xAA00FF00,0xAAFF0000,
            0xAA0000FF,0xAA00FF00,0xAAFF0000,
            0xAA0000FF,0xAA0000FF,0xAA00FF00,
            0xAAFF0000,
    };

    /**
     * @param percent 轨迹动画更新进度 0～100
     */
    @Override
    public void onTraceAnimationUpdate(int percent) {
        Log.i("TAG==", "onTraceAnimationUpdate: " + percent);
    }

    /**
     *
     * @param position 轨迹动画更新的当前位置点
     */
    @Override
    public void onTraceUpdatePosition(LatLng position) {
        Log.i("TAG==", "onTraceUpdatePosition: " + position.toString());
    }

    /**
     * 轨迹动画完成
     */
    @Override
    public void onTraceAnimationFinish() {
        Log.i("TAG==", "onTraceAnimationFinish: ");
    }
}