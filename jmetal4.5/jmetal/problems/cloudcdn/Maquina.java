package jmetal.problems.cloudcdn;

public class Maquina {
	Integer vmId;
	String  vmNombre;
	Integer bandwidth;
	public Maquina(Integer vmId, String vmNombre, Integer bandwidth) {
		super();
		this.vmId = vmId;
		this.vmNombre = vmNombre;
		this.bandwidth = bandwidth;
	}
	public Integer getVmId() {
		return vmId;
	}
	public void setVmId(Integer vmId) {
		this.vmId = vmId;
	}
	public String getVmNombre() {
		return vmNombre;
	}
	public void setVmNombre(String vmNombre) {
		this.vmNombre = vmNombre;
	}
	public Integer getBandwidth() {
		return bandwidth;
	}
	public void setBandwidth(Integer bandwidth) {
		this.bandwidth = bandwidth;
	}
}
