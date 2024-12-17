package baidumapsdk.demo.geometry;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.baidu.mapapi.animation.TrackAnimation;
import com.baidu.mapapi.map.BM3DModel;
import com.baidu.mapapi.map.BM3DModelOptions;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.LineStyle;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Polyline;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.map.TextureOption;
import com.baidu.mapapi.map.TrackAnimationUpdateListener;
import com.baidu.mapapi.model.LatLng;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import baidumapsdk.demo.R;

public class TrackAnimationDemo extends AppCompatActivity implements View.OnClickListener{


    private final static String tag = TrackAnimationDemo.class.getSimpleName();

    private MapView mMapView;
    private BaiduMap mBaiduMap;
    private Marker mMarkerA;
    private Polyline mPolyline;
    private BitmapDescriptor bitmapA = BitmapDescriptorFactory.fromResource(R.drawable.icon_marka);

    TrackAnimation markerTrackAnimation;
    TrackAnimation polylineTrackAnimation;
    TrackAnimation texturePolylineTrackAnimation;
    TrackAnimation modelTrackAnimation;

    private BitmapDescriptor mRedTexture = BitmapDescriptorFactory.fromAsset("Icon_road_red_arrow.png");
    private BitmapDescriptor mBlueTexture = BitmapDescriptorFactory.fromAsset("Icon_road_blue_arrow.png");
    private BitmapDescriptor mGreenTexture = BitmapDescriptorFactory.fromAsset("Icon_road_green_arrow.png");

    private Polyline mTexturePolyline;

    private boolean is3dModelInit = false;
    private boolean isPolylineInit = false;

