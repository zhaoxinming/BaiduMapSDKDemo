package baidumapsdk.demo.geometry;

import android.content.Context;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.baidu.mapapi.animation.ScaleAnimation;
import com.baidu.mapapi.map.BM3DModelOptions;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.track.TraceAnimationListener;
import com.baidu.mapapi.map.track.TraceOptions;
import com.baidu.mapapi.map.track.TraceOverlay;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import baidumapsdk.demo.R;

public class TrackOverlayUpgradeDemo extends AppCompatActivity implements View.OnClickListener, TraceAnimationListener  {
    private MapView mMapView;
    private BaiduMap mBaiDuMap;
    private TraceOverlay mTraceOverlay;
    private BitmapDescriptor bitmapA = BitmapDescriptorFactory.fromResource(R.drawable.icon_marka);
    private final static String tag = BM3DModelDemo.class.getSimpleName();
    private String parentPath;
    private double mapZoomFactor;       //轨迹点回调的误差因子
    private Marker mMarkerA;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_overlay_demo2);
        mMapView = findViewById(R.id.bmapView);
        mBaiDuMap = mMapView.getMap();
        mBaiDuMap.setMaxAndMinZoomLevel(20.0f,16.0f);
        Button addTrace = findViewById(R.id.add_3d_model);
        Button addTrace2 = findViewById(R.id.add_gradient_track);
        Button traceClean = findViewById(R.id.track_clean);
        addTrace.setOnClickListener(this);
        addTrace2.setOnClickListener(this);
        traceClean.setOnClickListener(this);
        //轨迹回调经纬度位置的精确度
        MapStatus mapStatus = mBaiDuMap.getMapStatus();
        mapZoomFactor = 0.01/(mapStatus.zoom * mapStatus.zoom);
        //3D模型数据
        parentPath = getFilesDir().getAbsolutePath();
        copyFilesAssets(this,"model3D",parentPath + "/model3D/");
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mTraceOverlay != null){
            mTraceOverlay.resume();
        }
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mTraceOverlay != null){
            mTraceOverlay.pause();
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
        if (v.getId() == R.id.add_3d_model) {
            if (null != mTraceOverlay) {
                mTraceOverlay.clear(); // 清除轨迹数据，但不会移除轨迹覆盖物
            }

            if(null!=mMarkerA){
                mMarkerA.remove();//清除途径点图标数据
                mMarkerA = null;
            }

            mBaiDuMap.clear();
            TraceOptions traceOptions = initTraceOptions();
            traceOptions.setPointMove(true);
            traceOptions.icon3D(init3DModelOptions());
            traceOptions.setDataSmooth(false);
            traceOptions.setTrackBloom(true);
            traceOptions.setBloomSpeed(10.0f);

            // 添加轨迹动画
            mTraceOverlay = mBaiDuMap.addTraceOverlay(traceOptions, this);
            upDataMapStatus();
        } else if (v.getId() == R.id.add_gradient_track) {
            if (null != mTraceOverlay) {
                mTraceOverlay.clear(); // 清除轨迹数据，但不会移除轨迹覆盖物
            }
            if(null!=mMarkerA){
                mMarkerA.remove();//清除途径点图标数据
                mMarkerA = null;
            }
            mBaiDuMap.clear();
            TraceOptions traceOptions = initTraceOptions();
            traceOptions.useColorArray(true);
            traceOptions.colors(arrColor);
            traceOptions.setDataSmooth(false);

            mTraceOverlay = mBaiDuMap.addTraceOverlay(traceOptions, this);
            upDataMapStatus();
        } else if (v.getId() == R.id.track_clean) {
            removeTrace();
        }
    }

    /**
     * 配置轨迹参数
     */
    private TraceOptions initTraceOptions() {
        TraceOptions traceOptions = new TraceOptions();
        traceOptions.animationTime(4000);
        traceOptions.animate(true);
        traceOptions.animationType(TraceOptions.TraceAnimateType.TraceOverlayAnimationEasingCurveLinear);
        traceOptions.color(0xAA0000FF);
        traceOptions.setTrackMove(true);
        traceOptions.width(10);
        traceOptions.setRotateWhenTrack(false);
        traceOptions.points(getTraceLocation());
        return traceOptions;
    }

    /**
     * 配置3D模型参数.暂时只支持.obj/.gltf格式
     */
    private BM3DModelOptions init3DModelOptions(){
        //3d模型轨迹点只支持如下设置
        BM3DModelOptions bm3DModelOptions = new BM3DModelOptions();
        ///路径和名称为必填项
        bm3DModelOptions.setModelPath(parentPath + "/model3D");
        bm3DModelOptions.setScale(20.0f);
//        bm3DModelOptions.setZoomFixed(false);
        //文件后缀名也需要一起传入
        bm3DModelOptions.setModelName("CesiumMan");
        bm3DModelOptions.setBM3DModelType(BM3DModelOptions.BM3DModelType.BM3DModelTypeglTF);
        bm3DModelOptions.setSkeletonAnimationEnable(true);
        bm3DModelOptions.animationSpeed(2.0f);
        bm3DModelOptions.animationRepeatCount(0);

        bm3DModelOptions.setRotate(0.0f,0.0f,0.0f);
        bm3DModelOptions.setOffset(0.0f,0.0f,-100.0f);
        return bm3DModelOptions;
    }

    /**
     * 添加图标
     */
    public void buildMarker(LatLng position) {
        // add marker overlay
        MarkerOptions markerOptionsA = new MarkerOptions()
                .position(position)
                .icon(bitmapA)// 设置 Marker 覆盖物的图标
                .zIndex(9)// 设置 marker 覆盖物的 zIndex
                ;
        if(mMarkerA==null)
        mMarkerA = (Marker) (mBaiDuMap.addOverlay(markerOptionsA));
        ScaleAnimation scaleAnimation = new ScaleAnimation(0f,1f);
        scaleAnimation.setDuration(1500);
        if(mMarkerA != null) {
            mMarkerA.setAnimation(scaleAnimation);
            mMarkerA.startAnimation();
        }

    }

    /**
     * 运行模型缩小的动画
     * 其它动画效果可参考MarkerAnimationDemo
     */
    public void scaleMarker(){
        ScaleAnimation scaleAnimation = new ScaleAnimation(1f,0f);
        scaleAnimation.setDuration(1500);
        if(mMarkerA != null) {
            mMarkerA.setAnimation(scaleAnimation);
            mMarkerA.startAnimation();
        }
    }

    /**
     * 移除图层
     */
    private void removeTrace() {
        if (null != mTraceOverlay) {
            mTraceOverlay.clear(); // 清除轨迹数据，但不会移除轨迹覆盖物
            mTraceOverlay.remove(); // 移除轨迹覆盖物
        }
        mBaiDuMap.clear();
    }

    /**
     * 更新地图范围
     */
    private void upDataMapStatus() {
        if (null == mTraceOverlay || null == mBaiDuMap) {
            return;
        }
        LatLngBounds latLngBounds = mTraceOverlay.getLatLngBounds();
        mBaiDuMap.setMapStatus(MapStatusUpdateFactory.newLatLngBounds(latLngBounds));
        //每次更新地图后获取地图的缩放比例尺
        MapStatus mapStatus = mBaiDuMap.getMapStatus();
        mapZoomFactor = 0.01/(mapStatus.zoom * mapStatus.zoom);
    }

    /**
     * 获取轨迹点
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
     *  从assets目录中复制整个文件夹内容
     */
    public void copyFilesAssets(Context context, String oldPath, String newPath) {
        InputStream is = null;
        FileOutputStream fos = null;
        try {
            String fileNames[] = context.getAssets().list(oldPath);// 获取assets目录下的所有文件及目录名
            for (int i = 0; i < fileNames.length; i++) {
                String fileNameStr = fileNames[i];
                Log.e(tag, "copyFilesFassets: " + fileNameStr);
            }
            if (fileNames.length > 0) {// 如果是目录
                File file = new File(newPath);
                file.mkdirs();// 如果文件夹不存在，则递归
                for (String fileName : fileNames) {
                    copyFilesAssets(context,oldPath + File.separator + fileName,newPath+File.separator+fileName);
                }
            } else { // 如果是文件
                is = context.getAssets().open(oldPath);
                fos = new FileOutputStream(new File(newPath));
                byte[] buffer = new byte[1024];
                int byteCount=0;
                while((byteCount=is.read(buffer))!=-1) {
                    fos.write(buffer, 0, byteCount);
                }
                fos.flush();// 刷新缓冲区
                is.close();
                fos.close();
            }
        } catch (Exception e) {
            Log.e(tag, "Copy custom style file failed", e);
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                Log.e(tag, "Close stream failed", e);
            }
        }
    }
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
        if(((position.latitude - 40.057997) < mapZoomFactor) && ((position.latitude - 40.057997) >-1.0 * mapZoomFactor)
                && ((position.longitude - 116.307725) < mapZoomFactor) && ((position.longitude - 116.307725) > -1.0 * mapZoomFactor)){
            buildMarker(position);
        }
    }

    /**
     * 轨迹动画完成
     */
    @Override
    public void onTraceAnimationFinish() {
        Log.i("TAG==", "onTraceAnimationFinish: ");
        /**
         * 触发轨迹结束动画
         */
        List<LatLng> viewPoints = new ArrayList<>();
        viewPoints.add(new LatLng(40.058099,116.305563));
        viewPoints.add(new LatLng(40.054633,116.307539));
        viewPoints.add(new LatLng(40.056066,116.310504));
        viewPoints.add(new LatLng(40.059144,116.309426));

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(viewPoints);
        LatLngBounds latLngBounds =  builder.build();
        //跟新地图范围的动画
        mBaiDuMap.animateMapStatus(MapStatusUpdateFactory.newLatLngBounds(latLngBounds), 1500);
        //Marker动画 缩小直到消失
        scaleMarker();
    }

}
