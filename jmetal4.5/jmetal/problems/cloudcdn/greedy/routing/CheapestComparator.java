package jmetal.problems.cloudcdn.greedy.routing;

import java.util.Comparator;

import jmetal.problems.cloudcdn.RegionDatacenter;

public class CheapestComparator implements Comparator<RegionDatacenter> {
	private double AvgPrice(RegionDatacenter dc) {
		double totalPrice = 0.0;
		int typeCount = 0;
		
		for (int i = 0; i < dc.getMaxVMType(); i++) {
			if (dc.hasVmType(i)) {
				totalPrice += dc.computeVMCost(i);
				typeCount++;
			}
		}

		return totalPrice / typeCount;
	}

	@Override
	public int compare(RegionDatacenter arg0, RegionDatacenter arg1) {
		return Double.compare(AvgPrice(arg0), AvgPrice(arg1));
	}
}
