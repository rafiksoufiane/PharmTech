package com.bhcc.app.pharmtech.view.review;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bhcc.app.pharmtech.R;

import java.io.File;
import java.util.Scanner;

/**
 * A simple {@link Fragment} subclass.
 */
public class ReviewDetailFragment extends Fragment {

    // Bundle argument id
    private static final String EXTRA_FILE_NAME = "extra: fileName";

    // views
    private TextView mReviewDetail;

    // file name
    private String fileName;

    /**
     * To crate a new fragment w/ bundle arguments
     * @param fileName
     * @return ReviewDetailFragment
     */
    public static ReviewDetailFragment newInstance(String fileName) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(EXTRA_FILE_NAME, fileName);
        ReviewDetailFragment reviewDetailFragment = new ReviewDetailFragment();
        reviewDetailFragment.setArguments(bundle);
        return reviewDetailFragment;
    }

    public ReviewDetailFragment() {
        // Required empty public constructor
    }


    /**
     * To set up views & read data from the file
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return view
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Read data from the file
        // Store each line to the string
        fileName = getArguments().getSerializable(EXTRA_FILE_NAME).toString();
        File file = new File(getActivity().getFilesDir(), fileName);
        String temp = "\n";

        try {
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                temp += scanner.nextLine() + '\n';
            }
        }
        catch (Exception ex) {}

        // Set up views
        View view = inflater.inflate(R.layout.fragment_review_detail, container, false);

        // Set data from the file to the text view
        mReviewDetail = (TextView) view.findViewById(R.id.text_view_quiz_detail);
        mReviewDetail.setText(temp);

        // Inflate the layout for this fragment
        return view;
    }

}
