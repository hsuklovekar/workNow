package SingleReservoir;


/**
 * 管道信息
 */
public class PipeParam {
    // 管道ID
    private String pipeId;
    // 管道最大容量
    private Double maxCapacity;
    // 管道可用容量
    private Double availableCapacity = 0.0;
    // 已占用容量
    private Double usedCapacity = 0.0;

    public PipeParam(String pipeId,
                     Double maxCapacity,
                     Double availableCapacity,
                     Double usedCapacity) {
        this.pipeId = pipeId;
        this.maxCapacity = maxCapacity;
        this.availableCapacity = availableCapacity;
        this.usedCapacity = usedCapacity;
    }
    public Double getAvailableCapacity() {
        return availableCapacity;
    }
}
