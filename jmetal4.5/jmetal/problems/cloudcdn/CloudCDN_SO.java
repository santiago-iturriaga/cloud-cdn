package jmetal.problems.cloudcdn;

import jmetal.core.Solution;
import jmetal.encodings.solutionType.cloudcdn.CloudCDNSolutionType;
import jmetal.problems.cloudcdn.greedy.routing.BestQoS;
import jmetal.problems.cloudcdn.greedy.routing.Cheapest;
import jmetal.problems.cloudcdn.greedy.routing.RandomRouting;
import jmetal.problems.cloudcdn.greedy.routing.RoutingAlgorithm;
import jmetal.problems.cloudcdn.greedy.routing.SimpleRR;
import jmetal.util.JMException;

public class CloudCDN_SO extends CloudCDN_base {
	private static final long serialVersionUID = 8680593990260640094L;
	
	protected RoutingAlgorithm routingAlgorithm_;
	
	double[] contentsLowerLimits_;
	double[] contentsUpperLimits_;
	double[] vmTypesLowerLimits_;
	double[] vmTypesUpperLimits_;

	public CloudCDN_SO(String solutionType) {
		this(solutionType, "test/", 0, "RandomRouting", false);
	}

	public CloudCDN_SO(String solutionType, String pathName,
			int instanceNumber, String routingAlgorithm, boolean twophase) {

		super(solutionType, pathName, instanceNumber, twophase);
		
		numberOfVariables_ = regionesDatacenters_.size()
				+ (regionesDatacenters_.size() * 24); // 1 document assignment +
														// DC*24 hours VM
														// assignment
		numberOfObjectives_ = 1;
		numberOfConstraints_ = 2;

		problemName_ = "CloudCDN_SO";

		length_ = new int[numberOfVariables_];
		for (int var = 0; var < regionesDatacenters_.size(); var++) {
			length_[var] = getDocumentos().size();
		}

		for (int var_dc = 0; var_dc < regionesDatacenters_.size(); var_dc++) {
			for (int var_hr = 0; var_hr < 24; var_hr++) {
				length_[var_dc * 24 + var_hr + regionesDatacenters_.size()] = getMaquinas()
						.size();
			}
		}

		contentsLowerLimits_ = new double[getDocumentos().size()];

		for (int i = 0; i < getDocumentos().size(); i++) {
			contentsLowerLimits_[i] = 0;
		}

		contentsUpperLimits_ = new double[getDocumentos().size()];

		for (int i = 0; i < getDocumentos().size(); i++) {
			contentsUpperLimits_[i] = 1;
		}

		vmTypesLowerLimits_ = new double[getMaquinas().size()];

		for (int i = 0; i < getMaquinas().size(); i++) {
			vmTypesLowerLimits_[i] = 0;
		}

		vmTypesUpperLimits_ = new double[getMaquinas().size()];

		for (int i = 0; i < getMaquinas().size(); i++) {
			vmTypesUpperLimits_[i] = 15; // getTrafico().size(); // Big enough
											// value.
		}

		if (routingAlgorithm.compareTo("SimpleRR") == 0) {
			routingAlgorithm_ = new SimpleRR(this);
		} else if (routingAlgorithm.compareTo("Cheapest") == 0) {
			routingAlgorithm_ = new Cheapest(this);
		} else if (routingAlgorithm.compareTo("BestQoS") == 0) {
			routingAlgorithm_ = new BestQoS(this);
		} else if (routingAlgorithm.compareTo("RandomRouting") == 0) {
			routingAlgorithm_ = new RandomRouting(this);
		}
	}

	public double[] getNumberOfContentsLowerLimits() {
		return contentsLowerLimits_;
	}

	public double[] getNumberOfContentsUpperLimits() {
		return contentsUpperLimits_;
	}

	public double[] getNumberOfVMTypesLowerLimits() {
		return vmTypesLowerLimits_;
	}

	public double[] getNumberOfVMTypesUpperLimits() {
		return vmTypesUpperLimits_;
	}

	public double[] getContentsLowerLimits() {
		return contentsLowerLimits_;
	}

