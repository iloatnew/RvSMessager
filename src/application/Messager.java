package application;
import model.*;
import java.util.ArrayList;

import controller.*;

public class Messager {

	private Sender sender; 
	private Receiving receiving;
	private ReadInputCommand readInputCommand;
	private ArrayList<Peer> peerList;
	private Peer localPeer;
	
	public Messager() {
	}
	
	public void init() {
		//init receiver
		int port = (int) (1000+Math.random()*10000);
		receiving = new Receiving(port,this);
		
		//init sender
		sender = new Sender();
		
		//init command handler
		readInputCommand = new ReadInputCommand(this);
		readInputCommand.start();
		
		//init peerList
		peerList = new ArrayList<Peer>();
		localPeer = sender.getLocalPeer();
		localPeer.setPort(port);
		refreshPeerList(localPeer);
		

		System.out.println("localhost information: ");
		System.out.println(localPeer.toString());
	}
	
	public Sender getSender() {
		return sender;
	}	

	/**
	 * close all threads and end the program
	 */
	public void close() {
		sender.stopPoking();
		readInputCommand.terminate();
		receiving.terminate();
		System.exit(0);
	}

	/**
	 * add peer into peerlisst, use current time as poketime
	 * @param targetPeer
	 */
	public void refreshPeerList(Peer targetPeer) {
		boolean existPeer = false;
		for(Peer onePeer : peerList) {
			if(onePeer.sameAddress(targetPeer)) {
				onePeer.setPokeTime(System.currentTimeMillis()/1000L);
				existPeer = true;
			}
		}
		if(!existPeer) {
			targetPeer.setPokeTime(System.currentTimeMillis()/1000L);
			peerList.add(targetPeer);
		}
		System.out.println("current peerlist: ");
		for(Peer onePeer : peerList) {
			System.out.println(onePeer.toString());
		}
	}
	
	public Peer getLocalPeer() {
		return localPeer;
	}

	public ArrayList<Peer> searchPeers(Peer musterPeer) {
		ArrayList<Peer> targetPeers = new ArrayList<Peer>();
		boolean existPeer = false;
		for(Peer onePeer : peerList) {
			if(onePeer.sameAddress(musterPeer) || onePeer.sameNanme(musterPeer)) {
				existPeer = true;
				targetPeers.add(onePeer);
			}
		}
		if(!existPeer) {
			if(musterPeer.getPort()!=-1)
				targetPeers.add(musterPeer);
			else {
				System.out.println("M failed: name not found!");
			}
		}
		return targetPeers;
	}
}
