package jmetal.problems.cloudcdn;

public class DatacenterMaquina {
	Integer dcId;
	Integer  vmId;
	Double costTimeStep;
	public Integer getDcId() {
		return dcId;
	}
	public void setDcId(Integer dcId) {
		this.dcId = dcId;
	}
	public Integer getVmId() {
		return vmId;
	}
	public void setVmId(Integer vmId) {
		this.vmId = vmId;
	}
	public Double getCostTimeStep() {
		return costTimeStep;
	}
	public void setCostTimeStep(Double costTimeStep) {
		this.costTimeStep = costTimeStep;
	}
	public DatacenterMaquina(Integer dcId, Integer vmId, Double costTimeStep) {
		super();
		this.dcId = dcId;
		this.vmId = vmId;
		this.costTimeStep = costTimeStep;
	}
}
