import java.text.ParseException;
import java.util.TooManyListenersException;

import javax.sip.InvalidArgumentException;
import javax.sip.SipException;


public class ServerRunner {
	public static void main(String[] args) throws InvalidArgumentException, TooManyListenersException, ParseException, SipException {
		SipLayerServer sipLayer = new SipLayerServer("oliver", "127.0.0.1", 5060);
		Server server = new Server(sipLayer);
		//server.sendInvite();
	}
	
	
}
