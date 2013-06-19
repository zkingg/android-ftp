package com.ftp.core;

import java.io.File;

import android.util.Log;

/**
 * Classe Descriptif des sessions utilisateur
 */
public class Session {
	private String ip;
	private String login;
	
	private boolean is_logged = false;
	private String current_directory = "/";
	
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

	public void setCurrentDirectory(String directory){this.current_directory = directory;}
	public String getCurrentDirectory(){return this.current_directory;}	
	
	public boolean connection(String mdp){
		if(mdp.equals("mdp")){
			return true;
		}else{
			return false;
		}
	}
	
}
