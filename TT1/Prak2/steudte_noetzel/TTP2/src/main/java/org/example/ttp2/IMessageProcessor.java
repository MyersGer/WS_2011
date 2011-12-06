package org.example.ttp2;

import java.text.ParseException;

import javax.sip.RequestEvent;
import javax.sip.ResponseEvent;

public interface IMessageProcessor {
	
	public void processDialogTerminated();
	public void processOK(ResponseEvent responseEvent);
	public void processTrying();
	public void processRinging();
	public void processInvite(RequestEvent requestEvent);
	public void processBye();
	

}
