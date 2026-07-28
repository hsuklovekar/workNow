package SingleReservoir;

public class SupplyActually {
    // 供水节点
    private String supplyNodeId;
    // 实际供水量
    private double effectiveSupply;
    // 最大可充水量
    //private double maxCharge;
    public SupplyActually(String supplyNodeId, double effectiveSupply) {
        this.supplyNodeId = supplyNodeId;
        this.effectiveSupply = effectiveSupply;
        //this.maxCharge = maxCharge;
    }
    public String getSupplyNodeId() {
        return supplyNodeId;
    }
    public double getEffectiveSupply() {
        return effectiveSupply;
    }
    //public double getMaxCharge() { return  maxCharge; }

    public void seteffectiveSupply(double effectiveSupply) {
        this.effectiveSupply = effectiveSupply;
    }

}
