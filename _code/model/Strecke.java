package model;

public class Strecke {

	private int strecken_id;
	private String bezeichnung;
	private int laenge;
	
	public Strecke (int strecken_id, String bezeichnung, int laenge) {
		this.setStrecken_id(strecken_id);
		this.setBezeichnung(bezeichnung);
		this.setLaenge(laenge);
	}
	
	public Strecke (int laenge) {
		this.setLaenge(laenge);
		this.setBezeichnung(laenge+"m");
	}

	/**
	 * @return the strecken_id
	 */
	public int getStrecken_id() {
		return strecken_id;
	}

	/**
	 * @param strecken_id the strecken_id to set
	 */
	public void setStrecken_id(int strecken_id) {
		this.strecken_id = strecken_id;
	}

	/**
	 * @return the bezeichnung
	 */
	public String getBezeichnung() {
		return bezeichnung;
	}

	/**
	 * @param bezeichnung the bezeichnung to set
	 */
	public void setBezeichnung(String bezeichnung) {
		this.bezeichnung = bezeichnung;
	}

	/**
	 * @return the laenge
	 */
	public int getLaenge() {
		return laenge;
	}

	/**
	 * @param laenge the laenge to set
	 */
	public void setLaenge(int laenge) {
		this.laenge = laenge;
	}
}
