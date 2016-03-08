package jmetal.encodings.solutionType.cloudcdn;

import jmetal.core.Problem;
import jmetal.core.Solution;
import jmetal.core.SolutionType;
import jmetal.core.Variable;
import jmetal.encodings.variable.ArrayInt;
import jmetal.problems.cloudcdn.CloudCDN_RRSO;

public class CloudCDNRRSolutionType extends SolutionType {
	CloudCDN_RRSO customProblem_;

	/**
	 * Constructor
	 * 
	 * @param problem
	 *            Problem to solve
	 */
	public CloudCDNRRSolutionType(Problem problem) {
		super(problem);

		customProblem_ = (CloudCDN_RRSO) problem;
	} // Constructor

	public int getVMVarIndex(int dc, int hour) {
		return getVMVarIndex(customProblem_, dc, hour);
	}

	public static int getVMVarIndex(CloudCDN_RRSO customProblem, int dc, int hour) {
		return customProblem.getRegionesDatacenters().size() + dc * 24 + hour;
	}

	/**
	 * Creates the variables of the solution
	 */
	public Variable[] createVariables() {
		if (problem_.getClass().equals(CloudCDN_RRSO.class)) {
			Variable[] variables = new Variable[problem_.getNumberOfVariables()];

			for (int var = 0; var < customProblem_.getRegionesDatacenters()
					.size(); var++) {
				variables[var] = new ArrayInt(customProblem_.getDocumentos()
						.size(),
						customProblem_.getNumberOfContentsLowerLimits(),
						customProblem_.getNumberOfContentsUpperLimits());
			}

			return variables;
		} else {
			System.out
					.println("[ERROR] Invalid problem type (not CloudCDN_SO problem). Skipping variable initialization.");
			return null;
		}
	} // createVariables

	public static ArrayInt GetDocumentVariables(Solution solution, int dc) {
		return (ArrayInt) solution.getDecisionVariables()[dc];
	}
}
