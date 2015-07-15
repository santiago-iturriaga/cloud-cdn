package jmetal.problems.cloudcdn;

public class QoS {
	Integer regUsrId;
	Integer regDocId;
	Integer qosMetric;
	public QoS(Integer regUsrId, Integer regDocId, Integer qosMetric) {
		super();
		this.regUsrId = regUsrId;
		this.regDocId = regDocId;
		this.qosMetric = qosMetric;
	}
	public Integer getRegUsrId() {
		return regUsrId;
	}
	public void setRegUsrId(Integer regUsrId) {
		this.regUsrId = regUsrId;
	}
	public Integer getRegDocId() {
		return regDocId;
	}
	public void setRegDocId(Integer regDocId) {
		this.regDocId = regDocId;
	}
	public Integer getQosMetric() {
		return qosMetric;
	}
	public void setQosMetric(Integer qosMetric) {
		this.qosMetric = qosMetric;
	}
}
