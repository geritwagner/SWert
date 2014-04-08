package globale_helper;

import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParsePosition;

public class SpeedFormat extends NumberFormat{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public StringBuffer format(double number, StringBuffer toAppendTo,
			FieldPosition pos) {
		LeistungHelper leistungHelper = new LeistungHelper();
		UnitsHelper unitsHelper = new UnitsHelper();
		double time = unitsHelper.toSKm(number);
		String zeitString = leistungHelper.parseSecInMinutenstring(time);
		return new StringBuffer(zeitString);
	}

	@Override
	public StringBuffer format(long number, StringBuffer toAppendTo,
			FieldPosition pos) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Number parse(String source, ParsePosition parsePosition) {
		// TODO Auto-generated method stub
		return null;
	}

}