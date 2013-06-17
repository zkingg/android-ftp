package com.ftp.activity;

import java.util.HashMap;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import com.ftp.core.ServerFTP;
import com.ftp.core.Session;
import com.ftp.core.Utils;

/**
 * Activité principale
 *
 */
public class MainActivity extends Activity implements OnClickListener {
	private ServerFTP server;
	private SharedPreferences prefs;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		findViewById(R.id.toggleButton_anonyme).setOnClickListener(this);
		findViewById(R.id.toggleButton_run_ftp).setOnClickListener(this);
		findViewById(R.id.button_exploreur_ftp).setOnClickListener(this);
		findViewById(R.id.button_gestion_compte).setOnClickListener(this);
		
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		server = new ServerFTP(this, prefs); 	
	}
	
	/**
	 * Methode de mise a jour de la ListView
	 * @param sessions : hashmap contenant les sessions a afficher
	 */
	public void refreshListSessions(HashMap<String,Session> sessions){
		((ListView)findViewById(R.id.listViewSessions)).setAdapter(new SessionsAdapter(this, sessions));
	}
	
	@Override
	protected void onResume() {
		if(server != null)
			server.actualizePrefs();
		
		super.onResume();
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
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
		switch(v.getId()){
		case R.id.toggleButton_anonyme:	
			if(((ToggleButton)v).getText().equals(getResources().getString(R.string.switch_on))){
				//si activation serveur
				server.enableAnonymousConnection();
				Toast.makeText(this, "Connexion annonyme authorisé", Toast.LENGTH_SHORT).show();
			}else{
				//si desactivation serveur
				server.disableAnonymousConnection();
				Toast.makeText(this, "Connexion annonyme interdite", Toast.LENGTH_SHORT).show();
			}
			break;
			
		case R.id.toggleButton_run_ftp:
			if(((ToggleButton)v).getText().equals(getResources().getString(R.string.switch_on))){
				//si activation serveur
				if(! this.server.isRunning()){
					this.server.startServer();
					((TextView)findViewById(R.id.textView_adress_ip_server)).setText("Adresse ip serveur :"+Utils.getIPAddress(true));				
				}
				
				Toast.makeText(this, "Serveur activé", Toast.LENGTH_SHORT).show();
			}else{
				//si desactivation serveur
				if(this.server.isRunning()){
					this.server.stopServer();
					((TextView)findViewById(R.id.textView_adress_ip_server)).setText("");				
				}
				
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
