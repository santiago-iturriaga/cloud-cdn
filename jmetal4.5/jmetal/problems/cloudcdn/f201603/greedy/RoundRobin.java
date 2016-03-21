/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jmetal.problems.cloudcdn.f201603.greedy;

import java.util.ArrayList;
import java.util.HashMap;
import jmetal.core.Solution;
import jmetal.encodings.solutionType.cloudcdn.CloudCDNSolutionf201603Type;
import jmetal.problems.cloudcdn.f201603.CloudCDN_MP;
import jmetal.problems.cloudcdn.f201603.RegionDatacenter;
import jmetal.problems.cloudcdn.f201603.RegionDatacenterNetworkCheapestComparator;
import jmetal.util.PseudoRandom;

/**
 *
 * @author santiago
 */
public class RoundRobin implements IGreedyRouting {

    private final CloudCDN_MP problem_;

    public RoundRobin(CloudCDN_MP problem) {
        problem_ = problem;
    }

    public boolean Route(Solution solution, int[] trafficRouting, int[] routingSummary, double[] storageComplementSummary) {
        //HashMap<Integer, Integer> cache;
        //cache = new HashMap<>();

        int numDC = problem_.getRegionesDatacenters().size();
        int currDC = 0;
        
        for (int i = 0; i < problem_.getTrafico().size(); i++) {
            int docId;
            docId = problem_.getTrafico().get(i).getDocId();

            int dcId;

            if (CloudCDNSolutionf201603Type.IsDocConsidered(problem_, solution, docId)) {
                dcId = problem_.getRegionesDatacenters().get(currDC).getRegDctId();
                storageComplementSummary[dcId] = storageComplementSummary[dcId] + problem_.getDocumentos().get(docId).getDocSize();
                currDC = (currDC + 1) % numDC; 
            } else {
                int loopCount;
                loopCount = 0;

                while (!CloudCDNSolutionf201603Type.IsDocStored(problem_, solution, 
                        problem_.getRegionesDatacenters().get(currDC).getRegDctId(), docId)) {
                    
                    currDC = (currDC + 1) % numDC;
                    loopCount++;

                    if (loopCount == numDC) {
                        // All documents must be assigned.
                        // TODO: considerar otras alternativas a la no factibilidad.
                        currDC = PseudoRandom.randInt(0, numDC-1);
                        CloudCDNSolutionf201603Type.SetDocStored(problem_, solution, 
                                problem_.getRegionesDatacenters().get(currDC).getRegDctId(), docId, true);

                        break;
                    }
                }
                
                dcId = problem_.getRegionesDatacenters().get(currDC).getRegDctId();
                currDC = (currDC + 1) % numDC;
            }

            trafficRouting[i] = dcId;
            routingSummary[dcId]++;
        }

        return true;
    }
}
