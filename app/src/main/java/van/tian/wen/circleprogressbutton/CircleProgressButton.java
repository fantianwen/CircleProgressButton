/**
 * Copyright (C) 2016 fantianwen <twfan_09@hotmail.com>
 * <p>
 * also you can see {@link https://github.com/fantianwen/CircleProgressButton}
 */
package van.tian.wen.circleprogressbutton;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;



/**
 * 圆形进度按钮
 */
public class CircleProgressButton extends View {

    /**
     * 进度正在增加
     */
    private final static int PROGRESS_PLUS = 0;

    /**
     * 进度正在减少
     */
    private final static int PROGRESS_REDUCE = 1;

    private Context mContext;

    private static final long TIME_INTERVAL = 1;
    private Paint mPaint;
    private int mWidth, mHeight;
    private float sweepAngle;

    /**
     * 结束标志位
     */
    private boolean isEnd;

    /**
     * 手放开之后，判断有没有回到progress为0的情况
     */
    private boolean isEndOk;

    /**
     * 总共长按的时长
     */
    private float longTouchInterval;

    /**
     * 圆弧渐变的角度增加
     */
    private int everyIntervalAngle = 5;

    /**
     * 监听进度情况
     */
    private CircleProcessListener mCircleProcessListener;

    private int mSize;

    private int mRadius;
    private float timeInterval;
    private String mText;

    public float getLongTouchInterval() {
        return longTouchInterval;
    }


    public CircleProgressButton(Context context) {
        this(context, null);
    }

    public CircleProgressButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleProgressButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        init();
    }

    public void setText(String text) {
        this.mText = text;
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = getMeasuredWidth();
        int height = getMeasuredHeight();

        mSize = (width > height ? height : width);
        mRadius = mSize / 2 - 15;

        setMeasuredDimension(mSize, mSize);

    }

    private Handler mLongPressedHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case 0:
                    isEnd = sweepAngle == 360;
                    if (isEnd) {
                        if (mCircleProcessListener != null) {
                            mCircleProcessListener.onFinished();
                        }
                        removeMessages(0);
                    } else {
                        sweepAngle += everyIntervalAngle;
                        invalidate();
                        sendEmptyMessageDelayed(0, TIME_INTERVAL);
                    }
                    break;
                case 1:
                    isEndOk = sweepAngle == 0;
                    if (!isEndOk) {
                        sweepAngle -= everyIntervalAngle;
                        invalidate();
                        sendEmptyMessageDelayed(1, TIME_INTERVAL);
                    } else {
                        if (mCircleProcessListener != null) {
                            mCircleProcessListener.onCancelOk();
                        }
                        removeMessages(1);
                    }

                    break;
                default:
                    break;
            }

        }
    };

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:

                if (!isEndOk) {
                    mCircleProcessListener.onReStart();
                    mLongPressedHandler.removeMessages(1);
                }

                mLongPressedHandler.sendEmptyMessage(0);

                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:

                if (!isEnd) {
                    mCircleProcessListener.onCancel();
                    mLongPressedHandler.sendEmptyMessage(1);
                }

                mLongPressedHandler.removeMessages(0);

                break;
        }

        return true;

    }

    private void init() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        mWidth = w;
        mHeight = h;

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // 画一个圆
        canvas.translate(mWidth / 2, mHeight / 2);

        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(Color.WHITE);
        canvas.drawCircle(0, 0, mRadius, mPaint);

        //画进度条
        mPaint.setColor(Color.parseColor("#00B2A5"));
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(25);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        RectF rectF = new RectF(-mRadius, -mRadius, mRadius, mRadius);
        canvas.drawArc(rectF, -90, sweepAngle, false, mPaint);

        //画文字
        mPaint.setColor(Color.parseColor("#FFD7000F"));
        mPaint.setTextAlign(Paint.Align.CENTER);
        mPaint.setTextSize(CommonUtils.sp2px(mContext, 16f));
        mPaint.setStrokeWidth(1f);
        mPaint.setStyle(Paint.Style.FILL);
        Rect bounds = new Rect();
        mPaint.getTextBounds(mText, 0, mText.length(), bounds);
        Paint.FontMetricsInt fontMetrics = mPaint.getFontMetricsInt();
        int baseline = -(fontMetrics.ascent + fontMetrics.descent) / 2;

        /**
         * 这里的第二个参数x是文字的中点的坐标
         */
        canvas.drawText(mText, 0, baseline, mPaint);

    }

    public void setCircleProcessListener(CircleProcessListener circleProcessListener) {
        this.mCircleProcessListener = circleProcessListener;
    }

    public interface CircleProcessListener {

        void onFinished();

        void onCancel();

        /**
         * 取消后进度转到0
         */
        void onCancelOk();

        void onReStart();
    }


}
