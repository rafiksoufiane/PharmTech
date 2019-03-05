package com.bhcc.app.pharmtech.view.quiz;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
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

/**
 * Created by xianbinZhen on 11/5/2017.
 */

public class QuizMixAndMatchFragment extends Fragment {
    // Extra for Bundle Arguments
    private static final String EXTRA_TOPIC_LIST = "extra: topic list";
    private static final String EXTRA_FIELD_LIST = "extra: field list";
    private static final String EXTRA_NUM_QUIZ = "extra: num quiz";
    private static final String STRING_CORRECT = "correct";
    private static final String STRING_WRONG = "wrong";

    // Static variables
    private static int numQuiz; // number of questions
    private static int index = 0; // points to current question
    private static int done = 0;
    private static int correct = 0;

    // Lists
    private String[] questionArrList;
    private String[] topicList;
    private String[] fieldList;
    private List<Medicine> medicines;
    private List<Medicine> allMedicines;
    private ArrayList<Integer> indexOfSubmittedQuestion;
    private boolean[][] mIsAnswerCorrect;


    // Views
    private RecyclerView mRecyclerViewMixMatch;
    private MixMatchAdapter mMixMatchAdapter;
    private ImageView mImageViewCheckedIcon;
    private ProgressBar mProgressBar;

    private TextView mDrugNameTextView;
    private TextView mScoreTextView;

    private View mSubmitButton;
    private ImageButton mNextButton;
    private ImageButton mPreviousButton;

    // Boolean Flags
    private boolean[] isQuestionsAnswered = null;

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
    public static QuizMixAndMatchFragment newInstance(String[] topicList, String[] fieldList, int numQuiz) {
        Bundle bundle = new Bundle();
        bundle.putStringArray(EXTRA_TOPIC_LIST, topicList);
        bundle.putStringArray(EXTRA_FIELD_LIST, fieldList);
        bundle.putInt(EXTRA_NUM_QUIZ, numQuiz);
        QuizMixAndMatchFragment quizFragment = new QuizMixAndMatchFragment();
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
        View view = inflater.inflate(R.layout.fragment_mix_match, container, false);

        mRecyclerViewMixMatch = (RecyclerView) view.findViewById(R.id.listViewMixMatch);
        mProgressBar = (ProgressBar) view.findViewById(R.id.progressBar_quiz);
        mSubmitButton = view.findViewById(R.id.buttonSubmit);
        mScoreTextView = (TextView) view.findViewById(R.id.score_quiz);
        mDrugNameTextView = (TextView) view.findViewById(R.id.medicine_gerneric_name_textview);
        mNextButton = (ImageButton) view.findViewById(R.id.next_button);
        mPreviousButton = (ImageButton) view.findViewById(R.id.previous_button);

        mRecyclerViewMixMatch.setLayoutManager(new LinearLayoutManager(getActivity()));


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

        // writing to external file ??
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
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        topicList = getArguments().getStringArray(EXTRA_TOPIC_LIST);
        fieldList = getArguments().getStringArray(EXTRA_FIELD_LIST);
        numQuiz = getArguments().getInt(EXTRA_NUM_QUIZ, 0);
        setUpView();
    }


