/*
 *
 * SGLView.java
 * 
 * Created by Wuwang on 2016/10/15
 * Copyright © 2016年 深圳哎吖科技. All rights reserved.
 */
package edu.wuwang.opengl.image;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

import java.io.IOException;

import edu.wuwang.opengl.image.filter.AFilter;
import edu.wuwang.opengl.image.filter.ColorFilter;

/**
 * Description:
 */
public class SGLView extends GLSurfaceView {

    private SGLRender render;

    //构造方法
    public SGLView(Context context) {
        this(context,null);
    }
    //构造方法
    public SGLView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    //初始化
    private void init(){
        //设置版本
        setEGLContextClientVersion(2);
        //创建渲染器
        render=new SGLRender(this);
        //设置渲染器
        setRenderer(render);
        //设置渲染模式为手动模式
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        //读取纹理bitmap，设置给渲染器
        try {
            render.setImage(BitmapFactory.decodeStream(getResources().getAssets().open("texture/fengj.png")));
            //主动请求渲染
            requestRender();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //获取渲染器
    public SGLRender getRender(){
        return render;
    }

    //设置渲染器颜色过滤
    public void setFilter(AFilter filter){
        render.setFilter(filter);
    }

}
