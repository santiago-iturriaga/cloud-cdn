//  NSGAII_Settings.java 
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
import jmetal.experiments.Settings;
import jmetal.metaheuristics.nsgaII.NSGAII;
import jmetal.metaheuristics.singleObjective.geneticAlgorithm.gGA;
import jmetal.operators.crossover.Crossover;
import jmetal.operators.crossover.CrossoverFactory;
import jmetal.operators.mutation.Mutation;
import jmetal.operators.mutation.MutationFactory;
import jmetal.operators.selection.Selection;
import jmetal.operators.selection.SelectionFactory;
import jmetal.problems.ProblemFactory;
import jmetal.util.JMException;

import java.util.HashMap;
import java.util.Properties;

/**
 * Settings class of algorithm NSGA-II (real encoding)
 */
public class gGA_Settings extends Settings {
	public int populationSize_;
	public int maxEvaluations_;
	public double mutationProbability_;
	public double crossoverProbability_;
	public double mutationDistributionIndex_;
	public double crossoverDistributionIndex_;

	/**
	 * Constructor
	 */
	public gGA_Settings(String problem, Object[] problemParams) {
		super(problem);

		try {
			problem_ = (new ProblemFactory()).getProblem(problemName_,
					problemParams);
		} catch (JMException e) {
			e.printStackTrace();
		}
		// Default experiments.settings
		populationSize_ = 50;
		//maxEvaluations_ = 50;
		//maxEvaluations_ = 250;
		maxEvaluations_ = 1000;
		//maxEvaluations_ = 5000;
		//maxEvaluations_ = 10000;
		//maxEvaluations_ = 25000;
		
		//mutationProbability_ = 1.0 / problem_.getNumberOfVariables();

		//mutationProbability_ = 1000.0 / problem_.getNumberOfBits();
		mutationProbability_ = 1.0 / problem_.getNumberOfBits();
		//mutationProbability_ = 10.0 / problem_.getNumberOfBits();
		//mutationProbability_ = 1.0 / problem_.getNumberOfBits();
		
		crossoverProbability_ = 0.9;
		
		mutationDistributionIndex_ = 20.0;
		crossoverDistributionIndex_ = 20.0;
	} // NSGAII_Settings

	/**
	 * Configure NSGAII with default parameter experiments.settings
	 * 
	 * @return A NSGAII algorithm object
	 * @throws jmetal.util.JMException
	 */
	public Algorithm configure() throws JMException {
		Algorithm algorithm;
		Selection selection;
		Crossover crossover;
		Mutation mutation;

		HashMap parameters; // Operator parameters

		// Creating the algorithm. There are two choices: NSGAII and its steady-
		algorithm = new gGA(problem_);

		// Algorithm parameters
		algorithm.setInputParameter("populationSize", populationSize_);
		algorithm.setInputParameter("maxEvaluations", maxEvaluations_);

		// Mutation and Crossover for Real codification
		parameters = new HashMap();
		parameters.put("probability", crossoverProbability_);
		parameters.put("distributionIndex", crossoverDistributionIndex_);
		/*crossover = CrossoverFactory.getCrossoverOperator(
				"cloudcdn.HUXCrossover", parameters);*/
		crossover = CrossoverFactory.getCrossoverOperator(
				"cloudcdn.SinglePointCrossover", parameters);
		
		parameters = new HashMap();
		parameters.put("probability", mutationProbability_);
		parameters.put("distributionIndex", mutationDistributionIndex_);
		mutation = MutationFactory.getMutationOperator(
				"cloudcdn.BitFlipMutation", parameters);

		// Selection Operator
		parameters = null;
		selection = SelectionFactory.getSelectionOperator("BinaryTournament2",
				parameters);

		// Add the operators to the algorithm
		algorithm.addOperator("crossover", crossover);
		algorithm.addOperator("mutation", mutation);
		algorithm.addOperator("selection", selection);

		return algorithm;
	} // configure
}