	public double[] getContentsUpperLimits() {
		return contentsUpperLimits_;
	}

	public int getTotalNumVM(Solution solution) {
		int total = 0;

		for (int i = 0; i < getRegionesDatacenters().size(); i++) {
			for (int h = 0; h < 24; h++) {
				for (int j = 0; j < getMaquinas().size(); j++) {
					try {
						total += CloudCDNSolutionType.GetVMVariables(solution,
								i, h).getValue(j);
					} catch (JMException e) {
						e.printStackTrace();
					}
				}
			}
		}

		return total;
	}

	public void evaluateFinalSolution(Solution solution) {
		double fitness = 0.0;
		double storageCost = 0.0;
		double machineCost = 0.0;
		double trafficCost = 0.0;

		System.out.println(">> [INFO] Routing algorithm: "
				+ routingAlgorithm_.getClass().toString());

		System.out.println(">> [INFO] Expected fitness: "
				+ solution.getObjective(0) + " [Penalty: "
				+ solution.getOverallConstraintViolation() + "]");

		try {
			System.out.println(">> Storage >>");
			
			for (int i = 0; i < getRegionesDatacenters().size(); i++) {
				double dataSize = 0.0;

				for (int j = 0; j < getDocumentos().size(); j++) {
					if (CloudCDNSolutionType.GetDocumentVariables(solution, i)
							.getValue(j) == 1) {
						dataSize += getDocumentos().get(j).docSize;
					}
				}

				System.out.println("DC " + i + " = " + dataSize + " GB");
				
				storageCost += getRegionesDatacenters().get(i)
						.computeStorageCost(dataSize);
			}

			System.out.println(">> VM >>");
			
			for (int i = 0; i < getRegionesDatacenters().size(); i++) {
				int total_horas;
				total_horas = 0;
				
				for (int h = 0; h < 24; h++) {
					for (int j = 0; j < getMaquinas().size(); j++) {
						int numVM;
						numVM = CloudCDNSolutionType.GetVMVariables(solution,
								i, h).getValue(j);

						total_horas += numVM;
						
						double priceVM;
						priceVM = getRegionesDatacenters().get(i)
								.computeVMCost(j);

						if (numVM > 0) {
							machineCost += numVM * priceVM;
						}
					}
				}
				
				System.out.println("DC " + i + " = " + total_horas + " hours");
			}

			routingAlgorithm_.Compute(solution, startEvalSecs, endEvalSecs);

			System.out.println(">> Traffic >>");
			
			for (int i = 0; i < getRegionesDatacenters().size(); i++) {
				trafficCost += getRegionesDatacenters().get(i)
						.computeTransferCost(
								routingAlgorithm_.getTrafficAmount()[i]);
				
				System.out.println("DC " + i + " = " + routingAlgorithm_.getTrafficAmount()[i] + " GB");
			}

			if ((QOS_THRESHOLD != 1.0)
					&& (routingAlgorithm_.getRatioQoS() >= QOS_THRESHOLD)) {
				solution.setNumberOfViolatedConstraint(routingAlgorithm_
						.getNumberOfBandwidthViolatedRequests());
				solution.setOverallConstraintViolation(routingAlgorithm_
						.getTotalViolatedBandwidth());
			} else {
				solution.setNumberOfViolatedConstraint(routingAlgorithm_
						.getNumberOfBandwidthViolatedRequests()
						+ routingAlgorithm_.getNumberOfQoSViolatedRequests());
				solution.setOverallConstraintViolation(routingAlgorithm_
						.getTotalViolatedBandwidth()
						+ routingAlgorithm_.getViolatedQoS());
			}

			fitness = (storageCost * (endEvalMonths - startEvalMonths))
					+ (machineCost)
					+ (trafficCost * (endEvalMonths - startEvalMonths));
		} catch (JMException e) {
			e.printStackTrace();
			fitness = Double.MAX_VALUE;
		}

		System.out.println(">> [INFO] Actual fitness: " + fitness
				+ " [Penalty: " + solution.getOverallConstraintViolation()
				+ "]");

		if (solution.getOverallConstraintViolation() > 0)
			fitness = -1;
		solution.setObjective(0, fitness);
	}

