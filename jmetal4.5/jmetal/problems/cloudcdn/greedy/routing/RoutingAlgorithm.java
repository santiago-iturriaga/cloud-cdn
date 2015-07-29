package jmetal.problems.cloudcdn.greedy.routing;

import jmetal.core.Solution;
import jmetal.encodings.solutionType.cloudcdn.CloudCDNSolutionType;
import jmetal.problems.cloudcdn.CloudCDN_SO;
import jmetal.util.JMException;

public abstract class RoutingAlgorithm {
	protected CloudCDN_SO problem_;

	protected int countRequests_;
	protected int[] totalRequests_;
	protected double[] totalTrafficAmount_;
	protected double[][] bandwidthConstraint_;

	protected double totalQos_;
	protected double violatedQos_;

	// Max. number of bytes per minute per datacenter.
	protected double[] maxGBPerMin_;

	// Minute precision.
	protected int totalBandwidthSlots_ = 24 * 60;

	protected double maxViolatedBandwidth_;
	protected double totalViolatedBandwidth_;

	protected int numberOfBandwidthViolatedRequests_;
	protected int numberOfQoSViolatedRequests_;

	public RoutingAlgorithm(CloudCDN_SO problem) {
		problem_ = problem;

		totalRequests_ = new int[problem_.getRegionesDatacenters().size()];
		totalTrafficAmount_ = new double[problem_.getRegionesDatacenters()
				.size()];
		bandwidthConstraint_ = new double[problem_.getRegionesDatacenters()
				.size()][totalBandwidthSlots_];
		maxGBPerMin_ = new double[problem_.getRegionesDatacenters().size()];
	}

	public void ResetState(Solution solution) throws JMException {
		countRequests_ = 0;
		maxViolatedBandwidth_ = 0.0;
		totalViolatedBandwidth_ = 0.0;
		totalQos_ = 0.0;
		violatedQos_ = 0.0;
		numberOfBandwidthViolatedRequests_ = 0;
		numberOfQoSViolatedRequests_ = 0;

		for (int i = 0; i < problem_.getRegionesDatacenters().size(); i++) {
			totalRequests_[i] = 0;
			totalTrafficAmount_[i] = 0;
			maxGBPerMin_[i] = 0;

			for (int j = 0; j < totalBandwidthSlots_; j++) {
				bandwidthConstraint_[i][j] = 0;
			}

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
	
	public abstract void Compute(Solution solution, int startTime, int endTime);

	public double getTotalViolatedBandwidth() {
		return totalViolatedBandwidth_;
	}

	public double getMaxViolatedBandwidth() {
		return maxViolatedBandwidth_;
	}

	public int getNumberOfBandwidthViolatedRequests() {
		return numberOfBandwidthViolatedRequests_;
	}

	public int getNumberOfQoSViolatedRequests() {
		return numberOfQoSViolatedRequests_;
	}

	public double[] getTrafficAmount() {
		return totalTrafficAmount_;
	}

	public int[] getTotalRequests() {
		return totalRequests_;
	}

	public double getViolatedQoS() {
		return violatedQos_;
	}

	public double getTotalQoS() {
		return totalQos_;
	}

	public double getRatioQoS() {
		return (double) (countRequests_ - getNumberOfQoSViolatedRequests())
				/ (double) countRequests_;
	}
}
