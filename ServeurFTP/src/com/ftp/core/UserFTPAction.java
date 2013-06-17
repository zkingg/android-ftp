package com.ftp.core;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketTimeoutException;

import android.util.Log;

/**
 * Classe qui gére les commandes des utilisateurs
 */
public class UserFTPAction extends Thread {
	private Socket client_socket;
	private ServerFTP server;
	/*private DataInputStream din;
	private DataOutputStream dout;*/
	private BufferedReader reader;
	private PrintWriter writer;
	
	public UserFTPAction(ServerFTP server, Socket socket){
		this.client_socket = socket;
		this.server = server;
		try {
			/*this.din = new DataInputStream(socket.getInputStream());
			this.dout = new DataOutputStream(socket.getOutputStream());*/
			reader = new BufferedReader(new InputStreamReader(client_socket.getInputStream()));
			writer = new PrintWriter(new OutputStreamWriter(client_socket.getOutputStream()), true);
			reply(220, "Bienvenue sur le server FTP");
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
				Thread.sleep(2000);//~debug a virer
				String command = reception();
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
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		//fermeture socket
		try {client_socket.close();}
		catch (IOException e) {}
		Log.i("ftp-client","client "+client_socket.getInetAddress().getHostAddress()+" has disconected");
	}
	
	private void envoi(String str){
		try {
			PrintStream p = new PrintStream(client_socket.getOutputStream());
			System.out.println("envoi en cour ...");
			p.println(str);		
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
	
	private String reception(){
		try {
			String line = "";
			String tmp_line = "";
			while ((tmp_line = reader.readLine()) != null) {
			    line += tmp_line;
			}
			
			System.out.println("réponse :"+line);
			return line;
		}
		catch (SocketTimeoutException e) {
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

	}
	
	public int reply(int code,String msg){
		this.writer.println(code+" "+msg);
		return code;
	}
}
