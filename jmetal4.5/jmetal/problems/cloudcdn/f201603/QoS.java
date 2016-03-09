package jmetal.problems.cloudcdn.f201603;

public class QoS {
	int regUsrId;
	int regDocId;
	int qosMetric;

	public QoS(int regUsrId, int regDocId, int qosMetric) {
		super();
		this.regUsrId = regUsrId;
		this.regDocId = regDocId;
		this.qosMetric = qosMetric;
	}

	public int getRegUsrId() {
		return regUsrId;
	}

	public void setRegUsrId(int regUsrId) {
		this.regUsrId = regUsrId;
	}

	public int getRegDocId() {
		return regDocId;
	}

	public void setRegDocId(int regDocId) {
		this.regDocId = regDocId;
	}

	public int getQosMetric() {
		return qosMetric;
	}

	public void setQosMetric(int qosMetric) {
		this.qosMetric = qosMetric;
	}
}
