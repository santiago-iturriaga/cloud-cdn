package jmetal.problems.cloudcdn.greedy.routing;

import java.util.ArrayList;
import java.util.Comparator;

import jmetal.problems.cloudcdn.QoS;
import jmetal.problems.cloudcdn.RegionDatacenter;

public class BestQoSComparator implements Comparator<RegionDatacenter> {
	int userRegionId_;
	ArrayList<ArrayList<QoS>> qos_;
	
	public BestQoSComparator(int userRegionId, ArrayList<ArrayList<QoS>> qos) {
		userRegionId_ = userRegionId;
		qos_ = qos;
	}
	
	@Override
	public int compare(RegionDatacenter arg0, RegionDatacenter arg1) {
		int arg0qos = qos_.get(userRegionId_).get(arg0.getRegDctId()).getQosMetric();
		int arg1qos = qos_.get(userRegionId_).get(arg1.getRegDctId()).getQosMetric();
		
		return Double.compare(arg0qos, arg1qos);
	}
}
