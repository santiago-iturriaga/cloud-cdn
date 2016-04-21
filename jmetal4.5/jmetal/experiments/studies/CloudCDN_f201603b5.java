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
public class CloudCDN_f201603b5 extends Experiment {

    private int instance_number;
    private String instance_type;
    private int time_horizon;
    private int max_evals;

    public CloudCDN_f201603b5(String instance_type, int instance_number, int time_horizon,
            int max_evals) {
        
        this.instance_number = instance_number;
        this.instance_type = instance_type;
        this.time_horizon = time_horizon;
        this.max_evals = max_evals;
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

            problemParams = new Object[]{"CloudCDNSolutionf201603b5Type",
                "../Instances/",
                "../Instances/" + instance_type + "/data." + instance_number + "/",
                "BestQoSSecure",
                time_horizon};

            algorithm[0] = new jmetal.experiments.settings.cloudcdn.SMSEMOA_f201603_Settings(
                    problemName, max_evals, false, problemParams).configure();
        } catch (IllegalArgumentException | JMException ex) {
            Logger.getLogger(CloudCDN_f201603b5.class.getName()).log(
                    Level.SEVERE, null, ex);
        }
    }

    public static void main(String[] args) throws JMException, IOException {
        int inst_number = 0;
        String inst_type = "";
        int time_horizon = 0;
        int num_threads = 1;
        int max_evals = 80000;
        int num_exec = 30;

        if (args.length != 6) {
            System.out.println("Error! Parametros incorrectos.");
            System.exit(-1);
        } else {
            inst_type = args[0].trim().toLowerCase();
            inst_number = Integer.parseInt(args[1].trim());
            time_horizon = Integer.parseInt(args[2].trim());
            num_threads = Integer.parseInt(args[3].trim());
            max_evals = Integer.parseInt(args[4].trim());
            num_exec = Integer.parseInt(args[5].trim());

            System.out.println("Instance Type  : " + inst_type);
            System.out.println("Instance Number: " + inst_number);
            System.out.println("Time Horizon   : " + time_horizon);
            System.out.println("Num Threads    : " + num_threads);
            System.out.println("Num Evaluations: " + max_evals);
            System.out.println("Num Executions : " + num_exec);

            /*
            Time horizon table:
            -------------------
            12 horas = (12 * (60 * 60)) = 43200
            1 día = 24 horas = (24 * (60 * 60)) = 86400
            2 días = 48 horas = (48 * (60 * 60)) = 172800
            3 días = 72 horas = (72 * (60 * 60)) = 259200
            4 días = 96 horas = (96 * (60 * 60)) = 345600
             */
        }

        CloudCDN_f201603b5 exp = new CloudCDN_f201603b5(inst_type, inst_number, time_horizon, max_evals);

        exp.experimentName_ = exp.getClass().getSimpleName();
        exp.algorithmNameList_ = new String[]{"SMSEMOA"};
        exp.problemList_ = new String[]{"cloudcdn.f201603.CloudCDN_MP"};
        exp.paretoFrontFile_ = new String[]{"CloudCDN_MP.pf"};
        exp.indicatorList_ = new String[]{"EPSILON", "SPREAD", "HV"};
        exp.experimentBaseDirectory_ = "results/" + exp.experimentName_;
        exp.paretoFrontDirectory_ = "results/data/paretoFronts";
        int numberOfAlgorithms = exp.algorithmNameList_.length;
        exp.algorithmSettings_ = new Settings[numberOfAlgorithms];
        exp.independentRuns_ = num_exec;
        exp.initExperiment();

        // Run the experiments
        exp.runExperiment(num_threads);

        exp.generateQualityIndicators();

        // Applying Friedman test
        //Friedman test = new Friedman(exp);
        //test.executeTest("EPSILON");
        //test.executeTest("HV");
        //test.executeTest("SPREAD");

        // Generate latex tables
        //exp.generateLatexTables();

        // Configure the R scripts to be generated
        //int rows;
        //int columns;
        //String prefix;
        //String[] problems;

        //rows = 2;
        //columns = 2;
        //prefix = exp.experimentName_;
        //problems = new String[]{"CloudCDN_MO"};
        //exp.generateRBoxplotScripts(rows, columns, problems, prefix, false, exp);
        //exp.generateRWilcoxonScripts(problems, prefix, exp);
    }
}
