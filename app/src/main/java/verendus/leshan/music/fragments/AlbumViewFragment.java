package verendus.leshan.music.fragments;

import android.animation.Animator;
import android.animation.ObjectAnimator;
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
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;

import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.ArrayList;

import verendus.leshan.music.objects.Album;
import verendus.leshan.music.objects.God;
import verendus.leshan.music.MainActivity;
import verendus.leshan.music.R;
import verendus.leshan.music.views.SquaredImageView;
import verendus.leshan.music.adapters.AlbumViewSongListAdapter;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AlbumViewFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AlbumViewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AlbumViewFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static Album album;
    private MainActivity mainActivity;

    private OnFragmentInteractionListener mListener;

    FloatingActionButton fab;
    RecyclerView recyclerView;
    SquaredImageView imageView;
    Toolbar toolbar;
    CollapsingToolbarLayout collapsingToolbarLayout;
    View rootView;
    Interpolator interpolator = new AccelerateDecelerateInterpolator();

    int startingX, startingY, width;

    public AlbumViewFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static AlbumViewFragment newInstance(int position, int x, int y, int width) {
        AlbumViewFragment fragment = new AlbumViewFragment();
        Bundle args = new Bundle();
        args.putInt("album", position);
        args.putInt("x", x);
        args.putInt("y", y);
        args.putInt("w", width);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        if (getArguments() != null) {
            mainActivity = (MainActivity) getActivity();
            album = mainActivity.getGod().getAlbums().get(getArguments().getInt("album"));
            startingX = getArguments().getInt("x");
            startingY = getArguments().getInt("y");
            width = getArguments().getInt("w");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_album_view, container, false);
        rootView.setVisibility(View.GONE);

        fab = (FloatingActionButton) rootView.findViewById(R.id.album_view_fab);
        fab.setVisibility(View.GONE);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.album_view_song_list);
        recyclerView.setVisibility(View.GONE);
        imageView = (SquaredImageView) rootView.findViewById(R.id.album_view_album_art);
        toolbar = (Toolbar) rootView.findViewById(R.id.album_view_toolbar);
        collapsingToolbarLayout = (CollapsingToolbarLayout) rootView.findViewById(R.id.album_view_collapsing_toolbar_layout);

        mainActivity.setSupportActionBar(toolbar);
        toolbar.setTitle("");
        mainActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        AlbumViewSongListAdapter.OnItemClickListener songItemClickListener = new AlbumViewSongListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                int realPosition = position - 1;
                mainActivity.setQueueAndPlaySong(album.getSongs(), realPosition);

            }
        };

        ArrayList<Object> objectArrayList = new ArrayList<>();
        objectArrayList.add(album.getName());
        objectArrayList.addAll(album.getSongs());

        GridLayoutManager llm = new GridLayoutManager(inflater.getContext(), 1);
        recyclerView.setLayoutManager(llm);
        recyclerView.setHasFixedSize(true);
        AlbumViewSongListAdapter recyclerViewAdapter = new AlbumViewSongListAdapter(inflater.getContext(), objectArrayList);
        recyclerViewAdapter.setOnItemClickListener(songItemClickListener);
        recyclerView.setAdapter(recyclerViewAdapter);




        Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "font.ttf");
        Typeface titleFont = Typeface.createFromAsset(getActivity().getAssets(), "titleFont.ttf");

        collapsingToolbarLayout.setCollapsedTitleTypeface(font);
        collapsingToolbarLayout.setExpandedTitleTypeface(font);

        God.overrideFonts(rootView, font);
        God.overrideFonts(toolbar, titleFont);


        mainActivity.getImageLoader().displayImage(album.getCoverArt(), imageView, new ImageLoadingListener() {
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
                        enterAnimation();
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

        return rootView;
    }

    private void setColorsAccordingToSwatch(Palette.Swatch swatch, FloatingActionButton fab, CollapsingToolbarLayout collapsingToolbarLayout) {

        mainActivity.getGod().setLibraryStatusBarColor(swatch.getRgb());
        fab.setBackgroundTintList(ColorStateList.valueOf(swatch.getRgb()));
        collapsingToolbarLayout.setContentScrimColor(swatch.getRgb());
        collapsingToolbarLayout.setCollapsedTitleTextColor(swatch.getTitleTextColor());
        collapsingToolbarLayout.setStatusBarScrimColor(swatch.getRgb());

    }

    private void enterAnimation(){
        rootView.setPivotX(0);
        rootView.setPivotY(0);
        ObjectAnimator moveX = ObjectAnimator.ofFloat(rootView, View.TRANSLATION_X, startingX - width/2, 0);
        ObjectAnimator moveY = ObjectAnimator.ofFloat(rootView, View.TRANSLATION_Y, startingY - width/2, 0);

        WindowManager wm = (WindowManager) getView().getContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();

        float width2 = (float) display.getWidth();

        Log.d("WIDTH :", width+"");
        Log.d("WIDTH2 :", width2+"");
        Log.d("XXX :", startingX+"");
        Log.d("YYY :", startingY+"");

        ObjectAnimator scaleX = ObjectAnimator.ofFloat(rootView, View.SCALE_X, (width / width2), 1);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(rootView, View.SCALE_Y, (width / width2), 1);

        scaleY.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                fab.setVisibility(View.VISIBLE);
                fab.show();
                recyclerView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        int duration = 3000;
        moveX.setDuration(duration);
        moveY.setDuration(duration);
        scaleX.setDuration(duration);
        scaleY.setDuration(duration);

        moveX.setInterpolator(interpolator);
        moveY.setInterpolator(interpolator);
        scaleX.setInterpolator(interpolator);
        scaleY.setInterpolator(interpolator);

        rootView.setVisibility(View.VISIBLE);
        moveX.start();
        moveY.start();
        scaleX.start();
        scaleY.start();
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
