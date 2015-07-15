/**
 * 
 */
package jmetal.problems.cloudcdn.greedy.routing;

import jmetal.core.Solution;
import jmetal.problems.cloudcdn.CloudCDN_SO;

public class SimpleRR {
	private CloudCDN_SO problem_;
	
	private double[] trafficAmount_; 
	
	public SimpleRR(CloudCDN_SO problem) {
		problem_ = problem;
		trafficAmount_ = new double[problem_.getCantidadRegionesDatacenters()];
	}

	public void Compute(Solution solution) {	
		for (int i=0; i<problem_.getCantidadRegionesDatacenters(); i++) {
			trafficAmount_[i] = 0;
		}
		
		int j=0;
		for (int i=0; i<problem_.getCantidadTrafico(); i++) {
			if (j==problem_.getCantidadRegionesDatacenters()) {
				j=0;
			}
			
			trafficAmount_[j] += problem_.getTrafico()[i].getDocSize();
			j++;
		}
	}
	
	public double[] getTrafficAmount() {
		return trafficAmount_;
	}
}
