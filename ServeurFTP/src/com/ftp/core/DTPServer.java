package com.ftp.core;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import android.util.Log;

public class DTPServer {
	public static final String RACINE_FTP = "/ftp/";
	
	private int port;
	private String ip;
	private ServerSocket data_server;
	private ServerFTP server;
	
	public int getPort() {return port;}
	public void setPort(int port) {	this.port = port;}
	public String getIp() {	return ip;}
	public void setIp(String ip) {this.ip = ip;}
	public ServerSocket getDataServer(){return this.data_server;}
	
	public DTPServer(ServerFTP server, String ip,int port) throws IOException{
		this.server = server;
		data_server = new ServerSocket(port);
		
		init();
	}
	
	public void statServer(int port) throws IOException{
		try {
			if(! data_server.isClosed())
				data_server.close();
		} catch (IOException e) {}
		
		data_server = new ServerSocket(port);
	}
	
	public void stopServer(){
		try {
			if(! data_server.isClosed())
				data_server.close();
		} catch (IOException e) {}
	}
	
	public void sendList(String path){
		try {
			String cmd ="";
			Socket client = data_server.accept();
			File rep = new File(server.getAppDirectory()+RACINE_FTP+path);
			Log.i("dtp-server","acessing to "+rep.getAbsolutePath());
			if(rep.isDirectory()){
				PrintStream p = new PrintStream(client.getOutputStream(),true);
				for(File file : rep.listFiles()){
					cmd += 	Utils.getUnixFileDescription(file)+"\r\n";
					Log.i("dtp-server","file :"+Utils.getUnixFileDescription(file));
				}
				
				p.println(cmd);
				client.close();
				Log.i("dtp-server","cmd :"+cmd);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void init(){
		File f = new File(server.getAppDirectory()+RACINE_FTP);
		if(! f.exists()){
			f.mkdir();
		}
	}
}
