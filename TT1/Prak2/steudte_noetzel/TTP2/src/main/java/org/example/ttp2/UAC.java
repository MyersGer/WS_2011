package org.example.ttp2;

import java.text.ParseException;

import javax.sip.InvalidArgumentException;
import javax.sip.SipException;
import javax.sip.message.Request;


public class UAC implements IMessageProcessor {
	
	private SIPLayer sipLayer;
	
	public UAC(SIPLayer sipLayer){
		this.sipLayer = sipLayer;
		sipLayer.registerObserver(this);
	}
	
	public void sendBye(String user, String host) throws ParseException, InvalidArgumentException, SipException{
		sipLayer.send(user, host, Request.BYE);
	}
	
	public void sendCancel(String user, String host) throws ParseException, InvalidArgumentException, SipException{
		sipLayer.send(user, host, Request.CANCEL);
	}
	
	public void sendInvite(String user, String host) throws ParseException, InvalidArgumentException, SipException{
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
		// TODO Auto-generated method stub
		System.out.println("Terminated");
	}



	@Override
	public void processOK() {
		// TODO Auto-generated method stub
		System.out.println("OK");
	}



	@Override
	public void processTrying() {
		// TODO Auto-generated method stub
		System.out.println("Trying");
	}



	@Override
	public void processRinging() {
		// TODO Auto-generated method stub
		System.out.println("Ringing");
	}
	

}
