package com.example.baidumaptest;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolygonOptions;
import com.baidu.mapapi.map.Stroke;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.OpenClientUtil;
import com.baidu.mapapi.utils.route.BaiduMapRoutePlan;
import com.baidu.mapapi.utils.route.RouteParaOption;

import java.util.ArrayList;
import java.util.List;

public class BaiduMapTest extends Activity implements View.OnClickListener{
    /**
     * 地图实例
     */
    private BaiduMap baiduMap=null;
    /**
     * 地图控件
     */
    private MapView mapView=null;

    /**
     * 当前的定位模式
     */
    private MyLocationConfiguration.LocationMode mCurrentMode= MyLocationConfiguration.LocationMode.NORMAL;

    /**
     * 定位相关
     */
    private LocationClient locationClient;
    private MyLocationListener myLocationListener;
    /**
     *传感器监听器
     */
    private MyOrientationListener myOrientationListener;
    private int mCurrentX;
    /**
     *定位经纬度
     */
    private double mCurrentLat;
    private double mCurrentLng;
    private double latitude=30.323463;   //纬度
    private double longtitude=120.353125;   //经度
    /**
     * 定位精度
     */
    private float mCurrentAccuracy;
    /**
     * 是否第一次定位
     */
    boolean isFirstLoc=true;

    BitmapDescriptor bitmap;
    BitmapDescriptor ddd;

