package verendus.leshan.music.objects;

import verendus.leshan.music.R;

/**
 * Created by leshan on 8/14/14.
 */
public class Song {

    String title;
    String albumName;
    String artist;
    long songId;
    String coverArt;
    int duration;
    int dateAdded;
    int trackNumber;
    Album album;

    int color;

    public Song(String title, String albumName, String artist, long songId, String coverArt, int duration, int dateAdded, int trackNumber) {
        this.title = title == null? "<unknown>" : title;
        this.albumName = albumName == null? "<unknown>" : albumName;
        this.artist = artist == null? "<unknown>" : artist;
        this.songId = songId;
        this.duration = duration;
        this.dateAdded = dateAdded;
        this.trackNumber = trackNumber;

        if(coverArt != null) {
            this.coverArt = coverArt;
        }else{
            this.coverArt = "drawable://" + R.drawable.sample_art;
        }



    }

    public String getTitle() {
        return title;
    }

    public String getAlbumName() {
        return albumName;
    }

    public String getArtist() {
        return artist;
    }

    public long getSongId() {
        return songId;
    }

    public String getCoverArt() {
        return coverArt;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getColor() {
        return color;
    }

    public int getDuration() {
        return duration;
    }

    public Album getAlbum() {
        return album;
    }

    public int getTrackNumber() {
        return trackNumber;
    }

    public void setAlbum(Album album) {
        this.album = album;
    }
}
