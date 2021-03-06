//  BitFlipMutation.java
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
package jmetal.operators.mutation.cloudcdn;

import jmetal.operators.mutation.*;
import jmetal.core.Solution;
import jmetal.encodings.solutionType.BinaryRealSolutionType;
import jmetal.encodings.solutionType.BinarySolutionType;
import jmetal.encodings.solutionType.IntSolutionType;
import jmetal.encodings.solutionType.cloudcdn.CloudCDNSolutionf201603Type;
import jmetal.encodings.variable.Binary;
import jmetal.encodings.variable.ArrayInt;
import jmetal.util.Configuration;
import jmetal.util.JMException;
import jmetal.util.PseudoRandom;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * This class implements a bit flip mutation operator. NOTE: the operator is
 * applied to binary or integer solutions, considering the whole solution as a
 * single encodings.variable.
 */
public class BitFlipMutationf201603 extends Mutation {

    /**
     * Valid solution types to apply this operator
     */
    private static final List VALID_TYPES = Arrays.asList(BinarySolutionType.class,
            BinaryRealSolutionType.class,
            IntSolutionType.class,
            CloudCDNSolutionf201603Type.class);

    private Double mutationProbability_ = null;

    /**
     * Constructor Creates a new instance of the Bit Flip mutation operator
     */
    public BitFlipMutationf201603(HashMap<String, Object> parameters) {
        super(parameters);
        if (parameters.get("probability") != null) {
            mutationProbability_ = (Double) parameters.get("probability");
        }
    } // BitFlipMutation

    /**
     * Perform the mutation operation
     *
     * @param probability Mutation probability
     * @param solution The solution to mutate
     * @throws JMException
     */
    public void doMutation(double probability, Solution solution) throws JMException {
        try {
            if ((solution.getType().getClass() == BinarySolutionType.class)
                    || (solution.getType().getClass() == BinaryRealSolutionType.class)) {
                for (int i = 0; i < solution.getDecisionVariables().length; i++) {
                    for (int j = 0; j < ((Binary) solution.getDecisionVariables()[i]).getNumberOfBits(); j++) {
                        if (PseudoRandom.randDouble() < probability) {
                            ((Binary) solution.getDecisionVariables()[i]).bits_.flip(j);
                        }
                    }
                }

                for (int i = 0; i < solution.getDecisionVariables().length; i++) {
                    ((Binary) solution.getDecisionVariables()[i]).decode();
                }
            } // if
            else if (solution.getType().getClass() == CloudCDNSolutionf201603Type.class) {
                // First decision variable is integer
                for (int j = 0; j < ((ArrayInt) solution.getDecisionVariables()[0]).getLength(); j++) {
                    if (PseudoRandom.randDouble() < probability) {
                        int value = PseudoRandom.randInt(
                                (int) ((ArrayInt) solution.getDecisionVariables()[0]).getLowerBound(j),
                                (int) ((ArrayInt) solution.getDecisionVariables()[0]).getUpperBound(j));

                        ((ArrayInt) solution.getDecisionVariables()[0]).setValue(j, value);
                    }
                }
                // Seconds decision variable is boolean
                for (int j = 0; j < ((Binary) solution.getDecisionVariables()[1]).getNumberOfBits(); j++) {
                    if (PseudoRandom.randDouble() < probability) {
                        ((Binary) solution.getDecisionVariables()[1]).bits_.flip(j);
                    }
                }
            } else { // Integer representation
                for (int i = 0; i < solution.getDecisionVariables().length; i++) {
                    if (PseudoRandom.randDouble() < probability) {
                        int value = PseudoRandom.randInt(
                                (int) solution.getDecisionVariables()[i].getLowerBound(),
                                (int) solution.getDecisionVariables()[i].getUpperBound());
                        solution.getDecisionVariables()[i].setValue(value);
                    } // if
                }
            } // else
        } catch (ClassCastException e1) {
            Configuration.logger_.severe("BitFlipMutation.doMutation: "
                    + "ClassCastException error" + e1.getMessage());
            Class cls = java.lang.String.class;
            String name = cls.getName();
            throw new JMException("Exception in " + name + ".doMutation()");
        }
    } // doMutation

    /**
     * Executes the operation
     *
     * @param object An object containing a solution to mutate
     * @return An object containing the mutated solution
     * @throws JMException
     */
    public Object execute(Object object) throws JMException {
        Solution solution = (Solution) object;

        if (!VALID_TYPES.contains(solution.getType().getClass())) {
            Configuration.logger_.severe("BitFlipMutation.execute: the solution "
                    + "is not of the right type. The type should be 'Binary', "
                    + "'BinaryReal' or 'Int', but " + solution.getType() + " is obtained");

            Class cls = java.lang.String.class;
            String name = cls.getName();
            throw new JMException("Exception in " + name + ".execute()");
        } // if 

        doMutation(mutationProbability_, solution);
        return solution;
    } // execute
} // BitFlipMutation
