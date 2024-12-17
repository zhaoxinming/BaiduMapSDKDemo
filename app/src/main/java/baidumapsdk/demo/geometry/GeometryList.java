package baidumapsdk.demo.geometry;


import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListView;

import com.baidu.mapapi.map.OverlayUtil;

import baidumapsdk.demo.R;
import baidumapsdk.demo.util.DemoInfo;
import baidumapsdk.demo.util.DemoListAdapter;

public class GeometryList extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menulist);
        ListView demoList = (ListView) findViewById(R.id.mapList);
        // 添加ListItem，设置事件响应
        demoList.setAdapter(new DemoListAdapter(GeometryList.this, DEMOS));
        demoList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> arg0, View v, int index, long arg3) {
                onListItemClick(index);
            }
        });
    }

    void onListItemClick(int index) {
        Intent intent;
        intent = new Intent(GeometryList.this, DEMOS[index].demoClass);
        this.startActivity(intent);
    }

    private static final DemoInfo[] DEMOS = {
            new DemoInfo(R.string.demo_title_marker, R.string.demo_desc_marker, MarkerDemo.class),
            new DemoInfo(R.string.demo_title_marker2, R.string.demo_desc_marker2, RichViewDemo.class),
            new DemoInfo(R.string.demo_title_marker_animation, R.string.demo_desc_marker_animation, MarkerAnimationDemo.class),
            new DemoInfo(R.string.demo_title_track_animation, R.string.demo_desc_track_animation, TrackAnimationDemo.class),
            new DemoInfo(R.string.demo_title_cluster, R.string.demo_desc_cluster, MarkerClusterDemo.class),
            new DemoInfo(R.string.demo_title_polyline, R.string.demo_desc_polyline, PolylineDemo.class),
            new DemoInfo(R.string.demo_title_polygon, R.string.demo_desc_polygon, PolygonDemo.class),
            new DemoInfo(R.string.demo_title_circle, R.string.demo_desc_circle, CircleDemo.class),
            new DemoInfo(R.string.demo_title_dot, R.string.demo_desc_dot, DotDemo.class),
            new DemoInfo(R.string.demo_title_arc, R.string.demo_desc_arc, ArcDemo.class),
            new DemoInfo(R.string.demo_title_text,R.string.demo_desc_text,TextDemo.class),
            new DemoInfo(R.string.demo_title_textPathMarker, R.string.demo_desc_textPathMarker, TextPathMarkerDemo.class),
            new DemoInfo(R.string.demo_title_groundoverlay, R.string.demo_desc_groundoverlay, GroundOverlayDemo.class),
            new DemoInfo(R.string.demo_title_tile, R.string.demo_desc_tile, TileOverlayDemo.class),
            new DemoInfo(R.string.demo_title_heat, R.string.demo_desc_heat, HeatMapDemo.class),
            new DemoInfo(R.string.demo_title_opengl, R.string.demo_desc_opengl, OpenglDemo.class),
            new DemoInfo(R.string.demo_title_track_show, R.string.demo_desc_track_show, TrackShowDemo.class),
            new DemoInfo(R.string.demo_title_Prism_show, R.string.demo_desc_Prism_show, Building3DPrismDemo.class),
            new DemoInfo(R.string.demo_title_Building_show, R.string.demo_desc_Building_show, Building3DDemo.class),
            new DemoInfo(R.string.demo_title_multi_point_show, R.string.demo_desc_multi_point_show, MultiPointOverlayDemo.class),
            new DemoInfo(R.string.demo_title_track_overlay, R.string.demo_desc_track_overlay, TrackOverlayDemo.class),
            new DemoInfo(R.string.demo_title_track_overlay_track2, R.string.demo_desc_track_overlay_track2, TrackOverlayDemo2.class),
            new DemoInfo(R.string.demo_title_track_overlay2, R.string.demo_desc_track_overlay2, TrackOverlayUpgradeDemo.class),
            new DemoInfo(R.string.demo_title_3d_track, R.string.demo_desc_3d_track, BM3DTrackDemo.class),

            new DemoInfo(R.string.demo_title_3d_modle_show, R.string.demo_desc_3d_modle_show, BM3DModelDemo.class),
            new DemoInfo(R.string.demo_title_near_cars_show, R.string.demo_desc_near_cars_show, NearCarsDemo.class),
            new DemoInfo(R.string.demo_title_marker_collision_show, R.string.demo_desc_marker_collision_show, MarkerCollisionDemo.class),
            new DemoInfo(R.string.demo_title_marker_poi_collision_show, R.string.demo_desc_marker_poi_collision_show, MarkerPoiCollidedDemo.class),
            new DemoInfo(R.string.demo_title_3d_heat_map_show, R.string.demo_desc_3d_heat_map_show, HeatMap3DDemo.class),
            new DemoInfo(R.string.demo_title_hexagon_map_show, R.string.demo_desc_hexagon_map_show, HexagonMapDemo.class)
    };
}

