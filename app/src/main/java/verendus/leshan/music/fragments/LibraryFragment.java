package verendus.leshan.music.fragments;

import android.annotation.TargetApi;
import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.graphics.ColorUtils;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.transition.ChangeBounds;
import android.transition.ChangeImageTransform;
import android.transition.ChangeTransform;
import android.transition.Explode;
import android.transition.Fade;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.transition.TransitionSet;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


import com.makeramen.roundedimageview.RoundedImageView;

import verendus.leshan.music.adapters.GenreListAdapter;
import verendus.leshan.music.adapters.PlaylistListAdapter;
import verendus.leshan.music.animation.TransitionHelper;
import verendus.leshan.music.objects.God;
import verendus.leshan.music.MainActivity;
import verendus.leshan.music.R;
import verendus.leshan.music.adapters.AlbumListAdapter;
import verendus.leshan.music.adapters.ArtistListAdapter;
import verendus.leshan.music.adapters.SongListAdapter;
import verendus.leshan.music.test.GridFragment;
import verendus.leshan.music.views.SquaredImageView;

import static android.transition.TransitionSet.ORDERING_TOGETHER;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link LibraryFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link LibraryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LibraryFragment extends Fragment {
    static God god;
    static MainActivity mainActivity;

    private OnFragmentInteractionListener mListener;

    private AppBarLayout appBar;
    private Toolbar toolbar;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private Typeface font, titleFont;
    static SongListAdapter.OnItemClickListener songItemClickListener;
    static AlbumListAdapter.OnItemClickListener albumItemClickListener;
    static GenreListAdapter.OnItemClickListener genreItemClickListener;
    static ArtistListAdapter.OnItemClickListener artistOnItemClickListener;

    private static final int NUM_OF_TABS = 5;
    private static final int SONGS = 0;
    private static final int ALBUMS = 1;
    private static final int ARTISTS = 2;
    private static final int PLAYLISTS = 4;
    private static final int GENRES = 3;
    private static final CharSequence[] TITLES = new CharSequence[]{"Songs", "Albums", "Artists", "Genres", "Playlists"};


    public LibraryFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static LibraryFragment newInstance() {
        LibraryFragment fragment = new LibraryFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = ((MainActivity) getActivity());
        god = mainActivity.getGod();
        mainActivity.setVolumeControlStream(AudioManager.STREAM_MUSIC);
    }

    MenuItem searchMenuItem;

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
        searchMenuItem = menu.getItem(0);
        Drawable drawable = searchMenuItem.getIcon();
        DrawableCompat.setTint(drawable, mainActivity.getResources().getColor(R.color.main_text_color));
        searchMenuItem.setIcon(drawable);

        updatePrimaryColor();
        updateDetailColor();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        mainActivity = ((MainActivity) getActivity());
        god = mainActivity.getGod();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        setHasOptionsMenu(true);
        View rootView = inflater.inflate(R.layout.fragment_library, container, false);
        final AppCompatActivity appCompatActivity = ((AppCompatActivity) getActivity());


        songItemClickListener = new SongListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                mainActivity.setQueueAndPlaySong(god.getSongs(), position);

            }
        };


        albumItemClickListener = new AlbumListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(final View viewHolder, int position) {

                //android.support.v4.app.FragmentManager fragmentManager = appCompatActivity.getSupportFragmentManager();
                //FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                AlbumViewFragment fragment = AlbumViewFragment.newInstance(position);

                TransitionHelper.setTransition(1, viewHolder);
                appCompatActivity.getSupportFragmentManager()
                        .beginTransaction()
                        .addSharedElement(viewHolder, "bar")
                        .replace(R.id.level1_fragment_container, fragment, "albumFragment")
                        .addToBackStack(null)
                        .commit();

            }
        };


        artistOnItemClickListener = new ArtistListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(final View view, int position) {

                android.support.v4.app.FragmentManager fragmentManager = appCompatActivity.getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                ArtistViewFragment fragment = ArtistViewFragment.newInstance(position);

                /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                    // Inflate transitions to apply
                    Transition changeTransform = TransitionInflater.from(appCompatActivity).inflateTransition(R.transition.change_image_transform);

                    Transition explodeTransform = TransitionInflater.from(appCompatActivity).inflateTransition(android.R.transition.explode);

                    // Setup enter transition on second fragment
                    fragment.setSharedElementEnterTransition(changeTransform);
                    fragment.setEnterTransition(explodeTransform);

                    // Setup exit transition on first fragment
                    LibraryFragment.this.setSharedElementReturnTransition(changeTransform);
                    LibraryFragment.this.setExitTransition(explodeTransform);

                    //fragmentTransaction.addSharedElement(holder.getIvAvatar(), getString(R.string.
                    //SquaredImageView imageView = (SquaredImageView) view.findViewById(R.id.artist_temp_art);
                    //fragmentTransaction.addSharedElement(imageView, getString(R.string.fragment_album_art));

                }*/


                RoundedImageView imageView = (RoundedImageView) view.findViewById(R.id.artist_temp_art);
                TransitionHelper.setTransition(2, imageView);
                fragmentTransaction.replace(R.id.level1_fragment_container, fragment, "artistFragment");
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();

                /*SquaredImageView imageView = (SquaredImageView) view.findViewById(R.id.album_temp_art);

                AlbumViewFragment fragment = AlbumViewFragment.newInstance(position);
                FragmentTransitionLauncher
                        .with(view.getContext())
                        .image(((BitmapDrawable)imageView.getDrawable()).getBitmap())
                        .from(imageView).prepare(fragment);
                getFragmentManager().beginTransaction().replace(R.id.level1_fragment_container, fragment).addToBackStack(null).commit();
*/

            }
        };

        genreItemClickListener = new GenreListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(final View view, int position) {

                android.support.v4.app.FragmentManager fragmentManager = appCompatActivity.getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                GenreViewFragment fragment = GenreViewFragment.newInstance(position);
                fragmentTransaction.add(R.id.level1_fragment_container, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();

            }
        };

        font = Typeface.createFromAsset(getActivity().getAssets(), "Roboto-Regular.ttf");
        titleFont = Typeface.createFromAsset(getActivity().getAssets(), "boldFont.ttf");

        appBar = (AppBarLayout) rootView.findViewById(R.id.appbar);
        toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        appCompatActivity.setSupportActionBar(toolbar);

        if (toolbar != null) {
            appCompatActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            Drawable drawable = getResources().getDrawable(R.mipmap.ic_drawer);
            DrawableCompat.setTint(drawable, getResources().getColor(R.color.main_text_color));
            toolbar.setNavigationIcon(drawable);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    god.getDrawerLayout().openDrawer(GravityCompat.START);
                }
            });
        }

        tabLayout = (TabLayout) rootView.findViewById(R.id.tab_layout);
        viewPager = (ViewPager) rootView.findViewById(R.id.viewpager);
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getActivity().getSupportFragmentManager());
        if (viewPager != null) {
            viewPager.setAdapter(viewPagerAdapter);
            tabLayout.setupWithViewPager(viewPager);
        }
        //God.overrideFonts(appBar, titleFont);
        God.overrideFonts(tabLayout, titleFont);
        God.overrideFonts(toolbar, font);


        return rootView;
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public class DetailsTransition extends TransitionSet {
        public DetailsTransition() {
            init();
        }

        /**
         * This constructor allows us to use this transition in XML
         */
        public DetailsTransition(Context context, AttributeSet attrs) {
            super(context, attrs);
            init();
        }

        private void init() {
            setOrdering(ORDERING_TOGETHER);
            addTransition(new ChangeBounds()).
                    addTransition(new ChangeTransform()).
                    addTransition(new ChangeImageTransform());
        }
    }

    class ViewPagerAdapter extends FragmentStatePagerAdapter {

        @Override
        public void unregisterDataSetObserver(DataSetObserver observer) {
            if (observer != null) {
                super.unregisterDataSetObserver(observer);
            }
        }


        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            FragmentTab fragment = new FragmentTab();
            Bundle bundle = new Bundle(1);
            bundle.putInt("TYPE", position);
            fragment.setArguments(bundle);
            return fragment;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return TITLES[position];
        }

        @Override
        public int getCount() {
            return NUM_OF_TABS;
        }
    }

    public static class FragmentTab extends Fragment {

        int type;


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
            type = getArguments().getInt("TYPE");
            Log.d("TYPE", "" + type);
            View rootView = inflater.inflate(R.layout.fragment, container, false);
            RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.fragment_list);
            if (type == SONGS) {
                GridLayoutManager llm = new GridLayoutManager(inflater.getContext(), 1);
                recyclerView.setLayoutManager(llm);
                recyclerView.setHasFixedSize(true);
                SongListAdapter recyclerViewAdapter = new SongListAdapter(inflater.getContext(), god.getSongs());
                recyclerViewAdapter.setOnItemClickListener(songItemClickListener);
                recyclerView.setAdapter(recyclerViewAdapter);

            } else if (type == ALBUMS) {
                //StaggeredGridLayoutManager llm = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
                GridLayoutManager llm = new GridLayoutManager(inflater.getContext(), 2);
                recyclerView.setLayoutManager(llm);
                recyclerView.setHasFixedSize(true);
                AlbumListAdapter recyclerViewAdapter = new AlbumListAdapter(inflater.getContext(), god.getAlbums());
                recyclerViewAdapter.setOnItemClickListener(albumItemClickListener);
                recyclerView.setAdapter(recyclerViewAdapter);

            } else if (type == ARTISTS) {
                //StaggeredGridLayoutManager llm = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
                GridLayoutManager llm = new GridLayoutManager(inflater.getContext(), 1);
                recyclerView.setLayoutManager(llm);
                recyclerView.setHasFixedSize(true);
                ArtistListAdapter recyclerViewAdapter = new ArtistListAdapter(inflater.getContext(), god.getArtists());
                recyclerViewAdapter.setOnItemClickListener(artistOnItemClickListener);
                recyclerView.setAdapter(recyclerViewAdapter);
            } else if (type == GENRES) {
                GridLayoutManager llm = new GridLayoutManager(inflater.getContext(), 1);
                recyclerView.setLayoutManager(llm);
                recyclerView.setHasFixedSize(true);
                GenreListAdapter recyclerViewAdapter = new GenreListAdapter(inflater.getContext(), god.getGenres());
                recyclerViewAdapter.setOnItemClickListener(genreItemClickListener);
                recyclerView.setAdapter(recyclerViewAdapter);
            } else if (type == PLAYLISTS) {
                GridLayoutManager llm = new GridLayoutManager(inflater.getContext(), 1);
                recyclerView.setLayoutManager(llm);
                recyclerView.setHasFixedSize(true);
                PlaylistListAdapter recyclerViewAdapter = new PlaylistListAdapter(inflater.getContext(), god.getPlaylists());
                //recyclerViewAdapter.setOnItemClickListener(songItemClickListener);
                recyclerView.setAdapter(recyclerViewAdapter);
            }

            return rootView;
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void updatePrimaryColor() {
        int primaryColor = mainActivity.getOptions().getPrimaryColor();
        int textColor = mainActivity.getOptions().getTextColor();

        appBar.setBackgroundColor(primaryColor);
        toolbar.setTitleTextColor(textColor);
        mainActivity.getGod().setLibraryStatusBarColor(primaryColor);
        tabLayout.setTabTextColors(textColor, mainActivity.getOptions().getDetailColor());

        Drawable menuDrawable = getResources().getDrawable(R.mipmap.ic_drawer);
        DrawableCompat.setTint(menuDrawable, textColor);
        toolbar.setNavigationIcon(menuDrawable);
        toolbar.setNavigationOnClickListener(v -> god.getDrawerLayout().openDrawer(GravityCompat.START));

        Drawable searchDrawable = searchMenuItem.getIcon();
        DrawableCompat.setTint(searchDrawable, textColor);
        searchMenuItem.setIcon(searchDrawable);
    }

    public void updateDetailColor() {
        int detailColor = mainActivity.getOptions().getDetailColor();
        int textColor = mainActivity.getOptions().getTextColor();

        tabLayout.setTabTextColors(textColor, detailColor);
        tabLayout.setSelectedTabIndicatorColor(detailColor);
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
