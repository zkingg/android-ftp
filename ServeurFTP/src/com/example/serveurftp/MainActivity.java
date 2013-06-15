package com.example.serveurftp;

import java.util.ArrayList;

import org.apache.commons.net.ftp.FTPClient;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.ftp.core.ServerFTP;
import com.ftp.core.Session;

public class MainActivity extends Activity implements OnClickListener {
	private ArrayList<Session> sessions;
	private ServerFTP server;
	private SharedPreferences prefs;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		findViewById(R.id.toggleButton_annonyme).setOnClickListener(this);
		findViewById(R.id.toggleButton_run_ftp).setOnClickListener(this);
		findViewById(R.id.button_exploreur_ftp).setOnClickListener(this);
		findViewById(R.id.button_gestion_compte).setOnClickListener(this);
		
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		sessions = new ArrayList<Session>();
		server = new ServerFTP(prefs); 
		Session s = new Session();
		s.setIp("10.0.0.1");
		s.setLogin("zkingg");
		sessions.add(s);
		
		((ListView)findViewById(R.id.listViewSessions)).setAdapter(new SessionsAdapteur(this, sessions));		
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		if(server != null)
			server.actualizePrefs();
		
		super.onResume();
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		Log.i("",R.id.action_settings+"="+item.getItemId());
		switch(item.getItemId()){
		case R.id.action_settings:
			startActivity(new Intent(this,Preferences.class));
			break;
		}
		return true;
	}

	@Override
	public void onClick(View v) {
		//Toast.makeText(this, "salut", Toast.LENGTH_SHORT).show();

		switch(v.getId()){
		case R.id.toggleButton_annonyme:	
			if(((ToggleButton)v).getText().equals("ON")){
				//si activation serveur
				Toast.makeText(this, "Connexion annonyme authorisé", Toast.LENGTH_SHORT).show();
			}else{
				//si desactivation serveur
				Toast.makeText(this, "Connexion annonyme interdite", Toast.LENGTH_SHORT).show();
			}
			break;
			
		case R.id.toggleButton_run_ftp:
			if(((ToggleButton)v).getText().equals("ON")){
				//si activation serveur
				if(! this.server.isRunning())
					this.server.startServer();
				
				Toast.makeText(this, "Serveur activé", Toast.LENGTH_SHORT).show();
			}else{
				//si desactivation serveur
				if(this.server.isRunning())
					this.server.stopServer();
				
				Toast.makeText(this, "Serveur desactivé", Toast.LENGTH_SHORT).show();
			}
			break;
			
		case R.id.button_gestion_compte:
			break;
			
		case R.id.button_exploreur_ftp:
			break;
		}
	}

}
