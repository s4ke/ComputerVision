package de.ubt.ai3.computervision.regions;

import java.util.Arrays;

/**
 * @author Martin Braun
 */
public class Main {

	private static final int[][] image =
			{
					{0, 1, 1, 1, 0},
					{0, 1, 0, 0, 0},
					{1, 1, 0, 1, 1},
					{1, 1, 0, 0, 0},
					{1, 0, 0, 1, 1}
			};

	public static int[][] partition(int[][] image_) {
		int[][] image = new int[image_.length][image_[0].length];
		for(int i = 0; i < image_.length; ++i) {
			for(int j = 0; j < image_[0].length; ++j) {
				image[i][j] = image_[i][j];
			}
		}

		UF uf = new UF( image.length * image[0].length );
		int xLength = image[0].length;
		for ( int y = 0; y < image.length; ++y ) {
			for ( int x = 0; x < image[0].length; ++x ) {
				if ( image[y][x] == 1 ) {
					if ( val( image, y - 1, x ) == 0
							&& val( image, y, x - 1 ) == 0 ) {
						//do nothing
					} else {
						if(val(image, y - 1, x) == 1) {
							uf.union( sing( y, x, xLength ), sing( y - 1, x, xLength ) );
						}
						if(val(image, y, x - 1) == 1) {
							uf.union( sing( y, x, xLength ), sing( y, x - 1, xLength ) );
						}
					}
				}
			}
		}

		for ( int y = 0; y < image.length; ++y ) {
			for ( int x = 0; x < image[0].length; ++x ) {
				if(image[y][x] != 0) {
					image[y][x] = uf.find( sing( y, x, xLength ) );
				}
			}
		}

		return image;
	}

	private static int sing(int y, int x, int xLength) {
		return xLength * y + x;
	}

	private static int val(int[][] image, int y, int x) {
		try {
			return image[y][x];
		}
		catch (ArrayIndexOutOfBoundsException e) {
			//hack, we don't care about indices that are out of bounds
			return 0;
		}
	}

	public static void main(String[] args) {
		int[][] img = partition( image );
		for ( int y = 0; y < image.length; ++y ) {
			System.out.println( Arrays.toString( img[y] ) );
		}
	}

}