package jmetal.experiments.studies;

import jmetal.core.Algorithm;
import jmetal.experiments.Experiment;
import jmetal.experiments.Settings;
import jmetal.util.JMException;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import jmetal.experiments.util.Friedman;

/**
 * Example of experiment. In particular four algorithms are compared when
 * solving four constrained problems.
 */
public class CloudCDNSimpleStudy_f201603 extends Experiment {

    /**
     * Configures the algorithms in each independent run
     *
     * @param problemName The problem to solve
     * @param problemIndex
     * @throws ClassNotFoundException
     */
    @Override
    public void algorithmSettings(String problemName, int problemIndex,
            Algorithm[] algorithm) throws ClassNotFoundException {
        try {
            Object[] problemParams;

            int maxEval = 40000;
            
            //problemParams = new Object[]{"CloudCDNSolutionf201603Type",
            //    "/home/santiago/github/cloud-cdn/Instances/",
            //    "/home/santiago/github/cloud-cdn/Instances/low/data.0/",
            //    "BestQoS"};
            
            problemParams = new Object[]{"CloudCDNSolutionf201603Type",
                "Instances/",
                "Instances/low/data.0/",
                "BestQoS"};

            //problemParams = new Object[] {"CloudCDNSolutionf201603Type", "test/", 0, "BestQoS"};
            //problemParams = new Object[] {"CloudCDNSolutionf201603Type", "test/", 0, "CheapestNetwork"};
            //problemParams = new Object[] {"CloudCDNSolutionf201603Type", "test/", 0, "CheapestComputing"};
            //problemParams = new Object[] {"CloudCDNSolutionf201603Type", "test/", 0, "RoundRobin"};
            algorithm[0] = new jmetal.experiments.settings.cloudcdn.SMSEMOA_f201603_Settings(
                    problemName, maxEval, problemParams).configure();
            //algorithm[1] = new jmetal.experiments.settings.cloudcdn.NSGAII_f201603_Settings(
            //        problemName, maxEval, problemParams).configure();
        } catch (IllegalArgumentException | JMException ex) {
            Logger.getLogger(CloudCDNSimpleStudy_f201603.class.getName()).log(
                    Level.SEVERE, null, ex);
        }
    }

    public static void main(String[] args) throws JMException, IOException {
        CloudCDNSimpleStudy_f201603 exp = new CloudCDNSimpleStudy_f201603();

        // exp.experimentName_ = "CloudCDNStudy";
        exp.experimentName_ = exp.getClass().getSimpleName() + "_low_0";

        exp.algorithmNameList_ = new String[]{"SMSEMOA"};
        //exp.algorithmNameList_ = new String[]{"NSGAII"};
        //exp.algorithmNameList_ = new String[]{"SMSEMOA", "NSGAII"};

        exp.problemList_ = new String[]{"cloudcdn.f201603.CloudCDN_MP"};
        exp.paretoFrontFile_ = new String[]{"CloudCDN_MP.pf"};

        //exp.indicatorList_ = new String[]{};
        exp.indicatorList_ = new String[]{"EPSILON", "SPREAD", "HV"};

        int numberOfAlgorithms = exp.algorithmNameList_.length;

        //exp.experimentBaseDirectory_ = "/home/siturria/github/cloud-cdn/jmetal4.5/results/" + exp.experimentName_;
        //exp.paretoFrontDirectory_ = "/home/siturria/github/cloud-cdn/jmetal4.5/results/data/paretoFronts";

        exp.experimentBaseDirectory_ = "jmetal4.5/results/" + exp.experimentName_;
        exp.paretoFrontDirectory_ = "jmetal4.5/results/data/paretoFronts";

        exp.algorithmSettings_ = new Settings[numberOfAlgorithms];
        exp.independentRuns_ = 2;

        exp.initExperiment();

        // Run the experiments
        //int numberOfThreads;
        exp.runExperiment(2);
        // exp.runExperiment(numberOfThreads = 4);

        exp.generateQualityIndicators();

        // Applying Friedman test
        /*Friedman test = new Friedman(exp);
        test.executeTest("EPSILON");
        test.executeTest("HV");
        test.executeTest("SPREAD");*/

        // Generate latex tables
        //exp.generateLatexTables();

        // Configure the R scripts to be generated
        /*int rows;
        int columns;
        String prefix;
        String[] problems;

        rows = 2;
        columns = 2;
        prefix = exp.experimentName_;
        problems = new String[]{"CloudCDN_MO"};
        exp.generateRBoxplotScripts(rows, columns, problems, prefix, false, exp);
        exp.generateRWilcoxonScripts(problems, prefix, exp);*/
    }
}
