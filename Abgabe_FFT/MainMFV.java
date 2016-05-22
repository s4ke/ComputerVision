/*
 * compile similar to the original version of this file
 * we basically just generate parts of the original signal with this
 * program so we can interpret the plots. We know that the original
 * signal contains 4 beeps with a somewhat equal delay between each other
 * 
 * This means that we can just split the stream in a suitable amount of
 * parts and then identify the frequencies. This has to be done by hand here
 * as this was not specifically asked in the exercise ;)
 */
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 * @authors Martin Braun, Andreas Braun
 */
public class MainMFV {


	public static final int samples_per_second = 8192;


	public static void main(String[] args) throws IOException {
		// Read raw file (path to the *.pcm fil goes here)
		FileInputStream inputStream = new FileInputStream( args[0] );
		DataInputStream dataInputStream = new DataInputStream( inputStream );

		List<Short> content = new ArrayList<Short>();  // values in pcm files are encoded as shorts (2 bytes).
		try {
			while ( dataInputStream.available() > 0 ) {
				Byte b1 = dataInputStream.readByte();
				Byte b2 = dataInputStream.readByte();
				short s = (short) ((b2 << 8) | (b1 & 0xff));    // convert bytes to short (c++ style), java's readShort uses wrong encoding.
				content.add( s );
			}

			dataInputStream.close();
			inputStream.close();

		}
		catch (IOException e) {
			e.printStackTrace();
			return;
		}

		//adjust this to split the signal into a different amount of parts
		int parts = 8;
		for(int wantedPart = 0; wantedPart < parts + 1; ++wantedPart) {

			int N = content.size();
			System.out.println( "File read with " + N + " samples." );

			// Use only first first two seconds here (for example)
			N = samples_per_second * 4;
			System.out.println( "Using only first " + N + " values." );


			int partLength = content.size() / parts;

			// Convert values to Complex because FFT needs complex values
			Complex[] c = new Complex[N];
			for ( int i = 0; i < N; ++i ) {
				if ( i < content.size()
						&& i >= (wantedPart - 1) * partLength
						&& i <= (wantedPart + 1) * partLength ) {
					c[i] = new Complex( content.get( i ), 0 ); // imaginary part is zero
				}
				else {
					c[i] = new Complex( 0, 0 );
				}
			}

			// calculate FFT
			Complex[] transformed = FFT.fft( c );


			// data for plot
			XYSeries series = new XYSeries( "FFT" );

			// print output
			for ( int i = 0; i < N / 2; i++ ) {
				double frequency = i * samples_per_second / N;
				double magnitude = transformed[i].abs() / N;
				double phase = transformed[i].phase() / Math.PI;

				// add magnitude to plot data
				series.add( frequency, magnitude );

				// print some values to console
				if ( i % 100 == 0 ) {
					System.out.println( "[" + frequency + "]: " + magnitude + " / " + phase );
				}

			}


			// plot data
			XYSeriesCollection data = new XYSeriesCollection( series );
			JFreeChart chart = ChartFactory.createXYLineChart(
					"FFT " + args[0] + "_part_" + wantedPart,
					"Frequency",
					"Magnitude",
					data,
					PlotOrientation.VERTICAL,
					false,
					false,
					false
			);

			ChartUtilities.saveChartAsPNG( new File( "part" + wantedPart + ".png" ), chart, 800, 600 );
		}

	}

}
