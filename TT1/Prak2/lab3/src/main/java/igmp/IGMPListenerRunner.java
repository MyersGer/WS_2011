package igmp;

import java.io.IOException;


public class IGMPListenerRunner {
	public static void main(String[] args) throws IOException {
		IGMPListener igmpListener = new IGMPListener();
		igmpListener.join();
		Thread igmpThread = new Thread(igmpListener);
		igmpThread.start();
	}
	
	
}
