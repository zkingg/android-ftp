package com.ftp.core;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.SocketException;
import java.util.HashMap;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import com.ftp.activity.MainActivity;

/**
 * Classe qui gère le serveur
 */
public class ServerFTP {
	public final static int DEFAULT_PORT = 30000;
	public final static int DEFAULT_DATA_PORT = 30001;

	private MainActivity activity;
	private boolean run;
	private int port;
	private int data_port;
	private SharedPreferences prefs;
	private ServerSocket socket_server;
	private HashMap<String,Session> sessions;
	private AsyncTask<Void,Void,Void> thread_server;
	private boolean ALLOWED_ANONYMOUS_CONNECTION = false;
	
	public ServerFTP(MainActivity activity,SharedPreferences prefs){
		this.activity = activity;
		this.prefs = prefs;
		this.run = false;
		this.sessions = new HashMap<String,Session>();
		
		actualizePrefs();
	}
	
	public void enableAnonymousConnection(){this.ALLOWED_ANONYMOUS_CONNECTION = true;}
	public void disableAnonymousConnection(){this.ALLOWED_ANONYMOUS_CONNECTION = false;}
	public boolean anonymousConnectionAllowed(){ return this.ALLOWED_ANONYMOUS_CONNECTION;}
	public boolean isRunning(){return this.run;}
	public int getPort(){ return this.port;}
	public void setPort(int port){this.port = port;}
	public HashMap<String,Session> getSessions(){return this.sessions;}
	public ServerSocket getSocketServer(){return this.socket_server;}
	public int getDataPort(){return this.data_port; }
	public Activity getActivity(){return this.activity;}
	
	/**
	 * Lancer le serveur
	 */
	public void startServer(){
		thread_server = new AsyncTask<Void,Void,Void>(){//command server
			@Override
			protected Void doInBackground(Void... params) {
				try {			

					socket_server=new ServerSocket(port);//init srv écoute
					Log.i("ftp-server","server started to listening to client");
					while(! socket_server.isClosed())
					{
					    UserFTPAction action =new UserFTPAction(ServerFTP.this, socket_server.accept());//prise en charge des connexion
					}
				}
				catch(SocketException e){} 
				catch (IOException e) {e.printStackTrace();}
				
				ServerFTP.this.sessions.clear();
				refreshListSessions();
				Log.i("ftp-server","server stoped listening to client");
				return null;
			}
		};
		thread_server.execute();		
		Log.i("server-ftp","running on port :"+port);
	
		this.run = true;
	}
	
	/**
	 * Arrete le serveur
	 */
	public void stopServer(){
		if(thread_server != null){
			try {
				if(! this.socket_server.isClosed())
					this.socket_server.close();
				
				/*if(! this.socket_data_server.isClosed())
					this.socket_data_server.close();*/
				
				//thread_server.cancel(true);
			} catch (IOException e) {}
		}
		
		Log.i("server-ftp","server stoped");
		this.run = false;
	}
	
	/**
	 * Met a jour les informations du serveur
	 */
	public void actualizePrefs() {
		try {
			this.port = Integer.parseInt(prefs.getString("port",""+DEFAULT_PORT));
			this.data_port = Integer.parseInt(prefs.getString("data_port",""+DEFAULT_DATA_PORT));
		} catch (NumberFormatException e) {
			e.printStackTrace();
			this.port = DEFAULT_PORT;
		}
	}
	
	/**
	 * Ajoute une session user dans la liste des sessions active
	 * @param session
	 */
	public void addUserSession(Session session){
		this.sessions.put(session.getIp(),session);
		refreshListSessions();
	}
	
	/**
	 * Actualise le table des seesions
	 */
	public void refreshListSessions(){
		this.activity.runOnUiThread(new Runnable(){
			@Override
			public void run(){
				ServerFTP.this.activity.refreshListSessions(ServerFTP.this.sessions);
			}
		});
	}
	
	/**
	 * Retire une session des sessions active
	 * @param session
	 */
	public void removeUserSession(String ip){
		this.sessions.remove(ip);
		refreshListSessions();
	}
	
	public File getAppDirectory(){return this.activity.getFilesDir().getAbsoluteFile();}
}
