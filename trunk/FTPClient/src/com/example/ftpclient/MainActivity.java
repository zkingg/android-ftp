package com.example.ftpclient;

import java.io.IOException;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPReply;

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
		EditText ftpServerEditText = (EditText) findViewById(R.id.ftpServerEditText);
		EditText portEditText = (EditText) findViewById(R.id.portEditText);
		EditText usernameEditText = (EditText) findViewById(R.id.usernameEditText);
		EditText passwordEditText = (EditText) findViewById(R.id.passwordEditText);
		Switch anonymousSwitch = (Switch) findViewById(R.id.anonymousSwitch);
		int checkedRadioBtn = ((RadioGroup) findViewById(R.id.transferModeRadioGrp)).getCheckedRadioButtonId();
		
		String address = ftpServerEditText.getText().toString();
		int port = Integer.parseInt(portEditText.getText().toString());
		String username = usernameEditText.getText().toString();
		String password = passwordEditText.getText().toString();
		boolean anonymous = anonymousSwitch.isChecked();
		int transferMode = (checkedRadioBtn == R.id.asciiRadioBtn ? FTP.ASCII_FILE_TYPE : FTP.BINARY_FILE_TYPE);
		
		Intent intent = new Intent(getApplicationContext(), FileList.class);
		
		FTPClient ftp = new FTPClient();
		FTPClientConfig config = new FTPClientConfig();
		boolean error = false;
		
		try {
			int reply;
			ftp.setFileType(transferMode);
			ftp.connect(address, port);
			Log.d("debug", ftp.getReplyString());			
			reply = ftp.getReplyCode();

			if(!FTPReply.isPositiveCompletion(reply)) {
				ftp.disconnect();
				System.err.println("FTP server refused connection.");
				System.exit(1);
			}
			
			startActivity(intent);
			
			ftp.logout();
		} catch (IOException e) {
			error = true;
			e.printStackTrace();
		} finally {
			if(ftp.isConnected()) {
		        try {
		        	ftp.disconnect();
		        } catch(IOException ioe) {
		          // do nothing
		        }
		    }
			System.exit(error ? 1 : 0);
		}
	}
    
}
