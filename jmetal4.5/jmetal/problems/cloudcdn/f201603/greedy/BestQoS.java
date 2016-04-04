/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jmetal.problems.cloudcdn.f201603.greedy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import jmetal.core.Solution;
import jmetal.encodings.solutionType.cloudcdn.CloudCDNSolutionf201603Type;
import jmetal.problems.cloudcdn.f201603.CloudCDN_MP;
import jmetal.problems.cloudcdn.f201603.QoS;
import jmetal.problems.cloudcdn.f201603.RegionDatacenter;
import jmetal.problems.cloudcdn.f201603.RegionDatacenterNetworkCheapestComparator;
import jmetal.problems.cloudcdn.f201603.Trafico;
import jmetal.util.JMException;

/**
 *
 * @author santiago
 */
public class BestQoS implements IGreedyRouting {

    private final CloudCDN_MP problem_;
    private final int[] zeroes;

    public BestQoS(CloudCDN_MP problem) {
        this.problem_ = problem;
        this.zeroes = new int[problem_.VM_RENTING_STEPS];
    }

    @Override
    public double Route(Solution solution, int[] routingSummary, 
            int[] reservedAllocation, int[] onDemandAllocation, 
            Optional<Integer> justProvId) {

        double totalQoS = 0.0;

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

            if (justProvId.isPresent()) {
                if (problem_.getDocumentos().get(docId).getProvId() != justProvId.get().intValue()) {
                    continue;
                }
            }
            
            int dcId;

            ArrayList<QoS> regionQoS = problem_.getSortedQoS(t.getRegUsrId());

            int bestIdx;
            bestIdx = 0;

            dcId = regionQoS.get(bestIdx).getRegDcId();

            while (!CloudCDNSolutionf201603Type.IsDocStored(problem_, solution, dcId, docId)) {
                bestIdx++;

                if (bestIdx >= regionQoS.size()) {
                    // All documents must be assigned.
                    // TODO: considerar otras alternativas a la no factibilidad.
                    bestIdx = 0;
                    dcId = regionQoS.get(0).getRegDcId();
                    CloudCDNSolutionf201603Type.SetDocStored(problem_, solution, dcId, docId, true);

                    break;
                } else {
                    dcId = regionQoS.get(bestIdx).getRegDcId();
                }
            }

            routingSummary[dcId] += t.getNumContenidos();
            totalQoS += (t.getNumContenidos() * problem_.getQoS().get(t.getRegUsrId()).get(dcId).getQosMetric()) / problem_.getTrafico().size();

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
                        Logger.getLogger(BestQoS.class.getName()).log(Level.SEVERE, null, ex);
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

        return totalQoS;
    }

    public boolean RouteWithCache(Solution solution, int[] trafficRouting, int[] routingSummary) {
        HashMap<Integer, Integer> cache;
        cache = new HashMap<>();

        ArrayList<RegionDatacenter> sortedDC = new ArrayList<>(problem_.getRegionesDatacenters());
        sortedDC.sort(new RegionDatacenterNetworkCheapestComparator());

        for (int i = 0; i < problem_.getTrafico().size(); i++) {
            int docId;
            docId = problem_.getTrafico().get(i).getDocId();

            int dcId;

            if (cache.containsKey(docId)) {
                dcId = cache.get(docId);
            } else {

                int cheapestIdx;
                cheapestIdx = 0;

                while (!CloudCDNSolutionf201603Type.IsDocStored(problem_, solution, sortedDC.get(cheapestIdx).getRegDctId(), docId)) {
                    cheapestIdx++;

                    if (cheapestIdx >= sortedDC.size()) {
                        // All documents must be assigned.
                        // TODO: considerar otras alternativas a la no factibilidad.
                        cheapestIdx = 0;
                        CloudCDNSolutionf201603Type.SetDocStored(problem_, solution, sortedDC.get(0).getRegDctId(), docId, true);

                        break;
                    }
                }

                dcId = sortedDC.get(cheapestIdx).getRegDctId();
                cache.put(docId, dcId);
            }

            trafficRouting[i] = dcId;
            routingSummary[dcId]++;
        }

        return true;
    }
}
