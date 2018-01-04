package controller;

import java.util.ArrayList;

import application.Messager;
import model.Peer;

public class AutoPoking extends Thread{
	
	private boolean terminate;
	private ArrayList<Peer> peerList;
	private Messager messager;
	
	public AutoPoking(Messager messager){
		this.messager = messager;
		terminate = false;
	}
	
	public void terminate() {
		terminate = true;
	}
	
	@Override
	public void run(){
		long curTime = System.currentTimeMillis() / 1000L;
		boolean oneMin = true;
		while(!terminate) {
			if(System.currentTimeMillis() / 1000L == (curTime + 30L)) {
				if(oneMin){
					deleteInActive();
				}
				sendPokeToEveryone();
				curTime = System.currentTimeMillis() / 1000L;
				oneMin = !oneMin;
			}
		}
	}

	private void sendPokeToEveryone() {
		messager.getControllCenter().send(peerList, myPoke);
		
	}

	private void deleteInActive() {
		// TODO Auto-generated method stub
		
	}

	public ArrayList<Peer> getPeerList() {
		return peerList;
	}

	public void setPeerList(ArrayList<Peer> peerList) {
		this.peerList = peerList;
	}
}
