package SingleReservoir;
/*
水库供水对象的供水类型
DOMESTIC 人饮、工业需水
AGRICULTURE 灌溉需水
ECOLOGY 生态需水
 */
public enum SupplyType{
    DOMESTIC, //人饮、工业需水
    AGRICULTURE, //灌溉需水
    ECOLOGY, //生态需水
    ReservoirDemand,//充蓄需水
    OTHER
}
