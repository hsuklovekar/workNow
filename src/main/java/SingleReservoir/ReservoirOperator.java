package SingleReservoir;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * 算子名称：单库供水算子（ReservoirOperator）。
 *
 * <p>功能描述：</p>
 * <ul>
 *   <li>根据上游输入的天然来水与供水目标，结合水库初始库容、死库容、正常蓄水位对应库容、蒸发损失与生态流量参数，计算各供水对象的实际供水量。</li>
 *   <li>计算时段末库容、弃水量，以及（在允许充库时）可腾出的最大充库空间。</li>
 *   <li>按平台规范返回统一结构：status / message / data。</li>
 * </ul>
 *
 * <p>输入契约（inputParams）：</p>
 * <ul>
 *  <li><b>inputNatural</b> (Number, 默认 0.0)：时段天然来水量。</li>
 *    <li><b>targetList</b> (List&lt;Map&lt;String, Object&gt;&gt;, 默认空列表)：供水目标集合，每个元素对应一个 SupplyTarget 结构，字段如下：
 *      <ul>
 *       <li><b>supplyNodeId</b> (String)：供水节点 ID。</li>
 *       <li><b>demand</b> (Number)：节点需水量。</li>
 *       <li><b>supplyType</b> (String/Number，可选)：供水类型（枚举编码或名称，当前算子不参与计算，仅透传/扩展位）。</li>
 *       <li><b>startSupplyStorageLimit</b> (Number)：起供库容门槛，当前库容未达到门槛时不参与供水分配。</li>
 *       <li><b>lowerStorageLimit</b> (Number，可选)：供水库容下限（当前版本预留字段）。</li>
 *       <li><b>priority</b> (Number)：供水优先级，数值越小优先级越高。</li>
 *       <li><b>pipeParamList</b> (List&lt;Map&lt;String, Object&gt;&gt;，可选)：从供水节点到目标节点的有序管道参数列表。</li>
 *       <li><b>coefficient</b> (Number，可选，默认 1.0)：需求打折系数（当前版本预留字段；如需启用可在 demand 前置折算）。</li>
 *     </ul>
 *   </li>
 * </ul>
 *
 * <p>参数配置契约（operatorParams）：</p>
 * <ul>
 *   <li>id (String)：水库 ID，默认 "UNKNOWN_RESERVOIR"。</li>
 *   <li>storageIntial (Number)：时段初库容，默认 0.0。</li>
 *   <li>siStorage (Number)：死库容，默认 0.0。</li>
 *   <li>zhengChangStorage (Number)：正常蓄水库容对应库容，默认 Double.MAX_VALUE。</li>
 *   <li>evaporationLossCoefficient (Number)：蒸散发损失系数，默认 0.05。</li>
 *   <li>meanAnnualRunoff (Number)：多年平均径流量，默认 0.0。</li>
 *   <li>ecologicalCoefficient (Number)：生态流量系数，默认 0.1。</li>
 *   <li>timeStep (Number)：时间步长（小时），默认 24（按逐日进行调度）。</li>
 *   <li>isCharge (Boolean)：是否允许充库，默认 false。</li>
 *   <li>waterCharge (Number)：上时段充库水量，默认 0.0。</li>
 *   <li>chargeStorage (Number)：停冲水位对应库容，默认与 zhengChangStorage 相同。</li>
 * </ul>
 *
 * <p>输出契约（返回 Map&lt;String, Object&gt;）：</p>
 * <ul>
 *   <li>status (Boolean)：执行状态。true 成功，false 失败。</li>
 *   <li>message (String)：状态信息。失败时包含错误原因。</li>
 *   <li>data (Map)：业务结果，字段包含：
 *     <ul>
 *       <li>reservoirId (String)：水库 ID。</li>
 *       <li>storageFinal (Double)：时段末库容。</li>
 *       <li>waterSurplus (Double)：弃水量。</li>
 *       <li>maxCharge (Double)：最大可充库容（仅允许充库时有意义）。</li>
 *       <li>supplyResults (Map&lt;String, Map&lt;String, Object&gt;&gt;)：各供水节点供水结果（supply/lack）。</li>
 *     </ul>
 *   </li>
 * </ul>
 */
