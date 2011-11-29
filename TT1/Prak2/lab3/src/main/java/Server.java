import javax.sip.DialogTerminatedEvent;
import javax.sip.RequestEvent;


public class Server implements IMessageProcessor{
	
	private SipLayer sipLayer;
	
	
	
	public Server(SipLayer sipLayer) {
		super();
		this.sipLayer = sipLayer;
	}



	public void start() {
		//startListening in sip
	}



	public void processMessage(String sender, String message) {
		// TODO Auto-generated method stub
		
	}



	public void processError(String errorMessage) {
		// TODO Auto-generated method stub
		
	}



	public void processInfo(String infoMessage) {
		// TODO Auto-generated method stub
		
	}



	public void processDialogTerminated(DialogTerminatedEvent diaTermEv) {
		// TODO Auto-generated method stub
		
	}



	public void processRequestEvent(RequestEvent requestEvent) {
		// TODO Auto-generated method stub
		
	}
	
	
}
