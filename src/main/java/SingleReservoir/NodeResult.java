package SingleReservoir;
import java.security.Key;
import java.util.List;
import java.util.Map;

public class NodeResult {
    private Map<String,SupplyActually> supplyActuallys;
    private ChargeDemand chargeDemand;
    private Double storageFinal;

    public NodeResult(Map<String,SupplyActually> supplyActuallys, ChargeDemand chargeDemand, Double storageFinal) {
        this.supplyActuallys = supplyActuallys;
        this.chargeDemand = chargeDemand;
        this.storageFinal = storageFinal;
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
}
