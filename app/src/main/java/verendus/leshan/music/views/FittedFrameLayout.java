package verendus.leshan.music.views;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

/**
 * Created by leshan on 8/5/16.
 */

public class FittedFrameLayout extends FrameLayout{
    private Rect insets = new Rect();

    public FittedFrameLayout(Context context) {
        super(context);
    }

    public FittedFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FittedFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public FittedFrameLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    protected void setChildPadding(View view, Rect insets){
        if(!(view instanceof ViewGroup))
            return;

        ViewGroup parent = (ViewGroup) view;
        if (parent instanceof FittedFrameLayout)
            ((FittedFrameLayout)parent).fitSystemWindows(insets);
        else{
            if( ViewCompat.getFitsSystemWindows(parent))
                parent.setPadding(insets.left,insets.top,insets.right,insets.bottom);
            else{
                for (int i = 0, z = parent.getChildCount(); i < z; i++)
                    setChildPadding(parent.getChildAt(i), insets);
            }
        }
    }

    @Override
    protected boolean fitSystemWindows(Rect insets) {
        this.insets = insets;
        for (int i = 0, z = getChildCount(); i < z; i++)
            setChildPadding(getChildAt(i), insets);

        return true;
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        super.addView(child, index, params);
        setChildPadding(child, insets);
    }
}
