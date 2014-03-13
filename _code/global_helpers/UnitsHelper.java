package global_helpers;

/**
 * Controller für Umrechnungen der Geschwindigkeitseinheiten
 * @author Honors-WInfo-Projekt (Fabian Böhm, Alexander Puchta)
 */
public class UnitsHelper {
	
	/**
	 * Umrechnung von [s/km] zu [min/km]
	 * @param sKm
	 * @return: min/km
	 */
	public static double toMinKm (double sKm) {
		return (sKm/60);
	}
	
	/**
	 * Umrechnung von [s/km] zu [km/h]
	 * @param sKm
	 * @return: km/h
	 */
	public static double toKmH (double sKm) {
		return  60*60/sKm;
	}
	
	/**
	 * Umrechnung von [s/km] zu [m/s]
	 * @param sKm
	 * @return: m/s
	 */
	public static double toMS (double sKm) {
		return (1000/sKm);
	}
	
	/**
	 * Umrechnung von [min/km] zu [s/km]
	 * @param minKm
	 * @return s/km
	 */
	public static double minKmToSKm (double minKm) {
		return minKm*60;
	}
	
	/**
	 * Umrechnung von [km/h] zu [s/km]
	 * @param kmH
	 * @return s/km
	 */
	public static double kmHToSKm (double kmH) {
		return (60*60)/kmH;
	}
	
	/**
	 * Umrechnung von [m/s] zu [s/km]
	 * @param MS
	 * @return s/km
	 */
	public static double MSToSKm (double MS) {
		return 1000/MS;
	}
}