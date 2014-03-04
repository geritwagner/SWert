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
	
	// TODO: wir sollten eine Methode fürs aktualisieren der Leistung ("Leistung bearbeiten") anbieten: 
	// Konsistent alle relevanten Attribute abfragen und über die Leistungs-ID identifizieren!

	public double getGeschwindigkeit();
	public String getZeitString();
	public double getZeit();
	
	
	// bei setZeit wird die Geschwindigkeit automatisch berechnet und gesetzt & vice versa
	public void setZeitFromString(String zeit);
	public void setZeitAndGeschwindigkeit(double zeit);
	public void setGeschwindigkeitAndGeschwindigkeit(double geschwindigkeit);

}
