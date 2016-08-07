package verendus.leshan.music.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import verendus.leshan.music.MainActivity;
import verendus.leshan.music.R;
import verendus.leshan.music.objects.Album;
import verendus.leshan.music.objects.God;
import verendus.leshan.music.objects.Song;

/**
 * Created by leshan on 9/3/15.
 */
public class AlbumViewSongListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    ArrayList<Object> songs;
    LayoutInflater inflater;
    Typeface font, titleFont;
    static OnItemClickListener itemClickListener;

    private final int TITLE = 0, SONG = 1;
    MainActivity mainActivity;
    private String albumName = "";



    public static class AlbumViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView songName;
        TextView artistName;
        TextView trackNumber;

        AlbumViewHolder(View itemView) {
            super(itemView);
            songName = (TextView)itemView.findViewById(R.id.album_view_song_temp_title);
            artistName = (TextView)itemView.findViewById(R.id.album_view_song_temp_artist);
            trackNumber = (TextView)itemView.findViewById(R.id.album_view_song_temp_track_number);
            itemView.setOnClickListener(AlbumViewHolder.this);

        }

        @Override
        public void onClick(View v) {
            if (itemClickListener != null) {
                itemClickListener.onItemClick(v, getPosition());
            }
        }
    }


    public static class AlbumTitleViewHolder extends RecyclerView.ViewHolder{
        TextView albumTitle, artistName, albumInfo;
        RoundedImageView artistIcon;

        AlbumTitleViewHolder(View itemView) {
            super(itemView);
            albumTitle = (TextView) itemView.findViewById(R.id.album_view_title);
            artistName = (TextView) itemView.findViewById(R.id.album_view_artist);
            albumInfo = (TextView) itemView.findViewById(R.id.album_view_album_info);
            artistIcon = (RoundedImageView) itemView.findViewById(R.id.album_view_artist_icon);

        }
    }

    public AlbumViewSongListAdapter(Context c, ArrayList<Object> songs){
        this.songs = songs;
        inflater = LayoutInflater.from(c);
        mainActivity = (MainActivity) c;
        font = Typeface.createFromAsset(c.getAssets(), "Roboto-Regular.ttf");
        titleFont = Typeface.createFromAsset(c.getAssets(), "boldFont.ttf");
    }

    @Override
    public int getItemViewType(int position) {

        if(songs.get(position) instanceof String){

            albumName = (String) songs.get(position);
            return TITLE;

        } else if (songs.get(position) instanceof Song){

            return SONG;

        }

        return super.getItemViewType(position);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        RecyclerView.ViewHolder viewHolder = null;

        switch (viewType){

            case TITLE :

                View titleView = inflater.inflate(R.layout.fragment_album_view_title, parent, false);
                viewHolder = new AlbumTitleViewHolder(titleView);

                break;

            case SONG :

                LinearLayout linearLayout = (LinearLayout)inflater.inflate
                        (R.layout.album_view_song_temp, parent, false);

                viewHolder = new AlbumViewHolder(linearLayout);

                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {

        switch (holder.getItemViewType()){

            case TITLE :

                AlbumTitleViewHolder albumTitleViewHolder = (AlbumTitleViewHolder) holder;

                Album album = God.getAlbumFromName(albumName);

                albumTitleViewHolder.albumTitle.setText(album.getName());
                albumTitleViewHolder.artistName.setText(album.getArtist());
                albumTitleViewHolder.albumInfo.setText(album.getSongs().size() + " songs");

                albumTitleViewHolder.albumTitle.setTypeface(titleFont);
                albumTitleViewHolder.artistName.setTypeface(font);
                albumTitleViewHolder.albumInfo.setTypeface(font);


                Picasso.with(mainActivity).load(God.getArtistFromName(album.getArtist()).getCoverArt())
                        .into(albumTitleViewHolder.artistIcon);

                break;

            case SONG :

                AlbumViewHolder albumViewHolder = (AlbumViewHolder) holder;
                albumViewHolder.songName.setTypeface(font);
                albumViewHolder.artistName.setTypeface(font);
                albumViewHolder.trackNumber.setTypeface(font);
                final Song currSong = (Song) songs.get(position);
                albumViewHolder.songName.setText(currSong.getTitle());
                albumViewHolder.artistName.setText(currSong.getArtist());
                albumViewHolder.trackNumber.setText(currSong.getTrackNumber() - 1000 + "");

                break;
        }


    }

    @Override
    public int getItemCount() {
        return songs.size();
    }

    public interface OnItemClickListener {
        public void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(AlbumViewSongListAdapter.OnItemClickListener mItemClickListener) {
        this.itemClickListener = mItemClickListener;
    }
}
