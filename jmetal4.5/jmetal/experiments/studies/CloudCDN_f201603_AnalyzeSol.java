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
    private final int NUM_PROVIDERS = 2;
    //private final int NUM_PROVIDERS = 4;

    public CloudCDN_f201603_AnalyzeSol() {

    }

    public void Analyze() throws JMException, IOException {
        CloudCDN_MP problem;
  
        problem = new CloudCDN_MP("CloudCDNSolutionf201603Type",
                "Instances/",
                "Instances/low/data.0/",
                "BestQoS");

        Path varFilePath = Paths.get("jmetal4.5/results/"
                + "CloudCDN_low_0_f201603/data/SMSEMOA/cloudcdn.f201603.CloudCDN_MP/VAR.0");

        /*
        problem = new CloudCDN_MP("CloudCDNSolutionf201603Type",
                "Instances/",
                "Instances/medium/data.0/",
                "BestQoS");

        Path varFilePath = Paths.get("jmetal4.5/results/"
                + "CloudCDN_med_0_f201603/data/SMSEMOA/cloudcdn.f201603.CloudCDN_MP/VAR.0");
        */
        Stream<String> s = Files.lines(varFilePath);

        s.forEach((solString) -> {
            String[] parts = solString.trim().split(" ");

            double qos = 0.0;
            double costTotal = 0.0;
            double sumPartial = 0.0;
            double[] costPartial = new double[NUM_PROVIDERS];
            
            CloudCDN_MP.EvaluateOutput output;
            output = problem.new EvaluateOutput();

            for (int provId = -1; provId < NUM_PROVIDERS; provId++) {
                ArrayInt vars0 = new ArrayInt(problem.getLength(0));
                Binary vars1 = new Binary(problem.getLength(1));

                for (int dc = 0; dc < problem.getRegionesDatacenters().size(); dc++) {
                    try {
                        vars0.setValue(dc, Integer.parseInt(parts[dc]));
                    } catch (JMException ex) {
                        Logger.getLogger(CloudCDN_f201603_AnalyzeSol.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

                for (int doc = 0; doc < problem.getDocumentos().size(); doc++) {
                    if ((problem.getDocumentos().get(doc).getProvId() == provId) || (provId == -1)) {
                        for (int dc = 0; dc < problem.getRegionesDatacenters().size(); dc++) {
                            int bitpos;
                            bitpos = CloudCDNSolutionf201603Type.GetDCDocIndex(
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

            System.out.println(costTotal + " " + sumPartial + " " + (sumPartial - costTotal) / sumPartial + " " + qos + 
                    " " + output.NetworkCost + " " + output.StorageCost + " " + output.ComputingCost);
        });
    }

    public static void main(String[] args) {
        try {
            CloudCDN_f201603_AnalyzeSol analyzer;
            analyzer = new CloudCDN_f201603_AnalyzeSol();

            analyzer.Analyze();
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, ex.getMessage(), ex);
        }
    }
}
