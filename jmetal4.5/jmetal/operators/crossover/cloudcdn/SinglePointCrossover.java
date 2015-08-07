//  SinglePointCrossover.java
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
import jmetal.encodings.solutionType.cloudcdn.CloudCDNRRSolutionType;
import jmetal.encodings.variable.ArrayInt;
import jmetal.operators.crossover.Crossover;
import jmetal.util.Configuration;
import jmetal.util.JMException;
import jmetal.util.PseudoRandom;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * This class allows to apply a Single Point crossover operator using two parent
 * solutions.
 */
public class SinglePointCrossover extends Crossover {
	/**
	 * Valid solution types to apply this operator
	 */
	private static final List VALID_TYPES = Arrays
			.asList(CloudCDNRRSolutionType.class);

	private Double crossoverProbability_ = null;

	/**
	 * Constructor Creates a new instance of the single point crossover operator
	 */
	public SinglePointCrossover(HashMap<String, Object> parameters) {
		super(parameters);
		if (parameters.get("probability") != null)
			crossoverProbability_ = (Double) parameters.get("probability");
	} // SinglePointCrossover

	/**
	 * Constructor Creates a new instance of the single point crossover operator
	 */
	// public SinglePointCrossover(Properties properties) {
	// this();
	// } // SinglePointCrossover

	/**
	 * Perform the crossover operation.
	 * 
	 * @param probability
	 *            Crossover probability
	 * @param parent1
	 *            The first parent
	 * @param parent2
	 *            The second parent
	 * @return An array containig the two offsprings
	 * @throws JMException
	 */
	public Solution[] doCrossover(double probability, Solution parent1,
			Solution parent2) throws JMException {
		
		Solution[] offSpring = new Solution[2];
		offSpring[0] = new Solution(parent1);
		offSpring[1] = new Solution(parent2);
		
		try {
			if (PseudoRandom.randDouble() < probability) {
				int crossoverPoint = PseudoRandom.randInt(0,
						parent1.numberOfVariables() - 1);

				int valueX1;
				int valueX2;

				ArrayInt arrayX1;
				ArrayInt arrayX2;

				for (int i = crossoverPoint; i < parent1
						.numberOfVariables(); i++) {

					arrayX1 = (ArrayInt) parent1.getDecisionVariables()[i];
					arrayX2 = (ArrayInt) parent2.getDecisionVariables()[i];

					for (int j = 0; j < arrayX1.getLength(); j++) {
						valueX1 = (int) arrayX1.getValue(j);
						valueX2 = (int) arrayX2.getValue(j);
						
						((ArrayInt) offSpring[0].getDecisionVariables()[i])
								.setValue(j, valueX2);
						((ArrayInt) offSpring[1].getDecisionVariables()[i])
								.setValue(j, valueX1);
					}
				}
			}
		} catch (ClassCastException e1) {
			Configuration.logger_
					.severe("SinglePointCrossover.doCrossover: Cannot perfom "
							+ "SinglePointCrossover");
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
	 * @return An object containing an array with the offSprings
	 * @throws JMException
	 */
	public Object execute(Object object) throws JMException {
		Solution[] parents = (Solution[]) object;

		if (!(VALID_TYPES.contains(parents[0].getType().getClass()) && VALID_TYPES
				.contains(parents[1].getType().getClass()))) {

			Configuration.logger_
					.severe("SinglePointCrossover.execute: the solutions "
							+ "are not of the right type. The type should be 'Binary' or 'Int', but "
							+ parents[0].getType() + " and "
							+ parents[1].getType() + " are obtained");

			Class cls = java.lang.String.class;
			String name = cls.getName();
			throw new JMException("Exception in " + name + ".execute()");
		} // if

		if (parents.length < 2) {
			Configuration.logger_
					.severe("SinglePointCrossover.execute: operator "
							+ "needs two parents");
			Class cls = java.lang.String.class;
			String name = cls.getName();
			throw new JMException("Exception in " + name + ".execute()");
		}

		Solution[] offSpring;
		offSpring = doCrossover(crossoverProbability_, parents[0], parents[1]);

		// -> Update the offSpring solutions
		for (int i = 0; i < offSpring.length; i++) {
			offSpring[i].setCrowdingDistance(0.0);
			offSpring[i].setRank(0);
		}
		return offSpring;
	} // execute
} // SinglePointCrossover
