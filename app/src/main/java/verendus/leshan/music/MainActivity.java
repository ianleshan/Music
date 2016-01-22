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
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
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

public class MainActivity extends AppCompatActivity implements LibraryFragment.OnFragmentInteractionListener, MusicChanger {


    static ArrayList<Song> songs = new ArrayList<>();
    static ArrayList<Album> albums = new ArrayList<>();
    static ArrayList<Artist> artists = new ArrayList<>();
    static ArrayList<Genre> genres = new ArrayList<>();
    static ArrayList<Playlist> playlists = new ArrayList<>();

    private God god;
    private static MusicService musicService;
    private Intent playIntent;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);


        createImageLoader();

        font = Typeface.createFromAsset(getAssets(), "font.ttf");
        titleFont = Typeface.createFromAsset(getAssets(), "titleFont.ttf");

        songTitle = (TextView) findViewById(R.id.now_playing_title);
        songArtist = (TextView) findViewById(R.id.now_playing_artist);
        queueLayoutHeader = (LinearLayout) findViewById(R.id.queue_layout_header);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        drawerHeaderImage = (SquaredImageView) navigationView.getHeaderView(0).findViewById(R.id.drawer_header_image);
        drawerHeaderTitle = (TextView) navigationView.getHeaderView(0).findViewById(R.id.drawer_header_title);
        queueList = (RecyclerView) findViewById(R.id.queue_layout_list);
        previewTemplate = (RelativeLayout) findViewById(R.id.preview_template);
        level1Container = (RelativeLayout) findViewById(R.id.level1_container);
        loadingScreen = (RelativeLayout) findViewById(R.id.loading_screen);
        nowPlayingPanel = (MySlidingUpLayout) findViewById(R.id.sliding_layout);
        queueSlidingPanel = (MySlidingUpLayout) findViewById(R.id.queue_sliding_panel);

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

        Log.d("MEASURED HEIGHT :", queueLayoutHeaderMeasuredHeight + "");

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

        god = new God(this);

        loadData();
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

    public void loadData() {

        class LongOperation extends AsyncTask<String, Void, String> {

            @Override
            protected String doInBackground(String... params) {

                loadSongs();

                Collections.sort(songs, new Comparator<Song>() {
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

                god.setSongs(songs);
                god.setAlbums(albums);
                god.setArtists(artists);

                loadGenres();
                god.setGenres(genres);

                loadPlaylists();
                god.setPlaylists(playlists);

                return "Executed";
            }



            @Override
            protected void onPostExecute(String result) {

                if (playIntent == null) {
                    playIntent = new Intent(MainActivity.this, MusicService.class);
                    startService(playIntent);
                    bindService(playIntent, musicConnection, Context.BIND_IMPORTANT);
                }



                god.setDrawerLayout(drawerLayout);

                android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                LibraryFragment fragment = LibraryFragment.newInstance();
                fragmentTransaction.add(R.id.library_fragment_container, fragment);
                fragmentTransaction.commit();

                int height = previewTemplate.getMeasuredHeight();
                nowPlayingPanel.setPanelHeight(height);
                level1Container.setPadding(0, 0, 0, height);
                nowPlayingPanel.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                nowPlayingPanel.setPanelSlideListener(god.getPanelSlideListener());
                god.setPreview(previewTemplate);
                god.setNowPlayingStatusBar((RelativeLayout) findViewById(R.id.status_bar_color_2));
                god.setLibraryStatusBar((RelativeLayout) findViewById(R.id.status_bar_color_1));


                //Make an animation for this
                loadingScreen.setVisibility(View.GONE);

            }

            @Override
            protected void onPreExecute() {

                //Make an animation for this
                loadingScreen.setVisibility(View.VISIBLE);

            }

            @Override
            protected void onProgressUpdate(Void... values) {
            }
        }

        LongOperation op = new LongOperation();
        op.execute();

    }

    public void loadSongs() {

        songs = new ArrayList<>();
        artists = new ArrayList<>();
        albums = new ArrayList<>();
        ContentResolver musicResolver = getContentResolver();
        Uri musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor musicCursor = musicResolver.query(musicUri, null, null, null, null);

        if (musicCursor != null && musicCursor.moveToFirst()) {
            //get columns
            int titleColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media.TITLE);
            int idColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media._ID);
            int artistColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media.ARTIST);
            int albumColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media.ALBUM);
            int trackColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media.TRACK);
            int isAlarmID = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media.IS_ALARM);
            int isNotificationID = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media.IS_NOTIFICATION);
            int isRingtoneID = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media.IS_RINGTONE);
            int durationColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media.DURATION);
            int dateAddedID = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media.DATE_ADDED);
            //add queue to list


            do {
                long thisId = musicCursor.getLong(idColumn);
                String thisTitle = musicCursor.getString(titleColumn);
                String thisArtist = musicCursor.getString(artistColumn);
                String thisAlbum = musicCursor.getString(albumColumn);
                int thisTrack = musicCursor.getInt(trackColumn);Log.d("TAG", thisTrack+"  ----  " + thisAlbum);
                int thisDuration = musicCursor.getInt(durationColumn);
                int thisDateAdded = musicCursor.getInt(dateAddedID);
                boolean isAlarm = (musicCursor.getShort(isAlarmID) != 0);
                boolean isNotification = (musicCursor.getShort(isNotificationID) != 0);
                boolean isRingtone = (musicCursor.getShort(isRingtoneID) != 0);
                //String x = musicCursor.getString(isSong);

                Long albumId = musicCursor.getLong(musicCursor
                        .getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID));

                Uri sArtworkUri = Uri
                        .parse("content://media/external/audio/albumart");
                Uri albumArtUri = ContentUris.withAppendedId(sArtworkUri, albumId);


                //Log.d("ALBUM ART" ,albumArtUri.toString());
                /**
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
                 **/

                if (!isAlarm && !isNotification && !isRingtone) {
                    Song song = new Song(thisTitle,
                            thisAlbum,
                            thisArtist,
                            thisId,
                            getAlbumArt(thisAlbum, thisArtist, albumArtUri.toString()),
                            thisDuration,
                            thisDateAdded);
                    songs.add(song);
                    boolean isSOngAlreadyInAlbum = false;
                    for (Album album : albums) {
                        if (song.getAlbum().contentEquals(album.getName())) {
                            isSOngAlreadyInAlbum = true;
                            album.addSong(song);
                        }
                    }
                    if (!isSOngAlreadyInAlbum) {
                        Album album = new Album(song.getAlbum(),
                                song.getArtist(),
                                //getAlbumArt(song.getAlbum(), song.getArtist(), albumArtUri.toString()));
                                //albumArtUri.toString());
                                song.getCoverArt());
                        album.addSong(song);
                        albums.add(album);
                    }
                }

                for (Album album : albums) {
                    boolean isAlbumAlreadyInArtist = false;
                    for (Artist artist : artists) {
                        if (artist.getName().contentEquals(album.getArtist())) {
                            isAlbumAlreadyInArtist = true;
                            artist.addAlbum(album);
                        }
                    }
                    if (!isAlbumAlreadyInArtist) {
                        artists.add(new Artist(album.getArtist(),
                                getArtistArt(album.getArtist())));
                        //albumArtUri.toString()));

                    }
                }
            }
            while (musicCursor.moveToNext());
            musicCursor.close();
        }

    }
    public void loadGenres() {

        ContentResolver musicResolver = getContentResolver();
        Uri genreUri = MediaStore.Audio.Genres.EXTERNAL_CONTENT_URI;
        Cursor genreCursor = musicResolver.query(genreUri, null, null, null, null);

        if (genreCursor != null && genreCursor.moveToFirst()) {
            //get columns
            int titleColumn = genreCursor.getColumnIndex
                    (MediaStore.Audio.Genres.NAME);
            int genreIDColumn = genreCursor.getColumnIndex
                    (MediaStore.Audio.Genres._ID);


            do {

                String thisTitle = genreCursor.getString(titleColumn);
                int genreID = genreCursor.getInt(genreIDColumn);
                //Log.d("GENRE :", thisTitle);
                Genre genre = new Genre(thisTitle);

                Uri genreListUri = MediaStore.Audio.Genres.Members.getContentUri("external", genreID);
                Cursor genreMembersCursor = musicResolver.query(genreListUri, null,null, null, null);

                if (genreMembersCursor != null && genreMembersCursor.moveToFirst()) {
                    //get columns
                    int genreMemberTitleColumn = genreMembersCursor.getColumnIndex
                            (MediaStore.Audio.Genres.Members.TITLE);

                    ArrayList<Song> songsInGenre = new ArrayList<>();

                    do {

                        String title = genreMembersCursor.getString(genreMemberTitleColumn);
                        songsInGenre.add(god.getSongFromName(title));
                        //Log.d("GENRE MEMBER :", title);

                    }
                    while (genreMembersCursor.moveToNext());
                    genreMembersCursor.close();
                    genre.setSongs(songsInGenre);
                    genres.add(genre);
                }


            }
            while (genreCursor.moveToNext());
            genreCursor.close();
        }

    }
    public void loadPlaylists() {

        ContentResolver musicResolver = getContentResolver();
        Uri musicUri = MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI;
        Cursor playlistCursor = musicResolver.query(musicUri, null, null, null, null);

        if (playlistCursor != null && playlistCursor.moveToFirst()) {
            //get columns
            int titleColumn = playlistCursor.getColumnIndex
                    (MediaStore.Audio.Playlists.NAME);
            int playlistIDColumn = playlistCursor.getColumnIndex
                    (MediaStore.Audio.Playlists._ID);


            do {

                String thisTitle = playlistCursor.getString(titleColumn);
                int playlistID = playlistCursor.getInt(playlistIDColumn);
                //Log.d("PLAYLIST :", thisTitle);
                Playlist playlist = new Playlist(thisTitle);

                Uri playlistUri = MediaStore.Audio.Playlists.Members.getContentUri("external", playlistID);
                Cursor playlistMembersCursor = musicResolver.query(playlistUri, null,null, null, null);

                if (playlistMembersCursor != null && playlistMembersCursor.moveToFirst()) {
                    //get columns
                    int playlistMemberTitleColumn = playlistMembersCursor.getColumnIndex
                            (MediaStore.Audio.Playlists.Members.TITLE);

                    ArrayList<Song> songsInPlaylist = new ArrayList<>();

                    do {

                        String title = playlistMembersCursor.getString(playlistMemberTitleColumn);
                        songsInPlaylist.add(god.getSongFromName(title));
                        //Log.d("PLAYLIST MEMBER :", title);

                    }
                    while (playlistMembersCursor.moveToNext());
                    playlistMembersCursor.close();
                    playlist.setSongs(songsInPlaylist);
                    playlists.add(playlist);
                }


            }
            while (playlistCursor.moveToNext());
            playlistCursor.close();
        }

    }

    private String getArtistArt(String artist) {

        String albumArtUrl = null;
        String x = null;

        SharedPreferences prefs = getSharedPreferences("artistArt", MODE_PRIVATE);
        albumArtUrl = prefs.getString(artist, null);
        if (albumArtUrl != null) {
            return albumArtUrl;
        } else {

            x = "http://ws.audioscrobbler.com/2.0/?method=artist.getinfo&artist="
                    + URLEncoder.encode(artist)
                    + "&api_key="
                    + "81f683d158289972f4532a1aefc70e48";
            //Log.d("AAAERTIST", x);
            try {
                XMLParser parser = new XMLParser();
                String xml = parser.getXmlFromUrl(x); // getting XML from URL
                Document doc = parser.getDomElement(xml);
                NodeList nl = doc.getElementsByTagName("image");
                //for (int i = 0; i < nl.getLength(); i++) {
                Element e = (Element) nl.item(3);
                if (e.getAttribute("size").contentEquals(IMAGE_QUALITY)) {
                    //Log.d("TAG", "ELEMENT" + i + "... Size = " + e.getAttribute("size") + " = " + parser.getElementValue(e));
                    albumArtUrl = parser.getElementValue(e);
                }
                //}
            } catch (Exception e) {
                e.printStackTrace();
            }

            SharedPreferences.Editor editor = getSharedPreferences("artistArt", MODE_PRIVATE).edit();
            editor.putString(artist, albumArtUrl);
            editor.commit();

            return albumArtUrl;
        }

    }

    private String getAlbumArt(String album, String artist, String loacalArt) {

        //if(loacalArt != null)return loacalArt;

        String albumArtUrl = null;
        String x = null;

        SharedPreferences prefs = getSharedPreferences("albumArt", MODE_PRIVATE);
        albumArtUrl = prefs.getString(album, null);

        if(albumArtUrl != null){
            return albumArtUrl;
        }else {


            try {
                StringBuilder stringBuilder = new StringBuilder("http://ws.audioscrobbler.com/2.0/");
                stringBuilder.append("?method=album.getinfo");
                stringBuilder.append("&api_key=");
                stringBuilder.append("81f683d158289972f4532a1aefc70e48");
                stringBuilder.append("&artist=" + URLEncoder.encode(artist, "UTF-8"));
                stringBuilder.append("&album=" + URLEncoder.encode(album, "UTF-8"));
                x = stringBuilder.toString();
                //Log.d("UURRLL", x);
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
                    if (e.getAttribute("size").contentEquals(IMAGE_QUALITY)) {
                        albumArtUrl = parser.getElementValue(e);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        SharedPreferences.Editor editor = getSharedPreferences("albumArt", MODE_PRIVATE).edit();
        editor.putString(album, albumArtUrl);
        editor.commit();


        return albumArtUrl;

    }

    private ServiceConnection musicConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.MusicBinder binder = (MusicService.MusicBinder) service;
            //get service
            musicService = binder.getService();
            //pass list
            musicService.setQueue(songs);
            musicService.setMusicChanger(MainActivity.this);
            musicBound = true;

            //if(musicService.isPlaying()){
            //    inAnimation.start();
            //    isShowingCoverArt = true;
            //}
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
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

            Log.d("LOCATING BUG :", "setting onPageSelected event");
            playSongFromQueue(position);


        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    @Override
    public void onPlay(final int position, boolean isNewQueue) {

        Log.d("LOCATING BUG :", "onPlay method");

        Song song = musicService.getQueue().get(position);
        songTitle.setText(song.getTitle());
        songArtist.setText(song.getArtist());
        drawerHeaderTitle.setText(song.getTitle());
        imageLoader.displayImage(song.getCoverArt(), drawerHeaderImage);

        if (isNewQueue) {
            NowPlayingViewPagerAdapter viewPagerAdapter = new NowPlayingViewPagerAdapter(getSupportFragmentManager());
            viewPager = (MyViewPager) findViewById(R.id.now_playing_view_pager);
            viewPager.removeOnPageChangeListener(onPageChangeListener);
            if (viewPager != null) {
                viewPager.setAdapter(viewPagerAdapter);
                viewPager.setCurrentItem(position);
            }
            viewPager.addOnPageChangeListener(onPageChangeListener);
            god.setNowPlayingViewPager(viewPager);

            GridLayoutManager llm = new GridLayoutManager(getApplicationContext(), 1);
            queueList.setLayoutManager(llm);
            queueList.setHasFixedSize(true);
            QueueListAdapter recyclerViewAdapter = new QueueListAdapter(getApplicationContext(), musicService.getQueue(), imageLoader);
            recyclerViewAdapter.setOnItemClickListener(queueOnItemClickListener);
            queueList.setAdapter(recyclerViewAdapter);
        } else {

            Log.d("HERE  :::", "HERE");
            if (viewPager != null) {
                viewPager.setCurrentItem(position, true);
            }

        }
        Thread thread = new Thread(new Runnable() {
            public void run() {
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
                god.setShuffleButton(view.findViewById(R.id.shuffle_toggle));


                LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.now_playing_layout);
                while (linearLayout.getTag() == null) {

                    linearLayout = (LinearLayout) view.findViewById(R.id.now_playing_layout);

                }


                final LinearLayout finalLinearLayout = linearLayout;
                runOnUiThread(new Runnable() {
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
        thread.start();


    }

    public void setQueueAndPlaySong(ArrayList<Song> newQueue, int position) {
        Log.d("LOCATING BUG :", "Setting queue");
        musicService.setQueue(newQueue);

        Log.d("LOCATING BUG :", "Setting position");
        musicService.setSong(position);

        Log.d("LOCATING BUG :", "Playing song");
        musicService.playSong();


    }


    public void playSongFromQueue(int position) {
        musicService.setSong(position);
        musicService.playSong();
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

    public static class SongFragmentTab extends android.support.v4.app.Fragment {

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
            Log.d("LOCATING BUG :", "Creating SongFragment number - " + position);
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


            //if(song.getCoverArt() == null) song.getAlbum().setCoverArt("drawable://" + R.drawable.sample_art);
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


    @Override
    public void onBackPressed() {

        if (nowPlayingPanel.getPanelState().equals(SlidingUpPanelLayout.PanelState.EXPANDED)) {
            if (queueSlidingPanel.getPanelState().equals(SlidingUpPanelLayout.PanelState.EXPANDED)) {
                queueSlidingPanel.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            }else {
                nowPlayingPanel.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            }
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        stopService(playIntent);
        musicService = null;
        super.onDestroy();
    }

    public static MusicService getMusicService() {
        return musicService;
    }
}
