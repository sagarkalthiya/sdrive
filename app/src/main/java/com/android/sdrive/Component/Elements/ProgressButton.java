package com.android.sdrive.Component.Elements;


import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.android.sdrive.R;

import java.util.ArrayList;
import java.util.List;

public class ProgressButton extends View implements View.OnClickListener {

    private static final String INSTANCE_STATE = "saved_instance";
    private static final String INSTANCE_MAX_PROGRESS = "max_progress";
    private static final String INSTANCE_HIDE_ON_FINISH = "hide_on_finish";
    private static final String INSTANCE_CURRENT_PROGRESS = "current_progress";
    private static final String INSTANCE_CURRENT_STATE = "current_state";
    private static final String INSTANCE_CANCELABLE = "cancelable";
    private static final String INSTANCE_IDLE_WIDTH = "idle_width";
    private static final String INSTANCE_IDLE_HEIGHT = "idle_height";
    private static final String INSTANCE_CANCEL_WIDTH = "cancel_width";
    private static final String INSTANCE_CANCEL_HEIGHT = "cancel_height";
    private static final String INSTANCE_FINISH_WIDTH = "finish_width";
    private static final String INSTANCE_FINISH_HEIGHT = "finish_height";
    private static final String INSTANCE_IDLE_BG_COLOR = "idle_bg_color";
    private static final String INSTANCE_FINISH_BG_COLOR = "finish_bg_color";
    private static final String INSTANCE_INDETERMINATE_BG_COLOR = "indeterminate_bg_color";
    private static final String INSTANCE_DETERMINATE_BG_COLOR = "determinate_bg_color";
    private static final String INSTANCE_PROGRESS_DETERMINATE_COLOR = "prog_det_color";
    private static final String INSTANCE_PROGRESS_INDETERMINATE_COLOR = "prog_indet_color";
    private static final String INSTANCE_PROGRESS_MARGIN = "prog_margin";

    public static final int STATE_IDLE = 1;
    public static final int STATE_INDETERMINATE = 2;
    public static final int STATE_DETERMINATE = 3;
    public static final int STATE_FINISHED = 4;

    private static final int BASE_START_ANGLE = -90;
    private static final int DEF_BG_COLOR = 0xB4000000;
    private static final boolean DEF_CANCELABLE = true;
    private static final int DEF_DETERMINATE_COLOR = Color.GREEN;
    private static final int DEF_INDETERMINATE_COLOR = Color.WHITE;
    private static final int DEF_PROGRESS_WIDTH = 8;
    private static final int DEF_PROGRESS_MARGIN = 5;
    private static final int DEF_PROGRESS_INTEDETERMINATE_WIDTH = 90;

    private Drawable mIdleIcon;
    private Drawable mCancelIcon;
    private Drawable mFinishIcon;

    private boolean mCancelable;
    private boolean mHideOnFinish;

    private int mIdleIconWidth;
    private int mIdleIconHeight;
    private int mCancelIconWidth;
    private int mCancelIconHeight;
    private int mFinishIconWidth;
    private int mFinishIconHeight;

    private int mCurrState;
    private int mMaxProgress;
    private int mCurrProgress;

    private Paint mBgPaint;
    private RectF mBgRect;

    private int mIdleBgColor;
    private int mFinishBgColor;
    private int mIndeterminateBgColor;
    private int mDeterminateBgColor;

    private Drawable mIdleBgDrawable;
    private Drawable mFinishBgDrawable;
    private Drawable mIndeterminateBgDrawable;
    private Drawable mDeterminateBgDrawable;

    private ValueAnimator mIndeterminateAnimator;
    private int mCurrIndeterminateBarPos;
    private int mProgressIndeterminateSweepAngle;

    private int mProgressDeterminateColor;
    private int mProgressIndeterminateColor;
    private int mProgressMargin;
    private Paint mProgressPaint;
    private RectF mProgressRect;

    private List<OnClickListener> mClickListeners;

