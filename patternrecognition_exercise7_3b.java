//compile with javac GradientDecline.java
//run with java GradientDecline

import Jama.Matrix;

import javax.swing.text.NumberFormatter;
import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * @author Julian Neuberger, Andreas Braun
 *         d) Calculate the next step (k = 2) by using Ho-Kashyap with η = 1, b(1) = (1, . . . , 1)T and
 *         with a(1) according to the result from 3b. (3)
 *         Remark: If you did not solve Exercise 3b, you may use a(1) = (−0.5,  1)T
 */
public class exercise5b {

    private static final double[][] y = {
            {1, 1.5}, {1, -1.5}, {1, -2}, {1, -2.5}, {1, -3}, {1, -3.5}, {1, -4},
            {-1, -2}, {-1, -2.5}, {-1, -3}, {-1, -3.5}, {-1, -4}, {-1, -4.5}
    };

    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#,###,##0.000");

    private static final int[] classes = {1, 1, 1, 1, 1, 1, 1, 2, 2, 2, 2, 2, 2};

    private static final double FIXED_THETA = 1;

    public static void main(String[] args) {
        double[][] b = new double[y.length][1];
        for (int i = 0; i < y.length; ++i) {
            b[i][0] = 1d;
        }

        double[][] a = {
                {-0.5},
                {1},
        };
        double bmin = 0.5;
        Matrix[] result = stepHoKashyap(y, a, b, bmin, 1);


        System.out.println("Matrix A");
        printMatrixPretty(result[0]);
        System.out.println("Matrix B");
        printMatrixPretty(result[1]);
    }

    public static Matrix getA(double[][] samples, int[] classes, double[][] b, double theta) {
        int dimension = samples[0].length;
        int sampleCount = samples.length;
        Matrix matrixB = new Matrix(b, b.length, 1);
        Matrix matrixY = new Matrix(samples, sampleCount, dimension);
        Matrix matrixYtransposed = matrixY.transpose();
        Matrix pseudoInverse = matrixYtransposed.times(matrixY).inverse().times(matrixYtransposed);
        return pseudoInverse.times(matrixB);
    }

    public static Matrix[] stepHoKashyap(double[][] samples, double[][] a, double[][] b, double bmin, int kcurrent) {
        int kmax = kcurrent + 1;
        int dimension = samples[0].length;
        int sampleCount = samples.length;
        Matrix matrixY = new Matrix(samples, sampleCount, dimension);

        Matrix vecB = new Matrix(b, b.length, 1);
        Matrix vecA = new Matrix(a, a.length, 1);

        // e = Ya - b
        Matrix vecE = matrixY.times(vecA).minus(vecB);

        Matrix vecEAbs;
        {
            double[][] tempArray = new double[y.length][1];
            for (int i = 0; i < vecB.getRowDimension(); i++) {
                tempArray[i][0] = Math.abs(vecE.get(i, 0)) * FIXED_THETA;
            }
            vecEAbs = new Matrix(tempArray);
        }

        // b = b + theta*(e+|e|)
        vecB = vecB.plus(vecE.plus(vecEAbs).times(FIXED_THETA));

        // a = PseudoY*b
        {
            Matrix matrixYtransposed = matrixY.transpose();
            vecA = matrixYtransposed.times(matrixY).inverse().times(matrixYtransposed).times(vecB);
        }

        // if(|e|) <= bmin ....
        for (int i = 0; i < vecE.getRowDimension(); i++) {
            double temp = Math.abs(vecE.get(i, 0));
            if (temp <= bmin) {
                System.out.println("Result invalid!");
                System.out.println("Result invalid!");
                System.out.println("Result invalid!");
            }
        }
        Matrix[] result = {vecA, vecB};
        return result;
    }


    protected static void printMatrixPretty(Matrix m) {
        for (int x = 0; x < m.getRowDimension(); ++x) {
            System.out.print('+');
            for (int i = 0; i < m.getColumnDimension(); ++i) {
                for (int j = 0; j < 6; ++j) {
                    System.out.print('-');
                }
                System.out.print('+');
            }
            System.out.println();
            System.out.print('|');
            for (int y = 0; y < m.getColumnDimension(); ++y) {
                System.out.print(DECIMAL_FORMAT.format(m.get(x, y)));
                System.out.print('|');
            }
            System.out.println();
        }
        System.out.print('+');
        for (int i = 0; i < m.getColumnDimension(); ++i) {
            for (int j = 0; j < 6; ++j) {
                System.out.print('-');
            }
            System.out.print('+');
        }
        System.out.println();
    }
}
