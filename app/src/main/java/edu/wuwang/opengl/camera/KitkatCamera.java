/*
 *
 * KatkitCamera.java
 * 
 * Created by Wuwang on 2016/11/12
 * Copyright © 2016年 深圳哎吖科技. All rights reserved.
 */
package edu.wuwang.opengl.camera;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.graphics.Point;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.util.Log;

/**
 * Description:
 */
public class KitkatCamera implements ICamera {

    private Config mConfig;
    private Camera mCamera;
    private CameraSizeComparator sizeComparator;

    private Camera.Size picSize;
    private Camera.Size preSize;

    private Point mPicSize;
    private Point mPreSize;

    public KitkatCamera(){
        //创建宽高比bean
        this.mConfig=new Config();
        //设置预览最小宽度
        mConfig.minPreviewWidth=720;
        //设置照片最小宽度
        mConfig.minPictureWidth=720;
        //设置宽高比
        mConfig.rate=1.778f;
        //创建大小排序
        sizeComparator=new CameraSizeComparator();
    }

    @Override
    public boolean open(int cameraId) {
        //打开相机
        mCamera=Camera.open(cameraId);
        //如果能打开相机
        if(mCamera!=null){
            //获取相机参数配置对象
            Camera.Parameters param=mCamera.getParameters();
            //获取最优相片宽高比
            picSize=getPropPictureSize(param.getSupportedPictureSizes(),mConfig.rate,
                mConfig.minPictureWidth);
            //获取最优预览宽高比
            preSize=getPropPreviewSize(param.getSupportedPreviewSizes(),mConfig.rate,mConfig
                .minPreviewWidth);
            param.setPictureSize(picSize.width,picSize.height);
            param.setPreviewSize(preSize.width,preSize.height);
            //设置参数
            mCamera.setParameters(param);
            Camera.Size pre=param.getPreviewSize();
            Camera.Size pic=param.getPictureSize();
            //创建图片和预览的宽高
            mPicSize=new Point(pic.height,pic.width);
            mPreSize=new Point(pre.height,pre.width);
            Log.e("wuwang","camera previewSize:"+mPreSize.x+"/"+mPreSize.y);
            return true;
        }
        return false;
    }

    //相机预览需要一个SurfaceTexture，就把自己创建的SurfaceTexture传递过来，设置进去
    public void setPreviewTexture(SurfaceTexture texture){
        if(mCamera!=null){
            try {
                mCamera.setPreviewTexture(texture);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void setConfig(Config config) {
        this.mConfig=config;
    }

    //开启预览
    @Override
    public boolean preview() {
        if(mCamera!=null){
            mCamera.startPreview();
        }
        return false;
    }


    @Override
    public boolean switchTo(int cameraId) {
        close();
        open(cameraId);
        return false;
    }

    @Override
    public void takePhoto(TakePhotoCallback callback) {

    }

    @Override
    public boolean close() {
        if(mCamera!=null){
            try{
                mCamera.stopPreview();
                mCamera.release();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return false;
    }

    @Override
    public Point getPreviewSize() {
        return mPreSize;
    }

    @Override
    public Point getPictureSize() {
        return mPicSize;
    }

    @Override
    public void setOnPreviewFrameCallback(final PreviewFrameCallback callback) {
        if(mCamera!=null){
            mCamera.setPreviewCallback(new Camera.PreviewCallback() {
                @Override
                public void onPreviewFrame(byte[] data, Camera camera) {
                    callback.onPreviewFrame(data,mPreSize.x,mPreSize.y);
                }
            });
        }
    }

    public void addBuffer(byte[] buffer){
        if(mCamera!=null){
            mCamera.addCallbackBuffer(buffer);
        }
    }

    public void setOnPreviewFrameCallbackWithBuffer(final PreviewFrameCallback callback) {
        if(mCamera!=null){
            Log.e("wuwang","Camera set CallbackWithBuffer");
            mCamera.setPreviewCallbackWithBuffer(new Camera.PreviewCallback() {
                @Override
                public void onPreviewFrame(byte[] data, Camera camera) {
                    callback.onPreviewFrame(data,mPreSize.x,mPreSize.y);
                }
            });
        }
    }


    private Camera.Size getPropPreviewSize(List<Camera.Size> list, float th, int minWidth){
        Collections.sort(list, sizeComparator);

        int i = 0;
        for(Camera.Size s:list){
            if((s.height >= minWidth) && equalRate(s, th)){
                break;
            }
            i++;
        }
        if(i == list.size()){
            i = 0;
        }
        return list.get(i);
    }

    private Camera.Size getPropPictureSize(List<Camera.Size> list, float th, int minWidth){
        Collections.sort(list, sizeComparator);

        int i = 0;
        for(Camera.Size s:list){
            if((s.height >= minWidth) && equalRate(s, th)){
                break;
            }
            i++;
        }
        if(i == list.size()){
            i = 0;
        }
        return list.get(i);
    }

    private boolean equalRate(Camera.Size s, float rate){
        float r = (float)(s.width)/(float)(s.height);
        if(Math.abs(r - rate) <= 0.03)
        {
            return true;
        }
        else{
            return false;
        }
    }

    private class CameraSizeComparator implements Comparator<Camera.Size> {
        public int compare(Camera.Size lhs, Camera.Size rhs) {
            // TODO Auto-generated method stub
            if(lhs.height == rhs.height){
                return 0;
            }
            else if(lhs.height > rhs.height){
                return 1;
            }
            else{
                return -1;
            }
        }

    }


}
