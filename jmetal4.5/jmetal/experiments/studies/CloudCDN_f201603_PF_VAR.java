/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jmetal.experiments.studies;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
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
public class CloudCDN_f201603_PF_VAR {

    private static final Logger LOG = Logger.getLogger(CloudCDN_f201603_PF_VAR.class.getName());

    public CloudCDN_f201603_PF_VAR() {

    }

    public void differentBulk() throws JMException, IOException {
        String[] basePath = new String[3];
        basePath[0] = "results/low_med_hi_0/CloudCDN_f201603_low_0_86400";
        basePath[1] = "results/low_med_hi_0/CloudCDN_f201603_medium_0_86400";
        basePath[2] = "results/low_med_hi_0/CloudCDN_f201603_high_0_86400";

        String[] algorithm = new String[3];
        algorithm[0] = "SMSEMOA";
        algorithm[1] = "NSGAII";
        algorithm[2] = "MOCHC";

        for (int b = 0; b < 3; b++) {
            System.out.println("===================================");
            System.out.println("=== " + basePath[b]);
            System.out.println("===================================");

            File fout = new File(basePath[b] + "/referenceFronts/cloudcdn.f201603.CloudCDN_MP.var");
            fout.createNewFile();
            FileOutputStream fos = new FileOutputStream(fout);
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));

            Path pfPath;
            pfPath = Paths.get(basePath[b] + "/referenceFronts/cloudcdn.f201603.CloudCDN_MP.rf");

            Stream<String> pf = Files.lines(pfPath);
            Iterator<String> iterPf = pf.iterator();

            int counting;
            counting = 0;

            while (iterPf.hasNext()) {
                String[] partsPf = iterPf.next().trim().split(" ");

                Double costPf, qosPf;
                costPf = Double.parseDouble(partsPf[0]);
                qosPf = Double.parseDouble(partsPf[1]);

                counting++;

                boolean found;
                found = false;

                String bestGuess = null;
                Double bestGuessDiff;
                bestGuessDiff = 0.0;

                for (int a = 0; (a < 3) && (!found); a++) {
                    for (int r = 0; (r < 30) && (!found); r++) {
                        Path funPath;
                        funPath = Paths.get(basePath[b] + "/data/" + algorithm[a] + "/cloudcdn.f201603.CloudCDN_MP/FUN." + r);

                        Stream<String> fun = Files.lines(funPath);
                        Iterator<String> iterFun = fun.iterator();

                        Path varPath;
                        varPath = Paths.get(basePath[b] + "/data/" + algorithm[a] + "/cloudcdn.f201603.CloudCDN_MP/VAR." + r);

                        Stream<String> var = Files.lines(varPath);
                        Iterator<String> iterVar = var.iterator();

                        while (iterFun.hasNext() && !found) {
                            String currentVar;
                            currentVar = iterVar.next();

                            String currentLine;
                            currentLine = iterFun.next();
                            String[] partsFun = currentLine.trim().split(" ");

                            Double costFun, qosFun;
                            costFun = Double.parseDouble(partsFun[0]);
                            qosFun = Double.parseDouble(partsFun[1]);

                            if ((costFun == costPf) && (qosFun == qosPf)) {
                                bestGuess = currentVar;
                                bestGuessDiff = 0.0;
                                found = true;
                            } else if (bestGuess == null) {
                                bestGuess = currentVar;
                                bestGuessDiff = Math.pow(costPf - costFun, 2) + Math.pow(qosPf - qosFun, 2);
                            } else {
                                double diff;
                                diff = Math.pow(costPf - costFun, 2) + Math.pow(qosPf - qosFun, 2);

                                if (diff < bestGuessDiff) {
                                    bestGuess = currentVar;
                                    bestGuessDiff = diff;
                                }
                            }
                        }
                    }
                }

                if (counting % 100 == 0) {
                    System.out.println(counting);
                }

                bw.write(bestGuess);
                bw.newLine();
            }

