package com.example.ftpclient;

import java.io.IOException;
import java.net.SocketException;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener {

	private String address;
	private int port = 21;
	private String username = "anonymous";
	private String password = "";
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
		// Get form values then connect to the FTP server.
		EditText ftpServerEditText = (EditText) findViewById(R.id.ftpServerEditText);
		EditText portEditText = (EditText) findViewById(R.id.portEditText);
		EditText usernameEditText = (EditText) findViewById(R.id.usernameEditText);
		EditText passwordEditText = (EditText) findViewById(R.id.passwordEditText);
		int checkedRadioBtn = ((RadioGroup) findViewById(R.id.transferModeRadioGrp))
				.getCheckedRadioButtonId();
		
		address = ftpServerEditText.getText().toString();
		
		// if the port field is not empty, we can parse it
		if (!portEditText.getText().toString().isEmpty())
			port = Integer.parseInt(portEditText.getText().toString());
		
		// if the username field is filled, use it instead of 'anonymous'
		if (!usernameEditText.getText().toString().isEmpty())
			username = usernameEditText.getText().toString();
		
		password = passwordEditText.getText().toString();
		
		transferMode = (checkedRadioBtn == R.id.asciiRadioBtn ? FTP.ASCII_FILE_TYPE : FTP.BINARY_FILE_TYPE);
		
		new ConnectToFTPServer().execute();
	}

	private class ConnectToFTPServer extends AsyncTask<Void, Void, Void> { 

		private boolean error = false;
		
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
				Log.d("ftpclient", ftp.getReplyString());
				ftp.login(username, password);
				Log.d("ftpclient", ftp.getReplyString());
				ftp.enterLocalPassiveMode();
				Log.d("ftpclient", ftp.getReplyString());
				ftp.setFileType(transferMode);
				Log.d("ftpclient", ftp.getReplyString());
				startActivity(intent);
			} catch (SocketException e) {
				error = true;
				Log.e("ftpclient", e.getMessage());
				e.printStackTrace();
			} catch (IOException e) {
				error = true;
				Log.e("ftpclient", e.getMessage());
				e.printStackTrace();
			} 
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			if (error) {
				Toast toast = Toast.makeText(getApplicationContext(), "Probl�me lors de la connexion", Toast.LENGTH_SHORT);
				toast.show();
			}
		}
	}

}
