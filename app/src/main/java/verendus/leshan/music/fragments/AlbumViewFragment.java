package verendus.leshan.music.fragments;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.RectEvaluator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Interpolator;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;

import verendus.leshan.music.MainActivity;
import verendus.leshan.music.R;
import verendus.leshan.music.adapters.AlbumViewSongListAdapter;
import verendus.leshan.music.animation.TransitionHelper;
import verendus.leshan.music.objects.Album;
import verendus.leshan.music.objects.God;
import verendus.leshan.music.views.SquaredImageView;


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

    ObjectAnimator moveX, moveY, scaleX, scaleY, revealAnimation;

    public AlbumViewFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static AlbumViewFragment newInstance(int position) {
        AlbumViewFragment fragment = new AlbumViewFragment();
        Bundle args = new Bundle();
        args.putInt("album", position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        if (getArguments() != null) {
            mainActivity = (MainActivity) getActivity();
            mainActivity.disableNavBar();
            album = mainActivity.getGod().getAlbums().get(getArguments().getInt("album"));
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TransitionHelper.Transition albumTransition = TransitionHelper.getTransition(1);

        Rect rectangle = new Rect();
        Window window = getActivity().getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(rectangle);
        int statusBarHeight = rectangle.top;
        int contentViewTop =
                window.findViewById(Window.ID_ANDROID_CONTENT).getTop();
        int titleBarHeight= contentViewTop - statusBarHeight;

        float width = (float)getActivity().getWindowManager().getDefaultDisplay().getWidth();
        float height = (float)getActivity().getWindowManager().getDefaultDisplay().getHeight();

        /*Rect bounds = new Rect();
        view.getLocalVisibleRect(bounds);
        Rect from = new Rect(bounds);
        Rect to = new Rect(bounds);
        from.bottom = 0;//albumTransition.getHeight();
        from.left = 0;*/

        Rect to = new Rect(0, 0, (int)width, (int)height);
        Rect from = new Rect(to);
        to.bottom = albumTransition.getHeight() * 2;

        Interpolator interpolator = new FastOutSlowInInterpolator();

        moveX = ObjectAnimator.ofFloat(view, View.TRANSLATION_X, albumTransition.getX(), 0);
        moveY = ObjectAnimator.ofFloat(view, View.TRANSLATION_Y, albumTransition.getY() - statusBarHeight, 0);
        scaleX = ObjectAnimator.ofFloat(view, View.SCALE_X, (float)albumTransition.getWidth()/width, 1);
        scaleY = ObjectAnimator.ofFloat(view, View.SCALE_Y, (((float)albumTransition.getWidth()*height) / width) / height, 1);

        revealAnimation = ObjectAnimator.ofObject(view, "clipBounds", new verendus.leshan.music.views.RectEvaluator(), to, from);


        //String toast = " X : " + albumTransition.getX() +" Y : " + albumTransition.getY() +" W : " + albumTransition.getWidth() +" H : " + albumTransition.getHeight();
        //Toast.makeText(getContext(), toast, Toast.LENGTH_SHORT).show();

        int duration = 350;

        view.setPivotX(0);
        //view.setPivotY(0);

        moveX.setInterpolator(interpolator);
        moveY.setInterpolator(interpolator);
        scaleX.setInterpolator(interpolator);
        scaleY.setInterpolator(interpolator);
        revealAnimation.setInterpolator(interpolator);

        moveX.setDuration(duration);
        moveY.setDuration(duration);
        scaleX.setDuration(duration);
        scaleY.setDuration(duration);
        revealAnimation.setDuration((long) (duration * 1.5));


        moveX.start();
        moveY.start();
        scaleX.start();
        scaleY.start();
        revealAnimation.start();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        setHasOptionsMenu(false);
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_album_view, container, false);

        fab = (FloatingActionButton) rootView.findViewById(R.id.album_view_fab);
        //fab.hide();
        recyclerView = (RecyclerView) rootView.findViewById(R.id.album_view_song_list);
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




        Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "Roboto-Regular.ttf");
        Typeface titleFont = Typeface.createFromAsset(getActivity().getAssets(), "boldFont.ttf");

        collapsingToolbarLayout.setCollapsedTitleTypeface(font);
        collapsingToolbarLayout.setExpandedTitleTypeface(font);

        God.overrideFonts(rootView, font);
        God.overrideFonts(toolbar, titleFont);

        Target target = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap loadedImage, Picasso.LoadedFrom from) {

                Palette.PaletteAsyncListener paletteListener = palette -> {



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
                };

                if (loadedImage != null && !loadedImage.isRecycled()) {
                    Palette.from(loadedImage).generate(paletteListener);
                }

                imageView.setImageBitmap(loadedImage);

            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        };

        Picasso.with(mainActivity)
                .load(album.getCoverArt())
                .error(R.drawable.sample_art)
                .into(target);

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

    public void onBackTriggered(){

        recyclerView.smoothScrollToPosition(0);
        fab.hide();

        moveX.reverse();
        moveY.reverse();
        scaleX.reverse();
        scaleY.reverse();
        revealAnimation.reverse();
        revealAnimation.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                getActivity().getSupportFragmentManager().beginTransaction().remove(AlbumViewFragment.this).commit();
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });

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
    public void onStop() {

        super.onStop();
    }

    @Override
    public void onDetach() {
        mainActivity.getGod().setLibraryStatusBarColor(mainActivity.getOptions().getPrimaryColor());
        mainActivity.enableNavBar();
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
