package de.ubt.ai3.computervision.classification;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Martin Braun
 */
public class GradientDecline {

	private static final double[][] y = {
			{1, 1, 1}, {1, 0, 1}, {1, 0, 4}, {1, 1, 0},
			{-1, 0, -16}, {-1, -1, -16}, {-1, -4, -4}, {-1, -4, -9},
			{-1, -1, -9}, {-1, -9, -1}
	};

	private static final int[] classes = {1, 1, 1, 1, 2, 2, 2, 2, 2, 2};

	private static final double FIX_THETA = 0.3;

	public static void main(String[] args) {
		double[] a = {13, -3, 1};
		List<double[]> as = new ArrayList<>();
		as.add( a );
		for ( int k = 0; k < 2; ++k ) {
			double[] lastA = as.get( as.size() - 1 );
			double[] curA = minus( lastA, perceptronFunction( lastA, y, classes ) * thetha( k ) );
			as.add( curA );
		}
		for ( double[] vec : as ) {
			System.out.println( Arrays.toString( vec ) );
		}

		//is the nth iterative version okay?
		for ( int i = 0; i < as.size(); ++i ) {
			double[] a_i = as.get( i );
			Set<Integer> wrongClassifieds = wrongClassification( a_i, y, classes );
			if ( wrongClassification( a_i, y, classes ).size() == 0 ) {
				System.out.println( "we can classify everything with the " + i + "th iterated value for a" );
			}
			else {
				System.out.println( "we can NOT classify everything with the the " + i + "th iterated value for a. the set of not classifiable indexes was: " + wrongClassifieds );
			}
		}
	}

	private static double thetha(int k) {
		return FIX_THETA;
	}

	public static double[] minus(double[] x, double y) {
		double[] ret = new double[x.length];
		for ( int i = 0; i < x.length; ++i ) {
			ret[i] = x[i] - y;
		}
		return ret;
	}

	public static double g(double[] a, double[] y) {
		if ( a.length != y.length ) {
			throw new AssertionError();
		}
		double res = 0;
		for ( int i = 0; i < a.length; ++i ) {
			res += a[i] * y[i];
		}
		return res;
	}

	public static int classification(double val) {
		if ( val > 0 ) {
			return 1;
		}
		else if ( val < 0 ) {
			return 2;
		}
		else {
			throw new AssertionError();
		}
	}

	public static Set<Integer> wrongClassification(double[] a, double[][] ys, int[] actualClassification) {
		Set<Integer> ret = new HashSet<>();
		for ( int i = 0; i < y.length; ++i ) {
			int expectedClass = actualClassification[i];
			int calculatedClass = classification( g( a, ys[i] ) );
			if ( calculatedClass != expectedClass ) {
				ret.add( i );
			}
		}
		return ret;
	}

	public static double perceptronFunction(double[] a, double[][] ys, int[] actualClassification) {
		Set<Integer> wrongClassifications = wrongClassification( a, ys, actualClassification );
		double ret = 0;
		for ( Integer wrongClassifiedIdx : wrongClassifications ) {
			ret -= g( a, y[wrongClassifiedIdx] );
		}
		return ret;
	}

}
