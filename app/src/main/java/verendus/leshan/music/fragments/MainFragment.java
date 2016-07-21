package verendus.leshan.music.fragments;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
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

import java.util.ArrayList;

import app.minimize.com.seek_bar_compat.SeekBarCompat;
import verendus.leshan.music.MainActivity;
import verendus.leshan.music.R;
import verendus.leshan.music.adapters.QueueListAdapter;
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
import verendus.leshan.music.views.MySlidingUpLayout;
import verendus.leshan.music.views.MyViewPager;
import verendus.leshan.music.views.SquaredImageView;

public class MainFragment extends Fragment implements LibraryFragment.OnFragmentInteractionListener, MusicChanger, WorkerCallbacks {


    static ArrayList<Song> songs = new ArrayList<>();
    static ArrayList<Album> albums = new ArrayList<>();
    static ArrayList<Artist> artists = new ArrayList<>();
    static ArrayList<Genre> genres = new ArrayList<>();
    static ArrayList<Playlist> playlists = new ArrayList<>();

    private static final String TAG = "MAIN_ACTIVITY_FRAGMENT";
    private static final String WORKER_FRAGMENT_TAG = "WorkerFragmentTag";

    private God god;
    private static MusicService musicService;
    private boolean musicBound = false;

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    RecyclerView queueList;
    QueueListAdapter.OnItemClickListener queueOnItemClickListener;
    RelativeLayout previewTemplate, level1Container, loadingScreen;
    LinearLayout queueLayoutHeader;
    static MySlidingUpLayout nowPlayingPanel, queueSlidingPanel;
    MyViewPager viewPager;
    TextView songTitle, songArtist, drawerHeaderTitle;
    SquaredImageView drawerHeaderImage;

    public static final String[] imageQualities = new String[]{ "small", "medium", "large", "extralarge", "mega"};
    public static int SMALL = 0;
    public static int MEDIUM = 1;
    public static int LARGE = 2;
    public static int EXTRA_LARGE = 3;
    public static int MEGA = 4;
    public static final String IMAGE_QUALITY = imageQualities[EXTRA_LARGE];

    static Typeface font;
    Typeface titleFont;

    static ImageLoader imageLoader;

    MainActivity mainActivity;


    public static MainFragment newInstance() {
        MainFragment fragment = new MainFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        mainActivity = ((MainActivity)getActivity());
        mainActivity.setVolumeControlStream(AudioManager.STREAM_MUSIC);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_main_fragment, container, false);

        mainActivity.setVolumeControlStream(AudioManager.STREAM_MUSIC);


            Log.d(TAG, "OnCreate called");
            Log.d(TAG, "Starting service");
            Intent intent = new Intent(mainActivity, MusicService.class);
            mainActivity.startService(intent);

            createImageLoader();

            font = Typeface.createFromAsset(mainActivity.getAssets(), "font.ttf");
            titleFont = Typeface.createFromAsset(mainActivity.getAssets(), "titleFont.ttf");

            songTitle = (TextView) rootView.findViewById(R.id.now_playing_title);
            songArtist = (TextView) rootView.findViewById(R.id.now_playing_artist);
            queueLayoutHeader = (LinearLayout) rootView.findViewById(R.id.queue_layout);
            drawerLayout = (DrawerLayout) rootView.findViewById(R.id.drawer);
            navigationView = (NavigationView) rootView.findViewById(R.id.nav_view);
            drawerHeaderImage = (SquaredImageView) navigationView.getHeaderView(0).findViewById(R.id.drawer_header_image);
            drawerHeaderTitle = (TextView) navigationView.getHeaderView(0).findViewById(R.id.drawer_header_title);
            queueList = (RecyclerView) rootView.findViewById(R.id.queue_layout_list);
            previewTemplate = (RelativeLayout) rootView.findViewById(R.id.preview_template);
            level1Container = (RelativeLayout) rootView.findViewById(R.id.level1_container);
            loadingScreen = (RelativeLayout) rootView.findViewById(R.id.loading_screen);
            nowPlayingPanel = (MySlidingUpLayout) rootView.findViewById(R.id.sliding_layout);
            queueSlidingPanel = (MySlidingUpLayout) rootView.findViewById(R.id.queue_sliding_panel);

            God.overrideFonts(previewTemplate, font);
            God.overrideFonts(loadingScreen, font);
            God.overrideFonts(nowPlayingPanel, font);
            drawerHeaderTitle.setTypeface(titleFont);

            drawerHeaderImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    drawerLayout.closeDrawers();
                    nowPlayingPanel.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
                }
            });

            int previewTemplateMeasuredHeight = previewTemplate.getMeasuredHeight();
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
                    if(musicService != null) {
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

                    switch (event.getAction()){

                        case MotionEvent.ACTION_DOWN:

                            nowPlayingPanel.setSlidingEnabled(false);

                            break;

                        case MotionEvent.ACTION_UP:

                            if(queueSlidingPanel.getPanelState() == SlidingUpPanelLayout.PanelState.COLLAPSED){
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


            android.support.v4.app.FragmentManager fm = mainActivity.getSupportFragmentManager();
            WorkerFragment workerFragment = (WorkerFragment) fm.findFragmentByTag(WORKER_FRAGMENT_TAG);

            if (workerFragment == null) {
                workerFragment = new WorkerFragment();
                fm.beginTransaction().add(workerFragment, WORKER_FRAGMENT_TAG).commit();
            }else {
                god = workerFragment.getGod();

                int height = god.getHeight();
                nowPlayingPanel.setPanelHeight(height);
                level1Container.setPadding(0, 0, 0, height);
                nowPlayingPanel.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                nowPlayingPanel.setPanelSlideListener(god.getPanelSlideListener());
                god.setPreview(previewTemplate);
                god.setNowPlayingStatusBar((RelativeLayout) rootView.findViewById(R.id.status_bar_color_2));
                god.setLibraryStatusBar((RelativeLayout) rootView.findViewById(R.id.status_bar_color_1));
                loadingScreen.setVisibility(View.GONE);

                if(god.isAlreadyPlaying())refreshUI(musicService.getPosition(), true);
            }
        return rootView;
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

        ImageLoaderConfiguration configuration = new ImageLoaderConfiguration.Builder(mainActivity)
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
            musicService.setMusicChanger(MainFragment.this);
            if(musicService.getGod() != null)god = musicService.getGod();
            musicBound = true;
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

    ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {
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

        god.setAlreadyPlaying(true);
        refreshUI(position, isNewQueue);
    }


    public void refreshUI(final int position, boolean isNewQueue){
            Song song = musicService.getQueue().get(position);
            songTitle.setText(song.getTitle());
            songArtist.setText(song.getArtist());
            drawerHeaderTitle.setText(song.getTitle());
            imageLoader.displayImage(song.getCoverArt(), drawerHeaderImage);

            if (isNewQueue) {
                final NowPlayingViewPagerAdapter viewPagerAdapter = new NowPlayingViewPagerAdapter(mainActivity.getSupportFragmentManager());
                viewPager = (MyViewPager) mainActivity.findViewById(R.id.now_playing_view_pager);
                viewPager.removeOnPageChangeListener(onPageChangeListener);
                if (viewPager != null) {
                    viewPager.setAdapter(viewPagerAdapter);
                    viewPager.setCurrentItem(position);
                }
                viewPager.addOnPageChangeListener(onPageChangeListener);
                god.setNowPlayingViewPager(viewPager);

                GridLayoutManager llm = new GridLayoutManager(mainActivity, 1);
                queueList.setLayoutManager(llm);
                queueList.setHasFixedSize(true);
                QueueListAdapter recyclerViewAdapter = new QueueListAdapter(mainActivity, musicService.getQueue(), imageLoader);
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
                        viewPager.getAdapter().notifyDataSetChanged();
                    }
                };

                ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
                itemTouchHelper.attachToRecyclerView(queueList);
            } else {

                //Log.d("HERE  :::", "HERE");
                if (viewPager != null) {
                    viewPager.removeOnPageChangeListener(onPageChangeListener);
                    viewPager.setCurrentItem(position, true);
                    viewPager.addOnPageChangeListener(onPageChangeListener);
                }

            }
            if(queueSlidingPanel.getPanelState() == SlidingUpPanelLayout.PanelState.COLLAPSED){
                if(position >= musicService.getQueue().size()){
                    queueList.scrollToPosition(position + 1);
                }else {
                    queueList.scrollToPosition(position + 1);
                }
            }
            Thread thread = new Thread(new Runnable() {
                public void run() {

                    while (viewPager == null) {

                    }
                    while (viewPager.findViewWithTag(position) == null) {

                    }

                    final View view = viewPager.findViewWithTag(position);
                    god.setCurrentTime((TextView) view.findViewById(R.id.now_playing_curr_time));
                    god.setTotalTime((TextView) view.findViewById(R.id.now_playing_total_time));
                    //god.setProgressBarDeterminate((ProgressBarDeterminate) view.findViewById(R.id.now_playing_preview_progress));
                    god.setSlider((SeekBarCompat) view.findViewById(R.id.now_playing_slider));
                    god.setPreview((RelativeLayout) view.findViewById(R.id.now_playing_preview));

                    //god.setNowPlayingTitle(view.findViewById(R.id.now_playing_title));
                    //god.setNowPlayingArtist(view.findViewById(R.id.now_playing_artist));
                    god.setRepeatButton(view.findViewById(R.id.repeat_toggle));
                    god.setPreviousButton(view.findViewById(R.id.previous));
                    god.setPauseButton(view.findViewById(R.id.play_pause));
                    god.setNextButton(view.findViewById(R.id.next));
                    god.setTimeControl(view.findViewById(R.id.timeControl));
                    god.setShuffleButton(view.findViewById(R.id.shuffle_toggle));


                    LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.now_playing_layout);
                    while (linearLayout.getTag() == null) {

                        linearLayout = (LinearLayout) view.findViewById(R.id.now_playing_layout);

                    }


                    final LinearLayout finalLinearLayout = linearLayout;
                    mainActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(finalLinearLayout.getTag() != null)god.setNowPlayingStatusBarColor( (int) finalLinearLayout.getTag());
                        }
                    });


                /*if (position != 0) {
                    while (viewPager.findViewWithTag(position - 1) == null) {

                    }

                    view = viewPager.findViewWithTag(position);
                    final RelativeLayout previewLayout = (RelativeLayout) view.findViewById(R.id.now_playing_preview);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (god.isPanelExpanded()) {
                                previewLayout.setVisibility(View.INVISIBLE);
                            } else {
                                previewLayout.setVisibility(View.VISIBLE);
                            }
                        }
                    });

                }


                if (position != musicService.getQueue().size()) {
                    while (viewPager.findViewWithTag(position + 1) == null) {

                    }

                    view = viewPager.findViewWithTag(position);
                    final RelativeLayout previewLayout = (RelativeLayout) view.findViewById(R.id.now_playing_preview);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (god.isPanelExpanded()) {
                                previewLayout.setVisibility(View.INVISIBLE);
                            } else {
                                previewLayout.setVisibility(View.VISIBLE);
                            }
                        }
                    });

                }*/
                }
            });
            //thread.start();
    }


    public void setQueueAndPlaySong(ArrayList<Song> newQueue, int position) {

        if(musicService != null) {
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

        android.support.v4.app.FragmentManager fragmentManager = mainActivity.getSupportFragmentManager();

        if(fragmentManager.findFragmentByTag("LIBRARY") == null) {
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            LibraryFragment fragment = LibraryFragment.newInstance();
            fragmentTransaction.add(R.id.library_fragment_container, fragment, "LIBRARY");
            fragmentTransaction.commit();
        }

        int height = previewTemplate.getMeasuredHeight();
        nowPlayingPanel.setPanelHeight(height);
        level1Container.setPadding(0, 0, 0, height);
        nowPlayingPanel.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        nowPlayingPanel.setPanelSlideListener(god.getPanelSlideListener());
        god.setHeight(height);
        god.setPreview(previewTemplate);
        god.setNowPlayingStatusBar((RelativeLayout) mainActivity.findViewById(R.id.status_bar_color_2));
        god.setLibraryStatusBar((RelativeLayout) mainActivity.findViewById(R.id.status_bar_color_1));


        //Make an animation for this
        loadingScreen.setVisibility(View.GONE);
    }

    @Override
    public void preExecute() {
        //Make an animation for this
        loadingScreen = (RelativeLayout) mainActivity.findViewById(R.id.loading_screen);
        loadingScreen.setVisibility(View.VISIBLE);
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
        public Fragment getItem(int position) {

            SongFragmentTab fragment = new SongFragmentTab();
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

    public static class SongFragmentTab extends Fragment {

        int position;

        ImageView playPause, next, previous, albumArt, previewPlayPause, shuffleTogle, repeatToggle;
        RelativeLayout preview;
        LinearLayout nowPlayingLayout;
        TextView totalTime, currentTime, previewTitle, previewArtist;
        SeekBarCompat slider;
        //ProgressBarDeterminate previewProgress;


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
            position = getArguments().getInt("TYPE");
            //Log.d("LOCATING BUG :", "Creating SongFragment number - " + position);
            View rootView = inflater.inflate(R.layout.now_playing, container, false);
            rootView.setTag(position);

            if(musicService == null)return rootView;
            final Song song = musicService.getQueue().get(position);

            albumArt = (ImageView) rootView.findViewById(R.id.now_playing_albumart);
            playPause = (ImageView) rootView.findViewById(R.id.play_pause);
            next = (ImageView) rootView.findViewById(R.id.next);
            previous = (ImageView) rootView.findViewById(R.id.previous);
            repeatToggle = (ImageView) rootView.findViewById(R.id.repeat_toggle);
            shuffleTogle = (ImageView) rootView.findViewById(R.id.shuffle_toggle);
            nowPlayingLayout = (LinearLayout) rootView.findViewById(R.id.now_playing_layout);
            //title = (TextView) rootView.findViewById(R.id.now_playing_title);
            //artist = (TextView) rootView.findViewById(R.id.now_playing_artist);
            totalTime = (TextView) rootView.findViewById(R.id.now_playing_total_time);
            currentTime = (TextView) rootView.findViewById(R.id.now_playing_curr_time);
            slider = (SeekBarCompat) rootView.findViewById(R.id.now_playing_slider);

            preview = (RelativeLayout) rootView.findViewById(R.id.now_playing_preview);
            previewTitle = (TextView) rootView.findViewById(R.id.now_playing_preview_title);
            previewArtist = (TextView) rootView.findViewById(R.id.now_playing_preview_artist);
            previewPlayPause = (ImageView) rootView.findViewById(R.id.now_playing_preview_play_pause);
            //previewProgress = (ProgressBarDeterminate) rootView.findViewById(R.id.now_playing_preview_progress);

            //title.setText(song.getTitle());
            //artist.setText(song.getArtist());

            previewTitle.setText(song.getTitle());
            previewArtist.setText(song.getArtist());

            View.OnClickListener onClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (musicService.isPlaying()) {
                        musicService.pauseSong();
                        playPause.setImageDrawable(getResources().getDrawable(R.drawable.ic_now_playing));
                        previewPlayPause.setImageDrawable(getResources().getDrawable(R.drawable.ic_now_playing));
                    } else {
                        musicService.resumeSong();
                        playPause.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause));
                        previewPlayPause.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause));
                    }

                }
            };
            playPause.setOnClickListener(onClickListener);
            previewPlayPause.setOnClickListener(onClickListener);
            next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    musicService.playNext();
                }
            });
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

            previous.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    musicService.playPrevious();
                }
            });


            //if(song.getCoverArt() == null) song.getAlbumName().setCoverArt("drawable://" + R.drawable.sample_art);
            imageLoader.displayImage(song.getCoverArt(), albumArt, new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String imageUri, View view) {

                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {

                    Palette.PaletteAsyncListener paletteListener = new Palette.PaletteAsyncListener() {
                        public void onGenerated(Palette palette) {
                            //holder.cardView.setCardBackgroundColor(palette.getVibrantColor(God.DEFAULT));
                            //holder.artistName.setTextColor(Color.WHITE);
                            //nowPlayingLayout.setBackgroundColor(palette.getVibrantColor(God.DEFAULT));

                            if (palette.getVibrantSwatch() != null) {

                                setColorsAccordingToSwatch(palette.getVibrantSwatch());
                                song.setColor(palette.getVibrantSwatch().getRgb());


                            } else if (palette.getMutedSwatch() != null) {

                                setColorsAccordingToSwatch(palette.getMutedSwatch());
                                song.setColor(palette.getMutedSwatch().getRgb());


                            } else if (palette.getLightVibrantSwatch() != null) {

                                setColorsAccordingToSwatch(palette.getLightVibrantSwatch());
                                song.setColor(palette.getLightVibrantSwatch().getRgb());


                            } else if (palette.getDarkVibrantSwatch() != null) {

                                setColorsAccordingToSwatch(palette.getDarkVibrantSwatch());
                                song.setColor(palette.getDarkVibrantSwatch().getRgb());


                            } else if (palette.getLightMutedSwatch() != null) {

                                setColorsAccordingToSwatch(palette.getLightMutedSwatch());
                                song.setColor(palette.getLightMutedSwatch().getRgb());


                            } else if (palette.getDarkMutedSwatch() != null) {

                                setColorsAccordingToSwatch(palette.getDarkMutedSwatch());
                                song.setColor(palette.getDarkMutedSwatch().getRgb());


                            }
                        }
                    };

                    if (loadedImage != null && !loadedImage.isRecycled()) {
                        Palette.from(loadedImage).generate(paletteListener);
                    }
                }

                @Override
                public void onLoadingCancelled(String imageUri, View view) {

                }
            });

            God.overrideFonts(rootView, font);


            return rootView;
        }

        private void setColorsAccordingToSwatch(Palette.Swatch swatch) {

            nowPlayingLayout.setBackgroundColor(swatch.getRgb());
            nowPlayingLayout.setTag(swatch.getRgb());
            //title.setTextColor(swatch.getTitleTextColor());
            currentTime.setTextColor(swatch.getTitleTextColor());
            totalTime.setTextColor(swatch.getTitleTextColor());


            //Log.d("COLOR", String.valueOf(swatch.getBodyTextColor()));
            //slider.setBackgroundColor(swatch.getBodyTextColor());
            String hexColor = String.format("#%06X", (0xFFFFFF & swatch.getBodyTextColor()));
            //Log.d("HEX-COLOR", hexColor);

            int intColor = swatch.getBodyTextColor();
            int noAlphaColor = Color.argb(255, Color.red(intColor), Color.green(intColor), Color.blue(intColor));
            slider.setProgressColor(Color.TRANSPARENT);
            slider.setThumbColor(noAlphaColor);
            slider.setProgressBackgroundColor(Color.TRANSPARENT);
            slider.setAlpha(Color.alpha(intColor) / 255f);

            //Log.d("HEX-COLOR", Color.alpha(intColor) + " / 255 = " + (Color.alpha(intColor) / 255f));

            //artist.setTextColor(swatch.getBodyTextColor());

            playPause.setColorFilter(swatch.getTitleTextColor(), PorterDuff.Mode.SRC_IN);
            next.setColorFilter(swatch.getTitleTextColor(), PorterDuff.Mode.SRC_IN);
            previous.setColorFilter(swatch.getTitleTextColor(), PorterDuff.Mode.SRC_IN);
            repeatToggle.setColorFilter(swatch.getTitleTextColor(), PorterDuff.Mode.SRC_IN);
            shuffleTogle.setColorFilter(swatch.getTitleTextColor(), PorterDuff.Mode.SRC_IN);

        }
    }


    public boolean onBackPressed() {

        if (nowPlayingPanel.getPanelState().equals(SlidingUpPanelLayout.PanelState.EXPANDED)) {
            if (queueSlidingPanel.getPanelState().equals(SlidingUpPanelLayout.PanelState.EXPANDED)) {
                queueSlidingPanel.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            }else {
                nowPlayingPanel.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            }
            return false;
        } else {
            return true;
        }
    }


    public void onDestroy() {
        Log.d(TAG, "OnDestroy called");
        super.onDestroy();

        if(mainActivity.isFinishing()){
            Log.d(TAG, "Activity is finishing");
            Log.d(TAG, "Stopping service");
            Intent stopIntent = new Intent(mainActivity, MusicService.class);
            mainActivity.stopService(stopIntent);
        }
    }

    private void bindMyService(){
        Log.d(TAG, "Bind Service called");
        if(!musicBound){
            Log.d(TAG, "Binding Service ...");

            Intent bindIntent = new Intent(mainActivity, MusicService.class);
            musicBound = mainActivity.bindService(bindIntent, musicConnection, Context.BIND_IMPORTANT);
        }
    }


    private void unbindMyService(){
        Log.d(TAG, "Unbind Service called");
        Log.d(TAG, "Unbinding Service ...");
        mainActivity.unbindService(musicConnection);
        musicBound = false;
    }


    public void onStart() {
        Log.d(TAG, "OnStart called");
        super.onStart();
        if(!mainActivity.isChangingConfigurations())bindMyService();
    }

    public void onStop() {
        Log.d(TAG, "OnStop called");
        super.onStop();
        if(!mainActivity.isChangingConfigurations())unbindMyService();
    }

    public void onPause() {
        super.onPause();
        Log.d(TAG, "OnPause called");
    }

    public void onResume() {
        super.onResume();
        Log.d(TAG, "OnResume called");
    }

    public static MusicService getMusicService() {
        return musicService;
    }
}
