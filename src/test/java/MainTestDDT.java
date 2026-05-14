import SingleReservoir.DDQ;
import SingleReservoir.DDT;
import SingleReservoir.PipeParam;
import SingleReservoir.SupplyTarget;
import SingleReservoir.SupplyType;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DDT/DDQ/SupplyTarget 功能示例测试。
 *
 * 说明：
 * 1) 本测试直接使用工程内已有的 SupplyType 与 PipeParam 定义。
 * 2) 通过 main 方法可独立运行并打印每个场景的输入/输出。
 */
public class MainTestDDT {

    public static void main(String[] args) {
        // -----------------------------
        // Step 1: 构建两个 DDQ（日期区间、水位区间、任务系数均不同）
        // -----------------------------
        Map<String, Double> coefficientsZone1 = new HashMap<>();
        coefficientsZone1.put(SupplyType.DOMESTIC.name(), 0.80);
        coefficientsZone1.put(SupplyType.AGRICULTURE.name(), 0.60);

        DDQ zone1 = new DDQ(
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 6, 30),
                120.0,
                100.0,
                coefficientsZone1
        );

        Map<String, Double> coefficientsZone2 = new HashMap<>();
        coefficientsZone2.put(SupplyType.DOMESTIC.name(), 0.95);
        coefficientsZone2.put(SupplyType.AGRICULTURE.name(), 0.75);

        DDQ zone2 = new DDQ(
                LocalDate.of(2026, 7, 1),
                LocalDate.of(2026, 12, 31),
                99.9,
                80.0,
                coefficientsZone2
        );

        // -----------------------------
        // Step 2: 构建 DDT 并添加 DDQ
        // -----------------------------
        DDT ddt = new DDT("DemoReservoir", new ArrayList<>());
        ddt.addDDQ(zone1);
        ddt.addDDQ(zone2);

        // -----------------------------
        // Step 3: 构建原始 SupplyTarget 列表（至少两个且 SupplyType 不同）
        // -----------------------------
        List<PipeParam> pipeParams = new ArrayList<>();
        pipeParams.add(new PipeParam("P-01", 100.0, 80.0, 20.0));

        SupplyTarget t1 = new SupplyTarget(
                "NODE-A",
                120.0,
                SupplyType.DOMESTIC,
                200.0,
                150.0,
                1,
                1.0,
                pipeParams
        );

        SupplyTarget t2 = new SupplyTarget(
                "NODE-B",
                180.0,
                SupplyType.AGRICULTURE,
                220.0,
                160.0,
                2,
                1.0,
                pipeParams
        );

        List<SupplyTarget> originalTargets = new ArrayList<>();
        originalTargets.add(t1);
        originalTargets.add(t2);

        // 场景一：命中第一个 DDQ
        runScenario(
                "Scenario-1 (match zone1)",
                ddt,
                LocalDate.of(2026, 3, 15),
                110.0,
                originalTargets
        );

        // 场景二：命中第二个 DDQ
        runScenario(
                "Scenario-2 (match zone2)",
                ddt,
                LocalDate.of(2026, 9, 10),
                90.0,
                originalTargets
        );

        // 场景三：不命中任何 DDQ（验证默认系数 1.0）
        runScenario(
                "Scenario-3 (no zone matched, default 1.0)",
                ddt,
                LocalDate.of(2027, 1, 1),
                130.0,
                originalTargets
        );

        // 额外验证：原始列表对象未被修改
        System.out.println("\n=== Verify original list remains unchanged ===");
        printTargets("Original targets after all scenarios", originalTargets);
    }

    /**
     * 执行单个测试场景：
     * 1) 打印输入（原始 targets）
     * 2) 调用 applyCoefficientsNewList
     * 3) 打印输出（新 targets）
     */
    private static void runScenario(String scenarioName,
                                    DDT ddt,
                                    LocalDate currentDate,
                                    double currentWaterLevel,
                                    List<SupplyTarget> originalTargets) {
        System.out.println("\n==================== " + scenarioName + " ====================");
        System.out.println("Input currentDate=" + currentDate + ", currentWaterLevel=" + currentWaterLevel);

        printTargets("Original targets (before apply)", originalTargets);

        List<SupplyTarget> updatedTargets = ddt.applyCoefficientsNewList(currentDate, currentWaterLevel, originalTargets);
        printTargets("Updated targets (after apply)", updatedTargets);
    }

    /**
     * 按要求打印 SupplyType、supplyNodeId、coefficient（并附带 demand 便于观察）。
     */
    private static void printTargets(String title, List<SupplyTarget> targets) {
        System.out.println("-- " + title + " --");
        for (SupplyTarget target : targets) {
            System.out.println("supplyNodeId=" + target.getSupplyNodeId()
                    + ", supplyType=" + target.getSupplyType()
                    + ", coefficient=" + target.getCoefficient()
                    + ", demand=" + target.getDemand());
        }
    }
}
