package org.example.ttp2;

import java.text.ParseException;

import javax.sip.Dialog;
import javax.sip.InvalidArgumentException;
import javax.sip.RequestEvent;
import javax.sip.ResponseEvent;
import javax.sip.SipException;
import javax.sip.message.Request;
import javax.sip.message.Response;

import org.apache.log4j.Logger;


public class UAC implements IMessageProcessor {
	private static final Logger LOGGER = Logger.getLogger("UAC");
	
	private SIPLayer sipLayer;
	
	public UAC(SIPLayer sipLayer){
		this.sipLayer = sipLayer;
		sipLayer.registerObserver(this);
	}
	
	public void sendBye(String user, String host) throws ParseException, InvalidArgumentException, SipException{
		LOGGER.debug("sendBye(" + user + ", " + host + " )");
		sipLayer.send(user, host, Request.BYE);
	}
	
	public void sendCancel(String user, String host) throws ParseException, InvalidArgumentException, SipException{
		LOGGER.debug("sendCancel(" + user + ", " + host + " )");
		sipLayer.send(user, host, Request.CANCEL);
	}
	
	public void sendInvite(String user, String host) throws ParseException, InvalidArgumentException, SipException{
		LOGGER.debug("sendInvite(" + user + ", " + host + " )");
		sipLayer.send(user, host, Request.INVITE);
//		int i = 1;
//		while (i<=5 && pending) {
//			try {
//				Thread.sleep(1000);
//				i++;				
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}			
//		}
//		if (!pending){
//			
//			String ip = "239.238.237.17";
//			int port = 9017;
//			
//			IGMPListener listener = new IGMPListener();
//			
//			try {
//				
//				listener.initialize(InetAddress.getByName(ip), port);
//				new Thread(listener).run();
//				
//			} catch (UnknownHostException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			
//		} else {
//			System.out.println("Timeout beim Invite erhalten!");
//		}
		
	}



	@Override
	public void processDialogTerminated() {
		LOGGER.debug("processDialogTerminated()");
	}



	@Override
	public void processOK(ResponseEvent responseEvent) {
		LOGGER.debug("processOK()");
	}



	@Override
	public void processTrying() {
		LOGGER.debug("processTrying()");
	}



	@Override
	public void processRinging() {
		LOGGER.debug("processRinging()");
	}

	@Override
	public void processInvite(RequestEvent requestEvent) {
		LOGGER.debug("processInvite()");
		
	}

	@Override
	public void processBye() {
		LOGGER.debug("processBye()");
		
	}
	

}
