package jmetal.experiments.greedy;

import java.util.Comparator;

import jmetal.problems.cloudcdn.RegionDatacenter;
import jmetal.util.PseudoRandom;

public class CloudCDNSimpleRR_VMCostGreedyComparator implements
		Comparator<RegionDatacenter> {

	private boolean randomize_;

	public CloudCDNSimpleRR_VMCostGreedyComparator(boolean randomize) {
		randomize_ = randomize;
	}

	@Override
	public int compare(RegionDatacenter arg0, RegionDatacenter arg1) {
		int vmPrice;
		vmPrice = Double.compare(arg0.VMPricingValue(), arg1.VMPricingValue());

		if (vmPrice != 0) {
			return vmPrice;
		} else {
			int transferPrice;
			transferPrice = Double.compare(arg0.TransferPricingValue(),
					arg1.TransferPricingValue());

			if (transferPrice != 0) {
				return transferPrice;
			} else {
				int storagePrice;
				storagePrice = Double.compare(arg0.StoragePricingValue(),
						arg1.StoragePricingValue());

				if (storagePrice != 0) {
					return storagePrice;
				} else {
					if (randomize_) {
						int rand;
						rand = PseudoRandom.randInt(0, 1);

						if (rand == 0)
							return -1;
						else
							return 1;
					} else {
						return 1;
					}
				}
			}
		}
	}
}
