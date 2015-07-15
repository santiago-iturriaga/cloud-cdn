//  HUXCrossover.java
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

package jmetal.operators.crossover.cloudcdn;

import jmetal.core.Solution;
import jmetal.encodings.solutionType.BinaryRealSolutionType;
import jmetal.encodings.solutionType.BinarySolutionType;
import jmetal.encodings.solutionType.cloudcdn.CloudCDNSolutionType;
import jmetal.encodings.variable.ArrayInt;
import jmetal.encodings.variable.Binary;
import jmetal.operators.crossover.Crossover;
import jmetal.util.Configuration;
import jmetal.util.JMException;
import jmetal.util.PseudoRandom;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * This class allows to apply a HUX crossover operator using two parent
 * solutions. NOTE: the operator is applied to the first encodings.variable of
 * the solutions, and the type of the solutions must be Binary or BinaryReal
 */
public class HUXCrossover extends Crossover {
	/**
	 * Valid solution types to apply this operator
	 */
	private static final List VALID_TYPES = Arrays.asList(CloudCDNSolutionType.class);

	private Double probability_ = null;

	/**
	 * Constructor Create a new instance of the HUX crossover operator.
	 */
	public HUXCrossover(HashMap<String, Object> parameters) {
		super(parameters);

		if (parameters.get("probability") != null)
			probability_ = (Double) parameters.get("probability");
	} // HUXCrossover

	/**
	 * Constructor Create a new intance of the HUX crossover operator.
	 */
	// public HUXCrossover(Properties properties) {
	// this();
	// } // HUXCrossover

	/**
	 * Perform the crossover operation
	 * 
	 * @param probability
	 *            Crossover probability
	 * @param parent1
	 *            The first parent
	 * @param parent2
	 *            The second parent
	 * @return An array containing the two offsprings
	 * @throws JMException
	 */
	public Solution[] doCrossover(double probability, Solution parent1,
			Solution parent2) throws JMException {
		
		Solution[] offSpring = new Solution[2];
		offSpring[0] = new Solution(parent1);
		offSpring[1] = new Solution(parent2);
		
		try {
			if (PseudoRandom.randDouble() < probability) {
				for (int var = 0; var < parent1.getDecisionVariables().length; var++) {
					ArrayInt p1 = (ArrayInt) parent1.getDecisionVariables()[var];
					ArrayInt p2 = (ArrayInt) parent2.getDecisionVariables()[var];

					for (int index = 0; index < p1.getLength(); index++) {
						if (p1.getValue(index) != p2.getValue(index)) {
							if (PseudoRandom.randDouble() < 0.5) {
								((ArrayInt) offSpring[0].getDecisionVariables()[var]).setValue(index, p2.getValue(index));
								((ArrayInt) offSpring[1].getDecisionVariables()[var]).setValue(index, p1.getValue(index));
							}
						}
					}
				}
			}
		} catch (ClassCastException e1) {
			Configuration.logger_
					.severe("HUXCrossover.doCrossover: Cannot perfom "
							+ "SinglePointCrossover ");
			Class cls = java.lang.String.class;
			String name = cls.getName();
			throw new JMException("Exception in " + name + ".doCrossover()");
		}
		return offSpring;
	} // doCrossover

	/**
	 * Executes the operation
	 * 
	 * @param object
	 *            An object containing an array of two solutions
	 * @return An object containing the offSprings
	 */
	public Object execute(Object object) throws JMException {
		Solution[] parents = (Solution[]) object;

		if (parents.length < 2) {
			Configuration.logger_
					.severe("HUXCrossover.execute: operator needs two "
							+ "parents");
			Class cls = java.lang.String.class;
			String name = cls.getName();
			throw new JMException("Exception in " + name + ".execute()");
		}

		if (!(VALID_TYPES.contains(parents[0].getType().getClass()) && VALID_TYPES
				.contains(parents[1].getType().getClass()))) {

			Configuration.logger_
					.severe("HUXCrossover.execute: the solutions "
							+ "are not of the right type. The type should be 'Binary' of "
							+ "'BinaryReal', but " + parents[0].getType()
							+ " and " + parents[1].getType() + " are obtained");

			Class cls = java.lang.String.class;
			String name = cls.getName();
			throw new JMException("Exception in " + name + ".execute()");

		} // if

		Solution[] offSpring = doCrossover(probability_, parents[0], parents[1]);

		for (int i = 0; i < offSpring.length; i++) {
			offSpring[i].setCrowdingDistance(0.0);
			offSpring[i].setRank(0);
		}

		return offSpring;
	} // execute
} // HUXCrossover
