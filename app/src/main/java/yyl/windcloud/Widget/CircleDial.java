package yyl.windcloud.Widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import com.orhanobut.logger.Logger;

/**
 #         ┌─┐       ┌─┐
 #      ┌──┘ ┴───────┘ ┴──┐
 #      │                 │
 #      │       ───       │
 #      │  ─┬┘       └┬─  │
 #      │                 │
 #      │       ─┴─       │
 #      │                 │
 #      └───┐         ┌───┘
 #          │         │
 #          │         │
 #          │         │
 #          │         └──────────────┐
 #          │                        │
 #          │                        ├─┐
 #          │                        ┌─┘
 #          │                        │
 #          └─┐  ┐  ┌───────┬──┐  ┌──┘
 #            │ ─┤ ─┤       │ ─┤ ─┤
 #            └──┴──┘       └──┴──┘
 #                神兽保佑
 #                代码无BUG!
 * Created by yinyiliang on 2016/7/26 0026.
 */
public class CircleDial extends View {
    private float centerX, centerY;
    private Paint linePaint; // 线条画笔
    private TextPaint mTextPaint; //文字画笔
    // 有渐变颜色的旋转起止角度
    private float startAngle ,stopAngle;
    // 圆半径 线长度
    private float r, l;
    //设置渐变色
    private Shader mShader, mWhiteShader;
    //起止温度 默认情况下为25 35
    private int startTem = 25,stopTem = 35;
    //中心实时温度 默认情况下为28
    private int centerTemper = 28;

    public CircleDial(Context context) {
        this(context,null);
    }

