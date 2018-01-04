package controller;

import java.util.ArrayList;
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
		case "MX": 		handleMX();
						break;
		case "M":		handleM();
						break;
		case "CONNECT": handelConnect();
						break;
		case "end":		messager.close();
						break;
		default: 		System.out.println("unavailable");
						break;
		}
	}



	/**
	 * handler for M conmmands. 
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
	 * handler for MX conmmands. 
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
	
	private void send(ArrayList<Peer> targetPeers, String something){
		System.out.println("sending "+something);
		for(Peer targetPeer : targetPeers) {
			messager.getSender().sendMessage(targetPeer,something);
			System.out.println("to "+targetPeer.toString());
		}
	}
}
