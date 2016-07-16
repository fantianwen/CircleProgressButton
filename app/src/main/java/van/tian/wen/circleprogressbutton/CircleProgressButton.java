/**
 * Copyright (C) 2016 fantianwen <twfan_09@hotmail.com>
 * <p/>
 * also you can see {@link https://github.com/fantianwen/CircleProgressButton}
 */
package van.tian.wen.circleprogressbutton;

import android.content.Context;
import android.content.res.TypedArray;
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
     * 圆的颜色
     */
    private int mCircleColor;

    /**
     * 进度条的颜色
     */
    private int mProgressColor;

    /**
     * 进度条的宽度
     */
    private float mProgressWidth;

    /**
     * 文字的颜色
     */
    private int mTextColor;

    /**
     * 文字的大小
     */
    private float mTextSize;

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


        TypedArray typedArray = mContext.obtainStyledAttributes(attrs, R.styleable.CircleProgressButton);
        mCircleColor = typedArray.getColor(R.styleable.CircleProgressButton_circleColor, Color.WHITE);
        mProgressColor = typedArray.getColor(R.styleable.CircleProgressButton_progressColor, Color.BLUE);
        mProgressWidth = typedArray.getDimension(R.styleable.CircleProgressButton_progressWidth, CommonUtils.dip2px(mContext, 1f));
        mTextColor = typedArray.getColor(R.styleable.CircleProgressButton_textColor, Color.BLACK);
        mTextSize = typedArray.getDimension(R.styleable.CircleProgressButton_textSize, CommonUtils.sp2px(mContext, 1f));

        typedArray.recycle();

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
                        sendEmptyMessageDelayed(PROGRESS_PLUS, TIME_INTERVAL);
                    }
                    break;
                case 1:
                    isEndOk = sweepAngle == 0;
                    if (!isEndOk) {
                        sweepAngle -= everyIntervalAngle;
                        invalidate();
                        sendEmptyMessageDelayed(PROGRESS_REDUCE, TIME_INTERVAL);
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
                    if (mCircleProcessListener != null) {
                        mCircleProcessListener.onReStart();
                    }
                    mLongPressedHandler.removeMessages(PROGRESS_REDUCE);
                }

                mLongPressedHandler.sendEmptyMessage(PROGRESS_PLUS);

                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:

                if (!isEnd) {
                    if (mCircleProcessListener != null) {
                        mCircleProcessListener.onCancel();
                    }
                    mLongPressedHandler.sendEmptyMessage(PROGRESS_REDUCE);
                }

                mLongPressedHandler.removeMessages(PROGRESS_PLUS);

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

        canvas.translate(mWidth / 2, mHeight / 2);

        //画一个圆
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(mCircleColor);
        canvas.drawCircle(0, 0, mRadius, mPaint);

        //画进度条
        mPaint.setColor(mProgressColor);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(mProgressWidth);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        RectF rectF = new RectF(-mRadius, -mRadius, mRadius, mRadius);
        canvas.drawArc(rectF, -90, sweepAngle, false, mPaint);

        //画文字
        mPaint.setColor(mTextColor);
        mPaint.setTextAlign(Paint.Align.CENTER);
        mPaint.setTextSize(mTextSize);
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
