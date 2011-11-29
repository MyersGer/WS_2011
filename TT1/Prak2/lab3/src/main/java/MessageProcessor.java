
public class MessageProcessor {

    public void processMessage(String sender, String message){
    	System.out.println(message);
    }

    public void processError(String errorMessage){
    	System.out.println(errorMessage);
    }

    public void processInfo(String infoMessage){
    	System.out.println(infoMessage);
    }

}
