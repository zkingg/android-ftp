package com.example.ftpclient;

import java.io.IOException;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.database.DataSetObserver;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.support.v4.app.NavUtils;

public class FileList extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_file_list);
		// Show the Up button in the action bar.
		setupActionBar();
		
		AsyncTask<Void, Void, Void> t = new AsyncTask<Void, Void, Void>() {
			
			FTPClient ftp = FtpClient.getInstance();
			ListView listView = (ListView) findViewById(R.id.listView1);
			ArrayAdapter<String> arrayAdapter = null;
			String[] filenames;
			
			@Override
			protected Void doInBackground(Void... params) {
				try {
					filenames = ftp.listNames();
					for (int i = 0; i < filenames.length; i++) {
						Log.d("nimitt", filenames[i]);
					}
				} catch (IOException e) {
					Log.e("nimitt", "Probl�me de r�cup�ration des fichiers");
					e.printStackTrace();
				}
				return null;
			}
			
			@Override
			protected void onPostExecute(Void result) {
				arrayAdapter = new ArrayAdapter<String>(FileList.this, R.id.listView1, filenames);
				listView.setAdapter(arrayAdapter);
				super.onPostExecute(result);
			}
		};
		
		t.execute();
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
		}
		return super.onOptionsItemSelected(item);
	}

}
