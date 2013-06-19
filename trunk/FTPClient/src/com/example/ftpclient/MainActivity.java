package com.example.ftpclient;

import java.io.IOException;
import java.net.SocketException;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPReply;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.ToggleButton;
import android.widget.RadioGroup;

public class MainActivity extends Activity implements OnClickListener {

	private String address = "10.0.2.2";
	private int port = 11111;
	private String username = "anonymous";
	private String password = "";
	private boolean anonymous = true;
	private int transferMode = FTP.ASCII_FILE_TYPE;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		findViewById(R.id.connectBtn).setOnClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onClick(View view) {
//		EditText ftpServerEditText = (EditText) findViewById(R.id.ftpServerEditText);
//		EditText portEditText = (EditText) findViewById(R.id.portEditText);
//		EditText usernameEditText = (EditText) findViewById(R.id.usernameEditText);
//		EditText passwordEditText = (EditText) findViewById(R.id.passwordEditText);
//		ToggleButton anonymousToggleButton = (ToggleButton) findViewById(R.id.anonymousToggleBtn);
//		int checkedRadioBtn = ((RadioGroup) findViewById(R.id.transferModeRadioGrp))
//				.getCheckedRadioButtonId();
//
//		 address = ftpServerEditText.getText().toString();
//		 port = Integer.parseInt(portEditText.getText().toString());
//		 username = usernameEditText.getText().toString();
//		 password = passwordEditText.getText().toString();
//		 anonymous = anonymousToggleButton.isChecked();
//		 transferMode = (checkedRadioBtn == R.id.asciiRadioBtn ?
//		 FTP.ASCII_FILE_TYPE : FTP.BINARY_FILE_TYPE);
		
		new ConnectToFTPServer().execute();
	}

	private class ConnectToFTPServer extends AsyncTask<Void, Void, Void> { 

		@Override
		protected Void doInBackground(Void... params) {
			FTPClient ftp = FtpClient.getFtpClient();
			FTPClientConfig config = FtpClient.getFtpClientConfig();
			Intent intent = new Intent(getApplicationContext(),	FileList.class);
			ftp.configure(config);
			try {
				if (ftp.isConnected())
					ftp.disconnect();
				ftp.connect(address, port);
				ftp.login(username, password);
				startActivity(intent);
			} catch (SocketException e) {
				Log.e("ftpclient", e.getMessage());
				e.printStackTrace();
			} catch (IOException e) {
				Log.e("ftpclient", e.getMessage());
				e.printStackTrace();
			}
			return null;
		}
	}

}
