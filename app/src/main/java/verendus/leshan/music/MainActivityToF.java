package verendus.leshan.music;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import app.minimize.com.seek_bar_compat.SeekBarCompat;
import verendus.leshan.music.adapters.QueueListAdapter;
import verendus.leshan.music.fragments.LibraryFragment;
import verendus.leshan.music.fragments.MainFragment;
import verendus.leshan.music.objects.Album;
import verendus.leshan.music.objects.Artist;
import verendus.leshan.music.objects.Genre;
import verendus.leshan.music.objects.God;
import verendus.leshan.music.objects.Playlist;
import verendus.leshan.music.objects.Song;
import verendus.leshan.music.service.MusicChanger;
import verendus.leshan.music.service.MusicService;
import verendus.leshan.music.service.WorkerCallbacks;
import verendus.leshan.music.service.WorkerFragment;
import verendus.leshan.music.utils.XMLParser;
import verendus.leshan.music.views.MySlidingUpLayout;
import verendus.leshan.music.views.MyViewPager;
import verendus.leshan.music.views.SquaredImageView;

public class MainActivityToF extends AppCompatActivity implements WorkerCallbacks{


    private static final String MAIN_FRAGMENT_TAG = "MainFragmentTag";
    static MainFragment mainFragment;
    private static final String TAG = "MAIN_ACTIVITY";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_test);

        android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
        mainFragment = (MainFragment) fm.findFragmentByTag(MAIN_FRAGMENT_TAG);

        if (mainFragment == null) {
            FragmentTransaction fragmentTransaction = fm.beginTransaction();
            MainFragment fragment = MainFragment.newInstance();
            fragmentTransaction.add(R.id.main_layout, fragment, MAIN_FRAGMENT_TAG);
            fragmentTransaction.commit();
        }
    }

    @Override
    public void postExecute(God god) {

        mainFragment.postExecute(god);
    }

    @Override
    public void preExecute() {

        mainFragment.preExecute();
    }

    @Override
    public void onBackPressed() {

        if(mainFragment.onBackPressed())super.onBackPressed();
    }


    @Override
    protected void onDestroy() {
        Log.d(TAG, "OnDestroy called");
        super.onDestroy();
        mainFragment.onDestroy();
    }

    @Override
    protected void onStart() {
        Log.d(TAG, "OnStart called");
        super.onStart();
        mainFragment.onStart();
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "OnStop called");
        super.onStop();
        mainFragment.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "OnPause called");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "OnResume called");
    }

    public static MusicService getMusicService() {
        return mainFragment.getMusicService();
    }

    public God getGod(){
        return mainFragment.getGod();
    }

    public void setQueueAndPlaySong(ArrayList<Song> queue, int position){
        mainFragment.setQueueAndPlaySong(queue, position);
    }

    public ImageLoader getImageLoader(){
        return mainFragment.getImageLoader();
    }
}
