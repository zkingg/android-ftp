package com.example.ftpclient;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;

public class FtpClient {
	
	private static FTPClient ftp = null;
	private static FTPClientConfig config = null;
	
	private FtpClient() {
		
	}
	
	public static FTPClient getInstance() {
		if (ftp == null)
			ftp = new FTPClient();
		return ftp;
	}
	
	public static FTPClientConfig getConfig() {
		if (config == null)
			config = new FTPClientConfig();
		return config;
	}
	
}
