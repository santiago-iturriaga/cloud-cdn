package jmetal.problems.cloudcdn.f201603;

public class QoS {
    int regUsrId;
    int regDcId;
    double qosMetric;

    public QoS(int regUsrId, int regDcId, double qosMetric) {
        super();
        this.regUsrId = regUsrId;
        this.regDcId = regDcId;
        this.qosMetric = qosMetric;
    }

    public int getRegUsrId() {
        return regUsrId;
    }

    public void setRegUsrId(int regUsrId) {
        this.regUsrId = regUsrId;
    }

    public int getRegDcId() {
        return regDcId;
    }

    public void setRegDocId(int regDcId) {
        this.regDcId = regDcId;
    }

    public double getQosMetric() {
        return qosMetric;
    }

    public void setQosMetric(double qosMetric) {
        this.qosMetric = qosMetric;
    }
}