            bw.close();
        }
    }

    public void differentIntensity() throws JMException, IOException {
        CloudCDN_MP problem;
        Path varFilePath;

        String basePath;
        basePath = "results/low_0_43k_172k/results/CloudCDN_f201603_low_0_";

        int[] timeHorizon = new int[3];
        timeHorizon[0] = 43200;
        timeHorizon[1] = 86400;
        timeHorizon[2] = 172800;

        String[] algorithm = new String[3];
        algorithm[0] = "SMSEMOA";
        algorithm[1] = "NSGAII";
        algorithm[2] = "MOCHC";

        for (int b = 0; b < 3; b++) {
            System.out.println("===================================");
            System.out.println("=== " + timeHorizon[b]);
            System.out.println("===================================");

            String currentBasePath;
            currentBasePath = basePath + timeHorizon[b];
            
            File fout = new File(currentBasePath + "/referenceFronts/cloudcdn.f201603.CloudCDN_MP.var");
            fout.createNewFile();
            FileOutputStream fos = new FileOutputStream(fout);
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));

            Path pfPath;
            pfPath = Paths.get(currentBasePath + "/referenceFronts/cloudcdn.f201603.CloudCDN_MP.rf");

            Stream<String> pf = Files.lines(pfPath);
            Iterator<String> iterPf = pf.iterator();

            int counting;
            counting = 0;

            while (iterPf.hasNext()) {
                String[] partsPf = iterPf.next().trim().split(" ");

                Double costPf, qosPf;
                costPf = Double.parseDouble(partsPf[0]);
                qosPf = Double.parseDouble(partsPf[1]);

                counting++;

                boolean found;
                found = false;

                String bestGuess = null;
                Double bestGuessDiff;
                bestGuessDiff = 0.0;

                for (int a = 0; (a < 3) && (!found); a++) {
                    for (int r = 0; (r < 30) && (!found); r++) {
                        Path funPath;
                        funPath = Paths.get(currentBasePath + "/data/" + algorithm[a] + "/cloudcdn.f201603.CloudCDN_MP/FUN." + r);

                        Stream<String> fun = Files.lines(funPath);
                        Iterator<String> iterFun = fun.iterator();

                        Path varPath;
                        varPath = Paths.get(currentBasePath + "/data/" + algorithm[a] + "/cloudcdn.f201603.CloudCDN_MP/VAR." + r);

                        Stream<String> var = Files.lines(varPath);
                        Iterator<String> iterVar = var.iterator();

                        while (iterFun.hasNext() && !found) {
                            String currentVar;
                            currentVar = iterVar.next();

                            String currentLine;
                            currentLine = iterFun.next();
                            String[] partsFun = currentLine.trim().split(" ");

                            Double costFun, qosFun;
                            costFun = Double.parseDouble(partsFun[0]);
                            qosFun = Double.parseDouble(partsFun[1]);

                            if ((costFun == costPf) && (qosFun == qosPf)) {
                                bestGuess = currentVar;
                                bestGuessDiff = 0.0;
                                found = true;
                            } else if (bestGuess == null) {
                                bestGuess = currentVar;
                                bestGuessDiff = Math.pow(costPf - costFun, 2) + Math.pow(qosPf - qosFun, 2);
                            } else {
                                double diff;
                                diff = Math.pow(costPf - costFun, 2) + Math.pow(qosPf - qosFun, 2);

                                if (diff < bestGuessDiff) {
                                    bestGuess = currentVar;
                                    bestGuessDiff = diff;
                                }
                            }
                        }
                    }
                }

                if (counting % 100 == 0) {
                    System.out.println(counting);
                }

                bw.write(bestGuess);
                bw.newLine();
            }

            bw.close();
        }
    }

    public static void main(String[] args) {
        try {
            CloudCDN_f201603_PF_VAR analyzer;
            analyzer = new CloudCDN_f201603_PF_VAR();

            //analyzer.simple();
            analyzer.differentBulk();
            //analyzer.differentIntensity();
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, ex.getMessage(), ex);
        }
    }
}
