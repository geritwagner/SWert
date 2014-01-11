package helper;

import java.awt.Toolkit;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

/**
 * Erweiterung zum PlainDocument
 * Erweiterungen: Nur Zahlen
 * @author Honors-WInfo-Projekt (Fabian Böhm, Alexander Puchta)
 */
public class IntegerDocument extends PlainDocument {

//----------------------- VARIABLEN -----------------------
	private static final long serialVersionUID = 1L;

//----------------------- ÖFFENTLICHE METHODEN -----------------------
	@Override
	public void insertString(int offset, String s, AttributeSet attributeSet)
			throws BadLocationException {
		try {
			Integer.parseInt(s);
		} catch (Exception ex) {
			Toolkit.getDefaultToolkit().beep();
			return;
		}
		super.insertString(offset, s, attributeSet);
	}
}
