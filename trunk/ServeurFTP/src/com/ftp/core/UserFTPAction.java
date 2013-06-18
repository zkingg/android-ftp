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

import android.os.Build;
import android.util.Log;

/**
 * Classe qui gére les commandes des utilisateurs
 */
public class UserFTPAction extends Thread {
	private Socket client_socket;
	private ServerFTP server;
	private Session session;
	
	public UserFTPAction(ServerFTP server, Socket socket){
		this.client_socket = socket;
		this.server = server;
		this.session = new Session();
		this.session.setIp(client_socket.getInetAddress().getHostAddress());
		
		reply(220, "Bienvenue sur le server FTP");
		this.start();

	}
	
	@Override
	public void run(){
		boolean exit = false;
		while(!exit && client_socket.isConnected()){
			try {
				Thread.sleep(1000);
				String command = reception();
				String[] args = command.split(" ");
				Log.i("ftp-client","client "+session.getIp()+" command :"+command);
				
				if(args[0].equals("USER")){//ACTION : USER : connection
					session.setLogin(args[1]);
					
					if(! this.server.anonymousConnectionAllowed()){//si need mdp
						reply(331, "Password needed");
						args = reception().split(" ");
						if(args[0].equals("PASS")){	
							if(session.connection(args[1])){//si conexnion ok
								reply(230, "User connected");
								session.setIsLogged(true);
								this.server.addUserSession(session);
							}else{
								reply(530, "Mot de passe incorrect");
								continue;
							}
						}else{
							//si echec
							reply(530, "Erreur de Protocole");
							continue;
						}
					}else{
						reply(230, "Anonymous connection allowed");
						session.setIsLogged(true);
						this.server.addUserSession(session);
					}
					
				}else if(args[0].equals("QUIT")){//ACTION : EXIT : fermeture socket
					exit = true;
					
				}else if(args[0].equals("SYST")){//ACTION : SYST : Info systeme
					reply(215, " Android "+Build.DISPLAY);
					
				}else if(args[0].equals("TYPE")){//ACTION : TYPE : precise le format des données qui seront envoyé
					session.setTypeData(args[1].charAt(0));
					reply(200,"Type set to "+args[1].charAt(0));
					
				}else if(args[0].equals("PASV")){//ACTION : PASV : déclenche mode passif
					String[] inet_num = this.server.getSocketDataServer().getInetAddress().getHostAddress().split("\\.");
					int port = this.server.getSocketDataServer().getLocalPort();
					reply(227,"Entering Passive Mode ("+inet_num[0]+","+inet_num[1]+","+inet_num[2]+","+inet_num[3]+","+((int)port/256)+","+port%256+").");
				
				}else if(args[0].equals("PORT")){//ACTION : PORT : Choix du port de téléchargement
					String[] inet_address = args[1].split("\\,");
					String ip = inet_address[0]+"."+inet_address[1]+"."+inet_address[2]+"."+inet_address[3];
					
					int port;
					try {
						port = Integer.parseInt(inet_address[4]) + Integer.parseInt(inet_address[5]);
					} catch (Exception e) {
						port = 0;
					}
					
					session.createClientDataSocket(ip, port);
					reply(200, "PORT command successful.");
					
				/** File explorer commands **/	
				}else if(args[0].equals("PWD")){//ACTION : PWD : Retourne chemin courant
					//pas fini ...
					reply(257,"'\\' is current directory.");
					
				}else if(args[0].equals("LIST")){//ACTION : LIST : renvoye la liste des fichiers et répertoires présents dans le répertoire courant
					reply(150,"Transfert in progress");
					//recup list dossier
					reply(226,"Transfert complete");
					
				}else if(args[0].equals("ABOR")){//ACTION : ABOR : Interuption d'un telechargement 
				}else if(args[0].equals("DELE")){//ACTION : DELE : supprime un fichier
				}else if(args[0].equals("RMD")){//ACTION : RMD : supprime un repertoire
				}else if(args[0].equals("MKD")){//ACTION : MKD : créer un répertoire
				}else if(args[0].equals("RETR")){//ACTION : RETR : Recuperation du fichier passer en paramétre
				}else if(args[0].equals("STOR")){//ACTION : STOR : Stockage du fichier donné, écrase si déjà exitant
				}else if(args[0].equals("STOU")){//ACTION : STOU : Stockage du fichier donné, nom unique généré, nom retourné dans reponse
				}else if(args[0].equals("")){//si aucune action -- connexion interompue ??
					Thread.sleep(3000);
					exit = true;
					
				}
				else{//ACTION UNDEFINED 
					reply(500, "Command Undefined :"+args[0]);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		//fermeture socket
		try {client_socket.close();}
		catch (IOException e) {}
		this.server.removeUserSession(session.getIp());
		Log.i("ftp-client","client "+session.getIp()+" has disconected");
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
			String rep= new BufferedReader(new InputStreamReader(client_socket.getInputStream())).readLine();
			System.out.println("réponse :"+rep);
			return rep!=null? rep : "";
		}
		catch (SocketTimeoutException e) {
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

	}
	
	public int reply(int code,String msg){
		//this.writer.println(code+" "+msg);
		envoi(code+" "+msg);
		Log.i("ftp-client","reply code "+code+" to "+session.getIp());
		return code;
	}
}
