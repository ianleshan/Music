package verendus.leshan.music.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import verendus.leshan.music.R;
import verendus.leshan.music.objects.Song;

/**
 * Created by leshan on 9/1/15.
 */
public class SongListAdapter extends RecyclerView.Adapter<SongListAdapter.RecyclerViewHolder> {

    ArrayList<Song> songs;
    LayoutInflater inflater;
    Typeface font;
    Context context;
    static OnItemClickListener itemClickListener;


    public static class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView songView, artistView;
        ImageView albumArt;

        RecyclerViewHolder(View itemView) {
            super(itemView);
            songView = (TextView) itemView.findViewById(R.id.title);
            artistView = (TextView) itemView.findViewById(R.id.artist);
            albumArt = (ImageView) itemView.findViewById(R.id.list_thumb);
            itemView.setOnClickListener(RecyclerViewHolder.this);

        }

        @Override
        public void onClick(View v) {
            if (itemClickListener != null) {
                itemClickListener.onItemClick(v, getPosition());
            }
        }
    }

    public SongListAdapter(Context c, ArrayList<Song> songs) {
        this.songs = songs;
        inflater = LayoutInflater.from(c);
        context = c;
        font = Typeface.createFromAsset(c.getAssets(), "Roboto-Regular.ttf");
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LinearLayout linearLayout = (LinearLayout) inflater.inflate
                (R.layout.song_temp, parent, false);

        /*MaterialRippleLayout layout = MaterialRippleLayout.on(linearLayout)
                .rippleAlpha(0.4f)
                .rippleColor(0xFF585858)
                .rippleOverlay(true)
                .rippleDuration(500)
                .rippleDelayClick(false)
                .create();*/

        RecyclerViewHolder recyclerViewHolder;
        recyclerViewHolder = new RecyclerViewHolder(linearLayout);


        return recyclerViewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {
        holder.songView.setTypeface(font);
        holder.artistView.setTypeface(font);
        //get song using position
        Song currSong = songs.get(position);
        //get title and artist strings
        holder.songView.setText(currSong.getTitle());
        holder.artistView.setText(currSong.getArtist());
        holder.albumArt.setImageBitmap(null);
        /*if(currSong.getAlbum() != null){
            imageLoader.displayImage(currSong.getAlbum().getCoverArt(), holder.albumArt);
        }else {
            imageLoader.displayImage(currSong.getCoverArt(), holder.albumArt);
        }*/
        Picasso.with(context)
                .load(currSong.getCoverArt())
                .error(R.drawable.sample_art)
                .into(holder.albumArt);
        //imageLoader.displayImage(currSong.getCoverArt(), holder.albumArt);
        //set position as tag
        holder.itemView.setTag(position);
        //return songLay;


    }

    @Override
    public int getItemCount() {
        return songs.size();
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.itemClickListener = mItemClickListener;
    }
}
