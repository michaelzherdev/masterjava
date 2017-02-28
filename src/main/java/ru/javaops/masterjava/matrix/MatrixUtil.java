package ru.javaops.masterjava.matrix;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

/**
 * gkislin
 * 03.07.2016
 */
public class MatrixUtil {

    // TODO implement parallel multiplication matrixA*matrixB
    public static int[][] concurrentMultiply(int[][] matrixA, int[][] matrixB, ExecutorService executor) throws InterruptedException, ExecutionException {
        final int matrixSize = matrixA.length;
        final int[][] matrixC = new int[matrixSize][matrixSize];

        List<Future<int[][]>> list = new ArrayList<>();

        int part = matrixSize / MainMatrix.THREAD_NUMBER;

        for (int x = 0; x < matrixSize; x += part) {
            final int start = x;
            final int end = x + part;

            Future<int[][]> future = executor.submit(() -> {
                int[][] partC = new int[matrixSize][matrixSize];
                for (int i = start; i < end; i++) {
                    for (int k = 0; k < matrixSize; k++) {
                        for (int j = 0; j < matrixSize; j++) {
                            partC[i][j] += matrixA[i][k] * matrixB[k][j];
                        }
                    }
                }
                return partC;
            });
            list.add(future);
        }

        // retrieve result
        int start = 0;
        for (Future<int[][]> future : list) {
            for (int i = start; i < start + part; i++) {
                matrixC[i] = future.get()[i];
            }
            start += part;
        }

        return matrixC;
    }

    // TODO optimize by https://habrahabr.ru/post/114797/
    public static int[][] singleThreadMultiply(int[][] matrixA, int[][] matrixB) {
        final int matrixSize = matrixA.length;
        final int[][] matrixC = new int[matrixSize][matrixSize];

        //        no enhancement; time = 1.5
//        for (int i = 0; i < matrixSize; i++) {
//            for (int j = 0; j < matrixSize; j++) {
//                int sum = 0;
//                for (int k = 0; k < matrixSize; k++) {
//                    sum += matrixA[i][k] * matrixB[k][j];
//                }
//                matrixC[i][j] = sum;
//            }
//        }

        //        enhancement 1 matrix transpose; time = 0.412
        int[][] matrixBTranspose = new int[matrixSize][matrixSize];
        for(int i = 0; i < matrixSize; i++)
            for(int j = 0; j < matrixSize; j++)
                matrixBTranspose[j][i] = matrixB[i][j];

        for (int i = 0; i < matrixSize; i++) {
            for (int j = 0; j < matrixSize; j++) {
                int sum = 0;
                for (int k = 0; k < matrixSize; k++) {
                    sum += matrixA[i][k] * matrixBTranspose[j][k];
                }
                matrixC[i][j] = sum;
            }
        }

        //        enhancement 2 cycle enhancement, time = 0.466
//        int thatColumn[] = new int[matrixSize];
//
//        for (int j = 0; j < matrixSize; j++) {
//            for (int k = 0; k < matrixSize; k++) {
//                thatColumn[k] = matrixB[k][j];
//            }
//
//            for (int i = 0; i < matrixSize; i++) {
//                int thisRow[] = matrixA[i];
//                int sum = 0;
//                for (int k = 0; k < matrixSize; k++) {
//                    sum += thisRow[k] * thatColumn[k];
//                }
//                matrixC[i][j] = sum;
//            }
//        }

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
