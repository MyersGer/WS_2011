package org.example.ttp2;

import java.text.ParseException;

import javax.sip.InvalidArgumentException;
import javax.sip.SipException;

public class UAS implements IMessageProcessor {
	
	private SIPLayer sipLayer;
	
	public UAS(SIPLayer sipLayer){
		this.sipLayer = sipLayer;
		sipLayer.registerObserver(this);
	}

	public void registerAtProxy(String proxyAddress, int proxyPort) throws ParseException, InvalidArgumentException, SipException{
		sipLayer.registerAtProxy(proxyAddress, proxyPort);
	}
	
	
	@Override
	public void processDialogTerminated() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void processOK() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void processTrying() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void processRinging() {
		// TODO Auto-generated method stub
		
	}
	
	
}
