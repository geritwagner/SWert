package model;

interface LeistungInterface {

	public long getId();
	public int getId_strecke();
	public void setId_strecke(int id_strecke);
	public long getId_athlet();
	public int getStrecke();
	public String getBezeichnung();
	public void setBezeichnung(String bezeichnung);
	public String getDatum();
	public void setDatum(String datum);
	public boolean isUsedForSlopeFaktor();
	public void setIsUsedForSlopeFaktor(boolean berechnungSlopeFaktor);
	public String toString();
	public boolean equals(Leistung andereLeistung);

	public double getGeschwindigkeit();
	public String getZeitString();
	public double getZeit();
	
	
	// bei setZeit wird die Geschwindigkeit automatisch berechnet und gesetzt & vice versa
	public void setZeitFromString(String zeit);
	public void setZeit(double zeit);
	public void setGeschwindigkeit(double geschwindigkeit);

}
