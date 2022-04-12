/*
 *
 * FGLRender.java
 * 
 * Created by Wuwang on 2016/9/29
 */
package edu.wuwang.opengl.render;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.view.View;

import java.lang.reflect.Constructor;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Description:这个Render是包装者模式，对传递过来的绘制对象进行包装，真正干活的是传递过来的shape
 */
public class FGLRender extends Shape {
    //被包装者对象
    private Shape shape;
    //被包装者class
    private Class<? extends Shape> clazz=Cube.class;

    public FGLRender(View mView) {
        super(mView);
    }

    //依赖注入被包装者对象
    public void setShape(Class<? extends Shape> shape){
        this.clazz=shape;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        //设置清空屏幕所用的颜色，状态设置函数
        GLES20.glClearColor(0.5f,0.5f,0.5f,1.0f);
        Log.e("wuwang","onSurfaceCreated");
        try {
            //通过反射获取依赖注入的渲染器对象
            Constructor constructor=clazz.getDeclaredConstructor(View.class);
            constructor.setAccessible(true);
            shape= (Shape) constructor.newInstance(mView);
        } catch (Exception e) {
            e.printStackTrace();
            //如果失败，默认三角形
            shape=new Cube(mView);
        }
        //调用真正的绘制方法
        shape.onSurfaceCreated(gl,config);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        Log.e("wuwang","onSurfaceChanged");
        //设置宽度和高度
        GLES20.glViewport(0,0,width,height);
        //宽度和高度传递到真正的绘制对象内
        shape.onSurfaceChanged(gl, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        Log.e("wuwang","onDrawFrame");
        //清空缓存区颜色，缓冲区颜色会被填充为glClearColor设置的颜色，状态使用函数
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT|GLES20.GL_DEPTH_BUFFER_BIT);
        //调用真正的绘制
        shape.onDrawFrame(gl);
    }

}
