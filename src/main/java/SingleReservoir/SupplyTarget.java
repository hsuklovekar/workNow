package SingleReservoir;

import java.util.List;

public class SupplyTarget {

    // 供水节点
    private String supplyNodeId;

    //节点需水
    private double demand;
    // 供水类型
    private SupplyType supplyType;

    // 最大供水规模 (m³/s)
    //private double maxSupplyScale;

    // 起供库容
    private double startSupplyStorageLimit;

    // 供水库容下限
    private double lowerStorageLimit;

    // 供水优先级（数值越小优先级越高）
    private int priority;

    // 供水管道路径，排好序的管道，从供水节点到目标节点的管道路径
    private List<PipeParam> pipeParamList;

    //  需求打折系数
    private double coefficient;

    public SupplyTarget(String supplyNodeId,
                        double demand,
                        SupplyType supplyType,
                        //double maxSupplyScale,
                        double startSupplyStorageLimit,
                        double lowerStorageLimit,
                        int priority,
                        double coefficient,
                        List<PipeParam> pipeParamList) {
        this.supplyNodeId = supplyNodeId;
        this.demand = demand;
        this.supplyType = supplyType;
        //this.maxSupplyScale = maxSupplyScale;
        this.startSupplyStorageLimit = startSupplyStorageLimit;
        this.lowerStorageLimit = lowerStorageLimit;
        this.priority = priority;
        this.coefficient = coefficient;
        this.pipeParamList = pipeParamList;
    }
    public void setSupplyNodeId(String supplyNodeId) {
        this.supplyNodeId = supplyNodeId;
    }

    public void setDemand(double demand) {
        this.demand = demand;
    }

    public void setSupplyType(SupplyType supplyType) {
        this.supplyType = supplyType;
    }

//    public void setMaxSupplyScale(double maxSupplyScale) {
//        return getUserSupplyScale(this.pipeParamList);
//    }

    public void setStartSupplyStorageLimit(double startSupplyStorageLimit) {
        this.startSupplyStorageLimit = startSupplyStorageLimit;
    }
    public void setCoefficient(double coefficient) {this.coefficient = coefficient;}

    public void setLowerStorageLimit(double lowerStorageLimit) {
        this.lowerStorageLimit = lowerStorageLimit;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public void setPipeParamList(List<PipeParam> pipeParamList) {this.pipeParamList = pipeParamList;}

    public String getSupplyNodeId() {
        return supplyNodeId;
    }

    public double getDemand() {
        return demand;
    }

    public double getCoefficient(){
        return coefficient;
    }

    public SupplyType getSupplyType() {
        return supplyType;
    }

    public double getMaxSupplyScale() {
        return getUserSupplyScale(this.pipeParamList);
    }

    public double getStartSupplyStorageLimit() {
        return startSupplyStorageLimit;
    }

    public double getLowerStorageLimit() {
        return lowerStorageLimit;
    }

    public int getPriority() {
        return priority;
    }

    public List<PipeParam> getPipeParamList() {return pipeParamList;}


    public Double getUserSupplyScale(List<PipeParam> pipeList) {
        //Double userSupplyScale = 10000.0;
        if (pipeList == null || pipeList.isEmpty()) {
            return 0.0;
        }

        // 初始化最小值为列表第一个元素的可用规模
        Double minCapacity = pipeList.get(0).getAvailableCapacity();

        for (PipeParam pipe : pipeList) {
            if (pipe.getAvailableCapacity() < minCapacity) {
                minCapacity = pipe.getAvailableCapacity();
            }
        }
        return minCapacity;
    }
}
