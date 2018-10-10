package ie.moses.pulseview;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.ColorInt;
import android.support.annotation.FloatRange;
import android.support.annotation.IntRange;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import static com.google.common.base.Preconditions.checkArgument;

public class PulseView extends View {

    public static final String TAG = PulseView.class.getSimpleName();

    private static final int DEFAULT_PULSE_COLOUR = Color.rgb(179, 176, 176);
    private static final int DEFAULT_DURATION = 2000;
    private static final float DEFAULT_START_ALPHA = 0.5F;
    private static final float DEFAULT_END_ALPHA = 0.0F;

    private Paint _paint;
    private ObjectAnimator _alphaAnimator;
    private boolean _isAnimating;

    private int _pulseColor = DEFAULT_PULSE_COLOUR;
    private int _duration = DEFAULT_DURATION;
    private float _startAlpha = DEFAULT_START_ALPHA;
    private float _endAlpha = DEFAULT_END_ALPHA;

    public PulseView(Context context) {
        this(context, null);
    }

    public PulseView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PulseView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private boolean _firstDraw = true;
    private RectF _rect;

    @Override
    protected void onDraw(Canvas canvas) {
        if (_firstDraw) {
            _firstDraw = false;
            _rect = new RectF(0, 0, getWidth(), getHeight());
        }

        canvas.drawRect(_rect, _paint);
    }

    public int getPulseColor() {
        return _pulseColor;
    }

    public void setPulseColor(@ColorInt int pulseColor) {
        _pulseColor = pulseColor;
    }

    public int getDuration() {
        return _duration;
    }

    public void setDuration(@IntRange(from = 0) int duration) {
        checkArgument(duration > 0,
                "invalid duration value " + duration + " attempted to be " +
                        "set for property duration, duration must be greater than 0");
        _duration = duration;
    }

    public float getStartAlpha() {
        return _startAlpha;
    }

    public void setStartAlpha(@FloatRange(from = 0.0D, to = 1.0D) float startAlpha) {
        checkArgument(startAlpha >= 0.0F && startAlpha <= 1.0F,
                "invalid alpha value " + startAlpha + " attempted to be " +
                        "set for property startAlpha, alpha must be between 0.0 and 1.0");
        _startAlpha = startAlpha;
    }

    public float getEndAlpha() {
        return _endAlpha;
    }

    public void setEndAlpha(@FloatRange(from = 0.0D, to = 1.0D) float endAlpha) {
        checkArgument(endAlpha >= 0.0F && endAlpha <= 1.0F,
                "invalid alpha value " + endAlpha + " attempted to be " +
                        "set for property endAlpha, alpha must be between 0.0 and 1.0");
        _endAlpha = endAlpha;
    }

    public void startAnimation() {
        if (!_isAnimating) {
            setVisibility(View.VISIBLE);
            _alphaAnimator.start();
            _isAnimating = true;
        } else {
            Log.d(TAG, "startAnimation() called while view is already animating");
        }
    }

    public void stopAnimation() {
        if (_isAnimating) {
            _alphaAnimator.end();
            setVisibility(View.INVISIBLE);
            _isAnimating = false;
        } else {
            Log.d(TAG, "stopAnimation() called when view has already stopped animating");
        }
    }

    private void init(Context context, @Nullable AttributeSet attrs) {
        if (isInEditMode()) {
            if (BuildConfig.DEBUG) {
                throw new IllegalStateException("could not initialise " +
                        PulseView.class.getSimpleName() + ", view is in edit mode");
            }
            return;
        }

        if (attrs != null) {
            initAttrs(context, attrs);
        }

        _paint = new Paint();
        _paint.setColor(_pulseColor);
        _paint.setAntiAlias(true);

        _alphaAnimator = ObjectAnimator.ofFloat(this, "alpha", _startAlpha, _endAlpha);
        _alphaAnimator.setRepeatCount(2);
        _alphaAnimator.setDuration(_duration);
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray attrValues = context.obtainStyledAttributes(attrs, R.styleable.PulseView);

        if (attrValues.hasValue(R.styleable.PulseView_pulseColour)) {
            int pulseColor = attrValues.getColor(R.styleable.PulseView_pulseColour, DEFAULT_PULSE_COLOUR);
            setPulseColor(pulseColor);
        }

        if (attrValues.hasValue(R.styleable.PulseView_startAlpha)) {
            float startAlpha = attrValues.getFloat(R.styleable.PulseView_startAlpha, DEFAULT_START_ALPHA);
            setStartAlpha(startAlpha);
        }

        if (attrValues.hasValue(R.styleable.PulseView_endAlpha)) {
            float endAlpha = attrValues.getFloat(R.styleable.PulseView_endAlpha, DEFAULT_END_ALPHA);
            setEndAlpha(endAlpha);
        }

        if (attrValues.hasValue(R.styleable.PulseView_duration)) {
            int duration = attrValues.getInteger(R.styleable.PulseView_duration, DEFAULT_DURATION);
            setDuration(duration);
        }

        attrValues.recycle();
    }

}
