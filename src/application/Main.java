package application;

public class Main {
	
	
	public static void main(String[] args) {
		Messager messager = new Messager();
		String name = args[0];
		int port = Integer.parseInt(args[1]);
		messager.init(name,port);
	}
}
