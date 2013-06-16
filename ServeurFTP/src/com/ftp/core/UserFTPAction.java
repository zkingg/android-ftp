package com.ftp.core;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import android.util.Log;

/**
 * Classe qui gére les commandes des utilisateurs
 */
public class UserFTPAction extends Thread {
	private Socket client_socket;
	private ServerFTP server;
	private DataInputStream din;
	private DataOutputStream dout;
    
	public UserFTPAction(ServerFTP server, Socket socket){
		this.client_socket = socket;
		this.server = server;
		try {
			this.din = new DataInputStream(socket.getInputStream());
			this.dout = new DataOutputStream(socket.getOutputStream());
			this.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void run(){
		boolean exit = false;
		while(!exit && client_socket.isConnected()){
			try {
				String command = din.readUTF();//recuperation comande user
				String[] args = command.split(" ");
				Log.i("ftp-client","client "+client_socket.getInetAddress().getHostAddress()+" command :"+command);
				
				if(args[0].equals("LIST")){//ACTION : LIST
					
				}else if(args[0].equals("USER")){//ACTION : USER
					if(args.length == 2){
						this.server.addUserSession(new Session(args[1],client_socket.getInetAddress().getHostAddress()));
					}else{
						this.server.addUserSession(new Session("Anonyme",client_socket.getInetAddress().getHostAddress()));
					}
					
				}else if(args[0].equals("EXIT")){//ACTION : EXIT
					exit = true;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		//fermeture socket
		try {client_socket.close();}
		catch (IOException e) {}
		Log.i("ftp-client","client "+client_socket.getInetAddress().getHostAddress()+" has disconected");
	}
}