    public ProgressButton(Context context) {
        this(context, null);
    }

    public ProgressButton(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        super.setOnClickListener(this);

        initIndeterminateAnimator();

        mClickListeners = new ArrayList<>();

        mBgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBgRect = new RectF();

        mProgressPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mProgressPaint.setStyle(Paint.Style.STROKE);
        mProgressPaint.setDither(true);
        mProgressPaint.setStrokeJoin(Paint.Join.ROUND);
        mProgressPaint.setStrokeCap(Paint.Cap.ROUND);
        mProgressPaint.setPathEffect(new CornerPathEffect(50f));

        mProgressRect = new RectF();

        Resources res = context.getResources();
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ProgressButton, 0, 0);

            initBackgroundDrawableFromAttribs(res, a);

            mCurrState = a.getInt(R.styleable.ProgressButton_state, STATE_IDLE);
            mCancelable = a.getBoolean(R.styleable.ProgressButton_cancelable, DEF_CANCELABLE);
            mHideOnFinish = a.getBoolean(R.styleable.ProgressButton_hideOnFinish, false);
            mProgressIndeterminateSweepAngle = a.getInteger(R.styleable.ProgressButton_progressIndeterminateSweepAngle, DEF_PROGRESS_INTEDETERMINATE_WIDTH);
            mProgressDeterminateColor = a.getColor(R.styleable.ProgressButton_progressDeterminateColor, DEF_DETERMINATE_COLOR);
            mProgressIndeterminateColor = a.getColor(R.styleable.ProgressButton_progressIndeterminateColor, DEF_INDETERMINATE_COLOR);
            mProgressPaint.setStrokeWidth(
                    a.getDimensionPixelSize(R.styleable.ProgressButton_progressWidth, DEF_PROGRESS_WIDTH)
            );
            mProgressMargin = a.getDimensionPixelSize(R.styleable.ProgressButton_progressMargin, DEF_PROGRESS_MARGIN);
            mCurrProgress = a.getInteger(R.styleable.ProgressButton_progress, 0);
            mMaxProgress = a.getInteger(R.styleable.ProgressButton_maxProgress, 100);

            int icIdleDrawableId = a.getResourceId(R.styleable.ProgressButton_idleIconDrawable, R.drawable.ic_file_download_light);
            mIdleIcon = res.getDrawable(icIdleDrawableId);
            mIdleIconWidth = a.getDimensionPixelSize(R.styleable.ProgressButton_idleIconWidth, mIdleIcon.getMinimumWidth());
            mIdleIconHeight = a.getDimensionPixelSize(R.styleable.ProgressButton_idleIconHeight, mIdleIcon.getMinimumHeight());

            int icCancelDrawableId = a.getResourceId(R.styleable.ProgressButton_cancelIconDrawable, R.drawable.ic_close_light);
            mCancelIcon = res.getDrawable(icCancelDrawableId);
            mCancelIconWidth = a.getDimensionPixelSize(R.styleable.ProgressButton_cancelIconWidth, mCancelIcon.getMinimumWidth());
            mCancelIconHeight = a.getDimensionPixelSize(R.styleable.ProgressButton_cancelIconHeight, mCancelIcon.getMinimumHeight());

            int icFinishDrawableId = a.getResourceId(R.styleable.ProgressButton_finishIconDrawable, R.drawable.ic_finish_light);
            mFinishIcon = res.getDrawable(icFinishDrawableId);
            mFinishIconWidth = a.getDimensionPixelSize(R.styleable.ProgressButton_finishIconWidth, mFinishIcon.getMinimumWidth());
            mFinishIconHeight = a.getDimensionPixelSize(R.styleable.ProgressButton_finishIconHeight, mFinishIcon.getMinimumHeight());

