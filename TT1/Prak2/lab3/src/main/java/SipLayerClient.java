import java.text.ParseException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.TooManyListenersException;

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
import javax.sip.TransactionTerminatedEvent;
import javax.sip.TransportNotSupportedException;
import javax.sip.address.Address;
import javax.sip.address.AddressFactory;
import javax.sip.address.SipURI;
import javax.sip.header.CSeqHeader;
import javax.sip.header.CallIdHeader;
import javax.sip.header.ContactHeader;
import javax.sip.header.ContentTypeHeader;
import javax.sip.header.FromHeader;
import javax.sip.header.HeaderFactory;
import javax.sip.header.MaxForwardsHeader;
import javax.sip.header.ToHeader;
import javax.sip.header.ViaHeader;
import javax.sip.message.MessageFactory;
import javax.sip.message.Request;
import javax.sip.message.Response;

public class SipLayerClient implements SipListener {

	private IMessageProcessor messageProcessor;

	private String username;

	private SipStack sipStack;

	private SipFactory sipFactory;

	private AddressFactory addressFactory;

	private HeaderFactory headerFactory;

	private MessageFactory messageFactory;

	private SipProvider sipProvider;

	/** Here we initialize the SIP stack. */
	public SipLayerClient(String username, String ip, int port) throws PeerUnavailableException, TransportNotSupportedException, InvalidArgumentException, ObjectInUseException, TooManyListenersException {
		setUsername(username);
		sipFactory = SipFactory.getInstance();
		sipFactory.setPathName("gov.nist");
		Properties properties = new Properties();
		properties.setProperty("javax.sip.STACK_NAME", "TextClient");
		properties.setProperty("javax.sip.IP_ADDRESS", ip);

		// DEBUGGING: Information will go to files
		// textclient.log and textclientdebug.log
		properties.setProperty("gov.nist.javax.sip.TRACE_LEVEL", "32");
		properties.setProperty("gov.nist.javax.sip.SERVER_LOG", "textclient.txt");
		properties.setProperty("gov.nist.javax.sip.DEBUG_LOG", "textclientdebug.log");

		sipStack = sipFactory.createSipStack(properties);
		headerFactory = sipFactory.createHeaderFactory();
		addressFactory = sipFactory.createAddressFactory();
		messageFactory = sipFactory.createMessageFactory();

		ListeningPoint tcp = sipStack.createListeningPoint(port, "tcp");
		ListeningPoint udp = sipStack.createListeningPoint(port, "udp");

		sipProvider = sipStack.createSipProvider(tcp);
		sipProvider.addSipListener(this);
		sipProvider = sipStack.createSipProvider(udp);
		sipProvider.addSipListener(this);
	}
	
	//just invite someone
	public void invite(String user, String host) throws ParseException, InvalidArgumentException, SipException{

		//Creates a SipURI based on the given user and host components
		SipURI from = addressFactory.createSipURI(getUsername(),getHost() + ":" + getPort());
		//Creates an Address with the new URI attribute value
		Address fromNameAddress = addressFactory.createAddress(from);
		//Sets the display name of the Address.
		fromNameAddress.setDisplayName(getUsername());
		//Creates a new FromHeader based on the newly supplied address and tag values. 
		FromHeader fromHeader = headerFactory.createFromHeader(fromNameAddress, "UAC");
		
		//To: Header = From: Header at Registration
		ToHeader toHeader = headerFactory.createToHeader(fromNameAddress, null);
		
		//Create RequestUri and define Transportprotocol
		SipURI requestURI = addressFactory.createSipURI(user, host); 
		requestURI.setTransportParam("udp");

		//Create Via Header
		ArrayList<ViaHeader> viaHeaders = new ArrayList<ViaHeader>();
		ViaHeader viaHeader = headerFactory.createViaHeader(getHost(), getPort(), "udp", null);
		viaHeaders.add(viaHeader);

		CallIdHeader callIdHeader = sipProvider.getNewCallId();

		long sequenceNumber = 1; 
		CSeqHeader cSeqHeader = headerFactory.createCSeqHeader(sequenceNumber, Request.INVITE);
		
		MaxForwardsHeader maxForwards = headerFactory.createMaxForwardsHeader(70);
		
		//Create Register
		Request request =  messageFactory.createRequest(requestURI, Request.INVITE, callIdHeader, cSeqHeader, fromHeader, toHeader, viaHeaders, maxForwards);
		
		//Complete Register
		SipURI contactURI = addressFactory.createSipURI(getUsername(), getHost());
		contactURI.setPort(getPort());
		 
		Address contactAddress = addressFactory.createAddress(contactURI);
		contactAddress.setDisplayName(getUsername());
		 
		ContactHeader contactHeader = headerFactory.createContactHeader(contactAddress);
		request.addHeader(contactHeader);
		
		//Send Message
		System.out.println(request.toString());
		sipProvider.sendRequest(request);
	}

	/** This method is called by the SIP stack when a response arrives. */
	public void processResponse(ResponseEvent evt) {
		Response response = evt.getResponse();
		int status = response.getStatusCode();

		if ((status >= 200) && (status < 300)) { // Success!
			messageProcessor.processInfo("--Sent");
			return;
		}

		messageProcessor.processError("Previous message not sent: " + status);
	}

	/**
	 * This method is called by the SIP stack when a new request arrives.
	 */
	public void processRequest(RequestEvent evt) {
		
		Request req = evt.getRequest();
		// TODO Change Message handling to messageProcessor
		String method = req.getMethod();
		if (!method.equals("MESSAGE")) { // bad request type.
			messageProcessor.processError("Bad request type: " + method);
			return;
		}

		FromHeader from = (FromHeader) req.getHeader("From");
		messageProcessor.processMessage(from.getAddress().toString(), new String(req.getRawContent()));
		Response response = null;
		try { // Reply with OK
			response = messageFactory.createResponse(200, req);
			ToHeader toHeader = (ToHeader) response.getHeader(ToHeader.NAME);
			toHeader.setTag("888"); // This is mandatory as per the spec.
			ServerTransaction st = sipProvider.getNewServerTransaction(req);
			st.sendResponse(response);
		} catch (Throwable e) {
			e.printStackTrace();
			messageProcessor.processError("Can't send OK reply.");
		}
	}

	/**
	 * This method is called by the SIP stack when there's no answer to a message. Note that this is treated differently from an error message.
	 */
	public void processTimeout(TimeoutEvent evt) {
		messageProcessor.processError("Previous message not sent: " + "timeout");
	}

	/**
	 * This method is called by the SIP stack when there's an asynchronous message transmission error.
	 */
	public void processIOException(IOExceptionEvent evt) {
		messageProcessor.processError("Previous message not sent: " + "I/O Exception");
	}

	/**
	 * This method is called by the SIP stack when a dialog (session) ends.
	 */
	public void processDialogTerminated(DialogTerminatedEvent evt) {
		messageProcessor.processDialogTerminated(evt);
	}

	/**
	 * This method is called by the SIP stack when a transaction ends.
	 */
	public void processTransactionTerminated(TransactionTerminatedEvent evt) {
	}

	public String getHost() {
		int port = sipProvider.getListeningPoint().getPort();
		String host = sipStack.getIPAddress();
		return host;
	}

	public int getPort() {
		int port = sipProvider.getListeningPoint().getPort();
		return port;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String newUsername) {
		username = newUsername;
	}

	public IMessageProcessor getMessageProcessor() {
		return messageProcessor;
	}

	public void setMessageProcessor(IMessageProcessor newMessageProcessor) {
		messageProcessor = newMessageProcessor;
	}

}