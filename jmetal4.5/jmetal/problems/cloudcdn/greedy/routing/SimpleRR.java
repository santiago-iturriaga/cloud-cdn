/**
 * 
 */
package jmetal.problems.cloudcdn.greedy.routing;

import jmetal.core.Solution;
import jmetal.core.Variable;
import jmetal.problems.cloudcdn.CloudCDN_SO;
import jmetal.problems.cloudcdn.Trafico;
import jmetal.util.JMException;
import jmetal.encodings.variable.ArrayInt;

public class SimpleRR {
	private CloudCDN_SO problem_;

	private double[] totalTrafficAmount_;
	private double[][] bandwidthConstraint_;

	// Max. number of bytes per minute per datacenter.
	private double[] maxGBPerMin_;

	// Minute precision.
	int totalBandwidthSlots_ = 24 * 60;

	private double maxViolatedBandwidth_;
	private double totalViolatedBandwidth_;

	public SimpleRR(CloudCDN_SO problem) {
		problem_ = problem;

		totalTrafficAmount_ = new double[problem_.getRegionesDatacenters()
				.size()];
		bandwidthConstraint_ = new double[problem_.getRegionesDatacenters()
				.size()][totalBandwidthSlots_];
		maxGBPerMin_ = new double[problem_.getRegionesDatacenters().size()];
	}

	public void Compute(Solution solution) {
		try {
			Variable[] vars = solution.getDecisionVariables();

			maxViolatedBandwidth_ = 0.0;
			totalViolatedBandwidth_ = 0.0;

			for (int i = 0; i < problem_.getRegionesDatacenters().size(); i++) {
				totalTrafficAmount_[i] = 0;
				maxGBPerMin_[i] = 0;

				for (int j = 0; j < totalBandwidthSlots_; j++) {
					bandwidthConstraint_[i][j] = 0;
				}

				double machGBPerMin;
				int machCount;
				for (int j = 0; j < problem_.getMaquinas().size(); j++) {
					machGBPerMin = problem_.getMaquinas().get(j)
							.getBandwidthGBpm();
					machCount = ((ArrayInt) vars[i
							+ problem_.getRegionesDatacenters().size()])
							.getValue(j);
					maxGBPerMin_[i] += machCount * machGBPerMin;
				}
			}

			int j = 0;
			int arrival = 0, day = 0, minuteOfDay = 0;
			int current_day = 0;
			int bestDC;
			int kIdx;

			Boolean assigned;
			Trafico t;

			current_day = problem_.getTrafico().get(0).getReqTime()
					/ (24 * 60 * 60);

			for (int i = 0; i < problem_.getTrafico().size(); i++) {
				arrival = problem_.getTrafico().get(i).getReqTime();
				day = arrival / (24 * 60 * 60);
				minuteOfDay = (arrival / 60) % (24 * 60);

				t = problem_.getTrafico().get(i);

				if (day == current_day) {
					assigned = false;
					kIdx = j;
					bestDC = j;

					for (int k = j; k < j
							+ problem_.getRegionesDatacenters().size(); k++) {

						if (((ArrayInt) vars[j]).getValue(t.getDocId()) == 1) {
							// The current DC has a copy of the required
							// document.

							if (bandwidthConstraint_[j][minuteOfDay]
									+ t.getDocSize() < maxGBPerMin_[j]) {
								// The current DC has enough bandwidth to
								// satisfy the request.

								totalTrafficAmount_[j] += problem_.getTrafico()
										.get(i).getDocSize();
								bandwidthConstraint_[j][minuteOfDay] += bandwidthConstraint_[j][minuteOfDay]
										+ problem_.getTrafico().get(i)
												.getDocSize();

								assigned = true;
							} else {
								if (bandwidthConstraint_[j][minuteOfDay] < bandwidthConstraint_[bestDC][minuteOfDay]) {
									bestDC = j;
								}

								kIdx = (kIdx + 1)
										% problem_.getRegionesDatacenters()
												.size();
							}
						} else {
							kIdx = (kIdx + 1)
									% problem_.getRegionesDatacenters().size();
						}
					}

					if (assigned) {
						j = (kIdx + 1)
								% problem_.getRegionesDatacenters().size();
					} else {
						totalTrafficAmount_[bestDC] += problem_.getTrafico()
								.get(i).getDocSize();
						bandwidthConstraint_[bestDC][minuteOfDay] += bandwidthConstraint_[bestDC][minuteOfDay]
								+ problem_.getTrafico().get(i).getDocSize();

						j = (bestDC + 1)
								% problem_.getRegionesDatacenters().size();
					}
				} else {
					current_day = day;

					for (int m = 0; m < problem_.getRegionesDatacenters()
							.size(); m++) {

						for (int n = 0; n < totalBandwidthSlots_; n++) {
							double diff;
							diff = bandwidthConstraint_[m][n] - maxGBPerMin_[j];

							if (diff > 0) {
								if (diff > maxViolatedBandwidth_) {
									maxViolatedBandwidth_ = diff;
								}
								totalViolatedBandwidth_ += diff;
							}

							bandwidthConstraint_[m][n] = 0;
						}
					}
				}
			}
		} catch (JMException e) {
			e.printStackTrace();

			totalViolatedBandwidth_ = Double.MAX_VALUE;
			maxViolatedBandwidth_ = Double.MAX_VALUE;
		}
	}

	public double getTotalViolatedBandwidth() {
		return totalViolatedBandwidth_;
	}

	public double getMaxViolatedBandwidth() {
		return maxViolatedBandwidth_;
	}

	public double[] getTrafficAmount() {
		return totalTrafficAmount_;
	}
}
