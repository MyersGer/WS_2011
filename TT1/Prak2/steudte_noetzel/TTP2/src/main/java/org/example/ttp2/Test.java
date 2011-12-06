package org.example.ttp2;

import java.text.ParseException;
import java.util.TooManyListenersException;

import javax.sip.InvalidArgumentException;
import javax.sip.ObjectInUseException;
import javax.sip.PeerUnavailableException;
import javax.sip.SipException;
import javax.sip.TransportNotSupportedException;

public class Test {
	
	private static String USERNAME = "Test";
	private static String HOST = "141.22.27.133";
	private static String PROXY = "tiserver03.cpt.haw-hamburg.de";
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			SIPLayer sippy = new SIPLayer(USERNAME, HOST, 5060);
			UAC client = new UAC(sippy);
			UAS server = new UAS(sippy);
			server.registerAtProxy(PROXY, 5060);
			client.sendInvite(USERNAME, PROXY);
			
			//client.sendInvite(USERNAME, HOST);
			
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
		}
		
	}

}
