package verendus.leshan.music.animation;

import android.view.View;

/**
 * Created by leshan on 8/6/16.
 */

public class TransitionHelper {

    public static final int ALBUM_TRANSITION = 1;
    public static final int ARTIST_TRANSITION = 2;
    public static Transition albumTransition;
    public static Transition artistTransition;

    public static class Transition{
        int width;
        int height;
        int x;
        int y;

        public int getWidth() {
            return width;
        }

        public int getHeight() {
            return height;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }
    }

    public static Transition getTransition(int id){
        switch (id){
            case ALBUM_TRANSITION :return albumTransition;
            case ARTIST_TRANSITION :return artistTransition;
        }
        return null;
    }

    public static void setTransition(int id, View view){
        switch (id){
            case ALBUM_TRANSITION :

                albumTransition = new Transition();
                int[] location = new int[2];
                view.getLocationOnScreen(location);

                albumTransition.x = location[0];// - view.getMeasuredWidth() /2;
                albumTransition.y = location[1];// - view.getMeasuredHeight() /2;

                albumTransition.width = view.getMeasuredWidth();
                albumTransition.height = view.getMeasuredHeight();

                break;

            case ARTIST_TRANSITION :

                artistTransition = new Transition();
                int[] artistLocation = new int[2];
                view.getLocationOnScreen(artistLocation);

                artistTransition.x = artistLocation[0];// - view.getMeasuredWidth() /2;
                artistTransition.y = artistLocation[1];// - view.getMeasuredHeight() /2;

                artistTransition.width = view.getMeasuredWidth();
                artistTransition.height = view.getMeasuredHeight();

                break;

        }
    }

}
