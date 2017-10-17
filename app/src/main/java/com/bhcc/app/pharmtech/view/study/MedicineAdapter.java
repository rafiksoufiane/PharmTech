package com.bhcc.app.pharmtech.view.study;

import com.bhcc.app.pharmtech.R;
import com.bhcc.app.pharmtech.data.model.Medicine;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;


/**
 * Adapter to bind {@link Medicine} list to the {@link MedicineViewHolder} viewHolder
 */
class MedicineAdapter extends RecyclerView.Adapter<MedicineViewHolder> {

    private List<Medicine> medicines;

    private Context mContext;

    /**
     * @param context   Activity's {@link Context}
     * @param medicines List of {@link Medicine }
     */
    public MedicineAdapter(Context context, List<Medicine> medicines) {
        this.mContext = context;
        this.medicines = medicines;
    }

    @Override
    public MedicineViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        View view = layoutInflater.inflate(R.layout.fragment_medicine, parent, false);
        return new MedicineViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MedicineViewHolder holder, int position) {
        Medicine medicine = medicines.get(position);
        holder.bindMedicine(medicine);
    }

    @Override
    public int getItemCount() {
        return medicines.size();
    }
}