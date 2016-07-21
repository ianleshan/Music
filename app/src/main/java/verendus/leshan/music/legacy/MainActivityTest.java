package verendus.leshan.music.legacy;


public class MainActivityTest {}/*extends AppCompatActivity implements MusicChanger {

    private static final int NUM_OF_TABS = 5;
    public static final int SONGS = 0;
    public static final int ALBUMS = 1;
    public static final int ARTISTS = 2;
    private static final CharSequence[] TITLES = new CharSequence[]{"Songs", "Albums", "Artists", "Genres", "Playlists"};
    ViewPager viewPager;
    TabLayout tabLayout;
    Toolbar toolbarTitle;



    //QuickReturnRecyclerView songList;
    static SongListAdapter.OnItemClickListener songItemClickListener, albumSongItemClickListener;
    static AlbumListAdapter.OnItemClickListener albumItemClickListener;
    Loading load;
    Slider slider;
    ProgressBarDeterminate previewProgress;
    RecyclerView albumViewSongList;

    Bitmap b;

    TimeInterpolator interpolator;

    Typeface font, titleFont;

    static ArrayList<Song> songs = new ArrayList<Song>();
    static ArrayList<Song> allSongs = new ArrayList<Song>();
    static ArrayList<Album> albums = new ArrayList<Album>();
    static ArrayList<Artist> artists = new ArrayList<Artist>();

    private MusicService musicSrv;
    private Intent playIntent;
    private boolean musicBound=false;

    Bitmap defaultBitmap;

    static ImageLoader imageLoader;
    static AppBarLayout toolbar;

    DrawerLayout drawerLayout;
    NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        //this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_my_test);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);


        toolbar = (AppBarLayout) findViewById(R.id.toolbar);
        toolbarTitle = (Toolbar) findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbarTitle);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        navigationView = (NavigationView) findViewById(R.id.nav_view);

        if(toolbarTitle != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbarTitle.setNavigationIcon(R.mipmap.ic_drawer);
            toolbarTitle.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    drawerLayout.openDrawer(GravityCompat.START);
                }
            });
        }

        interpolator = new FastOutSlowInInterpolator();

        int width = getWindowManager().getDefaultDisplay().getWidth();

        defaultBitmap = BitmapFactory.decodeResource(MainActivityTest.this.getApplicationContext().getResources(),
                R.drawable.sample_art);
        if (defaultBitmap.getWidth() > width) {
            defaultBitmap = Bitmap.createScaledBitmap(defaultBitmap, width, width, true);
        }
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheOnDisc(true)
                .showImageForEmptyUri(R.drawable.sample_art)
                .showImageOnFail(R.drawable.sample_art)
                        //.showImageOnLoading(R.drawable.sample_art)
                .cacheOnDisk(true)
                .displayer(new FadeInBitmapDisplayer(500))
                .build();

        ImageLoaderConfiguration configuration = new ImageLoaderConfiguration.Builder(this)
                .defaultDisplayImageOptions(defaultOptions).
                        denyCacheImageMultipleSizesInMemory()
                .build();

        ImageLoader.getInstance().init(configuration);
        imageLoader = ImageLoader.getInstance();


        albumViewSongList = (RecyclerView) findViewById(R.id.album_view_list);
        load = (Loading) findViewById(R.id.loading_icon);
        slider = (Slider) findViewById(R.id.slider);


        previewProgress = (ProgressBarDeterminate) findViewById(R.id.preview_progress);


        font = Typeface.createFromAsset(getAssets(), "font.ttf");
        titleFont = Typeface.createFromAsset(getAssets(), "titleFont.ttf");


        loadData();


        songItemClickListener = new SongListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {


            }
        };


        albumItemClickListener = new AlbumListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(final View view, int position) {


            }
        };

        Resources r = getResources();
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 84, r.getDisplayMetrics());

        final int[] rlayoutLocation = new int[2];


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
                                        getAlbumArt(song.getAlbumName(), song.getArtist()));
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
                                artists.add(new Artist(album.getArtist(), getArtistArt(album.getArtist())));
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
                    playIntent = new Intent(MainActivityTest.this, MusicService.class);
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
                //changeTabsFont();
                overrideFonts(toolbar, titleFont);
                viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

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

    private String getArtistArt(String artist) {

        String albumArtUrl = null;
        String x = null;

        x = "http://ws.audioscrobbler.com/2.0/?method=artist.getinfo&artist="
                + URLEncoder.encode(artist)
                + "&api_key="
                + "81f683d158289972f4532a1aefc70e48";
        Log.d("AAAERTIST", x);
        try {
            XMLParser parser = new XMLParser();
            String xml = parser.getXmlFromUrl(x); // getting XML from URL
            Document doc = parser.getDomElement(xml);
            NodeList nl = doc.getElementsByTagName("image");
            //for (int i = 0; i < nl.getLength(); i++) {
                Element e = (Element) nl.item(3);
                if (e.getAttribute("size").contentEquals("extralarge")) {
                    //Log.d("TAG", "ELEMENT" + i + "... Size = " + e.getAttribute("size") + " = " + parser.getElementValue(e));
                    albumArtUrl = parser.getElementValue(e);
                }
            //}
        } catch (Exception e) {
            e.printStackTrace();
        }
        return albumArtUrl;

    }

    private String getAlbumArt(String album, String artist) {

        String albumArtUrl = null;
        String x = null;

        try {
            StringBuilder stringBuilder = new StringBuilder("http://ws.audioscrobbler.com/2.0/");
            stringBuilder.append("?method=album.getinfo");
            stringBuilder.append("&api_key=");
            stringBuilder.append("81f683d158289972f4532a1aefc70e48");
            stringBuilder.append("&artist=" + URLEncoder.encode(artist, "UTF-8"));
            stringBuilder.append("&album=" + URLEncoder.encode(album, "UTF-8"));
            x = stringBuilder.toString();
            Log.d("UURRLL", x);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        try {
            XMLParser parser = new XMLParser();
            String xml = parser.getXmlFromUrl(x); // getting XML from URL
            Document doc = parser.getDomElement(xml);
            NodeList nl = doc.getElementsByTagName("image");
            for (int i = 0; i < nl.getLength(); i++) {
                Element e = (Element) nl.item(i);
                //Log.d("TAG", "Size = " + e.getAttribute("size") + " = " + parser.getElementValue(e));
                if (e.getAttribute("size").contentEquals("extralarge")) {
                    albumArtUrl = parser.getElementValue(e);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return albumArtUrl;

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
            musicSrv.setMusicChanger(MainActivityTest.this);
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
    public void onChange(int position) {

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

    private String formatTime(int duration) {
        String time = "0:00";
            if ((duration / 1000) % 60 < 10) {
                time = "" + duration / 1000 / 60 + ":" + "0" + (duration / 1000) % 60;
            } else {
                time = "" + duration / 1000 / 60 + ":" + (duration / 1000) % 60;
            }
        return time;
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
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

