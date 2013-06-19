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
	public static final int PASIVE_MODE = 0;
	public static final int ACTIVE_MODE = 1;
	
	private int port;
	private String ip;
	private ServerSocket data_server;
	private ServerFTP server;
	private int transfert_mode = DTPServer.PASIVE_MODE;
	private char type_data = 'I';//par d�faut mode tranfert : Binaire
	//A - ASCII text 
	//E - EBCDIC text 
	//I - image (binary data) 
	//L - local format
	
	public void setTransfertMode(int mode){this.transfert_mode = mode;}
	public int getTransfertMode(){return this.transfert_mode;}
	public void setTypeData(char type){this.type_data = type;}
	public char getTypeData(){return this.type_data;}
	public int getPort() {return port;}
	public void setPort(int port) {	this.port = port;}
	public String getIp() {	return ip;}
	public void setIp(String ip) {this.ip = ip;}
	public ServerSocket getDataServer(){return this.data_server;}
	
	public DTPServer(ServerFTP server) {
		this.server = server;
		//data_server = new ServerSocket(port);
		
		init();
	}
	
	/*public void statServer(int port) throws IOException{
		try {
			if(! data_server.isClosed())
				data_server.close();
		} catch (IOException e) {}
		
		data_server = new ServerSocket(port);
	}*/
	
	public Socket getClientSocket() throws IOException{
		if(this.transfert_mode == DTPServer.PASIVE_MODE){
			if(data_server != null && data_server.isBound()){
				try {data_server.close();}
				catch (IOException e) {}
			}
			
			data_server = new ServerSocket(server.getDataPort());
			return data_server.accept();
		}else if(this.transfert_mode == DTPServer.ACTIVE_MODE){
			return null;
		}else{
			return null;
		}
	}
	
	public void stopServer(){
		try {
			if(data_server != null && !data_server.isClosed())
				data_server.close();
			
			Log.i("dtp-server","closing dtp-server");
		} catch (IOException e) {}
	}
	
	public void sendList(String path){
		try {
			String cmd ="";
			//Socket client = data_server.accept();
			Socket client = this.getClientSocket();
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
		}finally{
			stopServer();
		}
	}
	
	private void init(){
		File f = new File(server.getAppDirectory()+RACINE_FTP);
		if(! f.exists()){
			f.mkdir();
		}
	}
}