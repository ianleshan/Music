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

    MainActivity mainActivity;


    public static class AlbumViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView songName;
        TextView artist;

        AlbumViewHolder(View itemView) {
            super(itemView);
            songName = (TextView) itemView.findViewById(R.id.album_view_song_temp_title);
            artist = (TextView) itemView.findViewById(R.id.album_view_song_temp_artist);
            itemView.setOnClickListener(AlbumViewHolder.this);

        }

        @Override
        public void onClick(View v) {
            if (itemClickListener != null) {
                itemClickListener.onItemClick(v, getPosition());
            }
        }
    }

    public GenreViewSongListAdapter(Context c, ArrayList<Object> songs) {
        this.songs = songs;
        inflater = LayoutInflater.from(c);
        mainActivity = (MainActivity) c;
        font = Typeface.createFromAsset(c.getAssets(), "Roboto-Regular.ttf");
        titleFont = Typeface.createFromAsset(c.getAssets(), "boldFont.ttf");
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        RecyclerView.ViewHolder viewHolder = null;
        LinearLayout linearLayout = (LinearLayout) inflater.inflate
                (R.layout.album_view_song_temp, parent, false);


        viewHolder = new AlbumViewHolder(linearLayout);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {

        AlbumViewHolder albumViewHolder = (AlbumViewHolder) holder;
        albumViewHolder.songName.setTypeface(font);
        albumViewHolder.artist.setTypeface(font);
        final Song currSong = (Song) songs.get(position);
        albumViewHolder.songName.setText(currSong.getTitle());
        albumViewHolder.artist.setText(currSong.getArtist());


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
