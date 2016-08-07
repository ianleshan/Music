package verendus.leshan.music.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.net.URLEncoder;
import java.util.ArrayList;

import verendus.leshan.music.objects.Artist;
import verendus.leshan.music.R;
import verendus.leshan.music.utils.XMLParser;

/**
 * Created by leshan on 9/3/15.
 */
public class ArtistListAdapter extends RecyclerView.Adapter<ArtistListAdapter.AlbumViewHolder> {

    ArrayList<Artist> artists;
    LayoutInflater inflater;
    Context context;
    Typeface font;
    ImageView iv;
    static OnItemClickListener itemClickListener;



    public static class AlbumViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView artistName;
        ImageView albumCover;
        RelativeLayout cardView;

        AlbumViewHolder(View itemView) {
            super(itemView);
                    artistName = (TextView)itemView.findViewById(R.id.artist_temp_name);
            albumCover = (ImageView)itemView.findViewById(R.id.artist_temp_art);
            cardView = (RelativeLayout) itemView.findViewById(R.id.artist_temp_layout_view);
                    itemView.setOnClickListener(AlbumViewHolder.this);

        }

        @Override
        public void onClick(View v) {
            if (itemClickListener != null) {
                itemClickListener.onItemClick(v, getPosition());
            }
        }
    }

    public ArtistListAdapter(Context c, ArrayList<Artist> artists){
        this.artists = artists;
        inflater = LayoutInflater.from(c);
        context = c;
        font = Typeface.createFromAsset(c.getAssets(), "boldFont.ttf");
    }

    @Override
    public AlbumViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        RelativeLayout linearLayout = (RelativeLayout) inflater.inflate
                        (R.layout.artist_temp, parent, false);
        AlbumViewHolder recyclerViewHolder = new AlbumViewHolder(linearLayout);
        return recyclerViewHolder;
    }

    @Override
    public void onBindViewHolder(final AlbumViewHolder holder, int position) {

        //holder.cardView.setBackgroundColor(context.getResources().getColor(R.color.card_background));
        //holder.albumName.setTextColor(context.getResources().getColor(R.color.text_color));
        holder.artistName.setTextColor(context.getResources().getColor(R.color.text_color));

        //holder.moreIcon.setColorFilter(Color.LTGRAY);

        holder.artistName.setTypeface(font);
        holder.artistName.setTypeface(font);
        final Artist currArtist = artists.get(position);
        holder.artistName.setText(currArtist.getName());
        holder.albumCover.setImageBitmap(null);


        //iv = holder.albumCover;

        //new RetrieveArtistFeedTask().execute(currArtist.getName());

        if(currArtist.getCoverArt() == null) currArtist.setCoverArt("drawable://" + R.drawable.sample_art);

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

        Picasso.with(context)
                .load(currArtist.getCoverArt())
                .error(R.drawable.sample_art)
                .into(holder.albumCover);
        holder.albumCover.setMinimumHeight(holder.albumCover.getMeasuredWidth());
        holder.itemView.setTag(position);
    }

    @Override
    public int getItemCount() {
        return artists.size();
    }

    public interface OnItemClickListener {
        public void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(ArtistListAdapter.OnItemClickListener mItemClickListener) {
        this.itemClickListener = mItemClickListener;
    }


}
