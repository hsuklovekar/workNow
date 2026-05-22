package SingleReservoir;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DDX {

    private List<WaterLevelPeriod> periods = new ArrayList<>();

    /**
     * 添加一个时间段水位
     */
    public void addPeriod(LocalDate startTime,
                          LocalDate endTime,
                          double waterLevel) {

        periods.add(new WaterLevelPeriod(
                startTime,
                endTime,
                waterLevel
        ));
    }

    /**
     * 根据时间获取对应水位
     */
    public Double getWaterLevel(LocalDate currentTime) {

        for (WaterLevelPeriod period : periods) {

            if (period.contains(currentTime)) {
                return period.getWaterLevel();
            }
        }

        return null;
    }

    public List<WaterLevelPeriod> getPeriods() {
        return periods;
    }

    public void setPeriods(List<WaterLevelPeriod> periods) {
        this.periods = periods;
    }
}
