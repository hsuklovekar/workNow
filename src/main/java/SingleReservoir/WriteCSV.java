package SingleReservoir;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class WriteCSV {

    public static void writeCSV(String filePath, List<List<Double>> data) throws IOException {

        BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));

        for (List<Double> row : data) {

            StringBuilder line = new StringBuilder();

            for (int i = 0; i < row.size(); i++) {
                line.append(row.get(i));

                // 不是最后一个就加逗号
                if (i < row.size() - 1) {
                    line.append(",");
                }
            }

            writer.write(line.toString());
            writer.newLine(); // 换行
        }

        writer.close();
    }


        public static void writeByColumn(String filePath, List<List<Double>> cols) throws Exception {

            BufferedWriter bw = new BufferedWriter(new FileWriter(filePath));

            // 找最大长度（决定行数）
            int max = 0;
            for (List<Double> col : cols) {
                if (col.size() > max) max = col.size();
            }

            // 按行写（每一行从各列取第 i 个）
            for (int i = 0; i < max; i++) {

                for (int j = 0; j < cols.size(); j++) {

                    List<Double> col = cols.get(j);

                    if (i < col.size()) {
                        bw.write(String.valueOf(col.get(i)));
                    }

                    if (j != cols.size() - 1) {
                        bw.write(",");
                    }
                }

                bw.newLine();
            }

            bw.close();
        }
    }
