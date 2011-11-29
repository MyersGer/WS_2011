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

public class SipLayer implements SipListener {

	private MessageProcessor messageProcessor;

	private String username;

	private SipStack sipStack;

	private SipFactory sipFactory;

	private AddressFactory addressFactory;

	private HeaderFactory headerFactory;

	private MessageFactory messageFactory;

	private SipProvider sipProvider;

	private int port;

	private String host;

	public SipLayer(String username, String ip, int port) throws PeerUnavailableException, TransportNotSupportedException, InvalidArgumentException, ObjectInUseException, TooManyListenersException {

		this.username = username;

		sipFactory = SipFactory.getInstance();

		sipFactory.setPathName("gov.nist");

		Properties properties = new Properties();

		properties.setProperty("javax.sip.STACK_NAME",

		"TextClient");

		properties.setProperty("javax.sip.IP_ADDRESS",

		ip);

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

	public void processDialogTerminated(DialogTerminatedEvent arg0) {
		// TODO Auto-generated method stub

	}

	public void processIOException(IOExceptionEvent arg0) {
		// TODO Auto-generated method stub

	}

	public void processRequest(RequestEvent arg0) {
		// TODO Auto-generated method stub

	}

	public void processResponse(ResponseEvent evt) {
		Response response = evt.getResponse();
		int status = response.getStatusCode();

		if ((status >= 200) && (status < 300)) { // Success!
			messageProcessor.processInfo("--Sent");
			return;
		}

		messageProcessor.processError("Previous message not sent: " + status);
	}

	public void processTimeout(TimeoutEvent arg0) {
		// TODO Auto-generated method stub

	}

	public void processTransactionTerminated(TransactionTerminatedEvent arg0) {
		// TODO Auto-generated method stub

	}

	public void sendMessage(String to, String message) throws ParseException, InvalidArgumentException, SipException {

		SipURI from = addressFactory.createSipURI(getUsername(), getHost() + ":" + getPort());
		Address fromNameAddress = addressFactory.createAddress(from);
		fromNameAddress.setDisplayName(getUsername());
		FromHeader fromHeader = headerFactory.createFromHeader(fromNameAddress, "textclientv1.0");

		String username = to.substring(to.indexOf(":") + 1, to.indexOf("@"));
		String address = to.substring(to.indexOf("@") + 1);

		SipURI toAddress = addressFactory.createSipURI(username, address);
		Address toNameAddress = addressFactory.createAddress(toAddress);
		toNameAddress.setDisplayName(username);
		ToHeader toHeader = headerFactory.createToHeader(toNameAddress, null);

		SipURI requestURI = addressFactory.createSipURI(username, address);
		requestURI.setTransportParam("udp");

		ArrayList viaHeaders = new ArrayList();
		ViaHeader viaHeader = headerFactory.createViaHeader(getHost(), getPort(), "udp", null);
		viaHeaders.add(viaHeader);

		CallIdHeader callIdHeader = sipProvider.getNewCallId();

		CSeqHeader cSeqHeader = headerFactory.createCSeqHeader(1, Request.MESSAGE);

		MaxForwardsHeader maxForwards = headerFactory.createMaxForwardsHeader(70);

		Request request = messageFactory.createRequest(requestURI, Request.MESSAGE, callIdHeader, cSeqHeader, fromHeader, toHeader, viaHeaders, maxForwards);

		SipURI contactURI = addressFactory.createSipURI(getUsername(), getHost());
		contactURI.setPort(getPort());
		Address contactAddress = addressFactory.createAddress(contactURI);
		contactAddress.setDisplayName(getUsername());
		ContactHeader contactHeader = headerFactory.createContactHeader(contactAddress);
		request.addHeader(contactHeader);

		ContentTypeHeader contentTypeHeader = headerFactory.createContentTypeHeader("text", "plain");
		request.setContent(message, contentTypeHeader);

		sipProvider.sendRequest(request);

	}

	public MessageProcessor getMessageProcessor() {
		return messageProcessor;
	}

	public void setMessageProcessor(MessageProcessor messageProcessor) {
		this.messageProcessor = messageProcessor;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public SipStack getSipStack() {
		return sipStack;
	}

	public void setSipStack(SipStack sipStack) {
		this.sipStack = sipStack;
	}

	public SipFactory getSipFactory() {
		return sipFactory;
	}

	public void setSipFactory(SipFactory sipFactory) {
		this.sipFactory = sipFactory;
	}

	public AddressFactory getAddressFactory() {
		return addressFactory;
	}

	public void setAddressFactory(AddressFactory addressFactory) {
		this.addressFactory = addressFactory;
	}

	public HeaderFactory getHeaderFactory() {
		return headerFactory;
	}

	public void setHeaderFactory(HeaderFactory headerFactory) {
		this.headerFactory = headerFactory;
	}

	public MessageFactory getMessageFactory() {
		return messageFactory;
	}

	public void setMessageFactory(MessageFactory messageFactory) {
		this.messageFactory = messageFactory;
	}

	public SipProvider getSipProvider() {
		return sipProvider;
	}

	public void setSipProvider(SipProvider sipProvider) {
		this.sipProvider = sipProvider;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

}
