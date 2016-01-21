package verendus.leshan.music.views;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;

/**
 * Created by leshan on 1/6/16.
 */
public class MySlidingUpLayout extends SlidingUpPanelLayout{

    private boolean pagingEnabled = true;


    public MySlidingUpLayout(Context context) {
        super(context);
    }

    public MySlidingUpLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    public void setSlidingEnabled(boolean enabled) {
        pagingEnabled = enabled;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (!pagingEnabled) {
            return false; // do not intercept
        }
        return super.onInterceptTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!pagingEnabled) {
            return false; // do not consume
        }
        return super.onTouchEvent(event);
    }
}
