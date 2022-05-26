/*
 *
 * AFilter.java
 * 
 * Created by Wuwang on 2016/10/17
 */
package edu.wuwang.opengl.image.filter;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import edu.wuwang.opengl.utils.ShaderUtils;

/**
 * Description: 真正的渲染base类
 */
public abstract class AFilter implements GLSurfaceView.Renderer {

    private Context mContext;
    private int mProgram;
    private int glHPosition;
    private int glHTexture;
    private int glHCoordinate;
    private int glHMatrix;
    private int hIsHalf;
    private int glHUxy;
    private Bitmap mBitmap;

    private FloatBuffer bPos;
    private FloatBuffer bCoord;

    private int textureId;
    private boolean isHalf;

    private float uXY;

    //定点和片段代码位置路径字符串
    private String vertex;
    private String fragment;
    //变换矩阵
    private float[] mViewMatrix=new float[16];
    private float[] mProjectMatrix=new float[16];
    private float[] mMVPMatrix=new float[16];

    //定点坐标
    private final float[] sPos={
            -1.0f,1.0f,
            -1.0f,-1.0f,
            1.0f,1.0f,
            1.0f,-1.0f
    };
    //纹理坐标
    private final float[] sCoord={
            0.0f,0.0f,
            0.0f,1.0f,
            1.0f,0.0f,
            1.0f,1.0f,
    };

    public AFilter(Context context,String vertex,String fragment){
        this.mContext=context;
        this.vertex=vertex;
        this.fragment=fragment;
        //写入本地顶点坐标
        ByteBuffer bb=ByteBuffer.allocateDirect(sPos.length*4);
        bb.order(ByteOrder.nativeOrder());
        bPos=bb.asFloatBuffer();
        bPos.put(sPos);
        bPos.position(0);
        //写入本地纹理
        ByteBuffer cc=ByteBuffer.allocateDirect(sCoord.length*4);
        cc.order(ByteOrder.nativeOrder());
        bCoord=cc.asFloatBuffer();
        bCoord.put(sCoord);
        bCoord.position(0);
    }
    //传递纹理资源图片
    public void setBitmap(Bitmap bitmap){
        this.mBitmap=bitmap;
    }


    public void setHalf(boolean half){
        this.isHalf=half;
    }

    public void setImageBuffer(int[] buffer,int width,int height){
        mBitmap= Bitmap.createBitmap(buffer,width,height, Bitmap.Config.RGB_565);
    }



    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        //设置背景图片颜色
        GLES20.glClearColor(1.0f,1.0f,1.0f,1.0f);
        //开启2D纹理渲染，默认纹理是关闭状态
        GLES20.glEnable(GLES20.GL_TEXTURE_2D);
        //获取着色器程序
        mProgram=ShaderUtils.createProgram(mContext.getResources(),vertex,fragment);
        //获取定点位置
        glHPosition=GLES20.glGetAttribLocation(mProgram,"vPosition");
        //获取纹理位置
        glHCoordinate=GLES20.glGetAttribLocation(mProgram,"vCoordinate");
        //获取纹理对象位置
        glHTexture=GLES20.glGetUniformLocation(mProgram,"vTexture");
        glHMatrix=GLES20.glGetUniformLocation(mProgram,"vMatrix");
        hIsHalf=GLES20.glGetUniformLocation(mProgram,"vIsHalf");
        glHUxy=GLES20.glGetUniformLocation(mProgram,"uXY");
        //提供给子类呗调用的方式
        onDrawCreatedSet(mProgram);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0,0,width,height);

        int w=mBitmap.getWidth();
        int h=mBitmap.getHeight();
        float sWH=w/(float)h;
        float sWidthHeight=width/(float)height;
        uXY=sWidthHeight;
        if(width>height){
            if(sWH>sWidthHeight){
                Matrix.orthoM(mProjectMatrix, 0, -sWidthHeight*sWH,sWidthHeight*sWH, -1,1, 3, 5);
            }else{
                Matrix.orthoM(mProjectMatrix, 0, -sWidthHeight/sWH,sWidthHeight/sWH, -1,1, 3, 5);
            }
        }else{
            if(sWH>sWidthHeight){
                Matrix.orthoM(mProjectMatrix, 0, -1, 1, -1/sWidthHeight*sWH, 1/sWidthHeight*sWH,3, 5);
            }else{
                Matrix.orthoM(mProjectMatrix, 0, -1, 1, -sWH/sWidthHeight, sWH/sWidthHeight,3, 5);
            }
        }
        //设置相机位置
        Matrix.setLookAtM(mViewMatrix, 0, 0, 0, 5.0f, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
        //计算变换矩阵
        Matrix.multiplyMM(mMVPMatrix,0,mProjectMatrix,0,mViewMatrix,0);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        //清空颜色，使用之前配置好的颜色
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT|GLES20.GL_DEPTH_BUFFER_BIT);
        //使用应用程序
        GLES20.glUseProgram(mProgram);
        //提供子类被调用的方式
        onDrawSet();
        //传递参数，默认是0
        GLES20.glUniform1i(hIsHalf,isHalf?1:0);
        //传递控件宽高比
        GLES20.glUniform1f(glHUxy,uXY);
        //传递变换矩阵
        GLES20.glUniformMatrix4fv(glHMatrix,1,false,mMVPMatrix,0);
        //开启定点数据
        GLES20.glEnableVertexAttribArray(glHPosition);
        GLES20.glEnableVertexAttribArray(glHCoordinate);
        //OpenGL默认激活的就是第一个纹理单元，第二个参数索引需要和纹理单元索引保持一致
        GLES20.glUniform1i(glHTexture, 0);
        //创建纹理
        textureId=createTexture();
        //传入数据，
        GLES20.glVertexAttribPointer(glHPosition,2,GLES20.GL_FLOAT,false,0,bPos);
        GLES20.glVertexAttribPointer(glHCoordinate,2,GLES20.GL_FLOAT,false,0,bCoord);
        //绘制矩形，按照abc bcd的方式绘制
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP,0,4);
    }


    //创建纹理函数
    private int createTexture(){
        int[] texture=new int[1];
        if(mBitmap!=null&&!mBitmap.isRecycled()){
            //创建一个纹理索引
            /**
             * 参数一：纹理数量
             * 参数二：纹理储存位置
             * 参数三：偏移量
             */
            GLES20.glGenTextures(1,texture,0);
            //需要绑定它，让之后任何的纹理指令都可以配置当前绑定的纹理
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,texture[0]);
            //设置缩小过滤为使用纹理中坐标最接近的一个像素的颜色作为需要绘制的像素颜色
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER,GLES20.GL_NEAREST);
            //设置放大过滤为使用纹理中坐标最接近的若干个颜色，通过加权平均算法得到需要绘制的像素颜色
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,GLES20.GL_TEXTURE_MAG_FILTER,GLES20.GL_LINEAR);
            //设置环绕方向S，截取纹理坐标到[1/2n,1-1/2n]。将导致永远不会与border融合
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S,GLES20.GL_CLAMP_TO_EDGE);
            //设置环绕方向T，截取纹理坐标到[1/2n,1-1/2n]。将导致永远不会与border融合
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T,GLES20.GL_CLAMP_TO_EDGE);
            //根据以上指定的参数，生成一个2D纹理
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, mBitmap, 0);
            return texture[0];
        }
        return 0;
    }

    public abstract void onDrawSet();
    public abstract void onDrawCreatedSet(int mProgram);

}
