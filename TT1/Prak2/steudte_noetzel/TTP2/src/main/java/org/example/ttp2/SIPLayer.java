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

public class SIPLayer implements SipListener {
	private static final Logger LOGGER = Logger.getLogger("SIPLayerLogger");

	static final String INV = "INVITE";
	static final String BYE = "BYE";
	static final String PROTOCOL = "udp";

	public final long INVITE_SEQUENCE_NUMBER = 1;

	private String username;
	private ArrayList<IMessageProcessor> observers;

	private SipStack sipStack;
	private SipFactory sipFactory;
	private AddressFactory addressFactory;
	private HeaderFactory headerFactory;
	private MessageFactory messageFactory;
	private SipProvider sipProvider;

	private String host;
	private int port;

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

	public void registerObserver(IMessageProcessor mp) {
		LOGGER.debug("registerObserver()");
		observers.add(mp);
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	private String getHost() {
		return this.host;
	}

	private int getPort() {
		return this.port;
	}

	public String send(String user, String host, String type) throws ParseException, InvalidArgumentException, SipException {
		LOGGER.debug("send(" + user + ", " + host + ", " + type + " )");
		
		String dialogId;
		
		// Create Main Elements

		// Creates a SipURI based on the given user and host components
		SipURI from = addressFactory.createSipURI(getUsername(), getHost() + ":" + getPort());
		// Creates an Address with the new URI attribute value
		Address fromNameAddress = addressFactory.createAddress(from);
		// Sets the display name of the Address.
		fromNameAddress.setDisplayName(getUsername());
		// Creates a new FromHeader based on the newly supplied address and tag
		// values.
		FromHeader fromHeader = headerFactory.createFromHeader(fromNameAddress, null);

		// To: Header = From: Header at Registration
		ToHeader toHeader = headerFactory.createToHeader(fromNameAddress, null);

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

		if (type == Request.INVITE) {
			ClientTransaction trans = sipProvider.getNewClientTransaction(request);
			dialogId = trans.getDialog().getDialogId();
			LOGGER.trace("GESENDET: \n" + request.toString());
	

			trans.sendRequest();
		} else {
			throw new RuntimeException("Request should be send statefull" + request.toString());
		}
		
		return dialogId;
	}

	/**
	 * @param proxyAddress
	 * @param proxyPort
	 * @return CallId as String
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
		FromHeader fromHeader = headerFactory.createFromHeader(fromNameAddress, null);

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

			if (INV.equals(method)) {
				for (IMessageProcessor mp : observers) {
					mp.processInvite(requestEvent);
				}
			} else if (BYE.equals(method)) {
				for (IMessageProcessor mp : observers) {
					mp.processBye();
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
		int status = response.getStatusCode();

		LOGGER.trace("ERHALTEN \n" + response.toString());

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

	static public String getCallId(ResponseEvent responseEvent) {
		return getCallId(responseEvent.getResponse().getHeader(CallIdHeader.NAME));
	}

	private static String getCallId(Header header) {
		CallIdHeader callIdHeader = (CallIdHeader) header;
		return callIdHeader.getCallId();
	}

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

	public Response createResponse(int arg0, Request arg1) {
		LOGGER.debug("createResponse()");
		try {
			return messageFactory.createResponse(arg0, arg1);
		} catch (ParseException e) {
			LOGGER.error("Could not generate: ", e);
			throw new RuntimeException(e);
		}
	}

	public void sendResponse(Response response) {
		LOGGER.debug("sendResponse()");
		LOGGER.trace(response.toString());
		try {
			sipProvider.sendResponse(response);
		} catch (SipException e) {
			LOGGER.error("Could not send: ", e);
		}
	}

	public ServerTransaction getNewServerTransaction(Request arg0) throws TransactionAlreadyExistsException, TransactionUnavailableException {
		LOGGER.debug("getNewServerTransaction()");
		return sipProvider.getNewServerTransaction(arg0);
	}

}
