package org.example.ttp2;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

/**
 * IGMPComponent Klasse als Basisklasse für den IGMP-Listener und den IGMP-Sender
 * Dient zur Kapselung von Funktionalität die sowohl im Listener als auch im Sender
 * benötigt wird
 * @author Carsten Noetzel, Armin Steudte
 *
 */
public abstract class IGMPComponent implements Runnable {

	// Größe der erwarteten Pakete
	public static final int PACKETSIZE = 257;
	
	protected MulticastSocket mSocket;
	protected InetAddress mcastAdr;
	protected int port;
	
	protected byte buf[];
	protected DatagramPacket pack;
	
	protected boolean isRunning;
	
	
	
	public IGMPComponent() {
		
		mSocket = null;
		isRunning = true;
		
		// Buffer für Pakete und passendes Datagramobjekt erzeugen
		buf = new byte[PACKETSIZE];
		
	}
	
	/**
	 * Setzt das isRunning-Flag und sorgt dafür, dass
	 * der Thread stoppt wird.
	 */
	public void stop() {
		
		this.isRunning = false;
		
	}
	
}
