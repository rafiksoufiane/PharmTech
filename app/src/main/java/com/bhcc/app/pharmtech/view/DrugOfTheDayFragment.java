package com.bhcc.app.pharmtech.view;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;

import com.bhcc.app.pharmtech.R;

/**
 * Created by Mussie on 11/9/2017.
 */

public class DrugOfTheDayFragment extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(inflater.inflate(R.layout.drug_of_the_day, null))
                // Add action buttons
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // we can say go back to normal view or where ever from here
                        // leaving it empty just makes it go back to who ever called it
                    }
                })
        // we don't really need this but it was in th example: feel more than free to remove
              /*  .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        MyDialog.this.getDialog().cancel();
                    }

                })
                */ ;
        return builder.create();
    }
}