            a.recycle();
        } else {
            mCurrState = STATE_IDLE;
            mCancelable = DEF_CANCELABLE;
            mHideOnFinish = false;
            mProgressIndeterminateSweepAngle = DEF_PROGRESS_INTEDETERMINATE_WIDTH;
            mProgressDeterminateColor = DEF_DETERMINATE_COLOR;
            mProgressIndeterminateColor = DEF_INDETERMINATE_COLOR;
            mProgressPaint.setStrokeWidth(DEF_PROGRESS_WIDTH);
            mProgressMargin = DEF_PROGRESS_MARGIN;
            mCurrProgress = 0;
            mMaxProgress = 100;

            mIdleBgColor = DEF_BG_COLOR;
            mFinishBgColor = DEF_BG_COLOR;
            mIndeterminateBgColor = DEF_BG_COLOR;
            mDeterminateBgColor = DEF_BG_COLOR;

            mIdleIcon = res.getDrawable(R.drawable.ic_file_download_light);
            mIdleIconWidth = mIdleIcon.getMinimumWidth();
            mIdleIconHeight = mIdleIcon.getMinimumHeight();

            mCancelIcon = res.getDrawable(R.drawable.ic_close_light);
            mCancelIconWidth = mCancelIcon.getMinimumWidth();
            mCancelIconHeight = mCancelIcon.getMinimumHeight();

            mFinishIcon = res.getDrawable(R.drawable.ic_finish_light);
            mFinishIconWidth = mFinishIcon.getMinimumWidth();
            mFinishIconHeight = mFinishIcon.getMinimumHeight();
        }

        if (mCurrState == STATE_INDETERMINATE)
            setIndeterminate();
        if (mCurrState == STATE_FINISHED && mHideOnFinish)
            setVisibility(GONE);
    }

    private void initBackgroundDrawableFromAttribs(Resources res, TypedArray attrs) {
        int idleResId = attrs.getResourceId(R.styleable.ProgressButton_idleBackgroundDrawable, -1);
        int finishResId = attrs.getResourceId(R.styleable.ProgressButton_finishBackgroundDrawable, -1);
        int indeterminateResId = attrs.getResourceId(R.styleable.ProgressButton_indeterminateBackgroundDrawable, -1);
        int determinateResId = attrs.getResourceId(R.styleable.ProgressButton_determinateBackgroundDrawable, -1);

        if (idleResId != -1) mIdleBgDrawable = res.getDrawable(idleResId);
        if (finishResId != -1) mFinishBgDrawable = res.getDrawable(finishResId);
        if (indeterminateResId != -1) mIndeterminateBgDrawable = res.getDrawable(indeterminateResId);
        if (determinateResId != -1) mDeterminateBgDrawable = res.getDrawable(determinateResId);

        mIdleBgColor = attrs.getColor(R.styleable.ProgressButton_idleBackgroundColor, DEF_BG_COLOR);
        mFinishBgColor = attrs.getColor(R.styleable.ProgressButton_finishBackgroundColor, DEF_BG_COLOR);
        mIndeterminateBgColor = attrs.getColor(R.styleable.ProgressButton_indeterminateBackgroundColor, DEF_BG_COLOR);
        mDeterminateBgColor = attrs.getColor(R.styleable.ProgressButton_determinateBackgroundColor, DEF_BG_COLOR);
    }

    public boolean isHideOnFinish() {
        return mHideOnFinish;
    }

    public int getCurrState() {
        return mCurrState;
    }

    public int getMaxProgress() {
        return mMaxProgress;
    }

    public int getProgress() {
        return mCurrProgress;
    }

    public Drawable getIdleIcon() {
        return mIdleIcon;
    }

    public Drawable getCancelIcon() {
        return mCancelIcon;
    }

    public Drawable getFinishIcon() {
        return mFinishIcon;
    }

    public boolean isCancelable() {
        return mCancelable;
    }

    public int getIdleIconWidth() {
        return mIdleIconWidth;
    }

    public int getIdleIconHeight() {
        return mIdleIconHeight;
    }

    public int getCancelIconWidth() {
        return mCancelIconWidth;
    }

    public int getCancelIconHeight() {
        return mCancelIconHeight;
    }

    public int getFinishIconWidth() {
        return mFinishIconWidth;
    }

    public int getFinishIconHeight() {
        return mFinishIconHeight;
    }

    public int getIdleBgColor() {
        return mIdleBgColor;
    }

    public int getFinishBgColor() {
        return mFinishBgColor;
    }

    public int getIndeterminateBgColor() {
        return mIndeterminateBgColor;
    }

    public int getDeterminateBgColor() {
        return mDeterminateBgColor;
    }

    public Drawable getIdleBgDrawable() {
        return mIdleBgDrawable;
    }

    public Drawable getFinishBgDrawable() {
        return mFinishBgDrawable;
    }

    public Drawable getIndeterminateBgDrawable() {
        return mIndeterminateBgDrawable;
    }

    public Drawable getDeterminateBgDrawable() {
        return mDeterminateBgDrawable;
    }

    public int getProgressDeterminateColor() {
        return mProgressDeterminateColor;
    }

    public int getProgressIndeterminateColor() {
        return mProgressIndeterminateColor;
    }

    public int getProgressMargin() {
        return mProgressMargin;
    }

    public int getProgressIndeterminateSweepAngle() {
        return mProgressIndeterminateSweepAngle;
    }

    public void setProgress(int progress) {
        if (mCurrState != STATE_DETERMINATE) return;
        mCurrProgress = Math.min(progress, mMaxProgress);
        invalidate();
    }

    public void setIdle() {
        mCurrState = STATE_IDLE;
        setVisibility(VISIBLE);
        invalidate();
    }

    public void setIndeterminate() {
        mCurrIndeterminateBarPos = BASE_START_ANGLE;
        mCurrState = STATE_INDETERMINATE;
        setVisibility(VISIBLE);
        invalidate();

        mIndeterminateAnimator.start();
    }

    public void setDeterminate() {
        mIndeterminateAnimator.end();

        mCurrProgress = 0;
        mCurrState = STATE_DETERMINATE;
        setVisibility(VISIBLE);
        invalidate();
    }

    public void setFinish() {
        mCurrProgress = 0;
        mCurrState = STATE_FINISHED;
        if (mHideOnFinish)
            setVisibility(GONE);
        invalidate();
    }

    public void setHideOnFinish(boolean hide) {
        mHideOnFinish = hide;
        if (mCurrState == STATE_FINISHED) {
            if (mHideOnFinish)
                setVisibility(GONE);
            else
                setVisibility(VISIBLE);
        }
    }

    public void setIdleIcon(Drawable idleIcon) {
        mIdleIcon = idleIcon;
        invalidate();
    }

    public void setCancelIcon(Drawable cancelIcon) {
        mCancelIcon = cancelIcon;
        invalidate();
    }

    public void setFinishIcon(Drawable finishIcon) {
        mFinishIcon = finishIcon;
        invalidate();
    }

    public void setCancelable(boolean cancelable) {
        mCancelable = cancelable;
        invalidate();
    }

    public void setIdleIconWidth(int idleIconWidth) {
        mIdleIconWidth = idleIconWidth;
        invalidate();
    }

    public void setIdleIconHeight(int idleIconHeight) {
        mIdleIconHeight = idleIconHeight;
        invalidate();
    }

    public void setCancelIconWidth(int cancelIconWidth) {
        mCancelIconWidth = cancelIconWidth;
        invalidate();
    }

    public void setCancelIconHeight(int cancelIconHeight) {
        mCancelIconHeight = cancelIconHeight;
        invalidate();
    }

    public void setFinishIconWidth(int finishIconWidth) {
        mFinishIconWidth = finishIconWidth;
        invalidate();
    }

    public void setFinishIconHeight(int finishIconHeight) {
        mFinishIconHeight = finishIconHeight;
        invalidate();
    }

    public void setMaxProgress(int maxProgress) {
        mMaxProgress = maxProgress;
        invalidate();
    }

    public void setIdleBgColor(int idleBgColor) {
        mIdleBgColor = idleBgColor;
        invalidate();
    }

    public void setFinishBgColor(int finishBgColor) {
        mFinishBgColor = finishBgColor;
        invalidate();
    }

    public void setIndeterminateBgColor(int indeterminateBgColor) {
        mIndeterminateBgColor = indeterminateBgColor;
        invalidate();
    }

    public void setDeterminateBgColor(int determinateBgColor) {
        mDeterminateBgColor = determinateBgColor;
        invalidate();
    }

    public void setIdleBgDrawable(Drawable idleBgDrawable) {
        mIdleBgDrawable = idleBgDrawable;
        invalidate();
    }

    public void setFinishBgDrawable(Drawable finishBgDrawable) {
        mFinishBgDrawable = finishBgDrawable;
        invalidate();
    }

    public void setIndeterminateBgDrawable(Drawable indeterminateBgDrawable) {
        mIndeterminateBgDrawable = indeterminateBgDrawable;
        invalidate();
    }

    public void setDeterminateBgDrawable(Drawable determinateBgDrawable) {
        mDeterminateBgDrawable = determinateBgDrawable;
        invalidate();
    }

    public void setProgressDeterminateColor(int progressDeterminateColor) {
        mProgressDeterminateColor = progressDeterminateColor;
        invalidate();
    }

    public void setProgressIndeterminateColor(int progressIndeterminateColor) {
        mProgressIndeterminateColor = progressIndeterminateColor;
        invalidate();
    }

    public void setProgressMargin(int progressMargin) {
        mProgressMargin = progressMargin;
        invalidate();
    }

    public void setProgressIndeterminateSweepAngle(int progressIndeterminateSweepAngle) {
        mProgressIndeterminateSweepAngle = progressIndeterminateSweepAngle;
        invalidate();
    }

    public void addOnClickListener(OnClickListener listener) {
        if (!mClickListeners.contains(listener))
            mClickListeners.add(listener);
    }

    public void removeOnClickListener(OnClickListener listener) {
        mClickListeners.remove(listener);
    }

    @Override
    public void onClick(View v) {
        if (!mCancelable && (mCurrState == STATE_INDETERMINATE || mCurrState == STATE_DETERMINATE))
            return;

        if (mCurrState == STATE_IDLE) {
            for (OnClickListener listener : mClickListeners)
                listener.onIdleButtonClick(v);
        }
        else if (mCurrState == STATE_INDETERMINATE || mCurrState == STATE_DETERMINATE) {
            for (OnClickListener listener : mClickListeners)
                listener.onCancelButtonClick(v);
        }
        else if (mCurrState == STATE_FINISHED) {
            for (OnClickListener listener : mClickListeners)
                listener.onFinishButtonClick(v);
        }
    }

    private void drawIdleState(Canvas canvas) {
        if (mIdleBgDrawable != null) {
            mIdleBgDrawable.setBounds(0, 0, getWidth(), getHeight());
            mIdleBgDrawable.draw(canvas);
        } else {
            mBgRect.set(0, 0, getWidth(), getHeight());
            mBgPaint.setColor(mIdleBgColor);
            canvas.drawOval(mBgRect, mBgPaint);
        }

        drawDrawableInCenter(mIdleIcon, canvas, mIdleIconWidth, mIdleIconHeight);
    }

    private void drawFinishState(Canvas canvas) {
        if (mFinishBgDrawable != null) {
            mFinishBgDrawable.setBounds(0, 0, getWidth(), getHeight());
            mFinishBgDrawable.draw(canvas);
        } else {
            mBgRect.set(0, 0, getWidth(), getHeight());
            mBgPaint.setColor(mFinishBgColor);
            canvas.drawOval(mBgRect, mBgPaint);
        }

        drawDrawableInCenter(mFinishIcon, canvas, mFinishIconWidth, mFinishIconHeight);
    }

    private void drawIndeterminateState(Canvas canvas) {
        if (mIndeterminateBgDrawable != null) {
            mIndeterminateBgDrawable.setBounds(0, 0, getWidth(), getHeight());
            mIndeterminateBgDrawable.draw(canvas);
        } else {
            mBgRect.set(0, 0, getWidth(), getHeight());
            mBgPaint.setColor(mIndeterminateBgColor);
            canvas.drawOval(mBgRect, mBgPaint);
        }

        if (mCancelable) {
            drawDrawableInCenter(mCancelIcon, canvas, mCancelIconWidth, mCancelIconHeight);
        }

        setProgressRectBounds();
        mProgressPaint.setColor(mProgressIndeterminateColor);
        canvas.drawArc(mProgressRect, mCurrIndeterminateBarPos, mProgressIndeterminateSweepAngle, false, mProgressPaint);
    }

    private void drawDeterminateState(Canvas canvas) {
        if (mDeterminateBgDrawable != null) {
            mDeterminateBgDrawable.setBounds(0, 0, getWidth(), getHeight());
            mDeterminateBgDrawable.draw(canvas);
        } else {
            mBgRect.set(0, 0, getWidth(), getHeight());
            mBgPaint.setColor(mDeterminateBgColor);
            canvas.drawOval(mBgRect, mBgPaint);
        }

        if (mCancelable) {
            drawDrawableInCenter(mCancelIcon, canvas, mCancelIconWidth, mCancelIconHeight);
        }

        setProgressRectBounds();
        mProgressPaint.setColor(mProgressDeterminateColor);
        canvas.drawArc(mProgressRect, BASE_START_ANGLE, getDegrees(), false, mProgressPaint);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mCurrState == STATE_IDLE) {
            drawIdleState(canvas);
        }
        else if (mCurrState == STATE_INDETERMINATE) {
            drawIndeterminateState(canvas);
        }
        else if (mCurrState == STATE_DETERMINATE) {
            drawDeterminateState(canvas);
        }
        else if (mCurrState == STATE_FINISHED) {
            drawFinishState(canvas);
        }
    }

    @Nullable
    @Override
    protected Parcelable onSaveInstanceState() {
        final Bundle bundle = new Bundle();
        bundle.putParcelable(INSTANCE_STATE, super.onSaveInstanceState());
        bundle.putInt(INSTANCE_MAX_PROGRESS, getMaxProgress());
        bundle.putBoolean(INSTANCE_HIDE_ON_FINISH, isHideOnFinish());
        bundle.putInt(INSTANCE_CURRENT_PROGRESS, getProgress());
        bundle.putInt(INSTANCE_CURRENT_STATE, getCurrState());
        bundle.putBoolean(INSTANCE_CANCELABLE, isCancelable());
        bundle.putInt(INSTANCE_IDLE_WIDTH, getIdleIconWidth());
        bundle.putInt(INSTANCE_IDLE_HEIGHT, getIdleIconHeight());
        bundle.putInt(INSTANCE_CANCEL_WIDTH, getCancelIconWidth());
        bundle.putInt(INSTANCE_CANCEL_HEIGHT, getCancelIconHeight());
        bundle.putInt(INSTANCE_FINISH_WIDTH, getFinishIconWidth());
        bundle.putInt(INSTANCE_FINISH_HEIGHT, getFinishIconHeight());
        bundle.putInt(INSTANCE_IDLE_BG_COLOR, getIdleBgColor());
        bundle.putInt(INSTANCE_FINISH_BG_COLOR, getFinishBgColor());
        bundle.putInt(INSTANCE_INDETERMINATE_BG_COLOR, getIndeterminateBgColor());
        bundle.putInt(INSTANCE_DETERMINATE_BG_COLOR, getDeterminateBgColor());
        bundle.putInt(INSTANCE_PROGRESS_DETERMINATE_COLOR, getProgressDeterminateColor());
        bundle.putInt(INSTANCE_PROGRESS_INDETERMINATE_COLOR, getProgressIndeterminateColor());
        bundle.putInt(INSTANCE_PROGRESS_MARGIN, getProgressMargin());
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            mHideOnFinish = bundle.getBoolean(INSTANCE_HIDE_ON_FINISH);
            mMaxProgress = bundle.getInt(INSTANCE_MAX_PROGRESS);
            mCurrProgress = bundle.getInt(INSTANCE_CURRENT_PROGRESS);
            mCurrState = bundle.getInt(INSTANCE_CURRENT_STATE);
            mCancelable = bundle.getBoolean(INSTANCE_CANCELABLE);
            mIdleIconWidth = bundle.getInt(INSTANCE_IDLE_WIDTH);
            mIdleIconHeight = bundle.getInt(INSTANCE_IDLE_HEIGHT);
            mCancelIconWidth = bundle.getInt(INSTANCE_CANCEL_WIDTH);
            mCancelIconHeight = bundle.getInt(INSTANCE_CANCEL_HEIGHT);
            mFinishIconWidth = bundle.getInt(INSTANCE_FINISH_WIDTH);
            mFinishIconHeight = bundle.getInt(INSTANCE_FINISH_HEIGHT);
            mIdleBgColor = bundle.getInt(INSTANCE_IDLE_BG_COLOR);
            mFinishBgColor = bundle.getInt(INSTANCE_FINISH_BG_COLOR);
            mIndeterminateBgColor = bundle.getInt(INSTANCE_INDETERMINATE_BG_COLOR);
            mDeterminateBgColor = bundle.getInt(INSTANCE_DETERMINATE_BG_COLOR);
            mProgressDeterminateColor = bundle.getInt(INSTANCE_PROGRESS_DETERMINATE_COLOR);
            mProgressIndeterminateColor = bundle.getInt(INSTANCE_PROGRESS_INDETERMINATE_COLOR);
            mProgressMargin = bundle.getInt(INSTANCE_PROGRESS_MARGIN);
            super.onRestoreInstanceState(bundle.getParcelable(INSTANCE_STATE));

            if (mCurrState == STATE_INDETERMINATE) mIndeterminateAnimator.start();

            return;
        }
        super.onRestoreInstanceState(state);
    }

    private void setProgressRectBounds() {
        float halfStroke = mProgressPaint.getStrokeWidth() / 2.0f;
        float totalMargin = mProgressMargin + halfStroke;
        mProgressRect.set(totalMargin, totalMargin, getWidth() - totalMargin, getHeight() - totalMargin);
    }

    private void initIndeterminateAnimator() {
        mIndeterminateAnimator = ValueAnimator.ofInt(0, 360);
        mIndeterminateAnimator.setInterpolator(new LinearInterpolator());
        mIndeterminateAnimator.setDuration(1000);
        mIndeterminateAnimator.setRepeatCount(ValueAnimator.INFINITE);
        mIndeterminateAnimator.setRepeatMode(ValueAnimator.RESTART);
        mIndeterminateAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (int) animation.getAnimatedValue();
                mCurrIndeterminateBarPos = value - BASE_START_ANGLE;
                invalidate();
            }
        });
    }

    private float getDegrees() {
        return ((float)mCurrProgress / (float)mMaxProgress) * 360;
    }

    private void drawDrawableInCenter(Drawable drawable, Canvas canvas, int width, int height) {
        int left = (getWidth() / 2) - (width / 2);
        int top = (getHeight() / 2) - (height / 2);
        drawable.setBounds(left, top, left + width, top + height);
        drawable.draw(canvas);
    }

    public interface OnClickListener {
        void onIdleButtonClick(View view);
        void onCancelButtonClick(View view);
        void onFinishButtonClick(View view);
    }
}
