package verendus.leshan.music.objects;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.io.Serializable;
import java.util.ArrayList;

import app.minimize.com.seek_bar_compat.SeekBarCompat;
import verendus.leshan.music.MainActivity;
import verendus.leshan.music.views.MyViewPager;

/**
 * Created by leshan on 12/23/15.
 */
public class God implements Serializable{

    MainActivity mainActivity;

    static ArrayList<Song> songs = new ArrayList<>();
    static ArrayList<Album> albums = new ArrayList<>();
    static ArrayList<Artist> artists = new ArrayList<>();
    static ArrayList<Genre> genres = new ArrayList<>();
    static ArrayList<Playlist> playlists = new ArrayList<>();
    final static int DEFAULT = 0xFF424242;

    DrawerLayout drawerLayout;
    SeekBarCompat slider;
    TextView currentTime, totalTime;
    Thread thread;
    MyViewPager nowPlayingViewPager;
    RelativeLayout preview, nowPlayingStatusBar, libraryStatusBar;

    View nowPlayingTitle, nowPlayingArtist;
    View repeatButton, previousButton, pauseButton, nextButton, shuffleButton;

    //ProgressBarDeterminate progressBarDeterminate;

    public God (MainActivity mainActivity){
        this.mainActivity = mainActivity;
    }


    public void setLibraryStatusBar(RelativeLayout libraryStatusBar) {
        this.libraryStatusBar = libraryStatusBar;
    }

    public void setNowPlayingStatusBar(RelativeLayout nowPlayingStatusBar) {
        this.nowPlayingStatusBar = nowPlayingStatusBar;
    }

    public void setLibraryStatusBarColor(int color) {

        //Make an animation for this
        libraryStatusBar.setBackgroundColor(color);
    }

    public void setNowPlayingStatusBarColor(int color) {

        //Make an animation for this
        nowPlayingStatusBar.setBackgroundColor(color);
    }

    public RelativeLayout getPreview() {
        return preview;
    }

    public void setPreview(RelativeLayout preview) {
        this.preview = preview;
    }

