package model;

interface LeistungInterface {

	public long getId();
	public int getId_strecke();
	public long getId_athlet();
	public int getStrecke();
	public String getBezeichnung();
	public String getDatum();
	public boolean isUsedForSlopeFaktor();
	public void setIsUsedForSlopeFaktor(boolean berechnungSlopeFaktor);
	public String toString();
	public boolean equals(Leistung andereLeistung);
	
	// TODO: update-Methode im Controller verwenden!
	public void updateLeistung(int id_strecke, double zeit, String bezeichnung, String datum);

	public double getGeschwindigkeit();
	public String getZeitString();
	public double getZeit();
	public Object[] getObjectDataForTable();
	
	// bei setZeit wird die Geschwindigkeit automatisch berechnet und gesetzt & vice versa
	public void setZeitFromString(String zeit);
	public void setZeitAndGeschwindigkeit(double zeit);
	public void setGeschwindigkeitAndGeschwindigkeit(double geschwindigkeit);

}
