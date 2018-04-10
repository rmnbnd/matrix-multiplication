package matrix.util;


import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class MatrixMultiplicationSimple {

    public static int[][] multiply(int[][] a, int[][] b, int n) {
        int[][] result = new int[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                for (int k = 0; k < n; k++) {
                    result[i][j] += a[i][k] * b[k][j];
                }
            }
        }
        return result;
    }

    public static int[][] multiplyFuture(int[][] a, int[][] b, int n) throws ExecutionException, InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(4);

        Future<Integer> futures[][] = new Future[n][n];

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                Future<Integer> future = executor.submit(new Naive(n, a, b, i, j));
                futures[i][j] = future;
            }
        }

        int[][] result = new int[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                result[i][j] = futures[i][j].get();
            }
        }

        executor.shutdown();

        return result;
    }

    private static class Naive implements Callable<Integer> {

        private final int n;
        private final int[][] a;
        private final int[][] b;
        private final int i;
        private final int j;

        private Naive(int n, int[][] a, int[][] b, int i, int j) {
            this.n = n;
            this.a = a;
            this.b = b;
            this.i = i;
            this.j = j;
        }

        @Override
        public Integer call() {
            int sum = 0;
            for (int k = 0; k < n; k++) {
                sum += a[i][k] * b[k][j];
            }
            return sum;
        }
    }
}
