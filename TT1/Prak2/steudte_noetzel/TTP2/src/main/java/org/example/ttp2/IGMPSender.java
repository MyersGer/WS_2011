package org.example.ttp2;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import org.apache.log4j.Logger;

public class IGMPSender extends IGMPComponent {

	// Name des Loggers
	public static final String TAG = "IGMPSender";
	// Loggerinstanz
	private static final Logger LOGGER = Logger.getLogger(TAG);
	// private static final byte TTL = 1;
	private UAS uas;

	public IGMPSender() {

		super();

		buf = "Hallo von Noetzel Steudte".getBytes();
		pack = new DatagramPacket(buf, buf.length);
	}

	/**
	 * Initialisiert den MulticastSocket
	 * 
	 * @param ip
	 *            IPAdresse der Multicastgruppe
	 * @param port
	 *            Port auf den
	 * @throws IOException
	 *             Fehler beim erzeugen des IPAdressen-Objekts oder Port
	 */
	protected void initialize(InetAddress ip, int port, UAS uas) throws IOException {
		this.uas = uas;

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
				if (uas.sessionCount() > 0) {
					mSocket.send(pack);
					LOGGER.debug("Nachricht erfolgreich gesendet: " + buf.toString());
				} else {
					LOGGER.debug("Keine Nachricht gesendet, weil session count <= 0");
				}
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

			mSocket.leaveGroup(mcastAdr);

		} catch (IOException e) {
			LOGGER.error("Fehler beim verlassend er Multicastgruppe: " + e);
		} finally {
			mSocket.close();
		}

		LOGGER.debug("MulticastSocket abgebaut");

	}

}
