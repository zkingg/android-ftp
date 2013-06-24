package com.ftp.activity;

import com.ftp.core.Session;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

public class NewUserActivity extends Activity implements OnClickListener {
	private TextView login;
	private TextView mdp;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.creation_user);
		findViewById(R.id.button_new_user_valid).setOnClickListener(this);
		Session s = new Session(this);
		s.closeDB();
		
		this.login = (TextView) findViewById(R.id.editText_new_user_login);
		this.mdp = (TextView) findViewById(R.id.editText_new_user_mdp);
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.button_new_user_valid:
			if(login.getText().equals("")){
				Toast.makeText(this, "Login manquant ...", Toast.LENGTH_SHORT).show();
			}else if(mdp.getText().equals("")){
				Toast.makeText(this, "Mot de passe manquant ...", Toast.LENGTH_SHORT).show();
			}else{
				Session s = new Session(this);
				if(s.createUser(login.getText().toString(), mdp.getText().toString())){
					Toast.makeText(this, "Creation reussite", Toast.LENGTH_SHORT).show();
					s.closeDB();
					finish();
				}else{
					Toast.makeText(this, "Echec creation nouvel utilisateur", Toast.LENGTH_SHORT).show();
					s.closeDB();
				}
			}
			
			break;
		}
	}
}
