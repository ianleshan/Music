package verendus.leshan.music.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
public class ArtistViewSongListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    ArrayList<Object> songs;
    LayoutInflater inflater;
    Typeface font, titleFont;
    static OnItemClickListener itemClickListener;

    private final int TITLE = 0, SONG = 1, ALBUM = 2;
    MainActivity mainActivity;


    public static class AlbumTitleViewHolder extends RecyclerView.ViewHolder{
        TextView albumTitle;

        AlbumTitleViewHolder(View itemView) {
            super(itemView);
            albumTitle = (TextView) itemView.findViewById(R.id.artist_view_title);

        }
    }


    public static class ArtistSongHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView songName;
        TextView artistName;
        ImageView albumArt;

        ArtistSongHolder(View itemView) {
            super(itemView);
            songName = (TextView)itemView.findViewById(R.id.title);
            artistName = (TextView)itemView.findViewById(R.id.artist);
            albumArt = (ImageView)itemView.findViewById(R.id.list_thumb);
            itemView.setOnClickListener(ArtistSongHolder.this);

        }

        @Override
        public void onClick(View v) {
            if (itemClickListener != null) {
                itemClickListener.onItemClick(v, getPosition());
            }
        }
    }

    public static class ArtistAlbumHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView albumName;
        TextView info;
        ImageView albumArt;

        ArtistAlbumHolder(View itemView) {
            super(itemView);
            albumName = (TextView)itemView.findViewById(R.id.album_temp_album);
            info = (TextView)itemView.findViewById(R.id.album_temp_artist);
            albumArt = (ImageView) itemView.findViewById(R.id.album_temp_art);
            itemView.setOnClickListener(ArtistAlbumHolder.this);

        }

        @Override
        public void onClick(View v) {
            if (itemClickListener != null) {
                itemClickListener.onItemClick(v, getPosition());
            }
        }
    }

    public ArtistViewSongListAdapter(Context c, ArrayList<Object> songs){
        this.songs = songs;
        inflater = LayoutInflater.from(c);
        mainActivity = (MainActivity) c;
        font = Typeface.createFromAsset(c.getAssets(), "Roboto-Regular.ttf");
        titleFont = Typeface.createFromAsset(c.getAssets(), "boldFont.ttf");
    }

    @Override
    public int getItemViewType(int position) {

        if(songs.get(position) instanceof String){
            return TITLE;

        } else if (songs.get(position) instanceof Song){

            return SONG;

        }else if (songs.get(position) instanceof Album){

            return ALBUM;

        }

        return super.getItemViewType(position);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        RecyclerView.ViewHolder viewHolder = null;

        switch (viewType){

            case TITLE :

                View titleView = inflater.inflate(R.layout.fragment_artist_view_title, parent, false);
                viewHolder = new AlbumTitleViewHolder(titleView);

                break;

            case SONG :

                LinearLayout songLayout = (LinearLayout)inflater.inflate
                        (R.layout.song_temp, parent, false);

                viewHolder = new ArtistSongHolder(songLayout);

                break;

            case ALBUM :

                CardView albumLayout = (CardView) inflater.inflate
                        (R.layout.album_temp, parent, false);

                viewHolder = new ArtistAlbumHolder(albumLayout);

                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {

        switch (holder.getItemViewType()){

            case TITLE :

                AlbumTitleViewHolder albumTitleViewHolder = (AlbumTitleViewHolder) holder;
                albumTitleViewHolder.itemView.setTag("title");
                albumTitleViewHolder.albumTitle.setText((String)songs.get(position));
                albumTitleViewHolder.albumTitle.setTypeface(titleFont);

                break;

            case SONG :

                ArtistSongHolder artistSongHolder = (ArtistSongHolder) holder;
                artistSongHolder.itemView.setTag("song");
                artistSongHolder.songName.setTypeface(font);
                artistSongHolder.artistName.setTypeface(font);
                final Song currSong = (Song) songs.get(position);
                artistSongHolder.songName.setText(currSong.getTitle());
                artistSongHolder.artistName.setText(currSong.getArtist());
                Picasso.with(mainActivity)
                        .load(currSong.getCoverArt())
                        .into(artistSongHolder.albumArt);

                break;

            case ALBUM :

                ArtistAlbumHolder artistAlbumHolder = (ArtistAlbumHolder) holder;
                artistAlbumHolder.itemView.setTag("album");
                artistAlbumHolder.albumName.setTypeface(font);
                artistAlbumHolder.info.setTypeface(font);
                final Album currAlbum = (Album) songs.get(position);
                artistAlbumHolder.albumName.setText(currAlbum.getName());
                artistAlbumHolder.info.setText(currAlbum.getArtist());
                Picasso.with(mainActivity)
                        .load(currAlbum.getCoverArt())
                        .error(R.drawable.sample_art)
                        .into(artistAlbumHolder.albumArt);
        }


    }

    @Override
    public int getItemCount() {
        return songs.size();
    }

    public interface OnItemClickListener {
        public void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(ArtistViewSongListAdapter.OnItemClickListener mItemClickListener) {
        this.itemClickListener = mItemClickListener;
    }
}
