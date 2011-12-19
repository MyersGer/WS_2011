package org.example.ttp2;


import java.text.ParseException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.TooManyListenersException;

import javax.sip.ClientTransaction;
import javax.sip.DialogTerminatedEvent;
import javax.sip.IOExceptionEvent;
import javax.sip.InvalidArgumentException;
import javax.sip.ListeningPoint;
import javax.sip.ObjectInUseException;
import javax.sip.PeerUnavailableException;
import javax.sip.RequestEvent;
import javax.sip.ResponseEvent;
import javax.sip.ServerTransaction;
import javax.sip.SipException;
import javax.sip.SipFactory;
import javax.sip.SipListener;
import javax.sip.SipProvider;
import javax.sip.SipStack;
import javax.sip.TimeoutEvent;
import javax.sip.TransactionAlreadyExistsException;
import javax.sip.TransactionTerminatedEvent;
import javax.sip.TransactionUnavailableException;
import javax.sip.TransportNotSupportedException;
import javax.sip.address.Address;
import javax.sip.address.AddressFactory;
import javax.sip.address.SipURI;
import javax.sip.header.CSeqHeader;
import javax.sip.header.CallIdHeader;
import javax.sip.header.ContactHeader;
import javax.sip.header.FromHeader;
import javax.sip.header.Header;
import javax.sip.header.HeaderFactory;
import javax.sip.header.MaxForwardsHeader;
import javax.sip.header.ToHeader;
import javax.sip.header.ViaHeader;
import javax.sip.message.MessageFactory;
import javax.sip.message.Request;
import javax.sip.message.Response;

import org.apache.log4j.Logger;

/**
 * SIPLayer zur Versand und Empfang von SIP Nachrichten
 * In dieser Klasse werden die Nachrichten zusammengebaut und versendet
 * ankommende Requests oder Responses werden an die Observer (implementieren
 * das IMessageProcessor Interface) weitergeleitet, damit diese die Nachrichten 
 * verarbeiten und entsprechend darauf reagieren können
 * @author Carsten Noetzel, Armin Steudte
 *
 */
public class SIPLayer implements SipListener {
	private static final Logger LOGGER = Logger.getLogger("SIPLayerLogger");

	static final String PROTOCOL = "udp";

	public final long INVITE_SEQUENCE_NUMBER = 1;

	private String username;							//Username unter dem man am Proxy registriert ist
	private String host;								//IP-Adresse unter der man erreichbar ist
	private int port;									//Port auf dem man SIP-Nachrichten empfängt
	private ArrayList<IMessageProcessor> observers;		//Liste der Observer die benachrichtigt werden sollen

	private SipStack sipStack;
	private SipFactory sipFactory;
	private AddressFactory addressFactory;
	private HeaderFactory headerFactory;
	private MessageFactory messageFactory;
	private SipProvider sipProvider;

	public SIPLayer(String username, String ip, int port) throws PeerUnavailableException, TransportNotSupportedException, InvalidArgumentException, ObjectInUseException, TooManyListenersException {

		this.host = ip;
		this.port = port;

		setUsername(username);
		this.observers = new ArrayList<IMessageProcessor>();

		sipFactory = SipFactory.getInstance();
		// Sets the pathname that identifies the location of a particular
		// vendor's implementation of this specification.
		sipFactory.setPathName("gov.nist");

		Properties properties = new Properties();
		properties.setProperty("javax.sip.STACK_NAME", "SIPLayer");
		properties.setProperty("javax.sip.IP_ADDRESS", ip);

		// SipFactory / AddressFactory / HeaderFactory / MessageFactory
		// Factory classes to create the various objects of the system. They
		// return objects that implement standard interface.
		sipStack = sipFactory.createSipStack(properties);
		headerFactory = sipFactory.createHeaderFactory();
		addressFactory = sipFactory.createAddressFactory();
		messageFactory = sipFactory.createMessageFactory();

		// SipStack - The first interface you'll need, used to create
		// ListeningPoints and SipProviders.
		// ListeningPoint - This interface encapsulates a transport/port pair
		ListeningPoint protocol = sipStack.createListeningPoint(ip, port, PROTOCOL);

		// SipProvider - register a listener for incoming SIP messages using
		// this interface
		sipProvider = sipStack.createSipProvider(protocol);
		sipProvider.addSipListener(this);

	}

	/**
	 * Registriert einen Observer der bei Nachrichtenempfang benachrichtigt werden soll 
	 * @param mp	Observer der das IMessageProcessor Interface implementiert
	 */
	public void registerObserver(IMessageProcessor mp) {
		LOGGER.debug("registerObserver()");
		observers.add(mp);
	}

