package com.davistsin.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;


public class BorderProgressButton extends View {
    private Paint mProgressPaint;
    private RectF mProgressBounds;
    private LinearGradient mProgressBgGradient;
    private Paint mBorderPaint;
    private RectF mBorderRect;
    private Paint mTextPaint;
    private Rect mTextRect = new Rect();
    private LinearGradient mTextBgGradient;
    // default values
    private static final int defaultProgress = 0;
    private static final int defaultProgressColor = Color.parseColor("#6200EE");
    private static final int defaultMax = 100;
    private static final String defaultText = "button";
    private static final int defaultTextColor = Color.parseColor("#6200EE");
    private static final int defaultTextCoverColor = Color.WHITE;
    private static final float defaultTextSize = 14; // sp
    private static final int defaultBorderColor = Color.parseColor("#6200EE");
    private static final float defaultBorderWidth = 1; // dp
    private static final float defaultBorderRadius = 8; // dp
    private static final int defaultBackgroundColor = Color.parseColor("#FEDB00");
    private static final int defaultAnimationDuration = 200; // 200ms
    // variable
    private int height;
    private int width;
    private int progress;
    private int progressColor;
    private int max = 100;
    private String text;
    private int textColor;
    private int textCoverColor;
    private float textSize;
    private int borderColor;
    private float borderWidth;
    private float borderRadius;
    private int backgroundColor;
    private int animationDuration;

    private long lastProgress = defaultProgress;
    private long animationEndTime = 0;

    public BorderProgressButton(Context context) {
        this(context, null);
    }

    public BorderProgressButton(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BorderProgressButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        final TypedArray typedArray = context.obtainStyledAttributes(attrs,
                R.styleable.ProgressButton);
        progress = typedArray.getInt(R.styleable.ProgressButton_bpbProgress, defaultProgress);
        progressColor = typedArray.getColor(R.styleable.ProgressButton_bpbProgressColor, defaultProgressColor);
        max = typedArray.getInt(R.styleable.ProgressButton_bpbMax, defaultMax);
        text = typedArray.getString(R.styleable.ProgressButton_bpbText);
        if (TextUtils.isEmpty(text)) {
            text = defaultText;
        }
        textColor = typedArray.getColor(R.styleable.ProgressButton_bpbTextColor, defaultTextColor);
        textCoverColor = typedArray.getColor(R.styleable.ProgressButton_bpbTextCoverColor, defaultTextCoverColor);
        textSize = typedArray.getDimension(R.styleable.ProgressButton_bpbTextSize, dp2px(context, defaultTextSize));
        borderColor = typedArray.getColor(R.styleable.ProgressButton_bpbBorderColor, defaultBorderColor);
        borderWidth = typedArray.getDimension(R.styleable.ProgressButton_bpbBorderWidth, dp2px(context, defaultBorderWidth));
        borderRadius = typedArray.getDimension(R.styleable.ProgressButton_bpbBorderRadius, dp2px(context, defaultBorderRadius));
        backgroundColor = typedArray.getColor(R.styleable.ProgressButton_bpbBackgroundColor, defaultBackgroundColor);
        animationDuration = typedArray.getInt(R.styleable.ProgressButton_bpbAnimationDuration, defaultAnimationDuration);
        typedArray.recycle();
        // progress paint
        mProgressPaint = new Paint();
        mProgressPaint.setAntiAlias(true);
        mProgressBounds = new RectF();
        // border paint
        mBorderPaint = new Paint();
        mBorderPaint.setColor(borderColor);
        mBorderPaint.setStrokeWidth(borderWidth);
        mBorderPaint.setAntiAlias(true);
        mBorderPaint.setStyle(Paint.Style.STROKE);
        mBorderRect = new RectF();
        // text paint
        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setColor(textColor);
        mTextPaint.setTextSize(textSize);
        // make this view clickable
        this.setClickable(true);

        this.animationEndTime = System.currentTimeMillis() + animationDuration;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        // support wrap_content
        if (widthMode == MeasureSpec.AT_MOST) {
            width = 200;
        } else {
            width = MeasureSpec.getSize(widthMeasureSpec);
        }
        if (heightMode == MeasureSpec.AT_MOST) {
            height = 80;
        } else {
            height = MeasureSpec.getSize(heightMeasureSpec);
        }
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        long now = System.currentTimeMillis();
        // calculate percent value
        float percent;
        if (now < this.animationEndTime) {
            float timePercent = 1 - (this.animationEndTime - now) / (float) this.animationDuration;
            percent = ((progress - lastProgress) * timePercent + lastProgress) / max;
        } else {
            percent = divides(progress, max);
        }
        // draw percent progress
        drawProgress(canvas, percent);
        // draw border if need
        drawBorder(canvas);
        // draw text
        drawText(canvas, percent);
        // animation
        if (now < animationEndTime) {
            invalidate();
        }
    }

