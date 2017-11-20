package com.bhcc.app.pharmtech.view.quiz;


import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.Random;

/**
 * Created by Mussie on 11/18/2017.
 */

public class TrueFalseQuizFragment extends Fragment{


    // Bundle argument id
    private static final String EXTRA_TOPIC_LIST = "extra: topic list";
    private static final String EXTRA_FIELD_LIST = "extra: field list";
    private static final String EXTRA_NUM_QUIZ = "extra: num quiz";

    // Static variables
    private final static int NUM_CHOICE = 2; // true or false
    private static int numQuiz;
    private static int index = 0;
    private static int done = 0;
    private static int correct = 0;

    // Lists
    private String[] topicList;
    private String[] fieldList;
    private List<Medicine> medicines;
    private List<Medicine> allMedicines;

    // Views & Widgets
    private LinearLayout mLinearLayout;
    private LinearLayout mSubmitButtonLayout;

    private TextView mScoreTextView;
    private TextView mQuestion;
    private Button[] mSubmitButton;
    private ImageButton mNextButton;
    private ImageButton mPreviousButton;

    private TextView[] tvQuestion = null;
    private RadioGroup[] rgChoices = null;
    private RadioButton[][] rbChoice1 = null;

    // Keeping track variables
    private int[] correctChoice;
    private String[] strQuestion = null;
    private String[] strAnswer = null;
    private ArrayList<Integer> indexOfSubmittedQuestion;
    boolean[] isViewCreated = null;

    // File name
    private String fileName;


    /**
     * To create a fragment w/ bundle arguments
     *
     * @param topicList
     * @param fieldList
     * @param numQuiz
     * @return TrueFalseQuizFragment
     */
    public static TrueFalseQuizFragment newInstance(String[] topicList, String[] fieldList,
                                                    int numQuiz) {
        Bundle bundle = new Bundle();
        bundle.putStringArray(EXTRA_TOPIC_LIST, topicList);
        bundle.putStringArray(EXTRA_FIELD_LIST, fieldList);
        bundle.putInt(EXTRA_NUM_QUIZ, numQuiz);
        TrueFalseQuizFragment trueFalseQuizFragment = new TrueFalseQuizFragment()   ;
        trueFalseQuizFragment.setArguments(bundle);
        return trueFalseQuizFragment;
    }



    /**
     * To set up views & widgets
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
        mScoreTextView = (TextView) view.findViewById(R.id.score_quiz);
        mQuestion = (TextView) view.findViewById(R.id.question_quiz);
        mNextButton = (ImageButton) view.findViewById(R.id.next_button);
        mPreviousButton = (ImageButton) view.findViewById(R.id.previous_button);
        return view;

    }



    /**
     * To create a review file for the quiz
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




    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    /**
     * To set up variables & views
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

        // Set up views
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
     */
    @Override
    public void onPause() {
        super.onPause();
        //getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
        saveToFile();
    }


