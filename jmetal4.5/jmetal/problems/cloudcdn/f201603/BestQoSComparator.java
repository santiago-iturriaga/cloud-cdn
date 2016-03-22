/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jmetal.problems.cloudcdn.f201603;

import java.util.Comparator;

/**
 *
 * @author santiago
 */
public class BestQoSComparator implements Comparator<QoS> {

    @Override
    public int compare(QoS arg0, QoS arg1) {
        if (arg0.qosMetric < arg1.qosMetric) {
            return -1;
        } else if (arg0.qosMetric > arg1.qosMetric) {
            return 1;
        } else {
            return 0;
        }
    }
}
