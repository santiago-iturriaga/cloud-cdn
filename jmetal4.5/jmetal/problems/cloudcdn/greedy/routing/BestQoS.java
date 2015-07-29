/**
 * 
 */
package jmetal.problems.cloudcdn.greedy.routing;

import java.util.ArrayList;
import jmetal.core.Solution;
import jmetal.problems.cloudcdn.CloudCDN_SO;
import jmetal.problems.cloudcdn.RegionDatacenter;
import jmetal.problems.cloudcdn.Trafico;
import jmetal.util.JMException;
import jmetal.encodings.solutionType.cloudcdn.CloudCDNSolutionType;

public class BestQoS extends RoutingAlgorithm {
	private ArrayList<ArrayList<RegionDatacenter>> priorityList;

	public BestQoS(CloudCDN_SO problem) {
		super(problem);

		priorityList = new ArrayList<ArrayList<RegionDatacenter>>();

		for (int i = 0; i < problem.getRegionesUsuarios().size(); i++) {
			ArrayList<RegionDatacenter> current = new ArrayList<RegionDatacenter>(
					problem.getRegionesDatacenters());

			current.sort(new BestQoSComparator(problem.getRegionesUsuarios()
					.get(i).getRegUsrId(), problem.getQoS()));

			priorityList.add(current);
		}
	}

	public void Compute(Solution solution, int startTime, int endTime) {
		try {
			ResetState(solution);

			int arrival = 0, day = 0, minuteOfDay = 0;
			int current_day = 0;
			int best_dc;

			Boolean assigned;
			Trafico t;

			current_day = problem_.getTrafico().get(0).getReqTime()
					/ (24 * 60 * 60);

			for (int i = 0; (i < problem_.getTrafico().size())
					&& (problem_.getTrafico().get(i).getReqTime() <= endTime); i++) {

				arrival = problem_.getTrafico().get(i).getReqTime();

				if (arrival >= startTime) {
					countRequests_++;

					day = arrival / (24 * 60 * 60);
					minuteOfDay = (arrival / 60) % (24 * 60);

					t = problem_.getTrafico().get(i);

					if (day == current_day) {
						assigned = false;
						best_dc = 0;

						for (int offset = 0; offset < problem_
								.getRegionesDatacenters().size() && !assigned; offset++) {

							int j;
							j = priorityList.get(t.getRegUsrId()).get(offset).getRegDctId();

							if (CloudCDNSolutionType.GetDocumentVariables(
									solution, j).getValue(t.getDocId()) == 1) {
								// The current DC has a copy of the required
								// document.

								if ((bandwidthConstraint_[j][minuteOfDay]
										+ t.getDocSize() < maxGBPerMin_[j])
										&& (problem_.getQoS(t.getRegUsrId(), j)
												.getQosMetric() <= problem_
												.getRegionesUsuarios()
												.get(t.getRegUsrId())
												.getQoSThreshold())) {
									// The current DC has enough bandwidth to
									// satisfy the request, and satisfies the
									// QoS

									totalRequests_[j]++;

									totalQos_ += problem_.getRegionesUsuarios()
											.get(t.getRegUsrId())
											.getQoSThreshold();

									totalTrafficAmount_[j] += problem_
											.getTrafico().get(i).getDocSize();

									bandwidthConstraint_[j][minuteOfDay] += bandwidthConstraint_[j][minuteOfDay]
											+ problem_.getTrafico().get(i)
													.getDocSize();

									assigned = true;
								} else {
									if ((bandwidthConstraint_[j][minuteOfDay] < bandwidthConstraint_[best_dc][minuteOfDay])
											&& (problem_.getQoS(
													t.getRegUsrId(), j)
													.getQosMetric() <= problem_
													.getRegionesUsuarios()
													.get(t.getRegUsrId())
													.getQoSThreshold())) {

										best_dc = j;
									}
								}
							}
						}

						if (!assigned) {
							totalRequests_[best_dc]++;

							totalTrafficAmount_[best_dc] += problem_
									.getTrafico().get(i).getDocSize();

							bandwidthConstraint_[best_dc][minuteOfDay] += bandwidthConstraint_[best_dc][minuteOfDay]
									+ problem_.getTrafico().get(i).getDocSize();

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
	}
}
