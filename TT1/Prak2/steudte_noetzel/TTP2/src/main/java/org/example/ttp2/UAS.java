package org.example.ttp2;

import java.text.ParseException;
import java.util.HashSet;
import java.util.Set;

import javax.sip.DialogTerminatedEvent;
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

/**
 * @author Carsten Noetzel, Armin Steudte
 *
 */
public class UAS implements IMessageProcessor {

	private static final boolean REGISTERED = true;
	private static final boolean UNREGISTERED = false;

	private IUpdateGUI gui;
	private SIPLayer sipLayer;
	private static final Logger LOGGER = Logger.getLogger("UAS");
	private boolean registered = UNREGISTERED;
	private String registerCallId = "";
	Set<String> dialogWaitingForAck = new HashSet<String>();	//Invites die mit OK bestätigt wurden und bei denen das ACK noch aussteht
	Set<String> dialogsActive = new HashSet<String>();			//aktive Dialoge
	private ContactHeader contactHeader;

	/**
	 * Constructor für den UAS
	 * @param sipLayer	Referenz auf den SIPLayer
	 * @param gui		Referenz auf die GUI um diese zu aktualisieren
	 */
	public UAS(SIPLayer sipLayer, IUpdateGUI gui) {
		this.gui = gui;
		this.sipLayer = sipLayer;
		sipLayer.registerObserver(this);				//Registrierung beim SIP-Layer um über Nachrichten informiert zu werden
		contactHeader = sipLayer.getContactHeader();
	}

	/**
	 * Methode zur Registiertung des UAS am Proxy mit der übergebenen IP und dem Port
	 * @param proxyAddress			IP-Adresse des Proxys
	 * @param proxyPort				Port des Proxys über den SIP-Nachrichten empfangen werden
	 * @throws ParseException
	 * @throws InvalidArgumentException
	 * @throws SipException
	 */
	public void registerAtProxy(String proxyAddress, int proxyPort) throws ParseException, InvalidArgumentException, SipException {
		LOGGER.debug("registerAtProxy(" + proxyAddress + ", " + proxyPort + " )");
		registerCallId = sipLayer.registerAtProxy(proxyAddress, proxyPort);
	}

	@Override
	public void processDialogTerminated(DialogTerminatedEvent dte) {
		LOGGER.debug("processDialogTerminated()");
		String dialogId = dte.getDialog().getDialogId();					//DialogID des Dialogs ermitteln der terminiert
		LOGGER.info("remove dialogId: " + dialogId + " from dialogsActive");
		dialogsActive.remove(dialogId);										//Dialog aus Liste entfernen
		gui.updateSessions(dialogsActive);									//GUI aktualisieren
	}

	@Override
	public void processOK(ResponseEvent responseEvent) {
		LOGGER.debug("processOK()");
		String receivedCallId = SIPLayer.getCallId(responseEvent);			//CallID der Response ermitteln

		//wenn der Server noch nicht am Proxy registriert ist und die CallID der Register-Nachricht mit
		//der CallID der Response übereinstimmt, gehört das OK zur Register-Nachricht
		if (registered == UNREGISTERED && registerCallId.equals(receivedCallId)) {
			LOGGER.debug("Registered at proxy");
			registered = REGISTERED;				//Flag setzen, Server ist nun registriert
			gui.setRegistered(registered);			//GUI aktualisieren
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
		//wenn der Server noch nicht registriert ist, kann der Server noch keinen Invite erhalten,
		//der Invite wird deshalb abgelehnt
		if (registered == UNREGISTERED) {
			LOGGER.warn("UAS not registered at proxy, declining invite");
			response = sipLayer.createResponse(Response.DECLINE, requestEvent.getRequest());	//Decline-Response erzeugen
			// TODO send response
		} else if (registered == REGISTERED) {
			String callId = SIPLayer.getCallId(requestEvent);
			LOGGER.info("UAS sending OK to callId: " + callId);

			try {
				ServerTransaction serverTransaction = sipLayer.getNewServerTransaction(requestEvent.getRequest());	//neue Servertransaktion erzeugen
				response = sipLayer.createResponse(Response.OK, requestEvent.getRequest());							//neue Response mit OK erzeugen
				response.addHeader(contactHeader);																	//contact Header anfügen
				LOGGER.trace("SENDEN: " + response.toString());
				serverTransaction.sendResponse(response);															//Response senden
				String dialogId = serverTransaction.getDialog().getDialogId();										//dialogID sichern
				dialogWaitingForAck.add(dialogId);																	//dislogID in die Liste der Dialoge schieben die auf ACK warten
			} catch (Exception e) {	
				LOGGER.warn("Response could not be send or build: ", e);
			}
		} else {
			LOGGER.warn("Don't know how to handle invite");
			response = sipLayer.createResponse(Response.NOT_IMPLEMENTED, requestEvent.getRequest());
			// TODO send response
		}

	}

	@Override
	public void processBye(RequestEvent requestEvent) {
		LOGGER.debug("processBye()");
		String dialogId = requestEvent.getDialog().getDialogId();										//DialogID des Request ermitteln
		
		//wenn der Dialog ein gültiger aktiver Dialog ist OK senden und Dialog aus Liste entfernen
		if (dialogsActive.contains(dialogId)) {
			ServerTransaction serverTransaction = requestEvent.getServerTransaction();					//neue Servertransaktion erzeugen
			Response response = sipLayer.createResponse(Response.OK, requestEvent.getRequest());		//Response erzeugen
			response.addHeader(contactHeader);															//contact Header anfügen
			LOGGER.trace("SENDEN: " + response.toString());
			try {
				serverTransaction.sendResponse(response);												//Response senden
			} catch (Exception e) {
				LOGGER.warn("Response could not be send or build: ", e);
			}

		}
	}

	@Override
	public void processAck(RequestEvent requestEvent) {						//DialogID des Request ermitteln
		String dialogId = requestEvent.getDialog().getDialogId();
		LOGGER.trace("DialogId: " + dialogId);
		
		//wenn sich die ID des Dialogs in der Liste der Dialoge befindet bei denen ein
		//ACK noch aussteht Dialog verschieben
		if (dialogWaitingForAck.contains(dialogId)) {
			LOGGER.info("Changing DialogId: " + dialogId + " to active");
			dialogsActive.add(dialogId);									//der Liste bestätigter Dialoge hinzufügen
			dialogWaitingForAck.remove(dialogId);							//aus der Liste unbestätigter Dialoge entfernen
			gui.updateSessions(dialogsActive);								//GUI aktualisieren					
		} else {
			LOGGER.warn("DialogId: " + dialogId + " is unknown in set of waiting dialogs");
			Response response = sipLayer.createResponse(Response.DECLINE, requestEvent.getRequest());
			// TODO send response
		}

	}

	/**
	 * Gibt die Anzahl aktiver bestätigter Session zurück
	 * @return	Anzahl aktiver bestätigter Sessions
	 */
	public int sessionCount() {
		return dialogsActive.size();
	}

}
