package baidumapsdk.demo;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.common.BaiduMapSDKException;


public class DemoApplication extends Application {

    public static String SP_NAME = "privacy";

    public static String SP_KEY = "ifAgree";

    @Override
    public void onCreate() {
        super.onCreate();
        // 在使用 SDK 各组间之前初始化 context 信息，传入 ApplicationContext
        // 默认隐私政策接口初始化方法
        SharedPreferences sp = getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        boolean ifAgree = sp.getBoolean(SP_KEY, false);
        if (ifAgree) {
            SDKInitializer.setAgreePrivacy(this, true);
        } else {
            SDKInitializer.setAgreePrivacy(this, false);
        }

        try {
            SDKInitializer.initialize(this);
        } catch (BaiduMapSDKException e) {

        }
        //自4.3.0起，百度地图SDK所有接口均支持百度坐标和国测局坐标，用此方法设置您使用的坐标类型.
        //包括BD09LL和GCJ02两种坐标，默认是BD09LL坐标。
        SDKInitializer.setCoordType(CoordType.BD09LL);

    }
}