public class ReservoirOperator {
    public Map<String, Object> execute(Map<String, Object> inputParams, Map<String, Object> operatorParams) {
        Map<String, Object> result = new HashMap<>();
        Map<String, Object> data = new HashMap<>();
        result.put("status", false);
        result.put("message", "INIT");
        result.put("data", data);

        try {
            Map<String, Object> safeInput = inputParams == null ? new HashMap<>() : inputParams;
            Map<String, Object> safeOperator = operatorParams == null ? new HashMap<>() : operatorParams;

            String reservoirId = String.valueOf(safeOperator.getOrDefault("id", "UNKNOWN_RESERVOIR"));
            double storageIntial = toDouble(safeOperator.getOrDefault("storageIntial", 0.0));
            double siStorage = toDouble(safeOperator.getOrDefault("siStorage", 0.0));
            double zhengChangStorage = toDouble(safeOperator.getOrDefault("zhengChangStorage", Double.MAX_VALUE));
            double evaporationLossCoefficient = toDouble(safeOperator.getOrDefault("evaporationLossCoefficient", 0.0));
            double meanAnnualRunoff = toDouble(safeOperator.getOrDefault("meanAnnualRunoff", 0.0));
            double ecologicalCoefficient = toDouble(safeOperator.getOrDefault("ecologicalCoefficient", 0.0));
            int timeStep = toInt(safeOperator.getOrDefault("timeStep", 24));
            boolean isCharge = toBoolean(safeOperator.getOrDefault("isCharge", false));
            double waterCharge = toDouble(safeOperator.getOrDefault("waterCharge", 0.0));
            double chargeStorage = toDouble(safeOperator.getOrDefault("chargeStorage", zhengChangStorage));

            double inputNatural = toDouble(safeInput.getOrDefault("inputNatural", 0.0));

            List<Map<String, Object>> targetList = new ArrayList<>();
            Object rawTargetList = safeInput.getOrDefault("targetList", new ArrayList<>());
            if (rawTargetList instanceof List) {
                targetList = (List<Map<String, Object>>) rawTargetList;
            }

            double availableWater = storageIntial - siStorage + inputNatural;
            double evaporationLossWater = inputNatural * evaporationLossCoefficient;
            availableWater -= evaporationLossWater;

            double ecologicalDemand = meanAnnualRunoff / 365.0 * (timeStep / 24.0) * ecologicalCoefficient;
            double ecologicalSupply = Math.min(ecologicalDemand, Math.max(0.0, inputNatural - evaporationLossWater));
            availableWater -= ecologicalSupply;

            if (isCharge) {
                availableWater += waterCharge;
            }

            double availableWaterInitial = availableWater;

            Map<Integer, List<Map<String, Object>>> grouped = targetList.stream()
                    .filter(item -> item != null)
                    .sorted(Comparator.comparingInt(item -> toInt(item.getOrDefault("priority", Integer.MAX_VALUE))))
                    .collect(Collectors.groupingBy(
                            item -> toInt(item.getOrDefault("priority", Integer.MAX_VALUE)),
                            TreeMap::new,
                            Collectors.toList()
                    ));

            Map<String, Object> supplyResults = new HashMap<>();
            double supplyAll = 0.0;

            for (Map.Entry<Integer, List<Map<String, Object>>> entry : grouped.entrySet()) {
                List<Map<String, Object>> group = entry.getValue();

                List<Map<String, Object>> validTargets = new ArrayList<>();
                for (Map<String, Object> target : group) {
                    double startLimit = toDouble(target.getOrDefault("startSupplyStorageLimit", 0.0));
                    if (availableWater > 0 && startLimit <= storageIntial) {
                        validTargets.add(target);
                    }
                }

                double totalDemand = validTargets.stream()
                        .mapToDouble(t -> toDouble(t.getOrDefault("demand", 0.0)))
                        .sum();

                double used = 0.0;
                for (Map<String, Object> target : group) {
                    String supplyNodeId = String.valueOf(target.getOrDefault("supplyNodeId", "UNKNOWN_NODE"));
                    double demand = toDouble(target.getOrDefault("demand", 0.0));
                    double supply = 0.0;

                    if (totalDemand > 0 && availableWater > 0) {
                        double ratio = demand / totalDemand;
                        double allocWater = availableWater * ratio;
                        supply = Math.min(Math.max(allocWater, 0.0), Math.max(demand, 0.0));
                    }

                    double lack = demand - supply;
                    Map<String, Object> nodeResult = new HashMap<>();
                    nodeResult.put("supply", supply);
                    nodeResult.put("lack", lack);
                    nodeResult.put("priority", toInt(target.getOrDefault("priority", Integer.MAX_VALUE)));
                    supplyResults.put(supplyNodeId, nodeResult);

                    used += supply;
                    supplyAll += supply;
                }

                availableWater -= used;
            }

            double storageFinal = availableWaterInitial - supplyAll + siStorage;
            double waterSurplus = 0.0;
            if (storageFinal > zhengChangStorage) {
                waterSurplus = storageFinal - zhengChangStorage;
                storageFinal = zhengChangStorage;
            }
            storageFinal = Math.max(siStorage, storageFinal);

            double maxCharge = 0.0;
            if (isCharge) {
                maxCharge = Math.max(0.0, chargeStorage - storageFinal);
            }

            data.put("reservoirId", reservoirId);
            data.put("storageFinal", storageFinal);
            data.put("waterSurplus", waterSurplus);
            data.put("maxCharge", maxCharge);
            data.put("supplyResults", supplyResults);
            data.put("availableWaterAfterEco", Math.max(0.0, availableWaterInitial));
            data.put("ecologicalSupply", ecologicalSupply);
            data.put("evaporationLossWater", evaporationLossWater);

            result.put("status", true);
            result.put("message", "SUCCESS");
            result.put("data", data);
        } catch (Exception e) {
            System.err.println("[ReservoirOperator] execute failed: " + e.getMessage());
            result.put("status", false);
            result.put("message", "执行失败: " + e.getMessage());
            result.put("data", data == null ? new HashMap<>() : data);
        }

        return result;
    }

    private double toDouble(Object value) {
        if (value == null) {
            return 0.0;
        }
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        try {
            return Double.parseDouble(String.valueOf(value));
        } catch (Exception e) {
            return 0.0;
        }
    }

    private int toInt(Object value) {
        if (value == null) {
            return 0;
        }
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        try {
            return Integer.parseInt(String.valueOf(value));
        } catch (Exception e) {
            return 0;
        }
    }

    private boolean toBoolean(Object value) {
        if (value == null) {
            return false;
        }
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        return Boolean.parseBoolean(String.valueOf(value));
    }
}
