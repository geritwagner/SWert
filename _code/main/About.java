package main;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

/**
 * @author Honors-WInfo-Projekt (Fabian Böhm, Alexander Puchta), Gerit Wagner
 */

public class About extends JDialog {

	private static final long serialVersionUID = 1L;
	private Dimension d = getToolkit().getScreenSize();
	
	
	public About() {
	    super(Hauptfenster.aktuellesHauptfenster, "About", true);
	
	    Box b = Box.createVerticalBox();
	    b.add(Box.createGlue());
	    
	    
	    EmptyBorder eBorder = new EmptyBorder(2, 10, 2, 10);
        JLabel version = new JLabel("SWert Version " + Hauptfenster.Version);
        version.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(), eBorder)); 

        JLabel authors = new JLabel("Entwickler: " + Hauptfenster.Autoren);
        authors.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(), eBorder)); 
        
        JLabel license = new JLabel("Lizenz: GPL?");
        // add license-link?
        license.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(), eBorder)); 

        b.add(version);
	    b.add(authors);
	    b.add(license);
	    
	    b.add(Box.createGlue());
	    getContentPane().add(b, "Center");
	
	    JPanel p2 = new JPanel();
	    JButton ok = new JButton("Ok");
	    p2.add(ok);
	    getContentPane().add(p2, "South");
	
	    ok.addActionListener(new ActionListener() {
	      public void actionPerformed(ActionEvent evt) {
	        setVisible(false);
	        dispose();
	      }
	    });
	    setResizable(false);
		setTitle("About");
		setBounds(100, 100, 100, 100);
	    setLocation((int) ((d.getWidth() - this.getWidth()) / 2.5), (int) ((d.getHeight() - this.getHeight()) / 2.5));
	    setSize(500, 150);
	    setVisible(true);
	  }
}