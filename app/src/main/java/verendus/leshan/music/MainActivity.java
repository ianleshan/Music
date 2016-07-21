package verendus.leshan.music;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.design.widget.NavigationView;
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
import android.view.MenuItem;
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
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.wnafee.vector.MorphButton;

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
import verendus.leshan.music.fragments.Equalizer;
import verendus.leshan.music.fragments.LibraryFragment;
import verendus.leshan.music.service.WorkerCallbacks;
import verendus.leshan.music.service.WorkerFragment;
import verendus.leshan.music.objects.Album;
import verendus.leshan.music.objects.Artist;
import verendus.leshan.music.objects.Genre;
import verendus.leshan.music.objects.God;
import verendus.leshan.music.objects.Playlist;
import verendus.leshan.music.objects.Song;
import verendus.leshan.music.service.MusicChanger;
import verendus.leshan.music.service.MusicService;
import verendus.leshan.music.utils.XMLParser;
import verendus.leshan.music.views.MySlidingUpLayout;
import verendus.leshan.music.views.MyViewPager;
import verendus.leshan.music.views.SquaredImageView;

public class MainActivity extends AppCompatActivity implements LibraryFragment.OnFragmentInteractionListener, MusicChanger, WorkerCallbacks {

    private static final String TAG = "MAIN_ACTIVITY";
    private static final String WORKER_FRAGMENT_TAG = "WorkerFragmentTag";

    private static God god;
    public static MusicService musicService;
    private boolean musicBound = false;

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    RecyclerView queueList;
    QueueListAdapter.OnItemClickListener queueOnItemClickListener;
    RelativeLayout level1Container, loadingScreen;
    LinearLayout queueLayoutHeader;
    static MySlidingUpLayout nowPlayingPanel, queueSlidingPanel;
    MyViewPager albumArtPager;
    ViewPager previewPager;
    TextView songTitle, songArtist, drawerHeaderTitle;
    SquaredImageView drawerHeaderImage;

    MorphButton playPause;
    ImageView next;
    ImageView previous;
    ImageView shuffleTogle;
    ImageView repeatToggle;
    ImageView previewPlayPause;
    TextView totalTime, currentTime;
    SeekBarCompat slider;
    LinearLayout nowPlayingLayout;

    RelativeLayout preview;

    public static final String[] imageQualities = new String[]{"small", "medium", "large", "extralarge", "mega"};
    public static int SMALL = 0;
    public static int MEDIUM = 1;
    public static int LARGE = 2;
    public static int EXTRA_LARGE = 3;
    public static int MEGA = 4;
    public static final String IMAGE_QUALITY = imageQualities[EXTRA_LARGE];

    static Typeface font;
    Typeface titleFont, boldFont;

