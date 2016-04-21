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
public class BestQoSSecure implements IGreedyRouting {

    private final CloudCDN_MP problem_;
    private final int[] zeroes;
    
    //private static ThreadLocal<int[][][]> vmNeededThreaded = new ThreadLocal<>();
    //private static ThreadLocal<int[][][]> vmOverflowThreaded = new ThreadLocal<>();
    public BestQoSSecure(CloudCDN_MP problem) {
        this.problem_ = problem;
        this.zeroes = new int[problem_.VM_RENTING_STEPS];
    }

    @Override
    public double Route(Solution solution, int[] routingSummary,
            int[] reservedAllocation, int[] onDemandAllocation,
            Optional<Integer> justProvId) {

        double totalQoS = 0.0;

        //if (vmNeededThreaded.get() == null) {
        //    vmNeededThreaded.set(new int[problem_.getRegionesDatacenters().size()][problem_.getNumProvedores()][CloudCDN_MP.VM_RENTING_STEPS]);
        //}
        int[][][] vmNeeded;
        //vmNeeded = vmNeededThreaded.get();
        vmNeeded = problem_.vmNeeded;

        //if (vmOverflowThreaded.get() == null) {
        //    vmOverflowThreaded.set(new int[problem_.getRegionesDatacenters().size()][problem_.getNumProvedores()][CloudCDN_MP.VM_RENTING_STEPS]);
        //}
        int[][][] vmOverflow;
        //vmOverflow = vmOverflowThreaded.get();
        vmOverflow = problem_.vmOverflow;

        int[][] vmMaxNeeded, vmMaxOverflow;
        //vmMaxNeeded = new int[problem_.getRegionesDatacenters().size()][problem_.getNumProvedores()];
        //vmMaxOverflow = new int[problem_.getRegionesDatacenters().size()][problem_.getNumProvedores()];
        vmMaxNeeded = problem_.vmMaxNeeded;
        vmMaxOverflow = problem_.vmMaxOverflow;

        ResetArrays(vmNeeded, vmOverflow, vmMaxNeeded, vmMaxOverflow);

        int lowerBound;
        lowerBound = 0;

        for (int i = 0; i < problem_.getTrafico().size(); i++) {
            //
            //=== Traffic routing algorithm ==========================
            //
            Trafico t;
            t = problem_.getTrafico().get(i);

            int docId;
            docId = t.getDocId();

            int provId;
            provId = problem_.getDocumentos().get(docId).getProvId();

            if (justProvId.isPresent()) {
                if (provId != justProvId.get()) {
                    continue;
                }
            }

            int dcId;
            dcId = RouteCurrentRequest(solution, t);

            routingSummary[dcId] += t.getNumContenidos();
            totalQoS += (t.getNumContenidos() * problem_.getQoS().get(t.getRegUsrId()).get(dcId).getQosMetric()) / problem_.getTrafico().size();

            //
            //=== VM allocation algorithm ==========================
            //
            int currStep;
            currStep = t.getReqTime() - lowerBound;

            if (currStep > problem_.VM_RENTING_STEPS) {
                NextRentBatch(solution, vmNeeded, vmOverflow, vmMaxNeeded, vmMaxOverflow,
                        reservedAllocation, onDemandAllocation);

                while (currStep > problem_.VM_RENTING_STEPS) {
                    lowerBound += problem_.VM_RENTING_STEPS;
                    currStep = currStep - problem_.VM_RENTING_STEPS;
                }
            }

            AccountCurrentRequest(t, dcId, provId, currStep,
                    vmNeeded, vmOverflow, vmMaxNeeded, vmMaxOverflow);
        }

        return totalQoS;
    }

    private void ResetArrays(int[][][] vmNeeded, int[][][] vmOverflow,
            int[][] vmMaxNeeded, int[][] vmMaxOverflow) {

        for (int d = 0; d < problem_.getRegionesDatacenters().size(); d++) {
            for (int h = 0; h < problem_.getNumProvedores(); h++) {
                System.arraycopy(zeroes, 0, vmNeeded[d][h], 0, problem_.VM_RENTING_STEPS);
                System.arraycopy(zeroes, 0, vmOverflow[d][h], 0, problem_.VM_RENTING_STEPS);
            }
        }

        for (int d = 0; d < problem_.getRegionesDatacenters().size(); d++) {
            System.arraycopy(zeroes, 0, vmMaxNeeded[d], 0, problem_.getNumProvedores());
            System.arraycopy(zeroes, 0, vmMaxOverflow[d], 0, problem_.getNumProvedores());
        }
    }

