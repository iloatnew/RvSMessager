package application;
import model.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import controller.*;

public class Messager {

	private Sender sender; 
	private Receiving receiving;
	private ReadInputCommand readInputCommand;
	private List<Peer> peerList;
	private Peer localPeer;
	
	public Messager() {
	}
	
	public void init(String name, int port) {
		showTextWithFrame("");
		showTextWithFrame("YOU CAN FIND ALL SYSTEM INFORMATION HERE");
		showTextWithFrame("");
		//init receiver
		//int port = (int) (1000+Math.random()*10000);
		receiving = new Receiving(port,this);
		receiving.start();
		
		//init sender
		sender = new Sender(this);
		
		//init command handler
		readInputCommand = new ReadInputCommand(this);
		readInputCommand.start();
		
		//init peerList
		peerList = new ArrayList<Peer>();
		peerList = Collections.synchronizedList(peerList);
		localPeer = sender.getLocalPeer();
		localPeer.setPort(port);
		localPeer.setName(name);
		refreshPeerList(localPeer);
		
		showTextWithFrame("");
		showTextWithFrame("localhost information: ");
		showTextWithFrame(localPeer.toString());
		showTextWithFrame("");

		System.out.println("Please give commands...Type 'HELP' to see examples");
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
	 * add peer into peer list, use current time as poke time
	 * if it is a new peer, send poke {@link #sender.poke(Peer targetPeer)} of it to all peers in list.
	 * @param targetPeer
	 */
	public void refreshPeerList(Peer targetPeer) {
		synchronized (peerList){
			boolean existPeer = false;
			for(Peer onePeer : peerList) {
				if(onePeer.sameAddress(targetPeer)) {
					
					// get the real name through POKE
					if(onePeer.getName().equals("unknown")){
						onePeer.setName(targetPeer.getName());
					}
							
					onePeer.setPokeTime(System.currentTimeMillis()/1000L);
					existPeer = true;
				}
			}
			if(!existPeer) {
				targetPeer.setPokeTime(System.currentTimeMillis()/1000L);
				peerList.add(targetPeer);
				sender.poke(targetPeer);
				sender.poke(getLocalPeer());
				showTextWithFrame("");
				showTextWithFrame("current peerlist: ");
				for(Peer onePeer : peerList) {
					showTextWithFrame(onePeer.toString());
				}
				showTextWithFrame("");
			}
		}
	}
	
	/**
	 * will be called after received a POKE or M message
	 * it checks the current peer list. 
	 * if the sender of POKE is not in the list, add it in
	 * if the name of M was found, send the MESSAGE
	 * @param musterPeer the condition of the searching
	 * @return
	 */
	public ArrayList<Peer> searchPeers(Peer musterPeer) {
		ArrayList<Peer> targetPeers = new ArrayList<Peer>();
		synchronized (peerList){
			boolean existPeer = false;
			for(Peer onePeer : peerList) {
				if(onePeer.sameAddress(musterPeer) || onePeer.sameNanme(musterPeer)) {
					existPeer = true;
					targetPeers.add(onePeer);
				}
			}
			if(!existPeer) {
				if(musterPeer.getPort()!=-1){
					targetPeers.add(musterPeer);
				}
				else {
					showTextWithFrame("");
					showTextWithFrame("M failed: name not found!");
					showTextWithFrame("");
				}
			}
		}
		return targetPeers;
	}
	
	/**
	 * when toDelete is localpeer, then it means local peer wants to disconnect
	 * all the peers in the list will be deleted
	 * otherwise delete the peer toDelete when it is found in the peer list
	 * otherwise do nothing
	 * @param toDelete = the peer to delete
	 */
	public void deletePeer(Peer toDelete) {
		synchronized (peerList){
			boolean changed = false;
			Iterator<Peer> peerItr = peerList.iterator(); 
			while(peerItr.hasNext()){
				Peer nextPeer = peerItr.next();
				// it removes all peers, when it received Disconnect itself message
				if(toDelete.samePeer(localPeer)) {
					peerItr.remove();
					changed = true;
				}
				else {
					//System.out.println("compairing "+ nextPeer.toString()+" "+toDelete.toString());
					if(nextPeer.samePeer(toDelete)){
						changed = true;
						showTextWithFrame("");
						showTextWithFrame("removing: "+nextPeer.toString());
						showTextWithFrame("");
						peerItr.remove();
					}
				}
			}
			if(changed) {
				showTextWithFrame("");
				showTextWithFrame("current peerlist: ");
				for(Peer onePeer : peerList) {
					showTextWithFrame(onePeer.toString());
				}
				showTextWithFrame("");
			}
		}
	}
	
	public void deleteInactivPeers(long curTime) {
		boolean changed = false;
		synchronized (peerList){
			Iterator<Peer> peerItr = peerList.iterator(); 
			while(peerItr.hasNext()){
				Peer next = peerItr.next();
				if(next.getPokeTime()+60L<=curTime && !next.samePeer(localPeer)){
					showTextWithFrame("");
					showTextWithFrame("removing inactive peer: "+next.toString());
					showTextWithFrame("");
					peerItr.remove();
					changed = true;
				}
			}
			if(changed) {
				showTextWithFrame("");
				showTextWithFrame("current peerlist: ");
				for(Peer onePeer : peerList) {
					showTextWithFrame(onePeer.toString()+" POKING TIME: "+(System.currentTimeMillis()/1000L - onePeer.getPokeTime()));
				}
				showTextWithFrame("");
			}
		}
		
	}
	
	public Peer getLocalPeer() {
		return localPeer;
	}
	
	public ReadInputCommand getControllCenter(){
		return readInputCommand;
	}
	
	public List<Peer> getPeerList(){
		synchronized (peerList){
			return this.peerList;
		}
	}
	
	private static void showTextWithFrame(String text) {
		if(text.length()==0) {
			for(int i=0;i<40;i++) {
				System.out.print(" ");
			}
			for(int i=0;i<40;i++) {
				System.out.print("*");
			}
			System.out.println("");	
		}
		else if(text.length()<36) {
			for(int i=0;i<40;i++) {
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
