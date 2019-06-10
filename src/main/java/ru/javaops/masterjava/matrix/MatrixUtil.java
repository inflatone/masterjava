package ru.javaops.masterjava.matrix;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * gkislin
 * 03.07.2016
 */
public class MatrixUtil {

    public static int[][] concurrentMultiply(int[][] matrixA, int[][] matrixB, ExecutorService executor) throws InterruptedException, ExecutionException {
        final int matrixSize = matrixA.length;
        final int[][] matrixC = new int[matrixSize][matrixSize];

        class ColumnMultiplyResult {
            private final int col;
            private final int[] columnC;

            public ColumnMultiplyResult(int col, int[] columnC) {
                this.col = col;
                this.columnC = columnC;
            }
        }

        final CompletionService<ColumnMultiplyResult> completionService = new ExecutorCompletionService<>(executor);

        for (int j = 0; j < matrixSize; j++) {
            final int col = j;
            final int[] columnB = new int[matrixSize];
            for (int k = 0; k < matrixSize; k++) {
                columnB[k] = matrixB[k][col];
            }
            completionService.submit(() -> {
                final int[] columnC = new int[matrixSize];
                for (int row = 0; row < matrixSize; row++) {
                    final int[] rowA = matrixA[row];
                    int sum = 0;
                    for (int k = 0; k < matrixSize; k++) {
                        sum += rowA[k] * columnB[k];
                    }
                    columnC[row] = sum;
                }
                return new ColumnMultiplyResult(col, columnC);
            });
        }
        for (int i = 0; i < matrixSize; i++) {
            ColumnMultiplyResult result = completionService.take().get();
            for (int k = 0; k < matrixSize; k++) {
                matrixC[k][result.col] = result.columnC[k];
            }
        }
        return matrixC;
    }

    public static int[][] concurrentMultiplyCayman(int[][] matrixA, int[][] matrixB, ExecutorService executor) throws InterruptedException {
        final int matrixSize = matrixA.length;
        final int[][] matrixC = new int[matrixSize][matrixSize];
        final int threadCount = Runtime.getRuntime().availableProcessors();
        final int maxIndex = matrixSize * matrixSize;
        final int cellsInThread = maxIndex / threadCount;


        final int[][] matrixBInverted = invertMatrix(matrixB, matrixSize);

        Set<Callable<Boolean>> threads = new HashSet<>();
        int fromIndex = 0;
        for (int i = 1; i <= threadCount; i++) {
            final int toIndex = i == threadCount ? maxIndex : fromIndex + cellsInThread;
            final int firstIndexFinal = fromIndex;
            threads.add(() -> {
                for (int j = firstIndexFinal; j < toIndex; j++) {
                    final int row = j / matrixSize;
                    final int col = j % matrixSize;
                    int sum = 0;
                    for (int k = 0; k < matrixSize; k++) {
                       sum += matrixA[row][k] * matrixBInverted[col][k];
                    }
                    matrixC[row][col] = sum;
                }
                return true;
            });
            fromIndex = toIndex;
        }
        executor.invokeAll(threads);
        return matrixC;
    }

    public static int[][] concurrentMultiplyDartVader(int[][] matrixA, int[][] matrixB, ExecutorService executor) throws InterruptedException {
        final int matrixSize = matrixA.length;
        final int[][] matrixC = new int[matrixSize][matrixSize];
        List<Callable<Void>> tasks = IntStream.range(0, matrixSize)
                .parallel()
                .mapToObj(col -> (Callable<Void>) () -> {
                    compute(matrixA, matrixB, matrixC, matrixSize, col);
                    return null;
                })
                .collect(Collectors.toList());
        executor.invokeAll(tasks);
        return matrixC;
    }

    public static int[][] concurrentMultiply2(int[][] matrixA, int[][] matrixB, ExecutorService executor) throws InterruptedException, ExecutionException {
        final int matrixSize = matrixA.length;
        final int[][] matrixC = new int[matrixSize][matrixSize];
        final int[][] matrixBInverted = invertMatrix(matrixB, matrixSize);

        List<Callable<Void>> tasks = new ArrayList<>(matrixSize);
        for (int i = 0; i < matrixSize; i++) {
            final int row = i;
            tasks.add(() -> {
               final int[] rowC = new int[matrixSize];
                for (int col = 0; col < matrixSize; col++) {
                    final int[] rowA = matrixA[row];
                    final int[] columnB = matrixBInverted[col];
                    int sum = 0;
                    for (int k = 0; k < matrixSize; k++) {
                        sum += rowA[k] * columnB[k];
                    }
                    rowC[col] = sum;
                }
                matrixC[row] = rowC;
                return null;
            });
        }
        executor.invokeAll(tasks);
        return matrixC;
    }

    private static int[][] invertMatrix(int[][] matrix, int matrixSize) {
        int[][] result = new int[matrix.length][matrix.length];
        for (int i = 0; i < matrixSize; i++) {
            for (int j = 0; j < matrixSize; j++) {
                result[i][j] = matrix[j][i];
            }
        }
        return result;
    }

    private static void compute(int[][] matrixA, int[][] matrixB, int[][] matrixC, int matrixSize, int col) {
        int[] columnB = new int[matrixSize];
        for (int k = 0; k < matrixSize; k++) {
            columnB[k] = matrixB[k][col];
        }
        for (int row = 0; row < matrixSize; row++) {
            int sum = 0;
            final int[] rowA = matrixA[row];
            for (int k = 0; k < matrixSize; k++)  {
                sum += columnB[k] * rowA[k];
            }
            matrixC[row][col] = sum;
        }
    }

    // Optimized by https://habrahabr.ru/post/114797/
    public static int[][] singleThreadMultiplyOpt(int[][] matrixA, int[][] matrixB) {
        final int matrixSize = matrixA.length;
        final int[][] matrixC = new int[matrixSize][matrixSize];

        for (int col = 0; col < matrixSize; col++) {
            compute(matrixA, matrixB, matrixC, matrixSize, col);
        }
        return matrixC;
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
