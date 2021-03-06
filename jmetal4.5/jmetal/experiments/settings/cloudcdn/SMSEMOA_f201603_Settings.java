//  SMSEMA_Settings.java 
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

import java.util.ArrayList;
import jmetal.experiments.settings.*;
import jmetal.core.Algorithm;
import jmetal.experiments.Settings;
import jmetal.metaheuristics.smsemoa.SMSEMOA;
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
import jmetal.core.Solution;
import jmetal.core.Variable;
import jmetal.encodings.solutionType.cloudcdn.CloudCDNSolutionf201603Type;
import jmetal.metaheuristics.smsemoa.FastSMSEMOA;
import jmetal.problems.cloudcdn.f201603.CloudCDN_MP;

/**
 * Settings class of algorithm SMSEMOA
 */
public class SMSEMOA_f201603_Settings extends Settings {

    public int populationSize_;
    public int maxEvaluations_;
    public double mutationProbability_;
    public double crossoverProbability_;
    public double crossoverDistributionIndex_;
    public double mutationDistributionIndex_;
    public double offset_;
    public boolean printHV_;

    /**
     * Constructor
     */
    public SMSEMOA_f201603_Settings(String problem, int maxEval, boolean printHV, Object[] problemParams) {
        super(problem);

        try {
            problem_ = (new ProblemFactory()).getProblem(problemName_, problemParams);
        } catch (JMException e) {
            e.printStackTrace();
        }
        //populationSize_ = 10;
        populationSize_ = 100;
        //maxEvaluations_ = 1000;
        //maxEvaluations_ = 10000;
        //maxEvaluations_ = 50000;
        maxEvaluations_ = maxEval;
        mutationProbability_ = 1.0 / problem_.getNumberOfBits();
        crossoverProbability_ = 0.9;
        crossoverDistributionIndex_ = 20.0;
        mutationDistributionIndex_ = 20.0;
        offset_ = 100.0;
        printHV_ = printHV;
    } // SMSEMOA_Settings

    /**
     * Configure SMSEMOA with user-defined parameter experiments.settings
     *
     * @return A SMSEMOA algorithm object
     * @throws jmetal.util.JMException
     */
    public Algorithm configure() throws JMException {
        Algorithm algorithm;
        Selection selection;
        Crossover crossover;
        Mutation mutation;

        HashMap parameters; // Operator parameters

        // Creating the algorithm. 
        algorithm = new SMSEMOA(problem_);
        //algorithm = new FastSMSEMOA(problem_);
        
        // Algorithm parameters
        algorithm.setInputParameter("populationSize", populationSize_);
        algorithm.setInputParameter("maxEvaluations", maxEvaluations_);
        algorithm.setInputParameter("offset", offset_);
        algorithm.setInputParameter("printHV", printHV_);

        // Mutation and Crossover for Real codification 
        parameters = new HashMap();
        parameters.put("probability", crossoverProbability_);
        parameters.put("distributionIndex", crossoverDistributionIndex_);
        //crossover = CrossoverFactory.getCrossoverOperator("SBXCrossover", parameters);                   
        //crossover = CrossoverFactory.getCrossoverOperator("cloudcdn.SinglePointCrossoverf201603", parameters);
        crossover = CrossoverFactory.getCrossoverOperator("cloudcdn.HUXCrossoverf201603", parameters);

        parameters = new HashMap();
        parameters.put("probability", mutationProbability_);
        parameters.put("distributionIndex", mutationDistributionIndex_);
        //mutation = MutationFactory.getMutationOperator("PolynomialMutation", parameters);          
        mutation = MutationFactory.getMutationOperator("cloudcdn.BitFlipMutationf201603", parameters);

        // Selection Operator
        parameters = null;
        selection = SelectionFactory.getSelectionOperator("RandomSelection", parameters);

        // Add the operators to the algorithm
        algorithm.addOperator("crossover", crossover);
        algorithm.addOperator("mutation", mutation);
        algorithm.addOperator("selection", selection);
        
        ArrayList<Solution> startingPopulation = new ArrayList<>();
        startingPopulation.add(new Solution(problem_, ((CloudCDN_MP)problem_).solutionTypeCustom_.createOneMaxVariables()));
        startingPopulation.add(new Solution(problem_, ((CloudCDN_MP)problem_).solutionTypeCustom_.createZeroMaxVariables()));
        algorithm.setInputParameter("startingPopulation", startingPopulation);
        
        return algorithm;
    } // configure

} // SMSEMOA_Settings
