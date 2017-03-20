package com.example.ysh.camera.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;

import com.example.ysh.camera.R;


/**
 * Created by ysh on 2017/3/17.
 */

public class CameraShutterView extends View {

    /**
     * For save and restore instance of progressbar.
     */
    private static final String INSTANCE_STATE = "saved_instance";
    private static final String INSTANCE_OUTER_RING_COLOR = "outer_ring_color";
    private static final String INSTANCE_INNER_RING_COLOR = "inner_ring_color";
    private static final String INSTANCE_INNER_CIRCLE_COLOR = "inner_circle_color";
    private static final String INSTANCE_TEXT_COLOR = "inner_text_color";
    private static final String INSTANCE_PROGRESS_FOREGROUND_COLOR = "progress_foreground_color";
    private static final String INSTANCE_PROGRESS_BACKGROUND_COLOR = "progress_background_color";
    private static final String INSTANCE_OUTER_RING_WIDTH = "inner_outer_ring_width";
    private static final String INSTANCE_SPACE_RING_WIDTH = "inner_space_ring_width";
    private static final String INSTANCE_PROGRESS_WIDTH = "inner_progress_width";
    private static final String INSTANCE_TEXT_SIZE = "inner_text_size";
    private static final String INSTANCE_RECORD_TIME = "record_time";

    public static final int TYPE_TAKE_PICTURE = 1;
    public static final int TYPE_TAKE_VIDEO = 2;
    private static final int MAX_ANGLE = 360;

    private ValueAnimator mAnimTakePicture;
    private ValueAnimator mAnimTakeVideoBegin;
    private ValueAnimator mAnimTakeVideoEnd;

    private Paint mOuterRingPaint;
    private Paint mInnerRingPaint;
    private Paint mInnerCirclePaint;
    private Paint mTextPaint;

    private int mOuterRingColor;
    private int mInnerRingColor;
    private int mInnerCircleColor;
    private int mTextColor;
    private int mProgressForegroundColor;
    private int mProgressBackgroundColor;

    private float mCenterX;
    private float mCenterY;
    private float mInnerCircleRadius;
    private float mOuterRingWidth;
    private float mSpaceRingWidth;
    private float mProgressWidth;
    private float mTextSize;

    private RectF mInnerRingRectF = new RectF(0, 0, 0, 0);
    private RectF mOuterRingRectF = new RectF(0, 0, 0, 0);

    private String mDrawText = "";
    private float mDrawTextX;
    private float mDrawTextY;

    private float mInnerRingScale;
    private float mInnerCircleScale;

    private long mRecordTime;
    private float mSweepAngle = MAX_ANGLE;
    private CountDownTimer mCountDownTimer;
    private int mShutterType = TYPE_TAKE_PICTURE;
    private boolean mIsProgress;
    private onShutterListener mListener;

    public CameraShutterView(Context context) {
        this(context, null);
    }

