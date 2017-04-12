BaiduMap baiduMap;
MapView mapView=(MapView)findViewById(R.id.mapView);
baiduMap=mapView.getMap();


/**
*一、设置地图类型（NORMAL、SATELLITE）
*/
//普通地图
baiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
//卫星地图
baiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);

//开启实时交通图(zIndex=5)
baiduMap.setTrafficEnabled(true);
//开启城市热力图(zIndex=6)
baiduMap.setBaiduHeatMapEnabled(true);


/**
*二、MarkerOption标注指定位置（标注图层Marker，zIndex=9）
*/

LatLng ll=new LatLng(latitude,longtitude);
BitmapDescriptor bitmap=BitmapDescriptorFactory.fromResource(R.drawable.bitmap);

//----------MarkerOptions---------
OverlayOptions options=new MarkerOptions()
		.position(ll)       //图标位置
		.icon(bitmap)       //图标图片
		.draggable(true)    //可以被拖动
		.zIndex(9);
//!!!添加Marker图层!!!
baiduMap.addOverlay(options);

//其他覆盖物OverlayOptions
文字覆盖物TextOptions   (zIndex=9)
几何图形覆盖物PolygonOptions  (zIndex=8)
地形图图层GroundOverlayOptions  (zIndex=3)

//弹出窗覆盖物InfoWindow   (zIndex=12)
//检索结果覆盖物PoiOverlay   (zIndex=7)
//公交路线规划TransitRouteOverlay


/**
*三、定位相关

*1、定义定位客户端和定位监听器
*LocationClient locationClient；
*MyLocationListener myLocationListener;

*2、注册监听器
*locationClient.registerLocationListener(myLocationListener);

*3、包括设置定位参数（LocationClientOption）
*.setCoorType("bd0911")--坐标类型（百度经纬度）
*.setScanSpan(1000)--定位时间间隔（ms）
*.setOpenGps(true)--打开GPS
*.setIsNeedAddress(true)--是否需要位置信息

*4、定位监听（MyLocationListener implements BDLocationListener）
*【获取定位数据MyLocationData】
*设置经纬度、精度accuracy

*【定位图层显示方式MyLocationConfiguration】
*第一个参数：定位模式（普通、罗盘、跟随）
*第二个参数：是否显示方向
*第三个参数：设置定位图标（BitmapDescriptor）
*MyLocationConfiguration（LocationMode.NORMAL,true,bitmap);

*【当前位置更新显示在中心位置MapStatusUpdate】
*第一个参数：定位中心点位置，缩放比例（3-19）
*MapStatusUpdate update=MapStatusUpdateFactory.newLatLngZoom(ll，17);
*baiduMap.animateMapStatus(update);    //更新地图
*
*/

//定位客户端LocationClient
LocationClient locationClient;
//定位监听器MyLocationListener
MyLocationListener myLocationListener;

//实例化
locationClient=new LocationClient();
myLocationListener=new MyLocationListener();

//!!!注册定位监听器
locationClient.registerLocationListener(myLocationListener);

//---------设置定位客户端参数LocationClientOption-------------
LocationClientOption option=new LocationClientOption();
option.setScanSpan(1000);   //定位间隔时间
option.setCoorType("bd0911");   //坐标类型（bd0911为百度经纬度）
option.setOpenGps(true);    //打开GPS
option.setIsNeedAdress(true);   //是否需要位置信息
//!!!
locationClient.setLocOption(option);

//-----------监听器的类MyLocationListener------------
public class MyLoctionListener implements BDLocationListener
{
	
	//重写onReceiveLocation方法
	@Override
	public void onReceiveLocation(BDLocation location)
	{
		//------------获取定位数据MyLocationData--------------
		MyLocationData locData=new MyLocationData.Builder()
					.accuracy(location.getRadius())
					.laititude(location.getLatitude())
					.longtitude(location.getLongtitude())
					.builder();
		//!!!设置定位数据!!!
		baiduMap.setMyLocationData(locData);
		
		//---------配置定位图层显示方式MyLocationConfiguration-----------
		BitmapDescriptor bitmap=BitmapDescriptorFactory.fronResource(R.drawable.image);
		//第一个参数：定位模式（普通、罗盘、跟随）；第二个参数：是否显示方向；第三个参数：定位图标
		MyLocationConfiguration config=new MyLocationConfiguration(LocationMode.NORMAL,true,bitmap);
		//!!!设置定位图层显示方式!!!
		baiduMap.setMyLocationConfigeration(config);
		
		//----------更新地图MapStatusUpdate，定位到当前位置，当前位置处于中心，设置缩放比例--------------
		if(isFirstLoc){
			//第一次定位时更新
			isFirstLoc=false;
			LatLng ll=new LatLng(location.getLatitude(),location.getLongtitude());
			float f=baiduMap.getMaxZoomLevel();   //获取最大缩放比例
			//第一个参数为中心点坐标；第二个参数为缩放比例
			MapSatusUpdate update=MapStatusUpdateFactory.newLatLngZoom(ll,f-2);
			//!!!更新地图!!!
			baiduMap.animateMapStatus(update);
		}
	}
}


