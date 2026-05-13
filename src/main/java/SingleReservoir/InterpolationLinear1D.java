package SingleReservoir;

public class InterpolationLinear1D {
    public static double Linear1D(Double[] x, Double[] y, Double x0) {
        int n = x.length;

        if (x0 <= x[0]) {
            //向左外推
            return y[0] + (y[1] - y[0]) / (x[1] - x[0]) * (x0 - x[0]);
        }
        if (x0 >= x[n - 1]) {
            //向右外推
            return y[n - 2] + (y[n - 1] - y[n - 2]) / (x[n - 1] - x[n - 2]) * (x0 - x[n - 2]);
        }
        for (int i = 0; i < n - 1; i++) {
            //区间内插
            if (x0 >= x[i] && x0 < x[i + 1]) {
                return y[i] + (x0 - x[i]) / (x[i + 1] - x[i]) * (y[i + 1] - y[i]);
            }
        }

        throw new IllegalStateException("插值失败，请检查数组！");
    }
}
