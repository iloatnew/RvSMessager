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
		

		System.out.println("localhost information: ");
		System.out.println(localPeer.toString());
		System.out.println("########################################################");
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
				System.out.println("current peerlist: ");
				for(Peer onePeer : peerList) {
					System.out.println(onePeer.toString());
				}
				System.out.println("########################################################");
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
					System.out.println("M failed: name not found!");
				}
			}
		}
		return targetPeers;
	}
	
	/**
	 * delete the peer toDelete when it is found in the peer list
	 * otherwise do nothing
	 * @param toDelete = the peer to delete
	 * @param disconnect = the DISCONNECT message, can be passed to other peers in the peer list
	 */
	public void deletePeer(Peer toDelete, String disconnect) {
		synchronized (peerList){
			Iterator<Peer> peerItr = peerList.iterator(); 
			while(peerItr.hasNext()){
				Peer next = peerItr.next();
				if(next.sameAddress(toDelete) && next.sameNanme(toDelete)){
					System.out.println("removing: "+next.toString());
					peerItr.remove();
					readInputCommand.send(peerList, disconnect);
				}
			}
			System.out.println("current peerlist: ");
			for(Peer onePeer : peerList) {
				System.out.println(onePeer.toString());
			}
			System.out.println("########################################################");
		}
	}
	
	public void deleteInactivPeers(long curTime) {
		System.out.println("checking inactive peer... ");
		synchronized (peerList){
			Iterator<Peer> peerItr = peerList.iterator(); 
			while(peerItr.hasNext()){
				Peer next = peerItr.next();
				if(next.getPokeTime()+60L<=curTime && next.getIp()!=localPeer.getIp()){
					System.out.println("removing inactive peer: "+next.toString());
					peerItr.remove();
				}
			}
			System.out.println("current peerlist: ");
			for(Peer onePeer : peerList) {
				System.out.println(onePeer.toString()+" POKING TIME: "+(System.currentTimeMillis()/1000L - onePeer.getPokeTime()));
			}
			System.out.println("########################################################");
		}
		
	}
	
	public Peer getLocalPeer() {
		return localPeer;
	}
	
	public ReadInputCommand getControllCenter(){
		return readInputCommand;
	}
	
	public List<Peer> getPeerList(){
		return this.peerList;
	}
}
