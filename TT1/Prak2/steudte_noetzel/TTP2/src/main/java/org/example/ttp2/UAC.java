package org.example.ttp2;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.TooManyListenersException;

import javax.sip.ClientTransaction;
import javax.sip.Dialog;
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


public class UAC implements SipListener {
	
	private String username;
	
	private SipStack sipStack;
	private SipFactory sipFactory;
	private AddressFactory addressFactory;
	private HeaderFactory headerFactory;
	private MessageFactory messageFactory;
	private SipProvider sipProvider;
	
	private String host;
	private int port;
	private Dialog dialog;
	private boolean pending;
	
	
	public UAC(String username, String ip, int port) throws PeerUnavailableException, TransportNotSupportedException,
															InvalidArgumentException, ObjectInUseException, 
															TooManyListenersException {
		this.host = ip;
		this.port = port;
		
		setUsername(username);
		sipFactory = SipFactory.getInstance();
		//Sets the pathname that identifies the location of a particular vendor's implementation of this specification.
		sipFactory.setPathName("gov.nist");
		
		Properties properties = new Properties();
		properties.setProperty("javax.sip.STACK_NAME", "Client");
		properties.setProperty("javax.sip.IP_ADDRESS", ip);
		
		//SipFactory / AddressFactory / HeaderFactory / MessageFactory
		//Factory classes to create the various objects of the system. They return objects that implement standard interface.
		sipStack = sipFactory.createSipStack(properties);
		headerFactory = sipFactory.createHeaderFactory();
		addressFactory = sipFactory.createAddressFactory();
		messageFactory = sipFactory.createMessageFactory();
		
		//SipStack - The first interface you'll need, used to create ListeningPoints and SipProviders. 
		//ListeningPoint - This interface encapsulates a transport/port pair
		ListeningPoint tcp = sipStack.createListeningPoint(ip, port, "tcp");
		ListeningPoint udp = sipStack.createListeningPoint(ip, port, "udp");
		
		//SipProvider - register a listener for incoming SIP messages using this interface
		sipProvider = sipStack.createSipProvider(tcp);
		sipProvider.addSipListener(this);
		sipProvider = sipStack.createSipProvider(udp);
		sipProvider.addSipListener(this);
	}
	
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
	
	private String getHost(){
		return this.host;
	}
	
	private int getPort(){
		return this.port;
	}
	
	public void registerAtProxy(String proxyAddress, int proxyPort) throws ParseException, InvalidArgumentException, SipException{
		//Create Main Elements
		
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
		SipURI requestURI = addressFactory.createSipURI("registrar", proxyAddress); 
		requestURI.setTransportParam("udp");

		//Create Via Header
		ArrayList<ViaHeader> viaHeaders = new ArrayList<ViaHeader>();
		ViaHeader viaHeader = headerFactory.createViaHeader(getHost(), getPort(), "udp", null);
		viaHeaders.add(viaHeader);

		CallIdHeader callIdHeader = sipProvider.getNewCallId();

		long sequenceNumber = 1; 
		CSeqHeader cSeqHeader = headerFactory.createCSeqHeader(sequenceNumber, Request.REGISTER);
		
		MaxForwardsHeader maxForwards = headerFactory.createMaxForwardsHeader(70);
		
		//Create Register
		Request request =  messageFactory.createRequest(requestURI, Request.REGISTER, callIdHeader, cSeqHeader, fromHeader, toHeader, viaHeaders, maxForwards);
		
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
	
	public void sendInvite(String user, String host) throws ParseException, InvalidArgumentException, SipException{
		send(user, host, Request.INVITE);
		pending = true;
	}
	
	public void sendBye(String user, String host) throws ParseException, InvalidArgumentException, SipException{
		send(user, host, Request.BYE);
	}
	
	public void sendCancel(String user, String host) throws ParseException, InvalidArgumentException, SipException{
		send(user, host, Request.CANCEL);
	}
	
	public void sendMessageOverDialog(String msg) throws ParseException, SipException{
		
		SipURI contactURI = addressFactory.createSipURI(getUsername(), getHost());
		contactURI.setPort(getPort());
		 
		Address contactAddress = addressFactory.createAddress(contactURI);
		contactAddress.setDisplayName(getUsername());
		 
		ContactHeader contactHeader = headerFactory.createContactHeader(contactAddress);
		
		ContentTypeHeader contentTypeHeader = headerFactory.createContentTypeHeader("text", "plain");
		
		Request request = dialog.createRequest(Request.MESSAGE);

		request.setHeader(contactHeader);
		request.setContent(msg, contentTypeHeader);

		ClientTransaction trans = sipProvider.getNewClientTransaction(request);
		
		//Send Message
		System.out.println(request.toString());
		trans.sendRequest();
	}
	
	private void send(String user, String host, String type) throws ParseException, InvalidArgumentException, SipException {
		//Create Main Elements
		
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
		CSeqHeader cSeqHeader = headerFactory.createCSeqHeader(sequenceNumber, type);
		
		MaxForwardsHeader maxForwards = headerFactory.createMaxForwardsHeader(70);
		
		//Create Register
		Request request =  messageFactory.createRequest(requestURI, type, callIdHeader, cSeqHeader, fromHeader, toHeader, viaHeaders, maxForwards);
		
		//Complete Register
		SipURI contactURI = addressFactory.createSipURI(getUsername(), getHost());
		contactURI.setPort(getPort());
		 
		Address contactAddress = addressFactory.createAddress(contactURI);
		contactAddress.setDisplayName(getUsername());
		 
		ContactHeader contactHeader = headerFactory.createContactHeader(contactAddress);
		request.addHeader(contactHeader);
		
		//Send Message
		System.out.println("GESENDET: \n" + request.toString());

		
		if (type == Request.INVITE) {
			ClientTransaction trans = sipProvider.getNewClientTransaction(request);
			dialog = trans.getDialog();
			trans.sendRequest();
		} else {
			sipProvider.sendRequest(request);
		}
		
	}

	@Override
	public void processDialogTerminated(DialogTerminatedEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void processIOException(IOExceptionEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void processRequest(RequestEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void processResponse(ResponseEvent arg0) {
		Response response = arg0.getResponse();
        int status = response.getStatusCode();
        
        System.out.println("ERHALTEN: \n" + response.toString());

        if( (status >= 200) && (status < 300) ) {
            System.out.println("OK erhalten");
        		if (pending) {
        			long i = 1;
        			Request ack;
					try {
						//Dialog dialog = arg0.getClientTransaction().getDialog();					
						ack = dialog.createAck(i);
						dialog.sendAck(ack);
						System.out.println("ACK gesendet");
						pending = false;
					} catch (InvalidArgumentException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (SipException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
        			
        		}
                return;
        }
		
	}

	@Override
	public void processTimeout(TimeoutEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void processTransactionTerminated(TransactionTerminatedEvent arg0) {
		// TODO Auto-generated method stub
		
	}

}
