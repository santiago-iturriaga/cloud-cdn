/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jmetal.problems.cloudcdn.f201603.greedy;

import jmetal.core.Solution;

/**
 *
 * @author santiago
 */
public interface IGreedyRouting {

    boolean Route(Solution solution, int[] trafficRouting, int[] routingSummary, double[] storageComplementSummary);
    
}
