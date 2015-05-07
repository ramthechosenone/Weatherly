package com.example.ram.weatherapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;

/**
 * Created by Ram on 04-05-2015.
 */
public class AlertDialogFragment extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Context context = getActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(context.getString(R.string.error_title)).setMessage(context.getString(R.string.error_message)).setPositiveButton(context.getString(R.string.positive_message), null);
//can create and configure at the same time, so can setTitle after (context)
        //create with new method and return it
        AlertDialog dialog = builder.create();
        return dialog;
    }
}
