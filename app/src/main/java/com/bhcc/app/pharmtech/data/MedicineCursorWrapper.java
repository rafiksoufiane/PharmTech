package com.bhcc.app.pharmtech.data;

import com.bhcc.app.pharmtech.data.model.Medicine;

import android.database.Cursor;
import android.database.CursorWrapper;

public class MedicineCursorWrapper extends CursorWrapper {

    public MedicineCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    /**
     * To get each data from each field
     * @return
     */
    public Medicine getMedicine() {
        String genericName = getString(
                getColumnIndex(MedicineSchema.MedicineTable.Cols.GENERIC_NAME));
        String brandName = getString(getColumnIndex(MedicineSchema.MedicineTable.Cols.BRAND_NAME));
        String purpose = getString(getColumnIndex(MedicineSchema.MedicineTable.Cols.PURPOSE));
        String deaSch = getString(getColumnIndex(MedicineSchema.MedicineTable.Cols.DEASCH));
        String special = getString(getColumnIndex(MedicineSchema.MedicineTable.Cols.SPECIAL));
        String category = getString(getColumnIndex(MedicineSchema.MedicineTable.Cols.CATEGORY));
        String studyTopic = getString(
                getColumnIndex(MedicineSchema.MedicineTable.Cols.STUDY_TOPIC));

        Medicine medicine = new Medicine(genericName, brandName, purpose, deaSch, special, category,
                studyTopic);

        return medicine;
    }
}
