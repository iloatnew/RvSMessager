package controller;

public class AutoPoking extends Thread{
	
	private boolean terminate;
	
	public AutoPoking(){
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
				System.out.println("poking...");
				curTime = System.currentTimeMillis() / 1000L;
			}
		}
	}
}
