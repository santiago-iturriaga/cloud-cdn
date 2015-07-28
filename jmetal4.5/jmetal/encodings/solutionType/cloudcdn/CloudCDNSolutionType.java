package jmetal.encodings.solutionType.cloudcdn;

import jmetal.core.Problem;
import jmetal.core.Solution;
import jmetal.core.SolutionType;
import jmetal.core.Variable;
import jmetal.encodings.variable.ArrayInt;
import jmetal.problems.cloudcdn.CloudCDN_SO;

public class CloudCDNSolutionType extends SolutionType {
	CloudCDN_SO customProblem_;

	/**
	 * Constructor
	 * 
	 * @param problem
	 *            Problem to solve
	 */
	public CloudCDNSolutionType(Problem problem) {
		super(problem);

		customProblem_ = (CloudCDN_SO) problem;
	} // Constructor

	public int getVMVarIndex(int dc, int hour) {
		return getVMVarIndex(customProblem_, dc, hour);
	}

	public static int getVMVarIndex(CloudCDN_SO customProblem, int dc, int hour) {
		return customProblem.getRegionesDatacenters().size() + dc * 24 + hour;
	}

	/**
	 * Creates the variables of the solution
	 */
	public Variable[] createVariables() {
		if (problem_.getClass().equals(CloudCDN_SO.class)) {
			Variable[] variables = new Variable[problem_.getNumberOfVariables()];

			for (int var = 0; var < customProblem_.getRegionesDatacenters()
					.size(); var++) {
				variables[var] = new ArrayInt(customProblem_.getDocumentos()
						.size(),
						customProblem_.getNumberOfContentsLowerLimits(),
						customProblem_.getNumberOfContentsUpperLimits());
			}

			for (int var_dc = 0; var_dc < customProblem_
					.getRegionesDatacenters().size(); var_dc++) {
				for (int var_hr = 0; var_hr < 24; var_hr++) {
					/*
					 * variables[var] = new ArrayInt(
					 * customProblem.getMaquinas().size(),
					 * customProblem.getNumberOfVMTypesLowerLimits(),
					 * customProblem.getNumberOfVMTypesUpperLimits());
					 */

					double[] vmTypesUpperLimits_ = new double[customProblem_
							.getMaquinas().size()];

					for (int i = 0; i < customProblem_.getMaquinas().size(); i++) {
						vmTypesUpperLimits_[i] = 1;
					}

					variables[getVMVarIndex(var_dc, var_hr)] = new ArrayInt(
							customProblem_.getMaquinas().size(),
							customProblem_.getNumberOfVMTypesLowerLimits(),
							customProblem_.getNumberOfVMTypesUpperLimits(),
							vmTypesUpperLimits_);
				}
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

	public static ArrayInt GetVMVariables(Solution solution, int dc, int hour) {
		CloudCDN_SO customProblem = (CloudCDN_SO) solution.getProblem();
		return (ArrayInt) solution.getDecisionVariables()[getVMVarIndex(
				customProblem, dc, hour)];
	}
}
