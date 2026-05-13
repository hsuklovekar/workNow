package SingleReservoir;
import java.util.*;
import java.util.stream.Collectors;
/**
 * 算子名称：单库供水算子（Reservoir）。
 *
 *功能描述：
 *   根据上游输入的天然来水与供水目标，结合水库初始库容、死库容、正常蓄水位对应库容、蒸发损失与生态流量参数，计算各供水对象的实际供水量。
 *   计算各供水任务供水量、时段末库容、弃水量，以及（在允许充库时）可腾出的最大充库空间。
 *   按平台规范返回统一结构：status / message / data。
 *
 * 输入（inputParams）：
 *  inputNatural (Number, 默认 0.0)：时段天然来水量。
 *    targetList (List)：供水目标集合，每个元素对应一个 SupplyTarget 结构，字段如下：
 *       supplyNodeId (String)：供水节点 ID。
 *       demand (Number)：节点需水量。
 *       supplyType (String/Number，可选)：供水类型（枚举编码或名称，当前算子不参与计算，仅透传/扩展位）。
 *       startSupplyStorageLimit (Number)：起供库容，当前库容未达到时不参与该任务的供水分配。
 *       lowerStorageLimit (Number)：供水库容下限。
 *       priority (Number)：供水优先级，数值越小优先级越高。
 *       pipeParamList (List)：从供水节点到目标用水户的管道参数列表。
 *       coefficient (Number，可选，默认 1.0)：需求打折系数。
 *
 * 参数配置（operatorParams）：
 *   id (String)：水库 ID，默认 "UNKNOWN_RESERVOIR"。
 *   storageIntial (Number)：时段初库容，默认 0.0。
 *   siStorage (Number)：死库容，默认 0.0。
 *   zhengChangStorage (Number)：正常蓄水库容对应库容，默认 Double.MAX_VALUE。
 *   evaporationLossCoefficient (Number)：蒸散发损失系数，默认 0.05。
 *   meanAnnualRunoff (Number)：多年平均径流量，默认 0.0。
 *   ecologicalCoefficient (Number)：生态流量系数，默认 0.1。
 *   timeStep (Number)：时间步长（小时），默认 24（按逐日进行调度）。
 *   isCharge (Boolean)：是否允许充库，默认 false。
 *   waterCharge (Number)：上时段充库水量，默认 0.0。
 *   chargeStorage (Number)：停冲水位对应库容，默认与 zhengChangStorage 相同。
 *
 * 输出：
 *   status (Boolean)：执行状态。true 成功，false 失败。
 *   message (String)：状态信息。失败时包含错误原因。
 *   data (Map)：业务结果，字段包含：
 *       reservoirId (String)：水库 ID。
 *       storageFinal (Double)：时段末库容。
 *       waterSurplus (Double)：弃水量。
 *       maxCharge (Double)：最大可充库容（仅允许充库时有意义）。
 *       supplyResults (Map)：各供水节点供水结果（supply/lack）。
 */
public class Reservoir {
    // 基础信息
    private String reservoirName;
    private String reservoirId;
    boolean isCharge;// 是否允许充库
    double waterCharge;// 上一时段充库水量
    //
    private double[][] waterLevelCapacityCurve;//水位-库容曲线
    private double evaporationLossCoefficient;//蒸散发损失系数
    private double meanAnnualRunoff;//多年平均径流量
    private double ecologicalCoefficient;//生态流量系数

    //时间参数
    int year;
    int month;
    int day;
    int timeStep;//时间间隔，小时

    // 特征水位
    private double siLevel;
    private double zhengChangLevel;
    private double fangHongGaoLevel;
    private double jiaoHeLevel;
    private double chargeLevel;//停冲水位

    // 特征库容
    private double siStorage;
    private double zhengChangStorage;
    private double fangHongGaoStorage;
    private double jiaoHeStorage;
    private double chargeStorage;//停冲水库对应库容

    //时段状态
    private double storageIntial;//时段初库容
    private double storageFinal;//时段末库容
    private double inputNatural;//水库来水
    private double inputfilling;//上游水库给其充库水量；
    //private double waterInCur;//水库入库水量（来水加充库水量）
    private double waterSurplus;//弃水
    private double availableWater;//可供水量

    public void setReservoirId(String reservoirId) {
        this.reservoirId = reservoirId;
    }

    public void setStorageIntial(double storageIntial) {
        this.storageIntial = storageIntial;
    }

    public void setZhengChangStorage(double zhengChangStorage) {
        this.zhengChangStorage = zhengChangStorage;
    }

    public void setSiStorage(double siStorage) {
        this.siStorage = siStorage;
    }

    public void setInputNatural(double inputNatural) {
        this.inputNatural = inputNatural;
    }

    public void setEvaporationLossCoefficient(double evaporationLossCoefficient) {
        this.evaporationLossCoefficient = evaporationLossCoefficient;
    }

