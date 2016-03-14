/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jmetal.problems.cloudcdn.f201603;

import java.util.Comparator;

/**
 *
 * @author santiago
 */
public class RegionDatacenterNetworkCheapestComparator implements Comparator<RegionDatacenter> {
	@Override
	public int compare(RegionDatacenter arg0, RegionDatacenter arg1) {
		if (arg0.transferPrice < arg1.transferPrice) {
			return -1;
		} else if (arg0.transferPrice > arg1.transferPrice) {
			return 1;
		} else {
			return 0;
		}
	}
}