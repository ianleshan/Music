package verendus.leshan.music.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import verendus.leshan.music.R;

/**
 * Created by leshan on 8/15/14.
 */
public class Loading extends View{

    Paint paint = new Paint();
    RectF rect = new RectF();
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        rect.set(5,5,canvas.getClipBounds().right - 5,canvas.getClipBounds().bottom - 5);
        paint.setColor(getResources().getColor(R.color.main_color));
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5);
        paint.setDither(true);
        paint.setAntiAlias(true);
        canvas.drawArc(rect, -45, 270, false, paint);
    }

    public Loading(Context context) {
        super(context);

    }

    public Loading(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public Loading(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}