    public void setMeanAnnualRunoff(double meanAnnualRunoff) {
        this.meanAnnualRunoff = meanAnnualRunoff;
    }

    public void setEcologicalCoefficient(double ecologicalCoefficient) {
        this.ecologicalCoefficient = ecologicalCoefficient;
    }

    public void setAvailableWater(double availableWater) {
        this.availableWater = availableWater;
    }

    public void setTimeStep(int timeStep) {
        this.timeStep = timeStep;
    }

    public double getStorageFinal() {
        return storageFinal;
    }

    // 供水对象列表
    private List<SupplyTarget> supplyTargets = new ArrayList<>();
    private List<SupplyActually> supplyActuallys = new ArrayList<>();

    // 添加供水对象
    public void addSupplyTarget(SupplyTarget target) {
        supplyTargets.add(target);
    }

    // 获取供水对象（按优先级排序）
    public List<SupplyTarget> getSupplyTargetsByPriority() {
        supplyTargets.sort(Comparator.comparingInt(SupplyTarget::getPriority));
        return supplyTargets;
    }

    public void clearSupplyTargets() {
        supplyTargets.clear();
    }

    public Map availableWater() {
        Map<String, Double> availableWaterNow = new HashMap<>();//输出
        availableWater = storageIntial - siStorage + inputNatural;//可供水量 = 当前水位对应库容减死水位对应库容 + 来水量
        Double evaporationLossWater;//扣除蒸散发水量
        Double evaporationWaterDemand;//生态需水
        Double evaporationWaterSupply;//生态供水

        //天然来水扣减蒸发
        evaporationLossWater = inputNatural * evaporationLossCoefficient;
        availableWater -= evaporationLossWater;
        //计算生态需水、供水
        evaporationWaterDemand = meanAnnualRunoff / 365.0 * timeStep / 24.0 * ecologicalCoefficient;
        evaporationWaterSupply = Math.min(evaporationWaterDemand, inputNatural - evaporationLossWater);
        availableWater -= evaporationWaterSupply;

        return availableWaterNow;
    }
    /**
     * 具体业务计算：水量平衡、分优先级供水分配、末库容与弃水计算。
     */
    @SuppressWarnings("unchecked")
    public NodeResult waterBalance() {
        Map<String, NodeResult> results = new HashMap<>();//输出

        Double availableWater = storageIntial - siStorage + inputNatural;//可供水量 = 当前水位对应库容减死水位对应库容 + 来水量

        Double evaporationLossWater;//扣除蒸散发水量
        Double evaporationWaterDemand;//生态需水
        Double evaporationWaterSupply;//生态供水
        //天然来水扣减蒸发
        evaporationLossWater = inputNatural * evaporationLossCoefficient;
        availableWater -= evaporationLossWater;
        //计算生态需水、供水
        evaporationWaterDemand = meanAnnualRunoff / 365.0 * timeStep / 24.0 * ecologicalCoefficient;
        evaporationWaterSupply = Math.min(evaporationWaterDemand, inputNatural - evaporationLossWater);
        availableWater -= evaporationWaterSupply;

        //如果有充库
        if (isCharge) {
            availableWater += waterCharge;
        }
        //储存最初available water
        double availableWaterInitial = availableWater;
        //计算各供水对象的供水量
        /*
        1.将起供水位与下限水位转化为有效库容
        当前水位对应库容大于起供水位，开始计算供水
        2.当出现优先级相等的任务时，先把可供水量按比例分，再进行计算
         */

        //double supply = 0.0;//每个供水对象的供水量，为中间变量
        double supplyAll = 0.0;
        double startSupplyStorage = 0.0;
        List<SupplyTarget> Targets = getSupplyTargetsByPriority();
        Map<String, SupplyActually> effectiveSupplys = new HashMap();
        SupplyCalculate supplyCal = new SupplyCalculate();
        //按优先级分组
        Map<Integer, List<SupplyTarget>> grouped =
                Targets.stream().collect(Collectors.groupingBy(
                        SupplyTarget::getPriority,
                        TreeMap::new,
                        Collectors.toList()
                ));

        for (Map.Entry<Integer, List<SupplyTarget>> entry : grouped.entrySet()) {
            List<SupplyTarget> group = entry.getValue();

            //  1. 先筛选“能参与供水的对象”
            List<SupplyTarget> validTargets = new ArrayList<>();
            for (SupplyTarget target : group) {
                if (availableWater > 0 &&
                        target.getStartSupplyStorageLimit() <= storageIntial) {
                    validTargets.add(target);
                }
            }
            //if (validTargets.isEmpty()) continue;

            // 2. 计算这一组总需求
            double totalDemand = validTargets.stream()
                    .mapToDouble(SupplyTarget::getDemand)
                    .sum();

            //if (totalDemand == 0) continue;
            double used = 0.0;
            Set<String> validTargetIds = validTargets.stream()
                    .map(SupplyTarget::getSupplyNodeId)
                    .collect(Collectors.toSet());

            // 3. 组内按比例分配（仅对符合条件的目标）
            for (SupplyTarget target : validTargets) {

                double supply = 0.0;
                String ID = target.getSupplyNodeId();

                // 按比例分“可用水量”
                if (totalDemand > 0) {
                    double ratio = target.getDemand() / totalDemand;
                    double allocWater = availableWater * ratio;
                    // 计算
                    supply = supplyCal.calculate(
                            allocWater,   //分到的份额
                            siStorage,
                            storageIntial,
                            timeStep,
                            target);
                }

                SupplyActually effectiveSupply = new SupplyActually(ID, supply);
                effectiveSupplys.put(ID, effectiveSupply);
                used += supply;
                supplyAll += supply;
            }

            // 4. 对不符合条件的目标补全 0 供水，避免下游调用出现空值
            for (SupplyTarget target : group) {
                String ID = target.getSupplyNodeId();
                if (!validTargetIds.contains(ID)) {
                    effectiveSupplys.put(ID, new SupplyActually(ID, 0.0));
                }
            }

            //5. 统一扣减这一组用掉的水
            availableWater -= used;
            //if (availableWater <= 0) break;

        }

        //计算时段末库容
        storageFinal = availableWaterInitial - supplyAll + siStorage;
        //计算弃水
        if (storageFinal > zhengChangStorage) {
            waterSurplus = storageFinal - zhengChangStorage;
            storageFinal = zhengChangStorage;
        }
        //计算缺水
        storageFinal = Math.max(siStorage, storageFinal);
        //按输出供水对象对应的
        //WaterSupply.put("output", effectiveSupplys);
        //计算可冲库容
        Double maxCharge = 0.0;
        if (isCharge) {
            maxCharge = chargeStorage - storageFinal;
            //WaterSupply.put("maxCharge", Arrays.asList(maxCharge));
        }
        ChargeDemand chargeDemand = new ChargeDemand(maxCharge);
        NodeResult WaterSupply = new NodeResult(effectiveSupplys, chargeDemand, storageFinal);
        results.put(reservoirId, WaterSupply);
        return WaterSupply;
    }

