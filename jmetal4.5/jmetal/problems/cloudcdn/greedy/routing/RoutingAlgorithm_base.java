package jmetal.problems.cloudcdn.greedy.routing;

import jmetal.core.Solution;
import jmetal.problems.cloudcdn.CloudCDN_base;
import jmetal.util.JMException;

public abstract class RoutingAlgorithm_base {
	protected CloudCDN_base problem_;

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
	
	protected int numberOfQoSViolatedRequests_;

	public RoutingAlgorithm_base(CloudCDN_base problem) {
		problem_ = problem;

		totalRequests_ = new int[problem_.getRegionesDatacenters().size()];
		totalTrafficAmount_ = new double[problem_.getRegionesDatacenters()
				.size()];
		bandwidthConstraint_ = new double[problem_.getRegionesDatacenters()
				.size()][totalBandwidthSlots_];
	}

	public void ResetState(Solution solution) throws JMException {
		countRequests_ = 0;
		totalQos_ = 0.0;
		violatedQos_ = 0.0;
		numberOfQoSViolatedRequests_ = 0;

		for (int i = 0; i < problem_.getRegionesDatacenters().size(); i++) {
			totalRequests_[i] = 0;
			totalTrafficAmount_[i] = 0;

			for (int j = 0; j < totalBandwidthSlots_; j++) {
				bandwidthConstraint_[i][j] = 0;
			}
		}
	}
	
	public abstract void Compute(Solution solution, int startTime, int endTime);

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
