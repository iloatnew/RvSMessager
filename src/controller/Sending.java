package controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import model.Peer;

public class Sending extends Thread {
	private Socket socket;
	private PrintWriter printWriter;
	private Peer targetPeer;
	private String text;
	
	public Sending(Peer targetPeer, String text ){
		this.targetPeer = targetPeer;
		this.text = text;
	}
	
	@Override
	public void run(){
		socket = new Socket();
		String ip = targetPeer.getIp();
		int port = targetPeer.getPort();
		try {
			socket.connect(new InetSocketAddress(ip,port),5000);
			printWriter = new PrintWriter(socket.getOutputStream());
			printWriter.write(text);
			printWriter.flush();
			printWriter.close();
            socket.close();
		} catch (SocketTimeoutException timeoutException) {
			System.out.println("time out!");
		} catch (SocketException socketException) {
			System.out.println("connect " + ip + " "+port + " faild!");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String getMyIp() {
		Socket socket = new Socket();
		try {
			socket.connect(new InetSocketAddress("ebay.com", 80));
			InetAddress ip =  socket.getLocalAddress();
			socket.close();
			return ip.toString();
		} catch (IOException e) {
			System.out.println("failed to get local IP!");
			e.printStackTrace();
			//TODO rebot system
		}
		return null;
	}

	public String getMyName() {

		java.net.InetAddress addr;
		try {
			addr = java.net.InetAddress.getLocalHost();
			return addr.getHostName();
		} catch (UnknownHostException e) {
			System.out.println("failed to get local Name!");
			e.printStackTrace();
			//TODO rebot system
		}
	    return null;
	}
}
