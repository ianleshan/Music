package verendus.leshan.music.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by leshan on 9/4/15.
 */
public class SquaredImageView extends ImageView{

    int sampleSize = 720/2;

    public SquaredImageView(Context context) {
        super(context);
    }

    public SquaredImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SquaredImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = getMeasuredWidth();
        setMeasuredDimension(width, width);
    }

    public void setSampleSize(int sampleSize) {
        if(sampleSize > 0)this.sampleSize = sampleSize;
    }

    /*@Override
    public void setImageBitmap(Bitmap bm) {
        if(bm != null) {
            Bitmap scaledBitmap = Bitmap.createScaledBitmap(bm, sampleSize, sampleSize, true);
            super.setImageBitmap(scaledBitmap);
        }else {
            super.setImageBitmap(bm);
        }
    }*/
}
