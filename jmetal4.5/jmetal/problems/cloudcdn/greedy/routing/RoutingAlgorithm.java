package jmetal.problems.cloudcdn.greedy.routing;

import jmetal.core.Solution;
import jmetal.encodings.solutionType.cloudcdn.CloudCDNSolutionType;
import jmetal.problems.cloudcdn.CloudCDN_SO;
import jmetal.problems.cloudcdn.CloudCDN_base;
import jmetal.util.JMException;

public abstract class RoutingAlgorithm extends RoutingAlgorithm_base {
	protected CloudCDN_base problem_;
	
	protected double maxViolatedBandwidth_;
	protected double totalViolatedBandwidth_;
	protected int numberOfBandwidthViolatedRequests_;

	// Max. number of bytes per minute per datacenter.
	protected double[] maxGBPerMin_;

	public RoutingAlgorithm(CloudCDN_base problem) {
		super(problem);
		maxGBPerMin_ = new double[problem_.getRegionesDatacenters().size()];
	}

	public void ResetState(Solution solution) throws JMException {
		maxViolatedBandwidth_ = 0.0;
		totalViolatedBandwidth_ = 0.0;
		numberOfBandwidthViolatedRequests_ = 0;
		
		for (int i = 0; i < problem_.getRegionesDatacenters().size(); i++) {
			maxGBPerMin_[i] = 0;

			double machGBPerMin;
			int machCount;
			for (int h = 0; h < 24; h++) {
				for (int j = 0; j < problem_.getMaquinas().size(); j++) {
					machGBPerMin = problem_.getMaquinas().get(j)
							.getBandwidthGBpm();
					machCount = CloudCDNSolutionType.GetVMVariables(
							solution, i, h).getValue(j);
					maxGBPerMin_[i] += machCount * machGBPerMin;
				}
			}
		}
	}
	
	public double getTotalViolatedBandwidth() {
		return totalViolatedBandwidth_;
	}

	public double getMaxViolatedBandwidth() {
		return maxViolatedBandwidth_;
	}

	public int getNumberOfBandwidthViolatedRequests() {
		return numberOfBandwidthViolatedRequests_;
	}
}
