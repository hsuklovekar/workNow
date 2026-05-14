package SingleReservoir;

import java.time.LocalDate;
import java.util.Map;

/**
 * 调度区：定义某个时间段和水位范围下，不同任务类型的打折系数。
 */
public class DDQ {

    // 调度区起始日期
    private LocalDate startDate;

    // 调度区结束日期
    private LocalDate endDate;

    // 上水位
    private double upperWaterLevel;

    // 下水位
    private double lowerWaterLevel;

    // 每种任务的打折系数，key为任务类型名称，value为系数
    private Map<String, Double> taskCoefficients;

    public DDQ(LocalDate startDate,
               LocalDate endDate,
               double upperWaterLevel,
               double lowerWaterLevel,
               Map<String, Double> taskCoefficients) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.upperWaterLevel = upperWaterLevel;
        this.lowerWaterLevel = lowerWaterLevel;
        this.taskCoefficients = taskCoefficients;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public double getUpperWaterLevel() {
        return upperWaterLevel;
    }

    public void setUpperWaterLevel(double upperWaterLevel) {
        this.upperWaterLevel = upperWaterLevel;
    }

    public double getLowerWaterLevel() {
        return lowerWaterLevel;
    }

    public void setLowerWaterLevel(double lowerWaterLevel) {
        this.lowerWaterLevel = lowerWaterLevel;
    }

    public Map<String, Double> getTaskCoefficients() {
        return taskCoefficients;
    }

    public void setTaskCoefficients(Map<String, Double> taskCoefficients) {
        this.taskCoefficients = taskCoefficients;
    }

    @Override
    public String toString() {
        return "DDQ{" +
                "startDate=" + startDate +
                ", endDate=" + endDate +
                ", upperWaterLevel=" + upperWaterLevel +
                ", lowerWaterLevel=" + lowerWaterLevel +
                ", taskCoefficients=" + taskCoefficients +
                '}';
    }
}
