/**
 * 
 */
package jmetal.problems.cloudcdn.greedy.routing;

import jmetal.core.Solution;
import jmetal.core.Variable;
import jmetal.problems.cloudcdn.CloudCDN_SO;
import jmetal.problems.cloudcdn.Trafico;
import jmetal.util.JMException;
import jmetal.encodings.solutionType.cloudcdn.CloudCDNSolutionType;
import jmetal.encodings.variable.ArrayInt;

public class SimpleRR {
	private CloudCDN_SO problem_;

	private int[] totalRequests_;
	private double[] totalTrafficAmount_;
	private double[][] bandwidthConstraint_;

	private double totalQos_;
	private double violatedQos_;

	// Max. number of bytes per minute per datacenter.
	private double[] maxGBPerMin_;

	// Minute precision.
	int totalBandwidthSlots_ = 24 * 60;

	private double maxViolatedBandwidth_;
	private double totalViolatedBandwidth_;

	private int numberOfBandwidthViolatedRequests_;
	private int numberOfQoSViolatedRequests_;

	public SimpleRR(CloudCDN_SO problem) {
		problem_ = problem;

		totalRequests_ = new int[problem_.getRegionesDatacenters().size()];
		totalTrafficAmount_ = new double[problem_.getRegionesDatacenters()
				.size()];
		bandwidthConstraint_ = new double[problem_.getRegionesDatacenters()
				.size()][totalBandwidthSlots_];
		maxGBPerMin_ = new double[problem_.getRegionesDatacenters().size()];
	}

	public void Compute(Solution solution) {
		try {
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

			int arrival = 0, day = 0, minuteOfDay = 0;
			int current_day = 0;
			int current_dc = 0;
			int best_dc;

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
					best_dc = current_dc;

					int offset;
					for (offset = 0; offset < problem_.getRegionesDatacenters()
							.size() && !assigned; offset++) {
						int j;
						j = (current_dc + offset)
								% problem_.getRegionesDatacenters().size();

						if (CloudCDNSolutionType.GetDocumentVariables(solution,
								j).getValue(t.getDocId()) == 1) {
							// The current DC has a copy of the required
							// document.

							if ((bandwidthConstraint_[j][minuteOfDay]
									+ t.getDocSize() < maxGBPerMin_[j])
									&& (problem_.getQoS(t.getRegUsrId(), j).getQosMetric() <= problem_
											.getRegionesUsuarios()
											.get(t.getRegUsrId())
											.getQoSThreshold())) {
								// The current DC has enough bandwidth to
								// satisfy the request, and satisfies the QoS

								totalRequests_[j]++;

								totalQos_ += problem_.getRegionesUsuarios()
										.get(t.getRegUsrId()).getQoSThreshold();

								totalTrafficAmount_[j] += problem_.getTrafico()
										.get(i).getDocSize();

								bandwidthConstraint_[j][minuteOfDay] += bandwidthConstraint_[j][minuteOfDay]
										+ problem_.getTrafico().get(i)
												.getDocSize();

								assigned = true;
							} else {
								if ((bandwidthConstraint_[j][minuteOfDay] < bandwidthConstraint_[best_dc][minuteOfDay])
										&& (problem_.getQoS(t.getRegUsrId(), j).getQosMetric() <= problem_
												.getRegionesUsuarios()
												.get(t.getRegUsrId())
												.getQoSThreshold())) {

									best_dc = j;
								}
							}
						}
					}

					if (assigned) {
						current_dc = (current_dc + offset + 1)
								% problem_.getRegionesDatacenters().size();
					} else {
						totalRequests_[best_dc]++;

						totalTrafficAmount_[best_dc] += problem_.getTrafico()
								.get(i).getDocSize();

						bandwidthConstraint_[best_dc][minuteOfDay] += bandwidthConstraint_[best_dc][minuteOfDay]
								+ problem_.getTrafico().get(i).getDocSize();

						totalQos_ += problem_.getRegionesUsuarios()
								.get(t.getRegUsrId()).getQoSThreshold();

						double diffqos;
						diffqos = problem_.getQoS(t.getRegUsrId(), best_dc).getQosMetric()
								- problem_.getRegionesUsuarios()
										.get(t.getRegUsrId()).getQoSThreshold();

						if (diffqos > 0) {
							violatedQos_ += diffqos;
							numberOfQoSViolatedRequests_++;
						}

						current_dc = (best_dc + 1)
								% problem_.getRegionesDatacenters().size();
					}
				} else {
					current_day = day;

					for (int m = 0; m < problem_.getRegionesDatacenters()
							.size(); m++) {

						for (int n = 0; n < totalBandwidthSlots_; n++) {
							double diff;
							diff = bandwidthConstraint_[m][n] - maxGBPerMin_[m];

							if (diff > 0) {
								if (diff > maxViolatedBandwidth_) {
									maxViolatedBandwidth_ = diff;
								}
								totalViolatedBandwidth_ += diff;							
								numberOfBandwidthViolatedRequests_++;
							}

							bandwidthConstraint_[m][n] = 0;
						}
					}
				}
			}
			
			for (int m = 0; m < problem_.getRegionesDatacenters()
					.size(); m++) {

				for (int n = 0; n < totalBandwidthSlots_; n++) {
					double diff;
					diff = bandwidthConstraint_[m][n] - maxGBPerMin_[m];

					if (diff > 0) {
						if (diff > maxViolatedBandwidth_) {
							maxViolatedBandwidth_ = diff;
						}
						totalViolatedBandwidth_ += diff;							
						numberOfBandwidthViolatedRequests_++;
					}

					bandwidthConstraint_[m][n] = 0;
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
}
