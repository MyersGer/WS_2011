package org.example.ttp2;

import java.text.ParseException;

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
	private String inviteCallId = "";
	private long cSeqInvite;

	public UAC(SIPLayer sipLayer) {
		this.sipLayer = sipLayer;
		sipLayer.registerObserver(this);
	}

	public void sendBye(String user, String host) throws ParseException, InvalidArgumentException, SipException {
		LOGGER.debug("sendBye(" + user + ", " + host + " )");
		sipLayer.send(user, host, Request.BYE);
	}

	public void sendCancel(String user, String host) throws ParseException, InvalidArgumentException, SipException {
		LOGGER.debug("sendCancel(" + user + ", " + host + " )");
		sipLayer.send(user, host, Request.CANCEL);
	}

	public void sendInvite(String user, String host) throws ParseException, InvalidArgumentException, SipException {
		LOGGER.debug("sendInvite(" + user + ", " + host + " )");
		inviteCallId = sipLayer.send(user, host, Request.INVITE);
		cSeqInvite = sipLayer.INVITE_SEQUENCE_NUMBER;
		LOGGER.info("inviteCallId: " + inviteCallId);
	}

	@Override
	public void processDialogTerminated() {
		LOGGER.debug("processDialogTerminated()");
	}

	@Override
	public void processOK(ResponseEvent responseEvent) {
		LOGGER.debug("processOK()");
		String receivedDialogId = responseEvent.getDialog().getDialogId();
		LOGGER.trace("receivedDialogId: " + receivedDialogId);
		String callId = SIPLayer.getCallId(responseEvent);
		LOGGER.trace("callId: " + callId);
		try {
			if (inviteCallId.equals(callId)) {
				Request ack = responseEvent.getDialog().createAck(cSeqInvite);
				responseEvent.getDialog().sendAck(ack);
			} else {
				LOGGER.info("Received OK, not for me");
			}
		} catch (Exception e) {
			LOGGER.warn("Could not build/send ack: ", e);
			throw new RuntimeException("Could not build/send ack: ", e);
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

	}

	@Override
	public void processBye() {
		LOGGER.debug("processBye()");

	}

}
