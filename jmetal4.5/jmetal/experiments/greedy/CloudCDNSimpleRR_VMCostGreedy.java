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

package jmetal.experiments.greedy;

import jmetal.core.Problem;
import jmetal.core.Solution;
import jmetal.core.Variable;
import jmetal.encodings.solutionType.cloudcdn.CloudCDNSolutionType;
import jmetal.problems.cloudcdn.CloudCDN_SO;
import jmetal.problems.cloudcdn.RegionDatacenter;
import jmetal.problems.cloudcdn.Trafico;
import jmetal.problems.cloudcdn.greedy.routing.CheapestComparator;
import jmetal.util.JMException;
import jmetal.util.PseudoRandom;

import java.io.IOException;
import java.util.ArrayList;

public class CloudCDNSimpleRR_VMCostGreedy {

	private boolean PRIORIZE_DOC_ALLOC = false;

	public Solution BuildSolution(Problem p) throws ClassNotFoundException {
		CloudCDN_SO problem_ = (CloudCDN_SO) p;

		try {
			// *********************************************************
			// Creo una solución totalmente vacía.
			// *********************************************************
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

			// *********************************************************
			// Comienzo con la heurística de alto nivel.
			// *********************************************************

			// Inicializo variables y estructuras.
			int totalBandwidthSlots_ = 24 * 60;
			double videoSlotSize = 320.0 / 8.0 * 60.0 / (1024.0 * 1024.0); // GB
																			// per
																			// minute
																			// required

			int[] totalRequests_;
			double[] totalTrafficAmount_;
			double[][] bandwidthConstraint_;
			double[][] maxBandwidth_;

			bandwidthConstraint_ = new double[problem_.getRegionesDatacenters()
					.size()][totalBandwidthSlots_];

			maxBandwidth_ = new double[problem_.getRegionesDatacenters().size()][totalBandwidthSlots_];

			totalRequests_ = new int[problem_.getRegionesDatacenters().size()];
			totalTrafficAmount_ = new double[problem_.getRegionesDatacenters()
					.size()];

			for (int i = 0; i < problem_.getRegionesDatacenters().size(); i++) {
				totalRequests_[i] = 0;
				totalTrafficAmount_[i] = 0;

				for (int j = 0; j < totalBandwidthSlots_; j++) {
					bandwidthConstraint_[i][j] = 0;
					maxBandwidth_[i][j] = 0;
				}
			}

			ArrayList<RegionDatacenter> priorityList;
			priorityList = new ArrayList<RegionDatacenter>(
					problem_.getRegionesDatacenters());
			priorityList
					.sort(new CloudCDNSimpleRR_VMCostGreedyComparator(false));

			int arrival = 0;
			int day = 0;
			int minuteOfDay = 0;
			int current_day = 0;

			current_day = problem_.getTrafico().get(0).getReqTime()
					/ (24 * 60 * 60);

			// Loop principal. Itero entre todos los pedidos de videos.
			for (int i = 0; (i < problem_.getTrafico().size())
					&& (problem_.getTrafico().get(i).getReqTime() <= problem_
							.EndTrainingSecs()); i++) {

				arrival = problem_.getTrafico().get(i).getReqTime();
				day = arrival / (24 * 60 * 60);
				minuteOfDay = (arrival / 60) % (24 * 60);

				Trafico t;
				t = problem_.getTrafico().get(i);

				if (day == current_day) {
					int numSlots;
					numSlots = (int) Math.ceil(t.getDocSize() / videoSlotSize);

					int docId;
					docId = t.getDocId();

					int targetDC;
					targetDC = -1;

					// Busco el primer datacenter que tenga el documento pedido
					// y que cumpla los requisitos de QoS.
					for (int index = 0; index < priorityList.size()
							&& targetDC == -1; index++) {
						int candidateDC;
						candidateDC = priorityList.get(index).getRegDctId();

						if (problem_.getQoS(t.getRegUsrId(), candidateDC)
								.getQosMetric() <= problem_
								.getRegionesUsuarios().get(t.getRegUsrId())
								.getQoSThreshold()) {

							if (PRIORIZE_DOC_ALLOC) {
								if (CloudCDNSolutionType.GetDocumentVariables(
										current, candidateDC).getValue(docId) == 1) {

									targetDC = candidateDC;
								}
							} else {
								CloudCDNSolutionType.GetDocumentVariables(
										current, candidateDC)
										.setValue(docId, 1);

								targetDC = candidateDC;
							}
						}
					}

					// Si no encontré ningún datacenter en el loop anterior,
					// ubico el documento en el datacenter más barato que que cumpla el QoS
					if (targetDC == -1) {
						for (int index = 0; index < priorityList.size()
								&& targetDC == -1; index++) {

							int candidateDC;
							candidateDC = priorityList.get(index).getRegDctId();

							double qos;
							qos = problem_.getQoS(t.getRegUsrId(), candidateDC).getQosMetric();
							
							double threshold;
							threshold = problem_.getRegionesUsuarios().get(t.getRegUsrId()).getQoSThreshold();
							
							if (qos <= threshold) {
								targetDC = candidateDC;
								
								CloudCDNSolutionType.GetDocumentVariables(current,
										candidateDC).setValue(docId, 1);
							}
						}
					}

					// Mando el pedido al datacenter seleccionado y acumulo el
					// ancho de banda.
					totalRequests_[targetDC]++;
					totalTrafficAmount_[targetDC] += problem_.getTrafico()
							.get(i).getDocSize();

					for (int l = 0; l < numSlots; l++) {
						bandwidthConstraint_[targetDC][(minuteOfDay + l)
								% (24 * 60)] += videoSlotSize;
					}
				} else {
					// Terminó un día entero de tráfico.
					current_day = day;

					// Actualizo los máximos de tráfico por minuto.
					for (int m = 0; m < problem_.getRegionesDatacenters()
							.size(); m++) {
						for (int n = 0; n < totalBandwidthSlots_; n++) {
							if (maxBandwidth_[m][n] < bandwidthConstraint_[m][n]) {
								maxBandwidth_[m][n] = bandwidthConstraint_[m][n];
							}

							bandwidthConstraint_[m][n] = 0;
						}
					}
				}
			}

			// Terminé de iterar. Actualizo los máximos de tráfico por minuto
			// del remanente de pedidos.
			for (int m = 0; m < problem_.getRegionesDatacenters().size(); m++) {
				for (int n = 0; n < totalBandwidthSlots_; n++) {
					if (maxBandwidth_[m][n] < bandwidthConstraint_[m][n]) {
						maxBandwidth_[m][n] = bandwidthConstraint_[m][n];
					}

					bandwidthConstraint_[m][n] = 0;
				}
			}

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
						if (maxBandwidth_[m][n] > hourlyMax) {
							hourlyMax = maxBandwidth_[m][n];
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

					for (int n = 0; n < problem_.getMaquinas().size(); n++) {
						CloudCDNSolutionType.GetVMVariables(current, m, h)
								.setValue(n, numVMs[n]);
					}
				}
			}

			return current;
		} catch (JMException e) {
			e.printStackTrace();
			return new Solution(problem_);
		}
	}

	public static void main(String[] args) throws JMException, IOException,
			ClassNotFoundException {

		CloudCDN_SO problem_ = new CloudCDN_SO("CloudCDNSolutionType", "test/",
				0, "SimpleRR", false);

		Solution current = (new CloudCDNSimpleRR_VMCostGreedy())
				.BuildSolution(problem_);

		problem_.evaluate(current);
		System.out.println("[0] Expected fitness: " + current.getObjective(0));
		System.out.println("[0] Expected overall constraint violation: "
				+ current.getOverallConstraintViolation());

		problem_.evaluateFinalSolution(current);
		System.out.println("[1] Actual fitness: " + current.getObjective(0));
		System.out.println("[1] Actual overall constraint violation: "
				+ current.getOverallConstraintViolation());
	}
}
