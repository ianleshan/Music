package verendus.leshan.music.objects;

import java.util.ArrayList;

/**
 * Created by leshan on 1/18/16.
 */
public class Playlist {

    String name;
    ArrayList<Song> songs = new ArrayList<>();

    public Playlist(String name) {
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
