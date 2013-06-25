package com.example.ftpclient;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;

public class CreateFileDialogFragment extends DialogFragment {
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		LayoutInflater inflater = getActivity().getLayoutInflater();

		builder.setTitle("Add new file")
		.setView(inflater.inflate(R.layout.dialog_add_file, null))
		.setPositiveButton(R.string.add_file, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Log.d("ftpclient", "add file");
			}
		})
		.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				CreateFileDialogFragment.this.getDialog().cancel();
			}
		});
		// Create the AlertDialog object and return it
		return builder.create();
	}
}
