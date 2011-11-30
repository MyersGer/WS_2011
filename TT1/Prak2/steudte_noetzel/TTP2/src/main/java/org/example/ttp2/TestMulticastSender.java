package org.example.ttp2;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class TestMulticastSender {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		String ip = "239.238.237.17";
		int port = 9017;
		
		IGMPSender sender = new IGMPSender();
		
		try {
			
			sender.initialize(InetAddress.getByName(ip), port);
			new Thread(sender).run();
			
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

}
