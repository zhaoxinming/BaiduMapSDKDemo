package baidumapsdk.demo.createmap;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.ParticleEffectType;
import com.baidu.mapapi.map.ParticleOptions;
import com.baidu.mapapi.model.LatLng;

import baidumapsdk.demo.R;

/**
 * 基础地图类型
 */
public class ParticleDemo extends AppCompatActivity {

    private MapView mMapView;
    private BaiduMap mBaiduMap;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_particle_type);

        mMapView = (MapView) findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();
    }

    /**
     * 粒子效果
     */
    public void setParticleType(View view) {
        int id = view.getId();
        if (id == R.id.snow) {// 雪花
            mBaiduMap.showParticleEffectByType(ParticleEffectType.Snow);
        } else if (id == R.id.rain_storm) {// 暴雨
            mBaiduMap.showParticleEffectByType(ParticleEffectType.RainStorm);
        } else if (id == R.id.smog) {// 沙尘
            mBaiduMap.showParticleEffectByType(ParticleEffectType.Smog);
        } else if (id == R.id.fireworks) {// 自定义烟花粒子特效方法
            List<BitmapDescriptor> fireworksImgs = new ArrayList<>();
            BitmapDescriptor firework_bullet = BitmapDescriptorFactory.fromAsset("firework_bullet.png");
            BitmapDescriptor firework_tail = BitmapDescriptorFactory.fromAsset("firework_tail.png");
            fireworksImgs.add(firework_bullet);
            fireworksImgs.add(firework_tail);
            ParticleOptions particleOptions = new ParticleOptions();
            particleOptions.setParticleImgs(fireworksImgs);

            LatLng latLng = new LatLng(39.992147, 116.301934);
            particleOptions.setParticlePos(latLng);
            mBaiduMap.customParticleEffectByType(ParticleEffectType.Fireworks, particleOptions);
            mBaiduMap.showParticleEffectByType(ParticleEffectType.Fireworks);
        } else if (id == R.id.flower) {// 花瓣，支持自定义
            mBaiduMap.showParticleEffectByType(ParticleEffectType.Flower);
        } else if (id == R.id.unknow) {// 关闭所有粒子效果
            mBaiduMap.closeParticleEffectByType(ParticleEffectType.UnKnow);
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
    }
}
