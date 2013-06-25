package com.example.ftpclient;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.net.ftp.FTPClient;

import com.example.ftpclient.CreateDirectoryDialogFragment.CreateDirectoryFragmentListener;
import com.example.ftpclient.CreateFileDialogFragment.CreateFileFragmentListener;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;

public class FileList extends FragmentActivity implements CreateFileFragmentListener, CreateDirectoryFragmentListener {
	
	List<String> filenames = null;
	ListView fileList = null;
	ArrayAdapter<String> adapter = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_file_list);
		// Show the Up button in the action bar.
		setupActionBar();
		filenames = new ArrayList<String>(); 
		fileList = null;
		adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, filenames);
		new ShowFileList().execute();
	}

	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {

		getActionBar().setDisplayHomeAsUpEnabled(true);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.file_list, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);			
			return true;
		case R.id.add_file:
			showCreateFileDialog();
			return true;
		case R.id.add_dir:
			showCreateDirectoryDialog();
			return true;
		case R.id.deconnect:
			new Deconnect().execute();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private class ShowFileList extends AsyncTask<Void, Void, List<String>> {

		@Override
		protected List<String> doInBackground(Void... params) {
			FTPClient ftp = FtpClient.getFtpClient();
			try {
				filenames.addAll(Arrays.asList(ftp.listNames()));
				Log.d("ftpclient", ftp.getReplyString());
			} catch (IOException e) {
				Log.e("ftpclient", "could not get filenames");
				e.printStackTrace();
			}
			return filenames;
		}
		
		@Override
		protected void onPostExecute(List<String> result) {
			fileList = (ListView) findViewById(R.id.listView1);
//			ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, result);
			fileList.setAdapter(adapter);
		}
		
	}
	
	private class AddDirectory extends AsyncTask<String, Void, Void> {
		@Override
		protected Void doInBackground(String... params) {
			FTPClient ftp = FtpClient.getFtpClient();
			try {
				ftp.makeDirectory(params[0]);
				filenames.add(params[0]);
				Log.d("ftpclient", ftp.getReplyString());
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			Collections.sort(filenames);
			adapter.notifyDataSetChanged();
		}
	}
	
	private class AddFile extends AsyncTask<String, Void, Void> {
		@Override
		protected Void doInBackground(String... params) {
			FTPClient ftp = FtpClient.getFtpClient();
			try {
				byte[] test = new byte[] {};
				InputStream in = new DataInputStream(new ByteArrayInputStream(test)); 
				ftp.storeFile(params[0], in);
				in.close();
				filenames.add(params[0]);
				Log.d("ftpclient", ftp.getReplyString());
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			Collections.sort(filenames);
			adapter.notifyDataSetChanged();
		}
	}
	
	private class Deconnect extends AsyncTask<Void, Void, Void> {
		@Override
		protected Void doInBackground(Void... params) {
			FTPClient ftp = FtpClient.getFtpClient();
			if (ftp.isConnected())
				try {
					ftp.disconnect();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			Intent intent = new Intent(getApplicationContext(), MainActivity.class);
			startActivity(intent);
			return null;
		}
	}
	
	public void showCreateFileDialog() {
		DialogFragment addFileDialog = new CreateFileDialogFragment();
		addFileDialog.show(getSupportFragmentManager(), "CreateFileDialogFragment");
	}
	
	public void showCreateDirectoryDialog() {
		DialogFragment addDirDialog = new CreateDirectoryDialogFragment();
		addDirDialog.show(getSupportFragmentManager(), "CreateDirectoryDialogFragment");
	}

	@Override
	public void onDialogPositiveClick(DialogFragment dialog, String s) {
		if (dialog instanceof CreateFileDialogFragment) {
			new AddFile().execute(new String[] { s });
		}
		else {
			new AddDirectory().execute(new String[] { s });
		}
	}

	@Override
	public void onDialogNegativeClick(DialogFragment dialog) {
		// Cancel button was clicked, do nothing.
	}

}
