/*
 *
 * SGLRender.java
 * 
 * Created by Wuwang on 2016/10/15
 */
package edu.wuwang.opengl.image;

import android.graphics.Bitmap;
import android.opengl.GLSurfaceView;
import android.view.View;


import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import edu.wuwang.opengl.image.filter.AFilter;
import edu.wuwang.opengl.image.filter.ColorFilter;
import edu.wuwang.opengl.image.filter.ContrastColorFilter;

/**
 * Description:这里是包装者模式，真正的渲染是ContrastColorFilter去实现的
 */
public class SGLRender implements GLSurfaceView.Renderer {

    //真正的渲染器
    private AFilter mFilter;
    //纹理bitmap
    private Bitmap bitmap;
    //View的宽高
    private int width,height;
    private boolean refreshFlag=false;
    //记录open配置
    private EGLConfig config;

    //构造方法
    public SGLRender(View mView){
        //真正的渲染器，传递上下文和渲染类型，创建默认渲染器
        mFilter=new ContrastColorFilter(mView.getContext(), ColorFilter.Filter.NONE);
    }


    public void setFilter(AFilter filter){
        refreshFlag=true;
        mFilter=filter;
        if(bitmap!=null){
            mFilter.setBitmap(bitmap);
        }
    }

    public void setImageBuffer(int[] buffer,int width,int height){
        bitmap= Bitmap.createBitmap(buffer,width,height, Bitmap.Config.RGB_565);
        mFilter.setBitmap(bitmap);
    }

    public void refresh(){
        refreshFlag=true;
    }

    public AFilter getFilter(){
        return mFilter;
    }



    //设置纹理图片
    public void setImage(Bitmap bitmap){
        this.bitmap=bitmap;
        //把纹理图片传递给真正的渲染器
        mFilter.setBitmap(bitmap);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        this.config=config;
        //调用真正的渲染
        mFilter.onSurfaceCreated(gl, config);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        //获取控件宽高
        this.width=width;
        this.height=height;
        //调用真正渲染
        mFilter.onSurfaceChanged(gl, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        //再次渲染，手动调用渲染
        if(refreshFlag&&width!=0&&height!=0){
            mFilter.onSurfaceCreated(gl, config);
            mFilter.onSurfaceChanged(gl,width,height);
            refreshFlag=false;
        }
        //调用真正渲染
        mFilter.onDrawFrame(gl);
    }
}