	/**
	 * Getter für den Username unter dem man am Proxy registriert ist
	 * @return	Username unter dem man am Proxy registriert ist
	 */
	private String getUsername() {
		return username;
	}

	/**
	 * Setter für den Username unter dem man am Proxy registriert ist
	 * @param username	Username unter dem man am Proxy registriert ist
	 */
	private void setUsername(String username) {
		this.username = username;
	}

	/**
	 * Getter für die IP-Adresse unter der man erreichbar ist 
	 * @return	IP-Adresse unter der man erreichbar ist
	 */
	private String getHost() {
		return this.host;
	}
	
	/**
	 * Getter für den Port auf dem man SIP-Nachrichten empfängt
	 * @return	Port auf dem man SIP-Nachrichten empfängt
	 */
	private int getPort() {
		return this.port;
	}

	/**
	 * Sendet eine SIP-Invite Nachricht an den übergebenen Empfänger
	 * @param user		User der eingeladen werden soll
	 * @param host		IP-Adresse unter der der User erreichbar ist
	 * @param type		Typ der Nachrich die gesendet werden soll
	 * @return			Transaktionsobjekt welches durch das Versenden der Nachricht entsteht
	 * @throws ParseException
	 * @throws InvalidArgumentException
	 * @throws SipException
	 */
	public ClientTransaction sendInvite(String user, String host, String type) throws ParseException, InvalidArgumentException, SipException {
		LOGGER.debug("send(" + user + ", " + host + ", " + type + " )");

		// Create Main Elements

		// Creates a SipURI based on the given user and host components
		SipURI from = addressFactory.createSipURI(getUsername(), getHost() + ":" + getPort());
		// Creates an Address with the new URI attribute value
		Address fromNameAddress = addressFactory.createAddress(from);
		// Sets the display name of the Address.
		fromNameAddress.setDisplayName(getUsername());
		// Creates a new FromHeader based on the newly supplied address and tag
		// values.
		FromHeader fromHeader = headerFactory.createFromHeader(fromNameAddress, "NoetzelSteudte");

		SipURI to = addressFactory.createSipURI(user, host + ":" + getPort());
		// Creates an Address with the new URI attribute value
		Address toNameAddress = addressFactory.createAddress(to);
		// Sets the display name of the Address.
		toNameAddress.setDisplayName(user);
		// To: Header = From: Header at Registration
		ToHeader toHeader = headerFactory.createToHeader(toNameAddress, null);

		// Create RequestUri and define Transportprotocol
		SipURI requestURI = addressFactory.createSipURI(user, host);
		requestURI.setTransportParam(PROTOCOL);

		// Create Via Header
		ArrayList<ViaHeader> viaHeaders = new ArrayList<ViaHeader>();
		ViaHeader viaHeader = headerFactory.createViaHeader(getHost(), getPort(), PROTOCOL, null);
		viaHeaders.add(viaHeader);

		CallIdHeader callIdHeader = sipProvider.getNewCallId();

		long sequenceNumber = INVITE_SEQUENCE_NUMBER;
		CSeqHeader cSeqHeader = headerFactory.createCSeqHeader(sequenceNumber, type);

		MaxForwardsHeader maxForwards = headerFactory.createMaxForwardsHeader(70);

		// Create Register
		Request request = messageFactory.createRequest(requestURI, type, callIdHeader, cSeqHeader, fromHeader, toHeader, viaHeaders, maxForwards);

		// Complete Register
		SipURI contactURI = addressFactory.createSipURI(getUsername(), getHost());
		contactURI.setPort(getPort());

		Address contactAddress = addressFactory.createAddress(contactURI);
		contactAddress.setDisplayName(getUsername());

		ContactHeader contactHeader = headerFactory.createContactHeader(contactAddress);
		request.addHeader(contactHeader);

		// Send Message
		ClientTransaction trans;
		if (type == Request.INVITE) {
			trans = sipProvider.getNewClientTransaction(request);

			LOGGER.trace("GESENDET: \n" + request.toString());

			trans.sendRequest();
		} else {
			throw new RuntimeException("Request should be send statefull" + request.toString());
		}

		return trans;
	}