    /**
     *
     * @param inputParams
     * @param operatorParams
     * @return
     */
    public Map<String, Object> execute(Map<String, Object> inputParams, Map<String, Object> operatorParams) {
        Map<String, Object> result = new HashMap<>();

        Reservoir reservoir = new Reservoir();
        try {
            // ========== 参数提取（按业务约定） ==========
            // 上游输入：本算子约定接收inputNatural TargetList
            Double inputNatural = (Double) inputParams.get("inputNatural");
            List<SupplyTarget> TargetList = (List<SupplyTarget>) inputParams.get("targetList");
            for (int i = 0; i < TargetList.size(); i++) {
                SupplyTarget target = TargetList.get(i);
                reservoir.addSupplyTarget(target);
            }
            Double waterCharge = (Double) inputParams.get("waterCharge");

            // 本算子配置
            String Id = (String) operatorParams.get("id");
            Double storageIntial = (Double) operatorParams.get("storageIntial");
            Double siStorage = (Double) operatorParams.get("siStorage");
            Double zhengChangStorage = (Double) operatorParams.get("zhengChangStorage");
            Double evaporationLossCoefficient = (Double) operatorParams.get("evaporationLossCoefficient");
            Double meanAnnualRunoff = (Double) operatorParams.get("meanAnnualRunoff");
            Double ecologicalCoefficient = (Double) operatorParams.get("ecologicalCoefficient");
            Integer timeStep = (Integer) operatorParams.get("timeStep");
            boolean isCharge = (Boolean) operatorParams.get("isCharge");



            reservoir.setReservoirId(Id);
            reservoir.setStorageIntial(storageIntial);   // 初始库容
            reservoir.setSiStorage(siStorage);        // 死库容
            reservoir.setZhengChangStorage(zhengChangStorage);//正常蓄水位对应库容
            reservoir.setInputNatural(inputNatural);     // 来水
            reservoir.setEvaporationLossCoefficient(evaporationLossCoefficient); // 蒸发系数
            reservoir.setMeanAnnualRunoff(meanAnnualRunoff);//多年平均径流量
            reservoir.setEcologicalCoefficient(ecologicalCoefficient);
            reservoir.setTimeStep(timeStep);
            // ========== 业务逻辑 ==========
            NodeResult resultSupply = reservoir.waterBalance();
            //========== 返回结果（固定格式） ==========
            result.put(reservoirId, resultSupply);
            //Double storageFinal = reservoir.getStorageFinal();

        } catch (RuntimeException e) {
            System.err.println("[Reservoir] execute failed: " + e.getMessage());
            result.put("status", false);
            result.put("message", "INIT");
            result.put("data", new HashMap<String, Object>());
        }
        return result;
    }
}