    /**
    * 界面控件
    */
    ToggleButton toggleButton;
    ImageButton reset,ibn;
    Button mMode,bn,route;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //init context before setContentView
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_baidu_map_test);
        mapView=(MapView)findViewById(R.id.mapView);
        baiduMap=mapView.getMap();
        //控件操作
        initMapView();
        //定位操作
        initMyLocation();
        //传感器操作
        initOrieneation();
    }

    /**
     * 传感器相关代码
     */
    private void initOrieneation() {
        //实例化MyOrientationListener，将context传过去
        myOrientationListener=new MyOrientationListener(getApplicationContext());
        //调用MyOrientationListener中的setOnOrientationListener方法
        myOrientationListener.setOnOrientationListener(new MyOrientationListener.OnOrientationListener() {
            @Override
            public void setOnOrientationChanged(float x) {
                mCurrentX=(int)x;
            }
        });
    }

    /**
     * 初始化定位相关代码
     */
    private void initMyLocation() {
        //定位客户端
        locationClient=new LocationClient(this);
        myLocationListener=new MyLocationListener();
        //注册定位监听器
        locationClient.registerLocationListener(myLocationListener);
        //设置定位参数
        LocationClientOption options=new LocationClientOption();
        options.setScanSpan(1000);   //设置定位间隔时间
        options.setCoorType("bd0911");   //设置坐标类型
        options.setOpenGps(true);    //打开GPS
        options.setIsNeedAddress(true);    //设置是否需要位置信息
        locationClient.setLocOption(options);
        locationClient.start();
    }

    /**
     * 初始化控件相关代码
     */
    private void initMapView() {
        toggleButton=(ToggleButton)findViewById(R.id.toggleButton);
        mMode=(Button) findViewById(R.id.mCurrentMode);
        reset=(ImageButton)findViewById(R.id.reset);
        ibn=(ImageButton)findViewById(R.id.ibn);
        bn=(Button)findViewById(R.id.bn);
        route=(Button)findViewById(R.id.route);

        //MapType：NORMAL or SATELLITE
        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked){
                    baiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
                }else{
                    baiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
                }
            }
        });
        //LocationMode：NORMAL 、COMPASS or FOLLOWING
        mMode.setText("普通");
        mMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (mCurrentMode){
                    case NORMAL:
                        mMode.setText("罗盘");
                        mCurrentMode= MyLocationConfiguration.LocationMode.COMPASS;
                        baiduMap.setMyLocationConfigeration(new MyLocationConfiguration(
                                mCurrentMode,true,bitmap
                        ));
                        break;
                    case COMPASS:
                        mMode.setText("跟随");
                        mCurrentMode= MyLocationConfiguration.LocationMode.FOLLOWING;
                        baiduMap.setMyLocationConfigeration(new MyLocationConfiguration(
                                mCurrentMode,true,bitmap
                        ));
                        break;
                    case FOLLOWING:
                        mMode.setText("普通");
                        mCurrentMode= MyLocationConfiguration.LocationMode.NORMAL;
                        baiduMap.setMyLocationConfigeration(new MyLocationConfiguration(
                                mCurrentMode,true,bitmap
                        ));
                        break;
                }
            }
        });
        //重新定位到当前位置
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LatLng latlng=new LatLng(mCurrentLat,mCurrentLng);
                //设置定位中心点
                MapStatusUpdate update= MapStatusUpdateFactory.newLatLngZoom(latlng,17);
                baiduMap.animateMapStatus(update);
            }
        });
        //定位到指定位置
        ibn.setOnClickListener(this);
        bn.setOnClickListener(this);
        //规划路线
        route.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LatLng l1=new LatLng(mCurrentLat,mCurrentLng);
                LatLng l2=new LatLng(latitude,longtitude);
                //设置路线的起点和终点
                RouteParaOption options=new RouteParaOption()
                        .startPoint(l1)
                        .endPoint(l2);
                try{
                    //打开百度地图的路线
                    //BaiduMapRoutePlan.setSupportWebRoute(true);
                    BaiduMapRoutePlan.openBaiduMapDrivingRoute(options,BaiduMapTest.this);
                }catch (Exception e){
                    e.printStackTrace();
                    showDialog();
                }
            }
        });
    }

    /**
     * 定位监听器，实现定位回调监听
     */
    public class MyLocationListener implements BDLocationListener{
        @Override
        public void onReceiveLocation(BDLocation location) {
            //获取当前位置
            mCurrentLat=location.getLatitude();
            mCurrentLng=location.getLongitude();
            //获取当前精度
            mCurrentAccuracy=location.getRadius();
            //获取定位数据
            MyLocationData locData=new MyLocationData.Builder()
                    .direction(mCurrentX)
                    .accuracy(mCurrentAccuracy)
                    .latitude(location.getLatitude())
                    .longitude(location.getLongitude())
                    .build();
            baiduMap.setMyLocationData(locData);
            //设置定位图标
            BitmapDescriptor bitmap=BitmapDescriptorFactory.fromResource(R.drawable.map_gps);
            //配置定位图层显示方式
            MyLocationConfiguration config=new MyLocationConfiguration(mCurrentMode,true,ddd);
            baiduMap.setMyLocationConfigeration(config);
            //第一次定位
            if(isFirstLoc){
                isFirstLoc=false;
                LatLng ll=new LatLng(mCurrentLat,mCurrentLng);
                //获取最大缩放级数
                float f=baiduMap.getMaxZoomLevel();
                //设置定位中心点
                MapStatusUpdate update=MapStatusUpdateFactory.newLatLngZoom(
                        ll,f-2);
                baiduMap.animateMapStatus(update);
            }
        }
    }

    @Override
    public void onClick(View view) {
        //添加Marker图层
        LatLng ll=new LatLng(latitude,longtitude);
        BitmapDescriptor bitmapDescriptor= BitmapDescriptorFactory
                .fromResource(R.drawable.icon_marka);
        OverlayOptions option=new MarkerOptions()
                .position(ll)
                .icon(bitmapDescriptor)
                .draggable(true)
                .zIndex(9);
        baiduMap.addOverlay(option);
        //添加多边形图层
        LatLng pt1=new LatLng(30.322123,120.352496);
        LatLng pt3=new LatLng(30.324761,120.354675);
        LatLng pt2=new LatLng(30.324765,120.352420);
        LatLng pt4=new LatLng(30.322197,120.354679);
        List<LatLng> pts=new ArrayList<LatLng>();
        pts.add(pt1);
        pts.add(pt2);
        pts.add(pt3);
        pts.add(pt4);
        OverlayOptions option2=new PolygonOptions()
                .fillColor(0x87CEEB00)
                .points(pts)
                .stroke(new Stroke(4,0x80808000));
        baiduMap.addOverlay(option2);
        //定位到指定位置，更新地图
        MapStatusUpdate update=MapStatusUpdateFactory.newLatLngZoom(ll,17);
        baiduMap.animateMapStatus(update);
        Toast.makeText(BaiduMapTest.this,"纬度："
                        + latitude+"  经度："+longtitude,
                Toast.LENGTH_SHORT).show();
    }

    /**
     * 没有安装百度地图时，点击路线，弹出对话框
     */
    public void showDialog(){
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("提示");
        builder.setMessage("您尚未安装百度地图app或app版本过低，点击确认安装？");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.dismiss();
                //跳转下载百度地图页面,OpenClientUtil调起百度客户端工具类
                OpenClientUtil.getLatestBaiduMapApp(BaiduMapTest.this);
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    @Override
    protected void onStart() {
        //设置定位图层可见
        baiduMap.setMyLocationEnabled(true);
        //调用传感器监听器的onStart方法
        myOrientationListener.onStart();
        super.onStart();
    }

    @Override
    protected void onResume() {
        mapView.onResume();
        super.onResume();
    }

    @Override
    protected void onPause() {
        mapView.onPause();
        super.onPause();
    }

    @Override
    protected void onStop() {
        //设置定位图层不可见
        baiduMap.setMyLocationEnabled(false);
        //调用传感器监听器的onStop方法
        myOrientationListener.onStop();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        if(mapView!=null){
            mapView.onDestroy();
            mapView=null;
        }
        super.onDestroy();
    }
}
