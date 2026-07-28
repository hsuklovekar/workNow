package SingleReservoir;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class readCSV {
    public static List<Double> readCSV(String filePath) throws Exception {

        List<Double>data = new ArrayList<>();

        BufferedReader br = new BufferedReader(new FileReader(filePath));
        String line;

        // 跳过表头
        br.readLine();

        while ((line = br.readLine()) != null) {
            String[] parts = line.split(",");


            for (int i = 0; i < parts.length; i++) {
                String valueStr = parts[i].trim();
                if (valueStr.isEmpty()) continue;
                double value = Double.parseDouble(valueStr);
                data.add(value);
            }
        }
        br.close();
        return data;
    }
}
