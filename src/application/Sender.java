package application;

import controller.*;
import model.*;

public class Sender {

	private AutoPoking autoPoking;
	
	/**
	 * Sender deals all type of sending jobs, includes normal send,
	 * disconnect, connect, poking and so on.
	 */
	public Sender() {
		autoPoking = new AutoPoking();
		autoPoking.start();
	}
 
	/**
	 * close autopoking. this could be called in from RvSMessager
	 */
	public void stopPoking() {
		autoPoking.terminate();
	}
	
	public void sendMessage(Peer targetPeer, String text) {
		Sending sending = new Sending(targetPeer,text);
		sending.start();
	}

	public Peer getLocalPeer() {
		Sending ipTest = new Sending(new Peer("", "", 0), "");
		String myIp = ipTest.getMyIp().replace("/","");
		String myName = ipTest.getMyName();
		int port = -1;
		return new Peer(myName, myIp, port);
	}

}
