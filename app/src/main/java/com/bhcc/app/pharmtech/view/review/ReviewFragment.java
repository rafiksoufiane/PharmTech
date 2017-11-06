package com.bhcc.app.pharmtech.view.review;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bhcc.app.pharmtech.R;
import com.bhcc.app.pharmtech.view.MainActivity;
import com.bhcc.app.pharmtech.view.navigation.ReplaceFragmentCommand;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * A simple {@link Fragment} subclass.
 */
public class ReviewFragment extends Fragment {

    // Lists
    private List<String> dateList;
    private List<String> fileNames;

    // views
    private RecyclerView quizListRecyclerView;

    // Adapter
    private QuizListAdapter quizListAdapter;

    public ReviewFragment() {
        // Required empty public constructor
    }


    /**
     * To set up lists & get data from the file
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // set up lists
        dateList = new ArrayList<>();
        fileNames = new ArrayList<>();

        // get data from the file
        // each review file's name
        File reviewInfo = new File(getActivity().getFilesDir(), MainActivity.fileName);
        try {
            Scanner fileInput = new Scanner(reviewInfo);
            while (fileInput.hasNextLine()) {
                // add to file name list
                String temp = fileInput.nextLine();
                fileNames.add(temp);
                Log.i("test5", temp);

                // modify date & time
                StringBuilder stringBuilder = new StringBuilder(temp);
                stringBuilder.insert(2, '/');
                stringBuilder.insert(5, '/');
                stringBuilder.insert(11, ':');
                stringBuilder.insert(14, ':');
                stringBuilder.replace(8, 9, " ");
                temp = stringBuilder.toString();

                // add to date list
                Log.i("test5", temp);
                dateList.add(temp);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.i("test5", "Error\n");
        }

    }

    /**
     * To set up UI
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // set up views
        View view = inflater.inflate(R.layout.fragment_review, container, false);
        quizListRecyclerView = (RecyclerView) view;
        quizListRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // update UI
        updateUI();

        return view;
    }

    /**
     * To update UI
     */
    private void updateUI() {

        // if no review in the file, show a warning toast
        // otherwise, show the review list
        if (dateList.size() > 0) {
            quizListAdapter = new QuizListAdapter(dateList);
            quizListRecyclerView.setAdapter(quizListAdapter);
        }
        else {
            Toast.makeText(getContext(), "Take a quiz first!!!", Toast.LENGTH_LONG).show();
        }

    }

    // =====================  ViewHolder =================================//

    private class QuizListHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        // widgets
        private TextView idTextView;
        private TextView nameTextView;
        private ImageButton trash;

        /**
         * Constructor
         * @param itemView
         */
        public QuizListHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);  //set onclick listener to the override method
            // link variables to widgets
            idTextView = (TextView) itemView.findViewById(R.id.quiz_review_id);
            nameTextView = (TextView) itemView.findViewById(R.id.quiz_review_date);
            trash = (ImageButton) itemView.findViewById(R.id.delete_button);
        }


        /**
         * To bind data to a holder
         * @param date
         */
        public void bindReview(String date) {

            // set text to each widget
            idTextView.setText("Quiz #" + (getPosition() + 1));
            nameTextView.setText("( " + date + " )");

            // Delete Review Part
            trash.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    try {

                        // Delete the review file
                        File fileDeleted = new File(getActivity().getFilesDir(), fileNames.get(getPosition()));
                        fileDeleted.delete();

                        // Remove file name from the list
                        fileNames.remove(getPosition());

                        // Write update file name list to the info file
                        File file = new File(getActivity().getFilesDir(),MainActivity.fileName);
                        PrintWriter printWriter = new PrintWriter(file);

                        for (String fileName : fileNames) {
                            printWriter.write(fileName + "\n");
                        }

                        printWriter.close();


                    }
                    catch (Exception ex) {}

                    //update UI
                    ReplaceFragmentCommand.startNewFragment(getActivity(), new ReviewFragment(), false);
                }
            });
        }

        /**
         * Set onClickListener to a holder
         * @param v
         */
        @Override
        public void onClick(View v) {
            ReviewDetailFragment fragment = ReviewDetailFragment.newInstance(fileNames.get(getPosition()));
            ReplaceFragmentCommand.startNewFragment(getActivity(), fragment, false);
        }
    }



    // =====================  Adapter ================================= //

    private class QuizListAdapter extends RecyclerView.Adapter<QuizListHolder> {
        // lists
        private List<String> dateList;

        /**
         * Constructor
         * @param dateList
         */
        public QuizListAdapter(List<String> dateList) {
            this.dateList = dateList;
        }

        /**
         * To set up views
         * @param parent
         * @param viewType
         * @return
         */
        @Override
        public QuizListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater.inflate(R.layout.fragment_quiz_review, parent, false);
            return new QuizListHolder(view);
        }

        /**
         * To bind adapter to a holder
         * @param holder
         * @param position
         */
        @Override
        public void onBindViewHolder(QuizListHolder holder, int position) {;
            holder.bindReview(dateList.get(position));
        }

        /**
         * To get the list size
         * @return List Size
         */
        @Override
        public int getItemCount() {
            return dateList.size();
        }
    }

}
