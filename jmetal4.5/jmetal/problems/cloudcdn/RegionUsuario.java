package jmetal.problems.cloudcdn;

public class RegionUsuario {
	int regUsrId;
	String regNombre;
	int regId;
	double qosThreshold;

	public int getRegUsrId() {
		return regUsrId;
	}

	public void setRegUsrId(int regUsrId) {
		this.regUsrId = regUsrId;
	}

	public String getRegNombre() {
		return regNombre;
	}
	
	public double getQoSThreshold() {
		return qosThreshold;
	}

	public void setRegNombre(String regNombre) {
		this.regNombre = regNombre;
	}

	public int getRegId() {
		return regId;
	}

	public void setRegId(int regId) {
		this.regId = regId;
	}

	public void setQoSThreshold(double threshold) {
		qosThreshold = threshold;
	}
	
	public RegionUsuario(int regUsrId, String regNombre, int regId, double qosThreshold) {
		super();
		this.regUsrId = regUsrId;
		this.regNombre = regNombre;
		this.regId = regId;
		this.qosThreshold = qosThreshold;
	}
}
