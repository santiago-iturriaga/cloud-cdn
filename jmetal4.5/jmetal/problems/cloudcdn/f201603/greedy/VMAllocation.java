/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jmetal.problems.cloudcdn.f201603.greedy;

import jmetal.problems.cloudcdn.f201603.CloudCDN_MP;
import jmetal.problems.cloudcdn.f201603.Trafico;

/**
 *
 * @author santiago
 */
public class VMAllocation {

    private CloudCDN_MP problem_;

    public VMAllocation(CloudCDN_MP problem) {
        problem_ = problem;
    }

    public int[] Allocate(int[] routing) {
        int[] allocation = new int[problem_.getRegionesDatacenters().size()];

        //Algoritmo de allocation muy simple
        //TODO: implementar algoritmo de VM allocation avanzado
        int[][] vmNeeded;
        vmNeeded = new int[problem_.getRegionesDatacenters().size()][problem_.VM_RENTING_STEPS];

        int[][] vmOverflow;
        vmOverflow = new int[problem_.getRegionesDatacenters().size()][problem_.VM_RENTING_STEPS];

        int lowerBound;
        lowerBound = 0;

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

                    allocation[i] += Math.ceil(maxDemand / problem_.VM_PROCESSING);
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

        return allocation;
    }
}
