package jmetal.problems.cloudcdn.f201603;

public class RegionDatacenter {

    public static double TOTAL_STORAGE;
    public static double TOTAL_TRANSFER;
    
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
        double discount;
        double logfactor;
        
        double dataSizeGB;
        dataSizeGB = dataSize / 1024;
        
        logfactor = Math.log10((dataSizeGB / TOTAL_STORAGE) * 10);
        if (logfactor > 0)
            discount = 1 - (logfactor * 0.08);
        else
            discount = 1;
        
        return (this.storagePrice * dataSizeGB) * discount;
    }

    public double computeTransferCost(double dataSize) {
        double discount;
        double logfactor;
        
        double dataSizeGB;
        dataSizeGB = dataSize / 1024;
        
        logfactor = Math.log10((dataSizeGB / TOTAL_TRANSFER) * 10);
        if (logfactor > 0)
            discount = 1 - (logfactor * 0.2);
        else
            discount = 1;
        
        return (this.transferPrice * dataSizeGB) * discount;
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
