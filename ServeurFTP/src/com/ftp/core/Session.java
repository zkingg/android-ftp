package com.ftp.core;

/**
 * Classe Descriptif des sessions utilisateur
 */
public class Session {
	private String ip;
	private String login;
	
	public Session(){}
	
	public Session(String login, String ip){
		this.ip = ip;
		this.login = login;
	}
	
	public String getIp() {return ip;}
	public void setIp(String ip) {this.ip = ip;	}
	public String getLogin() {return login;}
	public void setLogin(String login) {this.login = login;}
	
}
