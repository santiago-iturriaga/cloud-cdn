//  MOCHC.java
//
//  Author:
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
package jmetal.metaheuristics.mochc;

import java.util.ArrayList;
import jmetal.core.*;
import jmetal.encodings.variable.Binary;
import jmetal.util.JMException;
import jmetal.util.archive.CrowdingArchive;
import jmetal.util.comparators.CrowdingComparator;

import java.util.Comparator;
import jmetal.encodings.variable.ArrayInt;
import jmetal.qualityIndicator.Hypervolume;

/**
 * This class executes the MOCHC algorithm described in: A.J. Nebro, E. Alba, G.
 * Molina, F. Chicano, F. Luna, J.J. Durillo "Optimal antenna placement using a
 * new multi-objective chc algorithm". GECCO '07: Proceedings of the 9th annual
 * conference on Genetic and evolutionary computation. London, England. July
 * 2007.
 */
public class MOCHC extends Algorithm {

    private Hypervolume hv_;
    CrowdingArchive archive_;

    /**
     * Constructor Creates a new instance of MOCHC
     */
    public MOCHC(Problem problem) {
        super(problem);
        this.hv_ = new Hypervolume();
    }

    /**
     * Compares two solutionSets to determine if both are equals
     *
     * @param solutionSet A <code>SolutionSet</code>
     * @param newSolutionSet A <code>SolutionSet</code>
     * @return true if both are cotains the same solutions, false in other case
     */
    public boolean equals(SolutionSet solutionSet, SolutionSet newSolutionSet) {
        boolean found;
        for (int i = 0; i < solutionSet.size(); i++) {

            int j = 0;
            found = false;
            while (j < newSolutionSet.size()) {

                if (solutionSet.get(i).equals(newSolutionSet.get(j))) {
                    found = true;
                }
                j++;
            }
            if (!found) {
                return false;
            }
        }
        return true;
    } // equals

    /**
     * Calculate the hamming distance between two solutions
     *
     * @param solutionOne A <code>Solution</code>
     * @param solutionTwo A <code>Solution</code>
     * @return the hamming distance between solutions
     */
    public int hammingDistance(Solution solutionOne, Solution solutionTwo) {
        int distance = 0;
        for (int i = 0; i < problem_.getNumberOfVariables(); i++) {
            if (solutionOne.getDecisionVariables()[i].getClass().equals(Binary.class)) {
                distance
                        += ((Binary) solutionOne.getDecisionVariables()[i]).
                        hammingDistance((Binary) solutionTwo.getDecisionVariables()[i]);
            } else {
                distance
                        += ((ArrayInt) solutionOne.getDecisionVariables()[i]).
                        hammingDistance((ArrayInt) solutionTwo.getDecisionVariables()[i]);
            }
        }

        return distance;
    } // hammingDistance 