    static ImageLoader imageLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);


        Log.d(TAG, "OnCreate called");
        Log.d(TAG, "Starting service");
        Intent intent = new Intent(MainActivity.this, MusicService.class);
        startService(intent);

        createImageLoader();

        font = Typeface.createFromAsset(getAssets(), "Roboto-Regular.ttf");
        titleFont = Typeface.createFromAsset(getAssets(), "titleFont.ttf");
        boldFont = Typeface.createFromAsset(getAssets(), "boldFont.ttf");

        songTitle = (TextView) findViewById(R.id.now_playing_title);
        songArtist = (TextView) findViewById(R.id.now_playing_artist);
        queueLayoutHeader = (LinearLayout) findViewById(R.id.queue_layout);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        drawerHeaderImage = (SquaredImageView) navigationView.getHeaderView(0).findViewById(R.id.drawer_header_image);
        drawerHeaderTitle = (TextView) navigationView.getHeaderView(0).findViewById(R.id.drawer_header_title);
        queueList = (RecyclerView) findViewById(R.id.queue_layout_list);
        level1Container = (RelativeLayout) findViewById(R.id.level1_container);
        loadingScreen = (RelativeLayout) findViewById(R.id.loading_screen);
        nowPlayingPanel = (MySlidingUpLayout) findViewById(R.id.sliding_layout);
        queueSlidingPanel = (MySlidingUpLayout) findViewById(R.id.queue_sliding_panel);


        playPause = (MorphButton) findViewById(R.id.play_pause);
        next = (ImageView) findViewById(R.id.next);
        previous = (ImageView) findViewById(R.id.previous);
        repeatToggle = (ImageView) findViewById(R.id.repeat_toggle);
        shuffleTogle = (ImageView) findViewById(R.id.shuffle_toggle);

        nowPlayingLayout = (LinearLayout) findViewById(R.id.now_playing_layout);
        totalTime = (TextView) findViewById(R.id.now_playing_total_time);
        currentTime = (TextView) findViewById(R.id.now_playing_curr_time);
        slider = (SeekBarCompat) findViewById(R.id.now_playing_slider);

        preview = (RelativeLayout) findViewById(R.id.now_playing_preview);
        previewPlayPause = (ImageView) findViewById(R.id.now_playing_preview_play_pause);

        God.overrideFonts(loadingScreen, font);
        God.overrideFonts(nowPlayingPanel, font);
        God.overrideFonts(queueLayoutHeader, boldFont);



        drawerHeaderTitle.setTypeface(titleFont);

        drawerHeaderImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.closeDrawers();
                nowPlayingPanel.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
            }
        });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {

                Log.d("TAG", "onNavigationItemSelected");
                switch (item.getItemId()){
                    case R.id.nav_equalizer :

                        Log.d("TAG", "Equalizer Clicked");
                        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                        Equalizer fragment = Equalizer.newInstance().newInstance();
                        fragmentTransaction.add(R.id.level1_fragment_container, fragment);
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.commit();

                        drawerLayout.closeDrawers();

                        break;
                }
                return false;
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                musicService.playNext();
            }
        });

        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                musicService.playPrevious();
            }
        });

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (musicService.isPlaying()) {
                    musicService.pauseSong();
                    //playPause.setState(MorphButton.MorphState.END, true);
                    previewPlayPause.setImageDrawable(getResources().getDrawable(R.drawable.ic_now_playing));
                } else {
                    musicService.resumeSong();
                    //playPause.setState(MorphButton.MorphState.START, true);
                    previewPlayPause.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause));
                }

            }
        };
        playPause.setOnClickListener(onClickListener);
        previewPlayPause.setOnClickListener(onClickListener);

        preview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SlidingUpPanelLayout.PanelState panelState = nowPlayingPanel.getPanelState();
                if (panelState == SlidingUpPanelLayout.PanelState.COLLAPSED) {
                    nowPlayingPanel.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
                }
            }
        });

        preview.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                SlidingUpPanelLayout.PanelState panelState = nowPlayingPanel.getPanelState();
                if (panelState == SlidingUpPanelLayout.PanelState.COLLAPSED) {
                    nowPlayingPanel.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
                    queueSlidingPanel.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
                }
                return false;
            }
        });

        int previewTemplateMeasuredHeight = preview.getMeasuredHeight();
        int queueLayoutHeaderMeasuredHeight = queueLayoutHeader.getMeasuredHeight();

        //Log.d("MEASURED HEIGHT :", queueLayoutHeaderMeasuredHeight + "");

        level1Container.setPadding(0, 0, 0, previewTemplateMeasuredHeight);

        nowPlayingPanel.setPanelHeight(previewTemplateMeasuredHeight);
        //queueSlidingPanel.setPanelHeight(queueLayoutHeaderMeasuredHeight);

        nowPlayingPanel.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        queueSlidingPanel.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);

        queueSlidingPanel.setPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                nowPlayingPanel.setSlidingEnabled(false);
            }

            @Override
            public void onPanelCollapsed(View panel) {
                nowPlayingPanel.setSlidingEnabled(true);
                if (musicService != null) {
                    queueList.scrollToPosition(0);
                    if (musicService.getPosition() >= musicService.getQueue().size()) {
                        queueList.scrollToPosition(musicService.getPosition() + 1);
                    } else {
                        queueList.scrollToPosition(musicService.getPosition() + 1);
                    }
                }
            }

            @Override
            public void onPanelExpanded(View panel) {
                nowPlayingPanel.setSlidingEnabled(false);
            }

            @Override
            public void onPanelAnchored(View panel) {

            }

            @Override
            public void onPanelHidden(View panel) {

            }
        });
        queueSlidingPanel.setScrollableView(queueList);
        queueSlidingPanel.setDragView(queueLayoutHeader);
        queueLayoutHeader.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {

                    case MotionEvent.ACTION_DOWN:

                        nowPlayingPanel.setSlidingEnabled(false);

                        break;

                    case MotionEvent.ACTION_UP:

                        if (queueSlidingPanel.getPanelState() == SlidingUpPanelLayout.PanelState.COLLAPSED) {
                            nowPlayingPanel.setSlidingEnabled(true);
                        }

                        break;

                }

                return false;
            }
        });

        queueOnItemClickListener = new QueueListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                playSongFromQueue(position);
            }
        };


        android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
        WorkerFragment workerFragment = (WorkerFragment) fm.findFragmentByTag(WORKER_FRAGMENT_TAG);

        if (workerFragment == null) {
            workerFragment = new WorkerFragment();
            fm.beginTransaction().add(workerFragment, WORKER_FRAGMENT_TAG).commit();
        } else {
            god = workerFragment.getGod();

            int height = god.getHeight();
            nowPlayingPanel.setPanelHeight(height);
            level1Container.setPadding(0, 0, 0, height);
            nowPlayingPanel.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            nowPlayingPanel.setPanelSlideListener(god.getPanelSlideListener());
            god.setNowPlayingStatusBar((RelativeLayout) findViewById(R.id.status_bar_color_2));
            god.setLibraryStatusBar((RelativeLayout) findViewById(R.id.status_bar_color_1));

            god.setCurrentTime(currentTime);
            god.setTotalTime(totalTime);
            god.setSlider(slider);
            god.setRepeatButton(repeatToggle);
            god.setPreviousButton(previous);
            god.setPauseButton(playPause);
            god.setNextButton(next);
            god.setTimeControl(findViewById(R.id.timeControl));
            god.setShuffleButton(shuffleTogle);

            god.setPreview(preview);

            loadingScreen.setVisibility(View.GONE);

            if (god.isAlreadyPlaying()) refreshUI(musicService.getPosition(), true);
        }


    }

    private void createImageLoader() {
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheOnDisk(true)
                .showImageForEmptyUri(R.drawable.sample_art)
                .showImageOnFail(R.drawable.sample_art)
                .displayer(new FadeInBitmapDisplayer(200))
                //.showImageOnLoading(R.drawable.sample_art)
                .cacheOnDisk(true)
                .build();

        ImageLoaderConfiguration configuration = new ImageLoaderConfiguration.Builder(this)
                .defaultDisplayImageOptions(defaultOptions)
                //.denyCacheImageMultipleSizesInMemory()
                .build();

        ImageLoader.getInstance().init(configuration);
        imageLoader = ImageLoader.getInstance();
    }

    public ImageLoader getImageLoader() {
        return imageLoader;
    }


    private ServiceConnection musicConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "OnServiceConnected called");
            MusicService.MusicBinder binder = (MusicService.MusicBinder) service;
            musicService = binder.getService();
            musicService.setMusicChanger(MainActivity.this);
            if (musicService.getGod() != null){
                god = musicService.getGod();
                //refreshUI(musicService.getPosition(), true);
            }
            musicBound = true;
            musicService.setGod(god);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "OnServiceDisconnected called");
            musicService = null;
            musicBound = false;
        }
    };



    public God getGod() {
        return god;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onChange(int position) {

    }

    @Override
    public void onPrevious(int position) {


    }

    @Override
    public void onNext(int position) {


    }

    private ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(final int position) {

            playSongFromQueue(position);


        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    @Override
    public void onPlay(final int position, boolean isNewQueue) {

        playPause.setState(MorphButton.MorphState.START);

        god.setAlreadyPlaying(true);

        god.setCurrentTime(currentTime);
        god.setTotalTime(totalTime);
        god.setSlider(slider);
        god.setRepeatButton(repeatToggle);
        god.setPreviousButton(previous);
        god.setPauseButton(playPause);
        god.setNextButton(next);
        god.setTimeControl(findViewById(R.id.timeControl));
        god.setShuffleButton(shuffleTogle);
        god.setPreview(preview);

        refreshUI(position, isNewQueue);
    }


    public void refreshUI(final int position, boolean isNewQueue) {
        final Song song = musicService.getQueue().get(position);
        songTitle.setText(song.getTitle());
        songArtist.setText(song.getArtist());
        drawerHeaderTitle.setText(song.getTitle());

        Target target = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                drawerHeaderImage.setImageBitmap(bitmap);

                Palette.PaletteAsyncListener paletteListener = new Palette.PaletteAsyncListener() {
                    public void onGenerated(Palette palette) {

                        if (palette.getVibrantSwatch() != null) {
                            setColorsAccordingToSwatch(palette.getVibrantSwatch());
                        } else if (palette.getMutedSwatch() != null) {
                            setColorsAccordingToSwatch(palette.getMutedSwatch());
                        } else if (palette.getLightVibrantSwatch() != null) {
                            setColorsAccordingToSwatch(palette.getLightVibrantSwatch());
                        } else if (palette.getDarkVibrantSwatch() != null) {
                            setColorsAccordingToSwatch(palette.getDarkVibrantSwatch());
                        } else if (palette.getLightMutedSwatch() != null) {
                            setColorsAccordingToSwatch(palette.getLightMutedSwatch());
                        } else if (palette.getDarkMutedSwatch() != null) {
                            setColorsAccordingToSwatch(palette.getDarkMutedSwatch());
                        }

                        if (palette.getDarkMutedSwatch() != null) {
                            setQueueColorsAccordingToSwatch(palette.getDarkMutedSwatch());
                        } else if (palette.getLightMutedSwatch() != null) {
                            setQueueColorsAccordingToSwatch(palette.getLightMutedSwatch());
                        } else if (palette.getDarkVibrantSwatch() != null) {
                            setQueueColorsAccordingToSwatch(palette.getDarkVibrantSwatch());
                        } else if (palette.getLightVibrantSwatch() != null) {
                            setQueueColorsAccordingToSwatch(palette.getLightVibrantSwatch());
                        } else if (palette.getMutedSwatch() != null) {
                            setQueueColorsAccordingToSwatch(palette.getMutedSwatch());
                        } else if (palette.getVibrantSwatch() != null) {
                            setQueueColorsAccordingToSwatch(palette.getVibrantSwatch());
                        }
                    }
                };

                if (bitmap != null && !bitmap.isRecycled()) {
                    Palette.from(bitmap).generate(paletteListener);
                }
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        };

        Picasso.with(getApplicationContext())
                .load(song.getCoverArt())
                .into(target);

        if (isNewQueue) {
            final NowPlayingViewPagerAdapter albumArtPagerAdapter = new NowPlayingViewPagerAdapter(getSupportFragmentManager());
            final PreviewPagerAdapter previewPagerAdapter  = new PreviewPagerAdapter(getSupportFragmentManager());

            albumArtPager = (MyViewPager) findViewById(R.id.now_playing_view_pager);
            previewPager = (ViewPager) findViewById(R.id.now_playing_preview_pager);

            albumArtPager.removeOnPageChangeListener(onPageChangeListener);
            previewPager.removeOnPageChangeListener(onPageChangeListener);

            if (albumArtPager != null && previewPager != null) {
                albumArtPager.setAdapter(albumArtPagerAdapter);
                albumArtPager.setCurrentItem(position);

                previewPager.setAdapter(previewPagerAdapter);
                previewPager.setCurrentItem(position);
            }
            albumArtPager.addOnPageChangeListener(onPageChangeListener);
            previewPager.addOnPageChangeListener(onPageChangeListener);

            god.setNowPlayingViewPager(albumArtPager);


            GridLayoutManager llm = new GridLayoutManager(getApplicationContext(), 1);
            queueList.setLayoutManager(llm);
            queueList.setHasFixedSize(true);
            QueueListAdapter recyclerViewAdapter = new QueueListAdapter(getApplicationContext(), musicService.getQueue(), imageLoader);
            recyclerViewAdapter.setOnItemClickListener(queueOnItemClickListener);
            queueList.setAdapter(recyclerViewAdapter);

            ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
                @Override
                public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                    return false;
                }

                @Override
                public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                    //Remove swiped item from list and notify the RecyclerView
                    musicService.removeSongFromQueue(viewHolder.getAdapterPosition());
                    queueList.getAdapter().notifyItemRemoved(viewHolder.getAdapterPosition());
                    albumArtPager.getAdapter().notifyDataSetChanged();
                    previewPager.getAdapter().notifyDataSetChanged();
                }
            };

            ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
            itemTouchHelper.attachToRecyclerView(queueList);
        } else {

            //Log.d("HERE  :::", "HERE");
            if (albumArtPager != null && previewPager != null) {
                //albumArtPager.removeOnPageChangeListener(onPageChangeListener);
                //previewPager.removeOnPageChangeListener(onPageChangeListener);
                previewPager.setCurrentItem(position, true);
                albumArtPager.setCurrentItem(position, true);
                //previewPager.addOnPageChangeListener(onPageChangeListener);
                //albumArtPager.addOnPageChangeListener(onPageChangeListener);
            }

        }
        if (queueSlidingPanel.getPanelState() == SlidingUpPanelLayout.PanelState.COLLAPSED) {
            queueList.scrollToPosition(musicService.getQueue().size() - 1);
            if (position >= musicService.getQueue().size()) {
                queueList.scrollToPosition(position);
            } else {
                queueList.scrollToPosition(position + 1);
            }
        }
    }


    public void setQueueAndPlaySong(ArrayList<Song> newQueue, int position) {

        if (musicService != null) {
            musicService.setQueue(newQueue);
            musicService.setSong(position);
            musicService.playSong();
        }


    }



    public void playSongFromQueue(int position) {
        musicService.setSong(position);
        musicService.playSong();
    }

    @Override
    public void postExecute(God result) {
        this.god = result;
        god.setDrawerLayout(drawerLayout);

        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();

        if (fragmentManager.findFragmentByTag("LIBRARY") == null) {
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            LibraryFragment fragment = LibraryFragment.newInstance();
            fragmentTransaction.add(R.id.library_fragment_container, fragment, "LIBRARY");
            fragmentTransaction.commit();
        }

        int height = preview.getMeasuredHeight();
        nowPlayingPanel.setPanelHeight(height);
        level1Container.setPadding(0, 0, 0, height);
        nowPlayingPanel.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        nowPlayingPanel.setPanelSlideListener(god.getPanelSlideListener());
        god.setHeight(height);
        god.setPreview(preview);
        god.setNowPlayingStatusBar((RelativeLayout) findViewById(R.id.status_bar_color_2));
        god.setLibraryStatusBar((RelativeLayout) findViewById(R.id.status_bar_color_1));


        //Make an animation for this
        loadingScreen.setVisibility(View.GONE);
    }

    @Override
    public void preExecute() {
        //Make an animation for this
        loadingScreen = (RelativeLayout) findViewById(R.id.loading_screen);
        loadingScreen.setVisibility(View.VISIBLE);
    }

    public void setQueueColorsAccordingToSwatch(Palette.Swatch swatch) {
        (findViewById(R.id.queue_layout_header)).setBackgroundColor(swatch.getRgb());
        ((ImageView)(findViewById(R.id.queue_layout_header_icon))).setColorFilter(swatch.getBodyTextColor(), PorterDuff.Mode.SRC_IN);
        ((ImageView)(findViewById(R.id.queue_layout_header_more))).setColorFilter(swatch.getBodyTextColor(), PorterDuff.Mode.SRC_IN);
        songTitle.setTextColor(swatch.getTitleTextColor());
        songArtist.setTextColor(swatch.getBodyTextColor());
    }

    class NowPlayingViewPagerAdapter extends FragmentStatePagerAdapter {

        @Override
        public void unregisterDataSetObserver(DataSetObserver observer) {
            if (observer != null) {
                super.unregisterDataSetObserver(observer);
            }
        }


        public NowPlayingViewPagerAdapter(android.support.v4.app.FragmentManager fm) {
            super(fm);
        }

        @Override
        public android.support.v4.app.Fragment getItem(int position) {

            AlbumArtTab fragment = new AlbumArtTab();
            Bundle bundle = new Bundle(1);
            bundle.putInt("TYPE", position);
            fragment.setArguments(bundle);
            return fragment;
        }

        @Override
        public int getCount() {
            return musicService.getQueue().size();
        }
    }

    public static class AlbumArtTab extends android.support.v4.app.Fragment {

        int position;

        ImageView albumArt;


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
            position = getArguments().getInt("TYPE");
            //Log.d("LOCATING BUG :", "Creating SongFragment number - " + position);
            View rootView = inflater.inflate(R.layout.now_playing, container, false);
            rootView.setTag(position);

            if (musicService == null) return rootView;
            final Song song = musicService.getQueue().get(position);

            albumArt = (ImageView) rootView.findViewById(R.id.now_playing_albumart);


            //if(song.getCoverArt() == null) song.getAlbumName().setCoverArt("drawable://" + R.drawable.sample_art);
            //imageLoader.displayImage(song.getCoverArt(), albumArt);

            Picasso.with(getContext())
                    .load(song.getCoverArt())
                    .into(albumArt);

            return rootView;
        }


    }


    class PreviewPagerAdapter extends FragmentStatePagerAdapter {

        @Override
        public void unregisterDataSetObserver(DataSetObserver observer) {
            if (observer != null) {
                super.unregisterDataSetObserver(observer);
            }
        }


        public PreviewPagerAdapter(android.support.v4.app.FragmentManager fm) {
            super(fm);
        }

        @Override
        public android.support.v4.app.Fragment getItem(int position) {

            PreviewTab fragment = new PreviewTab();
            Bundle bundle = new Bundle(1);
            bundle.putInt("TYPE", position);
            fragment.setArguments(bundle);
            return fragment;
        }

        @Override
        public int getCount() {
            return musicService.getQueue().size();
        }
    }

    public static class PreviewTab extends android.support.v4.app.Fragment {

        int position;
        TextView previewTitle, previewArtist;
        //ProgressBarDeterminate previewProgress;


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
            position = getArguments().getInt("TYPE");
            //Log.d("LOCATING BUG :", "Creating SongFragment number - " + position);
            View rootView = inflater.inflate(R.layout.preview_title, container, false);
            rootView.setTag(position);

            if (musicService == null) return rootView;
            final Song song = musicService.getQueue().get(position);

            previewTitle = (TextView) rootView.findViewById(R.id.now_playing_preview_title);
            previewArtist = (TextView) rootView.findViewById(R.id.now_playing_preview_artist);
            //previewProgress = (ProgressBarDeterminate) rootView.findViewById(R.id.now_playing_preview_progress);

            previewTitle.setText(song.getTitle());
            previewArtist.setText(song.getArtist());


            God.overrideFonts(rootView, font);


            return rootView;
        }


    }

    private void setColorsAccordingToSwatch(Palette.Swatch swatch) {

        nowPlayingLayout.setBackgroundColor(swatch.getRgb());
        nowPlayingLayout.setTag(swatch.getRgb());
        god.setNowPlayingStatusBarColor((int) nowPlayingLayout.getTag());
        //title.setTextColor(swatch.getTitleTextColor());
        currentTime.setTextColor(swatch.getTitleTextColor());
        totalTime.setTextColor(swatch.getTitleTextColor());


        //Log.d("COLOR", String.valueOf(swatch.getBodyTextColor()));
        //slider.setBackgroundColor(swatch.getBodyTextColor());
        //String hexColor = String.format("#%06X", (0xFFFFFF & swatch.getBodyTextColor()));
        //Log.d("HEX-COLOR", hexColor);

        int intColor = swatch.getBodyTextColor();
        int noAlphaColor = Color.argb(255, Color.red(intColor), Color.green(intColor), Color.blue(intColor));
        slider.setProgressColor(Color.TRANSPARENT);
        slider.setThumbColor(noAlphaColor);
        slider.setProgressBackgroundColor(Color.TRANSPARENT);
        slider.setAlpha(Color.alpha(intColor) / 255f);

        //Log.d("HEX-COLOR", Color.alpha(intColor) + " / 255 = " + (Color.alpha(intColor) / 255f));

        //artist.setTextColor(swatch.getBodyTextColor());

        playPause.setForegroundTintList(new ColorStateList (
                new int [] [] {
                        new int [] {android.R.attr.state_pressed},
                        new int [] {android.R.attr.state_focused},
                        new int [] {}
                },
                new int [] {
                        swatch.getTitleTextColor(),
                        swatch.getTitleTextColor(),
                        swatch.getTitleTextColor()
                }
        ));
        playPause.setForegroundTintMode(PorterDuff.Mode.SRC_IN);
        //playPause.setColorFilter(swatch.getTitleTextColor(), PorterDuff.Mode.SRC_IN);
        next.setColorFilter(swatch.getTitleTextColor(), PorterDuff.Mode.SRC_IN);
        previous.setColorFilter(swatch.getTitleTextColor(), PorterDuff.Mode.SRC_IN);
        repeatToggle.setColorFilter(swatch.getTitleTextColor(), PorterDuff.Mode.SRC_IN);
        shuffleTogle.setColorFilter(swatch.getTitleTextColor(), PorterDuff.Mode.SRC_IN);

    }

    @Override
    public void onBackPressed() {

        if (nowPlayingPanel.getPanelState().equals(SlidingUpPanelLayout.PanelState.EXPANDED)) {
            if (queueSlidingPanel.getPanelState().equals(SlidingUpPanelLayout.PanelState.EXPANDED)) {
                queueSlidingPanel.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            } else {
                nowPlayingPanel.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            }
        } else {
            super.onBackPressed();
        }
    }


    @Override
    protected void onDestroy() {
        Log.d(TAG, "OnDestroy called");
        super.onDestroy();

        if (isFinishing()) {
            //Log.d(TAG, "Activity is finishing");
            //Log.d(TAG, "Stopping service");

            if(!musicService.isPlaying()) {
                Intent stopIntent = new Intent(getApplicationContext(), MusicService.class);
                stopService(stopIntent);
            }
        }
    }

    private void bindMyService() {
        Log.d(TAG, "Bind Service called");
        if (!musicBound) {
            Log.d(TAG, "Binding Service ...");

            Intent bindIntent = new Intent(getApplicationContext(), MusicService.class);
            musicBound = bindService(bindIntent, musicConnection, Context.BIND_IMPORTANT);
        }
    }


    private void unbindMyService() {
        Log.d(TAG, "Unbind Service called");
        Log.d(TAG, "Unbinding Service ...");
        unbindService(musicConnection);
        musicBound = false;
    }


    @Override
    protected void onStart() {
        Log.d(TAG, "OnStart called");
        super.onStart();
        if (!isChangingConfigurations()) bindMyService();
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "OnStop called");
        super.onStop();
        if (!isChangingConfigurations()) unbindMyService();
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
        return musicService;
    }
}
