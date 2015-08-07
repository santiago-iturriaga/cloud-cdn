//  ConstrainedProblemsStudy.java
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

package jmetal.experiments.studies;

import jmetal.core.Algorithm;
import jmetal.experiments.Experiment;
import jmetal.experiments.Settings;
import jmetal.util.JMException;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Example of experiment. In particular four algorithms are compared when
 * solving four constrained problems.
 */
public class CloudCDNRRCheapestStudy extends Experiment {

	/**
	 * Configures the algorithms in each independent run
	 * 
	 * @param problemName
	 *            The problem to solve
	 * @param problemIndex
	 * @throws ClassNotFoundException
	 */
	public void algorithmSettings(String problemName, int problemIndex,
			Algorithm[] algorithm) throws ClassNotFoundException {
		try {
			Object[] problemParams = { "CloudCDNRRSolutionType", "test/", 0,
					"RRCheapest", false };
			algorithm[0] = new jmetal.experiments.settings.cloudcdn.gGA_Settings(
					problemName, problemParams).configure();
		} catch (IllegalArgumentException ex) {
			Logger.getLogger(CloudCDNRRCheapestStudy.class.getName()).log(Level.SEVERE,
					null, ex);
		} catch (JMException ex) {
			Logger.getLogger(CloudCDNRRCheapestStudy.class.getName()).log(Level.SEVERE,
					null, ex);
		}
	}

	public static void main(String[] args) throws JMException, IOException {
		CloudCDNRRCheapestStudy exp = new CloudCDNRRCheapestStudy();

		//exp.experimentName_ = "CloudCDNStudy";
		exp.experimentName_ = exp.getClass().getSimpleName();

		// exp.algorithmNameList_ = new String[] { "ElitistES" };
		exp.algorithmNameList_ = new String[] { "gGA" };

		exp.problemList_ = new String[] { "cloudcdn.CloudCDN_RRSO" };
		exp.paretoFrontFile_ = new String[] { "CloudCDN_RRSO.pf" };

		exp.indicatorList_ = new String[] {};
		// exp.indicatorList_ = new String[] { "EPSILON", "SPREAD", "HV" };

		int numberOfAlgorithms = exp.algorithmNameList_.length;

		exp.experimentBaseDirectory_ = "/home/siturria/github/cloud-cdn/jmetal4.5/results/"
				+ exp.experimentName_;
		exp.paretoFrontDirectory_ = "/home/siturria/github/cloud-cdn/jmetal4.5/results/data/paretoFronts";
		exp.algorithmSettings_ = new Settings[numberOfAlgorithms];
		exp.independentRuns_ = 1;

		exp.initExperiment();

		// Run the experiments
		int numberOfThreads;
		exp.runExperiment(numberOfThreads = 1);
		// exp.runExperiment(numberOfThreads = 4);

		exp.generateQualityIndicators();

		// Applying Friedman test
		/*
		 * Friedman test = new Friedman(exp); test.executeTest("EPSILON");
		 * test.executeTest("HV"); test.executeTest("SPREAD");
		 */

		// Generate latex tables
		exp.generateLatexTables();

		// Configure the R scripts to be generated
		int rows;
		int columns;
		String prefix;
		String[] problems;
		boolean notch;

		// Configuring scripts for ZDT
		/*
		 * rows = 2; columns = 2; prefix = new String("Constrained"); problems =
		 * new String[] { "CloudCDN_SO" }; exp.generateRBoxplotScripts(rows,
		 * columns, problems, prefix, notch = false, exp);
		 * exp.generateRWilcoxonScripts(problems, prefix, exp);
		 */
	}
}
