package org.example.ttp2;

import java.awt.Color;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.util.Set;
import java.util.TooManyListenersException;

import javax.sip.InvalidArgumentException;
import javax.sip.ObjectInUseException;
import javax.sip.PeerUnavailableException;
import javax.sip.SipException;
import javax.sip.TransportNotSupportedException;


/**
 *	Grafische Benutzeroberfläche
 * @author Carsten Noetzel, Armin Steudte
 */
public class GUI extends javax.swing.JFrame implements IUpdateGUI{
	
	private UAS sipUAS;
	private UAC sipUAC;
	private IGMPListener listener;
	private IGMPSender sender;
	
	private static final String USERNAME = "Carmin";
	private static final String HOST = "141.22.27.34";
	private static final String PROXY = "tiserver03.cpt.haw-hamburg.de";
	private static final String MULTICAST_GROUP = "239.238.237.17";
	private static final int MULTICAST_PORT = 9017;
	private static final int SIP_PORT = 5060;
	
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBye;
    private javax.swing.JButton btnInvite;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JTextArea txtSessions;
    private javax.swing.JLabel labRemoteHost;
    private javax.swing.JLabel labRemoteUsername;
    private javax.swing.JLabel labStatusListener;
    private javax.swing.JLabel labStatusRegistered;
    private javax.swing.JLabel labStatusSender;
    private javax.swing.JTextArea txtMCast;
    private javax.swing.JTextField txtRemoteHost;
    private javax.swing.JTextField txtRemoteUsername;
    // End of variables declaration//GEN-END:variables
	

