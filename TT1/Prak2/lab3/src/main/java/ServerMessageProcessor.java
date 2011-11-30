import javax.sip.DialogTerminatedEvent;
import javax.sip.RequestEvent;

import org.apache.log4j.Logger;


public class ServerMessageProcessor implements IMessageProcessor{
	private static Logger logger = Logger.getLogger("SIP_Server");

	public void processMessage(String sender, String message) {
		logger.warn("Message: " + message);
		
	}

	public void processError(String errorMessage) {
		logger.warn("errorMessage: " + errorMessage);
		
	}

	public void processInfo(String infoMessage) {
		logger.warn("infoMessage: " + infoMessage);
		
	}

	public void processDialogTerminated(DialogTerminatedEvent diaTermEv) {
		logger.warn("processDialogTerminated: ");
		
	}

	public void processRequestEvent(RequestEvent requestEvent) {
		logger.warn("processRequestEvent: ");
		
	}

}
