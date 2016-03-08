package jmetal.problems.cloudcdn.greedy.routing;

import jmetal.problems.cloudcdn.CloudCDN_base;

public abstract class RRRoutingAlgorithm extends RoutingAlgorithm_base {
	public RRRoutingAlgorithm(CloudCDN_base problem) {
		super(problem);
	}

	public abstract double getMachineCost();
}
