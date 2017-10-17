package com.bhcc.app.pharmtech.view.filter;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bhcc.app.pharmtech.R;
import com.bhcc.app.pharmtech.data.MedicineLab;
import com.bhcc.app.pharmtech.data.MedicineSchema;
import com.bhcc.app.pharmtech.view.navigation.ReplaceFragmentCommand;
import com.bhcc.app.pharmtech.view.study.MedicineListFragment;

import java.util.ArrayList;
import java.util.List;

public class FilterFragment extends Fragment {

    private CheckBox checkAll;
    private LinearLayout linearLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_filter, container, false);
        linearLayout = (LinearLayout) view.findViewById(R.id.filter_activity_linear_layout);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final TextView mSelectStudyTopicTextView = new TextView(getActivity());
        mSelectStudyTopicTextView.setText("Select Study Topics");
        mSelectStudyTopicTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
        mSelectStudyTopicTextView.setPadding(10, 20, 10, 20);
        mSelectStudyTopicTextView.setGravity(Gravity.CENTER_HORIZONTAL);

        linearLayout.addView(mSelectStudyTopicTextView);

        // new checkboxes for study topics
        final ArrayList<CheckBox> studyTopicCheckBox = new ArrayList<>();

        final MedicineLab medicineLab = MedicineLab.get(getActivity());
        List<String> studyTopic = medicineLab.getStudyTopics();
        Log.i("test", studyTopic.toString());

        for (int i = 0; i < studyTopic.size(); i++) {
            String topic = studyTopic.get(i);
            CheckBox checkBox = new CheckBox(getActivity());
            checkBox.setText(topic);
            studyTopicCheckBox.add(checkBox);
        }

        // to hold checked study topics
        final ArrayList<String> studyTopicCheckedList = new ArrayList<>();

        for (int i = 0; i < studyTopicCheckBox.size(); i++) {
            studyTopicCheckBox.get(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onCheckboxClicked((CheckBox)v, studyTopicCheckedList);
                }
            });
            linearLayout.addView(studyTopicCheckBox.get(i));
        }

        // set up the check all check box
        checkAll = new CheckBox(getActivity());
        checkAll.setText("All");
        checkAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                studyTopicCheckedList.clear();
                for (CheckBox checkBox : studyTopicCheckBox) {
                    checkBox.setChecked(true);
                    studyTopicCheckedList.add(checkBox.getText().toString());
                }
            }
        });
        linearLayout.addView(checkAll);

        ///////////// UPDATE LIST ///////////////
        Button updateListButton = new Button(getActivity());
        updateListButton.setText("Update List");
        updateListButton.setGravity(Gravity.CENTER_HORIZONTAL);

        updateListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // error toast if there is no check box checked
                if (studyTopicCheckedList.isEmpty()) {
                    Toast.makeText(getActivity(),
                            "Please select at least one STUDY TOPIC", Toast.LENGTH_SHORT).show();
                }
                else {
                    String[] topicList = toStringArray(studyTopicCheckedList);

                    // get checked study topic and convert to where arguments
                    String whereArgs = "(";
                    for (int i = 0; i < topicList.length; i++) {
                        whereArgs += "?";
                        if (i != topicList.length - 1)
                            whereArgs += ",";
                    }
                    whereArgs += ")";

                    // update the list
                    MedicineLab.get(getContext())
                            .updateMedicineLab("StudyTopic IN " + whereArgs, topicList, MedicineSchema.MedicineTable.Cols.GENERIC_NAME);


                    Toast.makeText(getContext(), "List updated", Toast.LENGTH_SHORT).show();
                    ReplaceFragmentCommand.startNewFragment(getActivity(), new MedicineListFragment(), false);
                }
            }
        });

        linearLayout.addView(updateListButton);
    }

    /**
     * On checked listener
     * @param checkBox
     * @param checkedList
     */
    public void onCheckboxClicked(CheckBox checkBox, ArrayList<String> checkedList) {
        // Is the view now checked?
        boolean checked = checkBox.isChecked();

        if (checked) {
            checkedList.add(checkBox.getText().toString());
        }
        else {
            checkedList.remove(checkBox.getText().toString());
            checkAll.setChecked(false);
        }
    }

    /**
     * Convert ArrayList to an array
     * @param list
     * @return String[]
     */
    private String[] toStringArray(ArrayList<String> list) {
        String[] tempStrings = new String[list.size()];
        tempStrings = list.toArray(tempStrings);

        return  tempStrings;
    }
}
