package SingleReservoir;

import java.time.LocalDate;

public class WaterLevelPeriod {

    private LocalDate startTime;
    private LocalDate endTime;
    private double waterLevel;

    public WaterLevelPeriod() {
    }

    public WaterLevelPeriod(LocalDate startTime,
                            LocalDate endTime,
                            double waterLevel) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.waterLevel = waterLevel;
    }

    public boolean contains(LocalDate time) {
        return (time.isEqual(startTime) || time.isAfter(startTime))
                && time.isBefore(endTime);
    }

    public double getWaterLevel() {
        return waterLevel;
    }

    public LocalDate getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDate startTime) {
        this.startTime = startTime;
    }

    public LocalDate getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDate endTime) {
        this.endTime = endTime;
    }

    public void setWaterLevel(double waterLevel) {
        this.waterLevel = waterLevel;
    }
}