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
import jmetal.metaheuristics.singleObjective.evolutionStrategy.ElitistES;
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

public class ES_Settings extends Settings {
    public int mu_; 
    public int lambda_; 
	
	public int populationSize_;
	public int maxEvaluations_;
	public double mutationProbability_;
	public double crossoverProbability_;
	public double mutationDistributionIndex_;
	public double crossoverDistributionIndex_;

	/**
	 * Constructor
	 */
	public ES_Settings(String problem, Object[] problemParams) {
		super(problem);

		try {
			problem_ = (new ProblemFactory()).getProblem(problemName_,
					problemParams);
		} catch (JMException e) {
			e.printStackTrace();
		}
		// Default experiments.settings
		
    
	    // Requirement: lambda must be divisible by mu
	    //mu_ = 10  ;
	    //lambda_ = 20 ;

	    mu_ = 1;
	    lambda_ = 10;
	    
		maxEvaluations_ = 2500;
		// maxEvaluations_ = 25000;
		
		//mutationProbability_ = 1.0 / problem_.getNumberOfVariables();
		mutationProbability_ = 1.0 / problem_.getNumberOfBits();
	}

	public Algorithm configure() throws JMException {
		Algorithm algorithm;
		Selection selection;
		Crossover crossover;
		Mutation mutation;

		HashMap parameters; // Operator parameters

		// Creating the algorithm. There are two choices: NSGAII and its steady-
		algorithm = new ElitistES(problem_, mu_, lambda_);

		// Algorithm parameters
		algorithm.setInputParameter("maxEvaluations", maxEvaluations_);

		parameters = new HashMap();
		parameters.put("probability", mutationProbability_);
		mutation = MutationFactory.getMutationOperator(
				"cloudcdn.BitFlipMutation", parameters);

		algorithm.addOperator("mutation", mutation);

		return algorithm;
	} // configure
}
