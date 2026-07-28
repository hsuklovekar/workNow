package SingleReservoir;
import java.security.Key;
import java.util.List;
import java.util.Map;

public class NodeResult {
    private Map<String,SupplyActually> supplyActuallys;
    private ChargeDemand chargeDemand;
    private Double storageFinal;
    private Double evaporationLossWater;//蒸发渗漏损失
    private Double ecologyWaterSupply;//生态供水

    public NodeResult(Map<String,SupplyActually> supplyActuallys,
                      ChargeDemand chargeDemand,
                      Double storageFinal,
                      Double vaporationLossWater,
                      Double ecologyWaterSupply) {
        this.supplyActuallys = supplyActuallys;
        this.chargeDemand = chargeDemand;
        this.storageFinal = storageFinal;
        this.evaporationLossWater = vaporationLossWater;
        this.ecologyWaterSupply = ecologyWaterSupply;
    }

    public Map<String,SupplyActually> getSupplyActuallys() {
        return supplyActuallys;
    }

    public ChargeDemand  getChargeDemand() {
        return chargeDemand;
    }
    public Double getStorageFinal() {
        return storageFinal;
    }
    public Double getEvaporationLossWater() {
        return evaporationLossWater;
    }
    public Double getEcologyWaterSupply() {
        return ecologyWaterSupply;
    }
}