	/**
	 * Evaluates a solution
	 * 
	 * @param solution
	 *            The solution to evaluate
	 */
	public void evaluate(Solution solution) {
		FixSolution(solution);

		double fitness = 0.0;
		double storageCost = 0.0;
		double machineCost = 0.0;
		double trafficCost = 0.0;

		try {
			for (int i = 0; i < getRegionesDatacenters().size(); i++) {
				double dataSize = 0.0;

				for (int j = 0; j < getDocumentos().size(); j++) {
					if (CloudCDNSolutionType.GetDocumentVariables(solution, i)
							.getValue(j) == 1) {
						dataSize += getDocumentos().get(j).docSize;
					}
				}

				storageCost += getRegionesDatacenters().get(i)
						.computeStorageCost(dataSize);
			}

			for (int i = 0; i < getRegionesDatacenters().size(); i++) {
				for (int h = 0; h < 24; h++) {
					for (int j = 0; j < getMaquinas().size(); j++) {
						int numVM;
						numVM = CloudCDNSolutionType.GetVMVariables(solution,
								i, h).getValue(j);

						double priceVM;
						priceVM = getRegionesDatacenters().get(i)
								.computeVMCost(j);

						if (numVM > 0) {
							machineCost += numVM * priceVM;
						}
					}
				}
			}

			routingAlgorithm_.Compute(solution, startTrainingSecs,
					endTrainingSecs);

			double traffic;
			
			for (int i = 0; i < getRegionesDatacenters().size(); i++) {
				traffic = routingAlgorithm_.getTrafficAmount()[i];
				
				trafficCost += getRegionesDatacenters().get(i)
						.computeTransferCost(traffic);
			}

			if ((QOS_THRESHOLD != 1.0)
					&& (routingAlgorithm_.getRatioQoS() >= QOS_THRESHOLD)) {
				solution.setNumberOfViolatedConstraint(routingAlgorithm_
						.getNumberOfBandwidthViolatedRequests());
				solution.setOverallConstraintViolation(routingAlgorithm_
						.getTotalViolatedBandwidth());
			} else {
				solution.setNumberOfViolatedConstraint(routingAlgorithm_
						.getNumberOfBandwidthViolatedRequests()
						+ routingAlgorithm_.getNumberOfQoSViolatedRequests());
				solution.setOverallConstraintViolation(routingAlgorithm_
						.getTotalViolatedBandwidth()
						+ routingAlgorithm_.getViolatedQoS());
			}

			fitness = (storageCost * (endTrainingMonths - startTrainingMonths))
					+ (machineCost)
					+ (trafficCost * (endTrainingMonths - startTrainingMonths));
		} catch (JMException e) {
			e.printStackTrace();
			fitness = Double.MAX_VALUE;
		}

		solution.setObjective(0, fitness);
	}
	
	/**
	 * Evaluates the constraint overhead of a solution
	 * 
	 * @param solution
	 *            The solution
	 * @throws JMException
	 */
	public void evaluateConstraints(Solution solution) throws JMException {
		routingAlgorithm_.Compute(solution, startTrainingSecs, endTrainingSecs);

		if ((QOS_THRESHOLD != 1.0)
				&& (routingAlgorithm_.getRatioQoS() >= QOS_THRESHOLD)) {
			solution.setNumberOfViolatedConstraint(routingAlgorithm_
					.getNumberOfBandwidthViolatedRequests());
			solution.setOverallConstraintViolation(routingAlgorithm_
					.getTotalViolatedBandwidth());
		} else {
			solution.setNumberOfViolatedConstraint(routingAlgorithm_
					.getNumberOfBandwidthViolatedRequests()
					+ routingAlgorithm_.getNumberOfQoSViolatedRequests());
			solution.setOverallConstraintViolation(routingAlgorithm_
					.getTotalViolatedBandwidth()
					+ routingAlgorithm_.getViolatedQoS());
		}
	}
}
