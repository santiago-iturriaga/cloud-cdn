//  IBEA_Settings.java 
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

package jmetal.experiments.settings.cloudcdn;

import jmetal.core.Algorithm;
import jmetal.core.Operator;
import jmetal.experiments.Settings;
import jmetal.metaheuristics.ibea.IBEA;
import jmetal.operators.crossover.Crossover;
import jmetal.operators.crossover.CrossoverFactory;
import jmetal.operators.mutation.Mutation;
import jmetal.operators.mutation.MutationFactory;
import jmetal.operators.selection.BinaryTournament;
import jmetal.operators.selection.Selection;
import jmetal.problems.ProblemFactory;
import jmetal.util.JMException;
import jmetal.util.comparators.FitnessComparator;

import java.util.HashMap;
import java.util.Properties;

/**
 * Settings class of algorithm IBEA
 */
public class IBEA_Settings extends Settings {

	public int populationSize_;
	public int maxEvaluations_;
	public int archiveSize_;

	public double mutationProbability_;
	public double crossoverProbability_;

	public double crossoverDistributionIndex_;
	public double mutationDistributionIndex_;

	/**
	 * Constructor
	 */
	public IBEA_Settings(String problemName, Object[] problemParams) {
		super(problemName);

		try {
			problem_ = (new ProblemFactory()).getProblem(problemName_,
					problemParams);
		} catch (JMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Default experiments.settings
		populationSize_ = 20;
		// maxEvaluations_ = 25000;
		maxEvaluations_ = 2500;
		archiveSize_ = 100;

		mutationProbability_ = 1.0 / problem_.getNumberOfVariables();
		crossoverProbability_ = 0.9;

		crossoverDistributionIndex_ = 20.0;
		mutationDistributionIndex_ = 20.0;
	} // IBEA_Settings

	/**
	 * Configure IBEA with user-defined parameter experiments.settings
	 * 
	 * @return A IBEA algorithm object
	 * @throws jmetal.util.JMException
	 */
	public Algorithm configure() throws JMException {
		Algorithm algorithm;
		Operator selection;
		Operator crossover;
		Operator mutation;

		HashMap parameters; // Operator parameters

		algorithm = new IBEA(problem_);

		// Algorithm parameters
		algorithm.setInputParameter("populationSize", populationSize_);
		algorithm.setInputParameter("maxEvaluations", maxEvaluations_);
		algorithm.setInputParameter("archiveSize", archiveSize_);

		// Mutation and Crossover for Real codification
		parameters = new HashMap();
		parameters.put("probability", crossoverProbability_);
		parameters.put("distributionIndex", crossoverDistributionIndex_);
		/*
		 * crossover = CrossoverFactory.getCrossoverOperator("SBXCrossover",
		 * parameters);
		 */
		crossover = CrossoverFactory.getCrossoverOperator(
				"cloudcdn.HUXCrossover", parameters);

		parameters = new HashMap();
		parameters.put("probability", mutationProbability_);
		parameters.put("distributionIndex", mutationDistributionIndex_);
		/*
		 * mutation = MutationFactory.getMutationOperator("PolynomialMutation",
		 * parameters);
		 */
		mutation = MutationFactory.getMutationOperator(
				"cloudcdn.BitFlipMutation", parameters);

		/* Selection Operator */
		parameters = new HashMap();
		parameters.put("comparator", new FitnessComparator());
		selection = new BinaryTournament(parameters);

		// Add the operators to the algorithm
		algorithm.addOperator("crossover", crossover);
		algorithm.addOperator("mutation", mutation);
		algorithm.addOperator("selection", selection);

		return algorithm;
	} // configure
} // IBEA_Settings
