package jmetal.problems.cloudcdn;

import java.io.IOException;

import jmetal.core.Problem;
import jmetal.core.Solution;
import jmetal.encodings.solutionType.cloudcdn.CloudCDNSolutionType;
import jmetal.util.JMException;

public class CloudCDN_SO extends Problem {
	private static final long serialVersionUID = -6970983090454693518L;
	
	private int numberOfVMTypes_;
	private int numberOfContents_;

	double[] contentsLowerLimits_;
	double[] contentsUpperLimits_;
	double[] vmTypesLowerLimits_;
	double[] vmTypesUpperLimits_;
	
	public CloudCDN_SO(String solutionType) {
		this(solutionType, "prueba");
	}

	public CloudCDN_SO(String solutionType, String filename) {
		int numOfDC = 8; // cloudproblem.getNumberOfDC(); ???

		numberOfVariables_ = numOfDC * 2;
		numberOfObjectives_ = 1;
		numberOfConstraints_ = 2;

		problemName_ = "CloudCDN_SO";

		length_ = new int[numberOfVariables_];
		for (int var = 0; var < getNumberOfVariables() / 2; var++) {
			length_[var] = getNumberOfContents();
		}
		for (int var = getNumberOfVariables() / 2; var < getNumberOfVariables(); var++) {
			length_[var] = getNumberOfVMTypes();
		}

		contentsLowerLimits_ = new double[getNumberOfContents()];

		for (int i = 0; i < getNumberOfContents(); i++) {
			contentsLowerLimits_[i] = 0;
		}

		contentsUpperLimits_ = new double[getNumberOfContents()];

		for (int i = 0; i < getNumberOfContents(); i++) {
			contentsUpperLimits_[i] = 1;
		}
		
		vmTypesLowerLimits_ = new double[getNumberOfVMTypes()];

		for (int i = 0; i < getNumberOfVMTypes(); i++) {
			vmTypesLowerLimits_[i] = 0;
		}
		
		vmTypesUpperLimits_ = new double[getNumberOfVMTypes()];

		for (int i = 0; i < getNumberOfVMTypes(); i++) {
			vmTypesUpperLimits_[i] = getNumberOfContents(); // Big enough value.
		}
		
		solutionType_ = new CloudCDNSolutionType(this);

		try {
			if (solutionType.compareTo("CloudCDNSolutionType") == 0)
				solutionType_ = new CloudCDNSolutionType(this);
			else {
				throw new JMException("Solution type invalid");
			}
		} catch (JMException e) {
			e.printStackTrace();
		}

		try {
			readProblem(filename);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public int getNumberOfContents() {
		return numberOfContents_;
	}

	public double[] getNumberOfContentsLowerLimits() {
		return contentsLowerLimits_;
	}

	public double[] getNumberOfContentsUpperLimits() {
		return contentsUpperLimits_;
	}

	public int getNumberOfVMTypes() {
		return numberOfVMTypes_;
	}

	public double[] getNumberOfVMTypesLowerLimits() {
		return vmTypesLowerLimits_;
	}

	public double[] getNumberOfVMTypesUpperLimits() {
		return vmTypesUpperLimits_;
	}

	/**
	 * Evaluates a solution
	 * 
	 * @param solution
	 *            The solution to evaluate
	 */
	public void evaluate(Solution solution) {
		double fitness;
		fitness = 0.0;

		// TODO: implementar evaluate fitness.

		/*
		 * for (int i = 0; i < (numberOfCities_ - 1); i++) { int x; int y;
		 * 
		 * x = ((Permutation) solution.getDecisionVariables()[0]).vector_[i]; y
		 * = ((Permutation) solution.getDecisionVariables()[0]).vector_[i + 1];
		 * // cout << "I : " << i << ", x = " << x << ", y = " << y << endl ;
		 * fitness += distanceMatrix_[x][y]; } // for int firstCity; int
		 * lastCity;
		 * 
		 * firstCity = ((Permutation)
		 * solution.getDecisionVariables()[0]).vector_[0]; lastCity =
		 * ((Permutation)
		 * solution.getDecisionVariables()[0]).vector_[numberOfCities_ - 1];
		 * fitness += distanceMatrix_[firstCity][lastCity];
		 */

		solution.setObjective(0, fitness);
	}

	public void readProblem(String fileName) throws IOException {
		try {
			// TODO: implementar carga del problema.
			
			numberOfVMTypes_ = 4;
			numberOfContents_ = 1000;
		} catch (Exception e) {
			System.err.println("readProblem(): error when reading data file "
					+ e);
			System.exit(1);
		}
	}

	/**
	 * Evaluates the constraint overhead of a solution
	 * 
	 * @param solution
	 *            The solution
	 * @throws JMException
	 */
	public void evaluateConstraints(Solution solution) throws JMException {
		double[] constraint = new double[this.getNumberOfConstraints()];

		double total = 0.0;
		int number = 0;

		//TODO: implementar constraint C1 y C3.
		
		/*
		 * double c1 = solution.getDecisionVariables()[0].getValue(); double c2
		 * = solution.getDecisionVariables()[1].getValue(); double c3 =
		 * solution.getDecisionVariables()[1].getValue();
		 * 
		 * constraint[0] = (x2 + 9*x1 -6.0) ; constraint[1] = (-x2 + 9*x1 -1.0);
		 * 
		 * for (int i = 0; i < this.getNumberOfConstraints(); i++) if
		 * (constraint[i]<0.0){ total+=constraint[i]; number++; }
		 */

		solution.setOverallConstraintViolation(total);
		solution.setNumberOfViolatedConstraint(number);
	}
}
