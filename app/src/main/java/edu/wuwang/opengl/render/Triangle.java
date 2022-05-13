/*
 *
 * Triangle.java
 *
 * Created by Wuwang on 2016/9/30
 */
package edu.wuwang.opengl.render;

import android.opengl.GLES20;
import android.view.View;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Description:
 */
public class Triangle extends Shape {

    private FloatBuffer vertexBuffer;
    private final String vertexShaderCode =
            "attribute vec4 vPosition;" +
                    "void main() {" +
                    "  gl_Position = vPosition;" +
                    "}";

    private final String fragmentShaderCode =
            "precision mediump float;" +
                    "uniform vec4 vColor;" +
                    "void main() {" +
                    "  gl_FragColor = vColor;" +
                    "}";

    private int mProgram;

    static final int COORDS_PER_VERTEX = 3;
    static float triangleCoords[] = {
            0.5f, 0.5f, 0.0f, // top
            -0.5f, -0.5f, 0.0f, // bottom left
            0.5f, -0.5f, 0.0f  // bottom right
    };

    private int mPositionHandle;
    private int mColorHandle;


    //顶点个数
    private final int vertexCount = triangleCoords.length / COORDS_PER_VERTEX;
    //顶点之间的偏移量
    private final int vertexStride = COORDS_PER_VERTEX * 4; // 每个顶点四个字节



    //设置颜色，依次为红绿蓝和透明通道
    float color[] = {1.0f, 1.0f, 1.0f, 1.0f};

    public Triangle(View mView) {
        super(mView);
        //虚拟机数据复制到本地内存
        ByteBuffer bb = ByteBuffer.allocateDirect(
                triangleCoords.length * 4);
        bb.order(ByteOrder.nativeOrder());
        //转换成浮点数据
        vertexBuffer = bb.asFloatBuffer();
        //把虚拟机数据复制到本地内存中
        vertexBuffer.put(triangleCoords);
        //位置数据从起始位置开始读取
        vertexBuffer.position(0);
        //调用父类方法编译源码，传递类型为顶点
        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER,
                vertexShaderCode);
        //调用父类方法编译源码，传递类型为片段
        int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER,
                fragmentShaderCode);

        //创建一个空的OpenGLES程序
        mProgram = GLES20.glCreateProgram();
        //将顶点着色器加入到程序
        GLES20.glAttachShader(mProgram, vertexShader);
        //将片元着色器加入到程序中
        GLES20.glAttachShader(mProgram, fragmentShader);
        //连接到着色器程序
        GLES20.glLinkProgram(mProgram);
        //着色器对象链接到程序对象以后，记得删除着色器对象,及时释放内存，C#语言不会主动像java一样垃圾回收机制
        GLES20.glDeleteShader(vertexShader);
        GLES20.glDeleteShader(fragmentShader);



    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {

    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {

    }

    @Override
    public void onDrawFrame(GL10 gl) {

        //激活程序。默认是非激活状态
        GLES20.glUseProgram(mProgram);
        //获取属性位置
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
        //解释顶点数据
        /**
         * 参数1：属性位置
         * 参数2：每个属性数据计数，三维所以这里是3
         * 参数3：数据类型
         * 参数4：数据是否被标准化（-1 ，1区间）
         * 参数5:每个定点数据的步长，说白了：属性第二次出现的地方到第一个出现的首位置有多少个字节，包前不包后
         * 参数6：数据地址
         */
        GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                vertexStride, vertexBuffer);
        //启用三角形顶点的句柄，默认是禁用的
        GLES20.glEnableVertexAttribArray(mPositionHandle);
        //获取片元着色器的vColor成员的句柄
        mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");
        //设置绘制三角形的颜色
        GLES20.glUniform4fv(mColorHandle, 1, color, 0);
        /**
         * 参数一：绘制图元类型
         * 参数二：顶点数组的起始索引
         * 参数三：定点个数
         */
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount);
        //禁止顶点数组的句柄
        GLES20.glDisableVertexAttribArray(mPositionHandle);
    }

}
