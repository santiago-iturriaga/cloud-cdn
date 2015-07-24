package jmetal.problems.cloudcdn;

import java.util.Comparator;

public class TraficoComparator implements Comparator<Trafico> {
	@Override
	public int compare(Trafico arg0, Trafico arg1) {
		if (arg0.reqTime < arg1.reqTime) {
			return -1;
		} else if (arg0.reqTime > arg1.reqTime) {
			return 1;
		} else {
			return 0;
		}
	}
}
