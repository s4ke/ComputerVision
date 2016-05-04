package de.ubt.ai3.computervision;

import java.util.Arrays;

/**
 * X-correlation calculation in code
 *
 * @author Martin Braun (1249080), Andreas Braun
 */
public class XCorrelation {

	public static double[] xcorr(double[] a, double[] b) {
		double[] ret;
		int longer = Math.max( a.length, b.length );
		int maxLag = longer - 1;

		//for length N we can have 2*(N-1) + 1 non-zero values
		int range = 2 * (maxLag) + 1;

		ret = new double[range];

		//we calculate these in two goes, there obviously is an
		//easier way with only one loop, but nvm

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
				System.out.println( "curB: " + Arrays.toString( curB ) );
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

	public static int countMax(double[] y) {
		int count = 0;
		double max = Double.NEGATIVE_INFINITY;
		for ( int i = 0; i < y.length; ++i ) {
			double curVal = y[i];
			if ( curVal == max ) {
				++count;
			}
			else if ( curVal > max ) {
				max = curVal;
				count = 1;
			}
		}
		return count;
	}

	public static double max(double[] y) {
		double max = Double.NEGATIVE_INFINITY;
		for ( int i = 0; i < y.length; ++i ) {
			double curVal = y[i];
			if ( curVal > max ) {
				max = curVal;
			}
		}
		return max;
	}

	/**
	 * @return the lag value at which the first maximum has been found
	 */
	public static int firstMax(double[] y, double max) {
		int maxLag = (y.length - 1) / 2;
		int maxIdx = Integer.MIN_VALUE;
		boolean found = false;
		for ( int i = 0; i < y.length; ++i ) {
			if ( y[i] == max ) {
				maxIdx = i;
				found = true;
				break;
			}
		}
		if ( !found ) {
			throw new IllegalArgumentException( "maximum of " + max + " not found in array " + Arrays.toString( y ) );
		}
		return maxIdx - maxLag;
	}

	public static void main(String[] args) {
		//we use the values from 1.c) here. If other values
		//are needed, please change them here.
		double[] a = {1, 0.5, 0.3, 0, 0.5, 0.2, 1.2};
		double[] b = {0.5, 0.2};

		double[] y = xcorr( a, b );

		System.out.println( "Cross correlation sequence: " + Arrays.toString( y ) );
		System.out.println( "# of maximums: " + countMax( y ) );
		System.out.println( "best correlation at lag value: " + firstMax( y, max( y ) ) );
	}


}