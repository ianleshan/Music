package verendus.leshan.music.objects;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v4.graphics.ColorUtils;

import verendus.leshan.music.MainActivity;

/**
 * Created by leshan on 7/30/16.
 */

public class Options {

    public int primaryColor = Color.parseColor("#212121");
    public int detailColor = Color.parseColor("#ffb300");
    public int textColor = Color.parseColor("#ffffff");
    MainActivity mainActivity;

    public Options(MainActivity mainActivity) {
        this.mainActivity = mainActivity;

        SharedPreferences prefs = mainActivity.getSharedPreferences("options", mainActivity.MODE_PRIVATE);
        primaryColor = prefs.getInt("primaryColor", Color.parseColor("#212121"));
        detailColor = prefs.getInt("detailColor", Color.parseColor("#ffb300"));
        textColor = prefs.getInt("textColor", Color.parseColor("#ffffff"));
    }

    public int getPrimaryColor() {
        return primaryColor;
    }

    public void setPrimaryColor(int primaryColor) {
        this.primaryColor = primaryColor;
        this.textColor = getContrastColor(primaryColor);

        SharedPreferences.Editor editor = mainActivity.getSharedPreferences("options", mainActivity.MODE_PRIVATE).edit();
        editor.putInt("primaryColor", primaryColor);
        editor.putInt("textColor", textColor);
        editor.commit();
    }

    private int getContrastColor(int color) {

        if(ColorUtils.calculateContrast(Color.WHITE, color) > ColorUtils.calculateContrast(Color.BLACK, color)){
            return Color.WHITE;
        }else{
            return Color.BLACK;
        }
    }

    public int getDetailColor() {
        return detailColor;
    }

    public void setDetailColor(int detailColor) {
        this.detailColor = detailColor;

        SharedPreferences.Editor editor = mainActivity.getSharedPreferences("options", mainActivity.MODE_PRIVATE).edit();
        editor.putInt("detailColor", detailColor);
        editor.commit();
    }

    public int getTextColor() {
        return textColor;
    }
}