	/**
	 * Methode zum registrieren des Users am übergebenen Proxy
	 * @param proxyAddress		IP-Adresse des Proxy an dem man sich registrieren möchte
	 * @param proxyPort			Port des Proxys auf dem SIP-Nachrichten empfangen werden
	 * @return					Call ID des Registrierung um eine Antwort dem Request zuordnen zu können
	 * @throws ParseException
	 * @throws InvalidArgumentException
	 * @throws SipException
	 */
	public String registerAtProxy(String proxyAddress, int proxyPort) throws ParseException, InvalidArgumentException, SipException {
		LOGGER.debug("registerAtProxy(" + proxyAddress + ", " + proxyPort + " )");
		// Create Main Elements
		
		// Creates a SipURI based on the given user and host components
		SipURI from = addressFactory.createSipURI(getUsername(), getHost() + ":" + getPort());
		// Creates an Address with the new URI attribute value
		Address fromNameAddress = addressFactory.createAddress(from);
		// Sets the display name of the Address.
		fromNameAddress.setDisplayName(getUsername());
		// Creates a new FromHeader based on the newly supplied address and tag
		// values.
		FromHeader fromHeader = headerFactory.createFromHeader(fromNameAddress, "NoetzelSteudteRegister");

		// To: Header = From: Header at Registration
		ToHeader toHeader = headerFactory.createToHeader(fromNameAddress, null);

		// Create RequestUri and define Transportprotocol
		SipURI requestURI = addressFactory.createSipURI("registrar", proxyAddress);
		requestURI.setTransportParam(PROTOCOL);

		// Create Via Header
		ArrayList<ViaHeader> viaHeaders = new ArrayList<ViaHeader>();
		ViaHeader viaHeader = headerFactory.createViaHeader(getHost(), getPort(), PROTOCOL, null);
		viaHeaders.add(viaHeader);

		CallIdHeader callIdHeader = sipProvider.getNewCallId();

		long sequenceNumber = 1;
		CSeqHeader cSeqHeader = headerFactory.createCSeqHeader(sequenceNumber, Request.REGISTER);

		MaxForwardsHeader maxForwards = headerFactory.createMaxForwardsHeader(70);

		// Create Register
		Request request = messageFactory.createRequest(requestURI, Request.REGISTER, callIdHeader, cSeqHeader, fromHeader, toHeader, viaHeaders, maxForwards);

		// Complete Register
		SipURI contactURI = addressFactory.createSipURI(getUsername(), getHost());
		contactURI.setPort(getPort());

		Address contactAddress = addressFactory.createAddress(contactURI);
		contactAddress.setDisplayName(getUsername());

		ContactHeader contactHeader = headerFactory.createContactHeader(contactAddress);
		request.addHeader(contactHeader);

		// Send Message
		LOGGER.trace(request.toString());
		sipProvider.sendRequest(request);
		return callIdHeader.getCallId();
	}


	@Override
	public void processDialogTerminated(DialogTerminatedEvent dte) {
		LOGGER.debug("processDialogTerminated(" + dte.toString() + " )");
		//Alle Observer benachrichtigen
		for (IMessageProcessor mp : observers) {
			mp.processDialogTerminated(dte);
		}
	}

	@Override
	public void processIOException(IOExceptionEvent ioEx) {
		LOGGER.debug("processDialogTerminated(" + ioEx.toString() + " )");
	}

