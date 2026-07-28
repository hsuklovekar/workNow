package org.example;

import SingleReservoir.Reservoir;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

public class LxsTest {
    public static void main(String[] args) throws JsonProcessingException {
        Reservoir reservoir = new Reservoir();

        Map<String, Object> execute = reservoir.execute(getInput(), getOperatorput());

        System.out.println(execute);
    }

    private static Map<String, Object> getInput() throws JsonProcessingException {
        String json = "{\n" +
                "  \"targetList\": [\n" +
                "    {\n" +
                "      \"supplyNodeId\": \"A\",\n" +
                "      \"demand\": 28.42308591,\n" +
                "      \"supplyType\": \"DOMESTIC\",\n" +
                "      \"startSupplyStorageLimit\": 100,\n" +
                "      \"lowerStorageLimit\": 100,\n" +
                "      \"priority\": 1,\n" +
                "      \"pipeParamList\": [\n" +
                "        {\n" +
                "          \"maxCapacity\": 70,\n" +
                "          \"usedCapacity\": 70\n" +
                "        },\n" +
                "        {\n" +
                "          \"maxCapacity\": 777777.77,\n" +
                "          \"usedCapacity\": 0\n" +
                "        },\n" +
                "        {\n" +
                "          \"maxCapacity\": 777777.77,\n" +
                "          \"usedCapacity\": 0\n" +
                "        }\n" +
                "      ],\n" +
                "      \"coefficient\": 1\n" +
                "    },\n" +
                "    {\n" +
                "      \"supplyNodeId\": \"B\",\n" +
                "      \"demand\": 6.197219134,\n" +
                "      \"supplyType\": \"AGRICULTURE\",\n" +
                "      \"startSupplyStorageLimit\": 100,\n" +
                "      \"lowerStorageLimit\": 100,\n" +
                "      \"priority\": 2,\n" +
                "      \"pipeParamList\": [\n" +
                "        {\n" +
                "          \"availableCapacity\": 70\n" +
                "        },\n" +
                "        {\n" +
                "          \"availableCapacity\": 25\n" +
                "        },\n" +
                "        {\n" +
                "          \"availableCapacity\": 10.5\n" +
                "        }\n" +
                "      ],\n" +
                "      \"coefficient\": 1,\n" +
                "      \"maxSupplyScale\": 10.5\n" +
                "    },\n" +
                "    {\n" +
                "      \"supplyNodeId\": \"C\",\n" +
                "      \"demand\": 10,\n" +
                "      \"supplyType\": \"AGRICULTURE\",\n" +
                "      \"startSupplyStorageLimit\": 100,\n" +
                "      \"lowerStorageLimit\": 100,\n" +
                "      \"priority\": 3,\n" +
                "      \"pipeParamList\": [\n" +
                "        {\n" +
                "          \"availableCapacity\": 70\n" +
                "        },\n" +
                "        {\n" +
                "          \"availableCapacity\": 25\n" +
                "        },\n" +
                "        {\n" +
                "          \"availableCapacity\": 10.5\n" +
                "        }\n" +
                "      ],\n" +
                "      \"coefficient\": 1,\n" +
                "      \"maxSupplyScale\": 10.5\n" +
                "    },\n" +
                "    {\n" +
                "      \"supplyNodeId\": \"D\",\n" +
                "      \"demand\": 100,\n" +
                "      \"supplyType\": \"ReservoirDemand\",\n" +
                "      \"startSupplyStorageLimit\": 100,\n" +
                "      \"lowerStorageLimit\": 4060,\n" +
                "      \"priority\": 4,\n" +
                "      \"pipeParamList\": [\n" +
                "        {\n" +
                "          \"availableCapacity\": 70\n" +
                "        },\n" +
                "        {\n" +
                "          \"availableCapacity\": 25\n" +
                "        },\n" +
                "        {\n" +
                "          \"availableCapacity\": 10.5\n" +
                "        }\n" +
                "      ],\n" +
                "      \"coefficient\": 1,\n" +
                "      \"maxSupplyScale\": 10.5\n" +
                "    }\n" +
                "  ],\n" +
                "  \"waterCharge\": 0,\n" +
                "  \"inputNatural\": 89.74819607,\n" +
                "  \"timeStep\": 24,\n" +
                "  \"storageIntial\": 1288\n" +
                "}";

        ObjectMapper MAPPER = new ObjectMapper();
        return MAPPER.readValue(json, Map.class);
    }

    private static Map<String, Object> getOperatorput() throws JsonProcessingException {
        String json ="{\n" +
                "  \"xunxianStorage\": 6220,\n" +
                "  \"evaporationLossCoefficient\": 0.05,\n" +
                "  \"meanAnnualRunoff\": 5643,\n" +
                "  \"siStorage\": 33,\n" +
                "  \"zhengChangStorage\": 6220,\n" +
                "  \"ecologicalCoefficient\": 0.1,\n" +
                "  \"isCharge\": false,\n" +
                "  \"id\": \"AAA001\",\n" +
                "  \"enableDDT\": false,\n" +
                "  \"ddt\": {\n" +
                "    \"scheduleZones\": [\n" +
                "      {\n" +
                "        \"startDate\": \"\",\n" +
                "        \"endDate\": \"\",\n" +
                "        \"lowerWaterLevel\": null,\n" +
                "        \"upperWaterLevel\": null,\n" +
                "        \"taskCoefficients\": {}\n" +
                "      }\n" +
                "    ]\n" +
                "  },\n" +
                "  \"ddx\": {\n" +
                "    \"periods\": [\n" +
                "      {\n" +
                "        \"startTime\": \"\",\n" +
                "        \"endTime\": \"\",\n" +
                "        \"waterLevel\": null\n" +
                "      }\n" +
                "    ]\n" +
                "  }\n" +
                "}";


        ObjectMapper MAPPER = new ObjectMapper();
        return MAPPER.readValue(json, Map.class);
    }
}
