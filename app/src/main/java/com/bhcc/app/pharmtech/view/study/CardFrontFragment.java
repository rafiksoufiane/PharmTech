package com.bhcc.app.pharmtech.view.study;


import android.app.Fragment;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bhcc.app.pharmtech.R;
import com.bhcc.app.pharmtech.data.model.Medicine;

public class CardFrontFragment extends Fragment {

    // Bundle arguments
    private static final String ARG_MEDICINE_ID = "arg: medicine id";

    // Medicine
    private Medicine medicine;

    public CardFrontFragment() {
    }

    /**
     * To create a new fragment
     *
     * @param medicine
     * @return
     */
    public static CardFrontFragment newInstance(Medicine medicine) {
        Bundle args = new Bundle();  // a new bundle to hold arguments
        args.putSerializable(ARG_MEDICINE_ID, medicine);  // put a medicine as an argument
        CardFrontFragment fragment = new CardFrontFragment();  // create a new card front fragment
        fragment.setArguments(args);  // put arguments to a fragment
        return fragment;
    }

    /**
     * To get bundle arguments
     *
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();

        // check if a fragment contains any arguments
        // if so get a medicine
        if (args == null) {
            medicine = new Medicine("generic", "brand", "purpose", "deaSch", "special", "category", "studyTopic");
        } else {
            medicine = (Medicine) args.getSerializable(ARG_MEDICINE_ID);
        }
    }


    /**
     * To set up views
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_card_front, container, false);
        TextView mDrugName = (TextView) rootView.findViewById(R.id.medicine_name_textview);

        // Play audio button
        ImageView playAudioButton = (ImageView) rootView.findViewById(R.id.play_audio_button);

        try {
            String fileName = medicine.getGenericName().toLowerCase();
            fileName = fileName.replace(" + ", "/");
            Log.i("file", "this file initially: " + fileName);

            if (fileName.contains("/")) {
                StringBuilder stringBuilder = new StringBuilder(fileName);
                stringBuilder.deleteCharAt(fileName.indexOf('/'));
                if (fileName.contains("-"))
                    stringBuilder.deleteCharAt(fileName.indexOf('-') - 1);
                fileName = stringBuilder.toString();
                Log.i("audio", "this file not present: " + fileName);
            }
            int resID = getResources().getIdentifier(fileName, "raw", getActivity().getPackageName());
            final MediaPlayer myMediaPlayer = MediaPlayer.create(getActivity(), resID);
            if (myMediaPlayer.getDuration() == 0) {

                playAudioButton.setVisibility(View.INVISIBLE);
            }
        } catch (Exception ex) {
            Log.i("audio", "exception this file not present: " + ex.getLocalizedMessage());
            playAudioButton.setVisibility(View.INVISIBLE);
        }


        playAudioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String fileName = medicine.getGenericName().toLowerCase();
                    fileName = fileName.replace(" + ", "/");

                    if (fileName.contains("/")) {
                        StringBuilder stringBuilder = new StringBuilder(fileName);
                        stringBuilder.deleteCharAt(fileName.indexOf('/'));
                        if (fileName.contains("-"))
                            stringBuilder.deleteCharAt(fileName.indexOf('-') - 1);
                        fileName = stringBuilder.toString();
                        Log.i("test", fileName);
                    }
                    int resID = getResources().getIdentifier(fileName, "raw", getActivity().getPackageName());
                    final MediaPlayer myMediaPlayer = MediaPlayer.create(getActivity(), resID);
                    myMediaPlayer.start();
                    Log.i("audio", "now playing " + fileName);
                } catch (Exception ex) {
                    Toast.makeText(getActivity(), "No audio file for this medicine", Toast.LENGTH_SHORT).show();
                }
            }
        });


        // Flipping part
        TextView mFlip = (TextView) rootView.findViewById(R.id.flip_text_view_front);
        mDrugName.setText(medicine.getGenericName());
        mFlip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flipTheCard();
            }
        });


        return rootView;
    }

    private void flipTheCard() {
        getFragmentManager()
                .beginTransaction()
                .setCustomAnimations(
                        R.animator.card_flip_right_in, R.animator.card_flip_right_out,
                        R.animator.card_flip_left_in, R.animator.card_flip_left_out)
                .replace(R.id.container, CardBackFragment.newInstance(medicine))
                .commit();
    }
}
