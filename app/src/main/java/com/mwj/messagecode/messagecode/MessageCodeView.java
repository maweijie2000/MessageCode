package com.mwj.messagecode.messagecode;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;


/**
 * 密码框
 * Created by Jimes
 */

public class MessageCodeView extends View {

    private Paint mPaint;   //绘制对象

    private Handler mHandler; //用于指示输入提示

    private boolean isDrawText;//是否绘制文本

    private boolean isInputState = false;//是否输入状态

    private int mInputStateBoxColor;  //输入状态下框颜色
    private int mNoInputStateBoxColor;//未输入状态下框颜色
    private int mInputedStateBoxColor;//已输入状态下框颜色

    private int mTextColor;

    private int mWidth = 30;
    private int mheight = 40;

    private String mPassText = "";//要绘制的文字

    private Context mContext;

    private int mDrawTxtSize = 18;

    private int mDrawBoxLineSize = 4;

    public void setInputStateColor(int inputColor) {
        this.mInputStateBoxColor = inputColor;
    }

    public void setmPassText(String mPassText) {
        this.mPassText = mPassText;
    }

    public void setNoinputColor(int noinputColor) {
        this.mNoInputStateBoxColor = noinputColor;
    }

    public void setInputedStateBoxColor(int mInputedStateBoxColor) {
        this.mInputedStateBoxColor = mInputedStateBoxColor;
    }

    public void setInputState(boolean input) {
        isInputState = input;
    }

    public void setTextColor(int mTextColor) {
        this.mTextColor = mTextColor;
    }


    public MessageCodeView(Context context) {
        this(context, null);
    }

    public MessageCodeView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public void setmDrawTxtSize(int mDrawTxtSize) {
        this.mDrawTxtSize = mDrawTxtSize;
    }

    public void setmDrawBoxLineSize(int mDrawBoxLineSize) {
        this.mDrawBoxLineSize = mDrawBoxLineSize;
    }

    public MessageCodeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        // 初始化画笔
        mPaint = new Paint();
        mPaint.setAntiAlias(true);

        // 设置“空心”的外框的宽度
        mPaint.setStrokeWidth(mDrawBoxLineSize);
        mPaint.setPathEffect(new CornerPathEffect(1));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
        int modeWidth = MeasureSpec.getMode(widthMeasureSpec);
        int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);
        int modeHeight = MeasureSpec.getMode(heightMeasureSpec);

        int width = 0;
        int height = 0;

        if (modeWidth == MeasureSpec.EXACTLY) {//如果是精确测量 则直接返回值
            width = sizeWidth;
        } else {//指定宽度的大小
            width = mWidth;
            if (modeWidth == MeasureSpec.AT_MOST) {//如果是最大值模式  取当中的小值  防止超出父类控件的最大值
                width = Math.min(width, sizeWidth);
            }
        }

        if (modeHeight == MeasureSpec.EXACTLY) {//如果是精确测量 则直接返回值
            height = sizeHeight;
        } else {//指定高度的大小
            height = mheight;
            if (modeHeight == MeasureSpec.AT_MOST) {//如果是最大值模式  取当中的小值  防止超出父类控件的最大值
                height = Math.min(height, sizeHeight);
            }
        }
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        drawInputBox(canvas);    //绘制输入框

        drawInputTextOrPicture(canvas);         //绘制输入文本

    }

    /**
     * 绘制输入文本或密码图案
     *
     * @param canvas
     */
    private void drawInputTextOrPicture(Canvas canvas) {
        if (isDrawText) {            //是否需要进行绘制

            //绘制输入数据
            mPaint.setTextSize(mDrawTxtSize);//绘制字体大小
            mPaint.setColor(mTextColor);
            float stringWidth2 = mPaint.measureText(mPassText);

            float baseY2 = (getMeasuredHeight() / 2 - ((mPaint.descent() + mPaint.ascent()) / 2)) + stringWidth2 / 5;  //实现y轴居中方法
            float baseX2 = getMeasuredWidth() / 2 - stringWidth2 / 2;  //实现X轴居中方法
            canvas.drawText(mPassText, baseX2, baseY2, mPaint); //文字

        }
    }

    /**
     * 绘制输入框
     *
     * @param canvas
     */
    private void drawInputBox(Canvas canvas) {
        if (isDrawText) {

            mPaint.setColor(ContextCompat.getColor(mContext, mInputedStateBoxColor));
            mPaint.setStyle(Paint.Style.FILL);

            float baseY = getMeasuredHeight() / 2;  //实现y轴居中方法
            canvas.drawLine(0, baseY, getMeasuredWidth(), baseY, mPaint);

        } else {
            if (isInputState) { //是否是输入状态  输入状态和未输入状态颜色区分
                mPaint.setColor(ContextCompat.getColor(mContext, mInputStateBoxColor));
            } else {
                mPaint.setColor(ContextCompat.getColor(mContext, mNoInputStateBoxColor));
            }

            mPaint.setStyle(Paint.Style.FILL);

            float baseY = getMeasuredHeight() / 2;  //实现y轴居中方法
            canvas.drawLine(0, baseY, getMeasuredWidth(), baseY, mPaint);

        }

    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
    }


    /**
     * 刷新状态
     *
     * @param isinput 是否已经输入过
     */
    public void updateInputState(boolean isinput) {
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
        if (isinput) {
            isInputState = true;
            isDrawText = true;
        } else {
            isInputState = false;
            isDrawText = false;
        }
        invalidate();
    }

    /**
     * 设置为选中输入状态
     */
    public void startInputState() {
        isInputState = true;
        isDrawText = false;

        if (mHandler == null) {
            mHandler = new Handler();
        }
        mHandler.removeCallbacksAndMessages(null);

        mHandler.post(new Runnable() {
            @Override
            public void run() {                 //循环绘制 造成闪动状态
                invalidate();
                mHandler.postDelayed(this, 800);

            }
        });
    }

}
