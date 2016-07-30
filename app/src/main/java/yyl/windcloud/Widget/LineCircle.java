package yyl.windcloud.Widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import com.orhanobut.logger.Logger;

/**
 * Created by yinyiliang on 2016/7/26 0026.
 */
public class LineCircle extends View {
    private float centerX, centerY;
    private int viewWidth, viewHeight;
    private Paint linePaint;
    private TextPaint whitePaint;
    // 有渐变颜色的旋转起止角度
    private float startAngle ,stopAngle;
    // 圆半径 线长度
    private float r, l;
    private Shader shader, shaderWhite;
    //起止温度
    private int startTem = 25,stopTem = 36;
    //中心实时温度
    private int centerTemper = 28;

    public LineCircle(Context context) {
        this(context,null);
    }

    public LineCircle(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public LineCircle(Context context, AttributeSet attrs, int defStyleAttr) {
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

        whitePaint = new TextPaint();
        whitePaint.setColor(Color.WHITE);
        whitePaint.setStrokeWidth(4);
        whitePaint.setAntiAlias(true);

        setAngle(startTem,stopTem);
    }

    /**
     * 根据最低温度和最高温度计算出 开始和结束角度
     * @param minTemp 最低温度
     * @param maxTemp 最高温度
     */
    public void setAngle(int minTemp,int maxTemp) {
        if ((minTemp>=0&&minTemp<50) && (maxTemp>=0&&maxTemp<=50)) {
            this.startAngle = minTemp*2;
            this.stopAngle = maxTemp*3;
        } else if ((minTemp<0&&minTemp>-50) && (maxTemp<0 && maxTemp>-50)) {
            this.startAngle = (360-Math.abs(minTemp)*3);
            this.stopAngle = (360-Math.abs(maxTemp)*2);
        } else if ((minTemp<0&&minTemp>=-50) && (maxTemp>=0&&maxTemp<=50)) {
            this.startAngle = (360-Math.abs(minTemp)*3);
            this.stopAngle = maxTemp*2;
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
     * 设置起始温度
     * @param startTem
     */
    public void setStartTem(int startTem){
        this.startTem = startTem;
        invalidate();
    }

    /**
     * 设置截止温度
     * @param stopTem
     */
    public void setStopTem(int stopTem){
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
                    linePaint.setShader(shader);
                } else
                    linePaint.setShader(shaderWhite);
            } else if ((startAngle>180&&startAngle<=360) && (stopAngle>=0&&stopAngle<180)) {
                //当起始角度在左边半圆  终止角度在右边半圆时
                if ((angle>=0&&angle<=stopAngle) || (angle>=startAngle&&angle<=360)) {
                    linePaint.setShader(shader);
                } else {
                    linePaint.setShader(shaderWhite);
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

        whitePaint.setTextSize(r * 0.6f);
        whitePaint.setAntiAlias(true);
        whitePaint.setTextAlign(Paint.Align.CENTER);
        float textY = centerY - (whitePaint.descent() + whitePaint.ascent()) / 2;
        canvas.drawText(centerTemper+"°", centerX, textY, whitePaint);
    }

    /**
     * 画起始温度
     */
    private void drawStartTem(Canvas canvas) {
        whitePaint.setTextSize(r * 0.1f);
        canvas.drawText(startTem + "°",  calculateX(r * 1.1f, startAngle),
                calculateY(r * 1.1f, startAngle), whitePaint);
    }

    /**
     * 画截至温度
     */
    private void drawStopTem(Canvas canvas) {
        whitePaint.setTextSize(r * 0.1f);
        canvas.drawText(stopTem + "°", calculateX(r * 1.1f, stopAngle), calculateY(r * 1.1f, stopAngle), whitePaint);
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        viewWidth = getWidth();
        viewHeight = getHeight();

        centerX = viewWidth / 2f;
        centerY = viewHeight / 2f;
        r = viewWidth * 0.4f;
        l = viewWidth * 0.05f;

		/* 设置渐变色 */
        shader = new SweepGradient(centerX, centerY,
                new int[] {Color.parseColor("#FFDAB5"), Color.parseColor("#E87400")}, null);
        shaderWhite = new SweepGradient(centerX, centerY, new int[] { Color.WHITE, Color.WHITE }, null);
        Matrix matrix = new Matrix();
        // 使用matrix改变渐变色起始位置，默认是在90度位置
        matrix.setRotate(45, centerX, centerY);
        shader.setLocalMatrix(matrix);
        linePaint.setShader(shader);

        invalidate();
        super.onWindowFocusChanged(hasWindowFocus);
    }

}
