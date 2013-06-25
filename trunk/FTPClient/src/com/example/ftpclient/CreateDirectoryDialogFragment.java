package com.example.ftpclient;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

public class CreateDirectoryDialogFragment extends DialogFragment {
	public interface CreateDirectoryFragmentListener {
		public void onDialogPositiveClick(DialogFragment dialog, String dirname);
		public void onDialogNegativeClick(DialogFragment dialog);
	}
	
	CreateDirectoryFragmentListener mListener;
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		// Verify that the host activity implements the callback interface
		try {
			// Instantiate the listener so we can send events to the host
			mListener = (CreateDirectoryFragmentListener) activity;
		} catch(ClassCastException e) {
			// The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement CreateFileFragmentListener");
		}
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		LayoutInflater inflater = getActivity().getLayoutInflater();
		final View v = inflater.inflate(R.layout.dialog_add_directory, null);
		builder.setView(v)
		.setPositiveButton(R.string.add_dir, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				EditText et = (EditText) v.findViewById(R.id.directory_name);
				String s = et.getText().toString();
				mListener.onDialogPositiveClick(CreateDirectoryDialogFragment.this, s);
			}
		})
		.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				mListener.onDialogNegativeClick(CreateDirectoryDialogFragment.this);
			}
		});
		// Create the AlertDialog object and return it
		return builder.create();
	}
}
