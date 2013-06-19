package com.example.ftpclient;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;

public class FtpClient {
	
	private FtpClient() {}
	
	private static class FtpClientHolder {
		private final static FTPClient instance = new FTPClient();
	}
	
	private static class FtpClientConfigHolder {
		private final static FTPClientConfig instance = new FTPClientConfig();
	}
	
	public static FTPClient getFtpClient() {
		return FtpClientHolder.instance;
	}
	
	public static FTPClientConfig getFtpClientConfig() {
		return FtpClientConfigHolder.instance;
	}
	
}
