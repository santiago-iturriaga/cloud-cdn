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

	private Boolean feasible_;

	// Max. number of bytes per minute per datacenter.
	private int[] maxBytesPerMin_;

	// Minute precision.
	int totalBandwidthSlots_ = 24 * 60;

	public SimpleRR(CloudCDN_SO problem) {
		problem_ = problem;

		totalTrafficAmount_ = new double[problem_
				.getCantidadRegionesDatacenters()];
		bandwidthConstraint_ = new double[problem_
				.getCantidadRegionesDatacenters()][totalBandwidthSlots_];
		maxBytesPerMin_ = new int[problem_.getCantidadRegionesDatacenters()];
	}

	public void Compute(Solution solution) {
		try {
			feasible_ = true;
			Variable[] vars = solution.getDecisionVariables();

			for (int i = 0; i < problem_.getCantidadRegionesDatacenters(); i++) {
				totalTrafficAmount_[i] = 0;
				maxBytesPerMin_[i] = 0;

				for (int j = 0; j < totalBandwidthSlots_; j++) {
					bandwidthConstraint_[i][j] = 0;
				}

				int machBytesPerMin, machCount;
				for (int j = 0; j < problem_.getCantidadMaquinas(); j++) {
					machBytesPerMin = problem_.getMaquinas()[j].getBandwidth() * 60;
					machCount = ((ArrayInt) vars[i
							+ problem_.getCantidadRegionesDatacenters()])
							.getValue(j);
					maxBytesPerMin_[i] += machCount * machBytesPerMin;
				}
			}

			int j = 0;
			int arrival = 0, day = 0, minuteOfDay = 0;
			int current_day = 0;
			int bestDC;
			int kIdx;
			Boolean assigned;
			Trafico t;

			for (int i = 0; i < problem_.getCantidadTrafico(); i++) {
				arrival = problem_.getTrafico()[i].getReqTime();
				day = arrival / (24 * 60 * 60);
				minuteOfDay = (arrival / 60) % (24 * 60);

				t = problem_.getTrafico()[i];

				if (day == current_day) {
					assigned = false;
					kIdx = j;
					bestDC = j;

					for (int k = j; k < j
							+ problem_.getCantidadRegionesDatacenters(); k++) {

						if (((ArrayInt) vars[j]).getValue(t.getDocId()) == 1) {
							// The current DC has a copy of the required
							// document.

							if (bandwidthConstraint_[j][minuteOfDay]
									+ t.getDocSize() < maxBytesPerMin_[j]) {
								// The current DC has enough bandwidth to
								// satisfy the request.

								totalTrafficAmount_[j] += problem_.getTrafico()[i]
										.getDocSize();
								bandwidthConstraint_[j][minuteOfDay] += bandwidthConstraint_[j][minuteOfDay]
										+ problem_.getTrafico()[i].getDocSize();

								assigned = true;
							} else {
								if (bandwidthConstraint_[j][minuteOfDay] < bandwidthConstraint_[bestDC][minuteOfDay]) {
									bestDC = j;
								}

								kIdx = (kIdx + 1)
										% problem_
												.getCantidadRegionesDatacenters();
							}
						} else {
							kIdx = (kIdx + 1)
									% problem_.getCantidadRegionesDatacenters();
						}
					}

					if (assigned) {
						j = kIdx;
					} else {
						totalTrafficAmount_[bestDC] += problem_.getTrafico()[i]
								.getDocSize();
						bandwidthConstraint_[bestDC][minuteOfDay] += bandwidthConstraint_[bestDC][minuteOfDay]
								+ problem_.getTrafico()[i].getDocSize();

						/*if (((ArrayInt) vars[bestDC]).getValue(t.getDocId()) == 0) {
							feasible_ = false;
							return;
						}*/
					}
				} else {
					current_day = day;

					// TODO: contabilizar deadlines violados.

					for (int m = 0; m < problem_
							.getCantidadRegionesDatacenters(); m++) {
						for (int n = 0; n < totalBandwidthSlots_; n++) {
							bandwidthConstraint_[m][n] = 0;
						}
					}
				}
			}
		} catch (JMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			feasible_ = false;
		}
	}

	public double[] getTrafficAmount() {
		return totalTrafficAmount_;
	}

	public Boolean isFeasible() {
		return feasible_;
	}
}
