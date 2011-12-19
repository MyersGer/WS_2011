package igmp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import org.apache.log4j.Logger;


public class IGMPListener extends IGMPComponent{
	
	private IGMPListenerObserver observer = null;
	
	public void setObserver(IGMPListenerObserver observer){
		this.observer = observer;
	}
	
	// Name des Loggers
	public static final String TAG = "IGMPListener";
	
	// Loggerinstanz
	private static final Logger LOGGER = Logger.getLogger(TAG);
	
	/**
	 * Initialisiert den MulticastSocket 
	 * 
	 * @param ip			IPAdresse der Multicastgruppe
	 * @param port			Port auf den 
	 * @throws IOException  Fehler beim erzeugen des IPAdressen-Objekts oder Port
	 */
	public void initialize(InetAddress ip, int port) throws IOException{
		
		// Socket anlegen und Gruppe joinen
		mSocket = new MulticastSocket(port);
		mcastAdr = ip;
		
		mSocket.joinGroup(mcastAdr);
		
		// IP und Port für später speichern
		mcastAdr = ip;
		this.port = port;
		pack = new DatagramPacket(buf, buf.length);
		
	}
	
	@Override
	public void run() {
		
		LOGGER.debug("run()-Methode aufgerufen");
		LOGGER.debug("isRunning: "+isRunning);
		
		while(isRunning) {
			
			
			
			try {
				
				// Auf Daten warten
				mSocket.receive(pack);
				
				//Empfangene Daten ausgeben
				String message = new String(pack.getData());
				LOGGER.trace("Message: " + message);
				if(null!=observer)
					observer.addMessage(message);
				
			} catch (IOException e) {
				
				LOGGER.error("Fehler beim Lesen aus dem MulticastSocket: "+e);
				
				stop();
				
			}
			
			
		}
		
		LOGGER.debug("Schleife beendet");
		
		try {
			
			mSocket.leaveGroup(mcastAdr);
			
		} catch (IOException e) {
			LOGGER.error("Fehler beim verlassend er Multicastgruppe: "+e);
		} finally {
			mSocket.close();
		}
		
		LOGGER.debug("MulticastSocket abgebaut");

	}

}
