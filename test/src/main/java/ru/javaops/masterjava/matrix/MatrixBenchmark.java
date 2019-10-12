package ru.javaops.masterjava.matrix;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Warmup(iterations = 50)
@Measurement(iterations = 50)
@BenchmarkMode(Mode.SingleShotTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
@Threads(1)
@Fork(1)
@Timeout(time = 5, timeUnit = TimeUnit.MINUTES)
public class MatrixBenchmark {
    private static final int THREAD_NUMBER = 10;
    private static final ExecutorService executor = Executors.newFixedThreadPool(THREAD_NUMBER);
    private static int[][] matrixA;
    private static int[][] matrixB;

    @Param({"1000"})
    private int matrixSize;

    public static void main(String[] args) throws RunnerException {
        Options options = new OptionsBuilder()
                .include(MatrixBenchmark.class.getSimpleName())
                .threads(1)
                .forks(1)
                .timeout(TimeValue.minutes(5))
                .build();
        new Runner(options).run();
    }

    @Setup
    public void setUp() {
        matrixA = MatrixUtil.create(matrixSize);
        matrixB = MatrixUtil.create(matrixSize);
    }

    @Benchmark
    public int[][] singleThreadMultiply() {
        return MatrixUtil.singleThreadMultiply(matrixA, matrixB);
    }

    @Benchmark
    public int[][] concurrentMultiply() throws Exception {
        return MatrixUtil.concurrentMultiply(matrixA, matrixB, executor);
    }

    @Benchmark
    public int[][] concurrentMultiply2() throws Exception {
        return MatrixUtil.concurrentMultiply2(matrixA, matrixB, executor);
    }

    @Benchmark
    public int[][] concurrentMultiplyWithCompletionService() {
        return MatrixUtil.concurrentMultiplyWithCompletionService(matrixA, matrixB, executor);
    }

    @Benchmark
    public int[][] concurrentMultiplyStream() {
        return MatrixUtil.concurrentMultiplyStream(matrixA, matrixB);
    }

    @TearDown
    public void tearDown() {
        executor.shutdown();
    }
}