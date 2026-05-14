import SingleReservoir.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static SingleReservoir.SupplyType.*;
import static SingleReservoir.readCSV.readCSV;

public class MainTest {
    public static void main(String[] args) throws Exception {
        //读取水库天然来水序列 /Users/xiefanyi/work/环北调度
        //读取水库需水序列
        List<Double> inflowList = readCSV("/Users/xiefanyi/work/环北调度/inflowList.csv");
        List<Double> CityList = readCSV("/Users/xiefanyi/work/环北调度/renyinD.csv");
        List<Double> irrigateList = readCSV("/Users/xiefanyi/work/环北调度/irrigate.csv");
        List<Double> otherList = readCSV("/Users/xiefanyi/work/环北调度/other.csv");
        // 1.创建水库对象
        Reservoir reservoir = new Reservoir();
        reservoir.setReservoirId("AAA001");
        int size = inflowList.size();
        // 2.设置基础参数
        reservoir.setStorageIntial(1288.0);   // 初始库容
        reservoir.setSiStorage(100.0);        // 死库容
        reservoir.setZhengChangStorage(4060.0);//正常蓄水位对应库容
        reservoir.setInputNatural(inflowList.get(0));     // 来水
        reservoir.setEvaporationLossCoefficient(0.05); // 蒸发系数
        reservoir.setMeanAnnualRunoff(5643.0);//多年平均径流量
        reservoir.setEcologicalCoefficient(0.1);
        reservoir.setTimeStep(240); // 一旬，24*10小时

        // 3.构造供水对象
        List<PipeParam> list1 = new ArrayList<>();
        // 参数顺序：pipeId, maxCapacity, availableCapacity, usedCapacity
        list1.add(new PipeParam("PIPE_MAIN_01", 100.0, 70.0, 30.0));  // 主干：余量充足
        list1.add(new PipeParam("PIPE_SUB_01", 50.0, 25.0, 25.0));    // 分支：余量中等
        list1.add(new PipeParam("PIPE_END_01", 20.0, 10.5, 9.5));     // 末端：余量最小

        SupplyTarget t1 = new SupplyTarget(
                "A",
                CityList.get(0),
                DOMESTIC,
                100.0,
                100.0,
                1,1,
                list1
                );
        SupplyTarget t2 = new SupplyTarget("B",
                irrigateList.get(0),
                AGRICULTURE,
                100.0,
                100.0,
                2,
                1,
                list1
                );
        SupplyTarget t3 = new SupplyTarget("C",
                otherList.get(0),
                AGRICULTURE,
                100.0,
                100.0,
                3,
                1,
                list1
        );

        SupplyTarget t4 = new SupplyTarget("D",
                100.0,
                ReservoirDemand,
                100.0,
                4060.0,
                4,
                1,
                list1
                );


        // 4.执行水量平衡计算
        Map<String, NodeResult> result = new HashMap<>();
        //NodeResult nodeResult =
        //List<SupplyActually> effectiveSupplys = new ArrayList<>();
        //构造打印数据

        List<Double> citySupply = new ArrayList<>();
        List<Double> irrigateSupply = new ArrayList<>();
        List<Double> otherSupply = new ArrayList<>();
        List<List<Double>> data = new ArrayList<>();
        List<Double> storageFinal = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            if(i == 40){
                System.out.println("break");
            }
            reservoir.addSupplyTarget(t1);
            reservoir.addSupplyTarget(t2);
            reservoir.addSupplyTarget(t3);

             //= reservoir.waterBalance();

            NodeResult nodeResult1 = reservoir.waterBalance();
            Map<String,SupplyActually>  supplyActuallys = nodeResult1.getSupplyActuallys();
            //
            //
            citySupply.add(supplyActuallys.get("A").getEffectiveSupply());
            //
            //
            irrigateSupply.add(supplyActuallys.get("B").getEffectiveSupply());
            //
            otherSupply.add(supplyActuallys.get("C").getEffectiveSupply());
            //
            storageFinal.add(reservoir.getStorageFinal());
            if(i<size-1) {
                reservoir.clearSupplyTargets();
                t1.setDemand(CityList.get(i + 1));
                t2.setDemand(irrigateList.get(i + 1));
                t3.setDemand(otherList.get(i + 1));
                reservoir.setStorageIntial(reservoir.getStorageFinal());
                reservoir.setInputNatural(inflowList.get(i+1));
            }
        }
        data.add(citySupply);
        data.add(irrigateSupply);
        data.add(otherSupply);
        data.add(storageFinal);
        // 5.输出结果
        System.out.println("供水结果打印");
        WriteCSV.writeByColumn("/Users/xiefanyi/work/环北调度/result05013.csv", data);
/*
        List<SupplyActually> outputs = (List<SupplyActually>) result.get("output");

        for (SupplyActually sa : outputs) {
            System.out.println("供水对象: " + sa.getSupplyNodeId() +
                    "实际供水量: " + sa.getEffectiveSupply());
        }
*/
        System.out.println("时段末库容: " + reservoir.getStorageFinal());
        List<Double> citySupplyByExecute = new ArrayList<>();
        List<Double> irrigateSupplyByExecute = new ArrayList<>();
        List<Double> otherSupplyByExecute = new ArrayList<>();
        List<Double> storageFinalByExecute = new ArrayList<>();

