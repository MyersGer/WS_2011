package org.example.ttp2;

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

/**
 * UserAgentClient übernimmt das Einladen von anderen Nutzern
 * @author Carsten Noetzel, Armin Steudte
 *
 */
public class UAC implements IMessageProcessor {
	private static final Logger LOGGER = Logger.getLogger("UAC");

	private SIPLayer sipLayer;
	private String inviteCallId = "";
	private long cSeqInvite;
	//private Dialog dialog;
	private Dialog clientDiag;

	private ClientTransaction lastClientTrans;

	private boolean canceled = false;



	/**
	 * Constructor für den UAC
	 * @param sipLayer	Referenz auf den SIPLayer
	 */
	public UAC(SIPLayer sipLayer) {
		this.sipLayer = sipLayer;
		sipLayer.registerObserver(this);	//Registrierung beim SIP-Layer um über Nachrichten informiert zu werden
	}

	
	/**
	 * Sendet ein Bye für den vorhandenen Dialog an den entfernten User, hierzu wird zunächst ein Request erzeugt 
	 * und über eine Transaktion das Bye versendet
	 * @throws ParseException
	 * @throws InvalidArgumentException
	 * @throws SipException
	 */
	public void sendBye() throws ParseException, InvalidArgumentException, SipException {
		LOGGER.debug("sendBye()");
		
		Request bye = clientDiag.createRequest(Request.BYE);					//Request erzeugen
		ClientTransaction byeTrans = sipLayer.getNewClientTransaction(bye);		//ClientTransaktion erzeugen
		clientDiag.sendRequest(byeTrans);										//Bye senden
	}

	
	/**
	 * Sendet ein Cancel für die letzte Transaktion an den entfernten User, hierzu wird zunächst ein Request erzeugt
	 * und über eine Transaktion das Cancel versendet
	 * @throws ParseException
	 * @throws InvalidArgumentException
	 * @throws SipException
	 */
	public void sendCancel() throws ParseException, InvalidArgumentException, SipException {
		LOGGER.debug("sendCancel()");
		canceled = true;															//Flag setzen
		Request cancel = lastClientTrans.createCancel();							//Request erzeugen
		ClientTransaction cancelTran = sipLayer.getNewClientTransaction(cancel);	//ClientTransaktion erzeugen
		cancelTran.sendRequest();													//Cancel senden
	}

	/**
	 * Sendet ein Invite an den übergebenen User an den übergebenen Host
	 * @param user				User der eingeladen werden soll
	 * @param host				Host an den die Einladung gesendet wird
	 * @throws ParseException
	 * @throws InvalidArgumentException
	 * @throws SipException
	 */
	public void sendInvite(String user, String host) throws ParseException, InvalidArgumentException, SipException {
		LOGGER.debug("sendInvite(" + user + ", " + host + " )");
		canceled = false;														//Flag setzen
		lastClientTrans = sipLayer.sendInvite(user, host, Request.INVITE);		//letzte Transaktion setzen
		inviteCallId = lastClientTrans.getDialog().getCallId().getCallId();		//ID sichern, damit OK Response zugeordnet werden kann
		cSeqInvite = sipLayer.INVITE_SEQUENCE_NUMBER;							//Sequenznummer sichern
		
		LOGGER.info("inviteCallId: " + inviteCallId);
	}

	@Override
	public void processDialogTerminated(DialogTerminatedEvent dte) {
		LOGGER.debug("processDialogTerminated()");
	}

	@Override
	public void processOK(ResponseEvent responseEvent) {
		LOGGER.debug("processOK()");
		String receivedDialogId = responseEvent.getDialog().getDialogId();		//DialogID der Response ermitteln
		LOGGER.trace("receivedDialogId: " + receivedDialogId);
		String callId = SIPLayer.getCallId(responseEvent);						//CallID der Response ermitteln
		LOGGER.trace("callId: " + callId);
		try {
			//Wenn die CallID des versendeten Invites gleich der CallID der Response ist und das Invite nicht vorher durch einen Cancel-Request
			//gecancelt wurde gilt das OK dem Invite
			if (inviteCallId.equals(callId) && !canceled ) {
				Request ack = responseEvent.getDialog().createAck(cSeqInvite);		//ACK für das Invite erzeugen		
				LOGGER.info("Sending ACK: " + ack.toString());
				responseEvent.getDialog().sendAck(ack);								//ACK senden
				clientDiag = responseEvent.getDialog();								//Dialog setzen
			} else {
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
	
//	public boolean removeDialog() {	
//		LOGGER.debug("removeDialog()");
//		try {
//			Request bye = dialog.createRequest(Request.BYE);
//			dialog.sendRequest(sipLayer.getNewClientTransaction(bye));
//			return true;
//		} catch (SipException e) {
//			LOGGER.error("Could not send BYE: ", e);
//			return false;
//			
//		}
//	}
}
