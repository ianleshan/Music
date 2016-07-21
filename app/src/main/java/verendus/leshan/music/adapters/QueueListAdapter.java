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

import com.balysv.materialripple.MaterialRippleLayout;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import verendus.leshan.music.R;
import verendus.leshan.music.objects.Song;

/**
 * Created by leshan on 9/1/15.
 */
public class QueueListAdapter extends RecyclerView.Adapter<QueueListAdapter.RecyclerViewHolder> {

    ArrayList<Song> songs;
    LayoutInflater inflater;
    Typeface font;
    ImageLoader imageLoader;
    Context context;
    static OnItemClickListener itemClickListener;


    public static class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView songTitle, artist;
        ImageView albumArt;

        RecyclerViewHolder(View itemView) {
            super(itemView);
            songTitle = (TextView) itemView.findViewById(R.id.queue_list_temp_primary_text);
            artist = (TextView) itemView.findViewById(R.id.queue_list_temp_secondary_text);
            albumArt = (ImageView) itemView.findViewById(R.id.queue_list_thumb);
            itemView.setOnClickListener(RecyclerViewHolder.this);

        }

        @Override
        public void onClick(View v) {
            if (itemClickListener != null) {
                itemClickListener.onItemClick(v, getPosition());
            }
        }
    }

    public QueueListAdapter(Context c, ArrayList<Song> songs, ImageLoader imageLoader) {
        this.imageLoader = imageLoader;
        this.songs = songs;
        inflater = LayoutInflater.from(c);
        context = c;
        font = Typeface.createFromAsset(c.getAssets(), "Roboto-Regular.ttf");
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LinearLayout linearLayout = (LinearLayout) inflater.inflate
                (R.layout.queue_list_temp, parent, false);

        RecyclerViewHolder recyclerViewHolder = new RecyclerViewHolder(linearLayout);


        return recyclerViewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {
        holder.songTitle.setTypeface(font);
        holder.artist.setTypeface(font);
        Song currSong = songs.get(position);
        holder.songTitle.setText(currSong.getTitle());
        holder.artist.setText(currSong.getArtist());
        Picasso.with(context).load(currSong.getCoverArt()).into(holder.albumArt);
        holder.itemView.setTag(position);


    }

    @Override
    public int getItemCount() {
        return songs.size();
    }

    public interface OnItemClickListener {
        public void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.itemClickListener = mItemClickListener;
    }
}
