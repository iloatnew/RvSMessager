package controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import model.*;
import application.*;

public class ReadInputCommand extends Thread{
	
	private boolean terminate;
	private String userInput;
	private Messager messager;
	private String[] inputs;
	
	public ReadInputCommand(Messager messager){
		this.messager = messager;
		terminate = false;
		userInput = new String();
	}
	
	public void terminate() {
		terminate = true;
	}
	
	/**
	 * read input and let {@link #checkFormat(String)} check the type of commands
	 */
	@Override
	public void run(){
		Scanner input = new Scanner(System.in);
		while(!terminate) {
			try {
				userInput = input.nextLine();
				inputs = userInput.split("\\s+");
				checkFormat(inputs[0]);
			} catch (Exception e) {
				e.getStackTrace();
			}
		}
		input.close();
	}

	/**
	 * check the type of commands and give them to relative handler
	 * @param format
	 */
	private void checkFormat(String format) {
		switch (format) {
		case "MX": 			handleMX();
							break;
		case "M":			handleM();
							break;
		case "CONNECT": 	handelConnect();
							break;
		case "DISCONNECT":	handelDisconnect(); 
							break;
		case "EXIT":		handelDisconnect();
							messager.close();
							break;
		case "HELP":        showTextWithFrame("");
							showTextWithFrame("M 'NAME' 'TEXT' : ");
							showTextWithFrame("--Send 'TEXT' to contact with 'NAME' in the peer list");
							showTextWithFrame(" ");
							showTextWithFrame("MX 'IP' 'PORT' 'TEXT' :");
							showTextWithFrame("--Send 'TEXT' to 'IP' + 'PORT'");
							showTextWithFrame(" ");
							showTextWithFrame("CONNECT 'IP' 'PORT' :");
							showTextWithFrame("--Add contact with 'IP' 'PORT' to peer list");
							showTextWithFrame(" ");
							showTextWithFrame("DISCONNECT :");
							showTextWithFrame("--Remove this peer from the peer lists from all users");
							showTextWithFrame(" ");
							showTextWithFrame("EXIT : ");
							showTextWithFrame("--Disconnect and end the program");
							showTextWithFrame("");
							break;
		default: 			System.out.println("unavailable");
							break;
		}
	}


	/**
	 * handler for DISCONNECT commands
	 * it create and send "DISCONNECT" message to all peers in the peerlist
	 */
	private void handelDisconnect() {
		send(messager.getPeerList(), creatDCMessage());
		// let deletePeer() in messager send the DISCONNECT message. here will the message not be send
		messager.deletePeer(messager.getLocalPeer());
		
	}


	/**
	 * handler for M commands. 
	 * it calls {@link #createMMessage()} to create standard MESSAGE type message
	 */
	private void handleM() {
		String name = inputs[1];
		ArrayList<Peer> targetPeers = messager.searchPeers(new Peer(name,"1.1.1.1",-1));
		String message = creatMMessage();
		if(targetPeers.size()>0) {
			send(targetPeers, message);
		}
	}

	/**
	 * handler for MX commands. 
	 * it calls {@link #createMXMessage()} to create standard MESSAGE type message
	 */
	private void handleMX() {
		String ip = inputs[1];
		int port = Integer.parseInt(inputs[2]);
		Peer musterPeer = new Peer("unknown",ip,port);
		ArrayList<Peer> targetPeers = messager.searchPeers(musterPeer);
		String message = createMXMessage();
		send(targetPeers, message);
	}

	/**
	 * send Poke message
	 */
	private void handelConnect() {
		String ip = inputs[1];
		int port = Integer.parseInt(inputs[2]);
		Peer musterPeer = new Peer("unknown",ip,port);
		ArrayList<Peer> targetPeers = messager.searchPeers(musterPeer);
		String message = creatPokeMessage();
		send(targetPeers, message);
		messager.getSender().startPoking();
	}
	
	private String creatPokeMessage() {
		return "POKE "+ messager.getLocalPeer().toString();
	}

	private String createMXMessage() {
		String text = userInput.substring(userInput.indexOf(inputs[2]) + (inputs[2].length()+1) , 
										  userInput.length());
		text = "MESSAGE " + messager.getLocalPeer().toString()+" "+text;
		return text;
	}

	private String creatMMessage() {
		String text = userInput.substring(userInput.indexOf(inputs[1]) + (inputs[1].length()+1) , 
				  userInput.length());
		text = "MESSAGE " + messager.getLocalPeer().toString()+" "+text;
		return text;
	}

	private String creatDCMessage() {
		return "DISCONNECT " + messager.getLocalPeer().toString();
	}
	
	/**
	 * the main send activity. all messages, include POKE, MESSAGE, DISCONNECT will be send use this method
	 * @param targetPeers = the receiver
	 * @param something = the message
	 */
	public void send(List<Peer> targetPeers, String something){
//		System.out.println("sending "+something);
		for(Peer targetPeer : targetPeers) {
			messager.getSender().sendMessage(targetPeer,something);
//			System.out.println(" to "+targetPeer.toString());
		}
	}
	
	private static void showTextWithFrame(String text) {
		if(text.length()==0) {
			for(int i=0;i<40;i++) {
				System.out.print("*");
			}
			System.out.println("");	
		}
		else if(text.length()<36) {
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
