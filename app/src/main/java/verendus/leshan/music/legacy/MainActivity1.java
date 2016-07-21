package verendus.leshan.music.legacy;


public class MainActivity1 {}/*extends AppCompatActivity implements MusicChanger {

    private static final int NUM_OF_TABS = 5;
    public static final int SONGS = 0;
    public static final int ALBUMS = 1;
    public static final int ARTISTS = 2;
    private static final CharSequence[] TITLES = new CharSequence[]{"Songs", "Albums", "Artists", "Genres", "Playlists"};
    ViewPager viewPager;
    TabLayout tabLayout;
    Toolbar toolbarTitle;
    static boolean isAppBarShowing;

    private int mScrolledY;
    int mGravity = Gravity.TOP;

    //private static final int STATE_ONSCREEN = 0;
    //private static final int STATE_OFFSCREEN = 1;
    //private static final int STATE_RETURNING = 2;
    //private int mState = STATE_ONSCREEN;
    private int mMinRawY = 0;
    private int toolbarHeight;



    //QuickReturnRecyclerView songList;
    static SongListAdapter.OnItemClickListener songItemClickListener, albumSongItemClickListener;
    static AlbumListAdapter.OnItemClickListener albumItemClickListener;
    ImageView playPause, next, previous, clearCoverArt, previewPlayPause;
    SquaredImageView albumViewArt;
    RelativeLayout loadingScreen, preview, nowPlaying, dividerBar;
    RevealLinearLayout albumView;
    RelativeLayout navBar1, navBar2;
    LinearLayout rlayout;
    TextView title, artist, totalTime, currentTime, previewTitle, previewArtist;
    Loading load;
    Slider slider;
    ProgressBarDeterminate previewProgress;
    RecyclerView albumViewSongList;

    Bitmap b;

    ObjectAnimator inAnimation;
    ObjectAnimator outAnimation;
    ObjectAnimator loadingAnimation;
    ObjectAnimator loadingFinish;
    ObjectAnimator animator;
    ObjectAnimator scaleX;
    ObjectAnimator scaleY;
    static ObjectAnimator appBarEnter;
    static ObjectAnimator appBarExit;
    TimeInterpolator interpolator;
    AnimatorSet enterAnimation, exitAnimation, albumViewAnimation;

    Typeface font, titleFont;

    static ArrayList<Song> songs = new ArrayList<Song>();
    static ArrayList<Song> allSongs = new ArrayList<Song>();
    static ArrayList<Album> albums = new ArrayList<Album>();
    static ArrayList<Artist> artists = new ArrayList<Artist>();

    private MusicService musicSrv;
    private Intent playIntent;
    private boolean musicBound=false;
    boolean isShowingCoverArt = false;
    boolean isShowingAlbumVIew = false;
    boolean dontChangePreview = false;
    int position = -1, albumPosition = -1, albumSongPosition = -1;

    float primaryColorFactor = 0.3f, secondaryColorFactor = 0.5f;
    Bitmap defaultBitmap;

    static ImageLoader imageLoader;
    static LinearLayout toolbar;
    private SupportAnimator albumViewRevealAnimator, albumViewHideAnimator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        //this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_my);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);



        toolbar = (LinearLayout) findViewById(R.id.toolbar);
        toolbarTitle = (Toolbar) findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbarTitle);

        interpolator = new FastOutSlowInInterpolator();
        makeNowPlayingEnterAnimations();

        int width = getWindowManager().getDefaultDisplay().getWidth();

        defaultBitmap = BitmapFactory.decodeResource(MainActivity1.this.getApplicationContext().getResources(),
                R.drawable.sample_art);
        if(defaultBitmap.getWidth() > width) {
            defaultBitmap = Bitmap.createScaledBitmap(defaultBitmap, width, width, true);
        }
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheOnDisc(true).cacheInMemory(true)
                .showImageForEmptyUri(R.drawable.sample_art)
                .showImageOnFail(R.drawable.sample_art)
                //.showImageOnLoading(R.drawable.sample_art)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                //.displayer(new FadeInBitmapDisplayer(100))
                .build();

        ImageLoaderConfiguration configuration = new ImageLoaderConfiguration.Builder(this)
                .defaultDisplayImageOptions(defaultOptions).
                        denyCacheImageMultipleSizesInMemory()
                .build();

        ImageLoader.getInstance().init(configuration);
        imageLoader = ImageLoader.getInstance();

        //songList = (QuickReturnRecyclerView) findViewById(R.id.song_list);
        //songList.setReturningView(toolbar);
        clearCoverArt = (ImageView) findViewById(R.id.coverart_clear);
        //albumViewArt = (SquaredImageView) findViewById(R.id.album_view_art);
        playPause = (ImageView) findViewById(R.id.play_pause);
        next = (ImageView) findViewById(R.id.next);
        previous = (ImageView) findViewById(R.id.previous);
        nowPlaying = (RelativeLayout) findViewById(R.id.now_playing);
        rlayout = (LinearLayout) findViewById(R.id.rlayout);
        loadingScreen = (RelativeLayout) findViewById(R.id.loading_screen);
        albumView = (RevealLinearLayout) findViewById(R.id.album_view);
        albumViewSongList = (RecyclerView) findViewById(R.id.album_view_list);
        dividerBar = (RelativeLayout) findViewById(R.id.divider_bar);
        title = (TextView) findViewById(R.id.now_playing_title);
        artist = (TextView) findViewById(R.id.now_playing_artist);
        totalTime = (TextView) findViewById(R.id.total_time);
        currentTime = (TextView) findViewById(R.id.curr_time);
        load = (Loading) findViewById(R.id.loading_icon);
        slider = (Slider) findViewById(R.id.slider);

        preview = (RelativeLayout) findViewById(R.id.preview);
        previewTitle = (TextView) findViewById(R.id.preview_title);
        previewArtist = (TextView) findViewById(R.id.preview_artist);
        previewPlayPause = (ImageView) findViewById(R.id.preview_play_pause);
        previewProgress = (ProgressBarDeterminate) findViewById(R.id.preview_progress);


        font = Typeface.createFromAsset(getAssets(), "font.ttf");
        titleFont = Typeface.createFromAsset(getAssets(), "titleFont.ttf");

        //toolbarTitle.setTypeface(font);
        title.setTypeface(font);
        artist.setTypeface(font);
        totalTime.setTypeface(font);
        currentTime.setTypeface(font);
        previewTitle.setTypeface(font);
        previewArtist.setTypeface(font);

        albumView.setVisibility(View.GONE);

        loadingFinish = ObjectAnimator.ofFloat(loadingScreen, View.ALPHA, 0);

        loadingAnimation = ObjectAnimator.ofFloat(load, View.ROTATION, 720);
        loadingAnimation.setInterpolator(interpolator);
        loadingAnimation.setRepeatCount(ValueAnimator.INFINITE);
        loadingAnimation.setRepeatMode(ValueAnimator.RESTART);
        loadingAnimation.setDuration(1500);
        loadingAnimation.start();

        loadingScreen.setVisibility(View.VISIBLE);

        loadData();



        songItemClickListener = new SongListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                if (position == MainActivity1.this.position && songs == allSongs) {
                    inAnimation.start();
                } else {


                    dontChangePreview = true;




                    ObjectAnimator clone = inAnimation.clone();
                    clone.setDuration(0);
                    clone.start();

                    MainActivity1.this.position = position;
                    Log.d("TAG" ,songs.size() + " : BEFORE");
                    songs = allSongs;
                    Log.d("TAG", songs.size() + " : AFTER");
                    musicSrv.setQueue(songs);
                    musicSrv.setSong(position);
                    musicSrv.playSong();

                    int[] location = new int[2];
                    view.getLocationOnScreen(location);


                    ObjectAnimator moveX = ObjectAnimator.ofFloat(clearCoverArt, View.TRANSLATION_X, location[0], 0);
                    ObjectAnimator moveY = ObjectAnimator.ofFloat(clearCoverArt, View.TRANSLATION_Y, location[1], 0);


                    moveX.setInterpolator(interpolator);
                    moveY.setInterpolator(interpolator);
                    scaleX.setInterpolator(interpolator);
                    scaleY.setInterpolator(interpolator);


                    moveX.start();
                    moveY.start();
                    scaleX.start();
                    scaleY.start();

                    albumViewAnimation = new AnimatorSet();
                    albumViewAnimation.playTogether(moveX, moveY, scaleX, scaleY);


                    playPause.setImageResource(R.drawable.ic_pause);


                }
            }
        };


        albumSongItemClickListener = new SongListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                //if (position == MainActivity1.this.position) {
                //    inAnimation.start();
                //} else {


                    dontChangePreview = true;




                    ObjectAnimator clone = inAnimation.clone();
                    clone.setDuration(0);
                    clone.start();

                    MainActivity1.this.position = position;
                    songs = albums.get(albumPosition).loadData();
                    musicSrv.setQueue(songs);
                    musicSrv.setSong(position);
                    musicSrv.playSong();

                    int[] location = new int[2];
                    view.getLocationOnScreen(location);


                    ObjectAnimator moveX = ObjectAnimator.ofFloat(clearCoverArt, View.TRANSLATION_X, location[0], 0);
                    ObjectAnimator moveY = ObjectAnimator.ofFloat(clearCoverArt, View.TRANSLATION_Y, location[1], 0);


                    moveX.setInterpolator(interpolator);
                    moveY.setInterpolator(interpolator);
                    scaleX.setInterpolator(interpolator);
                    scaleY.setInterpolator(interpolator);


                    moveX.start();
                    moveY.start();
                    scaleX.start();
                    scaleY.start();

                    albumViewAnimation = new AnimatorSet();
                    albumViewAnimation.playTogether(moveX, moveY, scaleX, scaleY);


                    playPause.setImageResource(R.drawable.ic_pause);


               // }
            }
        };


        albumItemClickListener = new AlbumListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(final View view, int position) {

                isShowingAlbumVIew = true;
                albumPosition = position;

                int[] location = new int[2];
                view.getLocationOnScreen(location);

                albumView.setVisibility(View.VISIBLE);

                ObjectAnimator moveX = ObjectAnimator.ofFloat(albumViewSongList, View.TRANSLATION_X, (location[0] - view.getWidth() / 2), 0);
                ObjectAnimator moveY = ObjectAnimator.ofFloat(albumViewSongList, View.TRANSLATION_Y, (location[1] - view.getWidth() / 2), 0);
                ObjectAnimator scaleX = ObjectAnimator.ofFloat(albumViewSongList, View.SCALE_X, .5f, 1);
                ObjectAnimator scaleY = ObjectAnimator.ofFloat(albumViewSongList, View.SCALE_Y, .5f, 1);


                moveX.setInterpolator(interpolator);
                moveY.setInterpolator(interpolator);
                scaleX.setInterpolator(interpolator);
                scaleY.setInterpolator(interpolator);

                int duration = 300;

                moveX.setDuration(duration);
                moveY.setDuration(duration);
                scaleX.setDuration(duration);
                scaleY.setDuration(duration);


                //moveX.start();
                //moveY.start();
                //scaleX.start();
                //scaleY.start();


                makeAlbumViewAnimations((location[0] - view.getWidth() / 2), (location[0] - view.getWidth() / 2));

                albumViewRevealAnimator.start();

                GridLayoutManager llm = new GridLayoutManager(getApplicationContext(), 1);
                albumViewSongList.setLayoutManager(llm);
                albumViewSongList.setHasFixedSize(true);
                SongListAdapter recyclerViewAdapter = new SongListAdapter(getApplicationContext(), albums.get(position).loadData(), imageLoader);
                *//**//*recyclerViewAdapter.setOnItemClickListener(albumSongItemClickListener);
                albumViewSongList.setAdapter(recyclerViewAdapter);
                //albumViewSongList.setPadding(0, albumViewArt.getHeight(), 0, 0);


            }
        };

        albumView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        nowPlaying.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        clearCoverArt.setPivotX(0);
        clearCoverArt.setPivotY(0);

        Resources r = getResources();
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 84, r.getDisplayMetrics());

        final int[] rlayoutLocation = new int[2];


        scaleX = ObjectAnimator.ofFloat(clearCoverArt, View.SCALE_X, (px / width), 1);
        scaleY = ObjectAnimator.ofFloat(clearCoverArt, View.SCALE_Y, (px / width), 1);

        scaleX.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                rlayout.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {

                *//**rlayout.getLocationOnScreen(rlayoutLocation);
                animator = ObjectAnimator.ofFloat(rlayout, View.TRANSLATION_Y, -rlayoutLocation[1], 0);
                animator.setInterpolator(interpolator);
                animator.setDuration(400);
                animator.start();**//*
                rlayout.setVisibility(View.VISIBLE);

                // get the center for the clipping circle
                int cx = (rlayout.getLeft() + rlayout.getRight()) / 2;
                int cy = (rlayout.getTop());// + rlayout.getBottom()) / 2;

                // get the final radius for the clipping circle
                int finalRadius = Math.max(rlayout.getWidth(), rlayout.getHeight());

                SupportAnimator animator =
                        ViewAnimationUtils.createCircularReveal(rlayout, cx, cy, 0, finalRadius);
                animator.setInterpolator((Interpolator) interpolator);
                animator.setDuration(600);
                animator.start();

                exitAnimation();
                enterAnimation();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });


        navBar1 = (RelativeLayout) findViewById(R.id.nav_bar_1);
        navBar2 = (RelativeLayout) findViewById(R.id.nav_bar_2);

        int navBarHeight = 0;
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Resources resources = getResources();
            int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
            if (resourceId > 0) {
                navBarHeight = resources.getDimensionPixelSize(resourceId);
            }
        }

        Log.d("CLASS", navBar1.getLayoutParams().getClass().toString());

        //RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) navBar1.getLayoutParams();
        //params.height = navBarHeight;
        //navBar1.setLayoutParams(params);

        //LinearLayout.LayoutParams params2 = (LinearLayout.LayoutParams) navBar2.getLayoutParams();
        //params2.height = navBarHeight;
        //navBar2.setLayoutParams(params2);



        inAnimation = ObjectAnimator.ofFloat(nowPlaying, View.TRANSLATION_Y, 0);
        inAnimation.setDuration(400);
        outAnimation = ObjectAnimator.ofFloat(nowPlaying, View.TRANSLATION_Y, getWindowManager().getDefaultDisplay().getHeight() + navBarHeight);
        outAnimation.setDuration(400);


        //inAnimation = ObjectAnimator.ofFloat(nowPlaying, View.ALPHA, 1f);
        //outAnimation = ObjectAnimator.ofFloat(nowPlaying, View.ALPHA, 0f);


        inAnimation.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                nowPlaying.setVisibility(View.VISIBLE);
                isShowingCoverArt = true;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if(!dontChangePreview)updatePreview();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        outAnimation.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

                isShowingCoverArt = false;
                if(!(songs.size() <= 0))updatePreview();
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                nowPlaying.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        inAnimation.setInterpolator(interpolator);
        outAnimation.setInterpolator(interpolator);
        outAnimation.start();
        //nowPlaying.setVisibility(View.GONE);

        preview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inAnimation.start();
            }
        });

        playPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(musicSrv.isPlaying()){
                    musicSrv.pauseSong();
                    playPause.setImageResource(R.drawable.ic_now_playing);
                    previewPlayPause.setImageResource(R.drawable.ic_now_playing);
                }else{
                    musicSrv.resumeSong();
                    playPause.setImageResource(R.drawable.ic_pause);
                    previewPlayPause.setImageResource(R.drawable.ic_pause);
                }
            }
        });

        previewPlayPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(musicSrv.isPlaying()){
                    musicSrv.pauseSong();
                    playPause.setImageResource(R.drawable.ic_now_playing);
                    previewPlayPause.setImageResource(R.drawable.ic_now_playing);
                }else{
                    musicSrv.resumeSong();
                    playPause.setImageResource(R.drawable.ic_pause);
                    previewPlayPause.setImageResource(R.drawable.ic_pause);
                }
            }
        });

        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                musicSrv.playPrevious();
                MainActivity1.this.position = musicSrv.getPosition();


            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                musicSrv.playNext();
                MainActivity1.this.position = musicSrv.getPosition();


            }
        });


        slider.setOnValueChangedListener(new Slider.OnValueChangedListener() {
            @Override
            public void onValueChanged(int i) {
                if (musicSrv != null) {
                    if (musicSrv.isPlaying()) {
                        musicSrv.getMediaPlayer().seekTo(i);
                    } else {
                        musicSrv.pausePosition = i;
                    }
                }
            }
        });

        Thread th = new Thread(new Runnable() {
            public void run() {
                while (0 == 0) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            updateSlider();
                        }
                    });
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        th.start();
 *//**       MaterialRippleLayout.on(preview)
                .rippleAlpha(0.2f)
                .rippleColor(0xFF585858)
                .rippleOverlay(true)
                .rippleDuration(200)
                .rippleDelayClick(true)
                .create();
*//**
        MaterialRippleLayout.on(playPause)
                .rippleAlpha(0.2f)
                .rippleColor(0xFF585858)
                .rippleOverlay(true)
                .rippleDuration(200)
                .rippleDelayClick(true)
                .create();

        MaterialRippleLayout.on(next)
                .rippleAlpha(0.2f)
                .rippleColor(0xFF585858)
                .rippleOverlay(true)
                .rippleDuration(200)
                .rippleDelayClick(true)
                .create();

        MaterialRippleLayout.on(previous)
                .rippleAlpha(0.2f)
                .rippleColor(0xFF585858)
                .rippleOverlay(true)
                .rippleDuration(200)
                .rippleDelayClick(true)
                .create();

        MaterialRippleLayout.on(previewPlayPause)
                .rippleAlpha(0.2f)
                .rippleColor(0xFF585858)
                .rippleOverlay(true)
                .rippleDuration(200)
                .rippleDelayClick(true)
                .create();**//*


        preview.setVisibility(View.GONE);

    }

    private void makeAlbumViewAnimations(int x, int y) {


        // get the center for the clipping circle
        int cx = (albumViewSongList.getLeft() + albumViewSongList.getRight()) / 2;
        int cy = (albumViewSongList.getTop() + albumViewSongList.getBottom()) / 2;

        // get the final radius for the clipping circle
        int finalRadius = Math.max(albumViewSongList.getWidth(), albumViewSongList.getHeight());

        albumViewRevealAnimator =
                ViewAnimationUtils.createCircularReveal(albumViewSongList, cx, cy, 0, finalRadius);
        albumViewRevealAnimator.setInterpolator((Interpolator) interpolator);
        albumViewRevealAnimator.setDuration(600);



        albumViewHideAnimator =
                ViewAnimationUtils.createCircularReveal(albumViewSongList, cx, cy, finalRadius, 0);
        albumViewHideAnimator.setInterpolator((Interpolator) interpolator);
        albumViewHideAnimator.setDuration(600);

        albumViewHideAnimator.addListener(new SupportAnimator.AnimatorListener() {
            @Override
            public void onAnimationStart() {

            }

            @Override
            public void onAnimationEnd() {
                isShowingAlbumVIew = false;
                albumView.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel() {

            }

            @Override
            public void onAnimationRepeat() {

            }
        });
    }

    public void loadData() {

        class LongOperation extends AsyncTask<String, Void, String> {

            @Override
            protected String doInBackground(String... params) {
                allSongs = new ArrayList<>();
                artists = new ArrayList<>();
                albums = new ArrayList<>();
                ContentResolver musicResolver = getContentResolver();
                Uri musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                Cursor musicCursor = musicResolver.query(musicUri, null, null, null, null);

                if(musicCursor!=null && musicCursor.moveToFirst()){
                    //get columns
                    int titleColumn = musicCursor.getColumnIndex
                            (MediaStore.Audio.Media.TITLE);
                    int idColumn = musicCursor.getColumnIndex
                            (MediaStore.Audio.Media._ID);
                    int artistColumn = musicCursor.getColumnIndex
                            (MediaStore.Audio.Media.ARTIST);
                    int albumColumn = musicCursor.getColumnIndex
                            (MediaStore.Audio.Media.ALBUM);
                    int isAlarmID = musicCursor.getColumnIndex
                            (MediaStore.Audio.Media.IS_ALARM);
                    int isNotificationID = musicCursor.getColumnIndex
                            (MediaStore.Audio.Media.IS_NOTIFICATION);
                    int isRingtoneID = musicCursor.getColumnIndex
                            (MediaStore.Audio.Media.IS_RINGTONE);
                    //add queue to list


                    do {
                        long thisId = musicCursor.getLong(idColumn);
                        String thisTitle = musicCursor.getString(titleColumn);
                        String thisArtist = musicCursor.getString(artistColumn);
                        String thisAlbum = musicCursor.getString(albumColumn);
                        boolean isAlarm = (musicCursor.getShort(isAlarmID) != 0);
                        boolean isNotification = (musicCursor.getShort(isNotificationID) != 0);
                        boolean isRingtone = (musicCursor.getShort(isRingtoneID) != 0);
                        //String x = musicCursor.getString(isSong);
                        //Log.d("QWERTY", x);

                        Long albumId = musicCursor.getLong(musicCursor
                                .getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID));

                        Uri sArtworkUri = Uri
                                .parse("content://media/external/audio/albumart");
                        Uri albumArtUri = ContentUris.withAppendedId(sArtworkUri, albumId);

                        //Log.d("ALBUM ART" ,albumArtUri.toString());
                        *//**
                        Bitmap bitmap = null;
                        int width = getWindowManager().getDefaultDisplay().getWidth();
                        try {
                            bitmap = MediaStore.Images.Media.getBitmap(
                                    getApplicationContext().getContentResolver(), albumArtUri);
                            if(bitmap.getWidth() > width) {
                                bitmap = Bitmap.createScaledBitmap(bitmap, width, width, false);
                            }

                        } catch (FileNotFoundException exception) {
                            exception.printStackTrace();
                            bitmap = defaultBitmap;
                        } catch (IOException e) {

                            e.printStackTrace();
                        } catch (SecurityException s){
                            s.printStackTrace();
                            bitmap = defaultBitmap;
                        }
                         **//*

                        if(!isAlarm && !isNotification && !isRingtone) {
                            Song song = new Song(thisTitle,
                                    thisAlbum,
                                    thisArtist,
                                    thisId,
                                    albumArtUri.toString());
                            allSongs.add(song);
                            boolean isSOngAlreadyInAlbum = false;
                            for(Album album : albums){
                                if(song.getAlbumName().contentEquals(album.getName())){
                                    isSOngAlreadyInAlbum = true;
                                    album.addSong(song);
                                }
                            }
                            if(!isSOngAlreadyInAlbum){
                                Album album = new Album(song.getAlbumName(),
                                        song.getArtist(),
                                        song.getCoverArt());
                                album.addSong(song);
                                albums.add(album);
                            }
                        }

                        for (Album album : albums){
                            boolean isAlbumAlreadyInArtist = false;
                            for(Artist artist : artists){
                                if(artist.getName().contentEquals(album.getArtist())){
                                    isAlbumAlreadyInArtist = true;
                                    artist.addAlbum(album);
                                }
                            }
                            if(!isAlbumAlreadyInArtist){
                                artists.add(new Artist(album.getArtist(), album.getCoverArt()));
                            }
                        }
                    }
                    while (musicCursor.moveToNext());
                }
                return "Executed";
            }

            @Override
            protected void onPostExecute(String result) {

                if(playIntent==null){
                    playIntent = new Intent(MainActivity1.this, MusicService.class);
                    startService(playIntent);
                    bindService(playIntent, musicConnection, Context.BIND_IMPORTANT);
                }

                Collections.sort(allSongs, new Comparator<Song>() {
                    public int compare(Song a, Song b) {
                        return a.getTitle().toLowerCase().compareTo(b.getTitle().toLowerCase());
                    }
                });

                Collections.sort(albums, new Comparator<Album>() {
                    public int compare(Album a, Album b) {
                        return a.getName().toLowerCase().compareTo(b.getName().toLowerCase());
                    }
                });

                Collections.sort(artists, new Comparator<Artist>() {
                    public int compare(Artist a, Artist b) {
                        return a.getName().toLowerCase().compareTo(b.getName().toLowerCase());
                    }
                });

                tabLayout = (TabLayout) findViewById(R.id.tab_layout);


                viewPager = (ViewPager) findViewById(R.id.viewpager);
                ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
                viewPager.setAdapter(viewPagerAdapter);

                tabLayout.setupWithViewPager(viewPager);
                toolbarHeight = toolbar.getMeasuredHeight();
                //changeTabsFont();
                overrideFonts(toolbar, titleFont);
                makeAppBarAnimation();
                hideAppBar();
                viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                        hideAppBar();
                    }

                    @Override
                    public void onPageSelected(int position) {

                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {

                    }
                });
                //viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));


                //GridLayoutManager llm = new GridLayoutManager(getApplicationContext(), 1);
                //songList.setLayoutManager(llm);
                //songList.setHasFixedSize(true);
                //SongListAdapter recyclerViewAdapter = new SongListAdapter(getApplicationContext(), queue, imageLoader);
                //recyclerViewAdapter.setOnItemClickListener(songItemClickListener);
                //songList.setAdapter(recyclerViewAdapter);

                //quickReturnAttacher = QuickReturnAttacher.forView(songList);
                //quickReturnAttacher.addTargetView(toolbar, QuickReturnTargetView.POSITION_TOP, 50);
                loadingFinish.start();
            }

            @Override
            protected void onPreExecute() {

            }

            @Override
            protected void onProgressUpdate(Void... values) {}
        }

        LongOperation op = new LongOperation();
        op.execute();

    }


    //connect to the service
    private ServiceConnection musicConnection = new ServiceConnection(){

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.MusicBinder binder = (MusicService.MusicBinder)service;
            //get service
            musicSrv = binder.getService();
            //pass list
            musicSrv.setQueue(songs);
            musicSrv.setMusicChanger(MainActivity1.this);
            musicBound = true;

            //if(musicSrv.isPlaying()){
            //    inAnimation.start();
            //    isShowingCoverArt = true;
            //}
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicBound = false;
        }
    };

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onDestroy() {
        stopService(playIntent);
        musicSrv=null;
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if(isShowingCoverArt){
            //nowPlaying.setVisibility(View.GONE);
            outAnimation.start();
            preview.setVisibility(View.VISIBLE);
        }else if(isShowingAlbumVIew) {

            albumViewHideAnimator.start();

        }else{
            super.onBackPressed();
        }
    }


    @Override
    public void onChange(int position) {

        if(isShowingCoverArt){
            if(b != null){

            }
        }

        //Toast.makeText(getApplicationContext(), queue.get(position).getCoverArt(),Toast.LENGTH_LONG).show();


        this.position = position;

        Bitmap bmp = imageLoader.loadImageSync(songs.get(position).getCoverArt());

        if(bmp == null)bmp = defaultBitmap;

        clearCoverArt.setImageBitmap(bmp);
        title.setText(songs.get(position).getTitle());
        artist.setText(songs.get(position).getArtist());

        //previewTitle.setText(queue.get(position).getTitle());
        //previewArtist.setText(queue.get(position).getArtist());



        playPause.setImageResource(R.drawable.ic_pause);
        previewPlayPause.setImageResource(R.drawable.ic_pause);

*//**
        colorArt = new ColorArt(bmp);

        rlayout.setBackgroundColor(colorArt.getBackgroundColor());

        title.setTextColor(colorArt.getPrimaryColor());
        artist.setTextColor(colorArt.getSecondaryColor());

        totalTime.setTextColor(colorArt.getDetailColor());
        currentTime.setTextColor(colorArt.getDetailColor());
        dividerBar.setBackgroundColor(colorArt.getDetailColor());

        next.setColorFilter(colorArt.getDetailColor());
        previous.setColorFilter(colorArt.getDetailColor());
        playPause.setColorFilter(colorArt.getDetailColor());


        slider.setBackgroundColor(colorArt.getDetailColor());**//*




        updateSlider();
        totalTime.setText(formatTime(musicSrv.getMediaPlayer().getDuration()));


        *//**
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            //window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //window.setStatusBarColor(colorArt.getBackgroundColor());
            //window.setNavigationBarColor(colorArt.getBackgroundColor());

            MediaSessionCompat

            MediaSessionCompat mediaSession = new MediaSessionCompat(getApplicationContext(), "TAG", mSessionCallback, null);
            mediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS
                    | MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);


            Notification notification = new Notification.Builder(getApplicationContext())
                    // Show controls on lock screen even when user hides sensitive content.
                    .setVisibility(Notification.VISIBILITY_PUBLIC)
                    .setSmallIcon(R.drawable.ic_now_playing)
                            // Add media control buttons that invoke intents in your media service
                    .addAction(R.drawable.ic_previous, "Previous", null) // #0
                    .addAction(R.drawable.ic_pause, "Pause", null)  // #1
                    .addAction(R.drawable.ic_next, "Next", null)     // #2
                            // Apply the media style template
                    .setStyle(new Notification.MediaStyle()
                            .setShowActionsInCompactView(1 *//* #1: pause button *//*         *//**)
                            .setMediaSession(mediaSession.getSessionToken()))
                    .setContentTitle(queue.get(position).getTitle())
                    .setContentText(queue.get(position).getArtist())
                    .setLargeIcon(bmp)
                    .build();

            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.notify(10910, notification);
        }**//*

        if(!isShowingCoverArt)updatePreview();





        //Toast.makeText(getApplicationContext(), "Now playing : "+queue.get(position).getTitle(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPlay(int position, boolean isNewQueue) {

    }

    @Override
    public void onPrevious(int position) {

    }

    @Override
    public void onNext(int position) {

    }

    private void updatePreview() {

        if(!(position <= -1)) {
            previewTitle.setText(songs.get(position).getTitle());
            previewArtist.setText(songs.get(position).getArtist());
*//**
            previewPlayPause.setColorFilter(colorArt.getDetailColor());
            preview.setBackgroundColor(colorArt.getBackgroundColor());
            previewTitle.setTextColor(colorArt.getPrimaryColor());
            previewArtist.setTextColor(colorArt.getSecondaryColor());
            previewProgress.setBackgroundColor(colorArt.getDetailColor());

            //toolbarTitle.setTextColor(colorArt.getPrimaryColor());
            toolbar.setBackgroundColor(colorArt.getBackgroundColor());
            tabLayout.setBackgroundColor(colorArt.getBackgroundColor());
            tabLayout.setSelectedTabIndicatorColor(colorArt.getDetailColor());
            tabLayout.setTabTextColors(ColorStateList.valueOf(colorArt.getPrimaryColor()));
            toolbar.setBackgroundColor(colorArt.getBackgroundColor());**//*
            //changeTabsFont();
            overrideFonts(toolbar, titleFont);

            dontChangePreview = false;
        }
    }

    private String formatTime(int duration) {
        String time = "0:00";
            if ((duration / 1000) % 60 < 10) {
                time = "" + duration / 1000 / 60 + ":" + "0" + (duration / 1000) % 60;
            } else {
                time = "" + duration / 1000 / 60 + ":" + (duration / 1000) % 60;
            }
        return time;
    }

    private void updateSlider() {
        int now = 0;
        int total = 10;
        if (musicSrv != null) {
            if (musicSrv.isPlaying()) {
                now = musicSrv.getMediaPlayer().getCurrentPosition();
                total = musicSrv.getMediaPlayer().getDuration();

                slider.setMin(0);
                slider.setMax(total);
                slider.setValue(now);

                previewProgress.setMin(0);
                previewProgress.setMax(total);
                previewProgress.setProgress(now);

                currentTime.setText(formatTime(now));
            }
        }

    }

    public void makeNowPlayingEnterAnimations(){
        enterAnimation = new AnimatorSet();
        enterAnimation.playTogether(
                ObjectAnimator.ofFloat(title, View.ALPHA, 0, 1),
                ObjectAnimator.ofFloat(title, View.SCALE_X, .5f, 1),
                ObjectAnimator.ofFloat(title, View.SCALE_Y, .5f, 1));
        enterAnimation.setInterpolator(interpolator);

        exitAnimation = new AnimatorSet();
        exitAnimation.playTogether(
                ObjectAnimator.ofFloat(title, View.ALPHA, 1, 0),
                ObjectAnimator.ofFloat(title, View.SCALE_X, 1, .5f),
                ObjectAnimator.ofFloat(title, View.SCALE_Y, 1, .5f));
        exitAnimation.setInterpolator(interpolator);
        exitAnimation.setDuration(0);

    }

    public void makeAppBarAnimation(){
        appBarExit = ObjectAnimator.ofFloat(toolbar, View.TRANSLATION_Y, -toolbarHeight);
        appBarEnter = ObjectAnimator.ofFloat(toolbar, View.TRANSLATION_Y, 0);

        appBarEnter.setInterpolator(interpolator);
        appBarExit.setInterpolator(interpolator);

    }

    public static void hideAppBar(){
        if(isAppBarShowing) {
            appBarEnter.start();
            Log.d("APP_BAR", " --- HIDDEN");
            isAppBarShowing = false;
        }

    }

    public static void showAppBar(){
        if(!isAppBarShowing) {
            appBarExit.start();
            Log.d("APP_BAR", " --- SHOWN");
            isAppBarShowing = true;
        }
    }

    public void enterAnimation(){

        zoomInView(title, 30);
        zoomInView(artist, 150);
        zoomInView(previous, 150);
        zoomInView(playPause, 250);
        zoomInView(next, 350);
        zoomInView(currentTime, 300);
        zoomInView(slider, 350);
        zoomInView(totalTime, 400);
        zoomInView(dividerBar, 350);

    }

    public void exitAnimation(){

        zoomOutView(title, 50);
        zoomOutView(artist, 100);
        zoomOutView(previous, 150);
        zoomOutView(playPause, 200);
        zoomOutView(next, 250);
        zoomOutView(currentTime, 300);
        zoomOutView(slider, 350);
        zoomOutView(totalTime, 400);
        zoomOutView(dividerBar, 400);

    }

    public void zoomInView(Object target, long delay){
        AnimatorSet animation = enterAnimation.clone();
        animation.setTarget(target);
        animation.setStartDelay(delay);
        animation.start();
    }

    public void zoomOutView(Object target, long delay){
        AnimatorSet animation = exitAnimation.clone();
        animation.setTarget(target);
        animation.setStartDelay(0);
        animation.start();
    }
    
    
    //@SuppressLint("ValidFragment")
    public static class FragmentTab extends Fragment{

        int type;



        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
            type = getArguments().getInt("TYPE");
            Log.d("TYPE", "" + type);
            View rootView = inflater.inflate(R.layout.fragment, container, false);
            RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.fragment_list);
            if(type==SONGS) {
                GridLayoutManager llm = new GridLayoutManager(inflater.getContext(), 1);
                recyclerView.setLayoutManager(llm);
                recyclerView.setHasFixedSize(true);
                SongListAdapter recyclerViewAdapter = new SongListAdapter(inflater.getContext(), allSongs, imageLoader);
                recyclerViewAdapter.setOnItemClickListener(songItemClickListener);
                recyclerView.setAdapter(recyclerViewAdapter);

            }else if(type == ALBUMS){
                //StaggeredGridLayoutManager llm = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
                GridLayoutManager llm = new GridLayoutManager(inflater.getContext(), 2);
                recyclerView.setLayoutManager(llm);
                recyclerView.setHasFixedSize(true);
                AlbumListAdapter recyclerViewAdapter = new AlbumListAdapter(inflater.getContext(), albums, imageLoader);
                recyclerViewAdapter.setOnItemClickListener(albumItemClickListener);
                recyclerView.setAdapter(recyclerViewAdapter);

            }else{
                //StaggeredGridLayoutManager llm = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
                GridLayoutManager llm = new GridLayoutManager(inflater.getContext(), 2);
                recyclerView.setLayoutManager(llm);
                recyclerView.setHasFixedSize(true);
                ArtistListAdapter recyclerViewAdapter = new ArtistListAdapter(inflater.getContext(), artists, imageLoader);
                //recyclerViewAdapter.setOnItemClickListener(songItemClickListener);
                recyclerView.setAdapter(recyclerViewAdapter);
            }
            recyclerView.setPadding(0, toolbar.getMeasuredHeight(), 0, 0);
            recyclerView.setOnScrollListener(new RecyclerScrollListener());
            //recyclerView.setOnScrollChangeListener(new RecyclerScrollListener());
            return rootView;
        }
    }


    class ViewPagerAdapter extends FragmentStatePagerAdapter{

        @Override
        public void unregisterDataSetObserver(DataSetObserver observer) {
            if (observer != null) {
                super.unregisterDataSetObserver(observer);
            }
        }


        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            FragmentTab fragment = new FragmentTab();
            Bundle bundle = new Bundle(1);
            bundle.putInt("TYPE", position);
            fragment.setArguments(bundle);
            return fragment;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return TITLES[position];
        }

        @Override
        public int getCount() {
            return NUM_OF_TABS;
        }
    }




    private static class RecyclerScrollListener extends RecyclerView.OnScrollListener {

        int mScrolledY;




        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {


            //Log.d("SCROLLED", "" + "DX : " + dx + " * DY : " + dy);
            //Log.d("POSITION", "" + "SX : " + recyclerView.getScrollX() + " * SY : " + recyclerView.getScrollY());

            if(dy<-2){
                hideAppBar();
            }else if(dy>2){
                showAppBar();
            }

            *//**
            if(mGravity == Gravity.BOTTOM)
                mScrolledY += dy;
            else if(mGravity == Gravity.TOP)
                mScrolledY -= dy;

            if(toolbar == null)
                return;

            int translationY = 0;
            int rawY = mScrolledY;

            switch (mState) {
                case STATE_OFFSCREEN:
                    if(mGravity == Gravity.BOTTOM) {
                        if (rawY >= mMinRawY) {
                            mMinRawY = rawY;
                        } else {
                            mState = STATE_RETURNING;
                        }
                    } else if(mGravity == Gravity.TOP) {
                        if (rawY <= mMinRawY) {
                            mMinRawY = rawY;
                        } else {
                            mState = STATE_RETURNING;
                        }
                    }

                    translationY = rawY;
                    break;

                case STATE_ONSCREEN:
                    if(mGravity == Gravity.BOTTOM) {

                        if (rawY > toolbarHeight) {
                            mState = STATE_OFFSCREEN;
                            mMinRawY = rawY;
                        }
                    } else if(mGravity == Gravity.TOP) {

                        if (rawY < -toolbarHeight) {
                            mState = STATE_OFFSCREEN;
                            mMinRawY = rawY;
                        }
                    }
                    translationY = rawY;
                    break;

                case STATE_RETURNING:
                    if(mGravity == Gravity.BOTTOM) {
                        translationY = (rawY - mMinRawY) + toolbarHeight;

                        if (translationY < 0) {
                            translationY = 0;
                            mMinRawY = rawY + toolbarHeight;
                        }

                        if (rawY == 0) {
                            mState = STATE_ONSCREEN;
                            translationY = 0;
                        }

                        if (translationY > toolbarHeight) {
                            mState = STATE_OFFSCREEN;
                            mMinRawY = rawY;
                        }
                    } else if(mGravity == Gravity.TOP) {
                        translationY = (rawY + Math.abs(mMinRawY)) - toolbarHeight;

                        if (translationY > 0) {
                            translationY = 0;
                            mMinRawY = rawY - toolbarHeight;
                        }

                        if (rawY == 0) {
                            mState = STATE_ONSCREEN;
                            translationY = 0;
                        }

                        if (translationY < -toolbarHeight) {
                            mState = STATE_OFFSCREEN;
                            mMinRawY = rawY;
                        }
                    }
                    break;
            }

           // this can be used if the build is below honeycomb
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.HONEYCOMB) {
                TranslateAnimation anim = new TranslateAnimation(0, 0, translationY, translationY);
                anim.setFillAfter(true);
                anim.setDuration(0);
                toolbar.startAnimation(anim);
            } else {
                toolbar.setTranslationY(translationY);
            }**//*
        }
    }

    private final class MediaSessionCallback extends MediaSessionCompat.Callback {
        @Override
        public void onPlay() {
            resume();
        }
        @Override
        public void onPause() {
            pause();
        }
    }

    public void resume(){

        musicSrv.resumeSong();
        playPause.setImageResource(R.drawable.ic_pause);
        previewPlayPause.setImageResource(R.drawable.ic_pause);

    }

    public void pause(){

        musicSrv.pauseSong();
        playPause.setImageResource(R.drawable.ic_now_playing);
        previewPlayPause.setImageResource(R.drawable.ic_now_playing);

    }

    private void changeTabsFont() {

        ViewGroup vg = (ViewGroup) tabLayout.getChildAt(0);
        int tabsCount = vg.getChildCount();
        for (int j = 0; j < tabsCount; j++) {
            ViewGroup vgTab = (ViewGroup) vg.getChildAt(j);
            int tabChildsCount = vgTab.getChildCount();
            for (int i = 0; i < tabChildsCount; i++) {
                View tabViewChild = vgTab.getChildAt(i);
                if (tabViewChild instanceof TextView) {
                    ((TextView) tabViewChild).setTypeface(font);
                }
            }
        }
    }


    private void overrideFonts(final View v, Typeface font) {
        try {
            if (v instanceof ViewGroup) {
                ViewGroup vg = (ViewGroup) v;
                for (int i = 0; i < vg.getChildCount(); i++) {
                    View child = vg.getChildAt(i);
                    overrideFonts(child, font);
                }
            } else if (v instanceof TextView ) {
                ((TextView) v).setTypeface(font);
                //if(colorArt!=null)((TextView) v).setTextColor(colorArt.getPrimaryColor());
            }
        } catch (Exception e) {
        }
    }




}
*/