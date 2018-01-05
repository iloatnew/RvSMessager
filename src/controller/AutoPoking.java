package controller;

import java.util.List;
import application.Messager;
import model.Peer;

public class AutoPoking extends Thread{
	
	private boolean terminate;
	private List<Peer> peerList;
	private Messager messager;
	private String poke;
	
	public AutoPoking(Messager messager){
		poke = "";
		this.messager = messager;
		terminate = false;
	}
	
	public void terminate() {
		terminate = true;
	}
	
	@Override
	public void run(){
		long curTime = System.currentTimeMillis() / 1000L;
		while(!terminate) {
			if(System.currentTimeMillis() / 1000L == (curTime + 30L)) {
				curTime = System.currentTimeMillis() / 1000L;
				messager.deleteInactivPeers(curTime);
				sendPokeToEveryone();
			}
		}
	}

	/**
	 * send poke {@link #getPoke()} to every peers in the {@link #messager.getPeerList()}
	 */
	public void sendPokeToEveryone() {
		// need refresh list here, in order to get the newest list
		peerList = messager.getPeerList();
		if(poke.length()>0 && peerList.size()>0){
			messager.getControllCenter().send(peerList, poke);
		}
		else{
			if(peerList.size()>0){
				System.out.println("poke failed: poke message was not created!");
			}
		}
		
	}

	public List<Peer> getPeerList() {
		return peerList;
	}

	public void setPeerList(List<Peer> peerList) {
		this.peerList = peerList;
	}

	public String getPoke() {
		return poke;
	}

	public void setPoke(String poke) {
		this.poke = poke;
	}
}
