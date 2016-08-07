package verendus.leshan.music.fragments;

import android.content.Context;
import android.content.res.ColorStateList;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.graphics.ColorUtils;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.afollestad.materialdialogs.MaterialDialog;
import com.pes.androidmaterialcolorpickerdialog.ColorPicker;

import butterknife.BindView;
import butterknife.ButterKnife;
import verendus.leshan.music.MainActivity;
import verendus.leshan.music.R;
import verendus.leshan.music.adapters.AlbumListAdapter;
import verendus.leshan.music.adapters.ArtistListAdapter;
import verendus.leshan.music.adapters.GenreListAdapter;
import verendus.leshan.music.adapters.PlaylistListAdapter;
import verendus.leshan.music.adapters.SongListAdapter;
import verendus.leshan.music.objects.God;

public class SettingsFragment extends Fragment {

    private static final int NUM_OF_TABS = 3;
    private static final int APPEARANCE = 0;
    private static final int PLAYBACK = 1;
    private static final int LIBRARY = 2;
    private static final CharSequence[] TITLES = new CharSequence[]{"Appearance", "Playback", "Library"};

    static MainActivity mainActivity;
    God god;


    public SettingsFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static SettingsFragment newInstance() {
        SettingsFragment fragment = new SettingsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mainActivity = ((MainActivity)getActivity());
        god = mainActivity.getGod();

    }

    @BindView(R.id.settings_tab_layout) TabLayout tabLayout;
    @BindView(R.id.settings_viewpager) ViewPager viewPager;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_settings, container, false);
        ButterKnife.bind(this, rootView);

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getActivity().getSupportFragmentManager());
        if(viewPager != null){
            viewPager.setAdapter(viewPagerAdapter);
            tabLayout.setupWithViewPager(viewPager);
        }

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((ViewGroup) getView().getParent()).setFitsSystemWindows(true);
    }

    private class ViewPagerAdapter extends FragmentStatePagerAdapter {

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

    public static class FragmentTab extends Fragment{

        int type;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
            type = getArguments().getInt("TYPE");

            View rootView = inflater.inflate(R.layout.fragment, container, false);
            if(type==APPEARANCE) {
                rootView = inflater.inflate(R.layout.appearance_fragment, container, false);

                RelativeLayout themeSelector = (RelativeLayout) rootView.findViewById(R.id.theme_selector);
                LinearLayout primaryColorSelector = (LinearLayout) rootView.findViewById(R.id.primary_color_selector);
                FloatingActionButton detailColorSelector = (FloatingActionButton) rootView.findViewById(R.id.detail_color_selector);
                primaryColorSelector.setBackgroundColor(mainActivity.getOptions().getPrimaryColor());
                detailColorSelector.setBackgroundTintList(ColorStateList.valueOf(mainActivity.getOptions().getDetailColor()));

                themeSelector.setOnClickListener(v -> {
                    mainActivity.setTheme(R.style.DarkTheme);
                    themeSelector.setBackgroundColor(Color.BLACK);
                });


                primaryColorSelector.setOnClickListener(view ->{

                    final ColorPicker primaryColorPicker = new ColorPicker(mainActivity, 0, 0, 0);
                    primaryColorPicker.show();
                    Button okColor = (Button)primaryColorPicker.findViewById(R.id.okColorButton);
                    okColor.setOnClickListener(v -> {
                        primaryColorSelector.setBackgroundColor(primaryColorPicker.getColor());
                        mainActivity.setPrimaryColor(primaryColorPicker.getColor());
                        primaryColorPicker.dismiss();
                    });

                });

                detailColorSelector.setOnClickListener(view ->{

                    /*final ColorPicker detailColorPicker = new ColorPicker(mainActivity, 0, 0, 0);
                    detailColorPicker.show();
                    Button okColor = (Button)detailColorPicker.findViewById(R.id.okColorButton);
                    okColor.setOnClickListener(v -> {
                        detailColorSelector.setBackgroundTintList(ColorStateList.valueOf(detailColorPicker.getColor()));
                        mainActivity.setDetailColor(detailColorPicker.getColor());
                        detailColorPicker.dismiss();
                    });*/

                    boolean wrapInScrollView = true;
                    MaterialDialog colorPicker = new MaterialDialog.Builder(getContext())
                            .title("Pick a color")
                            .customView(R.layout.dialog_color_picker, wrapInScrollView)
                            .positiveText("Positive")
                            .show();

                    View colorPickerView = colorPicker.getView();

                    RecyclerView groupColorPicker = (RecyclerView)  colorPickerView.findViewById(R.id.material_color_group_picker);
                    RecyclerView shadeColorPicker = (RecyclerView)  colorPickerView.findViewById(R.id.material_color_shade_picker);


                });



            }/*else if(type == ALBUMS){
                //StaggeredGridLayoutManager llm = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
                GridLayoutManager llm = new GridLayoutManager(inflater.getContext(), 2);
                recyclerView.setLayoutManager(llm);
                recyclerView.setHasFixedSize(true);
                AlbumListAdapter recyclerViewAdapter = new AlbumListAdapter(inflater.getContext(), god.getAlbums());
                recyclerViewAdapter.setOnItemClickListener(albumItemClickListener);
                recyclerView.setAdapter(recyclerViewAdapter);

            }else if(type == ARTISTS){
                //StaggeredGridLayoutManager llm = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
                GridLayoutManager llm = new GridLayoutManager(inflater.getContext(), 1);
                recyclerView.setLayoutManager(llm);
                recyclerView.setHasFixedSize(true);
                ArtistListAdapter recyclerViewAdapter = new ArtistListAdapter(inflater.getContext(), god.getArtists());
                recyclerViewAdapter.setOnItemClickListener(artistOnItemClickListener);
                recyclerView.setAdapter(recyclerViewAdapter);
            }else if(type == GENRES){
                GridLayoutManager llm = new GridLayoutManager(inflater.getContext(), 1);
                recyclerView.setLayoutManager(llm);
                recyclerView.setHasFixedSize(true);
                GenreListAdapter recyclerViewAdapter = new GenreListAdapter(inflater.getContext(), god.getGenres());
                recyclerViewAdapter.setOnItemClickListener(genreItemClickListener);
                recyclerView.setAdapter(recyclerViewAdapter);
            }else if(type == PLAYLISTS){
                GridLayoutManager llm = new GridLayoutManager(inflater.getContext(), 1);
                recyclerView.setLayoutManager(llm);
                recyclerView.setHasFixedSize(true);
                PlaylistListAdapter recyclerViewAdapter = new PlaylistListAdapter(inflater.getContext(), god.getPlaylists());
                //recyclerViewAdapter.setOnItemClickListener(songItemClickListener);
                recyclerView.setAdapter(recyclerViewAdapter);
            }*/

            return rootView;
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
