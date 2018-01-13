package controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

import application.Messager;
import model.Peer;

public class Receiving extends Thread {
	
	private boolean terminate;
	private int port;
	private String receivedMessage;
	private String[] receivedMessages;
	private Messager messager;
	
	public Receiving(int port,Messager messager) {
		this.messager = messager;
		this.port = port;
		terminate = false;
		showTextWithFrame("");
		showTextWithFrame("YOU CAN FIND THE RECEIVED MESSAGE HERE");
		showTextWithFrame("");
	}
	
	public void terminate() {
		terminate = true;
	}
	
	/**
	 * read and split the header of messages,
	 * then let {@link #checkMessageFormat(String)} recognize the type of messages
	 * in addition, because a receiving thread runs always in the background, it can also take the job of removing inactive peers
	 * this {@link #messager.deleteInactivPeers()} should be done every second
	 */
	@Override
	public void run(){
		try {
	         ServerSocket serverSocket = new ServerSocket(port);
	         while(!terminate) {
		         Socket socket = serverSocket.accept();
		         BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		         receivedMessage = br.readLine();
		         receivedMessages = receivedMessage.split("\\s+");
		         checkMessageFormat(receivedMessages[0]);
		         
	         }
	         serverSocket.close();
	      } catch (IOException e) {
	         e.printStackTrace();
	      }
	}

	/**
	 * to define the type of messages and give them to relative handler
	 * @param format
	 */
	private void checkMessageFormat(String format) {
		switch (format) {
		case "MESSAGE": 	handelMessage();
							break;
		case "POKE":		handlePoke();
							break;
		case "DISCONNECT":	handleDisconnect();
							break;
		default: 			break;
		}
		
	}

	/**
	 * handle the user messages with type DISCONNECT
	 * when the disconnect message is about the peer itself, will be ignored
	 * otherwise when the peer in the list, remove the peer in the DISCONNECT message from the peer list
	 * and send the same DISCONNECT message to all other peers in peer list
	 */
	private void handleDisconnect() {
		String name = receivedMessages[1];
		String ip = receivedMessages[2];
		int port = Integer.parseInt(receivedMessages[3]);
		Peer toDelete = new Peer(name,ip,port);
		messager.deletePeer(toDelete);
	}

	/**
	 * deal the user messages with type MESSAGE
	 * do nothing but show the text to user
	 */
	private void handelMessage() {
		String text = receivedMessage.substring(
					  receivedMessage.indexOf(receivedMessages[3])+receivedMessages[3].length()+1, 
					  receivedMessage.length()
					  );
		String name = receivedMessages[1];
		showTextWithFrame("");
		showTextWithFrame(name + ": ");
		showTextWithFrame(text);
		showTextWithFrame("");
	}
	
	/**
	 * handle the Poke message
	 */
	private void handlePoke() {
		String name = receivedMessages[1];
		String ip = receivedMessages[2];
		int port = Integer.parseInt(receivedMessages[3]);
		Peer targetPeer = new Peer(name, ip, port);
		messager.refreshPeerList(targetPeer);
	}
	
	private static void showTextWithFrame(String text) {
		if(text.length()==0) {
			for(int i=0;i<80;i++) {
				System.out.print(" ");
			}
			for(int i=0;i<40;i++) {
				System.out.print("×");
			}
			System.out.println("");	
		}
		else if(text.length()<36) {
			for(int i=0;i<80;i++) {
				System.out.print(" ");
			}
			String first = "* "+text;
			System.out.print(first);
			for(int i =(40-first.length())-1;i>0;i--) {
				System.out.print(" ");
			}
			System.out.println("*");
		}
		else {
			String first = text.substring(0, 35);
			String last = text.substring(35, text.length());
			showTextWithFrame(first);
			showTextWithFrame(last);
		}
	}

}
