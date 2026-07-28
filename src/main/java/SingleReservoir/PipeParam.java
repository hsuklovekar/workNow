package SingleReservoir;


/**
 * 管道信息
 */
public class PipeParam {
    // 管道ID
    private String pipeId;
    // 管道最大容量
    private Double maxCapacity;
    // 已占用容量
    private Double usedCapacity = 0.0;

    public PipeParam() {
    }

    public PipeParam(String pipeId,
                     Double maxCapacity,
                     Double usedCapacity) {
        this.pipeId = pipeId;
        this.maxCapacity = maxCapacity;
        this.usedCapacity = usedCapacity;
    }

    // 管道可用容量 = 最大容量 - 已占用容量
    public Double availableCapacity() {
        return Math.max(maxCapacity - usedCapacity, 0.0);
    }

    public String getPipeId() {
        return pipeId;
    }

    public void setPipeId(String pipeId) {
        this.pipeId = pipeId;
    }

    public Double getMaxCapacity() {
        return maxCapacity;
    }

    public void setMaxCapacity(Double maxCapacity) {
        this.maxCapacity = maxCapacity;
    }

    public void setAvailableCapacity(Double availableCapacity) {
    }

    public Double getUsedCapacity() {
        return usedCapacity;
    }

    public void setUsedCapacity(Double usedCapacity) {
        this.usedCapacity = usedCapacity;
    }
}