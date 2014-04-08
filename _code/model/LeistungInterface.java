package model;

/**
 * @author Honors-WInfo-Projekt (Fabian Böhm, Alexander Puchta), Gerit Wagner
 */

interface LeistungInterface {
	
	public long getId();
	public int getId_strecke();
	public long getId_athlet();
	public int getStrecke();
	public String getStreckenString();
	public String getBezeichnung();
	public String getDatum();
	public boolean isUsedForSlopeFaktor();
	public String toString();
	public boolean equals(Leistung andereLeistung);
	public boolean equalsWithoutIDs (Leistung andereLeistung);
	
	public void updateLeistung(int id_strecke, String bezeichnung, String datum, double geschwindigkeit);

	public double getGeschwindigkeit();
	public String getZeitString();
	public double getZeit();
	
	// bei setZeit wird die Geschwindigkeit automatisch berechnet und gesetzt & vice versa
	public void setZeitFromString(String zeit);
	public void setZeitAndGeschwindigkeit(double zeit);
	public void setGeschwindigkeitAndGeschwindigkeit(double geschwindigkeit);

	public Object[] getObjectDataForTable();
}