package jmetal.problems.cloudcdn;

public class Maquina {
	int vmId;
	String vmNombre;

	int bandwidth; // Max. bandwidth in Mbps
	double bandwidthGBpm; // Max. bandwidth in GB per minute

	public Maquina(int vmId, String vmNombre, int bandwidth) {
		super();
		this.vmId = vmId;
		this.vmNombre = vmNombre;

		setBandwidth(bandwidth);
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
		this.bandwidthGBpm = ((bandwidth / 8.0) * 60.0) / 1024.0;
	}

	public double getBandwidthGBpm() {
		return this.bandwidthGBpm;
	}
}
