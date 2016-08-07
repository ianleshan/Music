package verendus.leshan.music.fragments;

import android.content.Context;
import android.content.res.ColorStateList;
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
    Toolbar toolbar;

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
            mainActivity.disableNavBar();
            genre = mainActivity.getGod().getGenres().get(getArguments().getInt("genre"));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_genre_view, container, false);

        fab = (FloatingActionButton) rootView.findViewById(R.id.genre_view_fab);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.genre_view_song_list);
        toolbar = (Toolbar) rootView.findViewById(R.id.genre_view_toolbar);

        mainActivity.setSupportActionBar(toolbar);
        toolbar.setTitle(genre.getName());

        int primary = mainActivity.getOptions().getPrimaryColor();
        int detail = mainActivity.getOptions().getDetailColor();
        int text = mainActivity.getOptions().getTextColor();

        toolbar.setBackgroundColor(primary);
        toolbar.setTitleTextColor(text);
        fab.setBackgroundTintList(ColorStateList.valueOf(detail));

        mainActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        GenreViewSongListAdapter.OnItemClickListener songItemClickListener = (view, position) ->
                mainActivity.setQueueAndPlaySong(genre.getSongs(), position);

        ArrayList<Object> objectArrayList = new ArrayList<>();
        objectArrayList.addAll(genre.getSongs());

        GridLayoutManager llm = new GridLayoutManager(inflater.getContext(), 1);
        recyclerView.setLayoutManager(llm);
        recyclerView.setHasFixedSize(true);
        GenreViewSongListAdapter recyclerViewAdapter = new GenreViewSongListAdapter(inflater.getContext(), objectArrayList);
        recyclerViewAdapter.setOnItemClickListener(songItemClickListener);
        recyclerView.setAdapter(recyclerViewAdapter);




        Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "Roboto-Regular.ttf");
        Typeface titleFont = Typeface.createFromAsset(getActivity().getAssets(), "boldFont.ttf");

        God.overrideFonts(rootView, font);
        God.overrideFonts(toolbar, titleFont);

        return rootView;
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