    public CircleDial(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public CircleDial(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    /**
     * 初始化
     */
    private void init() {

        linePaint = new Paint();
        linePaint.setStrokeWidth(3);
        linePaint.setAntiAlias(true);

        mTextPaint = new TextPaint();
        mTextPaint.setColor(Color.WHITE);
        mTextPaint.setStrokeWidth(4);
        mTextPaint.setAntiAlias(true);

        setAngle(startTem,stopTem);
    }

    /**
     * 根据最低温度和最高温度计算出 开始和结束角度
     * 为了简单计算 我们设定开始角度和结束角度夹角为60°
     * @param minTemp 最低温度
     * @param maxTemp 最高温度
     */
    public void setAngle(int minTemp,int maxTemp) {
        if ((minTemp>=0&&minTemp<50) && (maxTemp>=0&&maxTemp<=60)) {
            //当最低温度大于0时
            this.startAngle = minTemp*2;

            if (maxTemp >= 50) { //当温度大于50°时，末日的时候...最终角度最大值为150°
                this.stopAngle = 50*3;
            } else {
                this.stopAngle = startAngle+60;
            }

        } else if ((minTemp<0&&minTemp>-50) && (maxTemp<=0 && maxTemp>-50)) {
            //当最低温度小于0、最大温度小于或者等于0时
            if (maxTemp == 0) {
                //最高温度等于0时
                this.stopAngle = 0;
                this.startAngle = (360-60);
            } else {
                this.stopAngle = (360-Math.abs(maxTemp)*2);
                this.startAngle = stopAngle - 60;
                Logger.d(startAngle);
                Logger.d(stopAngle);
            }
        } else if ((minTemp<0&&minTemp>=-50) && (maxTemp>0&&maxTemp<=50)) {
            //当最低温度小于0，最高温度大于0时
            this.stopAngle = maxTemp*2;
            this.startAngle = (360-(60-stopAngle));
        }
        invalidate();
    }

    /**
     *  设置中心实时温度
     * @param centerTemper 实时温度
     */
    public void setCenterTemper(int centerTemper) {
        this.centerTemper = centerTemper;
        invalidate();
    }
    /**
     * 设置温度范围温度
     */
    public void setMinMaxTem(int startTem, int stopTem) {
        this.startTem = startTem;
        this.stopTem = stopTem;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.save();

        for (double angle = 0; angle <= 360d; angle += 3.0d) {
            float xStart = calculateX(r, angle);
            float xStop = calculateX(r - l, angle);

            float yStart = calculateY(r, angle);
            float yStop = calculateY(r - l, angle);

            //绘制起始角度和终止角度的着色线条
            //根据起始角度和终止角度所在位置，大致可以分为三种情况
            if (((startAngle>=0&&startAngle<180) && (stopAngle>=0&&stopAngle<180)) ||
                    ((startAngle>180&&startAngle<360) && (stopAngle>180&&stopAngle<360))){
                //当起始角度和终止角度都在左边半圆 或者都在右边半圆时
                if (angle <= stopAngle && angle >= startAngle) {
                    linePaint.setShader(mShader);
                } else
                    linePaint.setShader(mWhiteShader);
            } else if ((startAngle>180&&startAngle<=360) && (stopAngle>=0&&stopAngle<180)) {
                //当起始角度在左边半圆  终止角度在右边半圆时
                if ((angle>=0&&angle<=stopAngle) || (angle>=startAngle&&angle<=360)) {
                    linePaint.setShader(mShader);
                } else {
                    linePaint.setShader(mWhiteShader);
                }
            }

            //刻度盘大致为两种长度和宽度的线条
            if (angle == 207 || angle == 153) {
                //绘制边界位置的两条线条
                linePaint.setStrokeWidth(2);
                float xStartL = calculateX(r * 1.05f, angle);
                float xStopL = calculateX((r - l), angle);

                float yStartL = calculateY(r * 1.05f, angle);
                float yStopL = calculateY((r - l), angle);
                canvas.drawLine(xStartL, yStartL, xStopL, yStopL, linePaint);		//底部两条较长的线
            } else if (!(angle < 207 && angle > 153)) {
                //绘制其他位置的线条
                linePaint.setStrokeWidth(3);
                canvas.drawLine(xStart, yStart, xStop, yStop, linePaint);		//	画短线
            }
        }

        //绘制最低温度、最高温度、中心实时温度
        drawCenterTem(canvas);
        drawStartTem(canvas);
        drawStopTem(canvas);

        canvas.restore();
    }

    /**
     * 根据半径和角度计算x坐标
     */
    private float calculateX(float r, double angle) {
        angle = angle * ((2 * Math.PI) / 360);
        double x = r * Math.sin(angle);

        double xFinal = centerX + x;
        return (float) xFinal;
    }

    /**
     * 根据半径和角度计算y坐标
     */
    private float calculateY(float r, double angle) {
        angle = angle * ((2 * Math.PI) / 360);
        double y = r * Math.cos(angle);

        double yFinal = centerY - y;
        return (float) yFinal;
    }

    /**
     * 画中心位置温度
     *
     * @param canvas
     */
    private void drawCenterTem(Canvas canvas) {

        mTextPaint.setTextSize(r * 0.6f);
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        float textY = centerY - (mTextPaint.descent() + mTextPaint.ascent()) / 2;
        canvas.drawText(centerTemper+"°", centerX, textY, mTextPaint);
    }

    /**
     * 画起始温度
     */
    private void drawStartTem(Canvas canvas) {
        mTextPaint.setTextSize(r * 0.1f);
        canvas.drawText(startTem + "°",  calculateX(r * 1.1f, startAngle),
                calculateY(r * 1.1f, startAngle), mTextPaint);
    }

    /**
     * 画截至温度
     */
    private void drawStopTem(Canvas canvas) {
        mTextPaint.setTextSize(r * 0.1f);
        canvas.drawText(stopTem + "°", calculateX(r * 1.1f, stopAngle), calculateY(r * 1.1f, stopAngle), mTextPaint);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        int viewWidth = getWidth();
        int viewHeight = getHeight();

        centerX = viewWidth / 2f;
        centerY = viewHeight / 2f;
        r = viewWidth * 0.4f;
        l = viewWidth * 0.05f;

        //设置渐变色
        mShader = new SweepGradient(centerX, centerY, new int[] {
                Color.parseColor("#FB8B13"),
                Color.parseColor("#FB1414"),
                Color.parseColor("#1488FB"),
                Color.parseColor("#13FBE0"),
                Color.parseColor("#8BFB13"),
                Color.parseColor("#FB8B13")}, null);
        mWhiteShader = new SweepGradient(centerX, centerY, new int[] {
                Color.WHITE,
                Color.WHITE }, null);
        linePaint.setShader(mShader);

        invalidate();
    }

//    @Override
//    public void onWindowFocusChanged(boolean hasWindowFocus) {
//        int viewWidth = getWidth();
//        int viewHeight = getHeight();
//
//        centerX = viewWidth / 2f;
//        centerY = viewHeight / 2f;
//        r = viewWidth * 0.4f;
//        l = viewWidth * 0.05f;
//
//		//设置渐变色
//        mShader = new SweepGradient(centerX, centerY, new int[] {
//                        Color.parseColor("#FB8B13"),
//                        Color.parseColor("#FB1414"),
//                        Color.parseColor("#1488FB"),
//                        Color.parseColor("#13FBE0"),
//                        Color.parseColor("#8BFB13"),
//                        Color.parseColor("#FB8B13")}, null);
//        mWhiteShader = new SweepGradient(centerX, centerY, new int[] {
//                        Color.WHITE,
//                        Color.WHITE }, null);
//        linePaint.setShader(mShader);
//
//        invalidate();
//    }

}