	@Override
	public void processRequest(RequestEvent requestEvent) {
		LOGGER.debug("processRequest(" + requestEvent.toString() + " )");
		try {

			Request request = requestEvent.getRequest();
			String method = request.getMethod();
			//Alle Observer benachrichtigen je nachdem von welchem Typ der Request ist
			if (method.equals(Request.INVITE)) {
				for (IMessageProcessor mp : observers) {
					mp.processInvite(requestEvent);
				}
			} else if (method.equals(Request.BYE)) {
				for (IMessageProcessor mp : observers) {
					mp.processBye(requestEvent);
				}
			} else if (method.equals(Request.ACK)) {
				for (IMessageProcessor mp : observers) {
					mp.processAck(requestEvent);
				}
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	@Override
	public void processResponse(ResponseEvent responseEvent) {
		LOGGER.debug("processResponse(" + responseEvent.toString() + " )");

		Response response = responseEvent.getResponse();
		int status = response.getStatusCode();				//Status der Response auslesen

		LOGGER.trace("ERHALTEN \n" + response.toString());

		//Alle Observer benachrichtigen je nachdem welchen Status die Response hat
		switch (status) {
		case Response.TRYING:
			for (IMessageProcessor mp : observers) {
				mp.processTrying();
			}
			break;
		case Response.OK:
			for (IMessageProcessor mp : observers) {
				mp.processOK(responseEvent);
			}
			break;
		case Response.RINGING:
			for (IMessageProcessor mp : observers) {
				mp.processRinging();
			}
			break;
		default:
			break;
		}
	}

	
	/**
	 * Gibt die CallID des übergebenen ReponseEvents zurück
	 * @param responseEvent		ResponseEvent dessen CallID ermittelt werden soll
	 * @return					CallID des ReponseEvents
	 */
	static public String getCallId(ResponseEvent responseEvent) {
		return getCallId(responseEvent.getResponse().getHeader(CallIdHeader.NAME));
	}

	/**
	 * Gibt die CallID des übergebenen Headers zurück
	 * @param header			Header dessen CallID ermittelt werden soll
	 * @return					CallID des Headers
	 */
	private static String getCallId(Header header) {
		CallIdHeader callIdHeader = (CallIdHeader) header;
		return callIdHeader.getCallId();
	}

	/**
	 * Gibt die CallID des übergebenen RequestEvents zurück
	 * @param requestEvent		RequestEvent dessen CallID ermittelt werden soll
	 * @return					CallID des RequestEvents
	 */
	public static String getCallId(RequestEvent requestEvent) {
		return getCallId(requestEvent.getRequest().getHeader(CallIdHeader.NAME));
	}

	@Override
	public void processTimeout(TimeoutEvent timeOutEvent) {
		LOGGER.debug("processTimeout(" + timeOutEvent.toString() + " )");

	}

	@Override
	public void processTransactionTerminated(TransactionTerminatedEvent transactionTerminatedEvent) {
		LOGGER.debug("processTransactionTerminated(" + transactionTerminatedEvent.toString() + " )");
	}

	
	/**
	 * Diese Methode gibt den Contact Header für Nachrichten zurück
	 * @return
	 */
	public ContactHeader getContactHeader() {
		try {
			SipURI contactURI = addressFactory.createSipURI(getUsername(), getHost());
			contactURI.setPort(getPort());
			Address contactAddress = addressFactory.createAddress(contactURI);
			contactAddress.setDisplayName(getUsername());
			ContactHeader contactHeader = headerFactory.createContactHeader(contactAddress);
			return contactHeader;
		} catch (ParseException e) {
			throw new RuntimeException("Could not get contact address", e);
		}

	}

	
	/**
	 * Diese Methode erzeugt eine neue Response für den übergebenen Request
	 * @param type		Typ der Response die erzeugt werden soll
	 * @param req		Request für den die Response erzeugt werden soll
	 * @return			Response-Objekt vom übergebenen Typ für den übergebenen Request
	 */
	public Response createResponse(int type, Request req) {
		LOGGER.debug("createResponse()");
		try {
			return messageFactory.createResponse(type, req);	//über MessageFactory Response erzeugen
		} catch (ParseException e) {
			LOGGER.error("Could not generate: ", e);
			throw new RuntimeException(e);
		}
	}

	/**
	 * Diese Methode versendet eine Response über den SipProvider 
	 * @param response		Response die versendet werden soll
	 */
	public void sendResponse(Response response) {
		LOGGER.debug("sendResponse()");
		LOGGER.trace(response.toString());
		try {
			sipProvider.sendResponse(response);
		} catch (SipException e) {
			LOGGER.error("Could not send: ", e);
		}
	}

	/**
	 * Diese Methode erzeugt eine neue  ServerTransaction und gibt diese zurück
	 * @param req		Request für die die Transaktion erzeugt werden soll
	 * @return			ServerTransaktionsobjekt für den übergebenen Request
	 * @throws TransactionAlreadyExistsException
	 * @throws TransactionUnavailableException
	 */
	public ServerTransaction getNewServerTransaction(Request req) throws TransactionAlreadyExistsException, TransactionUnavailableException {
		LOGGER.debug("getNewServerTransaction()");
		return sipProvider.getNewServerTransaction(req);	//neue Server-Transaktion erzeugen
	}

	/**
	 * Diese Methode erzeugt eine neue  ClientTransaction und gibt diese zurück
	 * @param req		Request für die die Transaktion erzeugt werden soll
	 * @return			ClientTransaktionsobjekt für den übergebenen Request
	 * @throws TransactionUnavailableException
	 */
	public ClientTransaction getNewClientTransaction(Request req) throws TransactionUnavailableException {
		LOGGER.debug("getNewClientTransaction()");
		return sipProvider.getNewClientTransaction(req);	//neue Client-Transaktion erzeugen
	}

}
