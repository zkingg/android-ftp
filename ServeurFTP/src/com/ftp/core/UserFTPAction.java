package com.ftp.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.net.SocketTimeoutException;

import android.os.Build;
import android.util.Log;

/**
 * Classe qui g�re les commandes des utilisateurs
 */
public class UserFTPAction extends Thread {
	private Socket client_socket;
	private ServerFTP server;
	private DTPServer dtp_server;
	private Session session;
	
	public UserFTPAction(ServerFTP server, Socket socket){
		this.client_socket = socket;
		this.server = server;
		this.session = new Session(server.getActivity());
		this.session.setIp(client_socket.getInetAddress().getHostAddress());
		this.dtp_server = new DTPServer(server);
		reply(220, "Bienvenue sur le server FTP");
		this.start();
	}
	
	/**
	 * Recupere la commande du client ftp et met en forme les donn�es
	 * @return
	 */
	private String[] getClientCommand(){
		String[] result_command = new String[2];
		String command = reception();
		String[] args = command.split(" ");
		
		Log.i("ftp-client","client "+session.getIp()+" command :"+command);
		if(args.length == 2){
			result_command[1] = args[1];
		}else if(args.length > 2){
			for(int i=2;i<args.length;i++){
				args[1] += " "+args[i];	
			}
			result_command[1] = args[1];
		}
		
		result_command[0] = args[0];
		return result_command;
	}
	
