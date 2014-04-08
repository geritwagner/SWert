package main;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

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
    b.add(new JLabel("SWert Version " + Hauptfenster.Version));
    b.add(new JLabel("Contributors:" + Hauptfenster.Autoren));
    b.add(new JLabel("License: t.b.d."));
    b.add(Box.createGlue());
    getContentPane().add(b, "Center");

    JPanel p2 = new JPanel();
    JButton ok = new JButton("Ok");
    p2.add(ok);
    getContentPane().add(p2, "South");

    ok.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        setVisible(false);
      }
    });
    setResizable(false);
	setTitle("About");
	setBounds(100, 100, 100, 100);
    setLocation((int) ((d.getWidth() - this.getWidth()) / 2.5), (int) ((d.getHeight() - this.getHeight()) / 2.5));
    setSize(500, 150);
    setVisible(true);
    //TODO: icon
  }
}