package ru.javaops.masterjava.matrix;

import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * gkislin
 * 03.07.2016
 */
public class MatrixUtil {

    public static int[][] concurrentMultiplyWithCompletionService(int[][] matrixA, int[][] matrixB, ExecutorService executor) {
        final int matrixSize = matrixA.length;
        final int[][] matrixC = new int[matrixSize][matrixSize];
        final CompletionService<Void> completionService = new ExecutorCompletionService<>(executor);
        List<Future<Void>> futures = IntStream.range(0, matrixSize)
                .mapToObj(i -> completionService.submit(() -> calculate(matrixA, matrixB, new int[matrixSize], matrixC, i, matrixSize)))
                .collect(Collectors.toList());
        while (!futures.isEmpty()) {
            futures.remove(completionService.poll());
        }
        return matrixC;
    }

    public static int[][] concurrentMultiply(int[][] matrixA, int[][] matrixB, ExecutorService executor) throws InterruptedException {
        final int matrixSize = matrixA.length;
        final int[][] matrixC = new int[matrixSize][matrixSize];
        final CompletionService<Void> completionService = new ExecutorCompletionService<>(executor);
        Set<Callable<Void>> tasks = IntStream.range(0, matrixSize)
                .<Callable<Void>>mapToObj(j -> () -> calculate(matrixA, matrixB, new int[matrixSize], matrixC, j, matrixSize))
                .collect(Collectors.toSet());
        executor.invokeAll(tasks);
        return matrixC;
    }

    public static int[][] concurrentMultiply2(int[][] matrixA, int[][] matrixB, ExecutorService executor) throws InterruptedException {
        final int matrixSize = matrixA.length;
        final int[][] matrixC = new int[matrixSize][matrixSize];
        final CountDownLatch latch = new CountDownLatch(matrixSize);
        for (int row = 0; row < matrixSize; row++) {
            final int[] rowA = matrixA[row];
            final int[] rowC = matrixC[row];
            executor.submit(() -> {
                calculate(matrixB, matrixSize, rowA, rowC);
                latch.countDown();
            });
        }
        latch.await();
        return matrixC;
    }

    public static int[][] concurrentMultiplyStream(int[][] matrixA, int[][] matrixB) {
        final int matrixSize = matrixA.length;
        final int[][] matrixC = new int[matrixSize][matrixSize];
        IntStream.range(0, matrixSize)
                .parallel()
                .forEach(row -> calculate(matrixB, matrixSize, matrixA[row], matrixC[row]));

        return matrixC;
    }

    public static int[][] singleThreadMultiply(int[][] matrixA, int[][] matrixB) {
        final int matrixSize = matrixA.length;
        final int[][] matrixC = new int[matrixSize][matrixSize];

        final int[] thatColumn = new int[matrixSize];

        try {
            for (int i = 0; ; i++) {
                calculate(matrixA, matrixB, thatColumn, matrixC, i, matrixSize);
            }
        } catch (IndexOutOfBoundsException ignored) {
        }
        return matrixC;
    }

    private static Void calculate(int[][] matrixA, int[][] matrixB, int[] thatColumn, int[][] matrixC, int i, int size) {
        for (int j = 0; j < size; j++) {
            thatColumn[j] = matrixB[j][i];
        }
        for (int j = 0; j < size; j++) {
            int[] thisRow = matrixA[j];
            int sum = 0;
            for (int k = 0; k < size; k++) {
                sum += thisRow[k] * thatColumn[k];
            }
            matrixC[j][i] = sum;
        }
        return null;
    }

    private static void calculate(int[][] matrixB, int matrixSize, int[] rowA, int[] rowC) {
        for (int idx = 0; idx < matrixSize; idx++) {
            final int elA = rowA[idx];
            final int[] rowB = matrixB[idx];
            for (int col = 0; col < matrixSize; col++) {
                rowC[col] += elA * rowB[col];
            }
        }
    }

    public static int[][] create(int size) {
        int[][] matrix = new int[size][size];
        Random rn = new Random();

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                matrix[i][j] = rn.nextInt(10);
            }
        }
        return matrix;
    }

    public static boolean compare(int[][] matrixA, int[][] matrixB) {
        final int matrixSize = matrixA.length;
        for (int i = 0; i < matrixSize; i++) {
            for (int j = 0; j < matrixSize; j++) {
                if (matrixA[i][j] != matrixB[i][j]) {
                    return false;
                }
            }
        }
        return true;
    }
}
