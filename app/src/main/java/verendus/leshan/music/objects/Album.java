package verendus.leshan.music.objects;

import android.support.v7.graphics.Palette;

import java.io.Serializable;
import java.util.ArrayList;

import verendus.leshan.music.R;

/**
 * Created by leshan on 9/3/15.
 */
public class Album implements Serializable{

    ArrayList<Song> songs = new ArrayList<>();
    String name;
    String artist;
    String coverArt;
    Palette palette;

    public Album(String name, String artist, String coverArt) {
        this.name = name;
        this.artist = artist;

        if (coverArt != null) {
            this.coverArt = coverArt;
        } else {
            this.coverArt = "drawable://" + R.drawable.sample_art;
        }




    }

    public Palette getPalette() {
        return palette;
    }

    public void setPalette(Palette palette) {
        this.palette = palette;
    }

    public String getArtist() {
        return artist;
    }

    public String getCoverArt() {
        return coverArt;
    }

    public String getName() {
        return name;
    }

    public ArrayList<Song> getSongs() {
        return songs;
    }

    public void addSong(Song song) {
        songs.add(song);
    }


    public void setCoverArt(String coverArt) {
        this.coverArt = coverArt;
    }
}
