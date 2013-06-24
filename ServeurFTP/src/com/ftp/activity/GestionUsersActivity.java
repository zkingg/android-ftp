package com.ftp.activity;

import com.ftp.core.Session;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class GestionUsersActivity extends Activity implements OnClickListener {
	private ListView list_user;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gestion_compte);
		findViewById(R.id.button_ajout_user).setOnClickListener(this);
		
		this.list_user = (ListView) findViewById(R.id.listView_gestion_users);
		registerForContextMenu(list_user);
		list_user.setOnCreateContextMenuListener(this);
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.button_ajout_user:
			Intent i = new Intent(this,NewUserActivity.class);
			startActivity(i);
			break;
		}
	}
	
	@Override
	protected void onResume() {
		refreshListUser();
		super.onResume();
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		getMenuInflater().inflate(R.menu.gestion_user_context_menu,menu);		
		super.onCreateContextMenu(menu, v, menuInfo);
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		String login = (String) list_user.getAdapter().getItem(info.position);
		
		switch(item.getItemId()){
		case R.id.menu_item_delete:
			Session s = new Session(this);
			if(s.deleteUser(login)){
				Toast.makeText(this, "Suppression réussite", Toast.LENGTH_SHORT).show();
				refreshListUser();
			}else{
				Toast.makeText(this, "Echec suppression", Toast.LENGTH_SHORT).show();
			}
			
			break;
		}
		
		return true;
	}
	
	public void refreshListUser(){
		Session s = new Session(this);
		list_user.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,s.getListUser()));
		s.closeDB();
	}
}
