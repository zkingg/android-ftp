package com.ftp.core;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;

import org.apache.http.message.BufferedHeader;

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
	
	public String sendList(String path){
		String list_dir = "";
		try {
			Socket client = data_server.accept();
			File rep = new File(server.getAppDirectory()+RACINE_FTP+path);
			Log.i("dtp-server","acessing to "+rep.getAbsolutePath());
			if(rep.isDirectory()){
				PrintStream p = new PrintStream(client.getOutputStream(),true);
				for(File file : rep.listFiles()){
					if(file.isDirectory())
						p.println("d"+file.getName());	
					else
						p.println("-"+file.getName());
					
					Log.i("dtp-server","file"+file.getName());
				}
				p.flush();
			}
			//?????
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return list_dir;
	}
	
	private void init(){
		File f = new File(server.getAppDirectory()+RACINE_FTP);
		if(! f.exists()){
			f.mkdir();
		}
	}
}
