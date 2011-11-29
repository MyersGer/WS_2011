import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import org.apache.log4j.Logger;


public class IGMPListener implements Runnable{
	private static Logger logger = Logger.getLogger("IGMP_Client");
	final Integer PORT = 9017;

	boolean stop = true;
	private InetAddress group;
	private MulticastSocket socket;

	public void join() throws IOException {
		logger.debug("join()");
		socket.joinGroup(group);
		stop = false;
	}

	public void leave() throws IOException {
		logger.debug("leave()");
		stop = true;
		socket.leaveGroup(group);
	}
	

	public IGMPListener() throws IOException {
		super();
		group = InetAddress.getByName("239.238.237.17");
		socket = new MulticastSocket(PORT);
	}

	
	public void run() {
		logger.debug("run()");
		while (!stop) {
			 byte[] buf = new byte[256];
			 DatagramPacket recv = new DatagramPacket(buf, buf.length);
			 try {
				socket.receive(recv);
				logger.debug("Read from Multicast: " + new String(recv.getData()));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
