package com.example.ftpclient;

import java.io.IOException;

import org.apache.commons.net.ftp.FTPClient;
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

public class FileList extends FragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_file_list);
		// Show the Up button in the action bar.
		setupActionBar();
		
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
			DialogFragment addFileDialog = new CreateFileDialogFragment();
			addFileDialog.show(getSupportFragmentManager(), "new file");
			return true;
		case R.id.add_dir:
			DialogFragment addDirDialog = new CreateDirectoryDialogFragment();
			addDirDialog.show(getSupportFragmentManager(), "new directory");
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private class ShowFileList extends AsyncTask<Void, Void, String[]> {

		@Override
		protected String[] doInBackground(Void... params) {
			FTPClient ftp = FtpClient.getFtpClient();
			String[] filenames = null;
			try {
				filenames = ftp.listNames();
				Log.d("ftpclient", ftp.getReplyString());
			} catch (IOException e) {
				Log.e("ftpclient", "could not get filenames");
				e.printStackTrace();
			}
			return filenames;
		}
		
		@Override
		protected void onPostExecute(String[] result) {
			if (result == null)
				result = new String[] {};
			ListView view = (ListView) findViewById(R.id.listView1);
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, result);
			view.setAdapter(adapter);
		}
		
	}
	
	private class AddDirectory extends AsyncTask<String, Void, Void> {
		@Override
		protected Void doInBackground(String... params) {
			FTPClient ftp = FtpClient.getFtpClient();
			try {
				ftp.makeDirectory(params[0]);
				Log.d("ftpclient", ftp.getReplyString());
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}
	}
	
	private class AddFile extends AsyncTask<String, Void, Void> {
		@Override
		protected Void doInBackground(String... params) {
			FTPClient ftp = FtpClient.getFtpClient();
			try {
				ftp.stor(params[0]);
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}
	}

}