        double rollingStorage = 1288.0;
        for (int i = 0; i < size; i++) {
            SupplyTarget e1 = new SupplyTarget("A", CityList.get(i), DOMESTIC, 100.0, 100.0, 1, 1, list1);
            SupplyTarget e2 = new SupplyTarget("B", irrigateList.get(i), AGRICULTURE, 100.0, 100.0, 2, 1, list1);
            SupplyTarget e3 = new SupplyTarget("C", otherList.get(i), AGRICULTURE, 100.0, 100.0, 3, 1, list1);
            SupplyTarget e4 = new SupplyTarget("D", 0.0, ReservoirDemand, 100.0, 4060.0, 4, 1, list1);

            List<SupplyTarget> supplyTargets = new ArrayList<>();
            supplyTargets.add(e1);
            supplyTargets.add(e2);
            supplyTargets.add(e3);
            supplyTargets.add(e4);

            Map<String, Object> in = new HashMap<>();
            Map<String, Object> op = new HashMap<>();
            in.put("inputNatural", inflowList.get(i));
            in.put("targetList", supplyTargets);
            op.put("id", "AAA001");
            op.put("storageIntial", rollingStorage);
            op.put("siStorage", 33.0);
            op.put("zhengChangStorage", 6220.0);
            op.put("xunxianStorage", 6220.0);
            op.put("evaporationLossCoefficient", 0.05);
            op.put("meanAnnualRunoff", 5653.0);
            op.put("ecologicalCoefficient", 0.1);
            op.put("timeStep", 240);
            op.put("isCharge", false);

            Map<String, Object> resultSingle = reservoir.execute(in, op);
            NodeResult nodeResult1 = (NodeResult) resultSingle.get("AAA001");
            Map<String, SupplyActually> supplyActuallys = nodeResult1.getSupplyActuallys();

            citySupplyByExecute.add(supplyActuallys.get("A").getEffectiveSupply());
            irrigateSupplyByExecute.add(supplyActuallys.get("B").getEffectiveSupply());
            otherSupplyByExecute.add(supplyActuallys.get("C").getEffectiveSupply());
            storageFinalByExecute.add(nodeResult1.getStorageFinal());
            rollingStorage = nodeResult1.getStorageFinal();
        }

        System.out.println("execute 方式供水成果打印");
        for (int i = 0; i < size; i++) {
            System.out.println(
                    "时段 " + i
                            + " | inflow=" + inflowList.get(i)
                            + " | city=" + citySupplyByExecute.get(i)
                            + " | irrigate=" + irrigateSupplyByExecute.get(i)
                            + " | other=" + otherSupplyByExecute.get(i)
                            + " | storageFinal=" + storageFinalByExecute.get(i)
            );
        }

        List<List<Double>> executeData = new ArrayList<>();
        executeData.add(inflowList);
        executeData.add(citySupplyByExecute);
        executeData.add(irrigateSupplyByExecute);
        executeData.add(otherSupplyByExecute);
        executeData.add(storageFinalByExecute);
        WriteCSV.writeByColumn("/Users/xiefanyi/work/环北调度/result_execute_3.csv", executeData);

    }
}
