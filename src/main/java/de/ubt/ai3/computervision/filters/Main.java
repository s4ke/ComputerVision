package de.ubt.ai3.computervision.filters;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DirectColorModel;
import java.awt.image.PixelGrabber;
import java.io.File;
import java.io.IOException;

public class Main {

	private static int[][] filter = {
			{0, -2, 0},
			{-2, 7, -2},
			{0, -2, 0}
	};

	/**
	 * @param filterIn is stored so that the upper left is -1, -1 in the
	 * 3x3 case
	 */
	public static int[][] applyFilter(int[][] image, int[][] filterIn) {
		int imageHeight = image.length;
		int imageWidth = image[0].length;

		int filterHeight = filterIn.length;
		int filterWidth = filterIn[0].length;

		{
			int totalValue = 0;
			double[][] filter = new double[filterHeight][filterWidth];
			for ( int i = 0; i < filterWidth; ++i ) {
				for ( int j = 0; j < filterHeight; ++j ) {
					filter[i][j] = filterIn[i][j];
					totalValue += Math.abs( filter[i][j] );
				}
			}

			for ( int i = 0; i < filterWidth; ++i ) {
				for ( int j = 0; j < filterHeight; ++j ) {
					filter[i][j] /= totalValue;
				}
			}
		}

		if ( filterHeight % 2 == 0 || filterWidth % 2 == 0 ) {
			throw new IllegalArgumentException( "filterHeight or filterWidth must not be even" );
		}

		// we want the floor of the division.
		int amountToCropX = filterWidth / 2;
		int amountToCropY = filterHeight / 2;

		//we save the intermediary image here, so we don't lose information
		//too soon
		double[][] intermediary = new double[imageHeight][imageWidth];
		for ( int i = 0; i < imageHeight; ++i ) {
			for ( int j = 0; j < imageWidth; ++j ) {
				intermediary[i][j] = image[i][j];
			}
		}

		int highestVal = Integer.MIN_VALUE;
		int lowestVal = Integer.MAX_VALUE;

		//we don't filter the edges where the filter wouldn't fit
		//completely, but we don't crop the output image here just yet
		for ( int y = amountToCropY; y < (imageHeight - amountToCropY); ++y ) {
			for ( int x = amountToCropX; x < (imageWidth - amountToCropX); ++x ) {
				int value = 0;
				for ( int i = 0; i < filterWidth; ++i ) {
					for ( int j = 0; j < filterHeight; ++j ) {
						value += image[y - amountToCropY + i][x - amountToCropX + i] * filter[j][i];
					}
				}
				highestVal = Math.max( highestVal, value );
				lowestVal = Math.min( lowestVal, value );
				intermediary[y][x] = value;
			}
		}

		System.out.println( "highestVal: " + highestVal );
		System.out.println( "lowestVal: " + lowestVal );

		int add = Math.abs( Math.min( 0, lowestVal ) );
		int highestAdded = add + highestVal;

		//rescale the values in the image and store it back into the array
		for ( int y = 0; y < imageHeight; ++y ) {
			for ( int x = 0; x < imageWidth; ++x ) {
				try {
					double value = intermediary[y][x];
					double scale = value / highestAdded;
					intermediary[y][x] = scale * 255;
				}
				catch (ArrayIndexOutOfBoundsException e) {
					System.out.println();
				}
			}
		}

		//crop the output image (everything was properly ignored beforehand, we just
		//didn't make the signal smaller)
		int[][] out = new int[imageHeight - 2 * amountToCropY][imageWidth - 2 * amountToCropX];
		for(int y = 0; y < out.length; ++y) {
			for(int x = 0; x < out[0].length; ++x) {
				out[y][x] = (int) intermediary[y + amountToCropY][x + amountToCropX];
			}
		}
		return out;
	}

	public static void main(String[] args) throws Exception {
		// Load Image from file into 2D-array
		// First index is row, second index is column of image
		// Array contains grayscale values in range 0-255
		int[][] imArray = loadFromFile( "test.png" );

		imArray = applyFilter( imArray, filter );

		// Save array back to file
		saveToFile( imArray, "out.png", "png" );
	}


	/**
	 * Save image to file
	 *
	 * @param pixels 2D-grayscale image from 0-255
	 * @param filename e.g. "test.png"
	 * @param type e.g. "bmp", "png", ...
	 */
	public static void saveToFile(int[][] pixels, String filename, String type) throws IOException {
		// Convert to RGB
		int height = pixels.length;
		int width = pixels[0].length;
		int[] rgb = new int[width * height];
		for ( int y = 0; y < height; y++ ) {
			for ( int x = 0; x < width; x++ ) {
				rgb[y * width + x] = (pixels[y][x] << 16) + (pixels[y][x] << 8) + pixels[y][x];
			}
		}

		// save to file
		BufferedImage image = new BufferedImage( width, height, BufferedImage.TYPE_INT_RGB );
		image.setRGB( 0, 0, width, height, rgb, 0, width );
		File file = new File( filename );
		ImageIO.write( image, type, file );
	}

	/**
	 * Read image from file. Returns an 2D-array of int containing grayscale values
	 * (0-255).
	 */
	public static int[][] loadFromFile(String filename) throws IOException, InterruptedException {
		// read from file
		File file = new File( filename );
		Image img = ImageIO.read( file );

		int width = img.getWidth( null );
		int height = img.getHeight( null );
		if ( width * height <= 0 ) {
			throw new IOException( "Image has no data!" );
		}

		int[] array = new int[width * height];
		PixelGrabber grabber = new PixelGrabber(
				img, 0, 0, width, height,
				array, 0, width
		);
		grabber.grabPixels();

		//make grayscale
		int[][] grayscale = new int[height][width];
		DirectColorModel colorModel = (DirectColorModel) ColorModel
				.getRGBdefault();
		for ( int y = 0; y < height; y++ ) {
			for ( int x = 0; x < width; x++ ) {
				grayscale[y][x] = (colorModel.getRed( array[y * width + x] )
						+ colorModel.getGreen( array[y * width + x] ) + colorModel
						.getBlue( array[y * width + x] )) / 3;
			}
		}

		return grayscale;
	}
}
