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

import com.balysv.materialripple.MaterialRippleLayout;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;

import verendus.leshan.music.MainActivity;
import verendus.leshan.music.R;
import verendus.leshan.music.objects.Genre;
import verendus.leshan.music.objects.God;
import verendus.leshan.music.objects.Song;

/**
 * Created by leshan on 9/3/15.
 */
public class GenreViewSongListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    ArrayList<Object> songs;
    LayoutInflater inflater;
    Typeface font, titleFont;
    static OnItemClickListener itemClickListener;

    private final int TITLE = 0, SONG = 1;
    MainActivity mainActivity;
    private String albumName = "";



    public static class AlbumViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView songName;

        AlbumViewHolder(View itemView) {
            super(itemView);
                    songName = (TextView)itemView.findViewById(R.id.album_view_song_temp_title);
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

    public GenreViewSongListAdapter(Context c, ArrayList<Object> songs){
        this.songs = songs;
        inflater = LayoutInflater.from(c);
        mainActivity = (MainActivity) c;
        font = Typeface.createFromAsset(c.getAssets(), "font.ttf");
        titleFont = Typeface.createFromAsset(c.getAssets(), "titleFont.ttf");
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

                MaterialRippleLayout layout = MaterialRippleLayout.on(linearLayout)
                        .rippleAlpha(0.2f)
                        .rippleColor(0xFF585858)
                        .rippleOverlay(true)
                        .rippleDuration(200)
                        .rippleDelayClick(false)
                        .create();

                viewHolder = new AlbumViewHolder(layout);

                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {

        switch (holder.getItemViewType()){

            case TITLE :

                AlbumTitleViewHolder albumTitleViewHolder = (AlbumTitleViewHolder) holder;

                Genre genre = God.getGenreFromName(albumName);

                albumTitleViewHolder.albumTitle.setText(genre.getName());
                albumTitleViewHolder.artistName.setVisibility(View.GONE);
                albumTitleViewHolder.albumInfo.setText(genre.getSongs().size() + " songs");

                albumTitleViewHolder.albumTitle.setTypeface(titleFont);
                albumTitleViewHolder.artistName.setTypeface(font);
                albumTitleViewHolder.albumInfo.setTypeface(font);

                Log.d("DONE" , "DONE");
                //mainActivity.getImageLoader().displayImage(God.getArtistFromName(genre.getArtist()).getCoverArt(), albumTitleViewHolder.artistIcon);


                break;

            case SONG :

                AlbumViewHolder albumViewHolder = (AlbumViewHolder) holder;
                albumViewHolder.songName.setTypeface(font);
                final Song currSong = (Song) songs.get(position);
                albumViewHolder.songName.setText(currSong.getTitle());

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

    public void setOnItemClickListener(GenreViewSongListAdapter.OnItemClickListener mItemClickListener) {
        this.itemClickListener = mItemClickListener;
    }
}