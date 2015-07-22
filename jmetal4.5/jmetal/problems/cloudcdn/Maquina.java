package jmetal.problems.cloudcdn;

public class Maquina {
	int vmId;
	String  vmNombre;
	int bandwidth;
	public Maquina(int vmId, String vmNombre, int bandwidth) {
		super();
		this.vmId = vmId;
		this.vmNombre = vmNombre;
		this.bandwidth = bandwidth;
	}
	public int getVmId() {
		return vmId;
	}
	public void setVmId(int vmId) {
		this.vmId = vmId;
	}
	public String getVmNombre() {
		return vmNombre;
	}
	public void setVmNombre(String vmNombre) {
		this.vmNombre = vmNombre;
	}
	public int getBandwidth() {
		return bandwidth;
	}
	public void setBandwidth(int bandwidth) {
		this.bandwidth = bandwidth;
	}
}