    public GUI() {
        initComponents();
                
		try {
			//Sip-Komponenten erzeugen
			SIPLayer sipLayer = new SIPLayer(USERNAME, HOST, SIP_PORT);	//SIP-Layer
			sipUAS = new UAS(sipLayer, this);							//UserAgentServer
			sipUAC = new UAC(sipLayer);									//UserAgentClient
			
			sipUAS.registerAtProxy(PROXY, SIP_PORT);				//Registrierung am Proxy
			
			//IGMP-Sender erzeugen
			sender = new IGMPSender();

			//Sender initialisieren und Thread starten
			sender.initialize(InetAddress.getByName(MULTICAST_GROUP), MULTICAST_PORT, sipUAS, this);
			new Thread(sender).start();
			
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
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SipException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        txtMCast = new javax.swing.JTextArea();
        labRemoteUsername = new javax.swing.JLabel();
        txtRemoteUsername = new javax.swing.JTextField();
        labRemoteHost = new javax.swing.JLabel();
        txtRemoteHost = new javax.swing.JTextField();
        btnInvite = new javax.swing.JButton();
        btnBye = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        txtSessions = new javax.swing.JTextArea();
        jLabel2 = new javax.swing.JLabel();
        jSeparator2 = new javax.swing.JSeparator();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        labStatusRegistered = new javax.swing.JLabel();
        labStatusSender = new javax.swing.JLabel();
        labStatusListener = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        txtMCast.setColumns(20);
        txtMCast.setRows(5);
        jScrollPane1.setViewportView(txtMCast);

        labRemoteUsername.setText("Username");

        txtRemoteUsername.setText("Hexren");

        labRemoteHost.setText("Host");

        txtRemoteHost.setText("141.22.27.133");

        btnInvite.setText("Invite");
        btnInvite.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnInviteMouseClicked(evt);
            }
        });

        btnBye.setText("Bye");
        btnBye.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnByeMouseClicked(evt);
            }
        });

        jLabel1.setText("Multicast Messages");

        txtSessions.setColumns(20);
        txtSessions.setRows(5);
        jScrollPane2.setViewportView(txtSessions);

        jLabel2.setText("Inbound Invites");

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel3.setText("Status");

        jLabel4.setText("IGMP Sender");

        jLabel5.setText("IGMP Listener");

        jLabel6.setText("Registered");

        labStatusRegistered.setText("false");
        labStatusRegistered.setForeground(Color.RED);

        labStatusSender.setText("off");
        labStatusSender.setForeground(Color.RED);

        labStatusListener.setText("off");
        labStatusListener.setForeground(Color.RED);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 363, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jSeparator2, javax.swing.GroupLayout.DEFAULT_SIZE, 407, Short.MAX_VALUE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(btnInvite, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(btnBye, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(labRemoteUsername)
                                    .addComponent(labRemoteHost))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtRemoteHost, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtRemoteUsername, javax.swing.GroupLayout.PREFERRED_SIZE, 216, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 247, Short.MAX_VALUE)
                                    .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 247, Short.MAX_VALUE))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel3)
                                    .addComponent(jLabel6)
                                    .addComponent(jLabel4)
                                    .addComponent(jLabel5))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(labStatusListener)
                                    .addComponent(labStatusRegistered)
                                    .addComponent(labStatusSender))
                                .addGap(35, 35, 35))))
                    .addComponent(jLabel1))
                .addGap(14, 14, 14))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(labRemoteUsername)
                            .addComponent(txtRemoteUsername, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(labRemoteHost)
                            .addComponent(txtRemoteHost, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnInvite)
                            .addComponent(btnBye))
                        .addGap(26, 26, 26)
                        .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 143, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel6)
                                    .addComponent(labStatusRegistered))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel4)
                                    .addComponent(labStatusSender))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel5)
                                    .addComponent(labStatusListener)))))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 464, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(20, 20, 20))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(GUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(GUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(GUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(GUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                new GUI().setVisible(true);
            }
        });
    }

    
	/**
	 * INVITE
	 * @param evt
	 */
	private void btnInviteMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnInviteMouseClicked
		String user = txtRemoteUsername.getText();		//Nutzernamen aus Textfeld auslesen
		String host = txtRemoteHost.getText();			//Host aus Textfeld auslesen
		
		listener = new IGMPListener();					//IGMP Listener erzeugen
		
		try {
			sipUAC.sendInvite(user, host);				//Invite an Nutzer senden über UserAgentClient
			
			//Listener initialisieren und Thread starten
			listener.initialize(InetAddress.getByName(MULTICAST_GROUP), MULTICAST_PORT, this);
			new Thread(listener).start();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SipException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}//GEN-LAST:event_btnInviteMouseClicked

	
	/**
	 * BYE
	 * @param evt
	 */
	private void btnByeMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnByeMouseClicked
		try {
			sipUAC.sendBye();						//Bye senden über UserAgentClient
			
			setStatusListener(false);				//Status aktualisieren
			listener.stop();						//IGMP-Listener ausschalten
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SipException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}//GEN-LAST:event_btnByeMouseClicked



	@Override
	public void addMessage(String msg) {
		txtMCast.append(msg + "\n");
	}

	@Override
	public void setRegistered(boolean flag) {
		if (flag) {
			labStatusRegistered.setText("true");
			labStatusRegistered.setForeground(Color.GREEN);
		} else {
			labStatusRegistered.setText("false");
			labStatusRegistered.setForeground(Color.RED);
		}
	}

	@Override
	public void setStatusListener(boolean flag) {
		if (flag) {
			labStatusListener.setText("on");
			labStatusListener.setForeground(Color.GREEN);
		} else {
			labStatusListener.setText("off");
			labStatusListener.setForeground(Color.RED);
		}
	}

	@Override
	public void setStatusSender(boolean flag) {
		if (flag) {
			labStatusSender.setText("on");
			labStatusSender.setForeground(Color.GREEN);
		} else {
			labStatusSender.setText("off");
			labStatusSender.setForeground(Color.RED);
		}
	}

	@Override
	public void updateSessions(Set<String> sessions) {
		txtSessions.setText("");
		for (String session : sessions) {
			txtSessions.append(session + "\n");
		}		
	}
}
