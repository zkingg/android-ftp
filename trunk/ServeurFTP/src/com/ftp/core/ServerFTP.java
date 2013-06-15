package com.ftp.core;

import java.net.ServerSocket;

import android.content.SharedPreferences;
import android.util.Log;

public class ServerFTP {
	public final static int DEFAULT_PORT = 30000;
	
	private boolean run;
	private int port;
	private SharedPreferences prefs;
	private ServerSocket server;
	
	public ServerFTP(SharedPreferences prefs){
		this.prefs = prefs;
		this.run = false;
		
		actualizePrefs();
	}
	
	public boolean isRunning(){return this.run;}
	public int getPort(){ return this.port;}
	public void setPort(int port){this.port = port;}	
	
	public void startServer(){
		Log.i("server-ftp","running on port :"+port);
		this.run = true;
	}
	
	public void stopServer(){
		Log.i("server-ftp","server stoped");
		this.run = false;
	}

	public void actualizePrefs() {
		// TODO Auto-generated method stub
		try {
			this.port = Integer.parseInt(prefs.getString("port",""+DEFAULT_PORT));
		} catch (NumberFormatException e) {
			e.printStackTrace();
			this.port = DEFAULT_PORT;
		}
	}
	

}
