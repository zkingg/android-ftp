package com.ftp.core;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;

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
	private String current_directory = "/";
	private char type_data = 'I';//par d�faut mode tranfert : Binaire
	//A - ASCII text 
	//E - EBCDIC text 
	//I - image (binary data) 
	//L - local format
	
	public void setCurrentDirectory(String directory){
		if(directory.charAt(0) == '/')//chemin absolu
			this.current_directory = directory;
		else//chemin relatif
			this.current_directory += "/"+directory;
	}
	public String getCurrentDirectory(){return this.current_directory;}	
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
		
		init();
	}
	
	@Override
	protected void finalize() throws Throwable {
		stopServer();
		super.finalize();
	}
	
	/**
	 * Si mode passive :
	 * 	d�marre serveur dft et attend connexion client
	 * Si mode active :
	 * 	Se connecte sur le port de client ftp
	 * 
	 * @return socket du client
	 * @throws IOException
	 */
	public Socket getClientSocket() throws IOException{
		if(this.transfert_mode == DTPServer.PASIVE_MODE){
			try {
				if(data_server != null && data_server.isBound()){
					data_server.close();
				}
				
				data_server = new ServerSocket(server.getDataPort());
			}
			catch (IOException e) {}
			return data_server.accept();
			
		}else if(this.transfert_mode == DTPServer.ACTIVE_MODE){
			if(ip != null && port > 0 ){
				return new Socket(ip,port);
			}
		}
		
		throw new IOException("Mode de transfert incorrect");
	}
	
	public void stopServer(){
		try {
			if(data_server != null && !data_server.isClosed())
				data_server.close();
			
			Log.i("dtp-server","closing dtp-server");
		} catch (IOException e) {}
	}
	
	/**
	 * Commande List : envoi la liste des repertoire au client ftp
	 * @param path
	 */
	public void sendList(String path){
		Socket client = null;
		try {
			String cmd ="";
			client = this.getClientSocket();
			File rep = null;
			//if(path == null)
				rep = getFileWithSpace(server.getAppDirectory()+RACINE_FTP+current_directory);
			//else//si chemin precis�
				//rep = new File(server.getAppDirectory()+RACINE_FTP+path);
			
			Log.i("dtp-server","acessing to "+rep.getAbsolutePath());
			if(rep.isDirectory()){
				PrintStream p = new PrintStream(client.getOutputStream(),true);
				if(rep.listFiles() != null){
					for(File file : rep.listFiles()){
						cmd += 	Utils.getUnixFileDescription(file)+"\r\n";
						Log.i("dtp-server","file :"+Utils.getUnixFileDescription(file));
					}
				}
				
				p.println(cmd);
				client.close();
				Log.v("dtp-server","cmd :"+cmd);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}finally{
			stopServer();
			try {if(client != null)client.close();}
			catch (IOException e) {}
		}
	}
	
	public boolean removeDirectory(String dir) {
		try {
			File file = getFileWithSpace(server.getAppDirectory()+RACINE_FTP+dir);
			return removeDirectoryAndFile(file);
		} catch (URISyntaxException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public static boolean removeDirectoryAndFile(File dir){
		Log.i("","remove :"+dir.getAbsolutePath());
		if(dir.isDirectory() && dir.listFiles() != null){
			for(File file : dir.listFiles()){
				if(file.isDirectory())
					removeDirectoryAndFile(file);
				else
					file.delete();
			}
		}
		return dir.delete();
	}

	public boolean removeFile(String file_delete) {
		try {
			File file = getFileWithSpace(server.getAppDirectory()+RACINE_FTP+file_delete);
			Log.i("","remove :"+file.getAbsolutePath());
			return removeDirectoryAndFile(file);
		} catch (URISyntaxException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	private void init(){
		File f = new File(server.getAppDirectory()+RACINE_FTP);
		if(! f.exists()){
			f.mkdir();
		}
	}
	
	public boolean fileExist(String file){
		try {
			File f = getFileWithSpace(server.getAppDirectory()+RACINE_FTP+current_directory+"/"+file);
			return f.isDirectory() || f.isFile();
		} catch (URISyntaxException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean renameFile(String source,String destination){
		try {
			File file_src = getFileWithSpace(server.getAppDirectory()+RACINE_FTP+current_directory+"/"+source);
			File file_dst = getFileWithSpace(server.getAppDirectory()+RACINE_FTP+current_directory+"/"+destination);
			Log.i("","rename :"+file_src+" => "+file_dst);
			return file_src.renameTo(file_dst);
		} catch (URISyntaxException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean createDirectory(String dir) {
		// File file = new
		// File(server.getAppDirectory()+RACINE_FTP+current_directory+"/"+dir);
		
		try {
			String path = server.getAppDirectory() + RACINE_FTP + current_directory	+ "/" + dir;
			File file = getFileWithSpace(path);
			Log.i("", "mkdir :" + file.getAbsolutePath());
			return file.mkdir();
		} catch (URISyntaxException e) {
			e.printStackTrace();
			return false;
		}	
	}
	
	public static File getFileWithSpace(String path) throws URISyntaxException {
		URI outputURI = new URI(("file:///" + path.replaceAll(" ", "%20")));
		File file = new File(outputURI);
		return file;
	}

	public String getParentDirectory() {
		if(this.current_directory.equals("/"))
			return current_directory;
		
		String[] tmp = current_directory.split("/");
		String dir_to_skip = tmp[tmp.length-1];
		String parent = (String) current_directory.subSequence(0, current_directory.length()-dir_to_skip.length());
		return parent.equals("")? "/" : parent;
	}
}
