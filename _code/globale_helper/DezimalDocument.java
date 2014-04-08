package globale_helper;

/**
 * @author Honors-WInfo-Projekt (Fabian Böhm, Alexander Puchta), Gerit Wagner
 */

public class DezimalDocument extends javax.swing.text.PlainDocument{
	/**
	 * Erweiterung zum PlainDocument
	 * Erweiterungen: Nur Zahlen, Komma als Dezimaltrennzeichen, maximal 5 Stellen (inkl. Komma)
	 */
	private static final long serialVersionUID = 1L;

    @Override
	public void insertString(int offset, String str, javax.swing.text.AttributeSet a)
    throws javax.swing.text.BadLocationException {
        //Dezimaltrenner, je nach Land abfragen und einsetzen
        char decimalSeparator = (new java.text.DecimalFormatSymbols()).getDecimalSeparator();
        String valid = "0123456789" + decimalSeparator;
        for (int i=0; i<str.length();i++) {
            if (valid.indexOf(str.charAt(i)) == -1) {
                return;
            }
            //Aufruf der übergeordneten Methode
            if (getLength()+str.length() <= 5) {
            	super.insertString(offset, str, a);            	
            }
        }
    }
}