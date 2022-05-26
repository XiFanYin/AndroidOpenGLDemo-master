/*
 *
 * CameraView.java
 * 
 * Created by Wuwang on 2016/11/14
 * Copyright © 2016年 深圳哎吖科技. All rights reserved.
 */
package edu.wuwang.opengl.camera;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Point;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

/**
 * Description:视频使用opengl去渲染
 */
public class CameraView extends GLSurfaceView implements GLSurfaceView.Renderer {

    private KitkatCamera mCamera2;
    private CameraDrawer mCameraDrawer;
    private int cameraId=0;
    private Runnable mRunnable;

    //构造函数
    public CameraView(Context context) {
        this(context,null);
    }
    //构造函数
    public CameraView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init(){
        //设置版本
        setEGLContextClientVersion(2);
        //设置渲染器
        setRenderer(this);
        //设置渲染模式手动模式
        setRenderMode(RENDERMODE_WHEN_DIRTY);
        //创建相机工具类
        mCamera2=new KitkatCamera();
        //创建相机渲染类
        mCameraDrawer=new CameraDrawer(getResources());
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        //调用真正的渲染
        mCameraDrawer.onSurfaceCreated(gl,config);
        //
        if(mRunnable!=null){
            mRunnable.run();
            mRunnable=null;
        }
        //打开相机
        mCamera2.open(cameraId);
        //传递前置摄像头和后置摄像头标识
        mCameraDrawer.setCameraId(cameraId);
        //获取预览大小
        Point point=mCamera2.getPreviewSize();
        //设置预览大小
        mCameraDrawer.setDataSize(point.x,point.y);
        //获取渲染的位置，和相机绑定
        mCamera2.setPreviewTexture(mCameraDrawer.getSurfaceTexture());
        //手动调用刷新
        mCameraDrawer.getSurfaceTexture().setOnFrameAvailableListener(new SurfaceTexture.OnFrameAvailableListener() {
            @Override
            public void onFrameAvailable(SurfaceTexture surfaceTexture) {
                requestRender();
            }
        });
        //开启预览
        mCamera2.preview();
    }

    public void switchCamera(){
        mRunnable=new Runnable() {
            @Override
            public void run() {
                mCamera2.close();
                cameraId=cameraId==1?0:1;
            }
        };
        onPause();
        onResume();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        mCameraDrawer.setViewSize(width,height);
        GLES20.glViewport(0,0,width,height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        mCameraDrawer.onDrawFrame(gl);
    }

    @Override
    public void onPause() {
        super.onPause();
        mCamera2.close();
    }
}
