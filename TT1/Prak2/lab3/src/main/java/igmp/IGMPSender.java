package igmp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import javax.swing.plaf.SliderUI;

import org.apache.log4j.Logger;

public class IGMPSender implements Runnable {
	private static Logger logger = Logger.getLogger("IGMP_Server");
	final Integer PORT = 9017;

	boolean stop = true;
	MulticastSocket socket;
	InetAddress group;

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

	public IGMPSender() throws IOException {
		super();
		group = InetAddress.getByName("239.238.237.17");
		socket = new MulticastSocket(PORT);
	}

	public void run() {
		logger.debug("run()");
		String msg = "Hello from Oliver";
		while (!stop) {
			DatagramPacket hi = new DatagramPacket(msg.getBytes(), msg.length(), group, PORT);
			try {
				logger.debug("send(" + msg + ")");
				socket.send(hi);
				Thread.sleep(2000);

			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

}
