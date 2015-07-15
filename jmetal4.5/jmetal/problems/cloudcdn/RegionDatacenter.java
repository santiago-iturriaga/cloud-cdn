package jmetal.problems.cloudcdn;

public class RegionDatacenter {
	Integer regDctId;
	String regNombre;
	Integer regId;
	
	public RegionDatacenter(Integer regDctId, String regNombre, Integer regId) {
		super();
		this.regDctId = regDctId;
		this.regNombre = regNombre;
		this.regId = regId;
	}
	public Integer getRegDctId() {
		return regDctId;
	}
	public void setRegDctId(Integer regDctId) {
		this.regDctId = regDctId;
	}
	public String getRegNombre() {
		return regNombre;
	}
	public void setRegNombre(String regNombre) {
		this.regNombre = regNombre;
	}
	public Integer getRegId() {
		return regId;
	}
	public void setRegId(Integer regId) {
		this.regId = regId;
	}
	
	public Double computeStorageCost(Double dataSize) {
		//TODO: implementar!!!
		return 0.0;
	}

	public Double computeTransferCost(Double dataSize) {
		//TODO: implementar!!!
		return 0.0;
	}

	public Double computeVMCost(int vmtype) {
		//TODO: implementar!!!
		return 0.0;
	}
}
