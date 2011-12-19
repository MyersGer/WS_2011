package org.example.ttp2;

import javax.sip.DialogTerminatedEvent;
import javax.sip.RequestEvent;
import javax.sip.ResponseEvent;

/**
 * MessageProcessorInterface zur Benachrichtigung der Observer des SIP-Layers
 * Klassen die diese Interface implementieren, können sich als Observer am 
 * SIP-Layer registrieren und werden über ankommende Requests und Responses
 * informiert um diese zu verarbeiten
 * @author Carsten Noetzel, Armin Steudte
 *
 */
public interface IMessageProcessor {
	
	public void processDialogTerminated(DialogTerminatedEvent dte);
	public void processOK(ResponseEvent responseEvent);
	public void processTrying();
	public void processRinging();
	public void processInvite(RequestEvent requestEvent);
	public void processBye(RequestEvent requestEvent);
	public void processAck(RequestEvent requestEvent);
	

}
