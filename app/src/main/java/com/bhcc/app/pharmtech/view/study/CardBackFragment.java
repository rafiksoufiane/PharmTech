package com.bhcc.app.pharmtech.view.study;


import android.app.Fragment;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bhcc.app.pharmtech.data.NoteLab;
import com.bhcc.app.pharmtech.R;
import com.bhcc.app.pharmtech.data.model.Medicine;


public class CardBackFragment extends Fragment {

    // Bundle Argument id
    private static final String ARG_MEDICINE_ID = "arg: medicine id";

    // Medicine
    private Medicine medicine;

    /**
     * To create a new instance of the fragment
     * @param medicine
     * @return CardBackFragment w/ a medicine
     */
    public static CardBackFragment newInstance(Medicine medicine) {
        Bundle args = new Bundle();  // a new bundle to hold arguments
        args.putSerializable(ARG_MEDICINE_ID, medicine);  // put a medicine as an argument
        CardBackFragment fragment = new CardBackFragment();  // create a new card back fragment
        fragment.setArguments(args);  // put arguments to a fragment
        return fragment;
    }

    public CardBackFragment() { }

    /**
     * To get Bundle Arguments (a medicine)
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
        }
        else {
            medicine = (Medicine) args.getSerializable(ARG_MEDICINE_ID);
        }
    }

    /**
     * To set up the views
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return View
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_card_back, container, false);
        TextView mBrandName = (TextView) rootView.findViewById(R.id.medicine_brand_name_textview);
        mBrandName.setText("Brand Name: " + medicine.getBrandName());

        TextView mPurpose = (TextView) rootView.findViewById(R.id.medicine_purpose_textview);
        mPurpose.setText("Purpose: " + medicine.getPurpose());

        TextView mCategory = (TextView) rootView.findViewById(R.id.medicine_category_textview);
        mCategory.setText("Category: " + medicine.getCategory());

        TextView mDeaSch = (TextView) rootView.findViewById(R.id.medicine_deaSch_textview);
        mDeaSch.setText("DeaSch: " + medicine.getDeaSch());

        TextView mSpecial = (TextView) rootView.findViewById(R.id.medicine_special_textview);
        mSpecial.setText("Special: " + medicine.getSpecial());

        TextView mStudyTopic = (TextView) rootView.findViewById(R.id.medicine_study_topic_textview);
        mStudyTopic.setText("Study Topic: " + medicine.getStudyTopic());


        // Note Part // get note from database
        final TextView mNote = (TextView) rootView.findViewById(R.id.medicine_note_textview);

        try {
            mNote.setText("Note: " + NoteLab.get(getActivity()).getNote(medicine.getGenericName()).getNote());
        }
        catch (Exception ex) {
            mNote.setText("Note: ");
        }

        // Edit Note Part
        final EditText mNoteEdit = (EditText) rootView.findViewById(R.id.medicine_note_edit_textview);

        Button mEditButton = (Button) rootView.findViewById(R.id.editBtn);

        mEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String temp = mNoteEdit.getText().toString();//get what user input

                mNote.setText("Note: " + temp); //set it first

                NoteLab.get(getActivity()).check(medicine, temp);// go check the datebase if the note is old or new one.insert or update
            }
        });

        // Play audio button
        ImageView playAudioButton = (ImageView) rootView.findViewById(R.id.play_audio_button_back);


        try {
            String fileName = medicine.getBrandName();
            fileName = fileName.substring(0, fileName.length() - 1);
            fileName = fileName.toLowerCase();
            Log.i("Test", fileName);
            int resID = getResources().getIdentifier(fileName, "raw", getActivity().getPackageName());
            final MediaPlayer myMediaPlayer = MediaPlayer.create(getActivity(), resID);
            if (myMediaPlayer.getDuration() == 0) {
                playAudioButton.setVisibility(View.INVISIBLE);
            }
        }
        catch (Exception ex) {
            playAudioButton.setVisibility(View.INVISIBLE);
        }


        playAudioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String fileName = medicine.getBrandName();
                    fileName = fileName.substring(0, fileName.length() - 1);
                    fileName = fileName.toLowerCase();
                    Log.i("test", fileName);

                    if (fileName.contains("/")) {
                        StringBuilder stringBuilder = new StringBuilder(fileName);
                        stringBuilder.deleteCharAt(fileName.indexOf('/'));
                        fileName = stringBuilder.toString();
                    }
                    int resID = getResources().getIdentifier(fileName, "raw", getActivity().getPackageName());
                    final MediaPlayer myMediaPlayer = MediaPlayer.create(getActivity(), resID);
                    myMediaPlayer.start();
                }
                catch (Exception ex) {
                    Toast.makeText(getActivity(), "No audio file for this medicine", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Flipping part
        TextView mFlip = (TextView) rootView.findViewById(R.id.flip_text_view_back);
        mFlip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Flip the card
                getFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(
                                R.animator.card_flip_right_in, R.animator.card_flip_right_out,
                                R.animator.card_flip_left_in, R.animator.card_flip_left_out)
                        .replace(R.id.container, CardFrontFragment.newInstance(medicine))
                        .commit();
            }
        });

        return rootView;
    }
}
