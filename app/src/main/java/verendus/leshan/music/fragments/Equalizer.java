package verendus.leshan.music.fragments;

import android.content.Context;
import android.content.res.ColorStateList;
import android.media.audiofx.BassBoost;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import app.minimize.com.seek_bar_compat.SeekBarCompat;
import verendus.leshan.music.MainActivity;
import verendus.leshan.music.R;
import verendus.leshan.music.objects.God;


public class Equalizer extends Fragment {

    LinearLayout linearLayout;
    MainActivity mainActivity;
    God god;

    public Equalizer() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static Equalizer newInstance() {
        Equalizer fragment = new Equalizer();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mainActivity = ((MainActivity) getActivity());
        mainActivity.disableNavBar();
        god = mainActivity.getGod();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_equalizer, container, false);

        /*linearLayout = (LinearLayout) rootView.findViewById(R.id.bands_linear_layout);
        ArrayList<String> equalizerPresetNames = new ArrayList<>();
        ArrayAdapter<String> equalizerSpinnerAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, equalizerPresetNames);
        equalizerSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner presetSpinner = (Spinner) rootView.findViewById(R.id.equalizer_presets);


        final android.media.audiofx.Equalizer equalizer = mainActivity.getMusicService().getEqualizer();
        final BassBoost bassBoost = mainActivity.getMusicService().getBassBoost();
        final short numberOfBands = equalizer.getNumberOfBands();
        final short higherLevel = equalizer.getBandLevelRange()[0];
        final short lowerLevel = equalizer.getBandLevelRange()[1];

        for (short i = 0; i < equalizer.getNumberOfPresets(); i++) {
            equalizerPresetNames.add(equalizer.getPresetName(i));
        }

        presetSpinner.setAdapter(equalizerSpinnerAdapter);

        presetSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {

                equalizer.usePreset((short) position);

                for (short i = 0; i < numberOfBands; i++) {

                    SeekBarCompat seekBar = (SeekBarCompat) linearLayout.findViewById(i);
                    seekBar.setProgress(equalizer.getBandLevel(i) - higherLevel);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );

        for (short i = 0; i < numberOfBands; i++) {
            final short index = i;
            SeekBarCompat seekBar = new SeekBarCompat(getContext());
            seekBar.setLayoutParams(layoutParams);
            seekBar.setId(i);
            seekBar.setMax(lowerLevel - higherLevel);
            //seekBar.setMax(20);
            seekBar.setProgress(equalizer.getBandLevel(index) - higherLevel);
            seekBar.setPadding(50, 20, 50, 20);

            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                    equalizer.setBandLevel(index, (short) (i + higherLevel));
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });

            Log.d("EQUALIZER", equalizer.getBandLevel(index) + "");

            seekBar.setProgressColor(getResources().getColor(R.color.detail));
            seekBar.setThumbColor(getResources().getColor(R.color.detail));

            LinearLayout equalizerBandLayout = new LinearLayout(getContext());
            TextView bandFrequency = new TextView(getContext());

            bandFrequency.setText(equalizer.getCenterFreq(index) / 1000 + "Hz");
            bandFrequency.setGravity(Gravity.CENTER);

            equalizerBandLayout.addView(bandFrequency);
            equalizerBandLayout.addView(seekBar);
            linearLayout.addView(equalizerBandLayout);
            linearLayout.setLayoutParams(layoutParams);
        }

        short roundedStrength = bassBoost.getRoundedStrength();
        short strength = bassBoost.getProperties().strength;

        Log.d("BASS BOOST", " Rounded : " + roundedStrength);
        Log.d("BASS BOOST", " Strength : " + strength);

        SeekBar bassBoostSeekBar = (SeekBar) rootView.findViewById(R.id.bass_boost_slider);
        bassBoostSeekBar.setMax(1000);
        bassBoostSeekBar.setProgress(roundedStrength);
        bassBoostSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                bassBoost.setStrength((short) i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
*/

        return rootView;

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onResume() {
        super.onResume();
        mainActivity = ((MainActivity) getActivity());
        god = mainActivity.getGod();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mainActivity.enableNavBar();
    }
}
