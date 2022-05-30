/*
 *
 * CameraDrawer.java
 * 
 * Created by Wuwang on 2016/11/5
 * Copyright © 2016年 深圳哎吖科技. All rights reserved.
 */
package edu.wuwang.opengl.camera;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Arrays;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;

import edu.wuwang.opengl.filter.AFilter;
import edu.wuwang.opengl.filter.OesFilter;
import edu.wuwang.opengl.utils.Gl2Utils;
import edu.wuwang.opengl.utils.ShaderUtils;

/**
 * Description:
 */
public class CameraDrawer implements GLSurfaceView.Renderer {

    //变幻的矩阵
    private float[] matrix=new float[16];
    //new出来的关联自己的Texture的SurfaceTexture
    private SurfaceTexture surfaceTexture;
    //自定义预览View的宽高
    private int width,height;
    //预览宽高
    private int dataWidth,dataHeight;
    //抽取到其他对象中
    private AFilter mOesFilter;
    //相机id
    private int cameraId=1;

    //构造方法
    public CameraDrawer(Resources res){
        //创建对象
        mOesFilter=new OesFilter(res);
    }


    //设置预览的宽高 onSurfaceCreated调用
    public void setDataSize(int dataWidth,int dataHeight){
        this.dataWidth=dataWidth;
        this.dataHeight=dataHeight;
        //计算矩阵
        calculateMatrix();
    }
    //设置控件的宽高 onSurfaceChanged 调用
    public void setViewSize(int width,int height){
        this.width=width;
        this.height=height;
        calculateMatrix();
    }

    private void calculateMatrix(){
        //根据宽高计算出来裁剪矩阵
        Gl2Utils.getShowMatrix(matrix,this.dataWidth,this.dataHeight,this.width,this.height);
        //根据是前置摄像头还是后置摄像头进行旋转
        if(cameraId==1){
            //左右镜像
            Gl2Utils.flip(matrix,true,false);
            //旋转
            Gl2Utils.rotate(matrix,90);
        }else{
            //旋转
            Gl2Utils.rotate(matrix,270);
        }
        //把矩阵传递出去
        mOesFilter.setMatrix(matrix);
    }

    //返回创建的surfaceTexture对象
    public SurfaceTexture getSurfaceTexture(){
        return surfaceTexture;
    }
    //设置相机ID
    public void setCameraId(int id){
        this.cameraId=id;
        //摄像头旋转，计算变换矩阵
        calculateMatrix();
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        //创建一个纹理
        int texture = createTextureID();
        //创建SurfaceTexture 传入纹理对象引用
        surfaceTexture=new SurfaceTexture(texture);
        //创建渲染的Progream
        mOesFilter.create();
        //把纹理设置给其他对象
        mOesFilter.setTextureId(texture);
    }



    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        setViewSize(width,height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        //如果surfaceTexture不为null，就调用更新为最新帧
        if(surfaceTexture!=null){
            surfaceTexture.updateTexImage();
        }
        //调用绘制
        mOesFilter.draw();
    }

    private int createTextureID(){
        //可以储存多个纹理，因为我们这里只使用一个纹理，所以数组长度是1
        int[] texture = new int[1];
        //生成纹理
        GLES20.glGenTextures(1, texture, 0);
        //绑定纹理，设置纹理显示方式
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, texture[0]);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
            GL10.GL_TEXTURE_MIN_FILTER,GL10.GL_LINEAR);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
            GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
            GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
            GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);
        return texture[0];
    }

}
