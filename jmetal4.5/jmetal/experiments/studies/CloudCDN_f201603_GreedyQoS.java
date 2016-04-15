/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jmetal.experiments.studies;

import java.io.IOException;
import java.util.ArrayList;
import jmetal.core.Solution;
import jmetal.encodings.solutionType.cloudcdn.CloudCDNSolutionf201603Type;
import jmetal.problems.cloudcdn.f201603.CloudCDN_MP;
import jmetal.problems.cloudcdn.f201603.Trafico;
import jmetal.util.JMException;

/**
 *
 * @author santiago
 */
public class CloudCDN_f201603_GreedyQoS {

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
        
        CloudCDN_MP prob = new CloudCDN_MP("CloudCDNSolutionf201603Type",
                "../Instances/",
                "../Instances/" + inst_type + "/data." + inst_number + "/",
                "BestQoSSecure",
                time_horizon);
        CloudCDNSolutionf201603Type sol_type = new CloudCDNSolutionf201603Type(prob);
        Solution solution = new Solution(prob, sol_type.createVariables());
        
        double totalQoS = 0.0;
        int[] reservedAllocation = new int[prob.getRegionesDatacenters().size()];
        int[] onDemandAllocation = new int[prob.getRegionesDatacenters().size()];
        int[] routingSummary = new int[prob.getRegionesDatacenters().size()];
        
        ArrayList<Trafico> ongoing_traffic = new ArrayList<Trafico>();
        
        
        for (int tid = 0; tid < prob.getTrafico().size(); tid++) {
            Trafico t;
            t = prob.getTrafico().get(tid);
                    
            int dcId;
            dcId = prob.getSortedQoS(t.getRegUsrId()).get(0).getRegDcId();
            
            CloudCDNSolutionf201603Type.SetDocStored(prob, solution, dcId, t.getDocId(), true);
            
            routingSummary[dcId] += t.getNumContenidos();
            totalQoS += (t.getNumContenidos() * prob.getQoS().get(t.getRegUsrId()).get(dcId).getQosMetric()) / prob.getTrafico().size();
            
            
        }
    }
}
