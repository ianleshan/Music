package verendus.leshan.music.fragments;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.ArrayList;

import verendus.leshan.music.MainActivity;
import verendus.leshan.music.R;
import verendus.leshan.music.adapters.GenreViewSongListAdapter;
import verendus.leshan.music.objects.Genre;
import verendus.leshan.music.objects.God;
import verendus.leshan.music.views.SquaredImageView;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link GenreViewFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link GenreViewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GenreViewFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static Genre genre;
    private MainActivity mainActivity;

    private OnFragmentInteractionListener mListener;

    FloatingActionButton fab;
    RecyclerView recyclerView;
    SquaredImageView imageView;
    Toolbar toolbar;
    CollapsingToolbarLayout collapsingToolbarLayout;

    public GenreViewFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static GenreViewFragment newInstance(int position) {
        GenreViewFragment fragment = new GenreViewFragment();
        Bundle args = new Bundle();
        args.putInt("genre", position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        if (getArguments() != null) {
            mainActivity = (MainActivity) getActivity();
            genre = mainActivity.getGod().getGenres().get(getArguments().getInt("genre"));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_album_view, container, false);

        fab = (FloatingActionButton) rootView.findViewById(R.id.album_view_fab);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.album_view_song_list);
        imageView = (SquaredImageView) rootView.findViewById(R.id.album_view_album_art);
        toolbar = (Toolbar) rootView.findViewById(R.id.album_view_toolbar);
        collapsingToolbarLayout = (CollapsingToolbarLayout) rootView.findViewById(R.id.album_view_collapsing_toolbar_layout);

        mainActivity.setSupportActionBar(toolbar);
        toolbar.setTitle("");
        mainActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mainActivity.getImageLoader().displayImage(genre.getSongs().get(0).getCoverArt(), imageView, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {

            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

            }

            @Override
            public void onLoadingComplete(String imageUri, View view, final Bitmap loadedImage) {



                Palette.PaletteAsyncListener paletteListener = new Palette.PaletteAsyncListener() {
                    public void onGenerated(Palette palette) {



                        if (palette.getVibrantSwatch() != null) {

                            setColorsAccordingToSwatch(palette.getVibrantSwatch() , fab, collapsingToolbarLayout);

                        } else if (palette.getMutedSwatch() != null) {
                            setColorsAccordingToSwatch(palette.getMutedSwatch() , fab, collapsingToolbarLayout);


                        } else if (palette.getLightVibrantSwatch() != null) {
                            setColorsAccordingToSwatch(palette.getLightVibrantSwatch() , fab, collapsingToolbarLayout);


                        } else if (palette.getDarkVibrantSwatch() != null) {
                            setColorsAccordingToSwatch(palette.getDarkVibrantSwatch() , fab, collapsingToolbarLayout);


                        } else if (palette.getLightMutedSwatch() != null) {
                            setColorsAccordingToSwatch(palette.getLightMutedSwatch() , fab, collapsingToolbarLayout);

                        } else if (palette.getDarkMutedSwatch() != null) {
                            setColorsAccordingToSwatch(palette.getDarkMutedSwatch() , fab, collapsingToolbarLayout);

                        }
                    }
                };

                if (loadedImage != null && !loadedImage.isRecycled()) {
                    Palette.from(loadedImage).generate(paletteListener);
                }
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {

            }
        });

        GenreViewSongListAdapter.OnItemClickListener songItemClickListener = new GenreViewSongListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                int realPosition = position - 1;
                mainActivity.setQueueAndPlaySong(genre.getSongs(), realPosition);

            }
        };

        ArrayList<Object> objectArrayList = new ArrayList<>();
        objectArrayList.add(genre.getName());
        objectArrayList.addAll(genre.getSongs());

        GridLayoutManager llm = new GridLayoutManager(inflater.getContext(), 1);
        recyclerView.setLayoutManager(llm);
        recyclerView.setHasFixedSize(true);
        GenreViewSongListAdapter recyclerViewAdapter = new GenreViewSongListAdapter(inflater.getContext(), objectArrayList);
        recyclerViewAdapter.setOnItemClickListener(songItemClickListener);
        recyclerView.setAdapter(recyclerViewAdapter);




        Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "font.ttf");
        Typeface titleFont = Typeface.createFromAsset(getActivity().getAssets(), "titleFont.ttf");

        collapsingToolbarLayout.setCollapsedTitleTypeface(font);
        collapsingToolbarLayout.setExpandedTitleTypeface(font);

        God.overrideFonts(rootView, font);
        God.overrideFonts(toolbar, titleFont);

        return rootView;
    }

    private void setColorsAccordingToSwatch(Palette.Swatch swatch, FloatingActionButton fab, CollapsingToolbarLayout collapsingToolbarLayout) {

        mainActivity.getGod().setLibraryStatusBarColor(swatch.getRgb());
        fab.setBackgroundTintList(ColorStateList.valueOf(swatch.getRgb()));
        collapsingToolbarLayout.setContentScrimColor(swatch.getRgb());
        collapsingToolbarLayout.setCollapsedTitleTextColor(swatch.getTitleTextColor());
        collapsingToolbarLayout.setStatusBarScrimColor(swatch.getRgb());

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);/**
         if (context instanceof OnFragmentInteractionListener) {
         mListener = (OnFragmentInteractionListener) context;
         } else {
         throw new RuntimeException(context.toString()
         + " must implement OnFragmentInteractionListener");
         }**/
    }

    @Override
    public void onDetach() {
        mainActivity.getGod().setLibraryStatusBarColor(Color.parseColor("#00000000"));
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
