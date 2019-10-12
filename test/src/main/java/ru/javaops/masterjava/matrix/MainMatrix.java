package ru.javaops.masterjava.matrix;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainMatrix {
    private static final int MATRIX_SIZE = 2000;
    private static final int THREAD_NUMBER = 10;

    private final static ExecutorService executor = Executors.newFixedThreadPool(MainMatrix.THREAD_NUMBER);

    public static void main(String[] args) throws InterruptedException {
        final int[][] matrixA = MatrixUtil.create(MATRIX_SIZE);
        final int[][] matrixB = MatrixUtil.create(MATRIX_SIZE);

        double singleThreadSum = 0.;
        double concurrentThreadSum = 0.;
        double concurrentThreadWithCompletionServiceSum = 0.;
        double concurrentThread2Sum = 0.;
        double concurrentThreadStreamSum = 0.;

        int count = 1;
        while (count < 6) {
            System.out.println("Pass " + count);
            Result a = execute(() -> MatrixUtil.singleThreadMultiply(matrixA, matrixB), "Single thread");
            Result b = execute(() -> MatrixUtil.concurrentMultiply(matrixA, matrixB, executor), "Concurrent thread");
            Result c = execute(() -> MatrixUtil.concurrentMultiplyWithCompletionService(matrixA, matrixB, executor), "Concurrent with completion service thread");
            Result d = execute(() -> MatrixUtil.concurrentMultiply2(matrixA, matrixB, executor), "Concurrent 2 thread");
            Result e = execute(() -> MatrixUtil.concurrentMultiplyStream(matrixA, matrixB), "Concurrent with stream");
            singleThreadSum += a.duration;
            concurrentThreadSum += b.duration;
            concurrentThreadWithCompletionServiceSum += c.duration;
            concurrentThread2Sum += d.duration;
            concurrentThreadStreamSum += e.duration;

            if (!compare(a.matrix, b.matrix, c.matrix, d.matrix, e.matrix)) {
                break;
            }
            count++;
        }
        executor.shutdown();
        out("\nAverage single thread time, sec: %.3f", singleThreadSum / 5.);
        out("Average concurrent thread time, sec: %.3f", concurrentThreadSum / 5.);
        out("Average concurrent with completion service thread time, sec: %.3f", concurrentThreadWithCompletionServiceSum / 5.);
        out("Average concurrent 2 thread time, sec: %.3f", concurrentThread2Sum / 5.);
        out("Average concurrent with stream time, sec: %.3f", concurrentThreadStreamSum / 5.);
    }

    private static void out(String format, double ms) {
        System.out.println(String.format(format, ms));
    }

    private static Result execute(Multiplier multiplier, String name) throws InterruptedException {
        long start = System.currentTimeMillis();
        final int[][] matrix = multiplier.multiply();
        double duration = (System.currentTimeMillis() - start) / 1000.0;
        out(name + " time, sec: %.3f", duration);
        return new Result(matrix, duration);
    }

    private static boolean compare(int[][]... arrays) {
        for (int i = 1; i < arrays.length; i++) {
            if (!MatrixUtil.compare(arrays[i - 1], arrays[i])) {
                System.err.println("Comparison failed");
                return false;
            }
        }
        return true;
    }

    private interface Multiplier {
        int[][] multiply() throws InterruptedException;
    }

    private static class Result {
        private int[][] matrix;
        private double duration;

        Result(int[][] matrix, double duration) {
            this.matrix = matrix;
            this.duration = duration;
        }
    }
}