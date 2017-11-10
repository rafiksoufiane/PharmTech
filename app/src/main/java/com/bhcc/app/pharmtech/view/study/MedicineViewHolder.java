package com.bhcc.app.pharmtech.view.study;

import com.bhcc.app.pharmtech.R;
import com.bhcc.app.pharmtech.data.model.Medicine;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

class MedicineViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private TextView idTextView;

    private TextView nameTextView;

    private Medicine medicine;

    public MedicineViewHolder(View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);
        idTextView = (TextView) itemView.findViewById(R.id.medicine_id);
        nameTextView = (TextView) itemView.findViewById(R.id.medicine_name);
    }

    public void bindMedicine(Medicine medicine) {
        // get a medicine from an argument and set a medicine id and name to text views
        this.medicine = medicine;
        idTextView.setText(this.medicine.getGenericName());
        String brandName = "[" + this.medicine.getBrandName() + "]";
        System.out.println(brandName);
        nameTextView.setText(brandName);
    }

    @Override
    public void onClick(View v) {
        Toast.makeText(v.getContext(), medicine.getGenericName() + " Clicked", Toast.LENGTH_SHORT)
                .show();

        /*
        ReplaceFragmentCommand.startNewFragment((MainActivity) v.getContext(),
                MedicinePagerFragment.newInstance(medicine), true);
        */
    }
}