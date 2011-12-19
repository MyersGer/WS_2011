package org.example.ttp2;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import org.apache.log4j.Logger;

/**
 * IGMP Sender zum Versenden von Multicast Nachrichten
 * @author Carsten Noetzel, Armin Steudte
 *
 */
public class IGMPSender extends IGMPComponent {
	
	//Referenz auf Interface zur Aktualisierung der GUI
	private IUpdateGUI gui;

	// Name des Loggers
	public static final String TAG = "IGMPSender";
	// Loggerinstanz
	private static final Logger LOGGER = Logger.getLogger(TAG);
	
	//Referenz auf den UserAgentServer
	private UAS uas;

	public IGMPSender() {

		super();

		buf = "Noetzel Steudte rule em all".getBytes();
		pack = new DatagramPacket(buf, buf.length);
	}

	/**
	 * Initialisiert den MulticastSocket
	 * 
	 * @param ip			IPAdresse der Multicastgruppe
	 * @param port			Port auf dem Nachrichten gesendet werden
	 * @throws IOException	Fehler beim erzeugen des IPAdressen-Objekts oder Port
	 */
	protected void initialize(InetAddress ip, int port, UAS uas, IUpdateGUI gui) throws IOException {
		this.uas = uas;
		this.gui = gui;
		
		// Socket anlegen
		mSocket = new MulticastSocket();

		// Datagram mit Zielsocket versehen
		pack.setAddress(ip);
		pack.setPort(port);

		// IP und Port für später speichern
		mcastAdr = ip;
		this.port = port;

	}

	@Override
	public void run() {

		LOGGER.debug("run()-Methode aufgerufen");
		LOGGER.debug("isRunning: " + isRunning);

		while (isRunning) {
			try {
				//da nur bei aktiven Sessions gesendet werden soll, wird der UserAgentServer nach der Anzhahl
				//aktiver Sessions gefragt und bei aktiven Sessions mit dem Senden begonnen
				if (uas.sessionCount() > 0) {
					gui.setStatusSender(true);
					mSocket.send(pack);
					LOGGER.debug("Nachricht erfolgreich gesendet: " + new String(buf));
				} else {
					gui.setStatusSender(false);
					LOGGER.debug("Keine Nachricht gesendet, weil session count <= 0");
				}
				//Wartezeit damit die Nachrichten nicht zu schnell versand werden
				Thread.sleep(2000);

			} catch (IOException e) {

				LOGGER.error("Fehler beim Senden der Nachrichten an die MulticastGruppe: " + e);
				stop();

			} catch (InterruptedException e) {

				LOGGER.error("Fehler beim Sleep des Threads: " + e);
				stop();

			}

		}

		LOGGER.debug("Schleife beendet");

		try {
			//Multicastgruppe verlassen
			mSocket.leaveGroup(mcastAdr);

		} catch (IOException e) {
			LOGGER.error("Fehler beim verlassend er Multicastgruppe: " + e);
		} finally {
			mSocket.close();
		}

		LOGGER.debug("MulticastSocket abgebaut");

	}

}
