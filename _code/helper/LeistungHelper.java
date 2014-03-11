package helper;

import java.text.*;
import java.util.Locale;


/**
 * Controller zum Handlen aller Aktionen, die einzelne Leistungen betreffen
 * @author Honors-WInfo-Projekt (Fabian Böhm, Alexander Puchta)
 */
public class LeistungHelper {

	/**
	 * Berechnen der Geschwindigkeit [s/km] anhand Streckenlänge [m] und formatiertem Zeitstring (Form: 00:00:00:000 --> HH:MinMin:SecSec,MSecMSec)
	 */
	public double berechneGeschwindigkeit(int strecke, String zeit) {
		double sec = parseZeitInSec(zeit);
		return berechneGeschwindigkeit(strecke, sec);
	}
	
	public double berechneGeschwindigkeit (int strecke, double zeit){
		double streckeInKm = strecke/1000D;
		double geschwindigkeit = zeit/streckeInKm;
		return geschwindigkeit;
	}
	
	 /**
	  * Berechnen der Zeit [sec] anhand Streckenlänge und Geschwindigkeit [s/km]
	  */
	public double berechneZeit(int strecke, double geschwindigkeit) {
		double sec = 0;
		double streckeKm = strecke/1000D;
		sec = geschwindigkeit * streckeKm;
		return sec;
	}
	
	/**
	 * Umwandeln des formatierten Strings in [sec]
	 */
	public double parseMinStringToSec (String minutenString) {
		double sec = 0D;
		String[] zeitArray = new String[3];
		String[] minArray = minutenString.split(":");
		String[] secArray = minArray[1].split(",");
		
		zeitArray[0] = minArray[0];
		zeitArray[1] = secArray[0];
		zeitArray[2] = secArray[1];
		
		sec += Integer.parseInt(zeitArray[0])*60;
		sec += Integer.parseInt(zeitArray[1]);
		sec += Integer.parseInt(zeitArray[2])/1000;
		
		return sec;
	}
	 
	/**
	 * Umwandeln der [sec] in formatierten Zeitstring (00:00:00,00)
	 */
	public String parseSecInZeitstring (double sec) {
		String zeitString = null;
		int milliSec = (int) (sec * 100D) % 100;
		int intSec = (int) sec;
		int minuten = intSec / 60;
		intSec = intSec - (minuten * 60);
		int stunden = minuten / 60;
		minuten = minuten - (stunden * 60);
		zeitString = buildZeitString(stunden, minuten, intSec, milliSec);
		return zeitString;
	}
	
	/**
	 * Umwandeln der [sec] in formatierten Zeitstring ("00:00:00,00" ODER "00:00,00")
	 */
	public String parseSecInMinutenstring (double sec) {
		String zeitString = null;
		int milliSec = (int) (sec * 10D) % 10;
		int intSec = (int) sec;
		int minuten = intSec / 60;
		intSec = intSec - (minuten * 60);	
		int stunden = minuten / 60;
		minuten = minuten - (stunden * 60);
		zeitString = buildMinutenString(stunden,minuten, intSec, milliSec);
		return zeitString;
	}
	
	/**
	 * Aufbauen des formatierten Zeitstrings (00:00:00,00) aus
	 * Integer-Repräsentationen der Stunden, Minuten, Sekunden und Millisekunden
	 */
	private String buildZeitString(int stunden, int minuten, int sekunden, int milSekunden) {
		String zeitString = null;
		String stundenString = String.valueOf(stunden);
		if (stundenString.length() == 1) {
			stundenString = "0"+stundenString;
		}
		String minutenString = String.valueOf(minuten);
		if (minutenString.length() == 1) {
			minutenString = "0"+minutenString;
		}
		String sekundenString = String.valueOf(sekunden);
		if (sekundenString.length() == 1) {
			sekundenString = "0"+sekundenString;
		}
		String milSekundenString = String.valueOf(milSekunden);
		if (milSekundenString.length() == 1) {
			milSekundenString = "0"+milSekundenString;
		}
		
		zeitString = stundenString+":"+minutenString+":"+sekundenString+","+milSekundenString;
		
		return zeitString;
	}

	/**
	 * Aufbauen des formatierten Zeitstrings ("00:00:00,00" ODER "00:00,00") aus
	 * Integer-Repräsentationen der Stunden, Minuten, Sekunden und Millisekunden
	 */
	private String buildMinutenString(int stunden, int minuten, int sekunden, int milSekunden) {
		String zeitString = null;	
		
		String stundenString = String.valueOf(stunden);
		if (stundenString.length() == 1) {
			stundenString = "0"+stundenString;
		}
		
		String minutenString = String.valueOf(minuten);
		if (minutenString.length() == 1) {
			minutenString = "0"+minutenString;
		}
		String sekundenString = String.valueOf(sekunden);
		if (sekundenString.length() == 1) {
			sekundenString = "0"+sekundenString;
		}
		String milSekundenString = String.valueOf(milSekunden);		
		if (milSekundenString.length() == 1) {
			milSekundenString = "0"+milSekundenString;
		}
		if (stunden == 0) {
			zeitString = minutenString+":"+sekundenString+","+milSekundenString;
		} else {
			zeitString = stundenString + ":" + minutenString+":"+sekundenString+","+milSekundenString;
		}
		return zeitString;
	}
	
	/**
	 * Ersetzen des Dezimal-Kommes innerhalb eines String ("12,34") durch
	 * Punkt und parsen des entstehenden Double-Wertes (12.34)
	 */
	private double convertGeschwindigkeitStringToDouble (String geschwindigkeitString) {
		NumberFormat format = NumberFormat.getInstance(Locale.GERMANY);
		Number number;
		double geschwindigkeit = 0d;
		try {
			number = format.parse(geschwindigkeitString);
			geschwindigkeit = number.doubleValue();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return geschwindigkeit;
	}
	
	/**
	 * Umwandeln des formatierten Strings ("00:00:00,00") in [sec]
	 */
	public double parseZeitInSec (String zeit) {
		double sec = 0;
		String[] zeitSplit = new String[4];
		String[] hMinSec = zeit.split(":");
		String[] milSec = hMinSec[2].split(",");
		
		zeitSplit[0] = hMinSec[0];
		zeitSplit[1] = hMinSec[1];
		zeitSplit[2] = milSec[0];
		zeitSplit[3] = milSec[1];
		
		sec += Double.parseDouble(zeitSplit[0])*60*60;
		sec += Double.parseDouble(zeitSplit[1])*60;
		sec += Double.parseDouble(zeitSplit[2]);
		sec += Double.parseDouble(zeitSplit[3])/100;
		return sec;
	}
	
	/**
	 * Berechnen der Streckenlänge [m] aus Geschwindigkeit [s/km] und Zeit (1h = 3600s)
	 */
	public int berechneSchwellenStreckeAusGeschwindigkeit(double geschwindigkeit) {
		int strecke = 0;
		int sec = 3600;
		geschwindigkeit = UnitsHelper.toMS(geschwindigkeit);
		strecke = (int) (Math.round(geschwindigkeit * sec));
		return strecke;
	}
}