    public CameraShutterView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CameraShutterView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        final TypedArray attributes = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CameraShutterView,
                defStyleAttr, 0);
        mOuterRingColor = attributes.getColor(R.styleable.CameraShutterView_outerRingColor, Color.rgb(66, 145, 241));
        mInnerRingColor = attributes.getColor(R.styleable.CameraShutterView_innerRingColor, Color.rgb(66, 145, 241));
        mInnerCircleColor = attributes.getColor(R.styleable.CameraShutterView_innerCircleColor, Color.rgb(255, 255, 255));
        mTextColor = attributes.getColor(R.styleable.CameraShutterView_textColor, Color.rgb(66, 145, 241));
        mProgressForegroundColor = attributes.getColor(R.styleable.CameraShutterView_progressForegroundColor, Color.rgb(66, 145, 241));
        mProgressBackgroundColor = attributes.getColor(R.styleable.CameraShutterView_progressBackgroundColor, Color.rgb(204, 204, 204));

        mOuterRingWidth = attributes.getDimension(R.styleable.CameraShutterView_outerRingWidth, dp2px(1.5f));
        mSpaceRingWidth = attributes.getDimension(R.styleable.CameraShutterView_spaceRingWidth, dp2px(3.0f));
        mProgressWidth = attributes.getDimension(R.styleable.CameraShutterView_spaceRingWidth, dp2px(2.0f));
        mTextSize = attributes.getDimension(R.styleable.CameraShutterView_textSize, sp2px(16.0f));

        mRecordTime = attributes.getInteger(R.styleable.CameraShutterView_recordTime, 3000);
        attributes.recycle();

        initPaint();
        initAnim();
        initCountDownTimer();
    }

    private void initPaint() {
        mOuterRingPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mOuterRingPaint.setColor(mOuterRingColor);
        mOuterRingPaint.setStyle(Paint.Style.STROKE);
        mOuterRingPaint.setStrokeCap(Paint.Cap.ROUND);
        mOuterRingPaint.setStrokeWidth(mOuterRingWidth);

        mInnerRingPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mInnerRingPaint.setColor(mInnerRingColor);

        mInnerCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mInnerCirclePaint.setColor(mInnerCircleColor);

        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setColor(mTextColor);
        mTextPaint.setTextSize(mTextSize);
    }

    private void initAnim() {
        mAnimTakePicture = ValueAnimator.ofFloat(1.0f, 0.92f, 1.0f).setDuration(150);
        mAnimTakePicture.setInterpolator(new AccelerateInterpolator());
        mAnimTakePicture.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mInnerRingScale = (float) animation.getAnimatedValue();
                invalidate();
            }
        });

        mAnimTakeVideoBegin = ValueAnimator.ofFloat(0.0f, 1.0f).setDuration(300);
        mAnimTakeVideoBegin.setInterpolator(new AccelerateInterpolator());
        mAnimTakeVideoBegin.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mInnerCircleScale = (float) animation.getAnimatedValue();
                mInnerRingPaint.setAlpha((int) (255 * (1.0f - mInnerCircleScale)));
            }
        });
        mAnimTakeVideoBegin.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                mOuterRingPaint.setStrokeWidth(mProgressWidth);
                mCountDownTimer.start();
                invalidate();
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mIsProgress = true;
                mInnerRingPaint.setColor(mProgressBackgroundColor);
                mInnerRingPaint.setStyle(Paint.Style.STROKE);
                mInnerRingPaint.setStrokeWidth(mProgressWidth);
                invalidate();
            }
        });

        mAnimTakeVideoEnd = ValueAnimator.ofFloat(1.0f, 0.0f).setDuration(100);
        mAnimTakeVideoEnd.setInterpolator(new AccelerateInterpolator());
        mAnimTakeVideoEnd.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mInnerCircleScale = (float) animation.getAnimatedValue();
                mInnerRingPaint.setAlpha((int) (255 * (1.0f - mInnerCircleScale)));
                invalidate();
            }
        });
        mAnimTakeVideoEnd.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                mIsProgress = false;
                mOuterRingPaint.setStrokeWidth(mOuterRingWidth);
                mInnerRingPaint.setColor(mInnerRingColor);
                mInnerRingPaint.setStyle(Paint.Style.FILL);
                invalidate();
            }
        });
    }

    private void initCountDownTimer() {
        mCountDownTimer = new CountDownTimer(mRecordTime, 20) {

            public void onTick(long millisUntilFinished) {
                long first = millisUntilFinished % 10000 / 1000;
                long second = millisUntilFinished % 1000 / 100;
                mDrawText = first + "." + second;
                mSweepAngle = 1.0f * (mRecordTime - millisUntilFinished) / mRecordTime * MAX_ANGLE;
                invalidate();
            }

            public void onFinish() {
                mSweepAngle = MAX_ANGLE;
                invalidate();
                finishRecording();
            }
        };
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (isEnabled()) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (mShutterType == TYPE_TAKE_PICTURE) {
                        takePicture();
                    } else {
                        startRecording();
                    }
                    break;
            }
            return true;
        } else {
            return super.onTouchEvent(event);
        }
    }

    private void takePicture() {
        if (mListener != null) {
            mListener.onTakePicture();
        }
        mAnimTakePicture.start();
    }

    private void startRecording() {
        setEnabled(false);
        if (mListener != null) {
            mListener.onStartRecording();
        }
        mAnimTakeVideoBegin.start();
    }

    private void finishRecording() {
        setEnabled(true);
        if (mListener != null) {
            mListener.onFinishRecording();
        }
        mAnimTakeVideoEnd.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        calculateDrawRectF();

        canvas.drawArc(mInnerRingRectF, 270, MAX_ANGLE, false, mInnerRingPaint);
        canvas.drawArc(mOuterRingRectF, 270, mSweepAngle, false, mOuterRingPaint);
        canvas.drawCircle(mCenterX, mCenterY, mInnerCircleRadius, mInnerCirclePaint);
        if (mIsProgress) {
            canvas.drawText(mDrawText, mDrawTextX, mDrawTextY, mTextPaint);
        }
    }

    private void calculateDrawRectF() {
        float outerRingWidth = mOuterRingPaint.getStrokeWidth() / 2;
        mCenterX = getWidth() / 2;
        mCenterY = getHeight() / 2;
        mOuterRingRectF.left = getPaddingLeft() + outerRingWidth;
        mOuterRingRectF.top = getPaddingTop() + outerRingWidth;
        mOuterRingRectF.right = getWidth() - getPaddingRight() - outerRingWidth;
        mOuterRingRectF.bottom = getHeight() - getPaddingBottom() - outerRingWidth;

        if (mIsProgress) {
            mInnerRingRectF.left = mOuterRingRectF.left;
            mInnerRingRectF.top = mOuterRingRectF.top;
            mInnerRingRectF.right = mOuterRingRectF.right;
            mInnerRingRectF.bottom = mOuterRingRectF.bottom;

            float drawTextWidth = mTextPaint.measureText(mDrawText);
            Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
            mDrawTextX = mCenterX - drawTextWidth / 2;
            mDrawTextY = mCenterY - (fontMetrics.top + fontMetrics.bottom) / 2;
        } else {
            mInnerRingRectF.left = mOuterRingRectF.left + mSpaceRingWidth + outerRingWidth;
            mInnerRingRectF.top = mOuterRingRectF.top + mSpaceRingWidth + outerRingWidth;
            mInnerRingRectF.right = mOuterRingRectF.right - mSpaceRingWidth - outerRingWidth;
            mInnerRingRectF.bottom = mOuterRingRectF.bottom - mSpaceRingWidth - outerRingWidth;
        }

        if (mAnimTakePicture.isRunning()) {
            float innerScaleOffset = (mInnerRingRectF.right - mInnerRingRectF.left) * (1 - mInnerRingScale);
            mInnerRingRectF.left += innerScaleOffset;
            mInnerRingRectF.top += innerScaleOffset;
            mInnerRingRectF.right -= innerScaleOffset;
            mInnerRingRectF.bottom -= innerScaleOffset;
        }

        mInnerCircleRadius = mInnerRingRectF.width() / 2 * mInnerCircleScale - outerRingWidth;
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        final Bundle bundle = new Bundle();
        bundle.putParcelable(INSTANCE_STATE, super.onSaveInstanceState());
        bundle.putInt(INSTANCE_OUTER_RING_COLOR, getOuterRingColor());
        bundle.putInt(INSTANCE_INNER_RING_COLOR, getInnerRingColor());
        bundle.putInt(INSTANCE_INNER_CIRCLE_COLOR, getInnerCircleColor());
        bundle.putInt(INSTANCE_TEXT_COLOR, getTextColor());
        bundle.putInt(INSTANCE_PROGRESS_FOREGROUND_COLOR, getProgressForegroundColor());
        bundle.putInt(INSTANCE_PROGRESS_BACKGROUND_COLOR, getProgressBackgroundColor());

        bundle.putFloat(INSTANCE_OUTER_RING_WIDTH, getOuterRingWidth());
        bundle.putFloat(INSTANCE_SPACE_RING_WIDTH, getSpaceRingWidth());
        bundle.putFloat(INSTANCE_PROGRESS_WIDTH, getProgressWidth());
        bundle.putFloat(INSTANCE_TEXT_SIZE, getTextSize());

        bundle.putLong(INSTANCE_RECORD_TIME, getRecordTime());
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            final Bundle bundle = (Bundle) state;
            mOuterRingColor = bundle.getInt(INSTANCE_OUTER_RING_COLOR);
            mInnerRingColor = bundle.getInt(INSTANCE_INNER_RING_COLOR);
            mInnerCircleColor = bundle.getInt(INSTANCE_INNER_CIRCLE_COLOR);
            mTextColor = bundle.getInt(INSTANCE_TEXT_COLOR);
            mProgressForegroundColor = bundle.getInt(INSTANCE_PROGRESS_FOREGROUND_COLOR);
            mProgressBackgroundColor = bundle.getInt(INSTANCE_PROGRESS_BACKGROUND_COLOR);

            mOuterRingWidth = bundle.getFloat(INSTANCE_OUTER_RING_WIDTH);
            mSpaceRingWidth = bundle.getFloat(INSTANCE_SPACE_RING_WIDTH);
            mProgressWidth = bundle.getFloat(INSTANCE_PROGRESS_WIDTH);
            mTextSize = bundle.getFloat(INSTANCE_TEXT_SIZE);

            mRecordTime = bundle.getLong(INSTANCE_RECORD_TIME, 3000);
            initPaint();
            initAnim();
            initCountDownTimer();
            super.onRestoreInstanceState(bundle.getParcelable(INSTANCE_STATE));
            return;
        }
        super.onRestoreInstanceState(state);
    }

    private float dp2px(float dp) {
        final float scale = getResources().getDisplayMetrics().density;
        return dp * scale + 0.5f;
    }

    private float sp2px(float sp) {
        final float scale = getResources().getDisplayMetrics().scaledDensity;
        return sp * scale;
    }

    public int getOuterRingColor() {
        return mOuterRingColor;
    }

    public int getInnerRingColor() {
        return mInnerRingColor;
    }

    public int getInnerCircleColor() {
        return mInnerCircleColor;
    }

    public int getTextColor() {
        return mTextColor;
    }

    public int getProgressForegroundColor() {
        return mProgressForegroundColor;
    }

    public int getProgressBackgroundColor() {
        return mProgressBackgroundColor;
    }

    public float getOuterRingWidth() {
        return mOuterRingWidth;
    }

    public float getSpaceRingWidth() {
        return mSpaceRingWidth;
    }

    public float getProgressWidth() {
        return mProgressWidth;
    }

    public float getTextSize() {
        return mTextSize;
    }

    public long getRecordTime() {
        return mRecordTime;
    }

    public void setShutterType(int shutterType) {
        mShutterType = shutterType;
    }

    public void setListener(onShutterListener listener) {
        mListener = listener;
    }

    public interface onShutterListener {
        void onTakePicture();

        void onStartRecording();

        void onFinishRecording();
    }
}
