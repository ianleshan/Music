package verendus.leshan.music.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import verendus.leshan.music.R;
import verendus.leshan.music.objects.Album;
import verendus.leshan.music.views.SquaredImageView;

/**
 * Created by leshan on 9/3/15.
 */
public class AlbumListAdapter extends RecyclerView.Adapter<AlbumListAdapter.AlbumViewHolder> {

    ArrayList<Album> albums;
    static Album album;
    LayoutInflater inflater;
    Typeface font;
    static OnItemClickListener itemClickListener;
    ImageView iv;
    static AlbumViewHolder albumViewHolder;
    int position;
    AppCompatActivity appCompatActivity;


    public static class AlbumViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView albumName, artistName;
        CardView cardView;
        SquaredImageView albumCover;

        AlbumViewHolder(View itemView) {
            super(itemView);
            albumName = (TextView) itemView.findViewById(R.id.album_temp_album);
            artistName = (TextView) itemView.findViewById(R.id.album_temp_artist);
            albumCover = (SquaredImageView) itemView.findViewById(R.id.album_temp_art);
            cardView = (CardView) itemView.findViewById(R.id.album_temp_card_view);
            itemView.setOnClickListener(AlbumViewHolder.this);

        }

        public SquaredImageView getAlbumCover() {
            return albumCover;
        }

        @Override
        public void onClick(View v) {
            if (itemClickListener != null) {
                itemClickListener.onItemClick(albumCover, getPosition());
            }
        }
    }

    public AlbumListAdapter(Context c, ArrayList<Album> albums) {
        this.albums = albums;
        this.appCompatActivity = (AppCompatActivity) c;
        inflater = LayoutInflater.from(c);
        font = Typeface.createFromAsset(c.getAssets(), "boldFont.ttf");
    }

    @Override
    public AlbumViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        CardView cardView = (CardView) inflater.inflate
                (R.layout.album_temp, parent, false);
        AlbumViewHolder recyclerViewHolder = new AlbumViewHolder(cardView);
        return recyclerViewHolder;
    }

    @Override
    public void onBindViewHolder(final AlbumViewHolder holder, final int position) {
        //if (getColorArt != null) getColorArt.cancel(true);
        album = albums.get(position);
        this.albumViewHolder = holder;
        this.position = position;

        holder.itemView.setTag(position);
        holder.cardView.setCardBackgroundColor(appCompatActivity.getResources().getColor(R.color.card_background));
        holder.albumName.setTextColor(appCompatActivity.getResources().getColor(R.color.text_color));
        holder.artistName.setTextColor(appCompatActivity.getResources().getColor(R.color.detail_color));
        holder.albumName.setTypeface(font);
        holder.artistName.setTypeface(font);
        holder.albumName.setText(album.getName());
        holder.artistName.setText(album.getArtist());
        holder.albumCover.setImageBitmap(null);
        holder.albumCover.setSampleSize(holder.itemView.getWidth());
        if (album.getCoverArt() == null)
            album.setCoverArt("drawable://" + R.drawable.sample_art);
        //Log.d("TAAAAAAAAAG!!", album.getCoverArt());

        /*Target target = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                holder.albumCover.setImageBitmap(bitmap);

                Palette.PaletteAsyncListener paletteListener = new Palette.PaletteAsyncListener() {
                    public void onGenerated(Palette palette) {


                        if (palette.getVibrantSwatch() != null) {

                            setColorsAccordingToSwatch(palette.getVibrantSwatch() ,holder);

                        } else if (palette.getMutedSwatch() != null) {
                            setColorsAccordingToSwatch(palette.getMutedSwatch() ,holder);


                        } else if (palette.getLightVibrantSwatch() != null) {
                            setColorsAccordingToSwatch(palette.getLightVibrantSwatch() ,holder);


                        } else if (palette.getDarkVibrantSwatch() != null) {
                            setColorsAccordingToSwatch(palette.getDarkVibrantSwatch() ,holder);


                        } else if (palette.getLightMutedSwatch() != null) {
                            setColorsAccordingToSwatch(palette.getLightMutedSwatch() ,holder);

                        } else if (palette.getDarkMutedSwatch() != null) {
                            setColorsAccordingToSwatch(palette.getDarkMutedSwatch() ,holder);


                        }
                    }
                };

                if (bitmap != null && !bitmap.isRecycled()) {
                    Palette.from(bitmap).generate(paletteListener);
                }
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        };*/

        ViewCompat.setTransitionName(holder.albumCover, String.valueOf(position) + "_image");


        Picasso.with(appCompatActivity)
                .load(album.getCoverArt())
                .error(R.drawable.sample_art)
                .into(holder.albumCover);

        holder.albumCover.setMinimumHeight(holder.albumCover.getMeasuredWidth());
    }

    private void setColorsAccordingToSwatch(Palette.Swatch swatch, AlbumViewHolder holder) {

        holder.cardView.setCardBackgroundColor(swatch.getRgb());
        holder.albumName.setTextColor(swatch.getTitleTextColor());
        holder.artistName.setTextColor(swatch.getBodyTextColor());

    }



    @Override
    public int getItemCount() {
        return albums.size();
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(AlbumListAdapter.OnItemClickListener mItemClickListener) {
        this.itemClickListener = mItemClickListener;
    }

    @Override
    public void onViewDetachedFromWindow(AlbumViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        //Log.d("MESSAGE : ", "View detached");
        //if (getColorArt != null) getColorArt.cancel(true);
    }
}
