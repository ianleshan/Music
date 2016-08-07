package verendus.leshan.music.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import verendus.leshan.music.R;
import verendus.leshan.music.objects.Playlist;

/**
 * Created by leshan on 9/1/15.
 */
public class PlaylistListAdapter extends RecyclerView.Adapter<PlaylistListAdapter.RecyclerViewHolder> {

    ArrayList<Playlist> playlists;
    LayoutInflater inflater;
    Typeface font;
    static OnItemClickListener itemClickListener;


    public static class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView genreName, genreCount;

        RecyclerViewHolder(View itemView) {
            super(itemView);
            genreName = (TextView) itemView.findViewById(R.id.playlist_list_temp_primary_text);
            genreCount = (TextView) itemView.findViewById(R.id.playlist_list_temp_secondary_text);
            itemView.setOnClickListener(RecyclerViewHolder.this);

        }

        @Override
        public void onClick(View v) {
            if (itemClickListener != null) {
                itemClickListener.onItemClick(v, getPosition());
            }
        }
    }

    public PlaylistListAdapter(Context c, ArrayList<Playlist> playlists) {
        this.playlists = playlists;
        inflater = LayoutInflater.from(c);
        font = Typeface.createFromAsset(c.getAssets(), "boldFont.ttf");
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LinearLayout linearLayout = (LinearLayout) inflater.inflate
                (R.layout.playlist_list_temp, parent, false);

        RecyclerViewHolder recyclerViewHolder = new RecyclerViewHolder(linearLayout);


        return recyclerViewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {
        holder.genreName.setTypeface(font);
        holder.genreCount.setTypeface(font);
        Playlist currPlaylist = playlists.get(position);
        holder.genreName.setText(currPlaylist.getName());
        holder.genreCount.setText(currPlaylist.getSongs().size() + " songs");
        holder.itemView.setTag(position);


    }

    @Override
    public int getItemCount() {
        return playlists.size();
    }

    public interface OnItemClickListener {
        public void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.itemClickListener = mItemClickListener;
    }
}
