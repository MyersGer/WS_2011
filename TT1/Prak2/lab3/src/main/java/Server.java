import java.text.ParseException;

import javax.sip.DialogTerminatedEvent;
import javax.sip.InvalidArgumentException;
import javax.sip.RequestEvent;
import javax.sip.SipException;


public class Server{
	
	private SipLayerServer sipLayer;
	
	
	
	public Server(SipLayerServer sipLayer) {
		super();
		this.sipLayer = sipLayer;
	}
	
	public void sendInvite() throws ParseException, InvalidArgumentException, SipException {
		sipLayer.sendInvite("sip:wilma@141.22.26.40:5060"); 
	}



	
	
	
}
