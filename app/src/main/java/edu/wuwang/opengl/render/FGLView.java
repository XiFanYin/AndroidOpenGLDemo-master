/*
 *
 * FGLView.java
 * 
 * Created by Wuwang on 2016/9/29
 */
package edu.wuwang.opengl.render;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

import edu.wuwang.opengl.render.FGLRender;
import edu.wuwang.opengl.render.Shape;

/**
 * Description:渲染的自定义View
 */
public class FGLView extends GLSurfaceView {

    //真正的渲染代码
    private FGLRender renderer;
    //构造方法
    public FGLView(Context context) {
        this(context,null);
    }
    //构造方法
    public FGLView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init(){
        //设置版本
        setEGLContextClientVersion(2);
        //设置自定义渲染器
        setRenderer(renderer=new FGLRender(this));
        //设置需主动调用绘制
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    //提供向自定义渲染器提供真正的渲染器
    public void setShape(Class<? extends Shape> clazz){
        try {
            renderer.setShape(clazz);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
