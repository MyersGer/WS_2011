package sip;

import java.text.ParseException;

import javax.sip.ClientTransaction;
import javax.sip.Dialog;
import javax.sip.DialogTerminatedEvent;
import javax.sip.InvalidArgumentException;
import javax.sip.RequestEvent;
import javax.sip.ResponseEvent;
import javax.sip.SipException;
import javax.sip.message.Request;

import org.apache.log4j.Logger;

public class UAC implements IMessageProcessor {
	private static final Logger LOGGER = Logger.getLogger("UAC");

	private SIPLayer sipLayer;
	private String inviteCallId = "";
	private long cSeqInvite;
	private Dialog dialog;
	private Dialog clientDiag;

	private ClientTransaction lastClientTrans;

	private boolean canceled = false;
	private boolean established = false;



	public UAC(SIPLayer sipLayer) {
		this.sipLayer = sipLayer;
		sipLayer.registerObserver(this);
	}

	public void sendBye() throws ParseException, InvalidArgumentException, SipException {
		LOGGER.debug("sendBye()");
		
		Request bye = clientDiag.createRequest(Request.BYE);
		ClientTransaction byeTrans = sipLayer.getNewClientTransaction(bye);
		clientDiag.sendRequest(byeTrans);
	}

	public void sendCancel() throws ParseException, InvalidArgumentException, SipException {
		LOGGER.debug("sendCancel()");
		canceled = true;
		Request cancel = lastClientTrans.createCancel();
		ClientTransaction cancelTran = sipLayer.getNewClientTransaction(cancel);
		cancelTran.sendRequest();		
		established = false;
	}

	public void sendInvite(String user, String host) throws ParseException, InvalidArgumentException, SipException {
		LOGGER.debug("sendInvite(" + user + ", " + host + " )");
		canceled = false;
		lastClientTrans = sipLayer.sendInvite(user, host, Request.INVITE);
		inviteCallId = lastClientTrans.getDialog().getCallId().getCallId();
		cSeqInvite = sipLayer.INVITE_SEQUENCE_NUMBER;
		
		LOGGER.info("inviteCallId: " + inviteCallId);
	}

	@Override
	public void processDialogTerminated(DialogTerminatedEvent dte) {
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
			if (inviteCallId.equals(callId) && !canceled && !established) {
				established = true;
//				dialog = responseEvent.getDialog();
				Request ack = responseEvent.getDialog().createAck(cSeqInvite);			
				LOGGER.info("Sending ACK: " + ack.toString());
				responseEvent.getDialog().sendAck(ack);
				clientDiag = responseEvent.getDialog();
			} else {
				established = false;
				LOGGER.info("Received OK, not for me or canceled ");
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
	public void processBye(RequestEvent requestEvent) {
		LOGGER.debug("processBye()");

	}

	@Override
	public void processAck(RequestEvent requestEvent) {
		LOGGER.debug("processAck()");
		
	}
	
	public boolean removeDialog() {	
		LOGGER.debug("removeDialog()");
		try {
			Request bye = dialog.createRequest(Request.BYE);
			dialog.sendRequest(sipLayer.getNewClientTransaction(bye));
			return true;
		} catch (SipException e) {
			LOGGER.error("Could not send BYE: ", e);
			return false;
			
		}
	}
	
	

}
