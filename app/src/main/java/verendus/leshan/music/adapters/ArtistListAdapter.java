package verendus.leshan.music.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

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
    ImageLoader imageLoader;
    ImageView iv;
    static OnItemClickListener itemClickListener;



    public static class AlbumViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView artistName;
        ImageView albumCover;
        CardView cardView;

        AlbumViewHolder(View itemView) {
            super(itemView);
                    artistName = (TextView)itemView.findViewById(R.id.artist_temp_name);
            albumCover = (ImageView)itemView.findViewById(R.id.artist_temp_art);
            cardView = (CardView) itemView.findViewById(R.id.artist_temp_card_view);
                    itemView.setOnClickListener(AlbumViewHolder.this);

        }

        @Override
        public void onClick(View v) {
            if (itemClickListener != null) {
                itemClickListener.onItemClick(v, getPosition());
            }
        }
    }

    public ArtistListAdapter(Context c, ArrayList<Artist> artists, ImageLoader imageLoader){
        this.imageLoader = imageLoader;
        this.artists = artists;
        inflater = LayoutInflater.from(c);
        context = c;
        font = Typeface.createFromAsset(c.getAssets(), "boldFont.ttf");
    }

    @Override
    public AlbumViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        CardView linearLayout = (CardView) inflater.inflate
                        (R.layout.artist_temp, parent, false);
        AlbumViewHolder recyclerViewHolder = new AlbumViewHolder(linearLayout);
        return recyclerViewHolder;
    }

    @Override
    public void onBindViewHolder(final AlbumViewHolder holder, int position) {


        holder.cardView.setCardBackgroundColor(Color.BLACK);
        holder.artistName.setTextColor(Color.LTGRAY);
        //holder.moreIcon.setColorFilter(Color.LTGRAY);

        holder.artistName.setTypeface(font);
        holder.artistName.setTypeface(font);
        final Artist currArtist = artists.get(position);
        holder.artistName.setText(currArtist.getName());
        holder.albumCover.setImageBitmap(null);


        //iv = holder.albumCover;

        //new RetrieveArtistFeedTask().execute(currArtist.getName());

        if(currArtist.getCoverArt() == null) currArtist.setCoverArt("drawable://" + R.drawable.sample_art);

        Target target = new Target() {
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
        };

        Picasso.with(context)
                .load(currArtist.getCoverArt())
                .into(target);
        holder.albumCover.setMinimumHeight(holder.albumCover.getMeasuredWidth());
        holder.itemView.setTag(position);
    }

    private void setColorsAccordingToSwatch(Palette.Swatch swatch, AlbumViewHolder holder) {

        holder.cardView.setCardBackgroundColor(swatch.getRgb());
        holder.artistName.setTextColor(swatch.getTitleTextColor());
        //holder.moreIcon.setColorFilter(Color.WHITE);


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

    public class RetrieveArtistFeedTask extends AsyncTask<String, Void, String> {

        protected String doInBackground(String... urls) {
            String albumArtUrl = null;
            String x = null;

                x = "http://ws.audioscrobbler.com/2.0/?method=artist.getinfo&artist="
                        + URLEncoder.encode(urls[0])
                        + "&api_key="
                        + "81f683d158289972f4532a1aefc70e48"
                        + "&limit=" + 1 + "&page=" + 1;
                Log.d("AAAERTIST", x);
            try {
                XMLParser parser = new XMLParser();
                String xml = parser.getXmlFromUrl(x); // getting XML from URL
                Document doc = parser.getDomElement(xml);
                NodeList nl = doc.getElementsByTagName("image");
                for (int i = 0; i < nl.getLength(); i++) {
                    Element e = (Element) nl.item(i);
                    Log.d("TAG", "Size = " + e.getAttribute("size") + " = " + parser.getElementValue(e));
                    if (e.getAttribute("size").contentEquals("mega")) {
                        albumArtUrl = parser.getElementValue(e);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return albumArtUrl;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if(s == null) s = "drawable://" + R.drawable.sample_art;
            Log.d("TAAAAAAAAAG!!", s);
            imageLoader.displayImage(s, iv, new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String imageUri, View view) {

                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    /**if(album.getAlbumColorArt()==null) {
                     ColorArt colorArt = new ColorArt(loadedImage);
                     album.setAlbumColorArt(colorArt);
                     holder.cardView.setCardBackgroundColor(album.getAlbumColorArt().getBackgroundColor());
                     holder.albumName.setTextColor(album.getAlbumColorArt().getPrimaryColor());
                     holder.artistName.setTextColor(album.getAlbumColorArt().getSecondaryColor());
                     }**/
                }

                @Override
                public void onLoadingCancelled(String imageUri, View view) {

                }
            });
        }
    }
}
