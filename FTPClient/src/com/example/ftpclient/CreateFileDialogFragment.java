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

public class CreateFileDialogFragment extends DialogFragment {
	/* The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs to query it. */
	public interface CreateFileFragmentListener {
		public void onDialogPositiveClick(DialogFragment dialog, String s);
		public void onDialogNegativeClick(DialogFragment dialog);
	}
	
	// Use this instance of the interface to deliver action events
	CreateFileFragmentListener mListener;
	
	// Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		// Verify that the host activity implements the callback interface
		try {
			// Instantiate the listener so we can send events to the host
			mListener = (CreateFileFragmentListener) activity;
		} catch(ClassCastException e) {
			// The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement CreateFileFragmentListener");
		}
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// Build the dialog and set up the button click handlers
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		LayoutInflater inflater = getActivity().getLayoutInflater();
		final View v = inflater.inflate(R.layout.dialog_add_file, null);
		builder.setView(v)
		.setPositiveButton(R.string.add_file, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				EditText et = (EditText) v.findViewById(R.id.filename);
				String s = et.getText().toString();
				// Send the positive button event back to the host activity
                mListener.onDialogPositiveClick(CreateFileDialogFragment.this, s);
			}
		})
		.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// Send the negative button event back to the host activity
                mListener.onDialogNegativeClick(CreateFileDialogFragment.this);
			}
		});
		// Create the AlertDialog object and return it
		return builder.create();
	}
}