    private int RouteCurrentRequest(Solution solution, Trafico t) {
        int docId = t.getDocId();

        ArrayList<QoS> regionQoS = problem_.getSortedQoS(t.getRegUsrId());

        int bestIdx;
        bestIdx = 0;

        int dcId;
        dcId = regionQoS.get(bestIdx).getRegDcId();

        while (!problem_.solutionTypeCustom_.IsDocStored(solution, dcId, docId)) {
            bestIdx++;

            if (bestIdx >= regionQoS.size()) {
                // All documents must be assigned.
                // TODO: considerar otras alternativas a la no factibilidad.
                dcId = regionQoS.get(0).getRegDcId();
                problem_.solutionTypeCustom_.SetDocStored(solution, dcId, docId, true);
                return dcId;
            } else {
                dcId = regionQoS.get(bestIdx).getRegDcId();
            }
        }

        return dcId;
    }

    private void AccountCurrentRequest(Trafico t, int dcId, int provId, int currStep,
            int[][][] vmNeeded, int[][][] vmOverflow,
            int[][] vmMaxNeeded, int[][] vmMaxOverflow) {

        int time;

        int contentsInCurrStep;
        contentsInCurrStep = t.getNumContenidos();

        int contentsInNextStep;
        contentsInNextStep = 0;

        if (currStep + contentsInCurrStep > problem_.VM_RENTING_STEPS) {
            contentsInCurrStep = problem_.VM_RENTING_STEPS - currStep;
            contentsInNextStep = t.getNumContenidos() - contentsInCurrStep;

            if (contentsInNextStep > problem_.VM_RENTING_STEPS) {
                System.out.println("DANGER!");
            }
        }

        for (int len = 0; len < contentsInCurrStep; len++) {
            time = currStep + len;
            vmNeeded[dcId][provId][time]++;

            if (vmMaxNeeded[dcId][provId] < vmNeeded[dcId][provId][time]) {
                vmMaxNeeded[dcId][provId] = vmNeeded[dcId][provId][time];
            }
        }

        for (int len = 0; len < contentsInNextStep; len++) {
            vmOverflow[dcId][provId][len]++;

            if (vmMaxOverflow[dcId][provId] < vmOverflow[dcId][provId][len]) {
                vmMaxOverflow[dcId][provId] = vmOverflow[dcId][provId][len];
            }
        }
    }

    private void NextRentBatch(Solution solution,
            int[][][] vmNeeded, int[][][] vmOverflow,
            int[][] vmMaxNeeded, int[][] vmMaxOverflow,
            int[] reservedAllocation, int[] onDemandAllocation) {

        int neededVMs;

        for (int d = 0; d < problem_.getRegionesDatacenters().size(); d++) {
            neededVMs = 0;

            for (int h = 0; h < problem_.getNumProvedores(); h++) {
                neededVMs += (int) Math.ceil((double) vmMaxNeeded[d][h] / (double) problem_.VM_PROCESSING);

                System.arraycopy(vmOverflow[d][h], 0, vmNeeded[d][h], 0, problem_.VM_RENTING_STEPS);
                System.arraycopy(zeroes, 0, vmOverflow[d][h], 0, problem_.VM_RENTING_STEPS);
            }

            System.arraycopy(vmMaxOverflow[d], 0, vmMaxNeeded[d], 0, problem_.getNumProvedores());
            System.arraycopy(zeroes, 0, vmMaxOverflow[d], 0, problem_.getNumProvedores());

            int rentedVMs;
            try {
                rentedVMs = problem_.solutionTypeCustom_.GetRIVariables(solution).getValue(d);
            } catch (JMException ex) {
                Logger.getLogger(BestQoSSecure.class.getName()).log(Level.SEVERE, null, ex);
                rentedVMs = 0;
            }

            reservedAllocation[d] += Math.min(rentedVMs, neededVMs);
            onDemandAllocation[d] += Math.max(0, neededVMs - rentedVMs);
        }
    }
}
