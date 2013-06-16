package com.ftp.core;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.SocketException;
import java.util.HashMap;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import com.ftp.activity.MainActivity;

/**
 * Classe qui gère le serveur
 */
public class ServerFTP {
	public final static int DEFAULT_PORT = 30000;
	
	private MainActivity activity;
	private boolean run;
	private int port;
	private SharedPreferences prefs;
	private ServerSocket socket_server;
	private HashMap<String,Session> sessions;
	private AsyncTask<Void,Void,Void> thread_server;
	
	public ServerFTP(MainActivity activity,SharedPreferences prefs){
		this.activity = activity;
		this.prefs = prefs;
		this.run = false;
		this.sessions = new HashMap<String,Session>();
		
		addUserSession(new Session("Zkingg","10.0.0.1"));
		addUserSession(new Session("Zkingg2","10.0.0.3"));	
		actualizePrefs();
	}
	
	public boolean isRunning(){return this.run;}
	public int getPort(){ return this.port;}
	public void setPort(int port){this.port = port;}
	public HashMap<String,Session> getSessions(){return this.sessions;}
	
	/**
	 * Lancer le serveur
	 */
	public void startServer(){
		thread_server = new AsyncTask<Void,Void,Void>(){
			@Override
			protected Void doInBackground(Void... params) {
				try {
					socket_server=new ServerSocket(port);//init srv écoute
					while(! socket_server.isClosed())
					{
					    UserFTPAction action =new UserFTPAction(ServerFTP.this, socket_server.accept());//prise en charge des connexion
					}
				}
				catch(SocketException e){} 
				catch (IOException e) {e.printStackTrace();}
				
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
		this.activity.refreshListSessions(this.sessions);
	}
	
	/**
	 * Retire une session des sessions active
	 * @param session
	 */
	public void removeUserSession(Session session){
		this.sessions.remove(session.getIp());
		this.activity.refreshListSessions(this.sessions);
	}
	
}
