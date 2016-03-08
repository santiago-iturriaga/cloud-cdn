/**
 * 
 */
package jmetal.problems.cloudcdn.greedy.routing;

import java.util.ArrayList;

import jmetal.core.Solution;
import jmetal.problems.cloudcdn.CloudCDN_RRSO;
import jmetal.problems.cloudcdn.RegionDatacenter;
import jmetal.problems.cloudcdn.Trafico;
import jmetal.util.JMException;
import jmetal.encodings.solutionType.cloudcdn.CloudCDNSolutionType;

public class RRCheapest extends RRRoutingAlgorithm {
	private ArrayList<RegionDatacenter> priorityList;

	protected double[][] maxBandwidthConstraint_;
	
	public RRCheapest(CloudCDN_RRSO problem) {
		super(problem);

		priorityList = new ArrayList<RegionDatacenter>(
				problem.getRegionesDatacenters());
		priorityList.sort(new CheapestComparator());
		
		maxBandwidthConstraint_ = new double[problem_.getRegionesDatacenters()
		                  				.size()][totalBandwidthSlots_];
	}

	public void Compute(Solution solution, int startTime, int endTime) {
		try {
			ResetState(solution);

			for (int i = 0; i < problem_.getRegionesDatacenters().size(); i++) {
				for (int j = 0; j < totalBandwidthSlots_; j++) {
					maxBandwidthConstraint_[i][j] = 0;
				}
			}
			
			int arrival = 0, day = 0, minuteOfDay = 0;
			int current_day = 0;
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
						best_dc_violations = Integer.MAX_VALUE;

						int numSlots = (int) Math.ceil(t.getDocSize()
								/ videoSlotSize);

						for (int offset = 0; offset < problem_
								.getRegionesDatacenters().size() && !assigned; offset++) {

							int j;
							j = priorityList.get(offset).getRegDctId();

							if (CloudCDNSolutionType.GetDocumentVariables(
									solution, j).getValue(t.getDocId()) == 1) {
								// The current DC has a copy of the required
								// document.

								int violatedSlots = 0;

								if (problem_.getQoS(
												t.getRegUsrId(), j)
												.getQosMetric() > problem_
												.getRegionesUsuarios()
												.get(t.getRegUsrId())
												.getQoSThreshold()) {

									violatedSlots += numSlots;
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

						if (!assigned) {
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
						}
					} else {
						current_day = day;

						for (int m = 0; m < problem_.getRegionesDatacenters()
								.size(); m++) {

							for (int n = 0; n < totalBandwidthSlots_; n++) {
								if (bandwidthConstraint_[m][n] > maxBandwidthConstraint_[m][n]) {
									maxBandwidthConstraint_[m][n] = bandwidthConstraint_[m][n];
								}
								
								bandwidthConstraint_[m][n] = 0;
							}
						}
					}
				}
			}

			for (int m = 0; m < problem_.getRegionesDatacenters().size(); m++) {

				for (int n = 0; n < totalBandwidthSlots_; n++) {
					if (bandwidthConstraint_[m][n] > maxBandwidthConstraint_[m][n]) {
						maxBandwidthConstraint_[m][n] = bandwidthConstraint_[m][n];
					}

					bandwidthConstraint_[m][n] = 0;
				}
			}
		} catch (JMException e) {
			e.printStackTrace();
		}
	}

	@Override
	public double getMachineCost() {
		double machineCost = 0.0;
		
		//System.out.println(">> VM >>");
		
		// Calculo la cantidad de VM necesarias en cada hora para poder
		// cumplir con los máximos de tráfico por minuto.
		for (int m = 0; m < problem_.getRegionesDatacenters().size(); m++) {
			for (int h = 0; h < 23; h++) {
				int min_start, min_end;
				min_start = h * 60;
				min_end = (h + 1) * 60 - 1;

				double hourlyMax;
				hourlyMax = 0;

				for (int n = min_start; n < min_end; n++) {
					if (maxBandwidthConstraint_[m][n] > hourlyMax) {
						hourlyMax = maxBandwidthConstraint_[m][n];
					}
				}

				double remaining_bandwidth;
				remaining_bandwidth = hourlyMax;

				int numVMs[] = new int[problem_.getMaquinas().size()];
				for (int n = problem_.getMaquinas().size() - 1; n >= 0; n--) {
					numVMs[n] += remaining_bandwidth
							/ problem_.getMaquinas().get(n)
									.getBandwidthGBpm();
					remaining_bandwidth = remaining_bandwidth - numVMs[n];
				}

				if (remaining_bandwidth > 0) {
					numVMs[0]++;
				}

				double priceVM;
				for (int n = 0; n < problem_.getMaquinas().size(); n++) {
					priceVM = problem_.getRegionesDatacenters().get(m)
							.computeVMCost(n);

					if (numVMs[n] > 0) {
						machineCost += numVMs[n] * priceVM;
					}
				}
			}
			
			//System.out.println("DC " + i + " = " + total_horas + " hours");
		}
		
		return machineCost;
	}
}
