package controller;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

/**
 * Controller zum Handlen aller Aktionen, die einzelne Leistungen betreffen
 * @author Honors-WInfo-Projekt (Fabian B�hm, Alexander Puchta)
 */
public class LeistungController {
	

//----------------------- �FFENTLICHE METHODEN -----------------------
	
	/**
	 * Berechnen der Geschwindigkeit anhand Streckenl�nge und formatiertem Zeitstring
	 * @param strecke [m]
	 * @param zeit (Form: 00:00:00:000 --> HH:MinMin:SecSec,MSecMSec)
	 * @return geschwindigkeit [s/km]
	 */
	public double berechneGeschwindigkeit(int strecke, String zeit) {
		double sec = parseZeitInSec(zeit);
		double streckeInKm = strecke/1000D;
		double geschwindigkeit = sec/streckeInKm;
		return geschwindigkeit;
	}
	
	 /**
	  * Berechnen der Zeit anhand Streckenl�nge und Geschwindigkeit
	  * @param Strecke [m]
	  * @param geschwindigkeit: [s/km]
	  * @return [sec]
	  */
	public double berechneZeit(int strecke, double geschwindigkeit) {
		double sec = 0;
		double streckeKm = strecke/1000D;
		sec = geschwindigkeit * streckeKm;
		return sec;
	}
	
	/**
	 * Umwandeln des formatierten Strings in [sec]
	 * @param minutenString
	 * @return [sec]
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
	 * @param minutenString
	 * @return [sec]
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
	 * @param minutenString
	 * @return [sec]
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
	 * Integer-Repr�sentationen der Stunden, Minuten, Sekunden und Millisekunden
	 * @param stunden
	 * @param minuten
	 * @param sekunden
	 * @param milSekunden
	 * @return
	 */
	public String buildZeitString(int stunden, int minuten, int sekunden, int milSekunden) {
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
	 * Integer-Repr�sentationen der Stunden, Minuten, Sekunden und Millisekunden
	 * @param stunden
	 * @param minuten
	 * @param sekunden
	 * @param milSekunden
	 * @return
	 */
	public String buildMinutenString(int stunden, int minuten, int sekunden, int milSekunden) {
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
	 * @param geschwindigkeitString
	 * @return
	 */
	public double convertGeschwindigkeitStringToDouble (String geschwindigkeitString) {
		NumberFormat format = NumberFormat.getInstance(Locale.GERMANY);
		Number number;
		double geschwindigkeit = 0d;
		try {
			number = format.parse(geschwindigkeitString);
			geschwindigkeit = number.doubleValue();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return geschwindigkeit;
	}
	
	/**
	 * Umwandeln des formatierten Strings ("00:00:00,00") in [sec]
	 * @param minutenString
	 * @return zeit
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
	 * Berechnen der Streckenl�nge aus Geschwindigkeit und Zeit (1h = 3600s)
	 * @param geschwindigkeit [s/km]
	 * @return [m]
	 */
	public int berechneStreckeAusGeschwindigkeit(double geschwindigkeit) {
		int strecke = 0;
		int sec = 3600;
		geschwindigkeit = Einheitenumrechner.toMS(geschwindigkeit);
		strecke = (int) (Math.round(geschwindigkeit * sec));
		return strecke;
	}
}
