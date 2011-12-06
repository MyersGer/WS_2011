package org.example.ttp2;

import java.text.ParseException;
import java.util.HashSet;
import java.util.Set;

import javax.sip.InvalidArgumentException;
import javax.sip.RequestEvent;
import javax.sip.ResponseEvent;
import javax.sip.ServerTransaction;
import javax.sip.SipException;
import javax.sip.address.Address;
import javax.sip.address.SipURI;
import javax.sip.header.ContactHeader;
import javax.sip.message.Response;

import org.apache.log4j.Logger;

public class UAS implements IMessageProcessor {

	private static final boolean REGISTERED = true;
	private static final boolean UNREGISTERED = false;

	private SIPLayer sipLayer;
	private static final Logger LOGGER = Logger.getLogger("UAS");
	private boolean registered = UNREGISTERED;
	private String registerCallId = "";
	Set<String> dialogWaitingForAck = new HashSet<String>();
	Set<String> dialogsActive = new HashSet<String>();
	private ContactHeader contactHeader;

	public UAS(SIPLayer sipLayer) {
		this.sipLayer = sipLayer;
		sipLayer.registerObserver(this);
		contactHeader = sipLayer.getContactHeader();
	}

	public void registerAtProxy(String proxyAddress, int proxyPort) throws ParseException, InvalidArgumentException, SipException {
		LOGGER.debug("registerAtProxy(" + proxyAddress + ", " + proxyPort + " )");
		registerCallId = sipLayer.registerAtProxy(proxyAddress, proxyPort);

	}

	@Override
	public void processDialogTerminated() {
		LOGGER.debug("processDialogTerminated()");

	}

	@Override
	public void processOK(ResponseEvent responseEvent) {
		LOGGER.debug("processOK()");
		String receivedCallId = SIPLayer.getCallId(responseEvent);

		if (registered == UNREGISTERED && registerCallId.equals(receivedCallId)) {
			LOGGER.debug("Registered at proxy");
			registered = REGISTERED;
		} else {
			LOGGER.debug("Unknown OK");
		}
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
		Response response = null;
		if (registered == UNREGISTERED) {
			LOGGER.warn("UAS not registered at proxy, declining invite");
			response = sipLayer.createResponse(Response.DECLINE, requestEvent.getRequest());
			//TODO send response
		} else if (registered == REGISTERED) {
			String callId = SIPLayer.getCallId(requestEvent);
			LOGGER.info("UAS sending OK to callId: " + callId);
			
			try {
				ServerTransaction serverTransaction =  sipLayer.getNewServerTransaction(requestEvent.getRequest());
				response = sipLayer.createResponse(Response.OK, requestEvent.getRequest());
				response.addHeader(contactHeader);
				LOGGER.trace("SENDEN: " + response.toString());
				serverTransaction.sendResponse(response);
				String dialogId = serverTransaction.getDialog().getDialogId();
				dialogWaitingForAck.add(dialogId);
			} catch (Exception e) {
				LOGGER.warn("Response could not be send or build: ", e);
			}
		} else {
			LOGGER.warn("Don't know how to handle invite");
			response = sipLayer.createResponse(Response.NOT_IMPLEMENTED, requestEvent.getRequest());
			//TODO send response
		}

	}

	@Override
	public void processBye() {
		LOGGER.debug("processBye()");

	}

	@Override
	public void processAck(RequestEvent requestEvent) {
		//if check dialog in dialogWaiting for ack
		// yes: move dialog id to active dialog
		// no: decline
	String dialogId = requestEvent.getDialog().getDialogId();
	LOGGER.trace("DialogId: " + dialogId);
		if (dialogWaitingForAck.contains(dialogId)) {
			LOGGER.info("Changing DialogId: " + dialogId + " to active");
			dialogsActive.add(dialogId);
			dialogWaitingForAck.remove(dialogId);
		}else {
			LOGGER.warn("DialogId: " + dialogId + " is unknown in set of waiting dialogs");
			Response response = sipLayer.createResponse(Response.DECLINE, requestEvent.getRequest());
			//TODO send response
		}
		
	}

	public int sessionCount() {
		return dialogsActive.size();
	}

}