    /**
     * Runs of the MOCHC algorithm.
     *
     * @return a <code>SolutionSet</code> that is a set of non dominated
     * solutions as a result of the algorithm execution
     */
    public SolutionSet execute() throws JMException, ClassNotFoundException {
        int populationSize;
        int convergenceValue;
        int maxEvaluations;
        int minimumDistance;
        int evaluations;

        Comparator crowdingComparator = new CrowdingComparator();

        Operator crossover;
        Operator parentSelection;
        Operator newGenerationSelection;
        Operator cataclysmicMutation;

        double preservedPopulation;
        double initialConvergenceCount;
        boolean condition = false;
        SolutionSet solutionSet, offspringPopulation, newPopulation;

        // Read parameters
        initialConvergenceCount
                = ((Double) getInputParameter("initialConvergenceCount")).doubleValue();
        preservedPopulation
                = ((Double) getInputParameter("preservedPopulation")).doubleValue();
        convergenceValue
                = ((Integer) getInputParameter("convergenceValue")).intValue();
        populationSize
                = ((Integer) getInputParameter("populationSize")).intValue();
        maxEvaluations
                = ((Integer) getInputParameter("maxEvaluations")).intValue();

        boolean printHV;
        printHV = (boolean) getInputParameter("printHV");

        ArrayList<Solution> startingPopulation = (ArrayList<Solution>) getInputParameter("startingPopulation");

        // Read operators
        crossover = (Operator) getOperator("crossover");
        cataclysmicMutation = (Operator) getOperator("cataclysmicMutation");
        parentSelection = (Operator) getOperator("parentSelection");
        newGenerationSelection = (Operator) getOperator("newGenerationSelection");

        evaluations = 0;

        //Calculate the maximum problem sizes
        Solution aux = new Solution(problem_);
        int size = 0;
        for (int var = 0; var < problem_.getNumberOfVariables(); var++) {
            if (aux.getDecisionVariables()[var].getClass().equals(Binary.class)) {
                size += ((Binary) aux.getDecisionVariables()[var]).getNumberOfBits();
            } else {
                size += ((ArrayInt) aux.getDecisionVariables()[var]).getLength();
            }
        }
        minimumDistance = (int) Math.floor(initialConvergenceCount * size);

        archive_ = new CrowdingArchive(populationSize, problem_.getNumberOfObjectives());

        solutionSet = new SolutionSet(populationSize);

        for (int i = 0; i < startingPopulation.size(); i++) {
            problem_.evaluate(startingPopulation.get(i));
            problem_.evaluateConstraints(startingPopulation.get(i));
            evaluations++;
            solutionSet.add(startingPopulation.get(i));

            System.out.print(solutionSet.size() + " ");
        }

        while (solutionSet.size() < populationSize) {
            Solution solution = new Solution(problem_);
            problem_.evaluate(solution);
            problem_.evaluateConstraints(solution);
            evaluations++;
            solutionSet.add(solution);
        }

        System.out.println("\nRunning:");

        int lastHV = -1;

        while (!condition) {
            offspringPopulation = new SolutionSet(populationSize);
            for (int i = 0; i < solutionSet.size() / 2; i++) {
                Solution[] parents = (Solution[]) parentSelection.execute(solutionSet);

                //Equality condition between solutions
                if (hammingDistance(parents[0], parents[1]) >= (minimumDistance)) {
                    Solution[] offspring = (Solution[]) crossover.execute(parents);
                    problem_.evaluate(offspring[0]);
                    problem_.evaluateConstraints(offspring[0]);
                    problem_.evaluate(offspring[1]);
                    problem_.evaluateConstraints(offspring[1]);
                    evaluations += 2;
                    offspringPopulation.add(offspring[0]);
                    offspringPopulation.add(offspring[1]);
                }
            }
            SolutionSet union = solutionSet.union(offspringPopulation);
            newGenerationSelection.setParameter("populationSize", populationSize);
            newPopulation = (SolutionSet) newGenerationSelection.execute(union);

            if (equals(solutionSet, newPopulation)) {
                minimumDistance--;
            }
            if (minimumDistance <= -convergenceValue) {
                minimumDistance = (int) (1.0 / size * (1 - 1.0 / size) * size);
                //minimumDistance = (int) (0.35 * (1 - 0.35) * size);

                int preserve = (int) Math.floor(preservedPopulation * populationSize);
                newPopulation = new SolutionSet(populationSize);
                solutionSet.sort(crowdingComparator);
                for (int i = 0; i < preserve; i++) {
                    newPopulation.add(new Solution(solutionSet.get(i)));
                }
                for (int i = preserve; i < populationSize; i++) {
                    Solution solution = new Solution(solutionSet.get(i));
                    cataclysmicMutation.execute(solution);
                    problem_.evaluate(solution);
                    problem_.evaluateConstraints(solution);
                    evaluations += 1;
                    newPopulation.add(solution);
                }
            }

            solutionSet = newPopulation;
            if (evaluations >= maxEvaluations) {
                condition = true;
            }

            for (int i = 0; i < solutionSet.size(); i++) {
                archive_.add(solutionSet.get(i));
            }

            if (printHV) {
                if ((lastHV == -1) || (evaluations - lastHV >= 1000) || (evaluations >= maxEvaluations)) {
                    if (lastHV == -1) {
                        lastHV = 0;
                    } else {
                        lastHV = evaluations;
                    }

                    double currentPF[][];
                    currentPF = archive_.writeObjectivesToMatrix();

                    double normalizedFront[][];
                    normalizedFront = normalize(currentPF);

                    double hv_value;
                    hv_value = hv_.calculateHypervolume(
                            normalizedFront,
                            archive_.size(),
                            problem_.getNumberOfObjectives());

                    System.out.println(evaluations + " " + hv_value);
                    //ranking.getSubfront(0).printFeasibleFUN("FUN_" + evaluations);
                }
            }
        }

        System.out.println("Total evaluations " + evaluations);

        return archive_;
    } // execute

    private double[][] normalize(double[][] pf) {
        double[][] normalizedPF;
        normalizedPF = new double[pf.length][pf[0].length];

        for (int i = 0; i < pf.length; i++) {
            for (int j = 0; j < pf[0].length; j++) {
                normalizedPF[i][j] = 1.0 / pf[i][j];
            }
        }

        return normalizedPF;
    }
}  // MOCHC
