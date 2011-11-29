package igmp;

import java.io.IOException;


public class IGMPServerRunner {
	public static void main(String[] args) throws IOException {
		IGMPSender igmpSender = new IGMPSender();	
		igmpSender.join();
		Thread igmpThread = new Thread(igmpSender);
		igmpThread.start();
	}
	
	
}
