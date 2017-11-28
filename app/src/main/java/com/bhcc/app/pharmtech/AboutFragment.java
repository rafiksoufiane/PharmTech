package com.bhcc.app.pharmtech;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by steven
 * a rather simple fragment which will bring up the ABOUT section of the menu
 * i put a test string in there until we figure out what exactly NEEDS to be placed in about
 * section
 */

public class AboutFragment extends Fragment {
    private LinearLayout linearLayout;
    private TextView tvAbout;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_about, container, false);
        linearLayout = (LinearLayout) view.findViewById(R.id.about_linear_layout);
        tvAbout = (TextView) view.findViewById(R.id.about_text_view);
        return view;
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tvAbout.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
        tvAbout.setPadding(10, 20, 10, 20);
        tvAbout.setGravity(Gravity.CENTER_HORIZONTAL);

        //linearLayout.addView(tvLegal);
    }
}
