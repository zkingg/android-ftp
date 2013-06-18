package com.ftp.core;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Classe Descriptif des sessions utilisateur
 */
public class Session {
	private String ip;
	private String login;
	private char TYPE_DATA = 'I';//par défaut mode tranfert : Binaire
	//A - ASCII text 
	//E - EBCDIC text 
	//I - image (binary data) 
	//L - local format
	
	private boolean is_logged = false;
	private Socket client_data_socket;
	
	public Session(){}
	
	public Session(String login, String ip){
		this.ip = ip;
		this.login = login;
	}
	
	public void setIsLogged(boolean is_logged){this.is_logged = is_logged;}
	public boolean isLogged(){return this.is_logged;}
	public String getIp() {return ip;}
	public void setIp(String ip) {this.ip = ip;	}
	public String getLogin() {return login;}
	public void setLogin(String login) {this.login = login;}
	public void setTypeData(char type){this.TYPE_DATA = type;}
	public char getTypeData(){return this.TYPE_DATA;}
	
	
	public Socket getClientDataSocket(){return this.client_data_socket;}
	public void createClientDataSocket(String ip,int port) throws UnknownHostException, IOException{
		this.client_data_socket = new Socket(ip,port);
	}
	
	public boolean connection(String mdp){
		if(mdp.equals("mdp")){
			return true;
		}else{
			return false;
		}
	}
	
	
	
}
