package de.ubt.ai3.computervision;

import java.util.Arrays;

/**
 * @author Martin Braun
 */
public class Main {

	public static double[] xcorr(double[] a, double[] b) {
		double[] ret;


		int longer = Math.max( a.length, b.length );

		int maxLag = longer - 1;

		//for length N we can have 2*(N-1) + 1 non-zero values
		int range = 2 * (maxLag) + 1;

		ret = new double[range];


		//calculate the positive lag (and 0)
		{
			double[] paddedA = fillWithZeroes( 0, range, a );
			System.out.println( "paddedA: " + Arrays.toString( paddedA ) );
			for ( int idx = maxLag; idx < range; ++idx ) {
				int lag = idx - maxLag;
				double[] curB = fillWithZeroes( lag, range, b );
				System.out.println( "curB: " + Arrays.toString( curB ) );
				for ( int i = 0; i < range; ++i ) {
					ret[idx] += paddedA[i] * curB[i];
				}
			}
		}

		//calculate the negative lag (without 0)
		{
			double[] paddedA = fillWithZeroes( maxLag, range, a );
			System.out.println( "paddedA: " + Arrays.toString( paddedA ) );
			for ( int idx = 0; idx < maxLag; ++idx ) {
				int negLag = idx;
				double[] curB = fillWithZeroes( negLag, range, b );
				System.out.println( "curA: " + Arrays.toString( curB ) );
				for ( int i = 0; i < range; ++i ) {
					ret[idx] += paddedA[i] * curB[i];
				}
			}
		}

		return ret;
	}

	public static double[] fillWithZeroes(int position, int totalLength, double[] vals) {
		double[] ret = new double[totalLength];
		Arrays.fill( ret, 0 );
		for ( int i = 0; (i + position) < totalLength && i < vals.length; ++i ) {
			ret[i + position] = vals[i];
		}
		return ret;
	}

	public static void main(String[] args) {
		double[] a = {1, 0.5, 0.3, 0, 0.5, 0.2, 1.2};
		double[] b = {0.5, 0.2};

		System.out.println( "result: " + Arrays.toString( xcorr( a, b ) ) );
	}


}
