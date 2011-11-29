import javax.sip.DialogTerminatedEvent;
import javax.sip.RequestEvent;

public interface IMessageProcessor {

	public void processMessage(String sender, String message);

	public void processError(String errorMessage);

	public void processInfo(String infoMessage);

	public void processDialogTerminated(DialogTerminatedEvent diaTermEv);

	public void processRequestEvent(RequestEvent requestEvent);
}
