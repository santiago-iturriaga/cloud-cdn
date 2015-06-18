/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package globetraff;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.FileWriter;
import java.io.BufferedWriter;

/**
 *
 * @author mmlab
 */
public class GenerateThread extends Thread {

    public static final int PROWGEN     = 0;
    public static final int FREQSIZE    = 1;
    public static final int LRUSTACK    = 2;

    Runtime r;

    String dataDir;             // argv[2]);
    double web_redundancy;     //  = atoi(argv[3]);
    int oneTimerPerc;         //= atoi(argv[4]);
    double webZipfSlope;         // = atof(argv[5])
    double paretoTailIndex;      // = atof(argv[6]);
    double correlation;          // = atof(argv[7]);
    int stacksize;              // = atoi(argv[8]);
    int stackmode;              // = atoi(argv[9]);
    int percAtTail;           // = atof(argv[10]);
    int K;                    // =  atof(argv[11]);
    int mean;                 // =  atof(argv[12]);
    int std;                  // = atof(argv[13]);
    double initMZSlope;          // = atof(argv[14]);
    int MZplateau;              // = atoi(argv[15]);
    double tracesTau;            // = atof(argv[16]);
    double tracesLamda;          // = atof(argv[17]);
    int torrentInterarrival;        // =  atof(argv[18]);
    double videoZipfSlope;       // =  atof(argv[19]);
    double weibullK;             // = atof(argv[20]);
    double weibullL;             // = atof(argv[21]);
    double gammaK;               //   = atof(argv[22]);
    double gamma8;               //   = atof(argv[23]);
    double alpha;                //    = atof(argv[24]);
    double alphaBirth;           // = atof(argv[25]);
    int workloadSize;           // = atoi(argv[27])*pow(1024,3);
    double web_perc;             // = atof(argv[28]);
    double p2p_perc;             // = atof(argv[29]);
    double video_perc;           // = atof(argv[30]);
    double other_perc;           // = atof(argv[31]);
    double p2p_redundancy;           // = atof(argv[32]);
    double video_redundancy;           // = atof(argv[33]);
    double other_redundancy;           // = atof(argv[34]);
    double other_size;
    double pop_bias;
    int video_pop_distr;        // = arg[35]
    double otherZipfSlope;
    double fixedP2PObjectSize;
    
    @Override
    public void run() 
    {
        r = Runtime.getRuntime();
        String  dir = exec("pwd")+"/";

        //System.out.println(dir);

        //System.out.println("Preparing generator...");
        //exec("../setup");

        exec("./cleanup");

        System.out.println("Generating workload...");

        //exec("../ProWGen 0 \"/home/mmlab/GlobeTraff/JavaGUI\" 0.70 70 0.75 1.19 0.0 100 1 7 9300 9357 1318 0.6 20 87.74 1.16 3807 0.66 0.51 6010.0 0.37 23910.0 0.703 2.0164 5 0.35 0.16 0.2 0.29 0.5 0.5 0.0 1.0");
        System.out.println(prepareCommand(PROWGEN, dir));
        exec(prepareCommand(PROWGEN, dir));
        

        System.out.println(prepareCommand(FREQSIZE, dir));
        //exec(prepareCommand(FREQSIZE, dir)); //exec("./freqsize < data/default.web > data/docs.web");
        exec("./freq");

        System.out.println(prepareCommand(LRUSTACK, dir));
        writeToFile("lrustack",prepareCommand(LRUSTACK, dir)); //exec("./lrustack \"stack.dat\" 0.20 100 30 30 1 > data/default.tmp");
        exec("./lrustack");

        exec("./postprocess");

    }

    public String prepareCommand(int command_type, String dir)
    {
        String command = "";
        
        switch (command_type) {
            case PROWGEN:
            {
                 command = "../ProWGen 0 "+ dir + " " +
                            web_redundancy + " " +
                            oneTimerPerc + " " +
                            webZipfSlope + " " +
                            paretoTailIndex + " " +
                            correlation + " " +
                            stacksize + " " +
                            stackmode + " " +
                            percAtTail + " " +
                            K + " " +
                            mean + " " +
                            std + " " +
                            initMZSlope + " " +
                            MZplateau + " " +
                            tracesTau + " " +
                            tracesLamda + " " +
                            torrentInterarrival + " " +
                            videoZipfSlope + " " +
                            weibullK + " " +
                            weibullL + " " +
                            gammaK + " " +
                            gamma8 + " " +
                            alpha + " " +
                            alphaBirth + " " +
                            workloadSize + " " +
                            web_perc + " " +
                            p2p_perc + " " +
                            video_perc + " " +
                            other_perc + " " +
                            p2p_redundancy + " " +
                            video_redundancy + " " +
                            other_redundancy + " " +
                            other_size + " " +
                            video_pop_distr+ " "+
                            otherZipfSlope+ " "+
                            fixedP2PObjectSize;
                break;
            }
            case FREQSIZE:
            {
                command = "../freqsize < data/workload.web > data/docs.web";

                break;
            }
            case LRUSTACK:
            {
                //dir, pop_bias, stack_size, num_requests, num_docs,
                command = "../lrustack " + dir +" "+
                          pop_bias + " " +
                          stacksize + " " +
                          exec("./numWebRequests") + " " +
                          exec("./numWebDocs") + " " +
                          this.stackmode  + " " +
                          "stack.dat > data/workload.tmp";
                break;
            }
            default:
            {
                System.err.println("Invalid command");
                System.exit(1);
                break;
            }

        }

        return command;
    }


