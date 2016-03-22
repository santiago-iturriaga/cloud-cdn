/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jmetal.problems.cloudcdn.f201603.greedy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import jmetal.core.Solution;
import jmetal.encodings.solutionType.cloudcdn.CloudCDNSolutionf201603Type;
import jmetal.problems.cloudcdn.f201603.CloudCDN_MP;
import jmetal.problems.cloudcdn.f201603.RegionDatacenter;
import jmetal.problems.cloudcdn.f201603.RegionDatacenterNetworkCheapestComparator;
import jmetal.problems.cloudcdn.f201603.Trafico;
import jmetal.util.JMException;
import jmetal.util.PseudoRandom;

/**
 *
 * @author santiago
 */
public class RoundRobin implements IGreedyRouting {

    private final CloudCDN_MP problem_;
    private final int[] zeroes;

    public RoundRobin(CloudCDN_MP problem) {
        problem_ = problem;
        this.zeroes = new int[problem_.VM_RENTING_STEPS];
    }

    public void Route(Solution solution, int[] routingSummary, Double totalQoS, int[] reservedAllocation, int[] onDemandAllocation) {
        totalQoS = 0.0;

        int numDC = problem_.getRegionesDatacenters().size();
        int currDC = 0;

        int[][] vmNeeded;
        vmNeeded = new int[problem_.getRegionesDatacenters().size()][problem_.VM_RENTING_STEPS];

        int[][] vmOverflow;
        vmOverflow = new int[problem_.getRegionesDatacenters().size()][problem_.VM_RENTING_STEPS];

        int lowerBound;
        lowerBound = 0;

        int neededVMs;
        
        for (int i = 0; i < problem_.getTrafico().size(); i++) {
            //
            //=== Traffic routing algorithm ==========================
            //
            Trafico t;
            t = problem_.getTrafico().get(i);

            int docId;
            docId = t.getDocId();

            int dcId;

            int loopCount;
            loopCount = 0;

            while (!CloudCDNSolutionf201603Type.IsDocStored(problem_, solution,
                    problem_.getRegionesDatacenters().get(currDC).getRegDctId(), docId)) {

                currDC = (currDC + 1) % numDC;
                loopCount++;

                if (loopCount == numDC) {
                    // All documents must be assigned.
                    // TODO: considerar otras alternativas a la no factibilidad.
                    currDC = PseudoRandom.randInt(0, numDC - 1);
                    CloudCDNSolutionf201603Type.SetDocStored(problem_, solution,
                            problem_.getRegionesDatacenters().get(currDC).getRegDctId(), docId, true);

                    break;
                }
            }

            dcId = problem_.getRegionesDatacenters().get(currDC).getRegDctId();
            currDC = (currDC + 1) % numDC;

            routingSummary[dcId]++;

            totalQoS += t.getNumContenidos() * problem_.getQoS().get(t.getRegUsrId()).get(dcId).getQosMetric();
           
            //
            //=== VM allocation algorithm ==========================
            //
            int currStep;
            currStep = t.getReqTime() - lowerBound;

            if (currStep > problem_.VM_RENTING_STEPS) {
                for (int d = 0; d < problem_.getRegionesDatacenters().size(); d++) {
                    int maxDemand;
                    maxDemand = 0;

                    for (int j = 0; j < problem_.VM_RENTING_STEPS; j++) {
                        if (vmNeeded[d][j] > maxDemand) {
                            maxDemand = vmNeeded[d][j];
                        }
                    }

                    System.arraycopy(vmOverflow[d], 0, vmNeeded[d], 0, problem_.VM_RENTING_STEPS);
                    System.arraycopy(zeroes, 0, vmOverflow[d], 0, problem_.VM_RENTING_STEPS);

                    int rentedVMs;
                    try {
                        rentedVMs = CloudCDNSolutionf201603Type.GetRIVariables(solution).getValue(d);
                    } catch (JMException ex) {
                        Logger.getLogger(VMAllocation.class.getName()).log(Level.SEVERE, null, ex);
                        rentedVMs = 0;
                    }

                    neededVMs = (int) Math.ceil((double) maxDemand / (double) problem_.VM_PROCESSING);
                    reservedAllocation[d] += Math.min(rentedVMs, neededVMs);
                    onDemandAllocation[d] += Math.max(0, neededVMs - rentedVMs);
                }

                while (currStep > problem_.VM_RENTING_STEPS) {
                    lowerBound += problem_.VM_RENTING_STEPS;
                    currStep = currStep - problem_.VM_RENTING_STEPS;
                }
            }

            for (int len = 0; len < t.getNumContenidos(); len++) {
                if (currStep + len < problem_.VM_RENTING_STEPS) {
                    vmNeeded[dcId][currStep + len]++;
                } else {
                    vmOverflow[dcId][currStep + len - problem_.VM_RENTING_STEPS]++;
                }
            }
            
        }
    }
}
