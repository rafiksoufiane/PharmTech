package com.bhcc.app.pharmtech.view.study;

import android.app.Fragment;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bhcc.app.pharmtech.R;
import com.bhcc.app.pharmtech.data.model.Medicine;

public class CardContainerFragment extends Fragment {

    // Bundle arguments
    private static final String ARG_MEDICINE_ID = "arg: medicine id";

    // Medicine
    private Medicine medicine;

    /**
     * To create a new fragment w/ a bundle arguments
     * @param medicine
     * @return CardContainerFragment w/ arguments
     */
    public static CardContainerFragment newInstance(Medicine medicine) {
        Bundle args = new Bundle();  // a new bundle to hold arguments
        args.putSerializable(ARG_MEDICINE_ID, medicine);  // put a medicine as an argument
        CardContainerFragment fragment = new CardContainerFragment();  // create a new card front fragment
        fragment.setArguments(args);  // put arguments to a fragment
        return fragment;
    }

    /**
     * To get Bundle arguments
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
     * To set up the views and replace the container w/ CardFrontFragment
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_card_container, container, false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            getChildFragmentManager()
                    .beginTransaction()
                    .add(R.id.container, CardFrontFragment.newInstance(medicine))
                    .commit();
        }

        return rootView;
    }

}