package globale_helper;

import java.awt.*;
import javax.swing.text.*;

/**
 * @author Honors-WInfo-Projekt (Fabian Böhm, Alexander Puchta), Gerit Wagner
 */

public class IntegerDocument extends PlainDocument {
	/**
	 * Erweiterung zum PlainDocument
	 * Erweiterungen: Nur Zahlen
	 */
	private static final long serialVersionUID = 1L;

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