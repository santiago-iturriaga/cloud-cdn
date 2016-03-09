package jmetal.problems.cloudcdn.f201603;

public class RegionDatacenter {
	int regDctId;
	String regNombre;
	int regId;

	// These costs are for every time step
	double vmPrice;
	double storagePrice;
	double transferPrice;

	public RegionDatacenter(int regDctId, String regNombre, int regId,
			double storagePrice, double transferPrice, double vmPrice) {
		super();
		
		this.regDctId = regDctId;
		this.regNombre = regNombre;
		this.regId = regId;

		this.vmPrice = vmPrice;
		this.storagePrice = storagePrice;
		this.transferPrice = transferPrice;
	}

	public int getRegDctId() {
		return regDctId;
	}

	public void setRegDctId(int regDctId) {
		this.regDctId = regDctId;
	}

	public String getRegNombre() {
		return regNombre;
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

	public double computeStorageCost(double dataSize) {
		//TODO: modificar para considerar escala logarítmica
		return this.storagePrice * dataSize;
	}

	public double computeTransferCost(double dataSize) {
		//TODO: modificar para considerar escala logarítmica
		return this.transferPrice * dataSize;
	}

	public double computeVMCost(int numVM) {
		//TODO: modificar para considerar escala logarítmica
		return this.vmPrice * numVM;
	}
}
