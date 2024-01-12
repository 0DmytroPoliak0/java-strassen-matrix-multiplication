public class Main {
    private static final int THRESHOLD = 128;

    public static void main(String[] args) {
        int n = 2000;
        double[][] a = new double[n][n];
        double[][] b = new double[n][n];

        // Initialize matrices with random values
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                a[i][j] = Math.random() * 2 - 1;
                b[i][j] = Math.random() * 2 - 1;
            }
        }

        // Measure time for sequential multiplication
        long startTimeSequential = System.currentTimeMillis();
        double[][] cSequential = multiplySequentially(a, b);
        long endTimeSequential = System.currentTimeMillis();
        System.out.println("Sequential multiplication execution time: " + (endTimeSequential - startTimeSequential) + " ms");

        // Measure time for optimized parallel multiplication
        long startTimeParallel = System.currentTimeMillis();
        double[][] cParallel = strassenOptimized(a, b);
        long endTimeParallel = System.currentTimeMillis();
        System.out.println("Optimized parallel multiplication execution time: " + (endTimeParallel - startTimeParallel) + " ms");
    }

    public static double[][] multiplySequentially(double[][] a, double[][] b) {
        int n = a.length;
        double[][] result = new double[n][n];

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                for (int k = 0; k < n; k++) {
                    result[i][j] += a[i][k] * b[k][j];
                }
            }
        }

        return result;
    }

    public static double[][] strassenOptimized(double[][] a, double[][] b) {
        int n = a.length;

        if (n <= THRESHOLD) {
            return multiplySequentially(a, b);
        }

        // Initialize submatrices
        double[][] a11 = new double[n / 2][n / 2];
        double[][] a12 = new double[n / 2][n / 2];
        double[][] a21 = new double[n / 2][n / 2];
        double[][] a22 = new double[n / 2][n / 2];
        double[][] b11 = new double[n / 2][n / 2];
        double[][] b12 = new double[n / 2][n / 2];
        double[][] b21 = new double[n / 2][n / 2];
        double[][] b22 = new double[n / 2][n / 2];

        split(a, a11, a12, a21, a22);
        split(b, b11, b12, b21, b22);

        // Calculate products using Strassen algorithm
        double[][] p1 = strassenOptimized(a11, subtract(b12, b22));
        double[][] p2 = strassenOptimized(add(a11, a12), b22);
        double[][] p3 = strassenOptimized(add(a21, a22), b11);
        double[][] p4 = strassenOptimized(a22, subtract(b21, b11));
        double[][] p5 = strassenOptimized(add(a11, a22), add(b11, b22));
        double[][] p6 = strassenOptimized(subtract(a12, a22), add(b21, b22));
        double[][] p7 = strassenOptimized(subtract(a11, a21), add(b11, b12));

        // Calculate result submatrices
        double[][] c11 = add(subtract(add(p5, p4), p2), p6);
        double[][] c12 = add(p1, p2);
        double[][] c21 = add(p3, p4);
        double[][] c22 = subtract(subtract(add(p5, p1), p3), p7);

        // Merge result submatrices
        return combine(c11, c12, c21, c22);
    }

    private static double[][] add(double[][] a, double[][] b) {
        int n = a.length;
        double[][] result = new double[n][n];

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                result[i][j] = a[i][j] + b[i][j];
            }
        }

        return result;
    }

    private static double[][] subtract(double[][] a, double[][] b) {
        int n = a.length;
        double[][] result = new double[n][n];

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                result[i][j] = a[i][j] - b[i][j];
            }
        }

        return result;
    }

    private static void split(double[][] m, double[][] a, double[][] b, double[][] c, double[][] d) {
        int n = m.length / 2;

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                a[i][j] = m[i][j];
                b[i][j] = m[i][j + n];
                c[i][j] = m[i + n][j];
                d[i][j] = m[i + n][j + n];
            }
        }
    }

    private static double[][] combine(double[][] a, double[][] b, double[][] c, double[][] d) {
        int n = a.length;
        double[][] result = new double[n * 2][n * 2];

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                result[i][j] = a[i][j];
                result[i][j + n] = b[i][j];
                result[i + n][j] = c[i][j];
                result[i + n][j + n] = d[i][j];
            }
        }

        return result;
    }
}
