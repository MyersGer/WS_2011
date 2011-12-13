/*
 * SIPGUIApp.java
 */
package sipgui;

import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;
import sip.UAC;
import sip.UAS;

/**
 * The main class of the application.
 */
public class SIPGUIApp extends SingleFrameApplication {

    /**
     * At startup create and show the main frame of the application.
     */
    @Override
    protected void startup() {
        try {
            show(new SIPGUIView(this));
        } catch (UnknownHostException ex) {
            Logger.getLogger(SIPGUIApp.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SocketException ex) {
            Logger.getLogger(SIPGUIApp.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * This method is to initialize the specified window by injecting resources.
     * Windows shown in our application come fully initialized from the GUI
     * builder, so this additional configuration is not needed.
     */
    @Override
    protected void configureWindow(java.awt.Window root) {
    }

    /**
     * A convenient static getter for the application instance.
     * @return the instance of SIPGUIApp
     */
    public static SIPGUIApp getApplication() {
        return Application.getInstance(SIPGUIApp.class);
    }

    /**
     * Main method launching the application.
     */
    public static void main(String[] args) {
        launch(SIPGUIApp.class, args);
    }
}
