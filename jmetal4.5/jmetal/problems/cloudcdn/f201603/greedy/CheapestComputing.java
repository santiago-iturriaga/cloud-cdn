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
import jmetal.problems.cloudcdn.f201603.RegionDatacenterComptuingCheapestComparator;

/**
 *
 * @author santiago
 */
public class CheapestComputing implements IGreedyRouting {

    private final CloudCDN_MP problem_;

    public CheapestComputing(CloudCDN_MP problem) {
        problem_ = problem;
    }

    @Override
    public boolean Route(Solution solution, int[] trafficRouting, int[] routingSummary, double[] storageComplementSummary) {
        ArrayList<RegionDatacenter> sortedDC = new ArrayList<>(problem_.getRegionesDatacenters());
        sortedDC.sort(new RegionDatacenterComptuingCheapestComparator());

        for (int i = 0; i < problem_.getTrafico().size(); i++) {
            int docId;
            docId = problem_.getTrafico().get(i).getDocId();

            int dcId;

            if (CloudCDNSolutionf201603Type.IsDocConsidered(problem_, solution, docId)) {
                dcId = sortedDC.get(0).getRegDctId();
                storageComplementSummary[dcId] = storageComplementSummary[dcId] + problem_.getDocumentos().get(docId).getDocSize();
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
            }

            trafficRouting[i] = dcId;
            routingSummary[dcId]++;
        }

        return true;
    }

    public boolean RouteWithCache(Solution solution, int[] trafficRouting, int[] routingSummary) {
        HashMap<Integer, Integer> cache;
        cache = new HashMap<>();

        ArrayList<RegionDatacenter> sortedDC = new ArrayList<>(problem_.getRegionesDatacenters());
        sortedDC.sort(new RegionDatacenterComptuingCheapestComparator());

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