/**
*四、结合传感器，设置定位时的方向(direction)

*1、创建一个MyOrientationListener类，继承SensorEventListener

*2、获取传感器服务、注册传感器
//获取传感器服务getSystenService
*SensorManager sensorManager = (SensorManager)mContext.getSystenService(Context.SENSOR_SERVICE);
//获取传感器类型getDefaultSensor
*Sensor sensor = SensorManager.getDefaultSensor(Sensor.ORIENTATION);
//注册传感器（第一个参数：SensorEventListener；第二个参数：Sensor；第三个参数:Rate(获取传感器的速率））
*sensorManager.registerListener(this,sensor,SensorManager.SENSOR_DELAY_UI);

*3、定义OnOrientationListener接口，接口里实现setOnOrientationChanged方法
定义setOnOrientationListener(OnOrientationListener onOrientationListener)方法

*4、重写onSensorChanged方法
*public void onSensorChanged(SensorEvent event)
//获取传感器x位置的值
*float x=event.values[SensorManager.DATA_X];
//调用OnOrientationListener接口中的setOnOrientationChanged方法，传递x位置进去
*onOrientationListener.setOnOrientationChanged(x);

*5、在主函数中定义MyOrientationListener类
*MyOrientationListener myOrientationListener；
//调用setOnOrientationListener方法,new 一个OnOrientationListener接口
*myOrientationListener.setOnOrientationListener（new OnOrientationListener()
*重写setOnOrientationChanged方法，将w位置信息传递过来
*
*/

public class MyOrientationListener implements SensorEventListener
{
	//定义传感器服务器
	SensorManager sensorManager;
	//定义传感器类型
	Sensor sensor;
	//获取OnOrientationListener接口
	private OnOrientationListener onOrientationListener;
	
	//定义方向x位置的值
	float lastX=0.0;
	
	//定义上下文
	private Context mContext;
	//定义自身方法，传递上下文参数
	public MyOrientationListener(Context context)
	{
		this.mContext=context;
	}
	
	//---------------在onStart方法中获取传感器和注册监听器------------------
	public void onStart()
	{
		//获取传感器服务
		sensorManage=(SensorManager)mContext.getSystemService(Context.SENSOR_SERVICE);
		//获取传感器类型--Orientation方向传感器
		sensor=SensorManager.getDefaultSensor(Sensor.ORIENTATION);
		//注册监听器(第一个参数：SensorEventListener监听传感器事件的监听器；第二个参数：Sensor传感器类型；第三个参数：Rate获取传感器数据的频率）
		sensorManager.registerListener(this,sensor,SensorManager.SENSOR_DELAY_UI);
	}
	
	
	public void onStop()
	{
		//取消注册监听器
		sensorManager.unregisterListener(this);
	}
	
	
	//------------重写onSensorChanged方法，获取x位置变化信息-----------------------
	@Override
	public void onSensorChanged(SensorEvent event)
	{
		//获取传感器x位置的值（即方向信息）
		float x=event.values[SensorManager.DATA_X];
		//如果俩次x的位置大于1.0了，更新位置
		if(Math.abs(x-lastX)>1.0)
		{
			//调用OnOrientationListener接口的setOnOrientationChanged方法
			onOrientationListener.setOnOrientationChanged(x);
		}
		lastX=x;
	}
	
	//定义setOnOrientationListener方法，传递onOrientationListener接口
	public void setOnOrientationListener(OnOrientationListener onOrientationListener)
	{
		this.onOrientationListener=onOrientationListener;
	}
	
	//定义OnOrientationListener接口
	public interface OnOrientationListener
	{
		//定义一个方法，传递传感器x参数
		void setOnOrientationChanged(float x);
	}
}


//-----------------在主函数调用这个类----------------
private MyOrientationListener myOrientationLitener;
//定义x位置变化值
private mCurrentX;

//实例化MyOrientationListener对象，将应用的上下文传递过去
myOrientationListener=new MyOrientationListener(getApplicationContext());
//调用MyOrientationListener中的setOnOrientationListener方法
//(new 一个新的OnOrientationListener接口，重写该接口里的setOnOrientationChanged方法）
myOrientationListener.setOnOrientationListener(new OnOrientationListener())
{
	@Override
	public void setOnOrientationChanged(float x)
	{
		mCurrentX=x;
		//获取定位数据MyLocationData，将x位置加进去
		MyLocationData locData=new MyLocationData.Builder()
				.direction(mCurrentX)    //设置方向信息
				.latitude(latitude)
				.longtitude(longtitude)
				.accuracy(mCurrentAccuracy)
				.build();
		baiduMap.setMyLocationData(locData);
	}
}

onStart()
{
	//设置定位图层可见
	baiduMap.setMyLocationEnable(true);
	//调用MyOrientationListener的onStart方法，获取传感器服务和注册传感器
	myOrientationListener.onStart();
}

onStop()
{
	//设置定位图层不可见
	baiduMap.setMyLocationEnable(false);
	//调用MyOrientationListener的onStop方法，取消注册传感器
	myOrientationListener.onStop();
}


/**
*五、路线BaiduMapRoutePlan
*
*1、设置路线起始位置RouteParaOption
*
*2、打开百度地图路线
*
*/

//设置路线起始位置RouteParaOption
RouteParaOption option=new RouteParaOption()
		.startPoint(l1)
		.endPoint(l2);

try{
	//打开百度地图路线(三种路线选择：WalkingRout、TransitRout、DrivingRoute)
	BaiduMapRoutePlan.openBaiduMapDrivingRoute(option,MainActivity.this);
}
//异常处理，手机上没有百度地图客户端时
catch(Exception e){
	e.printStackTrace();
	//调用showDialog方法提示
	showDialog();
}



/**
*六、未下载百度地图对话框showDialog提醒下载OpenClientUtil
*/

AlertDialog.Builder builder=new AlertDialog.Builder(this);
builder.setTitle("");
builder.setMessage("");
builder.setPositiveButton("确定",new OnClickListener(){
	@Override 
	public void onClick(DialogInterface dialog,int i)
	{
		//点击后对话框消失
		dialog.dismiss();
		//利用OpenCilentUtil调用百度客户端工具类的getLatestBaiduMapApp方法，获取下载百度地图的网址
		OpenClientUtil.getLatestBaiduMapApp(MainActivity.this);
	}
});
builder.create().show();

