package com.bhcc.app.pharmtech.view.quiz;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bhcc.app.pharmtech.R;
import com.bhcc.app.pharmtech.data.MedicineLab;
import com.bhcc.app.pharmtech.data.MedicineSchema;
import com.bhcc.app.pharmtech.data.model.Medicine;
import com.bhcc.app.pharmtech.view.MainActivity;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class QuizFragment extends Fragment {

    // Extra for Bundle Arguments
    private static final String EXTRA_TOPIC_LIST = "extra: topic list";
    private static final String EXTRA_FIELD_LIST = "extra: field list";
    private static final String EXTRA_NUM_QUIZ = "extra: num quiz";

    // Static variables
    private static int numQuiz;
    private static int index = 0;
    private static int done = 0;
    private static int correct = 0;

    // Lists
    private String[] topicList;
    private String[] fieldList;
    private List<Medicine> medicines;
    private ArrayList<Integer> indexOfSubmittedQuestion;

    // Views
    private LinearLayout mLinearLayout;
    private LinearLayout mSubmitButtonLayout;
    private TextView mDrugNameTextView;
    private TextView mScoreTextView;

    private Button[] mSubmitButton;
    private ImageButton mNextButton;
    private ImageButton mPreviousButton;

    private TextView[] purposeTV = null;
    private TextView[] categoryTV = null;
    private TextView[] deaSchTV = null;
    private TextView[] specialTV = null;

    private EditText[] purposeET = null;
    private EditText[] categoryET = null;
    private EditText[] deaSchET = null;
    private EditText[] specialET = null;

    // Boolean Flags
    boolean[] isViewCreated = null;

    // File name
    private String fileName;


    /**
     * To Create new instance of Fragment
     *
     * @param topicList String list to hold chosen topics
     * @param fieldList String list to hold chosen fields
     * @param numQuiz   int var to hold number of all quizzes
     * @return QuizFragment
     */
    public static QuizFragment newInstance(String[] topicList, String[] fieldList, int numQuiz) {
        Bundle bundle = new Bundle();
        bundle.putStringArray(EXTRA_TOPIC_LIST, topicList);
        bundle.putStringArray(EXTRA_FIELD_LIST, fieldList);
        bundle.putInt(EXTRA_NUM_QUIZ, numQuiz);
        QuizFragment quizFragment = new QuizFragment();
        quizFragment.setArguments(bundle);
        return quizFragment;
    }


    /**
     * To create and set up views
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.activity_quiz, container, false);
        mLinearLayout = (LinearLayout) view.findViewById(R.id.quiz_activity_linear_layout);
        mSubmitButtonLayout = (LinearLayout) view.findViewById(
                R.id.quiz_activity_linear_layout_submit_button);
        mDrugNameTextView = (TextView) view.findViewById(R.id.question_quiz);
        mScoreTextView = (TextView) view.findViewById(R.id.score_quiz);
        mNextButton = (ImageButton) view.findViewById(R.id.next_button);
        mPreviousButton = (ImageButton) view.findViewById(R.id.previous_button);
        return view;

    }


    /**
     * To create a file for reviewing
     *
     * @param savedInstanceState
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        File reviewInfo = new File(getActivity().getFilesDir(), MainActivity.fileName);
        try {
            PrintWriter printWriter = new PrintWriter(new BufferedWriter(new FileWriter(reviewInfo, true)));
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMddyy_HHmmss");
            fileName = dateFormat.format(new Date()).toString();
            printWriter.append(fileName + "\n");
            printWriter.close();
        } catch (Exception ex) {
        }
    }

    /**
     * @param outState
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    /**
     * To get data from bundle arguments
     *
     * @param view
     * @param savedInstanceState
     */
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        topicList = getArguments().getStringArray(EXTRA_TOPIC_LIST);
        fieldList = getArguments().getStringArray(EXTRA_FIELD_LIST);
        numQuiz = getArguments().getInt(EXTRA_NUM_QUIZ, 0);
        setUpView();
    }

    /**
     * To lock the orientation
     */
    @Override
    public void onResume() {
        super.onResume();
        //getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    /**
     * To unlock the orientation
     * To save the quiz to the review file
     */
    @Override
    public void onPause() {
        super.onPause();
        //getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
        saveToFile();
    }

    /**
     * To set up fragment view
     */
    protected void setUpView() {
        // set up static variables starting with 0
        index = 0;
        done = 0;
        correct = 0;

        // set up lists
        mSubmitButton = new Button[numQuiz];

        purposeTV = new TextView[numQuiz];
        categoryTV = new TextView[numQuiz];
        deaSchTV = new TextView[numQuiz];
        specialTV = new TextView[numQuiz];

        purposeET = new EditText[numQuiz];
        categoryET = new EditText[numQuiz];
        deaSchET = new EditText[numQuiz];
        specialET = new EditText[numQuiz];

        indexOfSubmittedQuestion = new ArrayList<>();

        isViewCreated = new boolean[numQuiz];
        for (int i = 0; i < isViewCreated.length; i++) {
            isViewCreated[i] = false;
        }

        // get medicines and shuffle the list
        medicines = findMedicinesQuiz(topicList);
        Collections.shuffle(medicines);
        medicines = medicines.subList(0, numQuiz);
        Log.i("test1", String.valueOf(medicines.size()));

        for (Medicine medicine : medicines) {
            Log.i("test1", medicine.getGenericName());
        }
        for (int i = 0; i < fieldList.length; i++) {
            Log.i("test1", fieldList[i]);
        }

        ///////// Next button //////////
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                index = (index + 1) % numQuiz;
                updateUI();
            }
        });

        ///////// Previous //////////
        mPreviousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (index == 0) {
                    index = numQuiz;
                }
                index = (index - 1) % numQuiz;
                updateUI();
            }
        });

        // update UI
        updateUI();
    }

    private void updateUI() {
        // update scores
        mDrugNameTextView.setText(medicines.get(index).getGenericName());
        mScoreTextView.setText("Done: " + done + "/" + numQuiz
                + "\t\t\t" + "Correct: " + correct);

        // clear old views
        mSubmitButtonLayout.removeAllViews();
        mLinearLayout.removeAllViews();

        // add Drug name
        mLinearLayout.addView(mDrugNameTextView);

        // if a view is not yet created, create a new view
        if (!isViewCreated[index]) {

            // add each chosen field with edit text for an answer
            for (int i = 0; i < fieldList.length; i++) {
                String temp = fieldList[i];

                switch (temp) {
                    case MedicineSchema.MedicineTable.Cols.PURPOSE:
                        purposeTV[index] = new TextView(getContext());
                        mLinearLayout.addView(purposeTV[index]);
                        purposeTV[index].setText(temp + ":");
                        break;
                    case MedicineSchema.MedicineTable.Cols.CATEGORY:
                        categoryTV[index] = new TextView(getContext());
                        mLinearLayout.addView(categoryTV[index]);
                        categoryTV[index].setText(temp + ":");
                        break;
                    case MedicineSchema.MedicineTable.Cols.DEASCH:
                        deaSchTV[index] = new TextView(getContext());
                        mLinearLayout.addView(deaSchTV[index]);
                        deaSchTV[index].setText(temp + ":");
                        break;
                    case MedicineSchema.MedicineTable.Cols.SPECIAL:
                        specialTV[index] = new TextView(getContext());
                        mLinearLayout.addView(specialTV[index]);
                        specialTV[index].setText(temp + ":");
                        break;
                }

                if (temp.equals(MedicineSchema.MedicineTable.Cols.PURPOSE)) {
                    purposeET[index] = new EditText(getContext());
                    mLinearLayout.addView(purposeET[index]);
                } else if (temp.equals(MedicineSchema.MedicineTable.Cols.CATEGORY)) {
                    categoryET[index] = new EditText(getContext());
                    mLinearLayout.addView(categoryET[index]);
                } else if (temp.equals(MedicineSchema.MedicineTable.Cols.DEASCH)) {
                    deaSchET[index] = new EditText(getContext());

                    if (medicines.get(index).getDeaSch().equals("-")) {
                        deaSchET[index].setText("-");
                        deaSchET[index].setEnabled(false);
                        deaSchET[index].setBackgroundColor(Color.LTGRAY);
                    }

                    mLinearLayout.addView(deaSchET[index]);
                } else if (temp.equals(MedicineSchema.MedicineTable.Cols.SPECIAL)) {
                    specialET[index] = new EditText(getContext());

                    if (medicines.get(index).getSpecial().equals("")) {
                        specialET[index].setText("");
                        specialET[index].setEnabled(false);
                        specialET[index].setBackgroundColor(Color.LTGRAY);
                    }

                    mLinearLayout.addView(specialET[index]);
                }
            }

            ///////////// Submit Button ///////////////
            mSubmitButton[index] = new Button(getContext());
            mSubmitButton[index].setText("Submit");
            /*
            mSubmitButton[index].setGravity(Gravity.BOTTOM);
            mSubmitButton[index].setGravity(Gravity.CENTER_HORIZONTAL);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(0, 0, 0,
                    (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 25,
                            getResources().getDisplayMetrics()));

            mSubmitButton[index].setLayoutParams(params);
            */
            mSubmitButton[index].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    indexOfSubmittedQuestion.add(index);

                    boolean isPurposeCorrect = false;
                    boolean isCategoryCorrect = false;
                    boolean isDeaSchCorrect = false;
                    boolean isSpecialCorrect = false;

                    Log.i("test1", medicines.get(index).getPurpose());
                    Log.i("test1", medicines.get(index).getCategory());
                    Log.i("test1", medicines.get(index).getDeaSch());
                    Log.i("test1", medicines.get(index).getSpecial());


                    String purposeWithS = toPlural(medicines.get(index).getPurpose());

                    // Set the color of a text view to green if a user input correct answer
                    // otherwise, change the color to red
                    // Disable all edit text views
                    if (purposeET[index] != null) {

                        if (purposeET[index].getText().toString().toLowerCase().
                                equals(medicines.get(index).getPurpose().toLowerCase()) ||
                                purposeET[index].getText().toString().toLowerCase().
                                        equals(purposeWithS.toLowerCase())) {
                            purposeET[index].setTextColor(Color.GREEN);
                            isPurposeCorrect = true;
                        } else {
                            purposeET[index].setTextColor(Color.RED);
                            purposeTV[index].setText(purposeTV[index].getText()
                                    + " " + medicines.get(index).getPurpose());
                        }
                        purposeET[index].setEnabled(false);
                    } else {
                        isPurposeCorrect = true;
                    }

                    if (categoryET[index] != null) {
                        if (categoryET[index].getText().toString().toLowerCase()
                                .equals(medicines.get(index).getCategory().toLowerCase())) {
                            categoryET[index].setTextColor(Color.GREEN);
                            isCategoryCorrect = true;
                        } else {
                            categoryET[index].setTextColor(Color.RED);
                            categoryTV[index].setText(categoryTV[index].getText()
                                    + " " + medicines.get(index).getCategory());
                        }
                        categoryET[index].setEnabled(false);
                    } else {
                        isCategoryCorrect = true;
                    }

                    if (deaSchET[index] != null) {
                        if (deaSchET[index].getText().toString().toLowerCase()
                                .equals(medicines.get(index).getDeaSch().toLowerCase())) {
                            deaSchET[index].setTextColor(Color.GREEN);
                            isDeaSchCorrect = true;
                        } else {
                            deaSchET[index].setTextColor(Color.RED);
                            deaSchTV[index].setText(deaSchTV[index].getText()
                                    + " " + medicines.get(index).getDeaSch());
                        }
                        deaSchET[index].setEnabled(false);
                    } else {
                        isDeaSchCorrect = true;
                    }

                    if (specialET[index] != null) {
                        if (specialET[index].getText().toString().toLowerCase()
                                .equals(medicines.get(index).getSpecial().toLowerCase())) {
                            specialET[index].setTextColor(Color.GREEN);
                            isSpecialCorrect = true;
                        } else {
                            specialET[index].setTextColor(Color.RED);
                            specialTV[index].setText(specialTV[index].getText()
                                    + " " + medicines.get(index).getSpecial());
                        }
                        specialET[index].setEnabled(false);
                    } else {
                        isSpecialCorrect = true;
                    }

                    // Update Views and static variables
                    if (isPurposeCorrect && isCategoryCorrect && isDeaSchCorrect && isSpecialCorrect) {
                        correct++;
                        done++;
                        mSubmitButton[index].setText("Correct");
                        mSubmitButton[index].setEnabled(false);
                    } else {
                        done++;
                        mSubmitButton[index].setText("Incorrect");
                        mSubmitButton[index].setEnabled(false);
                    }

                    mScoreTextView.setText("Done: " + done + "/" + numQuiz
                            + "\t\t\t" + "Correct: " + correct);

                    if (done == numQuiz) {
                        showSummaryDialog();
                    }
                }
            });

            // add the submit button
            mSubmitButtonLayout.addView(mSubmitButton[index]);

            Log.i("test1", "ViewCreated");

            // update the boolean flag
            isViewCreated[index] = true;

        } else {

            // add created views to the layout
            for (int i = 0; i < fieldList.length; i++) {
                String temp = fieldList[i];

                switch (temp) {
                    case MedicineSchema.MedicineTable.Cols.PURPOSE:
                        mLinearLayout.addView(purposeTV[index]);
                        break;
                    case MedicineSchema.MedicineTable.Cols.CATEGORY:
                        mLinearLayout.addView(categoryTV[index]);
                        break;
                    case MedicineSchema.MedicineTable.Cols.DEASCH:
                        mLinearLayout.addView(deaSchTV[index]);
                        break;
                    case MedicineSchema.MedicineTable.Cols.SPECIAL:
                        mLinearLayout.addView(specialTV[index]);
                        break;
                }

                if (temp.equals(MedicineSchema.MedicineTable.Cols.PURPOSE)) {
                    mLinearLayout.addView(purposeET[index]);
                } else if (temp.equals(MedicineSchema.MedicineTable.Cols.CATEGORY)) {
                    mLinearLayout.addView(categoryET[index]);
                } else if (temp.equals(MedicineSchema.MedicineTable.Cols.DEASCH)) {
                    mLinearLayout.addView(deaSchET[index]);
                } else if (temp.equals(MedicineSchema.MedicineTable.Cols.SPECIAL)) {
                    mLinearLayout.addView(specialET[index]);
                }

            }

            mSubmitButtonLayout.addView(mSubmitButton[index]);
        }

    }

    /**
     * to find medicines from the database
     *
     * @param topicList String list to hold chosen topics
     * @return medicine list
     */
    private List<Medicine> findMedicinesQuiz(String[] topicList) {

        String whereArgs = "(";
        for (int i = 0; i < topicList.length; i++) {
            whereArgs += "?";
            if (i != topicList.length - 1) {
                whereArgs += ",";
            }
        }
        whereArgs += ")";

        List<Medicine> medicinesQuiz = MedicineLab.get(getContext())
                .getSpecificMedicines("StudyTopic IN " + whereArgs, topicList,
                        MedicineSchema.MedicineTable.Cols.GENERIC_NAME);
        return medicinesQuiz;
    }

    /**
     * To save quiz to review file
     */
    private void saveToFile() {
        File file = new File(getActivity().getFilesDir(), fileName);
        try {
            PrintWriter printWriter = new PrintWriter(file);

            for (int i : indexOfSubmittedQuestion) {
                printWriter.write(medicines.get(i).getGenericName() + "\n");

                for (int j = 0; j < fieldList.length; j++) {
                    String temp = fieldList[j];

                    switch (temp) {
                        case MedicineSchema.MedicineTable.Cols.PURPOSE:
                            printWriter.write("Purpose: ");
                            break;
                        case MedicineSchema.MedicineTable.Cols.CATEGORY:
                            printWriter.write("Category: ");
                            break;
                        case MedicineSchema.MedicineTable.Cols.DEASCH:
                            printWriter.write("DeaSCH: ");
                            break;
                        case MedicineSchema.MedicineTable.Cols.SPECIAL:
                            printWriter.write("Special: ");
                            break;
                    }

                    if (temp.equals(MedicineSchema.MedicineTable.Cols.PURPOSE)) {
                        printWriter.write(purposeET[i].getText().toString());
                        printWriter.write("  (" + medicines.get(i).getPurpose() + ")\n");

                    } else if (temp.equals(MedicineSchema.MedicineTable.Cols.CATEGORY)) {
                        printWriter.write(categoryET[i].getText().toString());
                        printWriter.write("  (" + medicines.get(i).getCategory() + ")\n");

                    } else if (temp.equals(MedicineSchema.MedicineTable.Cols.DEASCH)) {
                        if (deaSchET[i].getText().toString().equals("-")) {
                            printWriter.write("N/A\n");
                        } else {
                            printWriter.write(deaSchET[i].getText().toString());
                            printWriter.write("  (" + medicines.get(i).getDeaSch() + ")\n");
                        }

                    } else if (temp.equals(MedicineSchema.MedicineTable.Cols.SPECIAL)) {
                        if (specialET[i].getText().toString().equals("")) {
                            printWriter.write("N/A\n");
                        } else {
                            printWriter.write(specialET[i].getText().toString());
                            printWriter.write("  (" + medicines.get(i).getSpecial() + ")\n");
                        }
                    }

                }

                printWriter.write("\n");
            }

            printWriter.close();
        } catch (Exception ex) {
        }
    }

    /**
     * To Show dialog for the sorting selection
     */
    private void showSummaryDialog() {

        // custom dialog
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.score_summary_dialog);

        double percentage = (correct * 100.0 / numQuiz);
        Log.i("test", String.valueOf(percentage));

        TextView tvScorePercentage = (TextView) dialog.findViewById(R.id.score_percentage);
        tvScorePercentage.setText(String.valueOf((int) percentage) + "%");

        TextView tvCorrectPoints = (TextView) dialog.findViewById(R.id.correct_score);
        tvCorrectPoints.setText(String.valueOf(correct));

        TextView tvWrongPoints = (TextView) dialog.findViewById(R.id.wrong_score);
        tvWrongPoints.setText(String.valueOf(numQuiz - correct));

        TextView tvOK = (TextView) dialog.findViewById(R.id.score_summary_ok_button);
        tvOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }


    /**
     * Add 's' to the end of a string
     *
     * @param singular
     * @return String with 's' in the end
     */
    private String toPlural(String singular) {
        String temp = singular;
        char lastChar = temp.charAt(temp.length() - 1);

        switch (lastChar) {
            case 'y':
                temp = temp.substring(0, temp.length() - 1);
                temp += "ies";
                break;
            default:
                temp += "s";
        }
        Log.i("test", temp);
        return temp;
    }
}

