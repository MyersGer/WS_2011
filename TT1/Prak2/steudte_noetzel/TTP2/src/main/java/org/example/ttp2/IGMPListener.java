package org.example.ttp2;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import org.apache.log4j.Logger;

/**
 * IGMP Listener zum Empfang von Multicast Nachrichten
 * @author Carsten Noetzel, Armin Steudte
 *
 */
public class IGMPListener extends IGMPComponent{
	
	//Referenz auf Interface zur Aktualisierung der GUI
	private IUpdateGUI gui;
	
	// Name des Loggers
	public static final String TAG = "IGMPListener";
	
	// Loggerinstanz
	private static final Logger LOGGER = Logger.getLogger(TAG);
	
	/**
	 * Initialisiert den MulticastSocket 
	 * 
	 * @param ip			IPAdresse der Multicastgruppe
	 * @param port			Port auf dem Nachrichten empfangen werden
	 * @throws IOException  Fehler beim erzeugen des IPAdressen-Objekts oder Port
	 */
	protected void initialize(InetAddress ip, int port, IUpdateGUI gui) throws IOException{
		
		this.gui = gui;
		
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
			
			LOGGER.debug("Schleife erreicht");
			//Status in der GUI anzeigen
			gui.setStatusListener(true);		
			
			try {
				
				// Auf Daten warten
				mSocket.receive(pack);
				
				//Empfangene Daten ausgeben
				System.out.write(pack.getData(),0,pack.getLength());
				System.out.println();
				gui.addMessage(new String(pack.getData()));
				
			} catch (IOException e) {
				
				LOGGER.error("Fehler beim Lesen aus dem MulticastSocket: "+e);
				stop();
				
			}
			
			
		}
		
		LOGGER.debug("Schleife beendet");
		//Status in der GUI anzeigen
		gui.setStatusListener(false);
		
		try {
			//Multicastgruppe verlassen
			mSocket.leaveGroup(mcastAdr);
			
		} catch (IOException e) {
			LOGGER.error("Fehler beim verlassend er Multicastgruppe: "+e);
		} finally {
			mSocket.close();
		}
		
		LOGGER.debug("MulticastSocket abgebaut");

	}

}
