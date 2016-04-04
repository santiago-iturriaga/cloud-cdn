package jmetal.problems.cloudcdn.f201603;

public class RegionDatacenter {

    static double TOTAL_STORAGE;
    static double TOTAL_TRANSFER;
    
    int regDctId;
    String regNombre;
    int regId;

    // These costs are for every time step
    double vmPrice;
    double vmResPrice;
    double vmResUpfrontPrice;
    double storagePrice;
    double transferPrice;

    public RegionDatacenter(int regDctId, String regNombre, int regId,
            double storagePrice, double transferPrice, double vmPrice,
            double vmResPrice, double vmResUpfrontPrice) {
        super();

        this.regDctId = regDctId;
        this.regNombre = regNombre;
        this.regId = regId;

        this.vmPrice = vmPrice;
        this.vmResPrice = vmResPrice;
        this.vmResUpfrontPrice = vmResUpfrontPrice;
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
        return (this.storagePrice * dataSize) * (Math.log10((dataSize / TOTAL_STORAGE) * 10) * 0.08);
    }

    public double computeTransferCost(double dataSize) {
        return (this.transferPrice * dataSize) * (Math.log10((dataSize / TOTAL_TRANSFER) * 10) * 0.2);
    }

    public double computeVMCost(int numVM) {
        return this.vmPrice * numVM;
    }
    
    public double computeResVMCost(int numVM) {
        return this.vmResPrice * numVM;
    }
    
    public double computeResUpfrontVMCost(int numVM) {
        return this.vmResUpfrontPrice * numVM;
    }
}
