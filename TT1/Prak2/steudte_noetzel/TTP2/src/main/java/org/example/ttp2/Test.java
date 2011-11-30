package org.example.ttp2;

import java.text.ParseException;
import java.util.TooManyListenersException;

import javax.sip.InvalidArgumentException;
import javax.sip.ObjectInUseException;
import javax.sip.PeerUnavailableException;
import javax.sip.SipException;
import javax.sip.TransportNotSupportedException;

import org.omg.CORBA.Current;

public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			UAC testUAC = new UAC("UAC-Test","141.22.27.135",5060);
			//testUAC.registerAtProxy("tiserver03.cpt.haw-hamburg.de", 5060);
			testUAC.sendInvite("wilma", "141.22.26.40");
			testUAC.sendMessageOverDialog("This is a Test");
			//testUAC.sendCancel("wilma", "141.22.26.40");
			//testUAC.sendBye("wilma", "141.22.26.40");
		} catch (PeerUnavailableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransportNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ObjectInUseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TooManyListenersException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SipException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
