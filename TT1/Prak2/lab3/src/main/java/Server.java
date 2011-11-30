import java.text.ParseException;

import javax.sip.DialogTerminatedEvent;
import javax.sip.InvalidArgumentException;
import javax.sip.RequestEvent;
import javax.sip.SipException;

import org.apache.log4j.Logger;


public class Server{
	private static Logger logger = Logger.getLogger("SIP_Server");
	private SipLayerServer sipLayer;
	
	
	
	public Server(SipLayerServer sipLayer) {
		super();
		logger.debug("Server()");
		this.sipLayer = sipLayer;
		sipLayer.setMessageProcessor(new ServerMessageProcessor());
	}
	
	public void sendInvite() throws ParseException, InvalidArgumentException, SipException {
		logger.debug("sendInvite()");
		sipLayer.sendInvite("sip:wilma@141.22.26.40:5060"); 
	}

	public void sendRegister() throws ParseException, InvalidArgumentException, SipException {
		logger.debug("sendRegister()");
		sipLayer.sendRegister("sip:proxy@tiserver03.cpt.haw-hamburg.de:5060"); 
		
	}

	public void sendMessage(String to, String message) throws ParseException, InvalidArgumentException, SipException {
		sipLayer.sendMessage(to, message);
		
	}



	
	
	
}
