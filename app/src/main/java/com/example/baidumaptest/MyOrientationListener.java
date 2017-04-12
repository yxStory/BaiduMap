package com.example.baidumaptest;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

/**
 * Created by yx on 2017/4/11.
 */
public class MyOrientationListener implements SensorEventListener{
    private SensorManager sensorManager;
    private Sensor sensor;

    //接口
    private OnOrientationListener onOrientationListener;
    //定义坐标x的位置
    float lastX;
    private Context mContext;
    //定义一个方法，给主函数调用,（主函数传过来的context即这里的mContext）
    public MyOrientationListener(Context context){
        this.mContext=context;
    }

    public void onStart(){
        //获取传感器服务，通过mContext
        sensorManager=(SensorManager)mContext.getSystemService(Context.SENSOR_SERVICE);
        if(sensorManager!=null){
            //传感器类型为Orientation
            sensor=sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        }
        if(sensor!=null){
            //注册监听器
            sensorManager.registerListener(this,sensor,SensorManager.SENSOR_DELAY_UI);
        }
    }

    public void onStop(){
       //取消注册监听器
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        //获取传感器x位置转换的值
        float x=event.values[SensorManager.DATA_X];
        if(Math.abs(x-lastX)>1.0){
            //调用接口的setOnOrientationChanged方法，将传感器获取的x位置传进去
            onOrientationListener.setOnOrientationChanged(lastX);
        }
        lastX=x;
    }

    //定义一个方法，给主函数调用（主函数传过来的onOrientationListener即这里的onOrientationListener）
    public void setOnOrientationListener(OnOrientationListener onOrientationListener){
        this.onOrientationListener=onOrientationListener;
    }

    //定义一个接口
    public interface OnOrientationListener{
        void setOnOrientationChanged(float x);
    }
}
