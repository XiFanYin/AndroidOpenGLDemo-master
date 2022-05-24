package edu.wuwang.opengl.image.filter;

import android.content.Context;
import android.opengl.GLES20;

/**
 * Created by wuwang on 2016/10/22
 *
 * 真正的渲染器，继承父类
 */
public class ContrastColorFilter extends AFilter {

    //渲染模式
    private ColorFilter.Filter filter;

    private int hChangeType;
    private int hChangeColor;

    //传递顶点着色器和片段着色器
    public ContrastColorFilter(Context context, ColorFilter.Filter filter) {
        super(context, "filter/half_color_vertex.sh", "filter/half_color_fragment.sh");
        //设置构成方法传递过来的渲染类型
        this.filter=filter;
    }

    @Override
    public void onDrawSet() {
        //向着色器传递数据
        GLES20.glUniform1i(hChangeType,filter.getType());
        //函数需要3个float向量/数组作为它的值
        GLES20.glUniform3fv(hChangeColor,1,filter.data(),0);
    }

    @Override
    public void onDrawCreatedSet(int mProgram) {
        //查询uniform的位置值
        hChangeType=GLES20.glGetUniformLocation(mProgram,"vChangeType");
        hChangeColor=GLES20.glGetUniformLocation(mProgram,"vChangeColor");
    }

}
