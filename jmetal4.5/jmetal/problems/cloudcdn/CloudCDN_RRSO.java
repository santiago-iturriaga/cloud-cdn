package jmetal.problems.cloudcdn;

import jmetal.core.Solution;
import jmetal.encodings.solutionType.cloudcdn.CloudCDNRRSolutionType;
import jmetal.problems.cloudcdn.greedy.routing.BestQoS;
import jmetal.problems.cloudcdn.greedy.routing.Cheapest;
import jmetal.problems.cloudcdn.greedy.routing.RRCheapest;
import jmetal.problems.cloudcdn.greedy.routing.RRRoutingAlgorithm;
import jmetal.problems.cloudcdn.greedy.routing.RandomRouting;
import jmetal.problems.cloudcdn.greedy.routing.RoutingAlgorithm;
import jmetal.problems.cloudcdn.greedy.routing.SimpleRR;
import jmetal.util.JMException;

public class CloudCDN_RRSO extends CloudCDN_base {
	private static final long serialVersionUID = 8680593990260640094L;
	
	protected RRRoutingAlgorithm routingAlgorithm_;
	
	double[] contentsLowerLimits_;
	double[] contentsUpperLimits_;

	public CloudCDN_RRSO(String solutionType) {
		this(solutionType, "test/", 0, "RandomRouting", false);
	}

	public CloudCDN_RRSO(String solutionType, String pathName,
			int instanceNumber, String routingAlgorithm, boolean twophase) {

		super(solutionType, pathName, instanceNumber, twophase);
		
		numberOfVariables_ = regionesDatacenters_.size(); // 1 document assignment
		numberOfObjectives_ = 1;
		numberOfConstraints_ = 1;

		problemName_ = "CloudCDN_RRSO";

		length_ = new int[numberOfVariables_];
		for (int var = 0; var < regionesDatacenters_.size(); var++) {
			length_[var] = getDocumentos().size();
		}

		contentsLowerLimits_ = new double[getDocumentos().size()];

		for (int i = 0; i < getDocumentos().size(); i++) {
			contentsLowerLimits_[i] = 0;
		}

		contentsUpperLimits_ = new double[getDocumentos().size()];

		for (int i = 0; i < getDocumentos().size(); i++) {
			contentsUpperLimits_[i] = 1;
		}

		if (routingAlgorithm.compareTo("RRCheapest") == 0) {
			routingAlgorithm_ = new RRCheapest(this);
		}
	}

	public double[] getNumberOfContentsLowerLimits() {
		return contentsLowerLimits_;
	}

	public double[] getNumberOfContentsUpperLimits() {
		return contentsUpperLimits_;
	}

	public double[] getContentsLowerLimits() {
		return contentsLowerLimits_;
	}

	public double[] getContentsUpperLimits() {
		return contentsUpperLimits_;
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
					if (CloudCDNRRSolutionType.GetDocumentVariables(solution, i)
							.getValue(j) == 1) {
						dataSize += getDocumentos().get(j).docSize;
					}
				}

				System.out.println("DC " + i + " = " + dataSize + " GB");
				
				storageCost += getRegionesDatacenters().get(i)
						.computeStorageCost(dataSize);
			}

			routingAlgorithm_.Compute(solution, startEvalSecs, endEvalSecs);

			System.out.println(">> Traffic >>");
			
			for (int i = 0; i < getRegionesDatacenters().size(); i++) {
				trafficCost += getRegionesDatacenters().get(i)
						.computeTransferCost(
								routingAlgorithm_.getTrafficAmount()[i]);
				
				System.out.println("DC " + i + " = " + routingAlgorithm_.getTrafficAmount()[i] + " GB");
			}

			solution.setNumberOfViolatedConstraint(routingAlgorithm_.getNumberOfQoSViolatedRequests());
			solution.setOverallConstraintViolation(routingAlgorithm_.getViolatedQoS());

			machineCost = routingAlgorithm_.getMachineCost();

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
					if (CloudCDNRRSolutionType.GetDocumentVariables(solution, i)
							.getValue(j) == 1) {
						dataSize += getDocumentos().get(j).docSize;
					}
				}

				storageCost += getRegionesDatacenters().get(i)
						.computeStorageCost(dataSize);
			}

			routingAlgorithm_.Compute(solution, startTrainingSecs,
					endTrainingSecs);

			double traffic;
			
			for (int i = 0; i < getRegionesDatacenters().size(); i++) {
				traffic = routingAlgorithm_.getTrafficAmount()[i];
				
				trafficCost += getRegionesDatacenters().get(i)
						.computeTransferCost(traffic);
			}
			
			solution.setNumberOfViolatedConstraint(routingAlgorithm_.getNumberOfQoSViolatedRequests());
			solution.setOverallConstraintViolation(routingAlgorithm_.getViolatedQoS());

			machineCost = routingAlgorithm_.getMachineCost();
			
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

		solution.setNumberOfViolatedConstraint(routingAlgorithm_.getNumberOfQoSViolatedRequests());
		solution.setOverallConstraintViolation(routingAlgorithm_.getViolatedQoS());
	}
}