    private BM3DModel mBM3DModel;
    private String parentPath;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_animation_demo);
        mMapView = (MapView) findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();

        Button add3DModelBtn = findViewById(R.id.add_polyline_track_animation);
        Button add3DAnimationModelBtn = findViewById(R.id.add_texture_polyline_track_animation);
        Button remove3DModelBtn = findViewById(R.id.add_3dmodel_track_animation);
        add3DModelBtn.setOnClickListener(this);
        add3DAnimationModelBtn.setOnClickListener(this);
        remove3DModelBtn.setOnClickListener(this);

        parentPath = getFilesDir().getAbsolutePath();
        copyFilesAssets(this,"model3D",parentPath + "/model3D/");
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.add_polyline_track_animation) {
            if(!isPolylineInit){
                LatLng latLngA = new LatLng(39.97923, 116.357428);
                LatLng latLngB = new LatLng(39.94923, 116.397428);
                LatLng latLngC = new LatLng(39.97923, 116.437428);
                MarkerOptions markerOptionsA = new MarkerOptions().position(latLngA).icon(bitmapA);

                mMarkerA = (Marker) (mBaiduMap.addOverlay(markerOptionsA));
                List<LatLng> pointList = new ArrayList<>();
                pointList.add(latLngA);
                pointList.add(latLngB);
                pointList.add(latLngC);

                markerTrackAnimation = new TrackAnimation(pointList);
                markerTrackAnimation.setDuration(100000);
                markerTrackAnimation.setTrackPosRadio(0.0f, 1.0f);
                mMarkerA.setAnimation(markerTrackAnimation);

                // 覆盖物参数配置
                OverlayOptions ooPolyline = new PolylineOptions()
                        .width(10)
                        .color(Color.argb(255, 255, 0, 0))
                        .points(pointList);
                // 添加覆盖物
                mPolyline = (Polyline) mBaiduMap.addOverlay(ooPolyline);
                LineStyle forwardStyle = new LineStyle();
                forwardStyle.setColor(Color.argb(255, 255, 0, 0));
                forwardStyle.setWidth(10);
                mPolyline.setTrackForwardStyle(forwardStyle);
                LineStyle backwardStyle = new LineStyle();
                backwardStyle.setWidth(10);
                backwardStyle.setColor(Color.argb(0, 255, 0, 0));
                mPolyline.setTrackBackwardStyle(backwardStyle);
                polylineTrackAnimation = new TrackAnimation(pointList);
                polylineTrackAnimation.setDuration(100000);
                polylineTrackAnimation.setTrackPosRadio(0.0f, 1.0f);
                polylineTrackAnimation.setTrackLine(mPolyline);
                mPolyline.setAnimation(polylineTrackAnimation);
                isPolylineInit = true;
            }
            mPolyline.startAnimation();
            mMarkerA.startAnimation();
        }
        else if(v.getId() == R.id.add_texture_polyline_track_animation){
            LatLng latLngAAA = new LatLng(39.865, 116.444);
            LatLng latLngBBB = new LatLng(39.825, 116.494);
            LatLng latLngCCC = new LatLng(39.855, 116.534);
            LatLng latLngDDD = new LatLng(39.805, 116.594);
            List<LatLng> pointsListA = new ArrayList<LatLng>();
            pointsListA.add(latLngAAA);
            pointsListA.add(latLngBBB);
            pointsListA.add(latLngCCC);
            pointsListA.add(latLngDDD);
            // 折线多纹理分段绘制的纹理队列
            List<BitmapDescriptor> textureList = new ArrayList<BitmapDescriptor>();
            textureList.add(mRedTexture);
            textureList.add(mBlueTexture);
            textureList.add(mGreenTexture);
            // 折线每个点的纹理索引
            List<Integer> textureIndexs = new ArrayList<Integer>();
            textureIndexs.add(0);
            textureIndexs.add(1);
            textureIndexs.add(2);
            // 覆盖物参数配置
            OverlayOptions ooPolylineAA = new PolylineOptions().width(20)
                    .points(pointsListA)
                    .customTextureList(textureList)// 设置折线多纹理分段绘制的纹理队列
                    .textureIndex(textureIndexs);// 设置折线每个点的纹理索引            // 添加覆盖物
            mTexturePolyline = (Polyline) mBaiduMap.addOverlay(ooPolylineAA);

            texturePolylineTrackAnimation = new TrackAnimation(pointsListA);
            texturePolylineTrackAnimation.setDuration(100000);
            texturePolylineTrackAnimation.setTrackPosRadio(0,1);
            texturePolylineTrackAnimation.setTrackLine(mTexturePolyline);
            //设置轨迹动画运行时的前后线段的样式
            LineStyle forwardStyle = new LineStyle();
            forwardStyle.setBitmapResource(mRedTexture);
            forwardStyle.setWidth(20);
            forwardStyle.setTextureOption(TextureOption.Repeat);
            List<LineStyle> forwardStyles = new ArrayList<>();
            forwardStyles.add(forwardStyle);

            LineStyle forwardStyle1 = new LineStyle();
            forwardStyle1.setWidth(20);
            forwardStyle1.setTextureOption(TextureOption.Repeat);
            forwardStyle1.setBitmapResource(mBlueTexture);
            forwardStyles.add(forwardStyle1);

            LineStyle forwardStyle2 = new LineStyle();
            forwardStyle2.setWidth(20);
            forwardStyle2.setTextureOption(TextureOption.Repeat);
            forwardStyle2.setBitmapResource(mGreenTexture);
            forwardStyles.add(forwardStyle2);
            mTexturePolyline.setTrackForwardStyles(forwardStyles);

            LineStyle backwardStyle = new LineStyle();
            backwardStyle.setWidth(20);
            backwardStyle.setBitmapResource(mBlueTexture);
            List<LineStyle> backwardStyles = new ArrayList<>();
            backwardStyles.add(backwardStyle);
            backwardStyles.add(backwardStyle);
            backwardStyles.add(backwardStyle);
            mTexturePolyline.setTrackBackwardStyles(backwardStyles);

            mTexturePolyline.setAnimation(texturePolylineTrackAnimation);
            mTexturePolyline.startAnimation();

        }  else if (v.getId() == R.id.add_3dmodel_track_animation) {
            if(!is3dModelInit){
                BM3DModelOptions bm3DModelOptions = new BM3DModelOptions();
                bm3DModelOptions.setModelPath(parentPath + "/model3D");
                bm3DModelOptions.setModelName("among_us");
                bm3DModelOptions.setScale(10.0f);
                bm3DModelOptions.setPosition(new LatLng(39.915119,116.403963));
                mBM3DModel = (BM3DModel) mBaiduMap.addOverlay(bm3DModelOptions);

                LatLng latLngA = new LatLng(39.97923, 116.357428);
                LatLng latLngB = new LatLng(39.94923, 116.397428);
                LatLng latLngC = new LatLng(39.97923, 116.437428);
                List<LatLng> pointList = new ArrayList<>();
                pointList.add(latLngA);
                pointList.add(latLngB);
                pointList.add(latLngC);

                modelTrackAnimation = new TrackAnimation(pointList);
                modelTrackAnimation.setTrackPosRadio(0,1);
                modelTrackAnimation.setDuration(50000);
                modelTrackAnimation.setTrackUpdateListener(new TrackAnimationUpdateListener() {
                    @Override
                    public void onTrackUpdate(LatLng pt, float fAngle, float fPathFraction) {
                        Log.i("gucaohan","onTrackUpdate fAngle:"+fAngle);
                        mBM3DModel.setRotate(fAngle,fAngle,fAngle);
                    }
                });
                mBM3DModel.setAnimation(modelTrackAnimation);
                is3dModelInit = true;
            }
            mBM3DModel.startAnimation();
            mBM3DModel.setRotate(90,0,0);

        }
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
        // MapView的生命周期与Activity同步，当activity恢复时需调用MapView.onResume()
        mMapView.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // MapView的生命周期与Activity同步，当activity销毁时需调用MapView.destroy()
        mMapView.onDestroy();
    }

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

}
