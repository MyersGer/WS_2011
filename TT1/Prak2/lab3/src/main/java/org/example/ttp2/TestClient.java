package org.example.ttp2;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.util.TooManyListenersException;

import javax.sip.InvalidArgumentException;
import javax.sip.ObjectInUseException;
import javax.sip.PeerUnavailableException;
import javax.sip.SipException;
import javax.sip.TransportNotSupportedException;

import org.apache.log4j.Logger;

public class TestClient {
	
	private static final Logger LOGGER = Logger.getLogger("SIPLayerLogger");

	private static final String USERNAME = "Testikel";
	private static final String HOST = "141.22.27.135";
	private static final String PROXY = "tiserver03.cpt.haw-hamburg.de";
	private static final String MULTICAST_GROUP = "239.238.237.17";
	private static final int MULTICAST_PORT = 9017;
	private static final int SIP_PORT = 5060;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			SIPLayer sippy = new SIPLayer(USERNAME, HOST, SIP_PORT);
			UAC client = new UAC(sippy);

			client.sendInvite("Hexren", PROXY);

			IGMPListener listener = new IGMPListener();
		
			listener.initialize(InetAddress.getByName(MULTICAST_GROUP), MULTICAST_PORT);
			new Thread(listener).start();
			
			Thread.sleep(5000);
			LOGGER.info("Wartezeit vorbei");
			
			if (client.removeDialog()) {
				LOGGER.info("RemoveDialog OK");
			} else {
				LOGGER.info("RemoveDialog FAIL");
			}
			
			

		} catch (PeerUnavailableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransportNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ObjectInUseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TooManyListenersException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SipException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}


}
