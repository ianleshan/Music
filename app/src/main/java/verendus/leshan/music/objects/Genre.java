package verendus.leshan.music.objects;

import java.util.ArrayList;

/**
 * Created by leshan on 1/18/16.
 */
public class Genre {

    String name;
    ArrayList<Song> songs = new ArrayList<>();

    public Genre(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public ArrayList<Song> getSongs() {
        return songs;
    }

    public void setSongs(ArrayList<Song> songs) {
        this.songs = songs;
    }
}
