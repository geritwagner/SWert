package globale_helper;

import java.util.Comparator;

/**
 * @author Honors-WInfo-Projekt (Fabian Böhm, Alexander Puchta), Gerit Wagner
 */

public class IntegerComparator implements Comparator<Object> {
    @Override
	public int compare(Object o1, Object o2) {
        Integer int1 = (Integer)o1;
        Integer int2 = (Integer)o2;
        return int1.compareTo(int2);
    }

    @Override
	public boolean equals(Object o2) {
        return this.equals(o2);
    }
}