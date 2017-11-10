package com.bhcc.app.pharmtech.view.study;

import com.bhcc.app.pharmtech.R;
import com.bhcc.app.pharmtech.data.MedicineLab;
import com.bhcc.app.pharmtech.data.MedicineSchema;
import com.bhcc.app.pharmtech.data.model.Medicine;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;


public class MedicineListFragment extends Fragment {

    // Medicine list adapter
    private MedicineAdapter medicineAdapter;

    // Medicine recycler view
    RecyclerView medicineListRecyclerView;


    public MedicineListFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true); // To register the fragment to receive menu callbacks.
    }

    /**
     * To set up views
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_medicine_list, container, false);
        medicineListRecyclerView = (RecyclerView) view;
        medicineListRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // update UI
        updateUI();

        return view;
    }

    /**
     *  Added the SearchView interface to the app.
     *  The purpose of having a search view is for the user
     *  to be able to search for a certain drug in the list that
     *  he/she wants to review.
     *
     * @param menu
     * @param menuInflater
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {

        // Set up view
        super.onCreateOptionsMenu(menu, menuInflater);
        menuInflater.inflate(R.menu.fragment_medicine_search, menu);
        MenuItem searchItem = menu.findItem(R.id.menu_item_search);
        final SearchView searchView = (SearchView) searchItem.getActionView();

        // OnQuerySearchListener
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            /**
             * When the user submits the query.
             * @param s
             * @return
             */
            @Override
            public boolean onQueryTextSubmit(String s) {

                // if there is nothing in the serach bar
                // show every drugs
                if (s.length() == 0) {
                    MedicineLab.get(getContext())
                            .updateMedicineLab(null, null, MedicineSchema.MedicineTable.Cols.GENERIC_NAME);
                    updateUI();

                    return true;
                }

                // Where argument
                String[] args = new String[1];
                args[0] = s + "%";
                Log.i("Test", args[0]);

                // update the list
                MedicineLab.get(getContext())
                        .updateMedicineLab(MedicineSchema.MedicineTable.Cols.GENERIC_NAME + " LIKE ? ",
                                args, MedicineSchema.MedicineTable.Cols.GENERIC_NAME);

                // update UI
                updateUI();

                return true;
            }

            /**
             * When the user is typing in the search bar
             * @param s
             * @return
             */
            @Override
            public boolean onQueryTextChange(String s) {

                // if there is nothing in the serach bar
                // show every drugs
                if (s.length() == 0) {
                    MedicineLab.get(getContext())
                            .updateMedicineLab(null, null, MedicineSchema.MedicineTable.Cols.GENERIC_NAME);
                    updateUI();

                    return true;
                }

                // Where argument
                String[] args = new String[1];
                args[0] = s + "%";
                Log.i("Test", args[0]);


                // update the list
                MedicineLab.get(getContext())
                        .updateMedicineLab(MedicineSchema.MedicineTable.Cols.GENERIC_NAME + " LIKE ? ",
                                args, MedicineSchema.MedicineTable.Cols.GENERIC_NAME);

                // update UI
                updateUI();
                return true;
            }
        });
    }



    /**
     * To update UI
     */
    private void updateUI() {
        // set up the adapter w/ new lists
        MedicineLab medicineLab = MedicineLab.get(getActivity());
        List<Medicine> medicines = medicineLab.getMedicines();
        medicineAdapter = new MedicineAdapter(medicines);
        medicineListRecyclerView.setAdapter(medicineAdapter);
    }

    // =====================  ViewHolder =================================//

    private class MedicineHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // views
        private TextView idTextView;
        private TextView nameTextView;

        // medicine
        private Medicine medicine;

        /**
         * Constructor
         * @param itemView
         */
        public MedicineHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);  //set onclick listener to the override method
            // link variables to widgets
            idTextView = (TextView) itemView.findViewById(R.id.medicine_id);
            nameTextView = (TextView) itemView.findViewById(R.id.medicine_name);
        }

        /**
         * To set up each holder w/ data
         * @param medicine
         */
        public void bindMedicine(Medicine medicine) {
            // get a medicine from an argument and set a medicine id and name to text views
            this.medicine = medicine;
            idTextView.setText(this.medicine.getGenericName());
            nameTextView.setText("( " + this.medicine.getBrandName() + " )");
        }

        /**
         * To set up OnClickListener to each holder
         * @param v
         */
        @Override
        public void onClick(View v) {
            // show toast notifying a view holder is clicked
            //Toast.makeText(getContext(), medicine.getGenericName() + " Clicked", Toast.LENGTH_SHORT).show();

            // go to CardActivity
            Intent i = CardActivity.newIntent(getContext(), this.medicine.getGenericName());
            startActivity(i);
        }
    }


    // =====================  Adapter ================================= //

    private class MedicineAdapter extends RecyclerView.Adapter<MedicineHolder> {

        // medicine list
        private List<Medicine> medicines;

        /**
         * Constructor
         * @param medicines
         */
        public MedicineAdapter(List<Medicine> medicines) {
            this.medicines = medicines;
        }

        /**
         * To create a holder
         * @param parent
         * @param viewType
         * @return
         */
        @Override
        public MedicineHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater.inflate(R.layout.fragment_medicine, parent, false);
            return new MedicineHolder(view);
        }

        /**
         * To bind a holder
         * @param holder
         * @param position
         */
        @Override
        public void onBindViewHolder(MedicineHolder holder, int position) {
            Medicine medicine = medicines.get(position);
            // bind a medicine to a view holder
            holder.bindMedicine(medicine);
        }

        /**
         * get size of the list
         * @return
         */
        @Override
        public int getItemCount() {
            return medicines.size();
        }
    }
}

