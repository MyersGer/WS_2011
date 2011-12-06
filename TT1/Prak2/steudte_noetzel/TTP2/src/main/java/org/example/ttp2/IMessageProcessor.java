package org.example.ttp2;

public interface IMessageProcessor {
	
	public void processDialogTerminated();
	public void processOK();
	public void processTrying();
	public void processRinging();

}
