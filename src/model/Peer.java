package model;
public class Peer {
	
	private String name;
	private String ip;
	private int port;
	private long pokeTime;
	
	public Peer(String name, String ip, int port) {
		this.name = name;
		this.ip = ip;
		this.port = port;		
	}
	
	public String getName () {
		return name;
	}
	
	public String getIp () {
		return ip;
	}
	
	public void setPort(int port) {
		this.port = port;
	}
	
	public int getPort () {
		return port;
	}

	public boolean sameAddress(Peer targetPeer) {
		return (this.getIp().equals(targetPeer.getIp()) && this.getPort() == targetPeer.getPort());
	}
	
	@Override
	public String toString() {
		return name + " " + ip + " " + port;
	}

	public boolean samePeer(Peer otherPeer) {
		if(this.toString().equals(otherPeer.toString()))
			return true;
		else 
			return false;
	}
	
	public long getPokeTime() {
		return pokeTime;
	}

	public void setPokeTime(long pokeTime) {
		this.pokeTime = pokeTime;
	}

	public boolean sameNanme(Peer targetPeer) {
		return this.getName().equals(targetPeer.getName());
	}

	public void setName(String name) {
		this.name = name;
		
	}

}
