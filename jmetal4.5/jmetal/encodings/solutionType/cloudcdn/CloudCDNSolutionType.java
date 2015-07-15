package jmetal.encodings.solutionType.cloudcdn;

import jmetal.core.Problem;
import jmetal.core.SolutionType;
import jmetal.core.Variable;
import jmetal.encodings.variable.ArrayInt;
import jmetal.problems.cloudcdn.CloudCDN_SO;

public class CloudCDNSolutionType extends SolutionType {
	/**
	 * Constructor
	 * 
	 * @param problem
	 *            Problem to solve
	 */
	public CloudCDNSolutionType(Problem problem) {
		super(problem);
	} // Constructor

	/**
	 * Creates the variables of the solution
	 */
	public Variable[] createVariables() {
		if (problem_.getClass().equals(CloudCDN_SO.class)) {
			CloudCDN_SO customProblem = (CloudCDN_SO) problem_;

			Variable[] variables = new Variable[problem_.getNumberOfVariables()];

			for (int var = 0; var < problem_.getNumberOfVariables() / 2; var++) {
				
				variables[var] = new ArrayInt(
						customProblem.getCantidadDocumentos(),
						customProblem.getNumberOfContentsLowerLimits(),
						customProblem.getNumberOfContentsUpperLimits());
			}

			for (int var = problem_.getNumberOfVariables() / 2; var < problem_
					.getNumberOfVariables(); var++) {
				
				variables[var] = new ArrayInt(
						customProblem.getCantidadMaquinas(),
						customProblem.getNumberOfVMTypesLowerLimits(),
						customProblem.getNumberOfVMTypesUpperLimits());
			}

			return variables;
		} else {
			System.out
					.println("[ERROR] Invalid problem type (not CloudCDN_SO problem). Skipping variable initialization.");
			return null;
		}
	} // createVariables
}
