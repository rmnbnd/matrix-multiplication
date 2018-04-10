package matrix;

import matrix.util.MatrixMultiplicationSimple;
import matrix.util.MatrixMultiplicationStrassen;

import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Main class for running matrix multiplication (simple implementation with threads and strassen with forkjoin)
 */
public class Main {

    private static final int MIN = 1;
    private static final int MAX = 50;
    private static final int DIMENSION = 1024;

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        // generate matrix
        int[][] matrixA = generateMatrix(DIMENSION);
        int[][] matrixB = generateMatrix(DIMENSION);

        // simple multiplication
        long start = System.nanoTime();
        int[][] simpleResult = MatrixMultiplicationSimple.multiply(matrixA, matrixB, DIMENSION);
        long finish = System.nanoTime();

        System.out.println("simple - " + (finish - start) / 1000000000.0 + " secs");

        // simple multiplication
        start = System.nanoTime();
        int[][] simpleThreadResult = MatrixMultiplicationSimple.multiplyFuture(matrixA, matrixB, DIMENSION);
        finish = System.nanoTime();

        System.out.println("simple thread - " + (finish - start) / 1000000000.0 + " secs");

        // strassen multiplication
        start = System.nanoTime();
        int[][] strassenResult = MatrixMultiplicationStrassen.forkJoin(matrixA, matrixB, DIMENSION);
        finish = System.nanoTime();

        System.out.println("strassen - " + (finish - start) / 1000000000.0 + " secs");

        // check results
        System.out.println("matrices are equal: " + Arrays.deepEquals(strassenResult, simpleResult));
        System.out.println("matrices are equal: " + Arrays.deepEquals(simpleThreadResult, simpleResult));
        System.out.println("matrices are equal: " + Arrays.deepEquals(simpleThreadResult, strassenResult));
    }

    private static int[][] generateMatrix(int dimension) {
        int[][] matrix = new int[dimension][dimension];
        for (int i = 0; i < dimension; i++) {
            for (int j = 0; j < dimension; j++) {
                matrix[i][j] = ThreadLocalRandom.current().nextInt(MIN, MAX + 1);
            }
        }
        return matrix;
    }

}
