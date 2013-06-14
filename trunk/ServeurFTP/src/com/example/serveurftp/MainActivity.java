package com.example.serveurftp;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.io.CopyStreamListener;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.widget.Toast;

public class MainActivity extends Activity {
	CopyStreamListener c;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Toast.makeText(this, "salut", Toast.LENGTH_SHORT).show();
		FTPClient ftp1 = new FTPClient();
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