    /**
     * To set up fragment view
     */
    protected void setUpView() {
        // set up static variables starting with 0
        index = 0;
        done = 0;
        correct = 0;

        mProgressBar.setMax(numQuiz);


        indexOfSubmittedQuestion = new ArrayList<>();
        allMedicines = MedicineLab.get(getActivity()).getSpecificMedicines(null, null, MedicineSchema.MedicineTable.Cols.GENERIC_NAME);

        isQuestionsAnswered = new boolean[numQuiz];
        for (int i = 0; i < isQuestionsAnswered.length; i++) {
            isQuestionsAnswered[i] = false;
        }
        mIsAnswerCorrect = new boolean[numQuiz][fieldList.length];
        for (int i = 0; i < numQuiz; i++) {
            for (int j = 0; j < fieldList.length; j++) {
                mIsAnswerCorrect[i][j] = true;
            }
        }
        // get medicines and shuffle the list
        medicines = findMedicinesQuiz(topicList);
        Collections.shuffle(medicines);
        medicines = medicines.subList(0, numQuiz);


        ///////// Next button //////////
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                index = (index + 1) % numQuiz;
                updateUI();
                if (isQuestionsAnswered[index]) {
                    mSubmitButton.setEnabled(false);
                }
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
                if (isQuestionsAnswered[index]) {
                    mSubmitButton.setEnabled(false);
                }
            }
        });

        questionArrList = new String[fieldList.length];
        for (int i = 0; i < questionArrList.length; i++) {
            questionArrList[i] = "item" + i;
        }

        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImageView imgCheck;
                TextView textViewName, textViewContent;
                String strField;
                View itemView;

                boolean isAnsweredCorrect = true;
                for (int i = 0; i < fieldList.length; i++) {

                    textViewName = (TextView) mRecyclerViewMixMatch.getLayoutManager().findViewByPosition(i).findViewById(R.id.text_view_item_name);
                    textViewContent = (TextView) mRecyclerViewMixMatch.getLayoutManager().findViewByPosition(i).findViewById(R.id.text_view_item_context);
                    strField = textViewName.getText().toString();
                    itemView = mRecyclerViewMixMatch.getLayoutManager().findViewByPosition(i);
                    imgCheck = (ImageView) mRecyclerViewMixMatch.getLayoutManager().findViewByPosition(i).findViewById(R.id.imageView_checked);
                    boolean boo = false;
                    switch (strField) {
                        case "DEASch":
                            if (medicines.get(index).getDeaSch().equals(textViewContent.getText().toString()))
                                boo = true;
                            else
                                textViewContent.setText(medicines.get(index).getDeaSch());
                            break;
                        case "Category":
                            if (medicines.get(index).getCategory().equals(textViewContent.getText().toString()))
                                boo = true;
                            else
                                textViewContent.setText(medicines.get(index).getCategory());
                            break;
                        case "Purpose":
                            if (medicines.get(index).getPurpose().equals(textViewContent.getText().toString()))
                                boo = true;
                            else
                                textViewContent.setText(medicines.get(index).getPurpose());
                            break;
                        case "Special":
                            if (medicines.get(index).getSpecial().equals(textViewContent.getText().toString()))
                                boo = true;
                            else
                                textViewContent.setText(medicines.get(index).getSpecial());
                            break;
                    }
                    imgCheck.setVisibility(View.VISIBLE);
                    itemView.setClickable(false);
                    if ((imgCheck.getTag().equals(STRING_CORRECT) && boo) || (imgCheck.getTag().equals(STRING_WRONG) && !boo)) {
                        imgCheck.setImageResource(R.drawable.icn_correct);
                    } else {
                        isAnsweredCorrect = false;
                        textViewContent.setTextColor(Color.RED);
                        mIsAnswerCorrect[index][i] = false;
                        indexOfSubmittedQuestion.add(index);
                        imgCheck.setImageResource(R.drawable.icn_wrong);

                    }
                }
                if (isAnsweredCorrect)
                    correct++;
                done++;
                mSubmitButton.setEnabled(false);
                isQuestionsAnswered[index] = true;
                if (done >= numQuiz) {
                    showSummaryDialog();
                }
            }
        });
        updateUI();

    }

    private void updateUI() {
        mSubmitButton.setEnabled(true);
        mRecyclerViewMixMatch.setAdapter(null);
//        if (mMixMatchAdapter[index] == null)
        mMixMatchAdapter = new MixMatchAdapter(medicines.get(index), isQuestionsAnswered[index], mIsAnswerCorrect[index]);
        mRecyclerViewMixMatch.setAdapter(mMixMatchAdapter);
        mDrugNameTextView.setText(medicines.get(index).getGenericName());
        mScoreTextView.setText((index + 1) + " / " + numQuiz);
        mProgressBar.setProgress(index + 1);

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


    private class MedicineHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView mTextViewTopic;
        private TextView mTextViewcontent;
        private ImageView mImageViewChecked;

        public MedicineHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.fragment_mix_match_item, parent, false));
            itemView.setOnClickListener(this);

            mTextViewTopic = (TextView) itemView.findViewById(R.id.text_view_item_name);
            mTextViewcontent = (TextView) itemView.findViewById(R.id.text_view_item_context);
            mImageViewChecked = (ImageView) itemView.findViewById(R.id.imageView_checked);
            mImageViewChecked.setTag(STRING_CORRECT);

        }

        public void bind(String topic, String content, boolean isQuestionDone, boolean isAnsweredCorrect) {
            mTextViewTopic.setText(topic);
            mTextViewcontent.setText(content);
            if (isQuestionDone) {
                itemView.setClickable(false);
                if (!isAnsweredCorrect) {
                    mTextViewcontent.setTextColor(Color.RED);
                    mImageViewChecked.setImageResource(R.drawable.icn_wrong);
                }
            }
        }

        @Override
        public void onClick(View view) {
//mImageViewChecked.setImageResource(R.drawable.icn_correct);
            if (mImageViewChecked.getTag().equals(STRING_CORRECT) || mImageViewChecked.getVisibility() == View.VISIBLE) {
                mImageViewChecked.setImageResource(R.drawable.icn_wrong);
                mImageViewChecked.setTag(STRING_WRONG);
                mImageViewChecked.setVisibility(View.INVISIBLE);
            } else {
                mImageViewChecked.setImageResource(R.drawable.icn_correct);
                mImageViewChecked.setTag(STRING_CORRECT);
                mImageViewChecked.setVisibility(View.VISIBLE);
            }
        }


    }

    private class MixMatchAdapter extends RecyclerView.Adapter<MedicineHolder> {
        private Medicine mMedicine;
        private boolean[] mIsCorrectAnswer;
        private boolean mIsQuestionDone;
        private boolean[] mIsAnswerCorrect;

        private MixMatchAdapter(Medicine medicine, boolean isQuestionDone, boolean[] isAnswerCorrect) {
            mMedicine = medicine;
            mIsQuestionDone = isQuestionDone;
            mIsAnswerCorrect = new boolean[isAnswerCorrect.length];
            for (int i = 0; i < isAnswerCorrect.length; i++) {
                mIsAnswerCorrect[i] = isAnswerCorrect[i];
            }
            if (!mIsQuestionDone) {
                mIsCorrectAnswer = new boolean[fieldList.length];
                for (int i = 0; i < mIsCorrectAnswer.length; i++) {
                    if (((int) Math.floor(Math.random() * 101) % 2) == 0) {
                        mIsCorrectAnswer[i] = false;
                    } else {
                        mIsCorrectAnswer[i] = true;
                    }
                }
            }
        }


        @Override
        public MedicineHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            return new MedicineHolder(layoutInflater, parent);
        }


        @Override
        public void onBindViewHolder(MedicineHolder holder, int position) {

            String field = fieldList[position];
            String temp = "";
            if (mIsQuestionDone) {

            }
            switch (field) {
                case "DEASch":
                    if (mIsQuestionDone || mIsCorrectAnswer[position])
                        holder.bind(field, mMedicine.getDeaSch(), mIsQuestionDone, mIsAnswerCorrect[position]);
                    else {
                        do {
                            int index = (int) Math.floor(Math.random() * (allMedicines.size()));
                            temp = allMedicines.get(index).getDeaSch();
                            holder.bind(field, temp, false, true);
                        } while (temp.equals(mMedicine.getDeaSch()));
                    }
                    break;
                case "Purpose":

                    if (mIsQuestionDone || mIsCorrectAnswer[position])
                        holder.bind(field, mMedicine.getPurpose(), mIsQuestionDone, mIsAnswerCorrect[position]);
                    else {
                        do {
                            int index = (int) Math.floor(Math.random() * (allMedicines.size()));
                            temp = allMedicines.get(index).getPurpose();
                            holder.bind(field, temp, false, true);
                        } while (temp.equals(mMedicine.getPurpose()));
                    }
                    break;
                case "Special":
                    if (mIsQuestionDone || mIsCorrectAnswer[position])
                        holder.bind(field, mMedicine.getSpecial(), mIsQuestionDone, mIsAnswerCorrect[position]);

                    else {
                        do {
                            int index = (int) Math.floor(Math.random() * (allMedicines.size()));
                            temp = allMedicines.get(index).getSpecial();
                            holder.bind(field, temp, false, true);
                        } while (temp.equals(mMedicine.getSpecial()));
                    }
                    break;
                case "Category":
                    if (mIsQuestionDone || mIsCorrectAnswer[position])
                        holder.bind(field, mMedicine.getCategory(), mIsQuestionDone, mIsAnswerCorrect[position]);

                    else {
                        do {
                            int index = (int) Math.floor(Math.random() * (allMedicines.size()));
                            temp = allMedicines.get(index).getCategory();
                            holder.bind(field, temp, false, true);
                        } while (temp.equals(mMedicine.getCategory()));
                    }
                    break;
            }

        }

        @Override
        public int getItemCount() {
            return fieldList.length;
        }
    }

    private void showSummaryDialog() {

        // custom dialog
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.score_summary_dialog);

        double percentage = (correct * 100.0 / numQuiz);

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
     * To save the quiz to a review file
     */
    /**
     * To save quiz to review file
     */
    private void saveToFile() {
        File file = new File(getActivity().getFilesDir(), fileName);
        try {
            PrintWriter printWriter = new PrintWriter(file);
            for (int i : indexOfSubmittedQuestion) {
                printWriter.write("Generic Name: " + medicines.get(i).getGenericName() + "\n");

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
                    if (!mIsAnswerCorrect[i][j]) {
                        printWriter.write("Your answer was incorrect.");
                    }
                    if (temp.equals(MedicineSchema.MedicineTable.Cols.PURPOSE)) {
                        printWriter.write("\n  ( Correct answer is: " + medicines.get(i).getPurpose() + ")\n");
                    } else if (temp.equals(MedicineSchema.MedicineTable.Cols.CATEGORY)) {
                        printWriter.write("\n  ( Correct answer is: " + medicines.get(i).getCategory() + ")\n");
                    } else if (temp.equals(MedicineSchema.MedicineTable.Cols.DEASCH)) {
                        printWriter.write("\n  ( Correct answer is: " + medicines.get(i).getDeaSch() + ")\n");

                    } else if (temp.equals(MedicineSchema.MedicineTable.Cols.SPECIAL)) {
                        printWriter.write("\n  ( Correct answer is: " + medicines.get(i).getSpecial() + ")\n");
                    }
                }
                printWriter.write("\n");
            }

            printWriter.close();
        } catch (Exception ex) {
        }
    }
}
