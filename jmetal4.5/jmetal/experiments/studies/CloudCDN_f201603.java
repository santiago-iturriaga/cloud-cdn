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
public class CloudCDN_f201603 extends Experiment {

    private int instance_number;
    private String instance_type;
    private int time_horizon;

    public CloudCDN_f201603(String instance_type, int instance_number, int time_horizon) {
        this.instance_number = instance_number;
        this.instance_type = instance_type;
        this.time_horizon = time_horizon;
    }

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

            //int maxEval = 60000;
            int maxEval = 30000;
            //int maxEval = 10000;
            //int time_horizon = (96 * (60 * 60)); // 96 horas ~ 4 días

            /*
            problemParams = new Object[]{"CloudCDNSolutionf201603Type",
                "/home/santiago/github/cloud-cdn/Instances/",
                "/home/santiago/github/cloud-cdn/Instances/low/data.0/",
                "BestQoS"};
             */
            problemParams = new Object[]{"CloudCDNSolutionf201603Type",
                "../Instances/",
                "../Instances/" + instance_type + "/data." + instance_number + "/",
                "BestQoSSecure",
                time_horizon};

            //problemParams = new Object[] {"CloudCDNSolutionf201603Type", "test/", 0, "BestQoS"};
            //problemParams = new Object[] {"CloudCDNSolutionf201603Type", "test/", 0, "CheapestNetwork"};
            //problemParams = new Object[] {"CloudCDNSolutionf201603Type", "test/", 0, "CheapestComputing"};
            //problemParams = new Object[] {"CloudCDNSolutionf201603Type", "test/", 0, "RoundRobin"};
            algorithm[0] = new jmetal.experiments.settings.cloudcdn.SMSEMOA_f201603_Settings(
                    problemName, maxEval, problemParams).configure();
            //algorithm[1] = new jmetal.experiments.settings.cloudcdn.NSGAII_f201603_Settings(
            //        problemName, maxEval, problemParams).configure();
        } catch (IllegalArgumentException | JMException ex) {
            Logger.getLogger(CloudCDN_f201603.class.getName()).log(
                    Level.SEVERE, null, ex);
        }
    }

    public static void main(String[] args) throws JMException, IOException {
        int inst_number = 0;
        String inst_type = "";
        int time_horizon = 0;

        if (args.length != 3) {
            System.out.println("Error! Parametros incorrectos.");
            System.exit(-1);
        } else {
            inst_type = args[0].trim().toLowerCase();
            inst_number = Integer.parseInt(args[1].trim());
            time_horizon = Integer.parseInt(args[2].trim());
            
            System.out.println("Instance Type  : " + inst_type);
            System.out.println("Instance Number: " + inst_number);
            System.out.println("Time Horizon   : " + time_horizon);
        }

        inst_number = Integer.parseInt(args[1]);

        CloudCDN_f201603 exp = new CloudCDN_f201603(inst_type, inst_number, time_horizon);

        exp.experimentName_ = exp.getClass().getSimpleName() + "_" + inst_type + "_" + inst_number + "_" + time_horizon;
        exp.algorithmNameList_ = new String[]{"SMSEMOA"};
        exp.problemList_ = new String[]{"cloudcdn.f201603.CloudCDN_MP"};
        exp.paretoFrontFile_ = new String[]{"CloudCDN_MP.pf"};
        exp.indicatorList_ = new String[]{"EPSILON", "SPREAD", "HV"};
        exp.experimentBaseDirectory_ = "results/" + exp.experimentName_;
        exp.paretoFrontDirectory_ = "results/data/paretoFronts";
        int numberOfAlgorithms = exp.algorithmNameList_.length;
        exp.algorithmSettings_ = new Settings[numberOfAlgorithms];
        exp.independentRuns_ = 30;
        //exp.independentRuns_ = 2;
        exp.initExperiment();
        
        // Run the experiments
        exp.runExperiment(7);
        //exp.runExperiment(2);

        exp.generateQualityIndicators();

        // Applying Friedman test
        /*Friedman test = new Friedman(exp);
        test.executeTest("EPSILON");
        test.executeTest("HV");
        test.executeTest("SPREAD");*/

        // Generate latex tables
        exp.generateLatexTables();

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
