//  OverallConstraintViolationComparator.java
//
//  Author:
//       Antonio J. Nebro <antonio@lcc.uma.es>
//       Juan J. Durillo <durillo@lcc.uma.es>
//
//  Copyright (c) 2011 Antonio J. Nebro, Juan J. Durillo
//
//  This program is free software: you can redistribute it and/or modify
//  it under the terms of the GNU Lesser General Public License as published by
//  the Free Software Foundation, either version 3 of the License, or
//  (at your option) any later version.
//
//  This program is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU Lesser General Public License for more details.
// 
//  You should have received a copy of the GNU Lesser General Public License
//  along with this program.  If not, see <http://www.gnu.org/licenses/>.

package jmetal.util.comparators.cloudcdn;

import java.util.Comparator;

import jmetal.core.Solution;
import jmetal.util.comparators.IConstraintViolationComparator;
import jmetal.util.comparators.ObjectiveComparator;

/**
 * This class implements a <code>Comparator</code> (a method for comparing
 * <code>Solution</code> objects) based on the overall constraint violation of
 * the solucions, as in NSGA-II.
 */
public class OverallConstraintViolationComparator implements
		IConstraintViolationComparator {

	ObjectiveComparator comparator_;

	/**
	 * Constructor.
	 *
	 * @param nObj
	 *            The index of the objective to compare
	 */
	public OverallConstraintViolationComparator(int nObj) {
		comparator_ = new ObjectiveComparator(nObj);
	}

	public OverallConstraintViolationComparator(int nObj,
			boolean descendingOrder) {
		comparator_ = new ObjectiveComparator(nObj, descendingOrder);
	}

	/**
	 * Compares two solutions.
	 * 
	 * @param o1
	 *            Object representing the first <code>Solution</code>.
	 * @param o2
	 *            Object representing the second <code>Solution</code>.
	 * @return -1, or 0, or 1 if o1 is less than, equal, or greater than o2,
	 *         respectively.
	 */
	public int compare(Object o1, Object o2) {
		double overall1, overall2;
		overall1 = ((Solution) o1).getOverallConstraintViolation();
		overall2 = ((Solution) o2).getOverallConstraintViolation();

		int comp = Double.compare(overall1, overall2);
		if (comp == 0) {
			return comparator_.compare(o1, o2);
		} else {
			return comp;
		}
	} // compare

	public boolean needToCompare(Solution s1, Solution s2) {
		return true;
	}
}
