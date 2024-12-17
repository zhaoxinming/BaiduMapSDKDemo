package baidumapsdk.demo.geometry;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.baidu.mapapi.map.BMTrackType;
import com.baidu.mapapi.map.Track;
import com.baidu.mapapi.map.BMTrackOptions;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.track.TraceAnimationListener;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;

import android.content.Context;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import baidumapsdk.demo.R;

public class BM3DTrackDemo extends AppCompatActivity implements View.OnClickListener{

    private final static String tag = BM3DTrackDemo.class.getSimpleName();
    private MapView mMapView;
    private BaiduMap mBaiduMap;
    private int mWidth = 18;
    List<LatLng> points;            //轨迹点
    private Track track;
    private BitmapDescriptor mPalette =
            BitmapDescriptorFactory.fromResource(R.drawable.track_palette);
    private BitmapDescriptor mProjectionPalette =
            BitmapDescriptorFactory.fromResource(R.drawable.track_projection_palette);

    private TraceAnimationListener mTraceAnimationListener = new TraceAnimationListener() {
        @Override
        public void onTraceAnimationUpdate(int percent) {
            Log.i(tag,"onTraceAnimationUpdate : " + percent);
        }

        @Override
        public void onTraceUpdatePosition(LatLng position) {
            Log.i(tag,"onTraceUpdatePosition : " + position.toString());
        }

        @Override
        public void onTraceAnimationFinish() {
            Log.i(tag,"TraceAnimationFinish" );
        }
    };

    /**
     * 获取轨迹点的地理范围
     * @param points
     * @return
     */
    public LatLngBounds getLatLngBounds(List<LatLng> points) {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(points);
        return builder.build();
    }

    /**
     * 更新地图范围
     * @param points
     */
    private void upDataMapStatus(List<LatLng> points) {

        LatLngBounds latLngBounds = getLatLngBounds(points);
        mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLngBounds(latLngBounds));
        //每次更新地图后获取地图的缩放比例尺
        MapStatus mapStatus = mBaiduMap.getMapStatus();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bm_3d_track);
        // 初始化地图
        mMapView = (MapView) findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();

        Button addTrace = findViewById(R.id.add_3d_track);
        Button pauseTrack = findViewById(R.id.pause_track);
        Button traceClean = findViewById(R.id.resume_track);
        addTrace.setOnClickListener(this);
        pauseTrack.setOnClickListener(this);
        traceClean.setOnClickListener(this);
        points = getTrackDataFromFile(BM3DTrackDemo.this,"track_points.txt");
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.add_3d_track) {
            if(track != null){
                track.remove();
            }
            BMTrackOptions trackOptions = new BMTrackOptions();
            trackOptions.setTrackType(BMTrackType.Surface);

            int[] heights = new int[points.size()];
            int max = 0,min = 0;
            //自定义轨迹的高度
            for(int i = 0;i < points.size();i++){
                if( i <= 200){
                    heights[i] = (int) (0.1f * (i - 100) * (i - 100) + 400);
                    heights[i] = i * 6;
                } else if ( i <= 300){
                    heights[i] = (int)( 3 * i + 600);
                } else {
                    heights[i] = (int)(-(i - 400)* (i - 400) / 20.0f + 2000);
                }

                if(heights[i] > max){
                    max = heights[i];
                }
                if(heights[i] < min || min <= 0){
                   min = heights[i];
                }
            }

            trackOptions.setPoints(points);
            trackOptions.setHeights(heights);
            trackOptions.setPalette(mPalette);
            trackOptions.setPaletteOpacity(0.3f);
            trackOptions.setProjectionPalette(mProjectionPalette);
            trackOptions.setWidth(10);
            trackOptions.setAnimationTime(5000);
            trackOptions.setTraceAnimationListener(mTraceAnimationListener);
            trackOptions.setAnimateType(BMTrackOptions.BMTrackAnimateType.TraceOverlayAnimationEasingCurveLinear);
            track = (Track)mBaiduMap.addOverlay(trackOptions);
            upDataMapStatus(points);
        } else if(v.getId() == R.id.pause_track){
            if(track != null){
                track.pause();
            }
        } if (v.getId() == R.id.resume_track) {
            if(track != null){
                track.resume();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
        if(track != null){
            track.resume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
        if(track != null){
            track.pause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();

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

    private static final  int[] arrHeight = new int[]{
            10,15,20,
            25,30,35,
            40,45,50,
            55,60,65,
            70,75,80,
            85,
    };

    public List<LatLng> getTraceLocation() {
        ArrayList<LatLng> latLngList = new ArrayList<>();
        for (int i = 0; i < latlngs.length; i++) {
            latLngList.add(latlngs[i]);
        }
        return latLngList;

    }

    /**
     * 从资源文件中读取轨迹点数据
     * @param context
     * @param FileName
     * @return
     */
    private ArrayList<LatLng> getTrackDataFromFile(Context context, String FileName){
        ArrayList<LatLng> latLngList = new ArrayList<>();

        try {
            InputStream inputStream = null;
            inputStream = context.getAssets().open(FileName);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    double x = Double.parseDouble(parts[0].trim());
                    double y = Double.parseDouble(parts[1].trim());
                    latLngList.add(new LatLng(y,x));
                }
            }
            reader.close();
            inputStream.close();
        } catch (IOException e) {
            Log.e("LocationTypeDemo", "Copy track data file failed", e);
        }
        return latLngList;
    }

}
