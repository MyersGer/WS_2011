package org.example.ttp2;

import java.text.ParseException;

import javax.sip.DialogTerminatedEvent;
import javax.sip.RequestEvent;
import javax.sip.ResponseEvent;
import javax.sip.message.Request;

public interface IMessageProcessor {
	
	public void processDialogTerminated(DialogTerminatedEvent dte);
	public void processOK(ResponseEvent responseEvent);
	public void processTrying();
	public void processRinging();
	public void processInvite(RequestEvent requestEvent);
	public void processBye(RequestEvent requestEvent);
	public void processAck(RequestEvent requestEvent);
	

}
