package SingleReservoir;
/*
对每一个供水对象进行需供水计算：
输入水库可消耗水量、该供水对象输水规模、该供水对象耗水下限
如果需求小于可消耗水量，按需求供水；反之按可消耗水量供水；
如果供水量小于下限，按需求供水；反之按下限供水；
如果供水量大于管道规模，按管道规模供水。
按优先级顺序，遍历每一个供水对象：
计算供水后，更新可消耗水量，读取新供水对象的规模与下限。
 */
public class SupplyCalculate {
    public double calculate(double availableWater,
                            double siStorage,
                            double storageInitial,
                            Integer timeStep,
                            SupplyTarget target) {
        double demand = target.getDemand();
        double pipeScale = target.getMaxSupplyScale() * timeStep * 3600;//转化单位由流量转化为有效库容
        double maxSupplyByMinLimit = Math.max(availableWater - (target.getLowerStorageLimit()-siStorage),0.0);//转化单位由水位转化为有效库容
        double coefficient = target.getCoefficient();
        double supply;
        //
        demand = demand * coefficient;
        if (demand > pipeScale) {
            demand = pipeScale;
        }
        // 1 如果需求小于可用水量
        if (demand <= availableWater) {
            supply = demand;
        } else {
            supply = availableWater;
        }

        // 2 如果供水量小于下限
        if (supply < maxSupplyByMinLimit) {
            supply = supply;
        } else {
            supply = maxSupplyByMinLimit;
        }

        // 3 如果供水量大于管道规模
        if (supply > pipeScale) {
            supply = pipeScale;
        }

        return supply;
    }


}