	@Override
	public void run(){
		boolean exit = false;
		while(!exit && client_socket.isConnected()){
			try {
				String[] args = getClientCommand();
				if(args[0].equals("USER")){//ACTION : USER : connection
					session.setLogin(args[1]);
					
					if(! this.server.anonymousConnectionAllowed()){//si need mdp
						reply(331, "Password needed");
						args = getClientCommand();
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
					reply(221, "Goodbye.");
					exit = true;
					
				}else if(args[0].equals("FEAT")){//ACTION : FEAT : Retourne command dispo
					envoi("211-Extensions supported");
					envoi(" PASV");
					reply(211,"End");
					
				}else if(args[0].equals("SYST")){//ACTION : SYST : Info systeme
					reply(215, " Android "+Build.DISPLAY);
					
				}else if(args[0].equals("TYPE")){//ACTION : TYPE : precise le format des donn�es qui seront envoy�
					dtp_server.setTypeData(args[1].charAt(0));
					reply(200,"Type set to "+args[1].charAt(0));
					
				}else if(args[0].equals("PASV")){//ACTION : PASV : d�clenche mode passif
					String[] inet_num = Utils.getIPAddress(true).split("\\.");
					int port = this.server.getDataPort();
					dtp_server.setTransfertMode(DTPServer.PASIVE_MODE);
					reply(227,"Entering Passive Mode ("+inet_num[0]+","+inet_num[1]+","+inet_num[2]+","+inet_num[3]+","+((int)port/256)+","+port%256+").");
				
				}else if(args[0].equals("PORT")){//ACTION : PORT : Activation mode actif
					/** Mettre a jour ip serverdata **/
					String[] inet_address = args[1].split("\\,");
					String ip = inet_address[0]+"."+inet_address[1]+"."+inet_address[2]+"."+inet_address[3];
					
					try {
						int port = Integer.parseInt(inet_address[4])*256 + Integer.parseInt(inet_address[5]);
						dtp_server.setIp(ip);
						dtp_server.setPort(port);
						dtp_server.setTransfertMode(DTPServer.ACTIVE_MODE);
						reply(200, "PORT command successful.");
					} catch (Exception e) {
						reply(500, "Wrong parameters given .");
					}
					

					
				/** File explorer commands **/	
				}else if(args[0].equals("PWD")){//ACTION : PWD : Retourne chemin courant
					reply(257,"\""+dtp_server.getCurrentDirectory()+"\" is current directory.");
					
				}else if(args[0].equals("CDUP")){//ACTION : CDUP : ce deplacer vers repertoire 
					dtp_server.setCurrentDirectory(dtp_server.getParentDirectory());
					reply(250, "CDUP command successful.");
					
				}else if(args[0].equals("LIST")){//ACTION : LIST : renvoye la liste des fichiers et r�pertoires pr�sents dans le r�pertoire courant
					reply(150,"Opening ASCII mode data connection for file list");
					//recup list dossier
					dtp_server.sendList(args[1]);
					reply(226,"Transfer complete");
					
				}else if(args[0].equals("NLST")){//ACTION : NLST : renvoye la liste des fichiers et r�pertoires pr�sents dans le r�pertoire courant
					reply(150,"Opening ASCII mode data connection for file list");
					//recup list dossier
					dtp_server.sendList(null);
					reply(226,"Transfer complete");	
					
				}else if(args[0].equals("CWD")){//ACTION : CWD : Selection du repertoire de destination
					dtp_server.setCurrentDirectory(args[1]);
					reply(250, "CWD command successful.");
					
				}else if(args[0].equals("ABOR")){//ACTION : ABOR : Interuption d'un telechargement 
					reply(500, "Command Not yet implemented :"+args[0]);
					
				}else if(args[0].equals("DELE")){//ACTION : DELE : supprime un fichier
					if(dtp_server.removeFile(args[1]))
						reply(250, "DELE command successful.");
					else
						reply(550,"could not remove file");
				
				}else if(args[0].equals("RMD")){//ACTION : RMD : supprime un repertoire
					if(dtp_server.removeDirectory(args[1]))
						reply(250, "RMD command successful.");
					else
						reply(550,"could not remove directory");
				
				}else if(args[0].equals("MKD")){//ACTION : MKD : cr�er un r�pertoire
					if(dtp_server.createDirectory(args[1]))
						reply(257,"Directory created");
					else
						reply(550,"Create new directory faillure");					
					
				}else if(args[0].equals("RETR")){//ACTION : RETR : Recuperation du fichier passer en param�tre
					reply(500, "Command Not yet implemented :"+args[0]);
					
				}else if(args[0].equals("STOR")){//ACTION : STOR : Stockage du fichier donn�, �crase si d�j� exitant
					reply(500, "Command Not yet implemented :"+args[0]);
					
				}else if(args[0].equals("RNFR")){//ACTION : RNFR : Renomage de fichier
					String source = args[1];
					if(dtp_server.fileExist(source)){
						reply(350,"File ready to rename");
						args = getClientCommand();
						if(args[0].equals("RNTO")){
							if(dtp_server.renameFile(source, args[1]))
								reply(250,"Sucess rename");
							else
								reply(553,"Faillure rename");
						}else{
							reply(553,"Not expected command");
						}						
					}	
					else
						reply(550,"File not found");

				}else if(args[0].equals("STOU")){//ACTION : STOU : Stockage du fichier donn�, nom unique g�n�r�, nom retourn� dans reponse
					reply(500, "Command Not yet implemented :"+args[0]);
					
				}else if(args[0].equals("")){//si aucune action -- connexion interompue
					Thread.sleep(3000);
					exit = true;
				
				}
				else{//ACTION UNDEFINED 
					reply(500, "Command Undefined :"+args[0]);
				}
			} catch (Exception e) {
				reply(500, "Internal server error");
				e.printStackTrace();
			}
		}
		
		//fermeture socket
		try {client_socket.close();}
		catch (IOException e) {}
		this.server.removeUserSession(session.getIp());
		Log.i("ftp-client","client "+session.getIp()+" has disconected");
	}
	
	/*
	 * Envoi les donn�es sur DI du client
	 */
	private void envoi(String str){
		try {
			PrintStream p = new PrintStream(client_socket.getOutputStream());
			Log.v("","Envoi en cour ...");
			p.println(str);		
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
	
	/*
	 * Recupere les donn�es du client
	 */
	private String reception(){
		try {
			String rep= new BufferedReader(new InputStreamReader(client_socket.getInputStream())).readLine();
			//System.out.println("r�ponse :"+rep);
			/*while(rep == null){
				Thread.sleep(1000);
				Log.v("","r�ponse :"+rep+" from "+client_socket.getInetAddress().getHostAddress()+":"+client_socket.getPort());

				rep = reception();
			}*/
				
			Log.v("","r�ponse :"+rep);
			return rep!=null? rep : "";
		}
		catch (SocketTimeoutException e) {
			
		} catch (IOException e) {
			e.printStackTrace();
		}/* catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		
		return null;
	}
	
	/**
	 * Envoi un code de retour avec un msg personalis�
	 * @param code : code de retour
	 * @param msg
	 * @return code de retour
	 */
	public int reply(int code,String msg){
		envoi(code+" "+msg);
		Log.i("ftp-client","reply code "+code+" to "+session.getIp());
		return code;
	}
}
