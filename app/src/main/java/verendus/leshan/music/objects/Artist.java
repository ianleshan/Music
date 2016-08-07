package verendus.leshan.music.objects;


import java.util.ArrayList;
import java.util.HashSet;

import verendus.leshan.music.R;

/**
 * Created by leshan on 9/4/15.
 */
public class Artist {

    HashSet<Album> albums = new HashSet<>();
    HashSet<Song> songs = new HashSet<>();

    String name;
    String coverArt;

    public Artist(String name, String coverArt) {
        this.name = name;
        if(coverArt != null) {
            this.coverArt = coverArt;
        }else{
            this.coverArt = "drawable://" + R.drawable.sample_art;
        }
    }

    public String getName() {
        return name;
    }

    public void addAlbum(Album album) {
        albums.add(album);
        songs.addAll(album.getSongs());
    }

    public String getCoverArt() {
        return coverArt;
    }

    public void setCoverArt(String coverArt) {
        this.coverArt = coverArt;
    }

    public HashSet<Album> getAlbums() {
        return albums;
    }

    public HashSet<Song> getSongs() {
        return songs;
    }
}
