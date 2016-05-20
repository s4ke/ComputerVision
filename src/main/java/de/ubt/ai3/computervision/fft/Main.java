package de.ubt.ai3.computervision.fft;

import java.awt.Dimension;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.Range;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;


public class Main {


    public static final int samples_per_second = 8192;


    public static void main(String[] args) throws FileNotFoundException {

        String filename = "/Beispiel.pcm";

        // Read raw file
        InputStream inputStream = Main.class.getResourceAsStream(filename);
        DataInputStream dataInputStream = new DataInputStream(inputStream);

        List<Short> content = new ArrayList<Short>();  // values in pcm files are encoded as shorts (2 bytes).
        try {
            while (dataInputStream.available() > 0) {
                Byte b1 = dataInputStream.readByte();
                Byte b2 = dataInputStream.readByte();
                short s = (short) ((b2 << 8) | (b1 & 0xff));    // convert bytes to short (c++ style), java's readShort uses wrong encoding.
                content.add(s);
            }

            dataInputStream.close();
            inputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        int N = content.size();
        System.out.println("File read with " + N + " samples.");

        // Use only first first two seconds here (for example)
        N = samples_per_second * 2;
        System.out.println("Using only first " + N + " values.");

        // Convert values to Complex because FFT needs complex values
        Complex[] c = new Complex[N];
        for (int i = 0; i < N; ++i) {
            c[i] = new Complex(content.get(i), 0); // imaginary part is zero
        }

        // calculate FFT
        Complex[] transformed = FFT.fft(c);


        // data for plot
        XYSeries series = new XYSeries("FFT");

        // print output
        for (int i = 0; i < N / 2; i++) {
            double frequency = i * samples_per_second / N;
            double magnitude = transformed[i].abs() / N;
            double phase = transformed[i].phase() / Math.PI;

            // add magnitude to plot data
            series.add(frequency, magnitude);

            // print some values to console
            if (i % 100 == 0)
                System.out.println("[" + frequency + "]: " + magnitude + " / " + phase);

        }


        // plot data
        XYSeriesCollection data = new XYSeriesCollection(series);
        JFreeChart chart = ChartFactory.createXYLineChart("FFT " + filename, "Frequency", "Magnitude", data, PlotOrientation.VERTICAL, false, false, false);

        ChartFrame f = new ChartFrame("Fourier plot", chart);
        f.setMinimumSize(new Dimension(800, 600));
        f.setVisible(true);

    }

}
