import java.text.ParseException;
import java.util.TooManyListenersException;

import javax.sip.InvalidArgumentException;
import javax.sip.SipException;

import org.apache.log4j.Logger;


public class ServerRunner {
	private static Logger logger = Logger.getLogger("SIP_Server");
	
	public static void main(String[] args) throws InvalidArgumentException, TooManyListenersException, ParseException, SipException {
		logger.debug("main()");
		SipLayerServer sipLayer = new SipLayerServer("oliver", "141.22.27.133", 5060);
		
		Server server = new Server(sipLayer);
//		server.sendInvite();
//		server.sendRegister();
//		server.sendMessage("sip:wilma@141.22.26.40:5060", "foo");
	}
	
	
}
