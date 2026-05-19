package SingleReservoir;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 调度图：包含水库名和多个调度区、多个随时间变化的调度线。
 */
public class DDT {

    // 水库名称
    private String reservoirName;

    // 调度区列表
    private List<DDQ> scheduleZones;

    public DDT(String reservoirName, List<DDQ> scheduleZones) {
        this.reservoirName = reservoirName;
        this.scheduleZones = scheduleZones == null ? new ArrayList<>() : scheduleZones;
    }

    public void addDDQ(DDQ ddq) {
        if (this.scheduleZones == null) {
            this.scheduleZones = new ArrayList<>();
        }
        this.scheduleZones.add(ddq);
    }

    public String getReservoirName() {
        return reservoirName;
    }

    public void setReservoirName(String reservoirName) {
        this.reservoirName = reservoirName;
    }

    public List<DDQ> getScheduleZones() {
        return scheduleZones;
    }

    public void setScheduleZones(List<DDQ> scheduleZones) {
        this.scheduleZones = scheduleZones;
    }

    /**
     * 根据当前日期和当前水位匹配调度区，并返回打折后的新供水目标列表。
     * 注意：该方法不会修改原始 targets 列表中的对象。
     */
    public List<SupplyTarget> applyCoefficientsNewList(LocalDate currentDate,
                                                        double currentWaterLevel,
                                                        List<SupplyTarget> targets) {
        List<SupplyTarget> result = new ArrayList<>();
        if (targets == null || targets.isEmpty()) {
            return result;
        }

        DDQ matchedZone = findMatchedZone(currentDate, currentWaterLevel);
        Map<String, Double> coefficientMap = matchedZone == null ? null : matchedZone.getTaskCoefficients();

        for (SupplyTarget target : targets) {
            // 先拷贝原对象字段，确保不修改原对象
            SupplyTarget copiedTarget = new SupplyTarget(
                    target.getSupplyNodeId(),
                    target.getDemand(),
                    target.getSupplyType(),
                    target.getStartSupplyStorageLimit(),
                    target.getLowerStorageLimit(),
                    target.getPriority(),
                    target.getCoefficient(),
                    target.getPipeParamList()
            );

            // 新逻辑：按 supplyType 的枚举名匹配“乘数系数”，再与原始系数相乘
            double multiplier = 1.0;
            if (coefficientMap != null && target.getSupplyType() != null) {
                String taskTypeName = target.getSupplyType().name();
                multiplier = coefficientMap.getOrDefault(taskTypeName, 1.0);
            }
            double newCoefficient = target.getCoefficient() * multiplier;
            copiedTarget.setCoefficient(newCoefficient);
            result.add(copiedTarget);
        }

        return result;
    }

    private DDQ findMatchedZone(LocalDate currentDate, double currentWaterLevel) {
        if (scheduleZones == null || scheduleZones.isEmpty() || currentDate == null) {
            return null;
        }

        for (DDQ zone : scheduleZones) {
            boolean inDateRange = !currentDate.isBefore(zone.getStartDate()) && !currentDate.isAfter(zone.getEndDate());
            boolean inWaterLevelRange = currentWaterLevel >= zone.getLowerWaterLevel()
                    && currentWaterLevel <= zone.getUpperWaterLevel();

            if (inDateRange && inWaterLevelRange) {
                return zone;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return "DDT{" +
                "reservoirName='" + reservoirName + '\'' +
                ", scheduleZones=" + scheduleZones +
                '}';
    }
}
