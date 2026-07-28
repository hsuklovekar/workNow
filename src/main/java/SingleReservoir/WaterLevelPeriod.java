package SingleReservoir;

import java.time.LocalDate;

public class WaterLevelPeriod {

    private String startTime;
    private String endTime;
    private double waterLevel;

    public WaterLevelPeriod() {
    }

    public WaterLevelPeriod(String startTime,
                            String endTime,
                            double waterLevel) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.waterLevel = waterLevel;
    }

    /**
     * 判断给定日期的月日是否在当前时间段范围内（支持跨年）
     *
     * @param time 待判断的日期
     * @return true 如果 time 的月日在 [startTime, endTime) 范围内
     */
    public boolean contains(LocalDate time) {
        if (time == null || startTime == null || endTime == null) {
            return false;
        }

        // 解析时间段的月日
        int[] start = parseMonthDay(startTime);
        int[] end = parseMonthDay(endTime);

        if (start == null || end == null) {
            return false;
        }

        int startMonth = start[0];
        int startDay = start[1];
        int endMonth = end[0];
        int endDay = end[1];

        // 获取待判断日期的月日
        int targetMonth = time.getMonthValue();
        int targetDay = time.getDayOfMonth();

        // 判断是否在范围内
        if (startMonth <= endMonth) {
            // 不跨年情况：如 05-01 到 10-31
            boolean afterStart = (targetMonth > startMonth) ||
                    (targetMonth == startMonth && targetDay >= startDay);
            boolean beforeEnd = (targetMonth < endMonth) ||
                    (targetMonth == endMonth && targetDay < endDay);
            return afterStart && beforeEnd;
        } else {
            // 跨年情况：如 12-01 到 02-28
            // 判断是否在上半年部分（1月到endMonth）或下半年部分（startMonth到12月）
            boolean inFirstPart = (targetMonth <= endMonth) &&
                    !(targetMonth == endMonth && targetDay >= endDay);
            boolean inSecondPart = (targetMonth >= startMonth) &&
                    (targetMonth > startMonth || targetDay >= startDay);
            return inFirstPart || inSecondPart;
        }
    }

    /**
     * 解析 mm-dd 格式的字符串为 [月份, 日期] 数组
     *
     * @param dateStr 格式为 "mm-dd" 的字符串
     * @return int[2]，第一个元素是月份，第二个是日期；解析失败返回 null
     */
    private int[] parseMonthDay(String dateStr) {
        if (dateStr == null || dateStr.length() != 5 || dateStr.charAt(2) != '-') {
            return null;
        }
        try {
            int month = Integer.parseInt(dateStr.substring(0, 2));
            int day = Integer.parseInt(dateStr.substring(3, 5));

            // 简单的日期有效性检查
            if (month < 1 || month > 12 || day < 1 || day > 31) {
                return null;
            }

            return new int[]{month, day};
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public double getWaterLevel() {
        return waterLevel;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public void setWaterLevel(double waterLevel) {
        this.waterLevel = waterLevel;
    }
}