    public SlidingUpPanelLayout.PanelSlideListener getPanelSlideListener(){

        return new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {

                if(preview != null){
                    preview.setVisibility(View.VISIBLE);
                    preview.setAlpha(1 - (slideOffset*5));
                }

                if(nowPlayingStatusBar != null) {
                    float limit = .8f;
                    if (slideOffset < limit) {
                        nowPlayingStatusBar.setAlpha(0);
                    } else {
                        nowPlayingStatusBar.setAlpha(slideOffset);
                    }

                    if(shuffleButton != null) {

                        float x = slideOffset/1;

                        nowPlayingTitle.setScaleX(x);
                        nowPlayingTitle.setScaleY(x);

                        nowPlayingArtist.setScaleX(x);
                        nowPlayingArtist.setScaleY(x);

                        repeatButton.setScaleX(x);
                        repeatButton.setScaleY(x);

                        previousButton.setScaleX(x);
                        previousButton.setScaleY(x);

                        pauseButton.setScaleX(x);
                        pauseButton.setScaleY(x);

                        nextButton.setScaleX(x);
                        nextButton.setScaleY(x);

                        shuffleButton.setScaleX(x);
                        shuffleButton.setScaleY(x);

                        currentTime.setScaleX(x);
                        currentTime.setScaleY(x);

                        slider.setScaleX(x);
                        slider.setScaleY(x);

                        totalTime.setScaleX(x);
                        totalTime.setScaleY(x);
                    }



                }

            }

            @Override
            public void onPanelCollapsed(View panel) {
            }

            @Override
            public void onPanelExpanded(View panel) {

                if(preview != null){
                    preview.setVisibility(View.INVISIBLE);
                }

            }

            @Override
            public void onPanelAnchored(View panel) {

            }

            @Override
            public void onPanelHidden(View panel) {

            }
        };

    }

    public ViewPager getNowPlayingViewPager() {
        return nowPlayingViewPager;
    }

    public void setNowPlayingViewPager(MyViewPager nowPlayingViewPager) {
        this.nowPlayingViewPager = nowPlayingViewPager;
    }

    public TextView getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(TextView currentTime) {
        this.currentTime = currentTime;
    }

    public TextView getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(TextView totalTime) {
        this.totalTime = totalTime;
    }

    public SeekBarCompat getSlider() {
        return slider;
    }

    public void setSlider(SeekBarCompat slider) {
        slider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (mainActivity.getMusicService() != null && fromUser) {
                    //if (mainActivity.getMusicService().isPlaying()) {
                        mainActivity.getMusicService().getMediaPlayer().seekTo(progress);
                    //} else {
                       // mainActivity.getMusicService().pausePosition = progress;
                    //}
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        slider.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN :

                        nowPlayingViewPager.setPagingEnabled(false);
                        Log.d("TAG", "TOUCH_DOWN");

                        break;

                    case MotionEvent.ACTION_UP :

                        nowPlayingViewPager.setPagingEnabled(true);
                        Log.d("TAG", "TOUCH_UP");

                        break;
                }

                return false;
            }
        });

        this.slider = slider;
        startThread();
    }

    /*public ProgressBarDeterminate getProgressBarDeterminate() {
        return progressBarDeterminate;
    }

    public void setProgressBarDeterminate(ProgressBarDeterminate progressBarDeterminate) {
        this.progressBarDeterminate = progressBarDeterminate;
    }*/

    private void startThread() {

        if(thread != null){
            thread.interrupt();
        }
        thread = new Thread(new Runnable() {
            public void run() {
                while (0 == 0) {
                    mainActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            updateSlider();
                        }
                    });
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        thread.start();
    }

    int now = 0;
    int total = 10;

    private void updateSlider() {

        if (mainActivity.getMusicService() != null) {
            if (mainActivity.getMusicService().isPlaying()) {
                now = mainActivity.getMusicService().getMediaPlayer().getCurrentPosition();
                total = mainActivity.getMusicService().getMediaPlayer().getDuration();

                //slider.setMin(0);
                slider.setMax(total);
                slider.setProgress(now);

                /*progressBarDeterminate.setMin(0);
                progressBarDeterminate.setMax(total);
                progressBarDeterminate.setProgress(now);*/

                currentTime.setText(formatTime(now));
                totalTime.setText(formatTime(total));
            }
        }

    }

    public void setShuffleButton(View shuffleButton) {
        this.shuffleButton = shuffleButton;
    }

    public void setNowPlayingTitle(View nowPlayingTitle) {
        this.nowPlayingTitle = nowPlayingTitle;
    }

    public void setNowPlayingArtist(View nowPlayingArtist) {
        this.nowPlayingArtist = nowPlayingArtist;
    }

    public void setRepeatButton(View repeatButton) {
        this.repeatButton = repeatButton;
    }

    public void setPreviousButton(View previousButton) {
        this.previousButton = previousButton;
    }

    public void setPauseButton(View pauseButton) {
        this.pauseButton = pauseButton;
    }

    public void setNextButton(View nextButton) {
        this.nextButton = nextButton;
    }

    public DrawerLayout getDrawerLayout() {
        return drawerLayout;
    }

    public void setDrawerLayout(DrawerLayout drawerLayout) {
        this.drawerLayout = drawerLayout;
    }

    public static ArrayList<Song> getSongs() {
        return songs;
    }

    public static void setSongs(ArrayList<Song> songs) {
        God.songs = songs;
    }

    public static ArrayList<Album> getAlbums() {
        return albums;
    }

    public static void setAlbums(ArrayList<Album> albums) {
        God.albums = albums;
    }

    public static ArrayList<Artist> getArtists() {
        return artists;
    }

    public static void setArtists(ArrayList<Artist> artists) {
        God.artists = artists;
    }

    public static ArrayList<Genre> getGenres() {
        return genres;
    }

    public static void setGenres(ArrayList<Genre> genres) {
        God.genres = genres;
    }

    public static ArrayList<Playlist> getPlaylists() {
        return playlists;
    }

    public static void setPlaylists(ArrayList<Playlist> playlists) {
        God.playlists = playlists;
    }

    public static void overrideFonts(final View v, Typeface font) {
        try {
            if (v instanceof ViewGroup) {
                ViewGroup vg = (ViewGroup) v;
                for (int i = 0; i < vg.getChildCount(); i++) {
                    View child = vg.getChildAt(i);
                    overrideFonts(child, font);
                }
            } else if (v instanceof TextView) {
                ((TextView) v).setTypeface(font);
            }
        } catch (Exception e) {
        }
    }

    public static String formatTime(int duration) {
        String time = "0:00";
        if ((duration / 1000) % 60 < 10) {
            time = "" + duration / 1000 / 60 + ":" + "0" + (duration / 1000) % 60;
        } else {
            time = "" + duration / 1000 / 60 + ":" + (duration / 1000) % 60;
        }
        return time;
    }

    public static ImageLoader getImageLoder(Context context){
        /**DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheOnDisc(true)
                .showImageForEmptyUri(R.drawable.sample_art)
                .showImageOnFail(R.drawable.sample_art)
                //.showImageOnLoading(R.drawable.sample_art)
                .cacheOnDisk(true)
                .build();

        ImageLoaderConfiguration configuration = new ImageLoaderConfiguration.Builder(context)
                .defaultDisplayImageOptions(defaultOptions).
                        denyCacheImageMultipleSizesInMemory()
                .build();

        ImageLoader.getInstance().init(configuration);**/
        return ImageLoader.getInstance();
    }

    public static Artist getArtistFromName(String artistName) {

        for(Artist artist : artists){
            if(artist.getName().contentEquals(artistName))return artist;
        }

        return artists.get(0);
    }

    public static Album getAlbumFromName(String albumName) {

        for(Album album : albums){
            if(album.getName().contentEquals(albumName))return album;
        }

        return albums.get(0);
    }

    public static Song getSongFromName(String songName) {

        for(Song song : songs){
            if(song.getTitle().contentEquals(songName))return song;
        }

        return songs.get(0);
    }

    public static Genre getGenreFromName(String genreName) {

        for(Genre genre : genres){
            if(genre.getName().contentEquals(genreName))return genre;
        }

        return genres.get(0);
    }
}