    /**
     * To set up views & widgets
     */
    private void setUpView() {

        // initialize static variables
        index = 0;
        done = 0;
        correct = 0;

        // get all medicines
        allMedicines = MedicineLab.get(getActivity()).getSpecificMedicines(null, null, MedicineSchema.MedicineTable.Cols.GENERIC_NAME);

        // set up lists
        correctChoice = new int[numQuiz];

        mSubmitButton = new Button[numQuiz];

        tvQuestion = new TextView[numQuiz];
        strQuestion = new String[numQuiz];
        strAnswer = new String[numQuiz];
        rgChoices = new RadioGroup[numQuiz];
        rbChoice1 = new RadioButton[NUM_CHOICE][numQuiz];

        indexOfSubmittedQuestion = new ArrayList<>();

        // set up boolean flags to false
        isViewCreated = new boolean[numQuiz];
        for (int i = 0; i < isViewCreated.length; i++) {
            isViewCreated[i] = false;
        }

        // set up widgets
        mScoreTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
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
                if (index == 0)
                    index = numQuiz;
                index = (index - 1) % numQuiz;
                updateUI();
            }
        });

        // Update UI
        updateUI();
    }

    /**
     * To update UI
     */
    private void updateUI() {
        // update scores
        //mDrugNameTextView.setText(medicines.get(index).getGenericName());
        mScoreTextView.setText("Done: " + done + "/" + numQuiz
                + "\t\t\t" + "Correct: " + correct);

        // Clear old views
        mSubmitButtonLayout.removeAllViews();

        mLinearLayout.removeAllViews();

        // Add a question to the view
        mLinearLayout.addView(mQuestion);

        // if a view is not yet created, create a new view
        if (!isViewCreated[index]) {

            // To random number
            Random randomNumber = new Random();

            /////////////////// QUESTION /////////////////////////
            // random the question
            int randomField = Math.abs(randomNumber.nextInt() % fieldList.length);
            Log.i("test1", String.valueOf(randomField));

            strQuestion[index] = "The " + fieldList[randomField] + " of " // as long answer is at the back managable
                    +  medicines.get(index).getGenericName() + " is ";    // possible to randomize asking format


            // randomizing answer with 1/5 chance
            Medicine[] possibleMeds = new Medicine[5];
            possibleMeds[0] = medicines.get(index);

            for (int i =1; i <= 4; i++)
            {
                System.out.println(" " + medicines.size());
                possibleMeds[i] = medicines.get(Math.abs(randomNumber.nextInt() % medicines.size()));

                while (possibleMeds[i] == possibleMeds[0]) // making sure we don't accidentally have a better chance at the answer
                    possibleMeds[i] = medicines.get(Math.abs(randomNumber.nextInt() % medicines.size()));
            }

            // one of the 5 is selected to be shown in the question
            Medicine presentedMedicine = possibleMeds[Math.abs(randomNumber.nextInt() % 5)];



            // inserts last part of question
            if (fieldList[randomField].equalsIgnoreCase(MedicineSchema.MedicineTable.Cols.DEASCH))
            {
                strQuestion[index] += presentedMedicine.getDeaSch();
                if (presentedMedicine.getDeaSch().equalsIgnoreCase(medicines.get(index).getDeaSch()))
                    correctChoice[index] = 0; // // answer is true
                else
                    correctChoice[index] = 1;
            }
            else if (fieldList[randomField].equalsIgnoreCase(MedicineSchema.MedicineTable.Cols.PURPOSE))
            {
                strQuestion[index] += presentedMedicine.getPurpose();
                correctChoice[index] =  presentedMedicine.getPurpose().equalsIgnoreCase(medicines.get(index).getPurpose()) ? 0 : 1;
            }
            else if (fieldList[randomField].equalsIgnoreCase(MedicineSchema.MedicineTable.Cols.SPECIAL))
            {
                strQuestion[index] += presentedMedicine.getSpecial();
                correctChoice[index] =  presentedMedicine.getSpecial().equalsIgnoreCase(medicines.get(index).getSpecial()) ? 0 : 1;
            }
            else if (fieldList[randomField].equalsIgnoreCase(MedicineSchema.MedicineTable.Cols.CATEGORY))
            {
                strQuestion[index] += presentedMedicine.getCategory();
                correctChoice[index] =  presentedMedicine.getCategory().equalsIgnoreCase(medicines.get(index).getCategory()) ? 0 : 1;
            }



            /*strQuestion[index] = "What is the " + fieldList[randomField] + " of " +
                    medicines.get(index).getGenericName() + "/" + medicines.get(index).getBrandName();*/

            mQuestion.setText(strQuestion[index]);


            rbChoice1[0][index] = new RadioButton(getActivity());
            rbChoice1[0][index].setText("true");
            rbChoice1[0][index].setId((int)0);  // will be checkId later
            rbChoice1[0][index].setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);

            rbChoice1[1][index] = new RadioButton(getActivity());
            rbChoice1[1][index].setText("false");
            rbChoice1[1][index].setId((int)1);
            rbChoice1[1][index].setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
            //choices.add(temp1);

            rgChoices[index] = new RadioGroup(getActivity());
            rgChoices[index].addView( rbChoice1[0][index]);
            rgChoices[index].addView( rbChoice1[1][index]);
            // add radio group
            mLinearLayout.addView(rgChoices[index]);


            ///////////// Submit Button ///////////////
            Button button = new Button(getActivity());
            mSubmitButton[index] = button;
            mSubmitButton[index].setText("Submit");
//            mSubmitButton[index].setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
//            mSubmitButton[index].setGravity(Gravity.BOTTOM);
//            mSubmitButton[index].setGravity(Gravity.CENTER_HORIZONTAL);
//            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
//                    LinearLayout.LayoutParams.WRAP_CONTENT,
//                    LinearLayout.LayoutParams.WRAP_CONTENT
//            );
//            params.setMargins(0, 0, 0, toDP(25));
//
//            mSubmitButton[index].setLayoutParams(params);
            mSubmitButton[index].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    try {
                        // add the index to the submit list
                        indexOfSubmittedQuestion.add(index);

                        // find the checked radio button
                        int checkedId = rgChoices[index].getCheckedRadioButtonId();
                        System.out.println("checkedID: " + checkedId + "correcChoice[index]: " +correctChoice[index]);
                        // check if the answer is correct
                        // change the color
                        // update the score / done
                        if (checkedId == correctChoice[index]) {
                            mSubmitButton[index].setText("Correct");
                            strAnswer[index] = ((RadioButton) getView().findViewById(checkedId)).getText().toString();
                            correct++;
                        } else {
                            mSubmitButton[index].setText("Incorrect");
                            ((RadioButton) getView().findViewById(checkedId)).setTextColor(Color.parseColor("#E22170"));
                            strAnswer[index] = ((RadioButton) getView().findViewById(checkedId)).getText().toString();
                        }
                        done++;
                        mSubmitButton[index].setEnabled(false);
                        rbChoice1[correctChoice[index]][index].setTextColor(Color.parseColor("#4CAF50"));

                        // disable all radio buttons in this question
                        for (int i = 0; i < NUM_CHOICE; i++) {
                            rbChoice1[i][index].setEnabled(false);
                        }
                        rgChoices[index].setEnabled(false);
                        mScoreTextView.setText("Done: " + done + "/" + numQuiz
                                + "\t\t\t" + "Correct: " + correct);
                    } catch (Exception ex) {
                        Toast.makeText(getContext(), "Please choose one of the choices", Toast.LENGTH_SHORT).show();
                    }

                    if (done == numQuiz) {
                        showSummaryDialog();
                    }
                }
            });

            mSubmitButtonLayout.addView(mSubmitButton[index]);

            Log.i("test1", "ViewCreated");
            isViewCreated[index] = true;
        } else {
            //mLinearLayout.addView(tvQuestion[index]);
            mQuestion.setText(strQuestion[index]);
            mLinearLayout.addView(rgChoices[index]);

            mSubmitButtonLayout.addView(mSubmitButton[index]);

        }

    }

    /**
     * to find medicines from the database
     *
     * @param topicList
     * @return List of medicines
     */
    private List<Medicine> findMedicinesQuiz(String[] topicList) {

        String whereArgs = "(";
        for (int i = 0; i < topicList.length; i++) {
            whereArgs += "?";
            if (i != topicList.length - 1)
                whereArgs += ",";
        }
        whereArgs += ")";

        List<Medicine> medicinesQuiz = MedicineLab.get(getActivity())
                .getSpecificMedicines("StudyTopic IN " + whereArgs, topicList, MedicineSchema.MedicineTable.Cols.GENERIC_NAME);
        return medicinesQuiz;
    }

    /**
     * To convert from px to dp
     *
     * @param x
     * @return int dp
     */
    private int toDP(double x) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (float) x, getResources().getDisplayMetrics());
    }

    /**
     * To save the quiz to a review file
     */
    private void saveToFile() {
        File file = new File(getActivity().getFilesDir(), fileName);
        try {
            PrintWriter printWriter = new PrintWriter(file);

            for (int i : indexOfSubmittedQuestion) {
                printWriter.write(strQuestion[i] + "\n");
                /*printWriter.write("Your Answer: " +
                        ((RadioButton) getView().findViewById(rgChoices[i].getCheckedRadioButtonId())).getText() + "\n");
                        */
                printWriter.write("Your Answer: " + strAnswer[i] + "\n");
                printWriter.write("Correct Answer: " + (rbChoice1[correctChoice[i]][i]).getText() + "\n\n");
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
}