    private void drawProgress(Canvas canvas, float percent) {
        mProgressBgGradient = null;
        mProgressBgGradient = new LinearGradient(
                0, 0,
                width * percent, 0,
                new int[]{progressColor, backgroundColor},
                new float[]{50f, 50.001f},
                Shader.TileMode.CLAMP
        );
        mProgressPaint.setShader(mProgressBgGradient);
        // then draw progress
        mProgressBounds.left = 0;
        mProgressBounds.top = 0;
        mProgressBounds.right = width;
        mProgressBounds.bottom = height;
        canvas.drawRoundRect(mProgressBounds, borderRadius, borderRadius, mProgressPaint);
    }

    private void drawBorder(Canvas canvas) {
        if (borderWidth > 0) {
            mBorderPaint.setColor(borderColor);
            mBorderPaint.setStrokeWidth(borderWidth);
            mBorderRect.left = borderWidth / 2;
            mBorderRect.top = borderWidth / 2;
            mBorderRect.right = width - borderWidth / 2;
            mBorderRect.bottom = height - borderWidth / 2;
            canvas.drawRoundRect(mBorderRect, borderRadius, borderRadius, mBorderPaint);
        }
    }

    private void drawText(Canvas canvas, float percent) {
        mTextPaint.setColor(Color.BLACK);
        mTextPaint.setTextSize(textSize);
        float textWidth = mTextPaint.measureText(text);
        float coverWidth = width * percent;
        float textStart = (width - textWidth) / 2;

        if (coverWidth < textStart) {
            mTextPaint.setShader(null);
            mTextPaint.setColor(textColor);
        } else {
            mTextBgGradient = new LinearGradient(
                    textStart, 0,
                    coverWidth, 0,
                    new int[]{textCoverColor, textColor},
                    new float[]{50f, 50.001f},
                    Shader.TileMode.CLAMP
            );
            mTextPaint.setShader(mTextBgGradient);
        }
        mTextPaint.getTextBounds(text, 0, text.length(), mTextRect);
        int textHeight = mTextRect.height();
        canvas.drawText(text, (width - textWidth) / 2, (height + textHeight) / 2f, mTextPaint);
    }

    //
    // Support DataBinding
    //

    public void setBpbProgress(int progress) {
        this.lastProgress = this.progress;
        this.progress = progress;
        this.animationEndTime = System.currentTimeMillis() + animationDuration;
        invalidate();
    }

    public void setBpbProgressColor(int progressColor) {
        this.progressColor = progressColor;
        invalidate();
    }

    public void setBpbMax(int max) {
        this.max = max;
        invalidate();
    }

    public void setBpbText(String text) {
        this.text = text;
        invalidate();
    }

    public void setBpbTextColor(int textColor) {
        this.textColor = textColor;
        invalidate();
    }

    public void setBpbTextCoverColor(int textCoverColor) {
        this.textCoverColor = textCoverColor;
        invalidate();
    }

    public void setBpbTextSize(float textSize) {
        this.textSize = textSize;
        invalidate();
    }

    public void setBpbBorderColor(int borderColor) {
        this.borderColor = borderColor;
        invalidate();
    }

    public void setBpbBorderWidth(float borderWidth) {
        this.borderWidth = borderWidth;
        invalidate();
    }

    public void setBpbBorderRadius(float borderRadius) {
        this.borderRadius = borderRadius;
        invalidate();
    }

    public void setBpbBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
        invalidate();
    }

    public void setBpbAnimationDuration(int durationMill) {
        this.animationDuration = durationMill;
        invalidate();
    }

    private int dp2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    private float divides(int dividend, int divisor) {
        return (float) dividend / (float) divisor;
    }
}