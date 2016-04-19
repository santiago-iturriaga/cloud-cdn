/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jmetal.experiments.greedy;

import java.io.IOException;
import java.util.Optional;
import jmetal.core.Solution;
import jmetal.encodings.variable.Binary;
import jmetal.problems.cloudcdn.f201603.CloudCDN_MP;
import jmetal.problems.cloudcdn.f201603.Trafico;
import jmetal.problems.cloudcdn.f201603.greedy.BestQoSSecure;
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

        CloudCDN_MP prob = new CloudCDN_MP("CloudCDNSolutionf201603b100Type",
                "../Instances/",
                "../Instances/" + inst_type + "/data." + inst_number + "/",
                "BestQoSSecure",
                time_horizon);

        Solution solution = new Solution(prob, prob.solutionTypeCustom_.createVariables());

        for (int dcId = 0; dcId < prob.getRegionesDatacenters().size(); dcId++) {
            prob.solutionTypeCustom_.SetRIDCCount(solution, dcId, 0);
        }

        for (int bId = 0; bId < prob.solutionTypeCustom_.NUM_BUCKETS; bId++) {
            Binary bits = prob.solutionTypeCustom_.GetDocStorageVariables(solution);

            for (int i = 0; i < bits.bits_.length(); i++) {
                bits.bits_.set(i, false);
            }
        }

        for (int tid = 0; tid < prob.getTrafico().size(); tid++) {
            Trafico t;
            t = prob.getTrafico().get(tid);

            int dcId;
            dcId = prob.getSortedQoS(t.getRegUsrId()).get(0).getRegDcId();

            prob.solutionTypeCustom_.SetDocStored(prob, solution, dcId, t.getDocId(), true);
            //prob.solutionTypeCustom_.SetDocStored(prob, solution, 0, t.getDocId(), true);
        }

        Optional<Integer> justProvId = Optional.empty();

        double totalQoS = 0.0;
        int[] reservedAllocation = new int[prob.getRegionesDatacenters().size()];
        int[] onDemandAllocation = new int[prob.getRegionesDatacenters().size()];
        int[] routingSummary = new int[prob.getRegionesDatacenters().size()];

        BestQoSSecure greedy = new BestQoSSecure(prob);
        totalQoS = greedy.Route(solution, routingSummary, reservedAllocation, onDemandAllocation, justProvId);

        double networkCost = prob.computeNetworkCost(routingSummary);
        double storageCost = prob.computeStorageCost(solution);
        double computingCost = prob.computeComputingCost(solution, reservedAllocation, onDemandAllocation);

        System.out.println(networkCost + storageCost + computingCost + " " + totalQoS);
    }
}
