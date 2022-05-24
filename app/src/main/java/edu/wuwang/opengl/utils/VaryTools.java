package edu.wuwang.opengl.utils;

import android.opengl.Matrix;

import java.util.Arrays;
import java.util.Stack;

/**
 * Created by wuwang on 2016/10/30
 */

public class VaryTools {

    private float[] mMatrixCamera=new float[16];    //相机矩阵
    private float[] mMatrixProjection=new float[16];    //投影矩阵
    private float[] mMatrixCurrent=     //原始矩阵
            {1,0,0,0,
            0,1,0,0,
            0,0,1,0,
            0,0,0,1};

    private Stack<float[]> mStack;      //变换矩阵堆栈

    public VaryTools(){
        mStack=new Stack<>();
    }

    //保护现场，因为数据都是基本类型，所以可以理解为深拷贝
    public void pushMatrix(){
        mStack.push(Arrays.copyOf(mMatrixCurrent,16));
    }

    //恢复现场---把缓存的数据取出来
    public void popMatrix(){
        mMatrixCurrent=mStack.pop();
    }


    //清空栈
    public void clearStack(){
        mStack.clear();
    }

    //平移变换
    public void translate(float x,float y,float z){
        Matrix.translateM(mMatrixCurrent,0,x,y,z);
    }

    //旋转变换
    public void rotate(float angle,float x,float y,float z){
        Matrix.rotateM(mMatrixCurrent,0,angle,x,y,z);
    }

    //缩放变换
    public void scale(float x,float y,float z){
        Matrix.scaleM(mMatrixCurrent,0,x,y,z);
    }

    //设置相机
    public void setCamera(float ex,float ey,float ez,float cx,float cy,float cz,float ux,float uy,float uz){
        Matrix.setLookAtM(mMatrixCamera,0,ex,ey,ez,cx,cy,cz,ux,uy,uz);
    }
    //透视投影矩阵
    public void frustum(float left,float right,float bottom,float top,float near,float far){
        Matrix.frustumM(mMatrixProjection,0,left,right,bottom,top,near,far);
    }
    //正交投影矩阵
    public void ortho(float left,float right,float bottom,float top,float near,float far){
        Matrix.orthoM(mMatrixProjection,0,left,right,bottom,top,near,far);
    }
    //Vclip=Mprojection⋅Mview⋅Mmodel⋅Vlocal
    public float[] getFinalMatrix(){
        float[] ans=new float[16];
        Matrix.multiplyMM(ans,0,mMatrixCamera,0,mMatrixCurrent,0);
        Matrix.multiplyMM(ans,0,mMatrixProjection,0,ans,0);
        return ans;
    }

}
