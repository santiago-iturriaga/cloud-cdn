/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jmetal.problems.cloudcdn.f201603.greedy;

import java.util.logging.Level;
import java.util.logging.Logger;
import jmetal.core.Solution;
import jmetal.encodings.solutionType.cloudcdn.CloudCDNSolutionf201603Type;
import jmetal.problems.cloudcdn.f201603.CloudCDN_MP;
import jmetal.problems.cloudcdn.f201603.Trafico;
import jmetal.util.JMException;

/**
 *
 * @author santiago
 */
public class VMAllocation {

    private CloudCDN_MP problem_;

    public VMAllocation(CloudCDN_MP problem) {
        problem_ = problem;
    }

    public void Allocate(Solution solution, int[] routing, int[] reservedAllocation, int[] onDemandAllocation) {
        //Algoritmo de allocation muy simple
        //TODO: implementar algoritmo de VM allocation avanzado
        int[][] vmNeeded;
        vmNeeded = new int[problem_.getRegionesDatacenters().size()][problem_.VM_RENTING_STEPS];

        int[][] vmOverflow;
        vmOverflow = new int[problem_.getRegionesDatacenters().size()][problem_.VM_RENTING_STEPS];

        int lowerBound;
        lowerBound = 0;

        int neededVMs;

        for (int req = 0; req < problem_.getTrafico().size(); req++) {
            Trafico traff;
            traff = problem_.getTrafico().get(req);

            int currStep;
            currStep = traff.getReqTime() - lowerBound;

            if (currStep > problem_.VM_RENTING_STEPS) {
                for (int i = 0; i < problem_.getRegionesDatacenters().size(); i++) {
                    int maxDemand;
                    maxDemand = 0;

                    // TODO: mejorar la eficiencia de esta parte (usar arraycopy)
                    // http://docs.oracle.com/javase/7/docs/api/java/lang/System.html
                    for (int j = 0; j < problem_.VM_RENTING_STEPS; j++) {
                        if (vmNeeded[i][j] > maxDemand) {
                            maxDemand = vmNeeded[i][j];
                        }
                        vmNeeded[i][j] = vmOverflow[i][j];
                        vmOverflow[i][j] = 0;
                    }

                    int rentedVMs;
                    try {
                        rentedVMs = CloudCDNSolutionf201603Type.GetRIVariables(solution).getValue(i);
                    } catch (JMException ex) {
                        Logger.getLogger(VMAllocation.class.getName()).log(Level.SEVERE, null, ex);
                        rentedVMs = 0;
                    }

                    neededVMs = (int) Math.ceil(maxDemand / problem_.VM_PROCESSING);
                    reservedAllocation[i] += Math.min(rentedVMs, neededVMs);
                    onDemandAllocation[i] += Math.max(0, neededVMs - rentedVMs);
                }

                while (currStep > problem_.VM_RENTING_STEPS) {
                    lowerBound += problem_.VM_RENTING_STEPS;
                    currStep = currStep - problem_.VM_RENTING_STEPS;
                }
            }

            for (int len = 0; len < traff.getNumContenidos(); len++) {
                if (currStep + len < problem_.VM_RENTING_STEPS) {
                    vmNeeded[routing[req]][currStep + len]++;
                } else {
                    vmOverflow[routing[req]][currStep + len - problem_.VM_RENTING_STEPS]++;
                }
            }
        }
    }
}
