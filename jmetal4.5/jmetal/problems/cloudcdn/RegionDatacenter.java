package jmetal.problems.cloudcdn;

public class RegionDatacenter {
	int regDctId;
	String regNombre;
	int regId;

	double vmTypePrices[];
	int maxVmType = -1;

	double storageCostLimits[];
	double storageCostValues[];

	double transferCostLimits[];
	double transferCostValues[];

	public RegionDatacenter(int regDctId, String regNombre, int regId,
			String storageCostFunction, String transferCostFunction) {
		super();
		this.regDctId = regDctId;
		this.regNombre = regNombre;
		this.regId = regId;

		vmTypePrices = new double[CloudCDN_SO.CANTIDAD_MAXIMA_DE_MAQUINAS];

		loadStorageCostFuction(storageCostFunction);
		loadTransferCostFuction(transferCostFunction);
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

	public void printVmCosts() {
		for (int i = 0; i < maxVmType; i++) {
			if (hasVmType(i)) {
				System.out.println("VM Cost: DC=" + this.regDctId + " VMType="
						+ i + " Price=" + computeVMCost(i));
			}
		}
	}

	public void setVMCost(int vmType, double cost) {
		if (vmType > maxVmType)
			maxVmType = vmType;
		vmTypePrices[vmType] = cost;
	}

	public void loadStorageCostFuction(String function) {
		String[] data = function.trim().split("|");

		storageCostValues = new double[data.length];
		storageCostLimits = new double[data.length];

		String[] pair;
		for (int i = 0; i < data.length; i++) {
			pair = data[i].trim().split(",");
			if (!pair[0].equals("-")) {
				storageCostLimits[i] = Double.valueOf(pair[0]) * 1024; // GB
			} else {
				storageCostLimits[i] = Double.MAX_VALUE;
			}
			storageCostValues[i] = Double.valueOf(pair[1]);
		}
	}

	public void loadTransferCostFuction(String function) {
		String[] data = function.trim().split("|");

		transferCostValues = new double[data.length];
		transferCostLimits = new double[data.length];

		String[] pair;
		for (int i = 0; i < data.length; i++) {
			pair = data[i].trim().split(",");
			if (!pair[0].equals("-")) {
				transferCostLimits[i] = Double.valueOf(pair[0]) * 1024; // GB 
			} else {
				transferCostLimits[i] = Double.MAX_VALUE;
			}
			transferCostValues[i] = Double.valueOf(pair[1]);
		}
	}

	public double computeStorageCost(double dataSize) {
		int currentIndex = 0;
		
		double currentLimit, currentValue;
		currentLimit = storageCostLimits[0];
		currentValue = storageCostValues[0];
		
		while (currentLimit < dataSize) {
			currentIndex++;
			currentLimit = storageCostLimits[currentIndex];
			currentValue = storageCostValues[currentIndex];
		}
		
		return currentValue;
	}

	public double computeTransferCost(double dataSize) {
		int currentIndex = 0;
		
		double currentLimit, currentValue;
		currentLimit = transferCostLimits[0];
		currentValue = transferCostValues[0];
		
		while (currentLimit < dataSize) {
			currentIndex++;
			currentLimit = transferCostLimits[currentIndex];
			currentValue = transferCostValues[currentIndex];
		}
		
		return currentValue;
	}

	public boolean hasVmType(int vmType) {
		return vmTypePrices[vmType] > 0;
	}

	public double computeVMCost(int vmType) {
		return vmTypePrices[vmType];
	}
}
