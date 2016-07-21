package verendus.leshan.music.service;


import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import verendus.leshan.music.MainActivity;
import verendus.leshan.music.objects.Album;
import verendus.leshan.music.objects.Artist;
import verendus.leshan.music.objects.Genre;
import verendus.leshan.music.objects.God;
import verendus.leshan.music.objects.Playlist;
import verendus.leshan.music.objects.Song;
import verendus.leshan.music.utils.XMLParser;

/**
 * A simple {@link Fragment} subclass.
 */
public class WorkerFragment extends Fragment {


    static ArrayList<Song> songs = new ArrayList<>();
    static ArrayList<Album> albums = new ArrayList<>();
    static ArrayList<Artist> artists = new ArrayList<>();
    static ArrayList<Genre> genres = new ArrayList<>();
    static ArrayList<Playlist> playlists = new ArrayList<>();

    public God god;
    MainActivity mainActivity;
    WorkerCallbacks workerCallbacks;
    public static final String TAG = "WORKER_FRAGMENT";

    public static final String[] imageQualities = new String[]{ "small", "medium", "large", "extralarge", "mega"};
    public static int SMALL = 0;
    public static int MEDIUM = 1;
    public static int LARGE = 2;
    public static int EXTRA_LARGE = 3;
    public static int MEGA = 4;
    public static final String IMAGE_QUALITY = imageQualities[EXTRA_LARGE];


    public WorkerFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);
        mainActivity = (MainActivity)getActivity();
        workerCallbacks = mainActivity;
        loadData();
    }


    public God getGod() {
        return god;
    }

    public void loadData() {
        Log.d(TAG, "Loading data");

        class LongOperation extends AsyncTask<String, Void, String> {

            @Override
            protected String doInBackground(String... params) {

                Log.d(TAG, "doInBackGround called");
                if(god != null) {
                    if(!god.isDataLoaded()) {
                        Log.d(TAG, "object is not loaded but is not null");
                        inputData();
                    }else {
                        Log.d(TAG, "data recovered");
                    }
                }else {
                    Log.d(TAG, "object is null");
                    inputData();
                }

                Log.d(TAG, "doInBackGround finished");
                return "Executed";
            }

            private void inputData() {

                Log.d(TAG, "loading first time data");
                god = new God(mainActivity);

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
                god.dataLoaded();
            }


            @Override
            protected void onPostExecute(String result) {

                Log.d(TAG, "onPostExecute called");
                workerCallbacks.postExecute(god);

            }

            @Override
            protected void onPreExecute() {

                Log.d(TAG, "onPreExecute called");
                workerCallbacks.preExecute();

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
        ContentResolver musicResolver = mainActivity.getContentResolver();
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
                int thisTrack = musicCursor.getInt(trackColumn);
                Log.d("TAG", thisTrack+"  ----  " + thisAlbum);
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
                        if (song.getAlbumName().contentEquals(album.getName())) {
                            isSOngAlreadyInAlbum = true;
                            song.setAlbum(album);
                            album.addSong(song);
                        }
                    }
                    if (!isSOngAlreadyInAlbum) {
                        Album album = new Album(song.getAlbumName(),
                                song.getArtist(),
                                //getAlbumArt(song.getAlbumName(), song.getArtist(), albumArtUri.toString()));
                                //albumArtUri.toString());
                                song.getCoverArt());
                        song.setAlbum(album);
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

        ContentResolver musicResolver = mainActivity.getContentResolver();
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

        ContentResolver musicResolver = mainActivity.getContentResolver();
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

        SharedPreferences prefs = mainActivity.getSharedPreferences("artistArt", mainActivity.MODE_PRIVATE);
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

            SharedPreferences.Editor editor = mainActivity.getSharedPreferences("artistArt", mainActivity.MODE_PRIVATE).edit();
            editor.putString(artist, albumArtUrl);
            editor.commit();

            return albumArtUrl;
        }

    }
    private String getAlbumArt(String album, String artist, String loacalArt) {

        if(loacalArt != null)return loacalArt;

        String albumArtUrl = null;
        String x = null;

        SharedPreferences prefs = mainActivity.getSharedPreferences("albumArt", mainActivity.MODE_PRIVATE);
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

        SharedPreferences.Editor editor = mainActivity.getSharedPreferences("albumArt", mainActivity.MODE_PRIVATE).edit();
        editor.putString(album, albumArtUrl);
        editor.commit();


        return albumArtUrl;

    }

}
