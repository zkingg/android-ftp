package com.ftp.core;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Classe Descriptif des sessions utilisateur
 */
public class Session extends SQLiteOpenHelper {
	private SQLiteDatabase db;
	private static final int DATABASE_VERSION = 1;
	private static final String TABLE_NAME = "Users";
	private static final String TABLE_CREATE =
		"CREATE TABLE " + TABLE_NAME + " (" +
		"login varchar(100), " +
		"mdp varchar(32));";
	
	private String ip;
	private String login;
	
	private boolean is_logged = false;
	
	public Session(Context c){
		super(c,TABLE_NAME,null,DATABASE_VERSION);
		db = this.getWritableDatabase();
	}

	public Session(Context c,String login, String ip){
		super(c,TABLE_NAME,null,DATABASE_VERSION);
		this.db = this.getWritableDatabase();
		this.ip = ip;
		this.login = login;
	}
	
	public void setIsLogged(boolean is_logged){this.is_logged = is_logged;}
	public boolean isLogged(){return this.is_logged;}
	public String getIp() {return ip;}
	public void setIp(String ip) {this.ip = ip;	}
	public String getLogin() {return login;}
	public void setLogin(String login) {this.login = login;}
	
	public boolean connection(String mdp){
		/*if(mdp.equals("mdp")){
			return true;
		}else{
			return false;
		}*/

		String[] col = new String[]{"login"};
		String where = " login='"+login+"' and mdp='"+Utils.md5(mdp)+"'";
		Cursor c  = db.query(TABLE_NAME, col, where, null, null , null, null);
		if(c.moveToFirst()){
			return true;
		}else{
			return false;
		}
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(TABLE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
		onCreate(db);
	}
	
	public boolean createUser(String login, String mdp){

		ContentValues content = new ContentValues();
		content.put("login", login);
		content.put("mdp", Utils.md5(mdp));
		if(db.insert(TABLE_NAME,null,content) != -1){
			return true;
		}else{
			return false;
		}
	}

	public ArrayList<String> getListUser() {
		ArrayList<String> liste_user = new ArrayList<String>();
		String[] col = new String[]{"login"};
		
		Cursor c  = db.query(TABLE_NAME, col, null, null, null , null, null);
		while(c.moveToNext()){
			liste_user.add(c.getString(0));
		}
		
		return liste_user;
	}

	public boolean deleteUser(String login) {
		Log.v("","delete user "+login);
		
		if(db.delete(TABLE_NAME, "login = \""+login+"\"", null) >0)
			return true;
		else
			return false;
	}

	public void closeDB() {
		db.close();
	}
	
}
