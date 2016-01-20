package verendus.leshan.music.objects;


import java.util.ArrayList;

import verendus.leshan.music.R;

/**
 * Created by leshan on 9/4/15.
 */
public class Artist {

    ArrayList<Album> albums = new ArrayList<>();
    ArrayList<Song> songs = new ArrayList<>();

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
    }

    public String getCoverArt() {
        return coverArt;
    }

    public void setCoverArt(String coverArt) {
        this.coverArt = coverArt;
    }
}
