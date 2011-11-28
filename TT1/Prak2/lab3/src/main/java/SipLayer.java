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
import javax.sip.SipFactory;
import javax.sip.SipListener;
import javax.sip.SipProvider;
import javax.sip.SipStack;
import javax.sip.TimeoutEvent;
import javax.sip.TransactionTerminatedEvent;
import javax.sip.TransportNotSupportedException;
import javax.sip.address.AddressFactory;
import javax.sip.header.HeaderFactory;
import javax.sip.message.MessageFactory;

public class SipLayer implements SipListener {

	private MessageProcessor messageProcessor;

	private String username;

	private SipStack sipStack;

	private SipFactory sipFactory;

	private AddressFactory addressFactory;

	private HeaderFactory headerFactory;

	private MessageFactory messageFactory;

	private SipProvider sipProvider;

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

	public void processResponse(ResponseEvent arg0) {
		// TODO Auto-generated method stub

	}

	public void processTimeout(TimeoutEvent arg0) {
		// TODO Auto-generated method stub

	}

	public void processTransactionTerminated(TransactionTerminatedEvent arg0) {
		// TODO Auto-generated method stub

	}

}
