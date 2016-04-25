/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jmetal.experiments.studies;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import jmetal.core.Solution;
import jmetal.core.Variable;
import jmetal.encodings.solutionType.cloudcdn.CloudCDNSolutionf201603Type;
import jmetal.encodings.variable.ArrayInt;
import jmetal.encodings.variable.Binary;
import jmetal.problems.cloudcdn.f201603.CloudCDN_MP;
import jmetal.problems.cloudcdn.f201603.RegionDatacenter;
import jmetal.util.JMException;

/**
 *
 * @author santiago
 */
public class CloudCDN_f201603_AnalyzeSol {

    private static final Logger LOG = Logger.getLogger(CloudCDN_f201603_AnalyzeSol.class.getName());

    public CloudCDN_f201603_AnalyzeSol() {

    }

    public void Analyze(CloudCDN_MP problem, int num_prov, Path varFilePath) throws JMException, IOException {
        CloudCDNSolutionf201603Type solutionTypeCustom_ = problem.solutionTypeCustom_; //new CloudCDNSolutionf201603Type(problem, 25);

        Stream<String> s = Files.lines(varFilePath);
        s.forEach((solString) -> {
            String[] parts = solString.trim().split(" ");

            double qos = 0.0;
            double costTotal = 0.0;
            double sumPartial = 0.0;
            double[] costPartial = new double[num_prov];

            CloudCDN_MP.EvaluateOutput output;
            output = problem.new EvaluateOutput();

            for (int provId = -1; provId < num_prov; provId++) {
                ArrayInt vars0 = new ArrayInt(problem.getLength(0));
                Binary vars1 = new Binary(problem.getLength(1));

                for (int dc = 0; dc < problem.getRegionesDatacenters().size(); dc++) {
                    try {
                        //vars0.setValue(dc, Integer.parseInt(parts[dc]));
                        vars0.setValue(dc, 0);
                    } catch (JMException ex) {
                        Logger.getLogger(CloudCDN_f201603_AnalyzeSol.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

                for (int doc = 0; doc < problem.getDocumentos().size(); doc++) {
                    if ((problem.getDocumentos().get(doc).getProvId() == provId) || (provId == -1)) {
                        for (int dc = 0; dc < problem.getRegionesDatacenters().size(); dc++) {
                            int bitpos;
                            bitpos = solutionTypeCustom_.GetDCDocIndex(
                                    problem.getRegionesDatacenters().size(),
                                    problem.getDocumentos().size(),
                                    dc, doc);

                            char docbit;
                            docbit = parts[problem.getRegionesDatacenters().size() + 1].charAt(bitpos);

                            if (docbit == '1') {
                                vars1.setIth(bitpos, true);
                            } else {
                                vars1.setIth(bitpos, false);
                            }
                        }
                    }
                }

                Variable[] vars = new Variable[2];
                vars[0] = vars0;
                vars[1] = vars1;

                Solution sol;
                sol = new Solution(problem, vars);

                Optional<Integer> optProvId;
                if (provId == -1) {
                    optProvId = Optional.empty();
                } else {
                    optProvId = Optional.of(provId);
                }
                try {
                    if (provId == -1) {
                        output = problem.evaluate(sol, optProvId, true);
                    } else {
                        problem.evaluate(sol, optProvId, false);
                    }
                } catch (JMException ex) {
                    Logger.getLogger(CloudCDN_f201603_AnalyzeSol.class.getName()).log(Level.SEVERE, null, ex);
                }
                double obj1;
                obj1 = sol.getObjective(0);
                double obj2;
                obj2 = sol.getObjective(1);

                if (provId == -1) {
                    costTotal = obj1;
                    qos = obj2;
                } else {
                    costPartial[provId] = obj1;
                    sumPartial += obj1;
                }
            }

            System.out.println(costTotal + " " + sumPartial + " " + (sumPartial - costTotal) / sumPartial + " " + qos
                    + " " + output.NetworkCost + " " + output.StorageCost + " " + output.ComputingCost);
        });
    }

    public void simple() throws JMException, IOException {
        CloudCDN_MP problem;
        Path varFilePath;
        int time_horizon = (12 * (60 * 60)); // 12 horas ~ 0.5 dias

        System.out.println(" === LOW ======================================================= ");
        problem = new CloudCDN_MP("CloudCDNSolutionf201603Type",
                "../Instances/",
                "../Instances/low/data.0/",
                "BestQoS",
                time_horizon);

        varFilePath = Paths.get("results/"
                + "CloudCDNSimpleStudy_f201603_low_0/data/SMSEMOA/cloudcdn.f201603.CloudCDN_MP/VAR.0");

        Analyze(problem, 2, varFilePath);
    }

    public void differentBulk() throws JMException, IOException {
        CloudCDN_MP problem;
        Path varFilePath;
        int time_horizon = (12 * (60 * 60)); // 12 horas ~ 0.5 dias

        System.out.println(" === LOW ======================================================= ");
        problem = new CloudCDN_MP("CloudCDNSolutionf201603Type",
                "../Instances/",
                "../Instances/low/data.0/",
                "BestQoS",
                time_horizon);

        varFilePath = Paths.get("results/"
                + "CloudCDN_f201603_low_0_43200/data/SMSEMOA/cloudcdn.f201603.CloudCDN_MP/VAR.0");

        Analyze(problem, 2, varFilePath);

        System.out.println(" === MED ======================================================= ");
        problem = new CloudCDN_MP("CloudCDNSolutionf201603Type",
                "../Instances/",
                "../Instances/medium/data.0/",
                "BestQoS",
                time_horizon);

        varFilePath = Paths.get("results/"
                + "CloudCDN_f201603_medium_0_43200/data/SMSEMOA/cloudcdn.f201603.CloudCDN_MP/VAR.0");

        Analyze(problem, 4, varFilePath);

        System.out.println(" === HIGH ====================================================== ");
        problem = new CloudCDN_MP("CloudCDNSolutionf201603Type",
                "../Instances/",
                "../Instances/high/data.0/",
                "BestQoS",
                time_horizon);

        varFilePath = Paths.get("results/"
                + "CloudCDN_f201603_high_0_43200/data/SMSEMOA/cloudcdn.f201603.CloudCDN_MP/VAR.0");

        Analyze(problem, 6, varFilePath);
    }

    public void differentIntensity() throws JMException, IOException {
        CloudCDN_MP problem;
        Path varFilePath;

        String basePath = "results/inst_0/results/";
        String[] algorithm = new String[3];
        algorithm[0] = "SMSEMOA";
        algorithm[1] = "NSGAII";
        algorithm[2] = "MOCHC";

        for (int a = 0; a < 3; a++) {
            System.out.println("********************************************************************* ");
            System.out.println("*** " + algorithm[a]);
            System.out.println("********************************************************************* ");
            System.out.println("********************************************************************* ");

            System.out.println(" === LOW_43200 ====================================================== ");
            problem = new CloudCDN_MP("CloudCDNSolutionf201603b25Type",
                    "../Instances/",
                    "../Instances/low/data.0/",
                    "BestQoS",
                    43200);

            varFilePath = Paths.get(basePath
                    + "CloudCDN_f201603_low_0_43200/data/" + algorithm[a] + "/cloudcdn.f201603.CloudCDN_MP/VAR.0");

            Analyze(problem, 2, varFilePath);

            System.out.println(" === LOW_86400 ======================================================= ");
            problem = new CloudCDN_MP("CloudCDNSolutionf201603b25Type",
                    "../Instances/",
                    "../Instances/low/data.0/",
                    "BestQoS",
                    86400);

            varFilePath = Paths.get(basePath
                    + "CloudCDN_f201603_low_0_86400/data/" + algorithm[a] + "/cloudcdn.f201603.CloudCDN_MP/VAR.0");

            Analyze(problem, 2, varFilePath);

            System.out.println(" === LOW_172800 ======================================================= ");
            problem = new CloudCDN_MP("CloudCDNSolutionf201603b25Type",
                    "../Instances/",
                    "../Instances/low/data.0/",
                    "BestQoS",
                    172800);

            varFilePath = Paths.get(basePath
                    + "CloudCDN_f201603_low_0_172800/data/" + algorithm[a] + "/cloudcdn.f201603.CloudCDN_MP/VAR.0");

            Analyze(problem, 2, varFilePath);
        }
    }

    public static void main(String[] args) {
        try {
            CloudCDN_f201603_AnalyzeSol analyzer;
            analyzer = new CloudCDN_f201603_AnalyzeSol();

            //analyzer.simple();
            //analyzer.differentBulk();
            analyzer.differentIntensity();
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, ex.getMessage(), ex);
        }
    }
}