    private String exec(String command)
    {
        System.out.println("Executing: "+ command);
         String result = "";
         try {

              Process p = r.exec(command);
              InputStream in = p.getInputStream();
              BufferedInputStream buf = new BufferedInputStream(in);
              InputStreamReader inread = new InputStreamReader(buf);
              BufferedReader bufferedreader = new BufferedReader(inread);

              // Read the command output...
              String line;
              while ((line = bufferedreader.readLine()) != null) {
                  System.out.println(line);
                  result = result + line;
              }
              // Check for failure

              try {

                  if (p.waitFor() != 0) {
                      System.err.println("Exit value = " + p.exitValue());

                      //---------------------------

                      InputStream inn = p.getErrorStream();
                      BufferedInputStream buff = new BufferedInputStream(inn);
                      InputStreamReader inreadd = new InputStreamReader(buff);
                      BufferedReader bufferedreaderr = new BufferedReader(inreadd);

                      // Read the command output...
                      String linee;
                      while ((linee = bufferedreaderr.readLine()) != null) {
                          System.out.println(linee);
                      }

                      //---------------------------
                  }
              } catch (InterruptedException e) {
                  System.err.println(e);
              } finally {
                  // Close the InputStream
                  bufferedreader.close();
                  inread.close();
                  buf.close();
                  in.close();
              }
          } catch (IOException e) {
              System.err.println(e.getMessage());
          }

        return result;
    }

      private void execc(String command)
    {
        System.out.println("Executing: "+ command);
         try {

              Process p = r.exec(command);
              InputStream in = p.getInputStream();
              BufferedInputStream buf = new BufferedInputStream(in);
              InputStreamReader inread = new InputStreamReader(buf);
              BufferedReader bufferedreader = new BufferedReader(inread);

              // Read the command output...
              String line;
              while ((line = bufferedreader.readLine()) != null) {
                  System.out.println(line);
              }
              // Check for failure

              try {

                  if (p.waitFor() != 0) {
                      System.err.println("Exit value = " + p.exitValue());

                      //---------------------------

                      InputStream inn = p.getErrorStream();
                      BufferedInputStream buff = new BufferedInputStream(inn);
                      InputStreamReader inreadd = new InputStreamReader(buff);
                      BufferedReader bufferedreaderr = new BufferedReader(inreadd);

                      // Read the command output...
                      String linee;
                      while ((linee = bufferedreaderr.readLine()) != null) {
                          System.out.println(linee);
                      }

                      //---------------------------
                  }
              } catch (InterruptedException e) {
                  System.err.println(e);
              } finally {
                  // Close the InputStream
                  bufferedreader.close();
                  inread.close();
                  buf.close();
                  in.close();
              }
          } catch (IOException e) {
              System.err.println(e.getMessage());
          }
    }

    public void setParameters(  
                                double web_redundancy_val,
                                int oneTimerPerc_val,
                                double webZipfSlope_val,
                                double paretoTailIndex_val,
                                double correlation_val,
                                int stacksize_val,
                                int stackmode_val,
                                int percAtTail_val,
                                int K_val,
                                int mean_val,
                                int std_val,
                                double initMZSlope_val,
                                int MZplateau_val,
                                double tracesTau_val,
                                double tracesLamda_val,
                                int torrentInterarrival_val,
                                double videoZipfSlope_val,
                                double weibullK_val,
                                double weibullL_val,
                                double gammaK_val,
                                double gamma8_val,
                                double alpha_val,
                                double alphaBirth_val,
                                int workloadSize_val,
                                double web_perc_val,
                                double p2p_perc_val,
                                double video_perc_val,
                                double other_perc_val,
                                double p2p_redundancy_val,
                                double video_redundancy_val,
                                double other_redundancy_val,
                                double other_size_val,
                                double pop_bias_val,
                                int video_pop_distr_val,
                                double otherZipfSlope_val,
                                double fixedP2PObjectSize_val)
    {

        web_redundancy   = web_redundancy_val;
        oneTimerPerc	 = oneTimerPerc_val;
        webZipfSlope	 = webZipfSlope_val;
        paretoTailIndex	 = paretoTailIndex_val;
        correlation	 = correlation_val;
        stacksize	 = stacksize_val;
        stackmode	 = stackmode_val;
        percAtTail	 = percAtTail_val;
        K                = K_val;
        mean             = mean_val;
        std              = std_val;
        initMZSlope	 = initMZSlope_val;
        MZplateau	 = MZplateau_val;
        tracesTau	 = tracesTau_val;
        tracesLamda	 = tracesLamda_val;
        torrentInterarrival	 = torrentInterarrival_val;
        videoZipfSlope	 = videoZipfSlope_val;
        weibullK	 = weibullK_val;
        weibullL	 = weibullL_val;
        gammaK           = gammaK_val;
        gamma8           = gamma8_val;
        alpha            = alpha_val;
        alphaBirth	 = alphaBirth_val;
        workloadSize	 = workloadSize_val;
        web_perc	 = web_perc_val;
        p2p_perc	 = p2p_perc_val;
        video_perc	 = video_perc_val;
        other_perc	 = other_perc_val;
        p2p_redundancy	 = p2p_redundancy_val;
        video_redundancy = video_redundancy_val;
        other_redundancy = other_redundancy_val;
        other_size       = other_size_val;
        pop_bias         = pop_bias_val;
        video_pop_distr  = video_pop_distr_val;
        otherZipfSlope   = otherZipfSlope_val;
        fixedP2PObjectSize = fixedP2PObjectSize_val;
    }


  private static void writeToFile(String file, String content)
  {
      try{
          // Create file
          FileWriter fstream = new FileWriter(file);
          BufferedWriter out = new BufferedWriter(fstream);
          out.write(content);
          //Close the output stream
          out.close();
      }catch (Exception e){//Catch exception if any
      System.err.println("Error: " + e.getMessage());
      }
  }
}
