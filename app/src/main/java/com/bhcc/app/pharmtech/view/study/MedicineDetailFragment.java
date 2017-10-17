package com.bhcc.app.pharmtech.view.study;

import com.bhcc.app.pharmtech.data.MedicineLab;
import com.bhcc.app.pharmtech.R;
import com.bhcc.app.pharmtech.data.model.Medicine;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

public class MedicineDetailFragment extends Fragment {

    // Bundle argument id
    private static final String ARG_MEDICINE_ID = "arg: medicine id";

    // Medicine
    private Medicine medicine;
    private String medicineGenericName;

    // Views
    private TextView medicineNameTextView;

    public MedicineDetailFragment() {
    }

    /**
     * to create a new fragment with bundle arguments
     * @param medicineGenericName
     * @return MedicineDetailFragment w/ bundle arguments
     */
    public static MedicineDetailFragment newInstance(String medicineGenericName) {
        Bundle args = new Bundle();
        args.putString(ARG_MEDICINE_ID, medicineGenericName);
        MedicineDetailFragment fragment
                = new MedicineDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * To get bundle arguments
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();

        // check if a fragment contains any arguments
        // if so get a medicine id and get a medicine from a medicine lab
        if (args == null) {
            medicine = new Medicine("generic", "brand", "purpose", "deaSch", "special", "category",
                    "studyTopic");
        } else {
            medicineGenericName = args.getString(ARG_MEDICINE_ID);
            medicine = MedicineLab.get(getActivity()).getMedicine(medicineGenericName);
        }
    }

    /**
     * To set up views
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail, container, false);

        // link a variable to a widget
        medicineNameTextView = (TextView) view.findViewById(R.id.medicine_name_textview);
        // set a medicine name to a text view
        medicineNameTextView.setText(medicine.getGenericName());
        medicineNameTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getFragmentManager();
                Toast.makeText(getContext(), "aaa", Toast.LENGTH_SHORT).show();
                
            }
        });

        return view;
    }

}
