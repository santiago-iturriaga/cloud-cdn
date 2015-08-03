//  ConstrainedProblemsStudy.java
//
//  Authors:
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

package jmetal.experiments.studies;

import jmetal.core.Solution;
import jmetal.core.Variable;
import jmetal.encodings.solutionType.cloudcdn.CloudCDNSolutionType;
import jmetal.problems.cloudcdn.CloudCDN_SO;
import jmetal.problems.cloudcdn.Trafico;
import jmetal.util.JMException;

import java.io.IOException;

public class CloudCDNSimpleRRGreedy {

	public static void main(String[] args) throws JMException, IOException, ClassNotFoundException {
		CloudCDN_SO problem_ = new CloudCDN_SO("CloudCDNSolutionType", "test/",
				0, "SimpleRR");

		Variable[] vars = problem_.getSolutionType().createVariables();
		Solution current = new Solution(problem_, vars);

		for (int dc = 0; dc < problem_.getRegionesDatacenters().size(); dc++) {
			for (int doc = 0; doc < problem_.getDocumentos().size(); doc++) {
				CloudCDNSolutionType.GetDocumentVariables(current, dc)
						.setValue(doc, 0);
			}
		}

		for (int dc = 0; dc < problem_.getRegionesDatacenters().size(); dc++) {
			for (int t = 0; t < 24; t++) {
				for (int vm = 0; vm < problem_.getMaquinas().size(); vm++) {
					CloudCDNSolutionType.GetVMVariables(current, dc, t)
							.setValue(vm, 0);
				}
			}
		}
		/*
		try {
			int arrival = 0, day = 0, minuteOfDay = 0;
			int current_day = 0;
			int current_dc = 0;

			int best_dc;
			int best_dc_violations;

			double videoSlotSize = 320.0 / 8.0 * 60.0 / (1024.0 * 1024.0); // GB
																			// per
																			// minute
																			// required

			Boolean assigned;
			Trafico t;

			current_day = problem_.getTrafico().get(0).getReqTime()
					/ (24 * 60 * 60);

			for (int i = 0; (i < problem_.getTrafico().size())
					&& (problem_.getTrafico().get(i).getReqTime() <= problem_.TotalTrainingSecs()); i++) {

				arrival = problem_.getTrafico().get(i).getReqTime();
				day = arrival / (24 * 60 * 60);
				minuteOfDay = (arrival / 60) % (24 * 60);

				t = problem_.getTrafico().get(i);

				if (day == current_day) {
					assigned = false;
					best_dc = current_dc;
					best_dc_violations = Integer.MAX_VALUE;

					int numSlots = (int) Math.ceil(t.getDocSize()
							/ videoSlotSize);

					int offset;
					for (offset = 0; offset < problem_
							.getRegionesDatacenters().size() && !assigned; offset++) {
						int j;
						j = (current_dc + offset)
								% problem_.getRegionesDatacenters().size();

						if (CloudCDNSolutionType.GetDocumentVariables(
								solution, j).getValue(t.getDocId()) == 1) {
							// The current DC has a copy of the required
							// document.

							int violatedSlots = 0;

							for (int l = 0; l < numSlots; l++) {
								if ((bandwidthConstraint_[j][(minuteOfDay + l)
										% (24 * 60)]
										+ videoSlotSize > maxGBPerMin_[j])
										|| (problem_.getQoS(
												t.getRegUsrId(), j)
												.getQosMetric() > problem_
												.getRegionesUsuarios()
												.get(t.getRegUsrId())
												.getQoSThreshold())) {

									violatedSlots++;
								}
							}

							if (violatedSlots == 0) {
								// The current DC has enough bandwidth to
								// satisfy the request, and satisfies the
								// QoS
								totalRequests_[j]++;
								totalQos_ += problem_.getRegionesUsuarios()
										.get(t.getRegUsrId())
										.getQoSThreshold();
								totalTrafficAmount_[j] += problem_
										.getTrafico().get(i).getDocSize();

								for (int l = 0; l < numSlots; l++) {
									bandwidthConstraint_[j][(minuteOfDay + l)
											% (24 * 60)] += videoSlotSize;
								}

								assigned = true;
							} else {
								if (best_dc_violations > violatedSlots) {
									best_dc = j;
									best_dc_violations = violatedSlots;
								}
							}
						}
					}

					if (assigned) {
						current_dc = (current_dc + offset + 1)
								% problem_.getRegionesDatacenters().size();
					} else {
						totalRequests_[best_dc]++;

						totalTrafficAmount_[best_dc] += problem_
								.getTrafico().get(i).getDocSize();

						for (int l = 0; l < numSlots; l++) {
							bandwidthConstraint_[best_dc][(minuteOfDay + l)
									% (24 * 60)] += videoSlotSize;
						}

						totalQos_ += problem_.getRegionesUsuarios()
								.get(t.getRegUsrId()).getQoSThreshold();

						double diffqos;
						diffqos = problem_.getQoS(t.getRegUsrId(), best_dc)
								.getQosMetric()
								- problem_.getRegionesUsuarios()
										.get(t.getRegUsrId())
										.getQoSThreshold();

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
							diff = bandwidthConstraint_[m][n]
									- maxGBPerMin_[m];

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

			for (int m = 0; m < problem_.getRegionesDatacenters().size(); m++) {

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

		problem.evaluateFinalSolution(solution);*/
	}
}
