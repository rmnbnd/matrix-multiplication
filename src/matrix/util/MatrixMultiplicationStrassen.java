package matrix.util;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

public class MatrixMultiplicationStrassen {

    public static int[][] forkJoin(int[][] a, int[][] b, int n) {

        int m = newDimension(n);
        int[][] am = new int[m][m];
        int[][] bm = new int[m][m];

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                am[i][j] = a[i][j];
                bm[i][j] = b[i][j];
            }
        }

        StrassenTask task = new StrassenTask(am, bm);
        ForkJoinPool pool = new ForkJoinPool();

        int[][] strassen = pool.invoke(task);

        int[][] result = new int[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                result[i][j] = strassen[i][j];
            }
        }
        return result;
    }

    private static int newDimension(int n) {
        int log2 = (int) Math.ceil(Math.log(n) / Math.log(2));
        return (int) Math.pow(2, log2);
    }

    private static class StrassenTask extends RecursiveTask<int[][]> {

        private int n;
        private int[][] a;
        private int[][] b;

        StrassenTask(int[][] a, int[][] b) {
            this.a = a;
            this.b = b;
            this.n = a[0].length;
        }

        @Override
        protected int[][] compute() {
            if (n == 1) {
                return MatrixMultiplicationSimple.multiply(a, b, n);
            }

            n = n / 2;

            int[][] a11 = new int[n][n];
            int[][] a12 = new int[n][n];
            int[][] a21 = new int[n][n];
            int[][] a22 = new int[n][n];

            int[][] b11 = new int[n][n];
            int[][] b12 = new int[n][n];
            int[][] b21 = new int[n][n];
            int[][] b22 = new int[n][n];

            splitMatrix(a, a11, a12, a21, a22, n);
            splitMatrix(b, b11, b12, b21, b22, n);

            StrassenTask taskP1 = new StrassenTask(summation(a11, a22), summation(b11, b22));
            StrassenTask taskP2 = new StrassenTask(summation(a21, a22), b11);
            StrassenTask taskP3 = new StrassenTask(a11, subtraction(b12, b22));
            StrassenTask taskP4 = new StrassenTask(a22, subtraction(b21, b11));
            StrassenTask taskP5 = new StrassenTask(summation(a11, a12), b22);
            StrassenTask taskP6 = new StrassenTask(subtraction(a21, a11), summation(b11, b12));
            StrassenTask taskP7 = new StrassenTask(subtraction(a12, a22), summation(b21, b22));

            taskP1.fork();
            taskP2.fork();
            taskP3.fork();
            taskP4.fork();
            taskP5.fork();
            taskP6.fork();
            taskP7.fork();

            int[][] p1 = taskP1.join();
            int[][] p2 = taskP2.join();
            int[][] p3 = taskP3.join();
            int[][] p4 = taskP4.join();
            int[][] p5 = taskP5.join();
            int[][] p6 = taskP6.join();
            int[][] p7 = taskP7.join();

            int[][] c11 = summation(summation(p1, p4), subtraction(p7, p5));
            int[][] c12 = summation(p3, p5);
            int[][] c21 = summation(p2, p4);
            int[][] c22 = summation(subtraction(p1, p2), summation(p3, p6));

            return collectMatrix(c11, c12, c21, c22);
        }

    }

    private static void splitMatrix(int[][] a, int[][] a11, int[][] a12, int[][] a21, int[][] a22, int n) {
        for (int i = 0; i < n; i++) {
            for (int k = 0; k < n; k++) {
                a11[i][k] = a[i][k];
                a12[i][k] = a[i][k + n];
                a21[i][k] = a[i + n][k];
                a22[i][k] = a[i + n][k + n];
            }
        }
    }

    private static int[][] summation(int[][] a, int[][] b) {
        int n = a.length;
        int[][] c = new int[n][n];

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                c[i][j] = a[i][j] + b[i][j];
            }
        }
        return c;
    }

    private static int[][] subtraction(int[][] a, int[][] b) {
        int n = a.length;
        int[][] c = new int[n][n];

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                c[i][j] = a[i][j] - b[i][j];
            }
        }
        return c;
    }

    private static int[][] collectMatrix(int[][] a11, int[][] a12, int[][] a21, int[][] a22) {
        int n = a11.length;
        int[][] a = new int[n * 2][n * 2];
        for (int i = 0; i < n; i++) {
            for (int k = 0; k < n; k++) {
                a[i][k] = a11[i][k];
                a[i][k + n] = a12[i][k];
                a[i + n][k] = a21[i][k];
                a[i + n][k + n] = a22[i][k];
            }
        }
        return a;
    }

}
