package org.example.ttp2;

import java.util.Set;

/**
 * UpdateGUI Interface zur Aktualisierung der GUI
 * Die GUI implementiert diese Interface damit Objekte 
 * die eine Refrenz auf ein IUpdateGUI Objekt halten die
 * GUI aktualisieren können
 * @author Carsten Noetzel, Armin Steudte
 *
 */
public interface IUpdateGUI {
	
	public void addMessage(String msg);
	public void updateSessions(Set<String> sessions);
	public void setRegistered(boolean flag);
	public void setStatusListener(boolean flag);
	public void setStatusSender(boolean flag);
	

}
